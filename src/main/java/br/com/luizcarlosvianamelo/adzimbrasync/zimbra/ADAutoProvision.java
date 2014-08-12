package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADGroup;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADTree;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPConverter;

import com.zimbra.common.account.Key.DistributionListBy;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DistributionList;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;

/**
 * Classe que contém as funcionalidades para criação ou alteração dos usuários
 * e grupos do AD no Zimbra.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADAutoProvision {

	/**
	 * Classe interna que contém o mapeamento dos atributos do AD para os
	 * atributos do Zimbra.
	 * 
	 * @author Luiz Carlos Viana Melo
	 *
	 */
	static class ZimbraAttributeMapper {
		@SuppressWarnings("serial")
		private static final Map<String, String> DEFAULT_ZIMBRA_AD_USER_ATTR_MAP = new HashMap<String, String>() {{
			put("cn", "cn");
			put("name", "displayName");
			put("givenName", "givenName");
			put("sn", "sn");
		}};

		/**
		 * Função que retorna o mapeamento dos atributos do AD com os atributos
		 * do Zimbra.
		 * @return A lista associativa com o mapeamento dos atributos. A chave
		 * desta será o nome do atributo no AD enquanto o valor será o nome do
		 * atributo no Zimbra.
		 */
		public static Map<String, String> getUserAttributeMapping() {
			return ZimbraAttributeMapper.DEFAULT_ZIMBRA_AD_USER_ATTR_MAP;
		}
		
		/**
		 * Função que retorna o mapeamento dos atributos do AD com os atributos
		 * do Zimbra. Também coleta o mapeamento configurado no atributo
		 * <code>zimbraAutoProvAttrMap</code>.
		 * @param domain O domínio onde está configurado o mapeamento.
		 * @return A lista associativa com o mapeamento dos atributos. A chave
		 * desta será o nome do atributo no AD enquanto o valor será o nome do
		 * atributo no Zimbra.
		 */
		public static Map<String, String> getUserAttributeMapping(Domain domain) {
			// TODO Buscar a lista de atributos mapeados do domínio
			return ZimbraAttributeMapper.DEFAULT_ZIMBRA_AD_USER_ATTR_MAP;
		}
	}

	protected ADProvisioning prov;

	ADAutoProvision (ADProvisioning prov) {
		this.prov = prov;
	}

	protected ADTree openDomainADConnection(Domain domain) throws Exception {
		// se conectando com o servidor AD do domínio
		String adURL = domain.getAutoProvLdapURL();
		String adBindDn = domain.getAutoProvLdapAdminBindDn();
		String adBindPassword = domain.getAutoProvLdapAdminBindPassword();
		String adSearchBase = domain.getAutoProvLdapSearchBase();

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

	public synchronized Account autoProvisionAccount(Domain domain, ADUser user) throws Exception {
		// pega o mapeamento dos campos
		Map<String, String> attrMap = ZimbraAttributeMapper.getUserAttributeMapping(domain);
		Map<String, Object> attrValues = LDAPConverter.mapFieldsIntoAttributes(user, attrMap);
		
		// TODO Checar se a conta está ativa
		attrValues.put(Provisioning.A_zimbraMailStatus, Provisioning.MAIL_STATUS_ENABLED);
		
		/*
		 * Verifica se a conta já está cadastrada no Zimbra. Caso não estiver,
		 * cria ela.
		 */
		Account acct = this.prov.getAccountByName(user.getsAMAccountName());
		if (acct != null) {
			/*
			 * Atualiza apenas se o horário da última modificação no AD for
			 * maior que a última verificação do domínio no Zimbra.
			 */
			Date lastDomainCheck = domain.getAutoProvLastPolledTimestamp();
			if (lastDomainCheck.before(user.getWhenChanged())) {
				ZimbraLog.autoprov.debug("AD - Modifying account \"%s\"", user.getDistinguishedName());
				// atualiza a conta
				acct.modify(attrValues);
			} else
				ZimbraLog.autoprov.debug("AD - No modification is needed for account \"%s\"", user.getDistinguishedName());
		} else
			// significa que ela não foi encontrada. Logo, cria ela no Zimbra
			acct = this.prov.createAccount(user.getMail(), "AUTOPROVISIONED", attrValues);

		return acct;
	}
	
	public synchronized DistributionList autoProvisionDistributionList(Domain domain, ADGroup distributionList)
			throws Exception {
		
		/*
		 * Verifica se a lista já existe no Zimbra. Caso não existir, cria ela.
		 */
		DistributionList dl = this.prov.get(DistributionListBy.name, distributionList.getCn());
		if (dl != null) {
			
		}
		//dl.get
		return null;
	}
}
