package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.Hashtable;

/**
 * Classe que representa uma entidade da �rvore LDAP, onde os atributos anotados
 * com a anota��o {@link LDAPAttribute} representam os campos associados aos
 * atributos de uma entidade no LDAP.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class LDAPEntry {
	
	/**
	 * Construtor da classe.
	 */
	public LDAPEntry()
	{
	}
	
	/**
	 * Retorna a lista de atributos do LDAP com os campos associados da classe.
	 * Esta lista � montada a partir da lista de campos anotados com a anota��o
	 * {@link LDAPAttribute}.
	 * 
	 * @return A lista associativa onde a chave � o nome do atributo do LDAP e
	 * o valor o campo da classe associado ao atributo.
	 */
	public Hashtable<String, Field> getLDAPAttributesFields() {
		// pega a lista de campos
		Hashtable<String, Field> fields = new Hashtable<>();
		for (Field field : this.getClass().getFields())
		{
			// se o campo  tiver o annotation
			if (field.isAnnotationPresent(LDAPAttribute.class))
			{
				// pega o annotation
				LDAPAttribute ldapAttribute = (LDAPAttribute) field.getAnnotation(LDAPAttribute.class);
				String fieldName = field.getName();
				// se o nome do atributo for diferente do nome do campo
				if (ldapAttribute.name().length() > 0)
					fieldName = ldapAttribute.name(); 
				fields.put(fieldName, field);
			}
		}

		// lan�a exce��o se n�o tiver atributos a serem coletados
		if (fields.size() == 0)
			throw new InvalidParameterException("LDAP Entry has no attributes to be returned");
		return fields;
	}
}
