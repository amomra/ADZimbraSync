package br.com.luizcarlosvianamelo.adzimbrasync.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.UUID;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
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

}
