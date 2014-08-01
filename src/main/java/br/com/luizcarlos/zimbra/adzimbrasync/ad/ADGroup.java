package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import java.util.List;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPAttribute;

public class ADGroup extends ADEntry {
	
	@LDAPAttribute( name = "member" )
	private List<String> members;
	
	public ADGroup() {
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}
	
	private boolean isMember(String dn) {
		// verifica a lista de membros do grupo
		if (this.members != null) {
			for (String memberDn : this.members)
				// se o DN estiver contido na lista
				if (memberDn == dn)
					return true;
		}
		return false;
	}

	public boolean isMember(ADEntry adEntry) {
		// verifica se o DN da entrada está na lista
		return this.isMember(adEntry.getDistinguishedName());
	}
}
