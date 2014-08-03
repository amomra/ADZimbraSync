package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LDAPTree {

	private DirContext ldapContext;

	private String ldapUrl;

	private String ldapSearchBase;
	private String ldapSearchBindDn;
	private String ldapSearchBindPassword;

	public LDAPTree() {
		this("", "", "", "");
	}

	public LDAPTree(String ldapUrl,
			String ldapSearchBase,
			String ldapSearchBindDn,
			String ldapSearchBindPassword) {
		this.ldapContext = null;

		this.ldapUrl = ldapUrl;

		this.ldapSearchBase = ldapSearchBase;
		this.ldapSearchBindDn = ldapSearchBindDn;
		this.ldapSearchBindPassword = ldapSearchBindPassword;
	}

	public String getLdapUrl() {
		return ldapUrl;
	}

	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
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
	
	public void connect() throws NamingException {
		// realiza uma conexão não segura
		this.connect(false);
	}

	public void connect(boolean ssl) throws NamingException {		
		// inicializa os atributos da conexão
		Hashtable<String, String> ldapEnv = new Hashtable<>();
		ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        ldapEnv.put(Context.PROVIDER_URL, this.ldapUrl);
        ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
        ldapEnv.put(Context.SECURITY_PRINCIPAL, this.ldapSearchBindDn);
        ldapEnv.put(Context.SECURITY_CREDENTIALS, this.ldapSearchBindPassword);

        // se estiver habilitada a conexão segura
        if (ssl)
        	ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");
        	
		// inicializa a conexão
		this.ldapContext = new InitialDirContext(ldapEnv);
	}

	public void disconnect() throws NamingException {
		// finaliza a conexão
		if (this.ldapContext != null)
			this.ldapContext.close();
		this.ldapContext = null;
	}

	public boolean isConnected() {
		return this.ldapContext != null;
	}

	public NamingEnumeration<SearchResult> search(String filter, String... returnAttributes) throws Exception {
		// lança exceção se não estiver conectado
		if (!this.isConnected())
			throw new Exception("Not connected to LDAP server");
		
		// ajusta os parâmetros da busca
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		// se a lista de atributos a serem retornadas for vazia, então retorna
		// todos os atributos
		if (returnAttributes.length > 0)
			searchControls.setReturningAttributes(returnAttributes);

		// realiza a busca
		return this.ldapContext.search(this.ldapSearchBase, filter, searchControls);
	}
}
