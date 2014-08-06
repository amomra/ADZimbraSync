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
		// cria o objeto da conex�o
		LDAPTree ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conex�o
		ldapTree.connect();

		// testa se est� conectado
		assertTrue("N�o se conectou com o servidor.", ldapTree.isConnected());

		// desconecta
		ldapTree.disconnect();
	}

	@Test
	public void testConnectSsl() throws Exception {
		// cria o objeto da conex�o
		LDAPTree ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// ajusta a pasta onde est�o inclu�dos os certificados do servidor
		ldapTree.setSSLCertificatesPath(
				this.prop.getLDAPCertificatePath(),
				this.prop.getLDAPCertificateFilePassword());

		// tenta fazer uma conex�o segura
		ldapTree.connect(true);

		// verifica se est� conectado
		assertTrue("N�o se conectou com o servidor atrav�s de uma conex�o segura.", ldapTree.isConnected());

		// desconecta
		ldapTree.disconnect();
	}

	@Test
	public void testDisconnect() throws NamingException {
		// cria o objeto da conex�o
		LDAPTree ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conex�o
		ldapTree.connect();

		// desconecta
		ldapTree.disconnect();

		// testa se est� conectado
		assertTrue("Ainda est� conectado com o servidor.", !ldapTree.isConnected());
	}

	@Test
	public void testSearch() throws Exception {
		// cria o objeto da conex�o
		LDAPTree ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conex�o
		ldapTree.connect();

		// faz a consulta do objectGUID dos usu�rios
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
		// cria o objeto da conex�o
		LDAPTree ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conex�o
		ldapTree.connect();
		
		// faz a consulta do sobrenome do usu�rio com o username "usuario"
		NamingEnumeration<SearchResult> result = ldapTree.search("(sAMAccountName=usuario)", "distinguishedName", "sn");
		
		// informa erro caso n�o encontre o usu�rio
		assertTrue("Usu�rio \"usuario\" n�o foi encontrado. Crie ele para este teste", result.hasMoreElements());
		
		SearchResult entry = result.nextElement();
		Attribute attr = entry.getAttributes().get("sn");
		
		// verifica se o atributo foi encontrado
		assertNotNull("Usu�rio \"usuario\" n�o possui o atributo \"sn\" ajustado. Ajuste ele para o teste", attr);
		
		String sn = (String) attr.get();
		String snNew = sn + " teste";
		
		// pega o DN do usu�rio
		attr = entry.getAttributes().get("distinguishedName");
		assertNotNull("Usu�rio \"usuario\" n�o possui o atributo \"distinguishedName\" ajustado. Tem alguma coisa muito errada o.O", attr);
		String dn = (String) attr.get();
		
		// ajusta o novo valor do sn
		ModificationItem [] itens = new ModificationItem[1];
		itens[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", snNew));
		ldapTree.modify(dn, itens);
		
		// faz a consulta novamente do valor do atributo
		result = ldapTree.search("(sAMAccountName=usuario)", "sn");
		assertTrue("Usu�rio \"usuario\" n�o foi encontrado mesmo depois de ter sido alterado um atributo dele neste instante o.O", result.hasMoreElements());
		entry = result.nextElement();
		attr = entry.getAttributes().get("sn");
		assertNotNull("Usu�rio \"usuario\" n�o possui o atributo \"sn\" ajustado sendo que este acabou de ser modificado", attr);
		
		String receivedSn = (String) attr.get();
		
		// verifica se o atributo foi modificado
		assertEquals(
				String.format("O valor de \"sn\" do usu�rio n�o foi modificado. Valor lido: %s", receivedSn),
				receivedSn,
				snNew);
		
		// ajusta o valor anterior
		itens[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute("sn", sn));
		ldapTree.modify(dn, itens);
			
		
		// desconecta
		ldapTree.disconnect();
	}
}
