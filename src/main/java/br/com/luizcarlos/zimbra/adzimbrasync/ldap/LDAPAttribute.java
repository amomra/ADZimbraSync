package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anota��o utilizada para definir que um atributo da classe tamb�m � um
 * atributo da entidade correspondente no LDAP. Ao fazer uma consulta ao LDAP �
 * poss�vel realizar a convers�o da entidade para um objeto de uma classe Java
 * onde os atributos da classe anotados ser�o preenchidos com os valores
 * correspondentes da �rvore LDAP.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LDAPAttribute {

	/**
	 * Propriedade que indica o nome do atributo no LDAP que o atributo da
	 * classe est� associado. Caso esta propriedade esteja com o valor padr�o,
	 * que � uma string vazia, ser� considerado o nome do atributo da classe.
	 */
	String name() default "";
}
