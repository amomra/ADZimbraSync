package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import com.zimbra.cs.account.ldap.LdapProvisioning;

/**
 * Classe respons�vel em realizar o provisionamento dos usu�rios e grupos do
 * AD para o Zimbra. As fun��es desta classe ir�o realizar uma c�pia destes na
 * base interna do AD.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADProvisioning extends LdapProvisioning {

	/**
	 * Construtor da classe.
	 */
	public ADProvisioning() {
		super();
	}
}
