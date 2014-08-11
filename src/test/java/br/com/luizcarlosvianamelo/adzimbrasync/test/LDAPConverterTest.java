package br.com.luizcarlosvianamelo.adzimbrasync.test;

import static org.junit.Assert.*;

import java.util.Hashtable;
import java.util.Map;

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

		// cria o objeto da conex�o
		this.ldapTree = new LDAPTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				this.prop.getLDAPSearchBindDn(),
				this.prop.getLDAPSearchBindPassword());

		// realiza a conex�o
		ldapTree.connect();
	}

	@Test
	public void testConvert() throws Exception {

		// faz a busca dos usu�rios
		NamingEnumeration<SearchResult> result = this.ldapTree.search("(&(objectCategory=Person)(userPrincipalName=*))");
		while (result.hasMoreElements()) {
			SearchResult entry = result.nextElement();

			ADUser user = LDAPConverter.convert(ADUser.class, entry);
			// esse atributo sempre ser� ajustado
			assertNotNull("O objeto do usu�rio n�o est� sendo preenchido adequadamente", user.getDistinguishedName());

			System.out.format("DN: %s | CC: %d\n", user.getDistinguishedName(), user.getCountryCode());
		}
	}

	@Test
	public void testMapFieldsIntoAttributes() throws Exception {
		// faz a busca dos usu�rios
		NamingEnumeration<SearchResult> result = this.ldapTree.search("(&(objectCategory=Person)(userPrincipalName=*))");
		while (result.hasMoreElements()) {
			SearchResult entry = result.nextElement();

			ADUser user = LDAPConverter.convert(ADUser.class, entry);
			// esse atributo sempre ser� ajustado
			assertNotNull("O objeto do usu�rio n�o est� sendo preenchido adequadamente", user.getDistinguishedName());
			
			// faz o mapeamento do distinguishedName para dn
			Map<String, String> mapping = new Hashtable<>();
			mapping.put("distinguishedName", "dn");
			
			Map<String, Object> mapped = LDAPConverter.mapFieldsIntoAttributes(user, mapping);
			
			// verifica se tem elementos
			assertTrue("A quantidade de atributos mapeados est� incorreto", mapped.size() == 1);
			
			String dn = (String) mapped.get("dn");
			
			// verifica se o atributo foi mapeado corretamente
			assertNotNull("O mapeamento n�o foi feito corretamente j� que o campo \"dn\" est� nulo", dn);
		}
	}
}
