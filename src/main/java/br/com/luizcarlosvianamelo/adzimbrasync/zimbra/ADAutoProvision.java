package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import java.util.List;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADTree;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUsersRepository;

import com.zimbra.common.account.Key.DomainBy;
import com.zimbra.common.account.ZAttrProvisioning.AutoProvMode;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;

public class ADAutoProvision {

	private ADProvisioning prov;
	
	ADAutoProvision (ADProvisioning prov) {
		this.prov = prov;
	}
	
	private ADTree openDomainADConnection(Domain domain) throws Exception {
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
			
			// TODO Fazer código para adicionar o timestamp da última sincronização do usuário.
		}
	}
	
	private void autoProvisionDomainADDistributionLists(ADTree adTree, Domain domain) {
		
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
	
	public synchronized Account autoProvisionAccount(Domain domain, ADUser user) throws ServiceException {
		// TODO Fazer a conversão dos atributos do AD para os attributos do Zimbra
		/*
		 * Verifica se a conta já está cadastrada no Zimbra. Caso não estiver,
		 * cria ela.
		 */
		Account acct = this.prov.getAccountByName(user.getsAMAccountName());
		if (acct == null)
			// significa que ela não foi encontrada. Logo, cria ela no Zimbra
			acct = this.prov.createAccount(user.getMail(), "", null);
		
		
		return acct;
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
		}
	}
}
