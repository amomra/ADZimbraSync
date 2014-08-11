package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
	 * Função que realiza a conversão de uma entrasda do LDAP retornado de uma
	 * busca na árvore para um objeto Java.
	 * @param objType O objeto do tipo {@link Class} correspondente ao tipo do
	 * objeto a ser retornado pela função. O tipo do objeto deverá uma classe
	 * que herda diretamente ou indiretamente a classe {@link LDAPEntry}.
	 * @param entry A entrada do LDAP que foi retornada na consulta que será
	 * convertida para o objeto.
	 * @return O objeto gerado a partir dos atributos do LDAP.
	 * @throws Exception Lança exceção quando não for possível ajustar o valor
	 * de um campo do objeto.
	 */
	public static <ObjectType extends LDAPEntry>
	ObjectType convert(Class<ObjectType> objType, SearchResult entry) throws Exception {
		// cria uma instância do objeto
		ObjectType obj = objType.newInstance();

		// pega a lista de atributos do LDAP e os campos da classe associados
		Map<String, Field> attrFields = obj.getLDAPAttributesFields();
		for (Entry<String, Field> attrField : attrFields.entrySet()) {

			// pega a lista de atributos
			Attributes attrs = entry.getAttributes();

			Attribute attr = attrs.get(attrField.getKey());
			if (attr != null)
			{
				Field field = attrField.getValue();
				// pega a anotação informando a classe de conversão do atributo
				LDAPAttribute attrAnn = field.getAnnotation(LDAPAttribute.class);
				LDAPAttributeParser parser = (LDAPAttributeParser) attrAnn.attributeParser().newInstance();

				// verifica o tipo para chamar a função de conversão
				Type fieldType = field.getType();
				// se for uma lista, pega o tipo interno
				if (fieldType.equals(List.class))
					fieldType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

				// faz a chamada do parser
				if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class))
					parser.parseAsBoolean(field, obj, attr);
				else if (fieldType.equals(byte.class) || fieldType.equals(Byte.class))
					parser.parseAsByte(field, obj, attr);
				else if (fieldType.equals(char.class) || fieldType.equals(Character.class))
					parser.parseAsChar(field, obj, attr);
				else if (fieldType.equals(short.class) || fieldType.equals(Short.class))
					parser.parseAsShort(field, obj, attr);
				else if (fieldType.equals(int.class) || fieldType.equals(Integer.class))
					parser.parseAsInt(field, obj, attr);
				else if (fieldType.equals(long.class) || fieldType.equals(Long.class))
					parser.parseAsLong(field, obj, attr);
				else if (fieldType.equals(float.class) || fieldType.equals(Float.class))
					parser.parseAsFloat(field, obj, attr);
				else if (fieldType.equals(double.class) || fieldType.equals(Double.class))
					parser.parseAsDouble(field, obj, attr);
				else
					parser.parseAsObject(field, obj, attr);
			}
		}

		return obj;
	}

	/**
	 * Função que cria o mapa de atributos do LDAP com os seus respectivos
	 * valores de acordo com o mapeamento dos campos passado.
	 * @param obj O objeto que terá os seus campos mapeados para os atributos
	 * do LDAP. Apenas os campos que estão definidos no mapeamento serão lidos.
	 * @param attrMap A definição do mapeamento dos atributos do LDAP com os
	 * campos do objeto. Caso haja vários mapeamentos para o mesmo atributo do
	 * LDAP, apenas o último será considerado. 
	 * @return Retorna a lista associativa associando o atributo do LDAP com o
	 * valor do campo.
	 * @throws Exception Lança exceção quando não for possível ler o valor do
	 * campo do objeto passado.
	 */
	public static <ObjectType extends LDAPEntry>
	Map<String, Object> mapFieldsIntoAttributes(ObjectType obj, Map<String, String> attrMap) throws Exception {
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
				field.setAccessible(true);

				// faz o mapeamento do atributo com o valor do campo
				mappedAttributes.put(attrName, field.get(obj));
			}
		}

		return mappedAttributes;
	}
}
