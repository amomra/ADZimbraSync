package br.com.luizcarlosvianamelo.adzimbrasync.test;

import static org.junit.Assert.*;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;

import org.junit.Test;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPConverter;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPTree;

public class LDAPConverterTest {

	private TestProperties prop;

	private LDAPTree ldapTree;

	public LDAPConverterTest() throws Exception {
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
	public void testConvert() throws Exception {

		// faz a busca dos usuários
		NamingEnumeration<SearchResult> result = this.ldapTree.search("(&(objectCategory=Person)(userPrincipalName=*))");
		while (result.hasMoreElements()) {
			SearchResult entry = result.nextElement();

			ADUser user = LDAPConverter.convert(ADUser.class, entry);
			// esse atributo sempre será ajustado
			if (user.getDistinguishedName() != null)
				System.out.format("DN: %s | CC: %d\n", user.getDistinguishedName(), user.getCountryCode());
			else
				fail("O objeto do usuário não está sendo preenchido adequadamente");
		}
	}

}
