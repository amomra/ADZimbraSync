package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;

/**
 * Classe que realiza a conversão do valor do atributo lido do LDAP para os
 * tipos suportados pelo Java. A biblioteca de LDAP do Java usada neste projeto
 * retorna objetos do tipo <code>String</code> para todos os atributos lidos,
 * tornando necessária a realização da conversão para tipos específicos do Java.
 * Esta classe faz a conversão desta <code>String</code> para todos os tipos primitivos
 * utilizando as funções <code>parse</code> de cada um deles. Caso não seja um
 * tipo primitivo ou um tipo não tratado por esta classe o campo não será
 * alterado.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class LDAPAttributeConverter {

	/**
	 * Construtor da classe.
	 */
	public LDAPAttributeConverter() {
	}

	/**
	 * Função que faz a leitura do campo e converte o valor para que este possa
	 * ser modificado no LDAP.
	 * @param field O campo da classe a ser modificada.
	 * @param obj O objeto contendo o valor.
	 * @return O atributo do LDAP.
	 * @throws Exception Lança exceção se não for possível retornar o valor.
	 */
	public final Attribute getValueAsAttribute(Field field, Object obj) throws Exception {
		// verifica se o valor do campo é null
		Object fieldValue = field.get(obj);
		if (fieldValue == null)
			return null;
		
		// pega o annotation do campo do LDAP para verificar o nome do atributo
		// do LDAP associado ao campo
		LDAPAttribute ann = (LDAPAttribute) field.getAnnotation(LDAPAttribute.class);
		if (ann == null)
			throw new Exception(String.format("Can't find LDAPAttribute annotation in field: %s", field.getName()));
		
		String attrName = ann.name();
		if (attrName.length() == 0)
			attrName = field.getName();
		
		// cria o atributo a ser modificado
		BasicAttribute attr = new BasicAttribute(attrName);
		
		// verifica se o tipo do campo é uma lista
		if (fieldValue instanceof List) {
			// faz o cast para List
			List<?> values = (List<?>) fieldValue;
			for (Object value : values) {
				// adiciona no atributo se ele for diferente de null
				if (value != null)
					attr.add(value.toString());
			}
		} else
			// adiciona o valor
			attr.add(fieldValue.toString());
		return attr;
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um booleano.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsBoolean(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de Boolean com os valores dos campos
			List<Boolean> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++)
				attrValues.add(Boolean.parseBoolean((String) attribute.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			field.setBoolean(obj, Boolean.parseBoolean((String) attribute.get()));
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um byte.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsByte(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de Byte com os valores dos campos
			List<Byte> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++)
				attrValues.add(Byte.parseByte((String) attribute.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			field.setByte(obj, Byte.parseByte((String) attribute.get()));
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um caracter.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsChar(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de Character com os valores dos campos
			List<Character> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++)
				attrValues.add(((String) attribute.get(i)).charAt(0));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			field.setChar(obj, ((String) attribute.get()).charAt(0));
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um inteiro de 16 bits.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsShort(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de Short com os valores dos campos
			List<Short> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++)
				attrValues.add(Short.parseShort((String) attribute.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			field.setShort(obj, Short.parseShort((String) attribute.get()));
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um inteiro de 32 bits.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsInt(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de Integer com os valores dos campos
			List<Integer> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++)
				attrValues.add(Integer.parseInt((String) attribute.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			field.setInt(obj, Integer.parseInt((String) attribute.get()));
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um inteiro de 64 bits.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsLong(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de Long com os valores dos campos
			List<Long> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++)
				attrValues.add(Long.parseLong((String) attribute.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			field.setLong(obj, Long.parseLong((String) attribute.get()));
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um ponto flutuante de 32 bits.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsFloat(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de Float com os valores dos campos
			List<Float> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++)
				attrValues.add(Float.parseFloat((String) attribute.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			field.setFloat(obj, Float.parseFloat((String) attribute.get()));
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um ponto flutuante de 64 bits.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsDouble(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de Double com os valores dos campos
			List<Double> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++)
				attrValues.add(Double.parseDouble((String) attribute.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			field.setDouble(obj, Double.parseDouble((String) attribute.get()));
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um {@link String}.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsString(Field field, Object obj, Attribute attribute) throws Exception {
		/*
		 * Por padrão, a biblioteca de LDAP do Java trata todos os atributos
		 * como String.
		 * Ref: http://docs.oracle.com/javase/jndi/tutorial/ldap/misc/attrs.html
		 */
		field.setAccessible(true);

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de strings com os valores dos campos
			List<String> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++)
				attrValues.add((String) attribute.get(i));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			field.set(obj, attribute.get());
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um {@link Date}.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsDate(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// cria o parser para a data vinda do LDAP
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
		dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de dates com os valores dos campos
			List<Date> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++) {
				String dateString = (String) attribute.get(i);

				attrValues.add(dateFormater.parse(dateString));
			}

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			String dateString = (String) attribute.get();
			// apenas ajusta o valor do campo
			field.set(obj, dateFormater.parse(dateString));
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um {@link DN}.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public final void parseAsDN(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo é uma lista
		if (field.getType() == List.class) {
			// cria uma lista de DN com os valores dos campos
			List<DN> attrValues = new ArrayList<>();
			for (int i = 0; i < attribute.size(); i++) {
				String dnString = (String) attribute.get(i);

				attrValues.add(DN.parse(dnString));
			}

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			String dnString = (String) attribute.get();
			// apenas ajusta o valor do campo
			field.set(obj, DN.parse(dnString));
		}
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um objeto Java. Por
	 * padrão, esta função irá fazer a conversão do objeto para {@link String}.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsCustomObject(Field field, Object obj, Attribute attribute) throws Exception {
		this.parseAsString(field, obj, attribute);
	}
}
