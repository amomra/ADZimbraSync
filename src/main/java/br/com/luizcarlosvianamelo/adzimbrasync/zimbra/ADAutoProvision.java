package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import java.util.Date;
import java.util.List;
import java.util.Map;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADGroup;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.AttributeAccessMode;

import com.zimbra.common.account.Key.DistributionListBy;
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

	protected ADProvisioning prov;

	/**
	 * Construtor da classe.
	 * @param prov A classe de provisionamento utilizada.
	 */
	ADAutoProvision (ADProvisioning prov) {
		this.prov = prov;
	}

	/**
	 * Função que faz o provisionamento automático da conta do AD no Zimbra. A
	 * função irá buscar a conta no Zimbra e atualizar os seus atributos de
	 * acordo com as configurações no AD. Caso a conta não exista no Zimbra, ela
	 * será criada.
	 * @param domain O domínio da conta.
	 * @param user O objeto do usuário lido do AD.
	 * @return Retorna o objeto do tipo {@link Account} representando a conta no
	 * zimbra.
	 * @throws Exception Lança exceção quando não for possível atualizar ou
	 * criar a conta no Zimbra.
	 */
	public synchronized Account autoProvisionAccount(Domain domain, ADUser user) throws Exception {
		// pega o mapeamento dos campos
		Map<String, String> attrMap = ZimbraLDAPMapper.getUserAttributeMapping(domain);
		Map<String, Object> attrValues = ZimbraLDAPMapper.mapObjectFieldsIntoAttributes(user,
				AttributeAccessMode.READ, attrMap);
		
		/*
		 * Não precisa fazer a checagem se a conta está desabilitada pois o
		 * Zimbra já impede o login neste caso.
		 */
		attrValues.put(Provisioning.A_zimbraMailStatus, Provisioning.MAIL_STATUS_ENABLED);
		
		/*
		 * Verifica se a conta já está cadastrada no Zimbra. Caso não estiver,
		 * cria ela. A função abaixo usa o e-mail para fazer a busca.
		 */
		Account acct = this.prov.getAccountByName(user.getMail());
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
		} else {
            ZimbraLog.autoprov.info("AD - Creating new Accout for e-mail \"%s\"", user.getMail());
            // significa que ela não foi encontrada. Logo, cria ela no Zimbra
            acct = this.prov.createAccount(user.getMail(), "AUTOPROVISIONED", attrValues);
        }

		return acct;
	}
	
	/**
	 * Função que faz o provisionamento automático da lista de distribuição do
	 * AD no Zimbra. A função irá buscar a lista no Zimbra e atualizar os seus
	 * atributos de acordo com as configurações no AD. Caso a conta não exista
	 * no Zimbra, ela será criada.
	 * @param domain O domínio da conta.
	 * @param distributionList O objeto da lista lido do AD.
	 * @param groupUsers Lista de usuários pertecentes ao grupo.
	 * @return Retorna o objeto do tipo {@link DistributionList} representando a
	 * lista de distribuição no Zimbra.
	 * @throws Exception Lança exceção quando não for possível atualizar ou
	 * criar a lista de distribuição no Zimbra.
	 */
	public synchronized DistributionList autoProvisionDistributionList(Domain domain, ADGroup distributionList, List<ADUser> groupUsers)
			throws Exception {
		// pega o mapeamento dos campos
		Map<String, String> attrMap = ZimbraLDAPMapper.getGroupAttributeMapping();
		Map<String, Object> attrValues = ZimbraLDAPMapper.mapObjectFieldsIntoAttributes(distributionList,
				AttributeAccessMode.READ, attrMap);
		
		// monta a lista de usuários
		String[] mailList = new String[groupUsers.size()];
		for (int i = 0; i < mailList.length; i++) {
			mailList[i] = groupUsers.get(i).getMail();
		}
		// ajusta na lista
		attrValues.put(Provisioning.A_zimbraMailForwardingAddress, mailList);

		/*
		 * Verifica se a lista já existe no Zimbra. Caso não existir, cria ela.
		 * O nome da lista no Zimbra representa o seu e-mail.
		 */
		DistributionList dl = this.prov.get(DistributionListBy.name, distributionList.getMail());
		if (dl != null) {
			/*
			 * Atualiza apenas se o horário da última modificação no AD for
			 * maior que a última verificação do domínio no Zimbra. Também
			 * verifica se a quantidade de usuários do grupo com e-mail foi
			 * modificada.
			 */
			int numEmailsOnList = dl.getAllMembers().length;
			Date lastDomainCheck = domain.getAutoProvLastPolledTimestamp();
			
			if (lastDomainCheck.before(distributionList.getWhenChanged()) ||
					groupUsers.size() != numEmailsOnList) {
				// atualiza a lista
				dl.modify(attrValues);
			}
		} else
			// significa que a lista não foi encontrada. Logo. cria ela no Zimbra
			dl = this.prov.createDistributionList(distributionList.getMail(), attrValues);
		
		return dl;
	}
}
