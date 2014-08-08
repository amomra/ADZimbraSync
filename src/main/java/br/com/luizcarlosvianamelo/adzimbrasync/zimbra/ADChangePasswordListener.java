package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import java.util.Map;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADTree;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUsersRepository;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ldap.ChangePasswordListener;;

/**
 * Classe respons�vel em realizar a mudan�a da senha de um usu�rio no AD quando
 * esta opera��o for realizada atrav�s do Zimbra.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
@SuppressWarnings("rawtypes")
public class ADChangePasswordListener extends ChangePasswordListener {

	/**
	 * Construtor da classe.
	 */
	public ADChangePasswordListener() {
	}
			
	/**
	 * Fun��o que ser� chamada quando o processo de mudan�a de senha for
	 * realizado pelo Zimbra. Esta ir� fazer a mudan�a da senha no AD.
	 * @param account O objeto da conta do Zimbra associada a conta do AD que
	 * ter� a sua senha modificada.
	 * @param newPassword A <code>String</code> contendo a senha a ser ajustada
	 * no AD.
	 * @param context Par�metro n�o utilizado.
	 * @param attrsToModify Par�metro n�o utilizado.
	 */
	@Override
	public void preModify(Account account, String newPassword, Map context,
			Map<String, Object> attrsToModify) throws ServiceException {
		// captura as exce��es do LDAP e lan�a as exce��es do Zimbra
		try {
			Provisioning prov = Provisioning.getInstance();
			// pega as informa��es do dom�nio ao qual o usu�rio pertence
			Domain dom = prov.getDomain(account);
			
			// pega as configura��es do LDAP para auto-provisioning
			String ldapUrl = dom.getAutoProvLdapURL();
			String ldapSearchBase = dom.getAutoProvLdapSearchBase();
			// pega o usu�rio de administrador do LDAP
			String ldapAdminBindDn = dom.getAutoProvLdapAdminBindDn();
			String ldapAdminBindPassword = dom.getAutoProvLdapAdminBindPassword();
			
			// faz a conex�o segura com o servidor
			ADTree adTree = new ADTree(
					ldapUrl,
					ldapSearchBase,
					ldapAdminBindDn,
					ldapAdminBindPassword);
			// ajusta a pasta de certificados
			adTree.setSSLCertificatesPath("/opt/zimbra/java/jre/lib/security/cacerts", "changeit");
			
			adTree.connect(true);
			
			// busca o usu�rio no AD
			ADUsersRepository rep = adTree.getUsersRepository();
			ADUser user = rep.queryUserByAccountName(account.getUid());
			
			// se n�o encontrou o usu�rio no AD
			if (user == null)
				throw AccountServiceException.NO_SUCH_ACCOUNT(account.getUid());
			
			// faz a mudan�a da senha do usu�rio
			rep.changeUserPassword(user, newPassword);
			
			// desconecta
			adTree.disconnect();
		} catch (Exception ex) {
			throw AccountServiceException.PERM_DENIED(ex.getMessage());
		}
		
	}
	
	/**
	 * Fun��o que ser� chamada ap�s a mudan�a da senha do usu�rio. Neste caso,
	 * n�o � necess�rio realizar a��es ap�s a mudan�a da senha.
	 */
	@Override
	public void postModify(Account account, String newPassword, Map context) {
		// n�o faz nada
	}

}
