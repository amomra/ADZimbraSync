package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import java.util.List;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPEntry;

public class ADGroup extends LDAPEntry {

	@LDAPAttribute
	private String distinguishedName;
	
	@LDAPAttribute
	private String cn;
	
	@LDAPAttribute
	private String mail;
	
	@LDAPAttribute( name = "member" )
	private List<String> members;
	
	public ADGroup() {
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}
	
	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public List<String> getMembers() {
		return members;
	}

	public void setMembers(List<String> members) {
		this.members = members;
	}
}
