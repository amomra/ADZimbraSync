package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPConverter;

/**
 * Classe que representa o reposit�rio de usu�rios que est�o contidos na �rvore do
 * AD. As funcionalidades contidas nesta podem ser utilizadas para manipula��o
 * dos usu�rios na �rvore.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADUsersRepository {

	private ADTree adTree;

	/**
	 * Construtor da classe. Como esta classe sempre estar� associada � uma
	 * �rvore do AD, ela n�o pode ser inicializada fora dela.
	 * @param adTree A �rvore do AD associada � este reposit�rio.
	 */
	ADUsersRepository(ADTree adTree) {
		this.adTree = adTree;
	}

	/**
	 * Fun��o que retorna todos os usu�rios contidos na �rvore do AD.
	 * @return A lista contendo todos os usu�rios contidos na �rvore do AD. Caso
	 * n�o existam usu�rios a serem retornados, � retornada uma lista vazia.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public List<ADUser> queryUsers() throws Exception {
		return this.queryUsers(null);
	}
	
	/**
	 * Fun��o que retorna todos os usu�rios contidos na �rvore do AD.
	 * @param withMail Informa a fun��o se apenas os usu�rios com e-mail dever�o
	 * ser buscados.
	 * @return A lista contendo todos os usu�rios contidos na �rvore do AD. Caso
	 * n�o existam usu�rios a serem retornados, � retornada uma lista vazia.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public List<ADUser> queryUsers(boolean withMail) throws Exception {
		String searchFilter = null;
		if (withMail)
			searchFilter = "(mail=*)";
		return this.queryUsers(searchFilter);
	}

	/**
	 * Fun��o que faz a busca dos usu�rios contidos na �rvore do AD de acordo com
	 * o filtro definido.
	 * @param searchFilter O filtro a ser aplicado durante a busca dos usu�rios.
	 * Este segue o padr�o de filtros do LDAP.
	 * @return A lista contendo os usu�rios da �rvore do AD que n�o foram
	 * filtrados. Caso n�o haja grupos na �rvore ou todos os usu�rios foram
	 * filtrados, � retornada uma lista vazia.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public List<ADUser> queryUsers(String searchFilter) throws Exception {
		// faz a consulta ao LDAP dos usu�rios
		String searchString = "(&(objectCategory=Person)%s)";

		// caso seja adicionado mais um filtro
		if (searchFilter != null)
			searchString = String.format(searchString, searchFilter);
		else
			searchString = String.format(searchString, "");

		// faz a consulta
		NamingEnumeration<SearchResult> result = this.adTree.search(searchString);

		// armazena a lista de usu�rios
		List<ADUser> users = new ArrayList<>();

		// monta os objetos
		while (result.hasMoreElements()) {

			SearchResult entry = result.nextElement();

			ADUser user = LDAPConverter.convert(ADUser.class, entry);
			// adiciona na lista
			users.add(user);
		}

		return users;
	}

	/**
	 * Fun��o que faz a busca de um usu�rio a partir do seu login.
	 * @param accountName O login do usu�rio a ser buscado.
	 * @return Retorna o usu�rio representado pelo login. Caso n�o encontre,
	 * retorna <code>null</code>.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public ADUser queryUserByAccountName(String accountName) throws Exception {
		/*
		 * Faz a consulta do usu�rio que possui o attributo sAMAccountName
		 * com o valor passado. Esta consulta dever� retornar apenas um
		 * usu�rio.
		 */
		List<ADUser> users = this.queryUsers(String.format("(sAMAccountName=%s)", accountName));

		// retorna o usu�rio caso ele tenha sido encontrado
		if (users.size() > 0)
			// se por algum motivo bizarro ele encontrar mais de um, retorna
			// apenas o primeiro
			return users.get(0);

		return null;
	}

	/**
	 * Fun��o que faz a busca de um usu�rio a partir do seu DN.
	 * @param userDN O DN do usu�rio a ser buscado.
	 * @return Retorna o usu�rio representado pelo DN. Caso n�o encontre,
	 * retorna <code>null</code>.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public ADUser queryUserByDN(String userDN) throws Exception {
		/*
		 * Faz a consulta do usu�rio que possui o attributo distinguishedName
		 * com o valor passado. Esta consulta dever� retornar apenas um
		 * usu�rio.
		 */
		List<ADUser> users = this.queryUsers(String.format("(distinguishedName=%s)", userDN));

		// retorna o usu�rio caso ele tenha sido encontrado
		if (users.size() > 0)
			// se por algum motivo bizarro ele encontrar mais de um, retorna
			// apenas o primeiro
			return users.get(0);

		return null;
	}

	/**
	 * Fun��o que faz a busca de usu�rios a partir de um nome.
	 * @param userName O nome do usu�rios a ser buscado. Podem ser utilizados os
	 * wildcards suportados pelo LDAP.
	 * @param withMail Indica se somente os usu�rios que tiverem o atributo
	 * <code>mail</code> ajustado ser�o retornados.
	 * @return Retorna a lista de usu�rios que possuem o nome passado nos
	 * par�metros. Caso n�o haja usu�rios, � retornada uma lista vazia.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public List<ADUser> queryUsersByName(String userName, boolean withMail) throws Exception {
		String searchQuery = String.format("(name=%s)", userName);

		// busca apenas os usu�rios que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";

		return this.queryUsers(searchQuery);
	}

	/**
	 * Fun��o que retorna todos os usu�rios que pertencem ao grupo.
	 * @param group O grupo a ser buscado.
	 * @param withMail Indica se somente os usu�rios que tiverem o atributo
	 * <code>mail</code> ajustado ser�o retornados.
	 * @return Retorna a lista de usu�rio que s�o membros do grupo. Caso o grupo
	 * n�o tenha usu�rios, � retornada uma lista vazia.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public List<ADUser> queryGroupMembers(ADGroup group, boolean withMail) throws Exception {
		/*
		 * Faz a consulta na base LDAP por todos os usu�rios que possuirem o
		 * atributo memberOf ajustado com o valor do DN do grupo
		 */
		String searchQuery = String.format("(memberOf=%s)", group.getDistinguishedName());

		// busca apenas os usu�rios que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";

		return this.queryUsers(searchQuery);
	}
	
	/**
	 * Fun��o que realiza a mudan�a da senha de um usu�rio no AD. Esta
	 * funcionalidade funcionar� apenas se for estabelecida uma conex�o segura
	 * com o servidor.
	 * @param user O objeto do usu�rio que ter� a sua senha alterada. O campo
	 * <code>distinguishedName</code> dever� estar ajustado com o DN do usu�rio.
	 * @param newPassword A string contendo a nova senha do usu�rio.
	 * @throws Exception Lan�a uma exce��o caso n�o for poss�vel alterar a senha.
	 */
	public void changeUserPassword(ADUser user, String newPassword) throws Exception {
		/*
		 * A senha dever� ser codificada em UTF-16 e entre aspas para que ela
		 * seja alterada no servidor.
		 */
		String quotedPassword = String.format("\"%s\"", newPassword);
		
		// codifica para UTF-16
		byte[] encodedPassword = quotedPassword.getBytes("UTF-16LE");
		
		// ajusta a senha
		this.adTree.modify(user.getDistinguishedName(),
				new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
						new BasicAttribute("unicodePwd", encodedPassword)));
	}
}
