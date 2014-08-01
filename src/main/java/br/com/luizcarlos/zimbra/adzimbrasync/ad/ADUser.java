package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPAttribute;

public class ADUser extends ADEntry {
	
	@LDAPAttribute
	private String givenName;
	
	@LDAPAttribute
	private String sn;
	
	public ADUser() {
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
}
