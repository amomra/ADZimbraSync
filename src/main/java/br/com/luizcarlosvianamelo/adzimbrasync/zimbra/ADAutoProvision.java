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
		// se conectando com o servidor AD do dom�nio
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
		// caso n�o consiga se conectar
		if (!adTree.isConnected())
			return null;
		
		return adTree;
	}

	private void autoProvisionDomainADUsers(ADTree adTree, Domain domain) throws Exception {
		// pega o reposit�rio de usu�rios
		ADUsersRepository rep = adTree.getUsersRepository();
		
		// busca os usu�rios com e-mail
		List<ADUser> users = rep.queryUsers(true);
		ZimbraLog.autoprov.debug("AD - Provisioning %d accounts from domain %s",
				users.size(), domain.getName());
		
		// para cada usu�rio
		for (ADUser user : users) {
			// cria ou atualiza a conta no zimbra
			ZimbraLog.autoprov.info("AD - auto provisioning account \"%s\"", user.getDistinguishedName());
			this.autoProvisionAccount(domain, user);
			
			// TODO Fazer c�digo para adicionar o timestamp da �ltima sincroniza��o do usu�rio.
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
		
		// faz a leitura das listas de distribui��o do AD
		this.autoProvisionDomainADDistributionLists(adTree, domain);
		
		ZimbraLog.autoprov.debug("AD - Disconnecting of domain %s", domain.getName());
		
		// desconecta do servidor AD
		adTree.disconnect();
	}
	
	private static boolean autoProvEagerEnabledForDomain(Domain domain) {
		// a fun��o getAutoProvModeAsString n�o funciona
		return domain.getMultiAttrSet(Provisioning.A_zimbraAutoProvMode)
				.contains(AutoProvMode.EAGER.name());
	}
	
	public synchronized Account autoProvisionAccount(Domain domain, ADUser user) throws ServiceException {
		// TODO Fazer a convers�o dos atributos do AD para os attributos do Zimbra
		/*
		 * Verifica se a conta j� est� cadastrada no Zimbra. Caso n�o estiver,
		 * cria ela.
		 */
		Account acct = this.prov.getAccountByName(user.getsAMAccountName());
		if (acct == null)
			// significa que ela n�o foi encontrada. Logo, cria ela no Zimbra
			acct = this.prov.createAccount(user.getMail(), "", null);
		
		
		return acct;
	}
	
	public synchronized void autoProvisionAccountsAndGroups() throws ServiceException {
		/* 
		 * Verifica quais dom�nios est�o habilitados para provisionamento
		 * autom�tico no servidor.
		 */
		Server server = this.prov.getLocalServer();
		String[] scheduledDomains = server.getAutoProvScheduledDomains();
		
		for (String domainName : scheduledDomains) {
			// busca o dom�nio
			Domain domain = this.prov.get(DomainBy.name, domainName);
			if (domain == null) {
				// informa que o dom�nio n�o existe e ignora o restante do
				// processamento do mesmo.
                ZimbraLog.autoprov.warn("AD - EAGER auto provision: no such domain %s", domainName);
                continue;
            }
			
			// verifica se o dom�nio est� habilitado para a provis�o autom�tica
			// no modo EAGER
			if (!autoProvEagerEnabledForDomain(domain)) {
				/*
				 * Copiado do c�digo fonte do Zimbra:
				 * - fun��o AutoProvisionEager#handleScheduledDomains
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
				// fazendo a provis�o do dom�nio
				this.autoProvisionDomainADEntries(domain);
			} catch (Exception ex) {
				// informa o erro que ocorreu durante a provis�o do dom�nio
				ZimbraLog.autoprov.error("AD - Error while provisioning domain %s. Error: %s",
						domainName, ex.getMessage());
			}
		}
	}
}
