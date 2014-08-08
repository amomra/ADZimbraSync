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
 * Classe responsável em realizar a mudança da senha de um usuário no AD quando
 * esta operação for realizada através do Zimbra.
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
	 * Função que será chamada quando o processo de mudança de senha for
	 * realizado pelo Zimbra. Esta irá fazer a mudança da senha no AD.
	 * @param account O objeto da conta do Zimbra associada a conta do AD que
	 * terá a sua senha modificada.
	 * @param newPassword A <code>String</code> contendo a senha a ser ajustada
	 * no AD.
	 * @param context Parâmetro não utilizado.
	 * @param attrsToModify Parâmetro não utilizado.
	 */
	@Override
	public void preModify(Account account, String newPassword, Map context,
			Map<String, Object> attrsToModify) throws ServiceException {
		// captura as exceções do LDAP e lança as exceções do Zimbra
		try {
			Provisioning prov = Provisioning.getInstance();
			// pega as informações do domínio ao qual o usuário pertence
			Domain dom = prov.getDomain(account);
			
			// pega as configurações do LDAP para auto-provisioning
			String ldapUrl = dom.getAutoProvLdapURL();
			String ldapSearchBase = dom.getAutoProvLdapSearchBase();
			// pega o usuário de administrador do LDAP
			String ldapAdminBindDn = dom.getAutoProvLdapAdminBindDn();
			String ldapAdminBindPassword = dom.getAutoProvLdapAdminBindPassword();
			
			// faz a conexão segura com o servidor
			ADTree adTree = new ADTree(
					ldapUrl,
					ldapSearchBase,
					ldapAdminBindDn,
					ldapAdminBindPassword);
			// ajusta a pasta de certificados
			adTree.setSSLCertificatesPath("/opt/zimbra/java/jre/lib/security/cacerts", "changeit");
			
			adTree.connect(true);
			
			// busca o usuário no AD
			ADUsersRepository rep = adTree.getUsersRepository();
			ADUser user = rep.queryUserByAccountName(account.getUid());
			
			// se não encontrou o usuário no AD
			if (user == null)
				throw AccountServiceException.NO_SUCH_ACCOUNT(account.getUid());
			
			// faz a mudança da senha do usuário
			rep.changeUserPassword(user, newPassword);
			
			// desconecta
			adTree.disconnect();
		} catch (Exception ex) {
			throw AccountServiceException.PERM_DENIED(ex.getMessage());
		}
		
	}
	
	/**
	 * Função que será chamada após a mudança da senha do usuário. Neste caso,
	 * não é necessário realizar ações após a mudança da senha.
	 */
	@Override
	public void postModify(Account account, String newPassword, Map context) {
		// não faz nada
	}

}
