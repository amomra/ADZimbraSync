package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Classe que realiza a conversão do valor do atributo lido do LDAP para os
 * tipos suportados pelo Java. Esta classe faz a conversão desta
 * <code>String</code> para todos os tipos primitivos utilizando as funções
 * <code>parse</code> de cada um deles. Caso não seja um tipo primitivo ou um
 * tipo não tratado por esta classe o campo não será alterado.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
class LDAPAttributeConverter {

	/**
	 * Função que faz a conversão do valor do campo para uma lista de
	 * <i>strings</i>.
	 * @param field O campo da classe a ser convertido.
	 * @param obj O objeto que terá o valor do campo convertido.
	 * @return Retorna a lista de {@link String} com os valores do campo. Cada
	 * elemento desta contém o valor contido na lista ou vetor convertido para
	 * <i>string</i>. Caso o campo não seja uma lista, a lista a ser retornada
	 * conterá apenas um valor. Caso o valor do campo seja <code>null</code>,
	 * será retornado este valor.
	 * @throws Exception Lança exceção quando não for possível converter o valor
	 * do campo.
	 */
	public static List<String> getFieldValue(Field field, Object obj) throws Exception {
		// verifica o tipo para chamar a função de conversão
		Class<?> fieldType = field.getType();
		// se for uma lista, pega o tipo interno
		if (Iterable.class.isAssignableFrom(fieldType))
			fieldType = (Class<?>) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
		
		/*
		 * A princípio, o tipo Date é o único que precisa de um tratamento
		 * especial na conversão.
		 */
		if (fieldType.equals(Date.class))
			return getFieldValueAsDate(field, obj);
		
		// trata como um objeto padrão
		return getFieldValueAsObject(field, obj);
	}
	
	/**
	 * Função que faz a conversão do valor do campo para uma lista de
	 * <i>strings</i>. O valor do campo será convertido através da chamada do
	 * método {@link Object#toString()} do mesmo.
	 * @param field O campo da classe a ser convertido.
	 * @param obj O objeto que terá o valor do campo convertido.
	 * @return Retorna a lista de {@link String} com os valores do campo. Cada
	 * elemento desta contém o valor contido na lista ou vetor convertido para
	 * <i>string</i>. Caso o campo não seja uma lista, a lista a ser retornada
	 * conterá apenas um valor. Caso o valor do campo seja <code>null</code>,
	 * será retornado este valor.
	 * @throws Exception Lança exceção quando não for possível converter o valor
	 * do campo.
	 */
	private static List<String> getFieldValueAsObject(Field field, Object obj) throws Exception {
		field.setAccessible(true);
		// verifica se o valor do campo é nulo
		Object fieldValue = field.get(obj);
		if (fieldValue == null)
			return null;
		
		List<String> values = new ArrayList<>();
		
		/*
		 * Verifica se o tipo do campo é simples, um vetor ou um Iterable.
		 */
		Class<?> fieldType = field.getType();
		if (fieldType.isArray()) {
			// trata os elementos deste vetor como objetos genéricos
			int arraySize = Array.getLength(fieldValue);
			for (int i = 0; i < arraySize; i++) {
				Object arrayValue = Array.get(fieldValue, i);
				// adiciona na lista apenas se o valor não for nulo
				if (arrayValue != null)
					values.add(arrayValue.toString());
			}
		} else if (Iterable.class.isAssignableFrom(fieldType)) {
			// trata ele como um Iterable
			Iterable<?> list = (Iterable<?>) fieldValue;
			for (Object listValue : list) {
				// verifica se o objeto não é nulo
				if (listValue != null)
					values.add(listValue.toString());
			}
		} else
			// o campo é um tipo simples
			values.add(fieldValue.toString());
		
		return values;
	}
	
	/**
	 * Função que faz a conversão do valor do campo, sendo que este é do tipo
	 * {@link Date}, para uma lista de <i>strings</i>. O valor do campo será
	 * formatado de acordo com a formatação suportada pelo LDAP para este tipo
	 * de dado (<code>"yyyyMMddHHmmss'Z'"</code>).
	 * @param field O campo da classe a ser convertido.
	 * @param obj O objeto que terá o valor do campo convertido.
	 * @return Retorna a lista de {@link String} com os valores do campo. Cada
	 * elemento desta contém o valor contido na lista ou vetor convertido para
	 * <i>string</i>. Caso o campo não seja uma lista, a lista a ser retornada
	 * conterá apenas um valor. Caso o valor do campo seja <code>null</code>,
	 * será retornado este valor.
	 * @throws Exception Lança exceção quando não for possível converter o valor
	 * do campo.
	 */
	private static List<String> getFieldValueAsDate(Field field, Object obj) throws Exception {
		field.setAccessible(true);
		// verifica se o valor do campo é nulo
		Object fieldValue = field.get(obj);
		if (fieldValue == null)
			return null;

		List<String> values = new ArrayList<>();
		
		/*
		 * O tratamento de um Date é diferente dos outros objetos pois este
		 * deverá estar formatado de acordo com a notação do LDAP.
		 */
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss'Z'");

		/*
		 * Verifica se o tipo do campo é simples, um vetor ou um Iterable.
		 */
		Class<?> fieldType = field.getType();
		if (fieldType.isArray()) {
			// trata os elementos deste vetor como objetos genéricos
			int arraySize = Array.getLength(fieldValue);
			for (int i = 0; i < arraySize; i++) {
				Object arrayValue = Array.get(fieldValue, i);
				// adiciona na lista apenas se o valor não for nulo
				if (arrayValue != null)
					values.add(dateFormat.format(arrayValue));
			}
		} else if (Iterable.class.isAssignableFrom(fieldType)) {
			// trata ele como um Iterable
			Iterable<?> list = (Iterable<?>) fieldValue;
			for (Object listValue : list) {
				// verifica se o objeto não é nulo
				if (listValue != null)
					values.add(dateFormat.format(listValue));
			}
		} else
			// o campo é um tipo simples
			values.add(dateFormat.format(fieldValue));

		return values;
	}

	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado conforme o tipo do mesmo.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public static void setFieldValue(Field field, Object obj, List<String> values) throws Exception {
		// verifica o tipo para chamar a função de conversão
		Type fieldType = field.getType();
		// se for uma lista, pega o tipo interno
		if (fieldType.equals(List.class))
			fieldType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];

		// faz a chamada do parser para os tipos primitivos
		if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class))
			setFieldValueAsBoolean(field, obj, values);
		else if (fieldType.equals(byte.class) || fieldType.equals(Byte.class))
			setFieldValueAsByte(field, obj, values);
		else if (fieldType.equals(char.class) || fieldType.equals(Character.class))
			setFieldValueAsChar(field, obj, values);
		else if (fieldType.equals(short.class) || fieldType.equals(Short.class))
			setFieldValueAsShort(field, obj, values);
		else if (fieldType.equals(int.class) || fieldType.equals(Integer.class))
			setFieldValueAsInt(field, obj, values);
		else if (fieldType.equals(long.class) || fieldType.equals(Long.class))
			setFieldValueAsLong(field, obj, values);
		else if (fieldType.equals(float.class) || fieldType.equals(Float.class))
			setFieldValueAsFloat(field, obj, values);
		else if (fieldType.equals(double.class) || fieldType.equals(Double.class))
			setFieldValueAsDouble(field, obj, values);
		else if (fieldType.equals(String.class)) // inicio do parser dos tipos não primitivos
			setFieldValueAsString(field, obj, values);
		else if (fieldType.equals(Date.class))
			setFieldValueAsDate(field, obj, values);
		else if (fieldType.equals(DN.class))
			setFieldValueAsDN(field, obj, values);
	}
	
	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado como um booleano.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsBoolean(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);

		Class<?> fieldType = field.getType();
		
		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			boolean[] arrayValue = new boolean[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = Boolean.parseBoolean(values.get(i));		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de Boolean com os valores dos campos
			List<Boolean> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++)
				attrValues.add(Boolean.parseBoolean(values.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			if (values.size() > 0)
				field.set(obj, Boolean.parseBoolean(values.get(0)));
		}
	}

	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado como um byte.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsByte(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);

		Class<?> fieldType = field.getType();
		
		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			byte[] arrayValue = new byte[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = Byte.parseByte(values.get(i));		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de Boolean com os valores dos campos
			List<Byte> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++)
				attrValues.add(Byte.parseByte(values.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			if (values.size() > 0)
				field.set(obj, Byte.parseByte(values.get(0)));
		}
	}

	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado como um caracter.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsChar(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);

		Class<?> fieldType = field.getType();
		
		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			char[] arrayValue = new char[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = values.get(i).charAt(0);		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de Boolean com os valores dos campos
			List<Character> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++)
				attrValues.add(values.get(i).charAt(0));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			if (values.size() > 0)
				field.set(obj, values.get(0).charAt(0));
		}
	}

	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado como um inteiro de 16 bits.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsShort(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);

		Class<?> fieldType = field.getType();
		
		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			short[] arrayValue = new short[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = Short.parseShort(values.get(i));		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de Boolean com os valores dos campos
			List<Short> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++)
				attrValues.add(Short.parseShort(values.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			if (values.size() > 0)
				field.set(obj, Short.parseShort(values.get(0)));
		}
	}

	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado como um inteiro de 32 bits.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsInt(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);

		Class<?> fieldType = field.getType();
		
		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			int[] arrayValue = new int[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = Integer.parseInt(values.get(i));		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de Boolean com os valores dos campos
			List<Integer> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++)
				attrValues.add(Integer.parseInt(values.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			if (values.size() > 0)
				field.set(obj, Integer.parseInt(values.get(0)));
		}
	}

	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado como um inteiro de 64 bits.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsLong(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);

		Class<?> fieldType = field.getType();
		
		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			long[] arrayValue = new long[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = Long.parseLong(values.get(i));		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de Boolean com os valores dos campos
			List<Long> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++)
				attrValues.add(Long.parseLong(values.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			if (values.size() > 0)
				field.set(obj, Long.parseLong(values.get(0)));
		}
	}

	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado como um ponto flutuante de 32 bits.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsFloat(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);

		Class<?> fieldType = field.getType();
		
		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			float[] arrayValue = new float[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = Float.parseFloat(values.get(i));		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de Boolean com os valores dos campos
			List<Float> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++)
				attrValues.add(Float.parseFloat(values.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			if (values.size() > 0)
				field.set(obj, Float.parseFloat(values.get(0)));
		}
	}

	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado como um ponto flutuante de 64 bits.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsDouble(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);

		Class<?> fieldType = field.getType();
		
		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			double[] arrayValue = new double[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = Double.parseDouble(values.get(i));		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de Boolean com os valores dos campos
			List<Double> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++)
				attrValues.add(Double.parseDouble(values.get(i)));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			if (values.size() > 0)
				field.set(obj, Double.parseDouble(values.get(0)));
		}
	}

	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado como um {@link String}.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsString(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);

		Class<?> fieldType = field.getType();
		
		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			String[] arrayValue = new String[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = values.get(i);		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de Boolean com os valores dos campos
			List<String> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++)
				attrValues.add(values.get(i));

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			// apenas ajusta o valor do campo
			if (values.size() > 0)
				field.set(obj, values.get(0));
		}
	}

	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado como um {@link Date}.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsDate(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);
		
		Class<?> fieldType = field.getType();

		// cria o parser para a data vinda do LDAP
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
		dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));

		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			Date[] arrayValue = new Date[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = dateFormater.parse(values.get(i));		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de dates com os valores dos campos
			List<Date> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++) {
				String dateString = values.get(i);

				attrValues.add(dateFormater.parse(dateString));
			}

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			String dateString = values.get(0);
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
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	private static void setFieldValueAsDN(Field field, Object obj, List<String> values) throws Exception {
		field.setAccessible(true);
		
		Class<?> fieldType = field.getType();

		// se for um array
		if (fieldType.isArray()) {
			// cria um vetor
			DN[] arrayValue = new DN[values.size()];
			for (int i = 0; i < arrayValue.length; i++)
				arrayValue[i] = DN.parse(values.get(i));		
			field.set(obj, arrayValue);
		}
		// verifica se o tipo do campo é uma lista
		else if (List.class.isAssignableFrom(fieldType)) {
			// cria uma lista de DN com os valores dos campos
			List<DN> attrValues = new ArrayList<>();
			for (int i = 0; i < values.size(); i++) {
				String dnString = (String) values.get(i);

				attrValues.add(DN.parse(dnString));
			}

			// ajusta a lista
			field.set(obj, attrValues);
		} else {
			String dnString = values.get(0);
			// apenas ajusta o valor do campo
			field.set(obj, DN.parse(dnString));
		}
	}
}
