package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.naming.directory.Attribute;

/**
 * Classe responsável em fazer o parser de um Date a partir de um atributo do
 * LDAP. O formato padrão da <code>String</code> de datas no LDAP é
 * "yyyyMMddHHmmss".
 * @author Luiz Carlos Viana Melo
 *
 */
public class LDAPDateParser extends LDAPAttributeParser {

	/**
	 * Construtor da classe.
	 */
	public LDAPDateParser() {
	}

	/**
	 * Função que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um objeto Java.
	 * Neste caso, o objeto a ser armazenado será do tipo {@link Date}.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsObject(Field field, Object obj, Attribute attribute) throws Exception {
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

}
