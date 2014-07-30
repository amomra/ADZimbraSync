package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.Hashtable;

public class LDAPEntry {
	
	public LDAPEntry()
	{
	}
	
	public Hashtable<String, Field> getLDAPAttributesFields() {
		// pega a lista de campos
		Hashtable<String, Field> fields = new Hashtable<>();
		for (Field field : this.getClass().getDeclaredFields())
		{
			// se o campo  tiver o annotation
			if (field.isAnnotationPresent(LDAPAttribute.class))
			{
				// pega o annotation
				LDAPAttribute ldapAttribute = (LDAPAttribute) field.getAnnotation(LDAPAttribute.class);
				String fieldName = field.getName();
				// se o nome do atributo for diferente do nome do campo
				if (ldapAttribute.name() != "")
					fieldName = ldapAttribute.name(); 
				fields.put(fieldName, field);
			}
		}

		// lança exceção se não tiver atributos a serem coletados
		if (fields.size() == 0)
			throw new InvalidParameterException("LDAP Entry has no attributes to be returned");
		return fields;
	}
}
