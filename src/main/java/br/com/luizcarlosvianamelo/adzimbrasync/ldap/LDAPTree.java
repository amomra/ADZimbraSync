package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

/**
 * Classe que representa uma árvore do LDAP. Esta árvore contém uma série
 * de entidades que podem ser utilizados para representar os objetos de uma
 * organização. Estas entidades podem ser retornadas a partir de uma série
 * de consultas à árvore.
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
	 * Construtor padrão da classe. Este inicializa os parâmetros de conexão
	 * com valores padrões.
	 */
	public LDAPTree() {
		this("", "", "", "");
	}

	/**
	 * Construtor parametrizado da classe.
	 * @param ldapUrl O endereço do servidor LDAP.
	 * @param ldapSearchBase A base de busca na árvore do LDAP.
	 * @param ldapSearchBindDn O DN do usuário que pode realizar consultas sobre
	 * a árvore.
	 * @param ldapSearchBindPassword A senha do usuário de consulta.
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
	 * Função que ajusta o caminho das pastas onde estão inclusos os
	 * certificados do servidor LDAP. Estes são utilizados para a realização
	 * de uma conexão segura com o servidor.
	 * @param certPath O caminho da pasta de certificados.
	 * @param certPassword A senha do arquivo de certificados.
	 */
	public static void setSSLCertificatesPath(String certPath, String certPassword) {
		// ajusta o caminho no SO com as chaves para o servidor
		System.setProperty("javax.net.ssl.trustStore", certPath);
		System.setProperty("javax.net.ssl.trustStorePassword", certPassword);
	}

	/**
	 * Retorna o endereço do servidor LDAP.
	 */
	public String getLdapUrl() {
		return ldapUrl;
	}

	/**
	 * Ajusta o endereço do servidor LDAP.
	 */
	public void setLdapUrl(String ldapUrl) {
		this.ldapUrl = ldapUrl;
	}

	/**
	 * Retorna o DN que é utilizado como base nas buscas.
	 */
	public String getLdapSearchBase() {
		return ldapSearchBase;
	}

	/**
	 * Ajusta o DN que é utilizado como base nas buscas.
	 */
	public void setLdapSearchBase(String ldapSearchBase) {
		this.ldapSearchBase = ldapSearchBase;
	}

	/**
	 * Retorna o DN do usuário que será utilizado para a realização das buscas
	 * na árvore.
	 */
	public String getLdapSearchBindDn() {
		return ldapSearchBindDn;
	}

	/**
	 * Ajusta o DN do usuário que será utilizado para a realização das buscas na
	 * árvore.
	 */
	public void setLdapSearchBindDn(String ldapSearchBindDn) {
		this.ldapSearchBindDn = ldapSearchBindDn;
	}

	/**
	 * Retorna a senha do usuário que será utilizado para a realização das
	 * buscas na árvore.
	 */
	public String getLdapSearchBindPassword() {
		return ldapSearchBindPassword;
	}

	/**
	 * Ajusta a senha do usuário que será utilizado para a realização das
	 * buscas na árvore.
	 */
	public void setLdapSearchBindPassword(String ldapSearchBindPassword) {
		this.ldapSearchBindPassword = ldapSearchBindPassword;
	}

	/**
	 * Função que realiza uma conexão não segura com o servidor de acordo
	 * com os parâmetros definidos.
	 * @throws NamingException Lança exceção caso não for possível se conectar
	 * no servidor.
	 */
	public void connect() throws NamingException {
		// realiza uma conexão não segura
		this.connect(false);
	}

	/**
	 * Função que realiza uma conexão com o servidor de acordo com os parâmetros
	 * definidos.
	 * @param ssl Indica se será realizada uma conexão segura com o servidor.
	 * @throws NamingException Lança exceção caso não for possível se conectar
	 * no servidor.
	 */
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

	/**
	 * Realiza a desconexão com o servidor.
	 * @throws NamingException Lança exceção caso não for possível realiza-la.
	 */
	public void disconnect() throws NamingException {
		// finaliza a conexão
		if (this.ldapContext != null)
			this.ldapContext.close();
		this.ldapContext = null;
	}

	/**
	 * Informa se está conectado com o servidor.
	 * @return Retorna <code>true</code> caso estiver conectado. Caso contrário,
	 * retorna <code>false</code>.
	 */
	public boolean isConnected() {
		return this.ldapContext != null;
	}

	/**
	 * Função que realiza uma busca na árvore LDAP conforme o filtro passado.
	 * @param filter O filtro a ser aplicado na busca.
	 * @param returnAttributes A lista com os atributos das entidades que serão
	 * retornados.
	 * @return Retorna a estrutura contendo o resultado da consulta.
	 * @throws Exception Lança uma exceção quando não for possível realizar a
	 * consulta.
	 */
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

	/**
	 * Função que faz a busca de um tipo específico de entrada do LDAP.
	 * @param objType A classe que representa a entrada do LDAP.
	 * @return Retorna a lista de objetos com as entradas do LDAP. Caso não
	 * existam entradas, retorna uma lista vazia.
	 * @throws Exception Lança uma exceção quando não for possível realizar a
	 * consulta. 
	 */
	public <ObjectType extends LDAPEntry> List<ObjectType> search(Class<ObjectType> objType)
			throws Exception {
		return this.search(objType, "");
	}

	/**
	 * Função que faz a busca de um tipo específico de entrada do LDAP de acordo
	 * com o filtro passado.
	 * @param objType A classe que representa a entrada do LDAP.
	 * @param filter O filtro a ser utilizado na busca.
	 * @return Retorna a lista de objetos com as entradas do LDAP de acordo com
	 * o filtro LDAP. Caso não existam entradas, retorna uma lista vazia.
	 * @throws Exception Lança uma exceção quando não for possível realizar a
	 * consulta. 
	 */
	public <ObjectType extends LDAPEntry> List<ObjectType> search(Class<ObjectType> objType, String filter)
			throws Exception {
		// cria uma instância do objeto apenas para retornar a query da entrada
		ObjectType obj = objType.newInstance();
		
		// formata a query a ser realizada
		String query = String.format(obj.getEntryQueryFormat(), filter);
		
		// faz a consulta
		NamingEnumeration<SearchResult> result = this.search(query);

		// armazena a lista de entradas
		List<ObjectType> ldapEntries = new ArrayList<>();

		// monta os objetos
		while (result.hasMoreElements()) {

			SearchResult entry = result.nextElement();

			ObjectType ldapEntry = LDAPEntry.parseEntry(objType, entry.getAttributes());
			// adiciona na lista
			ldapEntries.add(ldapEntry);
		}

		return ldapEntries;
	}
	
	/**
	 * Função que modifica os valores dos atributos de uma entrada do LDAP.
	 * @param dn O DN da entrada do LDAP que será modificado.
	 * @param attrsToBeModified A lista de atributos da entrada que serão
	 * modificados.
	 * @throws Exception Lança uma exceção quando não for possível realizar a
	 * alteração.
	 */
	public void modify(DN dn, ModificationItem... attrsToBeModified) throws Exception {
		// lança exceção se não estiver conectado
		if (!this.isConnected())
			throw new Exception("Not connected to LDAP server");
		
		// modifica os atributos
		this.ldapContext.modifyAttributes(dn.toString(), attrsToBeModified);
	}
	
	/**
	 * Função que modifica os valores dos atributos de uma entrada do LDAP.
	 * @param dn O DN da entrada do LDAP que será modificado.
	 * @param attrsToBeModified A lista de atributos da entrada que serão
	 * modificados.
	 * @throws Exception Lança uma exceção quando não for possível realizar a
	 * alteração.
	 */
	public void modify(DN dn, Attributes attrsToBeModified) throws Exception {
		// lança exceção se não estiver conectado
		if (!this.isConnected())
			throw new Exception("Not connected to LDAP server");
		
		// modifica os atributos
		this.ldapContext.modifyAttributes(dn.toString(), DirContext.REPLACE_ATTRIBUTE, attrsToBeModified);
	}
	
	/**
	 * Função que modifica os valores dos atributos de uma entrada do LDAP com
	 * base no objeto Java.
	 * @param dn O DN da entrada do LDAP que será modificado.
	 * @param obj O objeto que contém os atributos a serem modificados.
	 * @param attrsToBeModified A lista de atributos da entrada que serão
	 * modificados.
	 * @throws Exception Lança uma exceção quando não for possível realizar a
	 * alteração.
	 */
	public <ObjectType extends LDAPEntry> void modify(DN dn, ObjectType obj, String... attrsToBeModified)
			throws Exception {
		// pega a lista de atributos que serão modificados
		Attributes attrs = obj.getLDAPAttributes(AttributeAccessMode.WRITE, attrsToBeModified);
		// se a lista não estiver vazia
		if (attrs.size() > 0) {
			
			// faz a mudança
			this.modify(dn, attrs);
		}
	}
}
