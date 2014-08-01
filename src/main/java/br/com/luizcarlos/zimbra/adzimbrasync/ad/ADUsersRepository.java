package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import java.util.ArrayList;
import java.util.List;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPConverter;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;

public class ADUsersRepository {
	
	private ADTree adTree;

	public ADUsersRepository(ADTree adTree) {
		this.adTree = adTree;
	}
	
	public List<ADUser> queryUsers() throws Exception {
		return this.queryUsers(null);
	}
	
	public List<ADUser> queryUsers(String searchFilter) throws Exception {
		// faz a consulta ao LDAP dos usuários
		String searchString = "(&(objectCategory=Person)%s)";

		// caso seja adicionado mais um filtro
		if (searchFilter != null)
			searchString = String.format(searchString, searchFilter);
		else
			searchString = String.format(searchString, "");
		
		// faz a consulta
		SearchResult result = this.adTree.search(searchString);
		
		// armazena a lista de usuários
		List<ADUser> users = new ArrayList<>();
		
		// monta os objetos
		for (SearchResultEntry entry : result.getSearchEntries()) {
			ADUser user = LDAPConverter.convert(ADUser.class, entry);
			// adiciona na lista
			users.add(user);
		}
		
		return users;
	}
	
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
	
	public ADUser queryUserByDN(String userDN) throws Exception {
		/*
		 * Faz a consulta do usuário que possui o attributo distinguishedName
		 * com o valor passado. Esta consulta deverá retornar apenas um
		 * usuário.
		 */
		List<ADUser> users = this.queryUsers(String.format("(distinguishedName=%s)", userDN));
		
		// retorna o usuário caso ele tenha sido encontrado
		if (users.size() > 0)
			// se por algum motivo bizarro ele encontrar mais de um, retorna
			// apenas o primeiro
			return users.get(0);
		
		return null;
	}
	
	public List<ADUser> queryUsersByName(String userName, boolean withMail) throws Exception {
		String searchQuery = String.format("(name=%s)", userName);
		
		// busca apenas os usuários que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";
		
		return this.queryUsers(searchQuery);
	}
	
	public List<ADUser> queryGroupMembers(ADGroup group, boolean withMail) throws Exception {
		/*
		 * Faz a consulta na base LDAP por todos os usuários que possuirem o
		 * atributo memberOf ajustado com o valor do DN do grupo
		 */
		String searchQuery = String.format("(memberOf=%s)", group.getDistinguishedName());

		// busca apenas os usuários que possuem e-mail
		if (withMail)
			searchQuery += "(mail=*)";
		
		return this.queryUsers(searchQuery);
	}
}
