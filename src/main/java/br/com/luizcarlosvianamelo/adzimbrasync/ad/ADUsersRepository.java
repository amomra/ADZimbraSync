package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import java.util.List;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.DN;

/**
 * Classe que representa o repositório de usuários que estão contidos na árvore do
 * AD. As funcionalidades contidas nesta podem ser utilizadas para manipulação
 * dos usuários na árvore.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADUsersRepository {

	private ADTree adTree;

	/**
	 * Construtor da classe. Como esta classe sempre estará associada à uma
	 * árvore do AD, ela não pode ser inicializada fora dela.
	 * @param adTree A árvore do AD associada à este repositório.
	 */
	ADUsersRepository(ADTree adTree) {
		this.adTree = adTree;
	}

	/**
	 * Função que retorna todos os usuários contidos na árvore do AD.
	 * @return A lista contendo todos os usuários contidos na árvore do AD. Caso
	 * não existam usuários a serem retornados, é retornada uma lista vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADUser> queryUsers() throws Exception {
		return this.queryUsers("");
	}
	
	/**
	 * Função que retorna todos os usuários contidos na árvore do AD.
	 * @param withMail Informa a função se apenas os usuários com e-mail deverão
	 * ser buscados.
	 * @return A lista contendo todos os usuários contidos na árvore do AD. Caso
	 * não existam usuários a serem retornados, é retornada uma lista vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADUser> queryUsers(boolean withMail) throws Exception {
		String searchFilter = "";
		if (withMail)
			searchFilter = "(mail=*)";
		return this.queryUsers(searchFilter);
	}

	/**
	 * Função que faz a busca dos usuários contidos na árvore do AD de acordo com
	 * o filtro definido.
	 * @param searchFilter O filtro a ser aplicado durante a busca dos usuários.
	 * Este segue o padrão de filtros do LDAP.
	 * @return A lista contendo os usuários da árvore do AD que não foram
	 * filtrados. Caso não haja grupos na árvore ou todos os usuários foram
	 * filtrados, é retornada uma lista vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADUser> queryUsers(String searchFilter) throws Exception {
		// faz a consulta ao LDAP dos usuários
		return this.adTree.search(ADUser.class, searchFilter);
	}

	/**
	 * Função que faz a busca de um usuário a partir do seu login.
	 * @param accountName O login do usuário a ser buscado.
	 * @return Retorna o usuário representado pelo login. Caso não encontre,
	 * retorna <code>null</code>.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public ADUser queryUserByAccountName(String accountName) throws Exception {
		/*
		 * Faz a consulta do usuário que possui o attributo sAMAccountName
		 * com o valor passado. Esta consulta deverá retornar apenas um
		 * usuário.
		 */
		List<ADUser> users = this.queryUsers(String.format("(sAMAccountName=%s)", accountName));

		// retorna o usuário caso ele tenha sido encontrado
		if (users.size() > 0)
			// se por algum motivo bizarro ele encontrar mais de um, retorna
			// apenas o primeiro
			return users.get(0);

		return null;
	}

	/**
	 * Função que faz a busca de um usuário a partir do seu DN.
	 * @param userDN O DN do usuário a ser buscado.
	 * @return Retorna o usuário representado pelo DN. Caso não encontre,
	 * retorna <code>null</code>.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public ADUser queryUserByDN(DN userDN) throws Exception {
		/*
		 * Faz a consulta do usuário que possui o attributo distinguishedName
		 * com o valor passado. Esta consulta deverá retornar apenas um
		 * usuário.
		 */
		List<ADUser> users = this.queryUsers(String.format("(distinguishedName=%s)", userDN.toString()));

		// retorna o usuário caso ele tenha sido encontrado
		if (users.size() > 0)
			// se por algum motivo bizarro ele encontrar mais de um, retorna
			// apenas o primeiro
			return users.get(0);

		return null;
	}

	/**
	 * Função que faz a busca de usuários a partir de um nome.
	 * @param userName O nome do usuários a ser buscado. Podem ser utilizados os
	 * wildcards suportados pelo LDAP.
	 * @param withMail Indica se somente os usuários que tiverem o atributo
	 * <code>mail</code> ajustado serão retornados.
	 * @return Retorna a lista de usuários que possuem o nome passado nos
	 * parâmetros. Caso não haja usuários, é retornada uma lista vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADUser> queryUsersByName(String userName, boolean withMail) throws Exception {
		String searchQuery = String.format("(name=%s)", userName);

		// busca apenas os usuários que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";

		return this.queryUsers(searchQuery);
	}

	/**
	 * Função que retorna todos os usuários que pertencem ao grupo.
	 * @param group O grupo a ser buscado.
	 * @param withMail Indica se somente os usuários que tiverem o atributo
	 * <code>mail</code> ajustado serão retornados.
	 * @return Retorna a lista de usuário que são membros do grupo. Caso o grupo
	 * não tenha usuários, é retornada uma lista vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADUser> queryGroupMembers(ADGroup group, boolean withMail) throws Exception {
		/*
		 * Faz a consulta na base LDAP por todos os usuários que possuirem o
		 * atributo memberOf ajustado com o valor do DN do grupo
		 */
		String searchQuery = String.format("(memberOf=%s)", group.getDistinguishedName().toString());

		// busca apenas os usuários que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";

		return this.queryUsers(searchQuery);
	}
	
	/**
	 * Função que realiza a mudança da senha de um usuário no AD. Esta
	 * funcionalidade funcionará apenas se for estabelecida uma conexão segura
	 * com o servidor.
	 * @param user O objeto do usuário que terá a sua senha alterada. O campo
	 * <code>distinguishedName</code> deverá estar ajustado com o DN do usuário.
	 * @param newPassword A string contendo a nova senha do usuário.
	 * @throws Exception Lança uma exceção caso não for possível alterar a senha.
	 */
	public void changeUserPassword(ADUser user, String newPassword) throws Exception {
		// ajusta a senha
		user.setPassword(newPassword);
		
		// ajusta a senha
		this.adTree.modify(user, "unicodePwd");
	}
	
	/**
	 * Função que modifica os atributos de um usuário no AD com base nos valores
	 * dos campos.
	 * @param user O objeto do usuário do AD. Para que este seja modificado, o
	 * campo <code>distinguishedName</code> deve estar corretamente preenchido
	 * com o DN do usuário a ser modificado. Apenas os atributos habilitados
	 * para escrita e com valor diferente de <code>null</code> serão modificados
	 * no LDAP.
	 * @throws Exception Lança exceção caso não seja possível modificar os
	 * atributos do usuário.
	 */
	public void modifyUser(ADUser user) throws Exception {
		// faz a chamada para modificação
		this.adTree.modify(user);
	}
}
