package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPTree;

public class ADTree extends LDAPTree {
	
	private ADUsersRepository usersRepository;
	
	private ADGroupsRepository groupsRepository;
	
	public ADTree() {
		// inicializa os repositórios
		this.usersRepository = new ADUsersRepository(this);
		this.groupsRepository = new ADGroupsRepository(this);
	}

	public ADUsersRepository getUsersRepository() {
		return usersRepository;
	}

	public ADGroupsRepository getGroupsRepository() {
		return groupsRepository;
	}
}
