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
 * Classe utilizada para realizar a autenticação de um usuário de um domínio com
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
	 * Função privada que faz o tratamento do erro retornado pela função de
	 * autenticação em busca de mensagens de erro do AD.
	 * @param acct O objeto da conta do usuário no Zimbra.
	 * @param ex A exceção lançada pela função de autenticação.
	 * @throws ServiceException Relança a exceção passada no parâmetro
	 * <code>ex</code> caso ela não conter um código retornado pelo AD.
	 */
	private void handleAuthenticationException(Account acct, AccountServiceException ex)
			throws ServiceException {
		/*
		 * Aqui será feita a verificação da necessidade de alteração de
		 * senha por parte do usuário na próxima efetuação de login. Isto
		 * só pode ser feito através da verificação da mensagem da exceção
		 * que será lançada pela função.
		 */
		Throwable cause = ex.getCause(); 
		// verifica se é uma exceção do LDAP e se tem a mensagem tem o código de mudança de senha
		if (cause != null &&
				cause instanceof LdapException &&
				cause.getMessage().contains("NT_STATUS_PASSWORD_MUST_CHANGE")) {
			// marca o usuário para mudança de senha
			acct.setPasswordMustChange(true);
		}
		else
			// caso não tenha sido lançada outra exceção, então relança a exceção atual.
			throw ex;		
	}

	/**
	 * Função que realiza a autenticação do usuário com o servidor AD.
	 * @param acct O objeto da conta do usuário no Zimbra.
	 * @param password A senha digitada pelo usuário.
	 * @param authCtxt O contexto de autenticação.
	 * @param mArgs Argumentos.
	 * @throws Exception Lança exceção caso não foi possível autenticar o
	 * usuário devido aos nome de usuário e/ou senha inválidos. Também lança
	 * quando ocorre um problema de comunicação.
	 */
	@Override
	public void authenticate(Account acct, String password,
			Map<String, Object> authCtxt, List<String> mArgs) throws Exception {
		try {
			// deixa que a própria classe de provisioning do Zimbra faz a autenticação
			LdapProv prov = (LdapProv) Provisioning.getInstance();
			
			// pega o domínio da conta
			Domain domain = prov.getDomain(acct);
			
			// faz a chamada
			prov.externalLdapAuth(domain, AuthMech.custom , acct, password, authCtxt);
		} catch (AccountServiceException ex) {
			// trata a exceção
			this.handleAuthenticationException(acct, ex);
		}
	}

	/**
	 * Informa que é necessário verificar o estado da senha do usuário.
	 */
	@Override
	public boolean checkPasswordAging() {
		return true;
	}
}
