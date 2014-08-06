package br.com.luizcarlosvianamelo.adzimbrasync.test;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADGroup;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADGroupsRepository;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADTree;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUsersRepository;

public class ADUsersRepositoryTest {

	private TestProperties prop;

	private ADTree adTree;

	private ADUsersRepository usersRep;

	public ADUsersRepositoryTest() throws Exception {
		// carrega as propriedades do teste
		this.prop = new TestProperties();

		// cria o objeto da conexão
		this.adTree = new ADTree(
				this.prop.getLDAPUrl(), 
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conexão
		this.adTree.connect();

		// pega o repositório de usuários
		this.usersRep = this.adTree.getUsersRepository();
	}

	@Test
	public void testQueryUsers() throws Exception {

		System.out.println("- queryUsers -----------------------");

		// busca todos os usuários que possuem um e-mail
		List<ADUser> users = this.usersRep.queryUsers("(mail=*)");
		// verifica se todos os usuários possuem um e-mail
		for (ADUser user : users) {
			System.out.format("Nome: %s\n", user.getName());
			// se tiver um que não possui
			if (user.getMail() == null)
				fail(String.format("O usuário \"%s\" não possui e-mail", user.getDistinguishedName()));
		}

		System.out.println("- queryUsers - 2 -------------------");

		// agora busca os usuários que possuem um e-mail ou seja o usuário administrador
		users = this.usersRep.queryUsers("(|(mail=*)(name=Administrator))");
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

		// busca todos os usuários que possuem a palavra "Usuário" no nome
		List<ADUser> users = this.usersRep.queryUsersByName("*Usuário*", false);
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

		// pega o repositório de grupos para retornar o grupo de administradores
		ADGroupsRepository groupsRep = this.adTree.getGroupsRepository();
		ADGroup adminGroup = groupsRep.getAdministratorsGroup();

		// pega a lista de usuários deste grupo
		List<ADUser> users = this.usersRep.queryGroupMembers(adminGroup, false);
		// verifica se todos os usuários são membros do grupo
		for (ADUser user : users) {
			System.out.format("Nome: %s\n", user.getName());

			if (!adminGroup.isMember(user))
				fail(String.format("Usuário \"%s\" não é membro do grupo \"%s\"", user.getName(), adminGroup.getName()));
		}
	}

	@Test
	public void testChangeUserPassword() throws Exception {
		/*
		 * Neste caso não será utilizada a mesma conexão dos outros testes já
		 * que para a mudança de senha é necessário uma conexão segura.
		 */
		// cria o objeto da conexão
		ADTree adTree = new ADTree(
				this.prop.getLDAPUrl(), 
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// ajusta a pasta onde estão incluídos os certificados do servidor
		adTree.setSSLCertificatesPath(
				this.prop.getLDAPCertificatePath(),
				this.prop.getLDAPCertificateFilePassword());

		// realiza a conexão segura
		adTree.connect(true);

		// pega o repositório de usuários
		ADUsersRepository usersRep = adTree.getUsersRepository();

		// pega o usuário configurado
		ADUser user = usersRep.queryUserByAccountName(this.prop.getADTestUserAccountName());
		assertNotNull("O usuário configurado não existe", user);

		// modifica a senha do usuário para "teste123" (o AD tem restrição de tamanho mínimo de senha)
		usersRep.changeUserPassword(user, "teste123");

		// desconecta
		adTree.disconnect();

		// e tenta reconectar com o usuário modificado e sua nova senha
		adTree.setLdapSearchBindDn(user.getDistinguishedName());
		adTree.setLdapSearchBindPassword("teste123");
		adTree.connect();

		// verifica se está conectado
		assertTrue("Não conseguiu se conectar com o usuário e a sua nova senha", adTree.isConnected());

		// desconecta
		adTree.disconnect();

		// retorna a senha para o valor anterior
		adTree.setLdapSearchBindDn(this.prop.getLDAPSearchBindDn());
		adTree.setLdapSearchBindPassword(this.prop.getLDAPSearchBindPassword());
		adTree.connect(true);
		usersRep.changeUserPassword(user, this.prop.getADTestUserPassword());

		// desconecta
		adTree.disconnect();
	}
}
