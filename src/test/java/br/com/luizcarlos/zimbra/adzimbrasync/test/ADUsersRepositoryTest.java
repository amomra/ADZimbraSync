package br.com.luizcarlos.zimbra.adzimbrasync.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import br.com.luizcarlos.zimbra.adzimbrasync.ad.ADGroup;
import br.com.luizcarlos.zimbra.adzimbrasync.ad.ADGroupsRepository;
import br.com.luizcarlos.zimbra.adzimbrasync.ad.ADTree;
import br.com.luizcarlos.zimbra.adzimbrasync.ad.ADUser;
import br.com.luizcarlos.zimbra.adzimbrasync.ad.ADUsersRepository;

public class ADUsersRepositoryTest {

	private TestProperties prop;
	
	private ADTree adTree;

	public ADUsersRepositoryTest() throws Exception {
		// carrega as propriedades do teste
		this.prop = new TestProperties();
		
		// cria o objeto da conexão
		this.adTree = new ADTree(
				this.prop.getLDAPHostname(), 
				this.prop.getLDAPPort(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conexão
		this.adTree.connect();
	}

	@Test
	public void testQueryUsers() throws Exception {
		
		System.out.println("- queryUsers -----------------------");

		// pega o repositório de usuários
		ADUsersRepository usersRep = this.adTree.getUsersRepository();

		// busca todos os usuários que possuem um e-mail
		List<ADUser> users = usersRep.queryUsers("(mail=*)");
		// verifica se todos os usuários possuem um e-mail
		for (ADUser user : users) {
			System.out.format("Nome: %s\n", user.getName());
			// se tiver um que não possui
			if (user.getMail() == null)
				fail(String.format("O usuário \"%s\" não possui e-mail", user.getDistinguishedName()));
		}

		System.out.println("- queryUsers - 2 -------------------");

		// agora busca os usuários que possuem um e-mail ou seja o usuário administrador
		users = usersRep.queryUsers("(|(mail=*)(name=Administrator))");
		// verifica se todos os usuários possuem um e-mail
		for (ADUser user : users) {
			System.out.format("Nome: %s\n", user.getName());
			// se tiver um que não possui e não forem o usuário administrator
			if (user.getMail() == null && !user.getName().equals("Administrator"))
				fail(String.format("O usuário \"%s\" não possui e-mail e não é o Administrator",
						user.getDistinguishedName()));
		}
	}

	@Test
	public void testQueryUsersByName() throws Exception {
		
		System.out.println("- queryUsersByName -----------------");

		// pega o repositório de usuários
		ADUsersRepository usersRep = this.adTree.getUsersRepository();

		// busca todos os usuários que possuem a palavra "Usuário" no nome
		List<ADUser> users = usersRep.queryUsersByName("*Usuário*", false);
		// verifica se todos os usuário
		for (ADUser user : users) {
			System.out.format("Nome: %s | E-mail: %s\n", user.getName(), user.getMail());
			
			// se não tiver o "Usuário" no nome
			if (!user.getName().contains("Usuário"))
				fail(String.format("O usuário \"%s\" não possui a palavra \"Usuário\" no nome", user.getDistinguishedName()));
		}
	}

	@Test
	public void testQueryGroupMembers() throws Exception {
		
		System.out.println("- queryGroupMembers ----------------");

		// pega o repositório de usuários
		ADUsersRepository usersRep = this.adTree.getUsersRepository();
		
		// pega o repositório de grupos para retornar o grupo de administradores
		ADGroupsRepository groupsRep = adTree.getGroupsRepository();
		ADGroup adminGroup = groupsRep.getAdministratorsGroup();
		
		// pega a lista de usuários deste grupo
		List<ADUser> users = usersRep.queryGroupMembers(adminGroup, false);
		// verifica se todos os usuários são membros do grupo
		for (ADUser user : users) {
			System.out.format("Nome: %s\n", user.getName());
			
			if (!adminGroup.isMember(user))
				fail(String.format("Usuário \"%s\" não é membro do grupo \"%s\"", user.getName(), adminGroup.getName()));
		}
	}
}
