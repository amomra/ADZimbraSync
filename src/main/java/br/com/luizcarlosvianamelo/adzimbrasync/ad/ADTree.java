package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPTree;

/**
 * Classe que representa uma �rvore do AD. Esta � utilizada para a consulta
 * dos grupos e usu�rios que est�o contidos na �rvore atrav�s dos reposit�rios
 * de ambos. Por ser essencialmente uma �rvore LDAP, esta herda caracter�sticas
 * da classe {@link LDAPTree}.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADTree extends LDAPTree {
	
	private ADUsersRepository usersRepository;
	
	private ADGroupsRepository groupsRepository;
	
	/**
	 * Construtor padr�o da classe. Este inicializa os par�metros de conex�o
	 * com valores padr�es.
	 */
	public ADTree() {
		this("", 389, "", "", "");
	}
	
	/**
	 * Construtor parametrizado da classe.
	 * @param hostname O endere�o do servidor AD.
	 * @param port A porta do servidor AD.
	 * @param ldapSearchBase A base de busca na �rvore do LDAP.
	 * @param ldapSearchBindDn O DN do usu�rio que pode realizar consultas sobre
	 * a �rvore.
	 * @param ldapSearchBindPassword A senha do usu�rio de consulta.
	 */
	public ADTree(String hostname,
			int port,
			String ldapSearchBase,
			String ldapSearchBindDn,
			String ldapSearchBindPassword) {
		super(hostname, port, ldapSearchBase, ldapSearchBindDn, ldapSearchBindPassword);
		
		// inicializa os reposit�rios
		this.usersRepository = new ADUsersRepository(this);
		this.groupsRepository = new ADGroupsRepository(this);
	}

	/**
	 * Retorna o objeto que representa o reposit�rio de usu�rios da �rvore do
	 * AD.
	 * @see ADUsersRepository
	 */
	public ADUsersRepository getUsersRepository() {
		return usersRepository;
	}

	/**
	 * Retorna o objeto que representa o reposit�rio de grupos da �rvore do
	 * AD.
	 * @see ADGroupsRepository
	 */
	public ADGroupsRepository getGroupsRepository() {
		return groupsRepository;
	}
}
