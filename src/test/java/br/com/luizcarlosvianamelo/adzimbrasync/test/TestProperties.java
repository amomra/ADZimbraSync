package br.com.luizcarlosvianamelo.adzimbrasync.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestProperties {

	private final String PROPERTIES_FILE = "config.properties";
	
	private String ldapUrl;
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
		this.ldapUrl = prop.getProperty("ldap.url");
		this.ldapSearchBase = prop.getProperty("ldap.searchBase");
		this.ldapSearchBindDn = prop.getProperty("ldap.searchBindDn");
		this.ldapSearchBindPassword = prop.getProperty("ldap.searchBindPassword");
	}

	public String getLDAPUrl() {
		return ldapUrl;
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
