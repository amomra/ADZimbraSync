package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LDAPConnection {

	private LDAPConnectionProtocol connectionProtocol;
	private String ldapUrl;
	private String ldapSearchBase;
	private String ldapSearchBindDn;
	private String ldapSearchBindPassword;
	
	private DirContext ldapContext;
	
	public LDAPConnection(LDAPConnectionProtocol connectionProtocol,
			String ldapUrl,
			String ldapSearchBase,
			String ldapSearchBindDn,
			String ldapSearchBindPassword) {
		
		this.connectionProtocol = connectionProtocol;
		this.ldapUrl = ldapUrl;
		this.ldapSearchBase = ldapSearchBase;
		this.ldapSearchBindDn = ldapSearchBindDn;
		this.ldapSearchBindPassword = ldapSearchBindPassword;
		
		this.ldapContext = null;
	}
		
	public LDAPConnectionProtocol getConnectionProtocol() {
		return connectionProtocol;
	}

	public void setConnectionProtocol(LDAPConnectionProtocol connectionProtocol) {
		this.connectionProtocol = connectionProtocol;
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

	public void connect() throws NamingException {
		// inicializa os atributos da conexão com o LDAP
		Hashtable<String, String> ldapEnv = new Hashtable<>();
		
		ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ldapEnv.put(Context.PROVIDER_URL, this.ldapUrl);
		ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		ldapEnv.put(Context.SECURITY_PRINCIPAL, this.ldapSearchBindDn);
		ldapEnv.put(Context.SECURITY_CREDENTIALS, this.ldapSearchBindPassword);
		
		if (this.connectionProtocol != LDAPConnectionProtocol.NONE)
			ldapEnv.put(Context.SECURITY_PROTOCOL, this.connectionProtocol.getProt());
		
		// inicializa o contexto do ldap
		this.ldapContext = new InitialDirContext(ldapEnv);
	}
	
	public void disconnect() {
		// apenas remove a referência para o objeto
		this.ldapContext = null;
	}
	
	public boolean isConnected() {
		return this.ldapContext != null;
	}
	
	public NamingEnumeration<SearchResult> get(String searchFilter) throws NamingException {
		return this.get(searchFilter, null);
	}
	
	public NamingEnumeration<SearchResult> get(String searchFilter, String []returnedAttrs) throws NamingException {
		// parâmetros da busca
		SearchControls searchControls = new SearchControls();
		// busca a árvore inteira
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		// se estiver ajustado para retornar apenas um conjunto de atributos
		if (returnedAttrs != null)
			searchControls.setReturningAttributes(returnedAttrs);
		return this.ldapContext.search(this.ldapSearchBase, searchFilter, searchControls);
	}
}
