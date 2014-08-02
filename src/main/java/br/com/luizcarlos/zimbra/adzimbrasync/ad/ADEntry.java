package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import java.util.List;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPEntry;

public class ADEntry extends LDAPEntry {
	
	@LDAPAttribute
	protected String distinguishedName;
	
	@LDAPAttribute
	protected String sAMAccountName;
	
	@LDAPAttribute
	protected String cn;
	
	@LDAPAttribute
	protected String name;
	
	@LDAPAttribute
	protected String mail;
	
	@LDAPAttribute( name = "memberOf" )
	protected List<String> memberOfGroups;
	
	public ADEntry() {
		
	}
	
	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getsAMAccountName() {
		return sAMAccountName;
	}

	public void setsAMAccountName(String sAMAccountName) {
		this.sAMAccountName = sAMAccountName;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public List<String> getMemberOfGroups() {
		return memberOfGroups;
	}

	public void setMemberOfGroups(List<String> memberOfGroups) {
		this.memberOfGroups = memberOfGroups;
	}
	
	private boolean isMemberOf(String groupDn) {
		// verifica a lista de grupos aos quais este grupo pertence
		if (this.memberOfGroups != null) {
			for (String memberOfGroupDn : this.memberOfGroups)
				// se o DN estiver contido na lista
				if (memberOfGroupDn.equals(groupDn))
					return true;
		}
		return false;
	}

	public boolean isMemberOf(ADEntry entry) {
		// verifica se o DN da entrada está na lista
		return this.isMemberOf(entry.getDistinguishedName());
	}
}
