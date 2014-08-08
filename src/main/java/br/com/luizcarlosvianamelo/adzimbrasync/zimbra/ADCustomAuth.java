package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.auth.ZimbraCustomAuth;
import com.zimbra.cs.account.auth.AuthMechanism.AuthMech;
import com.zimbra.cs.account.ldap.LdapProv;
import com.zimbra.cs.ldap.LdapException;

/**
 * Classe utilizada para realizar a autentica��o de um usu�rio de um dom�nio com
 * o servidor AD configurado para provisionamento.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADCustomAuth extends ZimbraCustomAuth {

	/**
	 * Construtor da classe.
	 */
	public ADCustomAuth() {
	}

	/**
	 * Fun��o privada que faz o tratamento do erro retornado pela fun��o de
	 * autentica��o em busca de mensagens de erro do AD.
	 * @param acct O objeto da conta do usu�rio no Zimbra.
	 * @param ex A exce��o lan�ada pela fun��o de autentica��o.
	 * @throws ServiceException Relan�a a exce��o passada no par�metro
	 * <code>ex</code> caso ela n�o conter um c�digo retornado pelo AD.
	 */
	private void handleAuthenticationException(Account acct, AccountServiceException ex)
			throws ServiceException {
		/*
		 * Aqui ser� feita a verifica��o da necessidade de altera��o de
		 * senha por parte do usu�rio na pr�xima efetua��o de login. Isto
		 * s� pode ser feito atrav�s da verifica��o da mensagem da exce��o
		 * que ser� lan�ada pela fun��o.
		 */
		Throwable cause = ex.getCause(); 
		// verifica se � uma exce��o do LDAP e se tem a mensagem tem o c�digo de mudan�a de senha
		if (cause != null &&
				cause instanceof LdapException &&
				cause.getMessage().contains("NT_STATUS_PASSWORD_MUST_CHANGE")) {
			// marca o usu�rio para mudan�a de senha
			acct.setPasswordMustChange(true);
		}
		else
			// caso n�o tenha sido lan�ada outra exce��o, ent�o relan�a a exce��o atual.
			throw ex;		
	}

	/**
	 * Fun��o que realiza a autentica��o do usu�rio com o servidor AD.
	 * @param acct O objeto da conta do usu�rio no Zimbra.
	 * @param password A senha digitada pelo usu�rio.
	 * @param authCtxt O contexto de autentica��o.
	 * @param mArgs Argumentos.
	 * @throws Exception Lan�a exce��o caso n�o foi poss�vel autenticar o
	 * usu�rio devido aos nome de usu�rio e/ou senha inv�lidos. Tamb�m lan�a
	 * quando ocorre um problema de comunica��o.
	 */
	@Override
	public void authenticate(Account acct, String password,
			Map<String, Object> authCtxt, List<String> mArgs) throws Exception {
		try {
			// deixa que a pr�pria classe de provisioning do Zimbra faz a autentica��o
			LdapProv prov = (LdapProv) Provisioning.getInstance();
			
			// pega o dom�nio da conta
			Domain domain = prov.getDomain(acct);
			
			// faz a chamada
			prov.externalLdapAuth(domain, AuthMech.custom , acct, password, authCtxt);
		} catch (AccountServiceException ex) {
			// trata a exce��o
			this.handleAuthenticationException(acct, ex);
		}
	}

	/**
	 * Informa que � necess�rio verificar o estado da senha do usu�rio.
	 */
	@Override
	public boolean checkPasswordAging() {
		return true;
	}
}
