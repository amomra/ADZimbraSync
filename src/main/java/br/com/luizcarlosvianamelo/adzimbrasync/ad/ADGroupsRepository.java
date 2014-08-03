package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import java.util.ArrayList;
import java.util.List;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPConverter;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;

/**
 * Classe que representa o reposit�rio de grupos que est�o contidos na �rvore do
 * AD. As funcionalidades contidas nesta podem ser utilizadas para manipula��o
 * dos grupos na �rvore.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADGroupsRepository {

	private ADTree adTree;

	/**
	 * Construtor da classe. Como esta classe sempre estar� associada � uma
	 * �rvore do AD, ela n�o pode ser inicializada fora dela.
	 * @param adTree A �rvore do AD associada � este reposit�rio.
	 */
	ADGroupsRepository(ADTree adTree) {
		this.adTree = adTree;
	}

	/**
	 * Fun��o que faz a busca de todos os grupos contidos na �rvore do AD.
	 * @return A lista contendo todos os grupos da �rvore do AD. Caso n�o
	 * encontre grupos, � retornada uma lista vazia.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public List<ADGroup> queryGroups() throws Exception {
		return this.queryGroups(null);
	}

	/**
	 * Fun��o que faz a busca dos grupos contidos na �rvore do AD de acordo com
	 * o filtro definido.
	 * @param searchFilter O filtro a ser aplicado durante a busca dos grupos.
	 * Este segue o padr�o de filtros do LDAP.
	 * @return A lista contendo os grupos da �rvore do AD que n�o foram
	 * filtrados. Caso n�o haja grupos na �rvore ou todos os grupos foram
	 * filtrados, � retornada uma lista vazia.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
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
		SearchResult result = this.adTree.search(searchString);

		// armazena a lista de usu�rios
		List<ADGroup> groups = new ArrayList<>();

		// monta os objetos
		for (SearchResultEntry entry : result.getSearchEntries()) {
			ADGroup group = LDAPConverter.convert(ADGroup.class, entry);
			// adiciona na lista
			groups.add(group);
		}

		return groups;
	}

	/**
	 * Fun��o que faz a busca de um grupo a partir do seu DN.
	 * @param groupDN O DN do grupo a ser buscado.
	 * @return Retorna o grupo representado pelo DN. Caso n�o encontre,
	 * retorna <code>null</code>.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public ADGroup queryGroupByDN(String groupDN) throws Exception {
		/*
		 * Faz a consulta do grupo que possui o attributo distinguishedName com o valor
		 * passado. Esta consulta dever� retornar apenas um grupo.
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
	 * Fun��o que faz a busca de grupos a partir de um nome.
	 * @param groupName O nome do grupo a ser buscado. Podem ser utilizados os
	 * wildcards suportados pelo LDAP.
	 * @param withMail Indica se somente os grupos que tiverem o atributo
	 * <code>mail</code> ajustado ser�o retornados.
	 * @return Retorna a lista de grupos que possuem o nome passado nos
	 * par�metros. Caso n�o haja grupos, � retornada uma lista vazia.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public List<ADGroup> queryGroupsByName(String groupName, boolean withMail) throws Exception {
		String searchQuery = String.format("(name=%s)", groupName);

		// busca apenas os grupos que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";

		return this.queryGroups(searchQuery);
	}
	
	/**
	 * Fun��o que faz a busca de listas de distribui��o a partir de um nome.
	 * @param distListName O nome da lista a ser buscada. Podem ser utilizados os
	 * wildcards suportados pelo LDAP.
	 * @param withMail Indica se somente as listas que tiverem o atributo
	 * <code>mail</code> ajustado ser�o retornados.
	 * @return Retorna a lista contendo as listas de distribui��o que possuem o
	 * nome passado nos par�metros. Caso n�o haja grupos, � retornada uma lista
	 * vazia.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public List<ADGroup> queryDistributionListsByName(String distListName, boolean withMail) throws Exception {
		/*
		 * Faz a query do LDAP em que busca as listas de distribui��o
		 * configuradas no AD. Isto � feito verificando se o bit 0x80000000 do
		 * atributo groupType n�o est� ajustado.
		 */
		String searchQuery = String.format("(name=%s)(!(groupType:1.2.840.113556.1.4.803:=2147483648))", distListName);

		// busca apenas os grupos que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";

		return this.queryGroups(searchQuery);
	}

	/**
	 * Fun��o que retorna o grupo interno do AD de administradores do sistema.
	 * O grupo a ser retornado possui o nome de "Administrators".
	 * @return Retorna o grupo de administradores.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD. Tamb�m lan�a quando o grupo de
	 * administradores n�o for encontrado.
	 */
	public ADGroup getAdministratorsGroup() throws Exception {
		/*
		 * Faz a consulta do grupo Administrators. Este grupo sempre existir� no
		 * AD e n�o pode ser alterado.
		 */
		List<ADGroup> groups = this.queryGroupsByName("Administrators", false);

		// retorna o grupo caso ele tenha sido encontrado
		if (groups.size() > 0)
			// se por algum motivo bizarro ele encontrar mais de um, retorna
			// apenas o primeiro
			return groups.get(0);

		// lan�a exce��o j� que o grupo sempre existir� no AD
		throw new Exception("Administrators group not found");
	}

	/**
	 * Fun��o que busca os grupos que uma entrada do AD pertence.
	 * @param entry A entrada do AD a ser checada.
	 * @param withMail Indica se somente os grupos que tiverem o atributo
	 * <code>mail</code> ajustado ser�o retornados.
	 * @return Retorna a lista de grupos aos quais a entrada do AD pertence.
	 * Caso esta n�o pertenca a um grupo ser� retornada uma lista vazia.
	 * @throws Exception Lan�a exce��o quando ocorre um erro durante a
	 * realiza��o da consulta no AD.
	 */
	public List<ADGroup> queryEntryGroups(ADEntry entry, boolean withMail) throws Exception {
		/*
		 * Faz a query do LDAP em que busca as entradas que tem o atributo
		 * memberOf com o valor do DN do usu�rio.
		 */
		String searchQuery = String.format("(member=%s)", entry.getDistinguishedName());

		// busca apenas os grupos que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";

		return this.queryGroups(searchQuery);
	}
}
