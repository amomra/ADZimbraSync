package br.com.luizcarlos.zimbra.adzimbrasync.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.UUID;

import org.junit.Test;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPTree;

public class LDAPTreeTest {
	
	private TestProperties prop;
	
	public LDAPTreeTest() throws IOException {
		// carrega as propriedades do teste
		this.prop = new TestProperties();
	}

	@Test
	public void testConnect() throws LDAPException {
		// cria o objeto da conex�o
				LDAPTree ldapTree = new LDAPTree(
						this.prop.getLDAPHostname(), 
						this.prop.getLDAPPort(),
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
	public void testDisconnect() throws LDAPException {
		// cria o objeto da conex�o
				LDAPTree ldapTree = new LDAPTree(
						this.prop.getLDAPHostname(), 
						this.prop.getLDAPPort(),
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
						this.prop.getLDAPHostname(), 
						this.prop.getLDAPPort(),
						this.prop.getLDAPSearchBase(),
						this.prop.getLDAPSearchBindDn(),
						this.prop.getLDAPSearchBindPassword());

		// realiza a conex�o
		ldapTree.connect();
		
		// faz a consulta do objectGUID dos usu�rios
		SearchResult result = ldapTree.search("(objectCategory=Person)", "objectGUID", "whenCreated");
		for (SearchResultEntry entry : result.getSearchEntries()) {
			
			Attribute attr = entry.getAttribute("objectGUID");
			System.out.println("objectGUID: " + UUID.nameUUIDFromBytes(attr.getValueByteArray()).toString());
			
			attr = entry.getAttribute("whenCreated");
			System.out.println("whenCreated: " + attr.getValue());
			
			
		}

		// desconecta
		ldapTree.disconnect();
	}

}
