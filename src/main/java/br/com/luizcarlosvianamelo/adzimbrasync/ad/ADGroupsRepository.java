package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import java.util.ArrayList;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPConverter;

/**
 * Classe que representa o repositório de grupos que estão contidos na árvore do
 * AD. As funcionalidades contidas nesta podem ser utilizadas para manipulação
 * dos grupos na árvore.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADGroupsRepository {

	private ADTree adTree;

	/**
	 * Construtor da classe. Como esta classe sempre estará associada à uma
	 * árvore do AD, ela não pode ser inicializada fora dela.
	 * @param adTree A árvore do AD associada à este repositório.
	 */
	ADGroupsRepository(ADTree adTree) {
		this.adTree = adTree;
	}

	/**
	 * Função que faz a busca de todos os grupos contidos na árvore do AD.
	 * @return A lista contendo todos os grupos da árvore do AD. Caso não
	 * encontre grupos, é retornada uma lista vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADGroup> queryGroups() throws Exception {
		return this.queryGroups(null);
	}

	/**
	 * Função que faz a busca de todos os grupos contidos na árvore do AD.
	 * @param withMail Informa a função se apenas os grupos com e-mail deverão
	 * ser buscados.
	 * @return A lista contendo todos os grupos da árvore do AD. Caso não
	 * encontre grupos, é retornada uma lista vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADGroup> queryGroups(boolean withMail) throws Exception {
		String searchFilter = null;
		if (withMail)
			searchFilter = "(mail=*)";
		return this.queryGroups(searchFilter);
	}

	/**
	 * Função que faz a busca dos grupos contidos na árvore do AD de acordo com
	 * o filtro definido.
	 * @param searchFilter O filtro a ser aplicado durante a busca dos grupos.
	 * Este segue o padrão de filtros do LDAP.
	 * @return A lista contendo os grupos da árvore do AD que não foram
	 * filtrados. Caso não haja grupos na árvore ou todos os grupos foram
	 * filtrados, é retornada uma lista vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADGroup> queryGroups(String searchFilter) throws Exception {
		// faz a consulta ao LDAP dos grupos
		String searchString = "(&(objectCategory=Group)%s)";

		// caso seja adicionado mais um filtro
		if (searchFilter != null)
			searchString = String.format(searchString, searchFilter);
		else
			searchString = String.format(searchString, "");

		// faz a consulta
		NamingEnumeration<SearchResult> result = this.adTree.search(searchString);

		// armazena a lista de usuários
		List<ADGroup> groups = new ArrayList<>();

		// monta os objetos
		while (result.hasMoreElements()) {
			SearchResult entry = result.nextElement();

			ADGroup group = LDAPConverter.convert(ADGroup.class, entry);
			// adiciona na lista
			groups.add(group);
		}

		return groups;
	}

	/**
	 * Função que faz a busca de um grupo a partir do seu DN.
	 * @param groupDN O DN do grupo a ser buscado.
	 * @return Retorna o grupo representado pelo DN. Caso não encontre,
	 * retorna <code>null</code>.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public ADGroup queryGroupByDN(String groupDN) throws Exception {
		/*
		 * Faz a consulta do grupo que possui o attributo distinguishedName com o valor
		 * passado. Esta consulta deverá retornar apenas um grupo.
		 */
		List<ADGroup> groups = this.queryGroups(String.format("(distinguishedName=%s)", groupDN));

		// retorna o grupo caso ele tenha sido encontrado
		if (groups.size() > 0)
			// se por algum motivo bizarro ele encontrar mais de um, retorna
			// apenas o primeiro
			return groups.get(0);

		return null;
	}

	/**
	 * Função que faz a busca de grupos a partir de um nome.
	 * @param groupName O nome do grupo a ser buscado. Podem ser utilizados os
	 * wildcards suportados pelo LDAP.
	 * @param withMail Indica se somente os grupos que tiverem o atributo
	 * <code>mail</code> ajustado serão retornados.
	 * @return Retorna a lista de grupos que possuem o nome passado nos
	 * parâmetros. Caso não haja grupos, é retornada uma lista vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADGroup> queryGroupsByName(String groupName, boolean withMail) throws Exception {
		String searchQuery = String.format("(name=%s)", groupName);

		// busca apenas os grupos que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";

		return this.queryGroups(searchQuery);
	}

	/**
	 * Função que faz a busca de listas de distribuição a partir de um nome.
	 * @param distListName O nome da lista a ser buscada. Podem ser utilizados os
	 * wildcards suportados pelo LDAP.
	 * @param withMail Indica se somente as listas que tiverem o atributo
	 * <code>mail</code> ajustado serão retornados.
	 * @return Retorna a lista contendo as listas de distribuição que possuem o
	 * nome passado nos parâmetros. Caso não haja grupos, é retornada uma lista
	 * vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADGroup> queryDistributionListsByName(String distListName, boolean withMail) throws Exception {
		/*
		 * Faz a query do LDAP em que busca as listas de distribuição
		 * configuradas no AD. Isto é feito verificando se o bit 0x80000000 do
		 * atributo groupType não está ajustado.
		 */
		String searchQuery = String.format("(name=%s)(!(groupType:1.2.840.113556.1.4.803:=2147483648))", distListName);

		// busca apenas os grupos que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";

		return this.queryGroups(searchQuery);
	}

	/**
	 * Função que retorna o grupo interno do AD de administradores do sistema.
	 * O grupo a ser retornado possui o nome de "Administrators".
	 * @return Retorna o grupo de administradores.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD. Também lança quando o grupo de
	 * administradores não for encontrado.
	 */
	public ADGroup getAdministratorsGroup() throws Exception {
		/*
		 * Faz a consulta do grupo Administrators. Este grupo sempre existirá no
		 * AD e não pode ser alterado.
		 */
		List<ADGroup> groups = this.queryGroupsByName("Administrators", false);

		// retorna o grupo caso ele tenha sido encontrado
		if (groups.size() > 0)
			// se por algum motivo bizarro ele encontrar mais de um, retorna
			// apenas o primeiro
			return groups.get(0);

		// lança exceção já que o grupo sempre existirá no AD
		throw new Exception("Administrators group not found");
	}

	/**
	 * Função que busca os grupos que uma entrada do AD pertence.
	 * @param entry A entrada do AD a ser checada.
	 * @param withMail Indica se somente os grupos que tiverem o atributo
	 * <code>mail</code> ajustado serão retornados.
	 * @return Retorna a lista de grupos aos quais a entrada do AD pertence.
	 * Caso esta não pertenca a um grupo será retornada uma lista vazia.
	 * @throws Exception Lança exceção quando ocorre um erro durante a
	 * realização da consulta no AD.
	 */
	public List<ADGroup> queryEntryGroups(ADEntry entry, boolean withMail) throws Exception {
		/*
		 * Faz a query do LDAP em que busca as entradas que tem o atributo
		 * memberOf com o valor do DN do usuário.
		 */
		String searchQuery = String.format("(member=%s)", entry.getDistinguishedName());

		// busca apenas os grupos que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";

		return this.queryGroups(searchQuery);
	}
}
