package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import javax.naming.directory.SearchResult;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPConverter;

/**
 * Classe que representa uma entrada do AD que simboliza um usu�rio. Este
 * usu�rio pode ser usado para fazer a autentica��o na �rvore do LDAP caso ele
 * tenha permiss�o para tal. Um objeto
 * desta classe pode ser obtido a partir do resultado de uma consulta do LDAP
 * atrav�s da chamada da fun��o de convers�o
 * {@link LDAPConverter#convert(Class, SearchResult) convert}.
 *  
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADUser extends ADEntry {
	
	@LDAPAttribute
	private int countryCode;
	
	@LDAPAttribute
	private String givenName;
	
	@LDAPAttribute
	private String sn;
	
	/**
	 * Construtor da classe. Este inicializa os atributos com o valor
	 * padr�o <code>null</code>.
	 */
	public ADUser() {
	}

	/**
	 * Retorna o c�digo do pa�s do usu�rio.
	 */
	public int getCountryCode() {
		return countryCode;
	}

	/**
	 * Ajusta o c�digo do pa�s do usu�rio.
	 */
	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * Retorna o nome completo do usu�rio.
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * Ajusta o nome completo do usu�rio.
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * Retorna o sobrenome (surname) do usu�rio.
	 */
	public String getSn() {
		return sn;
	}

	/**
	 * Ajusta o sobrenome (surname) do usu�rio.
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}
}
