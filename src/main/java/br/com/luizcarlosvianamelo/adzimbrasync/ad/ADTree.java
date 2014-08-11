package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPTree;

/**
 * Classe que representa uma árvore do AD. Esta é utilizada para a consulta
 * dos grupos e usuários que estão contidos na árvore através dos repositórios
 * de ambos. Por ser essencialmente uma árvore LDAP, esta herda características
 * da classe {@link LDAPTree}.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADTree extends LDAPTree {
	
	private ADUsersRepository usersRepository;
	
	private ADGroupsRepository groupsRepository;
	
	/**
	 * Construtor padrão da classe. Este inicializa os parâmetros de conexão
	 * com valores padrões.
	 */
	public ADTree() {
		this("",  "", "", "");
	}
	
	/**
	 * Construtor parametrizado da classe.
	 * @param ldapUrl O endereço do servidor AD.
	 * @param ldapSearchBase A base de busca na árvore do LDAP.
	 * @param ldapSearchBindDn O DN do usuário que pode realizar consultas sobre
	 * a árvore.
	 * @param ldapSearchBindPassword A senha do usuário de consulta.
	 */
	public ADTree(String ldapUrl,
			String ldapSearchBase,
			String ldapSearchBindDn,
			String ldapSearchBindPassword) {
		super(ldapUrl, ldapSearchBase, ldapSearchBindDn, ldapSearchBindPassword);
		
		// inicializa os repositórios
		this.usersRepository = new ADUsersRepository(this);
		this.groupsRepository = new ADGroupsRepository(this);
	}

	/**
	 * Retorna o objeto que representa o repositório de usuários da árvore do
	 * AD.
	 * @see ADUsersRepository
	 */
	public ADUsersRepository getUsersRepository() {
		return usersRepository;
	}

	/**
	 * Retorna o objeto que representa o repositório de grupos da árvore do
	 * AD.
	 * @see ADGroupsRepository
	 */
	public ADGroupsRepository getGroupsRepository() {
		return groupsRepository;
	}
}
