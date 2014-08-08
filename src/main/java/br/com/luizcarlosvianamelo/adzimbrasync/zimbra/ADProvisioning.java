package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import com.zimbra.cs.account.ldap.LdapProvisioning;

/**
 * Classe responsável em realizar o provisionamento dos usuários e grupos do
 * AD para o Zimbra. As funções desta classe irão realizar uma cópia destes na
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
