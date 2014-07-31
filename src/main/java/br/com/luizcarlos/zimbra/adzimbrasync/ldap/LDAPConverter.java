package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

public class LDAPConverter {

	public static <ObjectType extends LDAPEntry> ObjectType convert(Class<ObjectType> objType, SearchResult entry)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, NamingException {
		// cria uma instância do objeto
		ObjectType obj = objType.newInstance();

		// pega os atributos do objeto do LDAP
		Attributes attrs = entry.getAttributes();

		// pega a lista de atributos do LDAP e os campos da classe associados
		Hashtable<String, Field> attrFields = obj.getLDAPAttributesFields();
		for (Entry<String, Field> attrField : attrFields.entrySet()) {

			Attribute attr = attrs.get(attrField.getKey());
			if (attr != null)
			{

				// cria uma lista caso o atributo for multivalorado
				if (attr.size() > 1) {
					
					//attr.get
				}
				else {
					// apenas ajusta o valor do campo
					attrField.getValue().set(obj, attr.get());
				}
			}
		}

		return obj;
	}
}
