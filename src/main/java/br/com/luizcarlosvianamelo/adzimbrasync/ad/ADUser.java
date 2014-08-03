package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import javax.naming.directory.SearchResult;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPConverter;

/**
 * Classe que representa uma entrada do AD que simboliza um usuário. Este
 * usuário pode ser usado para fazer a autenticação na árvore do LDAP caso ele
 * tenha permissão para tal. Um objeto
 * desta classe pode ser obtido a partir do resultado de uma consulta do LDAP
 * através da chamada da função de conversão
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
	 * padrão <code>null</code>.
	 */
	public ADUser() {
	}

	/**
	 * Retorna o código do país do usuário.
	 */
	public int getCountryCode() {
		return countryCode;
	}

	/**
	 * Ajusta o código do país do usuário.
	 */
	public void setCountryCode(int countryCode) {
		this.countryCode = countryCode;
	}

	/**
	 * Retorna o nome completo do usuário.
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * Ajusta o nome completo do usuário.
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * Retorna o sobrenome (surname) do usuário.
	 */
	public String getSn() {
		return sn;
	}

	/**
	 * Ajusta o sobrenome (surname) do usuário.
	 */
	public void setSn(String sn) {
		this.sn = sn;
	}
}
