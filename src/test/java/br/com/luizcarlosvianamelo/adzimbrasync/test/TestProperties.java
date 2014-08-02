package br.com.luizcarlosvianamelo.adzimbrasync.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestProperties {

	private final String PROPERTIES_FILE = "config.properties";
	
	private String ldapHostname;
	private int ldapPort;
	private String ldapSearchBase;
	private String ldapSearchBindDn;
	private String ldapSearchBindPassword;
	
	public TestProperties() throws IOException {
		// carrega o inputstream do arquivo de propriedades
		InputStream in = this.getClass().getClassLoader().getResourceAsStream(PROPERTIES_FILE);
		
		// e tenta fazer o parser das propriedades
		Properties prop = new Properties();
		prop.load(in);
		
		// ajusta os parametros
		this.ldapHostname = prop.getProperty("ldap.hostname");
		this.ldapPort = Integer.parseInt(prop.getProperty("ldap.port"));
		this.ldapSearchBase = prop.getProperty("ldap.searchBase");
		this.ldapSearchBindDn = prop.getProperty("ldap.searchBindDn");
		this.ldapSearchBindPassword = prop.getProperty("ldap.searchBindPassword");
	}

	public String getLDAPHostname() {
		return ldapHostname;
	}

	public int getLDAPPort() {
		return ldapPort;
	}

	public String getLDAPSearchBase() {
		return ldapSearchBase;
	}

	public String getLDAPSearchBindDn() {
		return ldapSearchBindDn;
	}

	public String getLDAPSearchBindPassword() {
		return ldapSearchBindPassword;
	}
}
