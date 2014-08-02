package br.com.luizcarlos.zimbra.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

/**
 * Classe que representa uma entidade da árvore LDAP, onde os atributos anotados
 * com a anotação {@link LDAPAttribute} representam os campos associados aos
 * atributos de uma entidade no LDAP.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class LDAPEntry {
	
	/**
	 * Construtor da classe.
	 */
	public LDAPEntry()
	{
	}
	
	/**
	 * Função privada que retorna a lista contendo todos os campos declarados
	 * nas classes pertecentes a hierarquia.
	 * @param clazz A classe inicial.
	 * @return A lista de campos de todas as classes.
	 */
	private List<Field> getAllClassesFields(Class<?> clazz) {
		/*
		 * Como a função getFields não retorna os campos que não são publicos e
		 * a função getDeclaredFields não retorna os campos herdados da classe
		 * pai é necessário fazer uma função que percorra toda hierarquia de
		 * classes e coletar os campos de cada uma delas.
		 */
		
		// a condição de parada da recursão será quando a busca alcançar esta
		// classe
		if (clazz != LDAPEntry.class) {
			// faz a chamada recursiva para a classe pai da classe atual
			// para retornar a lista preenchida até o momento
			List<Field> fields = this.getAllClassesFields(clazz.getSuperclass());
			
			// e preenche a lista com os campos da classe atual
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			
			return fields;
		}
		
		// retorna a lista vazia já que esta classe não tem atributos do LDAP
		// a serem coletados
		return new ArrayList<>();
	}
	
	/**
	 * Retorna a lista de atributos do LDAP com os campos associados da classe.
	 * Esta lista é montada a partir da lista de campos anotados com a anotação
	 * {@link LDAPAttribute}.
	 * 
	 * @return A lista associativa onde a chave é o nome do atributo do LDAP e
	 * o valor o campo da classe associado ao atributo.
	 */
	public Hashtable<String, Field> getLDAPAttributesFields() {
		// pega a lista de campos de todas as classes na hierarquia
		List<Field> classesFields = this.getAllClassesFields(this.getClass());
		
		// pega a lista de campos
		Hashtable<String, Field> fields = new Hashtable<>();
		for (Field field : classesFields)
		{
			// se o campo  tiver o annotation
			if (field.isAnnotationPresent(LDAPAttribute.class))
			{
				// pega o annotation
				LDAPAttribute ldapAttribute = (LDAPAttribute) field.getAnnotation(LDAPAttribute.class);
				String fieldName = field.getName();
				// se o nome do atributo for diferente do nome do campo
				if (ldapAttribute.name().length() > 0)
					fieldName = ldapAttribute.name(); 
				fields.put(fieldName, field);
			}
		}

		// lança exceção se não tiver atributos a serem coletados
		if (fields.size() == 0)
			throw new InvalidParameterException("LDAP Entry has no attributes to be returned");
		return fields;
	}
}
