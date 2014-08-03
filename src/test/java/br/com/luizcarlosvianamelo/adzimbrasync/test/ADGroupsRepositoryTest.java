package br.com.luizcarlosvianamelo.adzimbrasync.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADGroup;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADGroupsRepository;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADTree;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUsersRepository;

public class ADGroupsRepositoryTest {

	private TestProperties prop;

	private ADTree adTree;

	public ADGroupsRepositoryTest() throws Exception {
		// carrega as propriedades do teste
		this.prop = new TestProperties();

		// cria o objeto da conex�o
		this.adTree = new ADTree(
				this.prop.getLDAPHostname(), 
				this.prop.getLDAPPort(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conex�o
		this.adTree.connect();
	}

	@Test
	public void testQueryGroups() throws Exception {

		System.out.println("- queryGroups ----------------------");

		// pega o reposit�rio de grupos
		ADGroupsRepository groupsRep = this.adTree.getGroupsRepository();

		// faz a busca de todos os grupos
		List<ADGroup> groups = groupsRep.queryGroups();
		// verifica se todos os grupos possuem e-mail
		for(ADGroup group : groups) {
			System.out.format("Nome: %s\n", group.getName());
		}

		System.out.println("- queryGroups - 2 ------------------");

		// faz a busca de todos os grupos que possuem e-mail
		groups = groupsRep.queryGroups("(mail=*)");
		// verifica se todos os grupos possuem e-mail
		for(ADGroup group : groups) {
			System.out.format("Nome: %s\n", group.getName());
			if (group.getMail() == null)
				fail(String.format("O grupo \"%s\" n�o possui e-mail", group.getName()));
		}
	}

	@Test
	public void testQueryGroupsByName() throws Exception {
		System.out.println("- queryGroupsByName ----------------");

		// pega o reposit�rio de grupos
		ADGroupsRepository groupsRep = this.adTree.getGroupsRepository();

		// faz a busca de todos os grupos que possuem a palavra "gr" no nome
		List<ADGroup> groups = groupsRep.queryGroupsByName("*gr*", false);
		// verifica se todos os grupos possuem a palavra citada
		for(ADGroup group : groups) {
			System.out.format("Nome: %s\n", group.getName());
			/*
			 * Deve-se ignorar a caixa das letras j� que as consultas do LDAP
			 * n�o s�o case-sensitive.
			 */
			if (!group.getName().toLowerCase().contains("gr"))
				fail(String.format("O grupo \"%s\" n�o possui a palavra \"gr\" no nome", group.getName()));
		}
	}

	@Test
	public void testQueryDistributionLists() throws Exception {
		System.out.println("- queryDistributionLists -----------");

		// pega o reposit�rio de grupos
		ADGroupsRepository groupsRep = this.adTree.getGroupsRepository();

		// faz a busca de todas listas de distribui��o
		List<ADGroup> groups = groupsRep.queryDistributionListsByName("*", false);

		for(ADGroup group : groups) {
			System.out.format("Nome: %s\n", group.getName());
		}
	}

	@Test
	public void testGetAdministratorsGroup() throws Exception {
		// pega o reposit�rio de grupos
		ADGroupsRepository groupsRep = this.adTree.getGroupsRepository();

		// busca o grupo de administradores
		ADGroup adminGroup = groupsRep.getAdministratorsGroup();

		// verifica se o nome do grupo � "Administrators"
		assertEquals(adminGroup.getName(), "Administrators");
	}

	@Test
	public void testQueryEntryGroups() throws Exception {
		System.out.println("- queryEntryGroups -----------------");

		// pega o reposit�rio de grupos
		ADGroupsRepository groupsRep = this.adTree.getGroupsRepository();

		// pega o reposit�rio de usu�rios e busca o usu�rio "Administrator"
		ADUsersRepository usersRep = this.adTree.getUsersRepository();
		ADUser adminUser = usersRep.queryUserByAccountName("Administrator");

		// faz a busca dos grupos do usu�rio
		List<ADGroup> groups = groupsRep.queryEntryGroups(adminUser, false);
		// verifica se o usu�rio � membro de todos os grupos
		for (ADGroup group : groups) {
			System.out.format("Nome: %s\n", group.getName());

			assertTrue(
					String.format("O usu�rio \"%s\" n�o pertence ao grupo \"%s\"", adminUser.getName(), group.getName()),
					group.isMember(adminUser)
					);
		}

	}

}
