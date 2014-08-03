package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import com.unboundid.ldap.sdk.Attribute;
import com.unboundid.ldap.sdk.SearchResultEntry;
import com.unboundid.ldap.sdk.migrate.ldapjdk.LDAPAttribute;

/**
 * Classe respons�vel em realizar a convers�o das entradas no LDAP em classes
 * Java. O mapeamento dos atributos do LDAP para os objetos � feito atrav�s da
 * utiliza��o da anota��o {@link LDAPAttribute}, onde os campos dos objetos
 * anotados com esta ser�o preenchidos com os valores dos atributos
 * correspondentes.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class LDAPConverter {

	/**
	 * Fun��o que realiza a convers�o de uma entrada do LDAP retornado de uma
	 * busca na �rvore para um objeto Java.
	 * @param objType O objeto do tipo {@link Class} correspondente ao tipo do
	 * objeto a ser retornado pela fun��o. O tipo do objeto dever� uma classe
	 * que herda diretamente ou indiretamente a classe {@link LDAPEntry}.
	 * @param entry
	 * @return
	 * @throws Exception Lan�a
	 */
	public static <ObjectType extends LDAPEntry> ObjectType convert(Class<ObjectType> objType, SearchResultEntry entry) throws Exception {
		// cria uma inst�ncia do objeto
		ObjectType obj = objType.newInstance();
		
		// pega a lista de atributos do LDAP e os campos da classe associados
		Hashtable<String, Field> attrFields = obj.getLDAPAttributesFields();
		for (Entry<String, Field> attrField : attrFields.entrySet()) {

			Attribute attr = entry.getAttribute(attrField.getKey());
			if (attr != null)
			{
				Field field = attrField.getValue();
				field.setAccessible(true);
				
				// verifica se o tipo do campo � uma lista
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
