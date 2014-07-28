package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
public @interface LDAPAttribute {

	String name() default "";
}
