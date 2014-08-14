package br.com.luizcarlosvianamelo.adzimbrasync.test;

import static org.junit.Assert.*;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;

import org.junit.Test;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPEntry;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPTree;

public class LDAPEntryTest {

	private TestProperties prop;

	private LDAPTree ldapTree;

	public LDAPEntryTest() throws Exception {
		// carrega as propriedades do teste
		this.prop = new TestProperties();

		// cria o objeto da conexão
		this.ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conexão
		ldapTree.connect();
	}
	
	@Test
	public void testGetLDAPAttributes() {
		//fail("Not yet implemented");
	}

	@Test
	public void testParseEntry() throws Exception {
		System.out.println(" testParseEntry ------------------------");
		// faz a busca dos usuários
		NamingEnumeration<SearchResult> result = this.ldapTree.search("(&(objectCategory=Person)(userPrincipalName=*))");
		while (result.hasMoreElements()) {
			SearchResult entry = result.nextElement();

			ADUser user = LDAPEntry.parseEntry(ADUser.class, entry.getAttributes());
			// esse atributo sempre será ajustado
			assertNotNull("O objeto do usuário não está sendo preenchido adequadamente", user.getDistinguishedName());

			System.out.format("DN: %s | CC: %s\n", user.getDistinguishedName(), user.getCountry());
		}
	}

}
