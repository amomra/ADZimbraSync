package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import java.util.List;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPConnection;

public class ADTree {
	
	public ADTree(LDAPConnection conn) {
		
	}

	public List<ADGroup> queryUserGroups(ADUser user) {
		return null;
	}

	public List<ADUser> queryGroupMembers(ADGroup group) {
		return null;
	}
}
