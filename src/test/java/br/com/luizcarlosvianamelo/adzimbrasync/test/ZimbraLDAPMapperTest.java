package br.com.luizcarlosvianamelo.adzimbrasync.test;

import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchResult;

import org.junit.Test;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADUser;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.AttributeAccessMode;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPEntry;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPTree;
import br.com.luizcarlosvianamelo.adzimbrasync.zimbra.ZimbraLDAPMapper;

public class ZimbraLDAPMapperTest {

	private TestProperties prop;

	private LDAPTree ldapTree;

	public ZimbraLDAPMapperTest() throws Exception {
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
	public void testMapFieldsIntoAttributes() throws Exception {
		System.out.println(" testMapFieldsIntoAttributes -----------");
		// faz a busca dos usuários
		NamingEnumeration<SearchResult> result = this.ldapTree.search("(&(objectCategory=Person)(userPrincipalName=*))");
		while (result.hasMoreElements()) {
			SearchResult entry = result.nextElement();

			ADUser user = LDAPEntry.parseEntry(ADUser.class, entry.getAttributes());
			// esse atributo sempre será ajustado
			assertNotNull("O objeto do usuário não está sendo preenchido adequadamente", user.getDistinguishedName());

			// faz o mapeamento do distinguishedName para dn
			Map<String, String> mapping = new Hashtable<>();
			mapping.put("distinguishedName", "dn");

			Map<String, Object> mapped = ZimbraLDAPMapper.mapObjectFieldsIntoAttributes(user, AttributeAccessMode.READ, mapping);

			// verifica se tem elementos
			assertTrue("A quantidade de atributos mapeados está incorreto", mapped.size() == 1);

			String dn = (String) mapped.get("dn").toString();

			// verifica se o atributo foi mapeado corretamente
			assertNotNull("O mapeamento não foi feito corretamente já que o campo \"dn\" está nulo", dn);
		}
	}
}
