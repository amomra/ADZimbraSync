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

		// cria o objeto da conex�o
		this.adTree = new ADTree(
				this.prop.getLDAPUrl(), 
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conex�o
		this.adTree.connect();

		// pega o reposit�rio de usu�rios
		this.usersRep = this.adTree.getUsersRepository();
	}

	@Test
	public void testQueryUsers() throws Exception {

		System.out.println("- queryUsers -----------------------");

		// busca todos os usu�rios que possuem um e-mail
		List<ADUser> users = this.usersRep.queryUsers("(mail=*)");
		// verifica se todos os usu�rios possuem um e-mail
		for (ADUser user : users) {
			System.out.format("Nome: %s\n", user.getName());
			// se tiver um que n�o possui
			if (user.getMail() == null)
				fail(String.format("O usu�rio \"%s\" n�o possui e-mail", user.getDistinguishedName()));
		}

		System.out.println("- queryUsers - 2 -------------------");

		// agora busca os usu�rios que possuem um e-mail ou seja o usu�rio administrador
		users = this.usersRep.queryUsers("(|(mail=*)(name=Administrator))");
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

		// busca todos os usu�rios que possuem a palavra "Usu�rio" no nome
		List<ADUser> users = this.usersRep.queryUsersByName("*Usu�rio*", false);
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

		// pega o reposit�rio de grupos para retornar o grupo de administradores
		ADGroupsRepository groupsRep = this.adTree.getGroupsRepository();
		ADGroup adminGroup = groupsRep.getAdministratorsGroup();

		// pega a lista de usu�rios deste grupo
		List<ADUser> users = this.usersRep.queryGroupMembers(adminGroup, false);
		// verifica se todos os usu�rios s�o membros do grupo
		for (ADUser user : users) {
			System.out.format("Nome: %s\n", user.getName());

			if (!adminGroup.isMember(user))
				fail(String.format("Usu�rio \"%s\" n�o � membro do grupo \"%s\"", user.getName(), adminGroup.getName()));
		}
	}

	@Test
	public void testChangeUserPassword() throws Exception {
		/*
		 * Neste caso n�o ser� utilizada a mesma conex�o dos outros testes j�
		 * que para a mudan�a de senha � necess�rio uma conex�o segura.
		 */
		// cria o objeto da conex�o
		ADTree adTree = new ADTree(
				this.prop.getLDAPUrl(), 
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// ajusta a pasta onde est�o inclu�dos os certificados do servidor
		adTree.setSSLCertificatesPath(
				this.prop.getLDAPCertificatePath(),
				this.prop.getLDAPCertificateFilePassword());

		// realiza a conex�o segura
		adTree.connect(true);

		// pega o reposit�rio de usu�rios
		ADUsersRepository usersRep = adTree.getUsersRepository();

		// pega o usu�rio configurado
		ADUser user = usersRep.queryUserByAccountName(this.prop.getADTestUserAccountName());
		assertNotNull("O usu�rio configurado n�o existe", user);

		// modifica a senha do usu�rio para "teste123" (o AD tem restri��o de tamanho m�nimo de senha)
		usersRep.changeUserPassword(user, "teste123");

		// desconecta
		adTree.disconnect();

		// e tenta reconectar com o usu�rio modificado e sua nova senha
		adTree.setLdapSearchBindDn(user.getDistinguishedName());
		adTree.setLdapSearchBindPassword("teste123");
		adTree.connect();

		// verifica se est� conectado
		assertTrue("N�o conseguiu se conectar com o usu�rio e a sua nova senha", adTree.isConnected());

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
