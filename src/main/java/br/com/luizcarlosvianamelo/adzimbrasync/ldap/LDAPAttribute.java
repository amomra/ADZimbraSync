package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotação utilizada para definir que um atributo da classe também é um
 * atributo da entidade correspondente no LDAP. Ao fazer uma consulta ao LDAP é
 * possível realizar a conversão da entidade para um objeto de uma classe Java
 * onde os atributos da classe anotados serão preenchidos com os valores
 * correspondentes da árvore LDAP.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LDAPAttribute {

	/**
	 * Propriedade que indica o nome do atributo no LDAP que o atributo da
	 * classe está associado. Caso esta propriedade esteja com o valor padrão,
	 * que é uma string vazia, será considerado o nome do atributo da classe.
	 */
	String name() default "";

	/**
	 * Propriedade que indica a classe do objeto que fará a conversão do atributo
	 * do LDAP para o tipo do campo. O valor padrão é a classe
	 * {@link LDAPAttributeConverter}, sendo que esta trata os tipos não primitivos
	 * como <code>String</code>.
	 */
	Class<?> attributeConverter() default LDAPAttributeConverter.class;
}
