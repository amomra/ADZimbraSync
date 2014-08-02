package br.com.luizcarlosvianamelo.adzimbrasync;

import com.zimbra.common.service.ServiceException;
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
