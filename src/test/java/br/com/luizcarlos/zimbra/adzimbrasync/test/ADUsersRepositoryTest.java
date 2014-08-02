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
	public void testQueryUsers() throws Exception {
		
		System.out.println("- queryUsers -----------------------");

		// pega o reposit�rio de usu�rios
		ADUsersRepository usersRep = this.adTree.getUsersRepository();

		// busca todos os usu�rios que possuem um e-mail
		List<ADUser> users = usersRep.queryUsers("(mail=*)");
		// verifica se todos os usu�rios possuem um e-mail
		for (ADUser user : users) {
			System.out.format("Nome: %s\n", user.getName());
			// se tiver um que n�o possui
			if (user.getMail() == null)
				fail(String.format("O usu�rio \"%s\" n�o possui e-mail", user.getDistinguishedName()));
		}

		System.out.println("- queryUsers - 2 -------------------");

		// agora busca os usu�rios que possuem um e-mail ou seja o usu�rio administrador
		users = usersRep.queryUsers("(|(mail=*)(name=Administrator))");
		// verifica se todos os usu�rios possuem um e-mail
		for (ADUser user : users) {
			System.out.format("Nome: %s\n", user.getName());
			// se tiver um que n�o possui e n�o forem o usu�rio administrator
			if (user.getMail() == null && !user.getName().equals("Administrator"))
				fail(String.format("O usu�rio \"%s\" n�o possui e-mail e n�o � o Administrator",
						user.getDistinguishedName()));
		}
	}

	@Test
	public void testQueryUsersByName() throws Exception {
		
		System.out.println("- queryUsersByName -----------------");

		// pega o reposit�rio de usu�rios
		ADUsersRepository usersRep = this.adTree.getUsersRepository();

		// busca todos os usu�rios que possuem a palavra "Usu�rio" no nome
		List<ADUser> users = usersRep.queryUsersByName("*Usu�rio*", false);
		// verifica se todos os usu�rio
		for (ADUser user : users) {
			System.out.format("Nome: %s | E-mail: %s\n", user.getName(), user.getMail());
			
			// se n�o tiver o "Usu�rio" no nome
			if (!user.getName().contains("Usu�rio"))
				fail(String.format("O usu�rio \"%s\" n�o possui a palavra \"Usu�rio\" no nome", user.getDistinguishedName()));
		}
	}

	@Test
	public void testQueryGroupMembers() throws Exception {
		
		System.out.println("- queryGroupMembers ----------------");

		// pega o reposit�rio de usu�rios
		ADUsersRepository usersRep = this.adTree.getUsersRepository();
		
		// pega o reposit�rio de grupos para retornar o grupo de administradores
		ADGroupsRepository groupsRep = adTree.getGroupsRepository();
		ADGroup adminGroup = groupsRep.getAdministratorsGroup();
		
		// pega a lista de usu�rios deste grupo
		List<ADUser> users = usersRep.queryGroupMembers(adminGroup, false);
		// verifica se todos os usu�rios s�o membros do grupo
		for (ADUser user : users) {
			System.out.format("Nome: %s\n", user.getName());
			
			if (!adminGroup.isMember(user))
				fail(String.format("Usu�rio \"%s\" n�o � membro do grupo \"%s\"", user.getName(), adminGroup.getName()));
		}
	}
}
