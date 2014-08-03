package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchResult;

/**
 * Classe responsável em realizar a conversão das entradas no LDAP em classes
 * Java. O mapeamento dos atributos do LDAP para os objetos é feito através da
 * utilização da anotação {@link LDAPAttribute}, onde os campos dos objetos
 * anotados com esta serão preenchidos com os valores dos atributos
 * correspondentes.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class LDAPConverter {

	/**
	 * Função que realiza a conversão de uma entrada do LDAP retornado de uma
	 * busca na árvore para um objeto Java.
	 * @param objType O objeto do tipo {@link Class} correspondente ao tipo do
	 * objeto a ser retornado pela função. O tipo do objeto deverá uma classe
	 * que herda diretamente ou indiretamente a classe {@link LDAPEntry}.
	 * @param entry
	 * @return
	 * @throws Exception Lança
	 */
	public static <ObjectType extends LDAPEntry> ObjectType convert(Class<ObjectType> objType, SearchResult entry) throws Exception {
		// cria uma instância do objeto
		ObjectType obj = objType.newInstance();
		
		// pega a lista de atributos do LDAP e os campos da classe associados
		Hashtable<String, Field> attrFields = obj.getLDAPAttributesFields();
		for (Entry<String, Field> attrField : attrFields.entrySet()) {

			// pega a lista de atributos
			Attributes attrs = entry.getAttributes();
			
			Attribute attr = attrs.get(attrField.getKey());
			if (attr != null)
			{
				Field field = attrField.getValue();
				field.setAccessible(true);
				
				// verifica se o tipo do campo é uma lista
				if (field.getType() == List.class) {
					// cria uma lista de strings com os valores dos campos
					List<String> attrValues = new ArrayList<>();
					for (int i = 0; i < attr.size(); i++)
						attrValues.add((String) attr.get(i));
					
					// ajusta a lista
					field.set(obj, attrValues);
				} else {
					// apenas ajusta o valor do campo
					field.set(obj, attr.get());
				}
			}
		}

		return obj;
	}
}
