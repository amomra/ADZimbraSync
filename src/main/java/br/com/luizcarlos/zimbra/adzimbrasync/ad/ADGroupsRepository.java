package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import java.util.ArrayList;
import java.util.List;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPConverter;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;

public class ADGroupsRepository {

	private ADTree adTree;

	public ADGroupsRepository(ADTree adTree) {
		this.adTree = adTree;
	}

	public List<ADGroup> queryGroups() throws Exception {
		return this.queryGroups(null);
	}

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

		// armazena a lista de usuários
		List<ADGroup> groups = new ArrayList<>();

		// monta os objetos
		for (SearchResultEntry entry : result.getSearchEntries()) {
			ADGroup group = LDAPConverter.convert(ADGroup.class, entry);
			// adiciona na lista
			groups.add(group);
		}

		return groups;
	}

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
	
	public List<ADGroup> queryGroupsByName(String groupName) throws Exception {
		return this.queryGroups(String.format("(name=%s)", groupName));
	}

	public ADGroup getAdministratorsGroup() throws Exception {
		/*
		 * Faz a consulta do grupo Administrators. Este grupo sempre existirá no
		 * AD e não pode ser alterado.
		 */
		List<ADGroup> groups = this.queryGroupsByName("Administrators");

		// retorna o grupo caso ele tenha sido encontrado
		if (groups.size() > 0)
			// se por algum motivo bizarro ele encontrar mais de um, retorna
			// apenas o primeiro
			return groups.get(0);
		
		// lança exceção já que o grupo sempre existirá no AD
		throw new Exception("Administrators group not found");
	}

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
