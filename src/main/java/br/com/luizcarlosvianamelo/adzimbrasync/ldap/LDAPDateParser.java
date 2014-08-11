package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.naming.directory.Attribute;

/**
 * Classe respons�vel em fazer o parser de um Date a partir de um atributo do
 * LDAP. O formato padr�o da <code>String</code> de datas no LDAP �
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
	 * Fun��o que faz o parser do objeto com o valor do atributo lido pela API
	 * do LDAP e armazena o resultado no campo desejado como um objeto Java.
	 * Neste caso, o objeto a ser armazenado ser� do tipo {@link Date}.
	 * @param field O campo do objeto que ir� receber o resultado da convers�o.
	 * @param obj O objeto que ter� o seu campo alterado com o resultado da
	 * convers�o.
	 * @param attribute O atributo lido do LDAP.
	 * @throws Exception Lan�a uma exce��o quando n�o for poss�vel ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void parseAsObject(Field field, Object obj, Attribute attribute) throws Exception {
		field.setAccessible(true);
		
		// cria o parser para a data vinda do LDAP
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyyMMddHHmmss");
		dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));

		// verifica se o tipo do campo � uma lista
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
