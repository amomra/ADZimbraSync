package br.com.luizcarlosvianamelo.adzimbrasync.zimbra;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.AttributeAccessMode;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPEntry;

import com.zimbra.cs.account.Domain;

/**
 * Classe responsável em fazer o mapeamento das entidades do servidor LDAP
 * externo para o Zimbra.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ZimbraLDAPMapper {
	@SuppressWarnings("serial")
	private static final Map<String, String> DEFAULT_ZIMBRA_AD_USER_ATTR_MAP = new HashMap<String, String>() {{
		put("cn", "cn");
		put("name", "displayName");
		put("givenName", "givenName");
		put("sn", "sn");
		put("distinguishedName", "zimbraAuthLdapExternalDn");
	}};

	@SuppressWarnings("serial")
	private static final Map<String, String> DEFAULT_ZIMBRA_AD_GROUP_ATTR_MAP = new HashMap<String, String>() {{

	}};


	/**
	 * Função que retorna o mapeamento dos atributos do usuário do AD com
	 * os atributos do Zimbra.
	 * @return A lista associativa com o mapeamento dos atributos. A chave
	 * desta será o nome do atributo no AD enquanto o valor será o nome do
	 * atributo no Zimbra.
	 */
	public static Map<String, String> getUserAttributeMapping() {
		return ZimbraLDAPMapper.DEFAULT_ZIMBRA_AD_USER_ATTR_MAP;
	}

	/**
	 * Função que retorna o mapeamento dos atributos do usuário do AD com
	 * os atributos do Zimbra. Também coleta o mapeamento configurado no
	 * atributo <code>zimbraAutoProvAttrMap</code>.
	 * @param domain O domínio onde está configurado o mapeamento.
	 * @return A lista associativa com o mapeamento dos atributos. A chave
	 * desta será o nome do atributo no AD enquanto o valor será o nome do
	 * atributo no Zimbra.
	 */
	public static Map<String, String> getUserAttributeMapping(Domain domain) {
		// TODO Buscar a lista de atributos mapeados do domínio
		return ZimbraLDAPMapper.DEFAULT_ZIMBRA_AD_USER_ATTR_MAP;
	}

	/**
	 * Função que retorna o mapeamento dos atributos do grupo do AD com
	 * os atributos do Zimbra.
	 * @return A lista associativa com o mapeamento dos atributos. A chave
	 * desta será o nome do atributo no AD enquanto o valor será o nome do
	 * atributo no Zimbra.
	 */
	public static Map<String, String> getGroupAttributeMapping() {
		return ZimbraLDAPMapper.DEFAULT_ZIMBRA_AD_GROUP_ATTR_MAP;
	}

	/**
	 * Função que cria o mapa de atributos do LDAP com os seus respectivos
	 * valores de acordo com o mapeamento dos campos passado.
	 * @param obj O objeto que terá os seus campos mapeados para os atributos
	 * do LDAP. Apenas os campos que estão definidos no mapeamento cujos os
	 * valores são diferentes de <code>null</code> serão lidos.
	 * @param attrAccessMode O modo de acesso dos atributos a serem retornados.
	 * @param attrMap A definição do mapeamento dos atributos do LDAP com os
	 * campos do objeto. Caso haja vários mapeamentos para o mesmo atributo do
	 * LDAP, apenas o último será considerado. Também serão apenas retornados
	 * os atributos com permissão de leitura do LDAP.
	 * @return Retorna a lista associativa associando o atributo do LDAP com o
	 * valor do campo.
	 * @throws Exception Lança exceção quando não for possível ler o valor do
	 * campo do objeto passado.
	 */
	public static <ObjectType extends LDAPEntry>
	Map<String, Object> mapObjectFieldsIntoAttributes(ObjectType obj, AttributeAccessMode attrAccessMode, 
			Map<String, String> attrMap) throws Exception {		
		Map<String, Object> mappedAttributes = new Hashtable<>();

		// pega a lista de campos do objeto
		Map<String, Field> attrFields = obj.getLDAPAttributesFields();
		for (Entry<String, Field> attrField : attrFields.entrySet()) {
			/*
			 * Verifica a qual atributo do LDAP o campo deverá ser associado.
			 * Caso não tenha sido feita a associação do campo atual com o
			 * atributo, o mesmo será ignorado.
			 */
			String attrName = attrMap.get(attrField.getKey());
			if (attrName != null) {
				Field field = attrField.getValue();
				LDAPAttribute ann = (LDAPAttribute) field.getAnnotation(LDAPAttribute.class);
				// ignora os atributos sem permissão
				if (!ann.accessMode().haveRequestedPermission(attrAccessMode))
					continue;

				field.setAccessible(true);

				Object value = field.get(obj);
				if (value != null)
					// faz o mapeamento do atributo com o valor do campo
					mappedAttributes.put(attrName, value);
			}
		}

		return mappedAttributes;
	}

	/**
	 * Função que preenche os campos do objeto com os valores dos atributos de
	 * acordo com o mapeamento.
	 * @param obj O objeto a ser preenchido.
	 * @param attributes Os valores dos atributos.
	 * @param attrMap O mapeamento dos atributos.
	 * @throws Exception Lança exceção quando não for possível preencher os
	 * campos do objeto.
	 */
	public static <ObjectType extends LDAPEntry>
	void fillAttributesIntoObjectFields(ObjectType obj, Map<String, ? extends Object> attributes,
			Map<String, String> attrMap) throws Exception {
		// retorna os campos do objeto
		Map<String, Field> attrFields = obj.getLDAPAttributesFields();
		
		for (Entry<String, Field> attrField : attrFields.entrySet()) {			
			// pega o atributo do zimbra ao qual o campo está associado
			String attributeName = attrMap.get(attrField.getKey());
			// ignora o campo se não existir o mapeamento
			if (attributeName != null) {
				Field field = attrField.getValue();
				field.setAccessible(true);
				
				// pega o valor do atributo
				Object value = attributes.get(attributeName);
				if (value == null)
					// ignora os valores nulos
					continue;
				
				// ajusta o valor do campo
				LDAPAttribute attrAnn = field.getAnnotation(LDAPAttribute.class);
				if (attrAnn == null || !attrAnn.accessMode().haveRequestedPermission(AttributeAccessMode.WRITE))
					/*
					 * Ignora os valores nulos e se o campo não estiver
					 * habilitado para escrita
					 */
					continue;
				
				// TODO Fazer lógica de conversão das informações a serem ajustadas no Zimbra
				
				// ajusta o valor
				field.set(obj, value);
			}
		}
	}
}
