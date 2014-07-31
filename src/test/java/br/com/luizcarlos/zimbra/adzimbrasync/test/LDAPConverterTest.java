package br.com.luizcarlos.zimbra.adzimbrasync.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;

import br.com.luizcarlos.zimbra.adzimbrasync.ad.ADUser;
import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPConverter;
import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPTree;

public class LDAPConverterTest {

	@Test
	public void testConvert() throws Exception {
		// cria o objeto da conex�o
		LDAPTree ldapTree = new LDAPTree(
				"192.168.10.132", 
				389,
				"dc=testedom,dc=local",
				"Administrator@TESTEDOM.LOCAL",
				"Test1234Lol");

		// realiza a conex�o
		ldapTree.connect();
		
		// faz a busca dos usu�rios
		SearchResult result = ldapTree.search("(objectCategory=Person)");
		for (SearchResultEntry entry : result.getSearchEntries()) {
			ADUser user = LDAPConverter.convert(ADUser.class, entry);
			// esse atributo sempre ser� ajustado
			if (user.getDistinguishedName() != null)
				System.out.println(user.getDistinguishedName());
			else
				fail("O objeto do usu�rio n�o est� sendo preenchido adequadamente");
		}

		// desconecta
		ldapTree.disconnect();
	}

}
