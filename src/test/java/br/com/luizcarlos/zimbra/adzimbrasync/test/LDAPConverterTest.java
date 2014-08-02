package br.com.luizcarlos.zimbra.adzimbrasync.test;

import static org.junit.Assert.*;

import org.junit.Test;

import com.unboundid.ldap.sdk.SearchResult;
import com.unboundid.ldap.sdk.SearchResultEntry;

import br.com.luizcarlos.zimbra.adzimbrasync.ad.ADUser;
import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPConverter;
import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPTree;

public class LDAPConverterTest {

	private TestProperties prop;

	private LDAPTree ldapTree;

	public LDAPConverterTest() throws Exception {
		// carrega as propriedades do teste
		this.prop = new TestProperties();

		// cria o objeto da conex�o
		this.ldapTree = new LDAPTree(
				this.prop.getLDAPHostname(), 
				this.prop.getLDAPPort(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conex�o
		ldapTree.connect();
	}

	@Test
	public void testConvert() throws Exception {

		// faz a busca dos usu�rios
		SearchResult result = this.ldapTree.search("(&(objectCategory=Person)(userPrincipalName=*))");
		for (SearchResultEntry entry : result.getSearchEntries()) {
			ADUser user = LDAPConverter.convert(ADUser.class, entry);
			// esse atributo sempre ser� ajustado
			if (user.getDistinguishedName() != null)
				System.out.println(user.getDistinguishedName());
			else
				fail("O objeto do usu�rio n�o est� sendo preenchido adequadamente");
		}
	}

}
