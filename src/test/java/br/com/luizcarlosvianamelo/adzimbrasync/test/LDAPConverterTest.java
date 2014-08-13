package br.com.luizcarlosvianamelo.adzimbrasync.test;

import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
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
		System.out.println(" testConvert ---------------------------");
		// faz a busca dos usuários
		NamingEnumeration<SearchResult> result = this.ldapTree.search("(&(objectCategory=Person)(userPrincipalName=*))");
		while (result.hasMoreElements()) {
			SearchResult entry = result.nextElement();

			ADUser user = LDAPConverter.convert(ADUser.class, entry);
			// esse atributo sempre será ajustado
			assertNotNull("O objeto do usuário não está sendo preenchido adequadamente", user.getDistinguishedName());

			System.out.format("DN: %s | CC: %d\n", user.getDistinguishedName(), user.getCountryCode());
		}
	}

	@Test
	public void testMapFieldsIntoAttributes() throws Exception {
		System.out.println(" testMapFieldsIntoAttributes -----------");
		// faz a busca dos usuários
		NamingEnumeration<SearchResult> result = this.ldapTree.search("(&(objectCategory=Person)(userPrincipalName=*))");
		while (result.hasMoreElements()) {
			SearchResult entry = result.nextElement();

			ADUser user = LDAPConverter.convert(ADUser.class, entry);
			// esse atributo sempre será ajustado
			assertNotNull("O objeto do usuário não está sendo preenchido adequadamente", user.getDistinguishedName());

			// faz o mapeamento do distinguishedName para dn
			Map<String, String> mapping = new Hashtable<>();
			mapping.put("distinguishedName", "dn");

			Map<String, Object> mapped = LDAPConverter.mapFieldsIntoAttributes(user, mapping);

			// verifica se tem elementos
			assertTrue("A quantidade de atributos mapeados está incorreto", mapped.size() == 1);

			String dn = (String) mapped.get("dn").toString();

			// verifica se o atributo foi mapeado corretamente
			assertNotNull("O mapeamento não foi feito corretamente já que o campo \"dn\" está nulo", dn);
		}
	}

	@Test
	public void testGetModificationItens() throws Exception {
		System.out.println(" testGetModificationItens --------------");
		// faz a busca dos usuários
		NamingEnumeration<SearchResult> result = this.ldapTree.search("(&(objectCategory=Person)(userPrincipalName=*))");
		// se não tiver usuários
		assertTrue("Não existe na base LDAP um usuário com o atributo userPrincipal ajustado. Para este teste isto é necessário",
				result.hasMoreElements());
		SearchResult entry = result.nextElement();

		ADUser user = LDAPConverter.convert(ADUser.class, entry);
		// esse atributo sempre será ajustado
		assertNotNull("O objeto do usuário não está sendo preenchido adequadamente", user.getDistinguishedName());

		// ajusta um novo nome para o usuário
		user.setGivenName("teste");

		List<Attribute> attrs = LDAPConverter.getEntryAttributes(user, "givenName");
		
		// verifica se está com a quantidade correta de atributos
		assertEquals(
				String.format("Quantidade de atributos retornados inválido: %d", attrs.size()), 1, attrs.size());
		Attribute attr = attrs.get(0);
		
		// verifica se o valor do atributo está correto
		assertEquals(String.format("Nome no atributo não está correto: %s", attr.get()), attr.get(), "teste");
	}
}
