package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import java.util.Map;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADTree;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUsersRepository;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AttributeManager;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.callback.CallbackContext;

/**
 * Classe responsável em monitorar as mudanças realizadas nos atributos de uma
 * entidade do Zimbra.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADAttributeManager extends AttributeManager {

	/**
	 * Construtor da classe.
	 */
	public ADAttributeManager(String dir) throws ServiceException {
		super(dir);
	}
	
	/**
	 * Função que verifica se os atributos a serem modificados no Zimbra estão
	 * mapeados para o AD.
	 * @param attrMap O mapeamento dos atributos do AD com os atributos do
	 * Zimbra.
	 * @param attrs Os atributos a serem modificados.
	 * @return Retorna <code>true</code> caso um atributo que estiver sendo
	 * modificado estiver mapeado. Caso contrário, retorna <code>false</code>.
	 */
	private static boolean haveAttributesToBeModified(Map<String, String> attrMap, Map<String, ? extends Object> attrs) {
		for (java.util.Map.Entry<String, String> entry : attrMap.entrySet()) {
			// caso o atributo mapeado do AD for modificado no Zimbra
			if (attrs.containsKey(entry.getValue()))
				return true;
		}
		return false;
	}

	/**
	 * Função chamada após a modificação dos atributos de uma entrada no Zimbra.
	 * Esta será chamada quando o administrator do sistema realizar uma
	 * modificação nas informações de um usuário. Apenas os domínios que
	 * estiverem habilitados para provisionamento serão considerados.
	 * @param attrs A lista dos atributos que foram modificados e seus
	 * respectivos valores.
	 * @param entry A entrada no Zimbra que foi modificada.
	 * @param context O contexto da chamada.
	 * @param allowCallback Indica se a chamada é permitida.
	 */
	@Override
	public void postModify(Map<String, ? extends Object> attrs,
            Entry entry, CallbackContext context, boolean allowCallback) {
		// faz o pós-processamento padrão
		super.postModify(attrs, entry, context, allowCallback);
		
		// ignora se a entrada não for uma conta
		if (!(entry instanceof Account))
			return;

		Account acct = (Account) entry;

		try {			
			ADProvisioning prov = (ADProvisioning) Provisioning.getInstance();
			
			// pega o domínio da conta
			Domain domain = prov.getDomain(acct);
			
			// verifica se há atributos para serem modificados antes de abrir a conexão
			Map<String, String> attrMap = ZimbraLDAPMapper.getUserAttributeMapping(domain);
			if (!haveAttributesToBeModified(attrMap, attrs))
				// finaliza a função já que não há atributos mapeados para serem modificados
				return;

			// busca o usuário no AD
			ADTree adTree = ADConnectionManager.openDomainADConnection(domain);
			if (adTree == null) {
				/*
				 * Se não abrir a conexão é porque o domínio não está habilitado
				 * para provisionamento. Para os problemas de conexão são
				 * lançadas exceções.
				 */
				return;
			}
			
			ADUsersRepository rep = adTree.getUsersRepository();
			ADUser user = rep.queryUserByAccountName(acct.getUid());
			if (user == null) {
				ZimbraLog.account.error(String.format("AD - Can't find user \"%s\" to modify in AD", acct.getUid()));
				return;
			}
			
			// preenche os atributos a serem modificados
			ZimbraLDAPMapper.fillAttributesIntoObjectFields(user, attrs, attrMap);
			
			// modifica o usuário
			rep.modifyUser(user);
			
			// e desconecta
			adTree.disconnect();

		} catch (Exception e) {
			ZimbraLog.account.error(String.format("AD - postModify caught exception: %s", e.getMessage()), e);
		}
	}
}
