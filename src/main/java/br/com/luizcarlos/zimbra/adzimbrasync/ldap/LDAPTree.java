package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

import javax.net.ssl.SSLSocketFactory;

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchScope;

public class LDAPTree {

	private LDAPConnection conn;

	private String hostname;
	private int port;

	private String ldapSearchBase;
	private String ldapSearchBindDn;
	private String ldapSearchBindPassword;

	public LDAPTree() {
		this("", 389, "", "", "");
	}

	public LDAPTree(String hostname,
			int port,
			String ldapSearchBase,
			String ldapSearchBindDn,
			String ldapSearchBindPassword) {
		this.conn = null;

		this.hostname = hostname;
		this.port = port;

		this.ldapSearchBase = ldapSearchBase;
		this.ldapSearchBindDn = ldapSearchBindDn;
		this.ldapSearchBindPassword = ldapSearchBindPassword;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getLdapSearchBase() {
		return ldapSearchBase;
	}

	public void setLdapSearchBase(String ldapSearchBase) {
		this.ldapSearchBase = ldapSearchBase;
	}

	public String getLdapSearchBindDn() {
		return ldapSearchBindDn;
	}

	public void setLdapSearchBindDn(String ldapSearchBindDn) {
		this.ldapSearchBindDn = ldapSearchBindDn;
	}

	public String getLdapSearchBindPassword() {
		return ldapSearchBindPassword;
	}

	public void setLdapSearchBindPassword(String ldapSearchBindPassword) {
		this.ldapSearchBindPassword = ldapSearchBindPassword;
	}

	public void setSSLCertificatesPath(String certPath) {
		// ajusta o caminho no SO com as chaves para o servidor
		System.setProperty("javax.net.ssl.keyStore", certPath);
		System.setProperty("javax.net.ssl.trustStore", certPath);
	}
	
	public void connect() throws LDAPException {
		// realiza uma conexão não segura
		this.connect(false);
	}

	public void connect(boolean ssl) throws LDAPException {		

		// inicializa a conexão
		if (ssl)
			this.conn = new LDAPConnection(
					SSLSocketFactory.getDefault(),
					this.hostname,
					this.port,
					this.ldapSearchBindDn,
					this.ldapSearchBindPassword);
		else
			this.conn = new LDAPConnection(
					this.hostname,
					this.port,
					this.ldapSearchBindDn,
					this.ldapSearchBindPassword);
	}

	public void disconnect() {
		// finaliza a conexão
		if (this.conn != null)
			this.conn.close();
		this.conn = null;
	}

	public boolean isConnected() {
		return this.conn != null && this.conn.isConnected();
	}

	public SearchResult search(String filter, String... returnAttributes) throws Exception {
		// lança exceção se não estiver conectado
		if (!this.isConnected())
			throw new Exception("Not connected to LDAP server");

		// realiza a busca
		return this.conn.search(this.ldapSearchBase, SearchScope.SUB, filter, returnAttributes);
	}
}
