package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.naming.directory.Attribute;

/**
 * Classe que realiza a convers�o do valor do atributo lido do LDAP para os
 * tipos suportados pelo Java. A biblioteca de LDAP do Java usada neste projeto
 * retorna objetos do tipo <code>String</code> para todos os atributos lidos,
 * tornando necess�ria a realiza��o da convers�o para tipos espec�ficos do Java.
 * Esta classe faz a convers�o desta <code>String</code> para todos os tipos primitivos
 * utilizando as fun��es <code>parse</code> de cada um deles. Caso n�o seja um
 * tipo primitivo, o objeto a ser convertido n�o ser� alterado.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class LDAPAttributeParser {

	/**
	 * Construtor da classe.
	 */
	public LDAPAttributeParser() {
	}
	
	/**
	 * Fun��o que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um objeto Java.
	 * @param field O campo do objeto que ir� receber o resultado da convers�o.
	 * @param obj O objeto que ter� o seu campo alterado com o resultado da
	 * convers�o.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsObject(Field field, Object obj, Attribute attribute) throws Exception {
		/*
		 * Por padr�o, a biblioteca de LDAP do Java trata todos os atributos
		 * como String.
		 * Ref: http://docs.oracle.com/javase/jndi/tutorial/ldap/misc/attrs.html
		 */
		field.setAccessible(true);
		
		// verifica se o tipo do campo � uma lista
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
	 * Fun��o que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um booleano.
	 * @param field O campo do objeto que ir� receber o resultado da convers�o.
	 * @param obj O objeto que ter� o seu campo alterado com o resultado da
	 * convers�o.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsBoolean(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo � uma lista
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
	 * Fun��o que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um byte.
	 * @param field O campo do objeto que ir� receber o resultado da convers�o.
	 * @param obj O objeto que ter� o seu campo alterado com o resultado da
	 * convers�o.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsByte(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo � uma lista
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
	 * Fun��o que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um caracter.
	 * @param field O campo do objeto que ir� receber o resultado da convers�o.
	 * @param obj O objeto que ter� o seu campo alterado com o resultado da
	 * convers�o.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsChar(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo � uma lista
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
	 * Fun��o que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um inteiro de 16 bits.
	 * @param field O campo do objeto que ir� receber o resultado da convers�o.
	 * @param obj O objeto que ter� o seu campo alterado com o resultado da
	 * convers�o.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsShort(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo � uma lista
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
	 * Fun��o que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um inteiro de 32 bits.
	 * @param field O campo do objeto que ir� receber o resultado da convers�o.
	 * @param obj O objeto que ter� o seu campo alterado com o resultado da
	 * convers�o.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsInt(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo � uma lista
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
	 * Fun��o que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um inteiro de 64 bits.
	 * @param field O campo do objeto que ir� receber o resultado da convers�o.
	 * @param obj O objeto que ter� o seu campo alterado com o resultado da
	 * convers�o.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsLong(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo � uma lista
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
	 * Fun��o que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um ponto flutuante de 32 bits.
	 * @param field O campo do objeto que ir� receber o resultado da convers�o.
	 * @param obj O objeto que ter� o seu campo alterado com o resultado da
	 * convers�o.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsFloat(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo � uma lista
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
	 * Fun��o que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um ponto flutuante de 64 bits.
	 * @param field O campo do objeto que ir� receber o resultado da convers�o.
	 * @param obj O objeto que ter� o seu campo alterado com o resultado da
	 * convers�o.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsDouble(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);

		// verifica se o tipo do campo � uma lista
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
	
}
