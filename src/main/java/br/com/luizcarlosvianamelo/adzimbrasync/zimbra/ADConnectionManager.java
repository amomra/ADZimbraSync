package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADTree;

import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Domain;

/**
 * Classe responsável em gerenciar as conexões do Zimbra com o AD.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADConnectionManager {

	/**
	 * Função que abre uma conexão com o AD com base nas configurações de
	 * provisionamento automático do domínio.
	 * @param domain O domínio configurado para provisionamento automático.
	 * @return Retorna o objeto da conexão com a árvore do AD. Caso não consiga
	 * conectar, retorna <code>null</code>.
	 * @throws Exception Lança uma exceção quando ocorre um erro na conexão.
	 */
	public static ADTree openDomainADConnection(Domain domain) throws Exception {
		// se conectando com o servidor AD do domínio
		String adURL = domain.getAutoProvLdapURL();
		String adBindDn = domain.getAutoProvLdapAdminBindDn();
		String adBindPassword = domain.getAutoProvLdapAdminBindPassword();
		String adSearchBase = domain.getAutoProvLdapSearchBase();
		
		/*
		 * Se estes atributos não estiverem ajustados, então não realiza a
		 * conexão. 
		 */
		if (adURL == null || adURL.equals("") ||
				adBindDn == null || adBindDn.equals("") ||
				adBindPassword == null || adBindPassword.equals("") ||
				adSearchBase == null || adSearchBase.equals(""))
			return null;

		ZimbraLog.autoprov.debug("AD - Connecting to AD - Url: %s | BindDn: %s | BindPassword: %s | " +
				"SearchBase: %s", adURL, adBindDn, adBindPassword, adSearchBase);

		ADTree adTree = new ADTree(
				adURL,
				adSearchBase,
				adBindDn,
				adBindPassword);
		adTree.connect();
		// caso não consiga se conectar
		if (!adTree.isConnected())
			return null;

		return adTree;
	}
	
}
