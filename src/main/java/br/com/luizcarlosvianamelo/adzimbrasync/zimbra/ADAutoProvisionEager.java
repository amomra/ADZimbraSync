package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import java.util.Date;
import java.util.List;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADGroup;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADGroupsRepository;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADTree;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUsersRepository;

import com.zimbra.common.account.Key.DomainBy;
import com.zimbra.common.account.ZAttrProvisioning.AutoProvMode;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;

public class ADAutoProvisionEager extends ADAutoProvision {

	ADAutoProvisionEager (ADProvisioning prov) {
		super(prov);
	}
	
	private void autoProvisionDomainADUsers(ADTree adTree, Domain domain) throws Exception {
		// pega o repositório de usuários
		ADUsersRepository rep = adTree.getUsersRepository();

		// busca os usuários com e-mail
		List<ADUser> users = rep.queryUsers(true);
		ZimbraLog.autoprov.debug("AD - Provisioning %d accounts from domain %s",
				users.size(), domain.getName());

		// para cada usuário
		for (ADUser user : users) {
			// cria ou atualiza a conta no zimbra
			ZimbraLog.autoprov.info("AD - auto provisioning account \"%s\"", user.getDistinguishedName());
			this.autoProvisionAccount(domain, user);
		}
	}

	private void autoProvisionDomainADDistributionLists(ADTree adTree, Domain domain) throws Exception {
		// pega o repositório de grupos
		ADGroupsRepository rep = adTree.getGroupsRepository();
		
		// busca as listas de distribuição do AD
		List<ADGroup> distribuitionLists = rep.queryDistributionLists(true);
		ZimbraLog.autoprov.debug("AD - Provisioning %d distribution lists from domain %s",
				distribuitionLists.size(), domain.getName());
		
		// para cada lista
		for (ADGroup distribuitionList : distribuitionLists) {
			// cria ou atualiza a lista de distribuição
			ZimbraLog.autoprov.info("AD - auto provisioning distribution list \"%s\"",
					distribuitionList.getDistinguishedName());
			this.autoProvisionDistributionList(domain, distribuitionList);
		}
	}

	private void autoProvisionDomainADEntries(Domain domain) throws Exception {		
		// se conecta com o servidor AD
		ZimbraLog.autoprov.debug("AD - Connecting to domain %s", domain.getName());

		ADTree adTree = this.openDomainADConnection(domain);
		if (adTree == null) {
			ZimbraLog.autoprov.warn("AD - Can't connect to domain %s. Accounts from it will not be provisioned.",
					domain.getName());
			return;
		}

		// faz a leitura das contas do servidor AD
		this.autoProvisionDomainADUsers(adTree, domain);

		// faz a leitura das listas de distribuição do AD
		this.autoProvisionDomainADDistributionLists(adTree, domain);

		ZimbraLog.autoprov.debug("AD - Disconnecting of domain %s", domain.getName());

		// desconecta do servidor AD
		adTree.disconnect();
	}

	private static boolean autoProvEagerEnabledForDomain(Domain domain) {
		// a função getAutoProvModeAsString não funciona
		return domain.getMultiAttrSet(Provisioning.A_zimbraAutoProvMode)
				.contains(AutoProvMode.EAGER.name());
	}
	
	public synchronized void autoProvisionAccountsAndGroups() throws ServiceException {
		/* 
		 * Verifica quais domínios estão habilitados para provisionamento
		 * automático no servidor.
		 */
		Server server = this.prov.getLocalServer();
		String[] scheduledDomains = server.getAutoProvScheduledDomains();

		for (String domainName : scheduledDomains) {
			// busca o domínio
			Domain domain = this.prov.get(DomainBy.name, domainName);
			if (domain == null) {
				// informa que o domínio não existe e ignora o restante do
				// processamento do mesmo.
				ZimbraLog.autoprov.warn("AD - EAGER auto provision: no such domain %s", domainName);
				continue;
			}

			// verifica se o domínio está habilitado para a provisão automática
			// no modo EAGER
			if (!autoProvEagerEnabledForDomain(domain)) {
				/*
				 * Copiado do código fonte do Zimbra:
				 * - função AutoProvisionEager#handleScheduledDomains
				 */

				/*
				 * remove it from the scheduled domains on the local server
				 */

				ZimbraLog.autoprov.info("AD - Domain %s is scheduled for EAGER auto provision " +
						"but EAGER mode is not enabled on the domain.  " +
						"Removing domain %s from %s on server %s", 
						domain.getName(), domain.getName(), 
						Provisioning.A_zimbraAutoProvScheduledDomains, server.getName());

				// will trigger callback for AutoProvScheduledDomains.  If scheduled 
				// domains become empty, the EAGER auto prov thread will be requested 
				// to shutdown.
				server.removeAutoProvScheduledDomains(domain.getName());
				continue;
			}

			ZimbraLog.autoprov.info("AD - Auto provisioning accounts and distribution lists on domain %s", domainName);

			try {
				// fazendo a provisão do domínio
				this.autoProvisionDomainADEntries(domain);
			} catch (Exception ex) {
				// informa o erro que ocorreu durante a provisão do domínio
				ZimbraLog.autoprov.error("AD - Error while provisioning domain %s. Error: %s",
						domainName, ex.getMessage());
			}
			
			// atualiza o horário da última checagem do domínio
			domain.setAutoProvLastPolledTimestamp(new Date(System.currentTimeMillis()));
		}
	}

}
