package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

public enum LDAPConnectionProtocol {

	NONE (null),
	SSL("ssl");
	
	private final String prot;
	
	private LDAPConnectionProtocol(String prot) {
		this.prot = prot;
	}

	public String getProt() {
		return prot;
	}
}
