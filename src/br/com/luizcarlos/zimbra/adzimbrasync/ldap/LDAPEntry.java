package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
public @interface LDAPEntry {
	
	String uidAttribute() default "uid";
}
