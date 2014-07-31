package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.SearchResultEntry;

public class LDAPConverter {

	public static <ObjectType extends LDAPEntry> ObjectType convert(Class<ObjectType> objType, SearchResultEntry entry) throws Exception {
		// cria uma instância do objeto
		ObjectType obj = objType.newInstance();
		
		// pega a lista de atributos do LDAP e os campos da classe associados
		Hashtable<String, Field> attrFields = obj.getLDAPAttributesFields();
		for (Entry<String, Field> attrField : attrFields.entrySet()) {

			Attribute attr = entry.getAttribute(attrField.getKey());
			if (attr != null)
			{
				Field field = attrField.getValue();
				field.setAccessible(true);
				
				// verifica se o tipo do campo é uma lista
				if (field.getType() == List.class) {
					// cria uma lista de strings com os valores dos campos
					List<String> attrValues = new ArrayList<>(Arrays.asList(attr.getValues()));
					
					// ajusta a lista
					field.set(obj, attrValues);
				} else {
					// apenas ajusta o valor do campo
					field.set(obj, attr.getValue());
				}
			}
		}

		return obj;
	}
}
