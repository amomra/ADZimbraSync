package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import javax.naming.directory.Attributes;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.AttributeAccessMode;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPEntry;

/**
 * Classe que representa uma entrada do AD que simboliza um usuário. Este
 * usuário pode ser usado para fazer a autenticação na árvore do LDAP caso ele
 * tenha permissão para tal. Um objeto
 * desta classe pode ser obtido a partir do resultado de uma consulta do LDAP
 * através da chamada da função de conversão
 * {@link LDAPEntry#parseEntry(Class, Attributes) parseEntry}.
 *  
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADUser extends ADEntry {

	@LDAPAttribute( name = "co" )
	private String country;

	@LDAPAttribute
	private String givenName;

	@LDAPAttribute
	private String sn;

	@LDAPAttribute(
			name = "unicodePwd",
			accessMode = AttributeAccessMode.WRITE,
			raw = true )
	private byte[] password;

	/**
	 * Construtor da classe. Este inicializa os atributos com o valor
	 * padrão <code>null</code>.
	 */
	public ADUser() {
	}

	@Override
	public String getEntryQueryFormat() {
		return "(&(objectCategory=Person)%s)";
	}

	/**
	 * Retorna o nome do país do usuário.
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Ajusta o nome do país do usuário.
	 */
	public void setCountryCode(String country) {
		this.country = country;
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

	/**
	 * Retorna a senha do usuário.
	 */
	public String getPassword() {
		try {
			// codifica a senha se ela for diferente de nulo
			if (this.password != null) {
				// remove as aspas do início e fim
				String quotedPassword = new String(this.password, "UTF-16LE");
				
				return quotedPassword.substring(1, quotedPassword.length() - 1);
			}
		} catch (Exception e) {}
		return null;
	}

	/**
	 * Ajusta a senha do usuário.
	 */
	public void setPassword(String password) {
		/*
		 * A senha deverá ser codificada em UTF-16 e entre aspas para que ela
		 * seja alterada no servidor.
		 */
		try {
			// codifica a senha para o tipo suportado
			String quotedPassword = String.format("\"%s\"",password);

			this.password = quotedPassword.getBytes("UTF-16LE");
		} catch (Exception e) {
			this.password = null;
		}
	}
}
