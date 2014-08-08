package br.com.luizcarlosvianamelo.adzimbrasync;

import br.com.luizcarlosvianamelo.adzimbrasync.zimbra.ADChangePasswordListener;
import br.com.luizcarlosvianamelo.adzimbrasync.zimbra.ADCustomAuth;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.auth.ZimbraCustomAuth;
import com.zimbra.cs.account.ldap.ChangePasswordListener;
import com.zimbra.cs.extension.ExtensionException;
import com.zimbra.cs.extension.ZimbraExtension;

/**
 * Extens�o do Zimbra que realiza o sincronismo das contas e listas de
 * distribu���o dispon�veis no AD com a base de dados do Zimbra. As informa��es
 * dos usu�rios e das listas de distribu���o ser�o coletadas apenas do AD, sendo
 * que quaisquer altera��es nestas atrav�s do Zimbra ser�o descartadas durante a
 * sincroniza��o. Esta tamb�m � respons�vel em gerenciar a forma de autentica��o
 * deste usu�rios com a base do AD, assim como o sincronismo da senha do
 * usu�rio quando esta for alterada.
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
	 * Fun��o que realiza a inializa��o da extens�o. Esta dever� iniciar a
	 * thread que realiza o sincronismo das contas.
	 */
	@Override
	public void init() throws ExtensionException, ServiceException {
		// registra o objeto que ir� tratar a mudan�a de senha do usu�rio no AD
		ChangePasswordListener.register("ADZimbraSyncPasswordChanger", new ADChangePasswordListener());
		// registra o m�todo de autentica��o do AD
		ZimbraCustomAuth.register("ad_ext", new ADCustomAuth());
	}
	
	/**
	 * Fun��o que finaliza a extens�o.
	 */
	@Override
	public void destroy() {
		
	}

	/**
	 * Retorna o nome da extens�o.
	 */
	@Override
	public String getName() {
		return "ADZimbraSync";
	}

}
