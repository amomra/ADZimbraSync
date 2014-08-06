package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * Classe que representa uma �rvore do LDAP. Esta �rvore cont�m uma s�rie
 * de entidades que podem ser utilizados para representar os objetos de uma
 * organiza��o. Estas entidades podem ser retornadas a partir de uma s�rie
 * de consultas � �rvore.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class LDAPTree {

	private DirContext ldapContext;

	private String ldapUrl;

	private String ldapSearchBase;
	private String ldapSearchBindDn;
	private String ldapSearchBindPassword;

	/**
	 * Construtor padr�o da classe. Este inicializa os par�metros de conex�o
	 * com valores padr�es.
	 */
	public LDAPTree() {
		this("", "", "", "");
	}

	/**
	 * Construtor parametrizado da classe.
	 * @param ldapUrl O endere�o do servidor LDAP.
	 * @param ldapSearchBase A base de busca na �rvore do LDAP.
	 * @param ldapSearchBindDn O DN do usu�rio que pode realizar consultas sobre
	 * a �rvore.
	 * @param ldapSearchBindPassword A senha do usu�rio de consulta.
	 */
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

	/**
	 * Retorna o endere�o do servidor LDAP.
	 */
	public String getLdapUrl() {
		return ldapUrl;
	}

	/**
	 * Ajusta o endere�o do servidor LDAP.
	 */
	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	/**
	 * Retorna o DN que � utilizado como base nas buscas.
	 */
	public String getLdapSearchBase() {
		return ldapSearchBase;
	}

	/**
	 * Ajusta o DN que � utilizado como base nas buscas.
	 */
	public void setLdapSearchBase(String ldapSearchBase) {
		this.ldapSearchBase = ldapSearchBase;
	}

	/**
	 * Retorna o DN do usu�rio que ser� utilizado para a realiza��o das buscas
	 * na �rvore.
	 */
	public String getLdapSearchBindDn() {
		return ldapSearchBindDn;
	}

	/**
	 * Ajusta o DN do usu�rio que ser� utilizado para a realiza��o das buscas na
	 * �rvore.
	 */
	public void setLdapSearchBindDn(String ldapSearchBindDn) {
		this.ldapSearchBindDn = ldapSearchBindDn;
	}

	/**
	 * Retorna a senha do usu�rio que ser� utilizado para a realiza��o das
	 * buscas na �rvore.
	 */
	public String getLdapSearchBindPassword() {
		return ldapSearchBindPassword;
	}

	/**
	 * Ajusta a senha do usu�rio que ser� utilizado para a realiza��o das
	 * buscas na �rvore.
	 */
	public void setLdapSearchBindPassword(String ldapSearchBindPassword) {
		this.ldapSearchBindPassword = ldapSearchBindPassword;
	}

	/**
	 * Fun��o que ajusta o caminho das pastas onde est�o inclusos os
	 * certificados do servidor LDAP. Estes s�o utilizados para a realiza��o
	 * de uma conex�o segura com o servidor.
	 * @param certPath O caminho da pasta de certificados.
	 * @param certPassword A senha do arquivo de certificados.
	 */
	public void setSSLCertificatesPath(String certPath, String certPassword) {
		// ajusta o caminho no SO com as chaves para o servidor
		System.setProperty("javax.net.ssl.trustStore", certPath);
		System.setProperty("javax.net.ssl.trustStorePassword", certPassword);
	}

	/**
	 * Fun��o que realiza uma conex�o n�o segura com o servidor de acordo
	 * com os par�metros definidos.
	 * @throws NamingException Lan�a exce��o caso n�o for poss�vel se conectar
	 * no servidor.
	 */
	public void connect() throws NamingException {
		// realiza uma conex�o n�o segura
		this.connect(false);
	}

	/**
	 * Fun��o que realiza uma conex�o com o servidor de acordo com os par�metros
	 * definidos.
	 * @param ssl Indica se ser� realizada uma conex�o segura com o servidor.
	 * @throws NamingException Lan�a exce��o caso n�o for poss�vel se conectar
	 * no servidor.
	 */
	public void connect(boolean ssl) throws NamingException {		
		// inicializa os atributos da conex�o
		Hashtable<String, String> ldapEnv = new Hashtable<>();
		ldapEnv.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
		ldapEnv.put(Context.PROVIDER_URL, this.ldapUrl);
		ldapEnv.put(Context.SECURITY_AUTHENTICATION, "simple");
		ldapEnv.put(Context.SECURITY_PRINCIPAL, this.ldapSearchBindDn);
		ldapEnv.put(Context.SECURITY_CREDENTIALS, this.ldapSearchBindPassword);

		// se estiver habilitada a conex�o segura
		if (ssl)
			ldapEnv.put(Context.SECURITY_PROTOCOL, "ssl");

		// inicializa a conex�o
		this.ldapContext = new InitialDirContext(ldapEnv);
	}

	/**
	 * Realiza a desconex�o com o servidor.
	 * @throws NamingException Lan�a exce��o caso n�o for poss�vel realiza-la.
	 */
	public void disconnect() throws NamingException {
		// finaliza a conex�o
		if (this.ldapContext != null)
			this.ldapContext.close();
		this.ldapContext = null;
	}

	/**
	 * Informa se est� conectado com o servidor.
	 * @return Retorna <code>true</code> caso estiver conectado. Caso contr�rio,
	 * retorna <code>false</code>.
	 */
	public boolean isConnected() {
		return this.ldapContext != null;
	}

	/**
	 * Fun��o que realiza uma busca na �rvore LDAP conforme o filtro passado.
	 * @param filter O filtro a ser aplicado na busca.
	 * @param returnAttributes A lista com os atributos das entidades que ser�o
	 * retornados.
	 * @return Retorna a estrutura contendo o resultado da consulta.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel realizar a
	 * consulta.
	 */
	public NamingEnumeration<SearchResult> search(String filter, String... returnAttributes) throws Exception {
		// lan�a exce��o se n�o estiver conectado
		if (!this.isConnected())
			throw new Exception("Not connected to LDAP server");

		// ajusta os par�metros da busca
		SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		// se a lista de atributos a serem retornadas for vazia, ent�o retorna
		// todos os atributos
		if (returnAttributes.length > 0)
			searchControls.setReturningAttributes(returnAttributes);

		// realiza a busca
		return this.ldapContext.search(this.ldapSearchBase, filter, searchControls);
	}
	
	/**
	 * Fun��o que modifica os valores dos atributos de uma entrada do LDAP.
	 * @param dn O DN da entrada do LDAP que ser� modificado.
	 * @param attrsToBeModified A lista de atributos da entrada que ser�o
	 * modificados.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel realizar a
	 * altera��o.
	 */
	public void modify(String dn, ModificationItem... attrsToBeModified) throws Exception {
		// lan�a exce��o se n�o estiver conectado
		if (!this.isConnected())
			throw new Exception("Not connected to LDAP server");
		
		// modifica os atributos
		this.ldapContext.modifyAttributes(dn, attrsToBeModified);
	}
}
