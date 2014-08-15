package br.com.luizcarlosvianamelo.adzimbrasync;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADTree;
import br.com.luizcarlosvianamelo.adzimbrasync.zimbra.ADChangePasswordListener;
import br.com.luizcarlosvianamelo.adzimbrasync.zimbra.ADCustomAuth;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.auth.ZimbraCustomAuth;
import com.zimbra.cs.account.ldap.ChangePasswordListener;
import com.zimbra.cs.extension.ExtensionException;
import com.zimbra.cs.extension.ZimbraExtension;

/**
 * Extensão do Zimbra que realiza o sincronismo das contas e listas de
 * distribuíção disponíveis no AD com a base de dados do Zimbra. As informações
 * dos usuários e das listas de distribuíção serão coletadas apenas do AD, sendo
 * que quaisquer alterações nestas através do Zimbra serão descartadas durante a
 * sincronização. Esta também é responsável em gerenciar a forma de autenticação
 * deste usuários com a base do AD, assim como o sincronismo da senha do
 * usuário quando esta for alterada.
 *  
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADZimbraSync implements ZimbraExtension {

	/**
	 * Construtor da classe.
	 */
	public ADZimbraSync() {
		
	}
	
	/**
	 * Função que realiza a inialização da extensão. Esta deverá iniciar a
	 * thread que realiza o sincronismo das contas.
	 */
	@Override
	public void init() throws ExtensionException, ServiceException {
		// ajusta a pasta de certificados
		ADTree.setSSLCertificatesPath("/opt/zimbra/java/jre/lib/security/cacerts", "changeit");
		
		// registra o objeto que irá tratar a mudança de senha do usuário no AD
		ChangePasswordListener.register("ADZimbraSyncPasswordChanger", new ADChangePasswordListener());
		// registra o método de autenticação do AD
		ZimbraCustomAuth.register("ad", new ADCustomAuth());
	}
	
	/**
	 * Função que finaliza a extensão.
	 */
	@Override
	public void destroy() {
		
	}

	/**
	 * Retorna o nome da extensão.
	 */
	@Override
	public String getName() {
		return "ADZimbraSync";
	}

}
