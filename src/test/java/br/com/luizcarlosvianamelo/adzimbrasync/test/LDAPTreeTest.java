package br.com.luizcarlosvianamelo.adzimbrasync.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.UUID;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchResult;

import org.junit.Test;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPTree;

public class LDAPTreeTest {

	private TestProperties prop;

	public LDAPTreeTest() throws IOException {
		// carrega as propriedades do teste
		this.prop = new TestProperties();
	}

	@Test
	public void testConnect() throws NamingException {
		// cria o objeto da conexão
		LDAPTree ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conexão
		ldapTree.connect();

		// testa se está conectado
		assertTrue("Não se conectou com o servidor.", ldapTree.isConnected());

		// desconecta
		ldapTree.disconnect();
	}

	@Test
	public void testConnectSsl() throws Exception {
		// cria o objeto da conexão
		LDAPTree ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// ajusta a pasta onde estão incluídos os certificados do servidor
		ldapTree.setSSLCertificatesPath(
				this.prop.getLDAPCertificatePath(),
				this.prop.getLDAPCertificateFilePassword());

		// tenta fazer uma conexão segura
		ldapTree.connect(true);

		// verifica se está conectado
		assertTrue("Não se conectou com o servidor através de uma conexão segura.", ldapTree.isConnected());

		// desconecta
		ldapTree.disconnect();
	}

	@Test
	public void testDisconnect() throws NamingException {
		// cria o objeto da conexão
		LDAPTree ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conexão
		ldapTree.connect();

		// desconecta
		ldapTree.disconnect();

		// testa se está conectado
		assertTrue("Ainda está conectado com o servidor.", !ldapTree.isConnected());
	}

	@Test
	public void testSearch() throws Exception {
		// cria o objeto da conexão
		LDAPTree ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conexão
		ldapTree.connect();

		// faz a consulta do objectGUID dos usuários
		NamingEnumeration<SearchResult> result = ldapTree.search("(objectCategory=Person)", "objectGUID", "whenCreated");

		while (result.hasMoreElements()) {

			SearchResult entry = result.nextElement();

			Attributes attrs = entry.getAttributes();

			Attribute attr = attrs.get("objectGUID");
			System.out.println("objectGUID: " + UUID.nameUUIDFromBytes(attr.get().toString().getBytes()).toString());

			attr = attrs.get("whenCreated");
			System.out.println("whenCreated: " + attr.get().toString());
		}

		// desconecta
		ldapTree.disconnect();
	}

	@Test
	public void testModify() throws Exception {
		// cria o objeto da conexão
		LDAPTree ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conexão
		ldapTree.connect();
		
		// faz a consulta do sobrenome do usuário com o username "usuario"
		NamingEnumeration<SearchResult> result = ldapTree.search("(sAMAccountName=usuario)", "distinguishedName", "sn");
		
		// informa erro caso não encontre o usuário
		assertTrue("Usuário \"usuario\" não foi encontrado. Crie ele para este teste", result.hasMoreElements());
		
		SearchResult entry = result.nextElement();
		Attribute attr = entry.getAttributes().get("sn");
		
		// verifica se o atributo foi encontrado
		assertNotNull("Usuário \"usuario\" não possui o atributo \"sn\" ajustado. Ajuste ele para o teste", attr);
		
		String sn = (String) attr.get();
		String snNew = sn + " teste";
		
		// pega o DN do usuário
		attr = entry.getAttributes().get("distinguishedName");
		assertNotNull("Usuário \"usuario\" não possui o atributo \"distinguishedName\" ajustado. Tem alguma coisa muito errada o.O", attr);
		String dn = (String) attr.get();
		
		// ajusta o novo valor do sn
		ModificationItem [] itens = new ModificationItem[1];
		itens[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", snNew));
		ldapTree.modify(dn, itens);
		
		// faz a consulta novamente do valor do atributo
		result = ldapTree.search("(sAMAccountName=usuario)", "sn");
		assertTrue("Usuário \"usuario\" não foi encontrado mesmo depois de ter sido alterado um atributo dele neste instante o.O", result.hasMoreElements());
		entry = result.nextElement();
		attr = entry.getAttributes().get("sn");
		assertNotNull("Usuário \"usuario\" não possui o atributo \"sn\" ajustado sendo que este acabou de ser modificado", attr);
		
		String receivedSn = (String) attr.get();
		
		// verifica se o atributo foi modificado
		assertEquals(
				String.format("O valor de \"sn\" do usuário não foi modificado. Valor lido: %s", receivedSn),
				receivedSn,
				snNew);
		
		// ajusta o valor anterior
		itens[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", sn));
		ldapTree.modify(dn, itens);
			
		
		// desconecta
		ldapTree.disconnect();
	}
}
