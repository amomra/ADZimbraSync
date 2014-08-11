package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.ldap.LdapProvisioning;
import com.zimbra.soap.type.AutoProvPrincipalBy;

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

	/**
	 * Fun��o que faz o provisionamento das contas contidas no servidor AD para
	 * o Zimbra. Esta fun��o � chamada pela thread interna do Zimbra respons�vel
	 * em realizar tal opera��o.
	 * @param scheduler A interface da thread do Zimbra para provisionamento.
	 * @throws ServiceException Lan�a exce��o quando ocorre um problema no
	 * provisionamento.
	 */
	@Override
	public void autoProvAccountEager(EagerAutoProvisionScheduler scheduler)
			throws ServiceException {
		// faz o provisionamento das contas do AD
		ADAutoProvision adAutoProv = new ADAutoProvision(this);
		adAutoProv.autoProvisionAccountsAndGroups();
		
		super.autoProvAccountEager(scheduler);
	}

	@Override
	public Account autoProvAccountLazy(Domain domain, String loginName,
			String loginPassword, AutoProvAuthMech authMech)
			throws ServiceException {
		// informa que esta opera��o n�o � suportada
		// return super.autoProvAccountLazy(domain, loginName, loginPassword, authMech);
		throw ServiceException.UNSUPPORTED();
	}

	@Override
	public Account autoProvAccountManual(Domain domain, AutoProvPrincipalBy by,
			String principal, String password) throws ServiceException {
		// informa que esta opera��o n�o � suportada
		// return super.autoProvAccountManual(domain, by, principal, password);
		throw ServiceException.UNSUPPORTED();
	}
}
