package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;

/**
 * Classe que representa uma entidade da árvore LDAP, onde os atributos anotados
 * com a anotação {@link LDAPAttribute} representam os campos associados aos
 * atributos de uma entidade no LDAP.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public abstract class LDAPEntry {
	
	/**
	 * Construtor da classe.
	 */
	public LDAPEntry() {
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
	public Map<String, AttributeField> getLDAPAttributesFields() throws Exception {
		// pega a lista de campos de todas as classes na hierarquia
		List<Field> classesFields = this.getAllClassesFields(this.getClass());
		
		// pega a lista de campos
		Hashtable<String, AttributeField> fields = new Hashtable<>();
		for (Field field : classesFields)
		{
			// se o campo  tiver o annotation
			if (field.isAnnotationPresent(LDAPAttribute.class))
			{
				// cria a associação do campo com o atributo
				AttributeField attrField = new AttributeField(field);
				// e adiciona na lista
				fields.put(attrField.getAttributeName(), attrField);
			}
		}

		// lança exceção se não tiver atributos a serem coletados
		if (fields.size() == 0)
			throw new IllegalArgumentException("LDAP Entry has no attributes to be returned");
		return fields;
	}
	
	/**
	 * Função que retorna a lista de atributos do LDAP contidos no objeto
	 * passado.
	 * @param attrAccessMode O modo de acesso dos atributos a serem retornados.
	 * @param attributesNames A lista com os nomes dos atributos que serão
	 * coletados. Caso esta seja vazia, serão coletados todos os atributos.
	 * @return Retorna a lista com os atributos contidos na lista de nomes
	 * que não estão com o valor igual a <code>null</code>. Também serão
	 * ignorados os atributos que não possuírem a permissão solicitada.
	 * Pode retornar uma lista vazia caso as condições citadas não tenham
	 * sido atendidas.
	 * @throws Exception Lança exceção quando não for possível coletar os
	 * atributos.
	 */
	public Attributes getLDAPAttributes(AttributeAccessMode attrAccessMode, String... attributesNames) throws Exception {
		Attributes attributes = new BasicAttributes();

		Map<String, AttributeField> attrFields = this.getLDAPAttributesFields();

		/*
		 * Serão modificados apenas os atributos que estiverem na lista de
		 * atributos a serem modificados e se o seus valores não forem
		 * nulos.
		 */
		if (attributesNames.length > 0) {
			for (String attrName : attributesNames) {
				AttributeField attrField = attrFields.get(attrName);
				// ignora o atributo se ele não exisitr na classe
				if (attrField == null)
					continue;

				// considera apenas os atributos que possuirem permissão determinada
				if (!attrField.haveRequestedPermission(attrAccessMode))
					continue;

				// adiciona na lista o atributo
				List<String> values = attrField.getAsList(this);
				if (values == null)
					continue;
				
				Attribute attr = new BasicAttribute(attrField.getAttributeName());
				
				for (String value : values)
					attr.add(value);
				
				attributes.put(attr);
			}
		} else {
			/*
			 * Se não forem especificados os atributos que serão modificado,
			 * então considera que todos eles serão.
			 */
			for (Entry<String, AttributeField> entry : attrFields.entrySet()) {
				AttributeField attrField = entry.getValue();

				// considera apenas os atributos que possuirem permissão determinada
				if (!attrField.haveRequestedPermission(attrAccessMode))
					continue;

				// adiciona na lista o atributo
				List<String> values = attrField.getAsList(this);
				if (values == null)
					continue;
				
				Attribute attr = new BasicAttribute(attrField.getAttributeName());
				
				for (String value : values)
					attr.add(value);
				
				attributes.put(attr);
			}
		}

		return attributes;
	}

	/**
	 * Função que realiza a conversão de uma entrada do LDAP retornado de uma
	 * busca na árvore para um objeto Java.
	 * @param entryType O objeto do tipo {@link Class} correspondente ao tipo do
	 * objeto a ser retornado pela função. O tipo do objeto deverá uma classe
	 * que herda diretamente ou indiretamente a classe {@link LDAPEntry}.
	 * @param attributes Os atributos da entrada do LDAP que foi retornada na
	 * consulta que será convertida para o objeto.
	 * @return O objeto gerado a partir dos atributos do LDAP.
	 * @throws Exception Lança exceção quando não for possível ajustar o valor
	 * de um campo do objeto.
	 */
	public static <EntryType extends LDAPEntry> EntryType parseEntry(Class<EntryType> entryType, Attributes attributes)
			throws Exception {
		// cria uma instância do objeto
		EntryType obj = entryType.newInstance();

		// pega a lista de atributos do LDAP e os campos da classe associados
		Map<String, AttributeField> attrFields = obj.getLDAPAttributesFields();
		for (Entry<String, AttributeField> entry : attrFields.entrySet()) {

			Attribute attr = attributes.get(entry.getKey());
			if (attr != null)
			{
				AttributeField attrField = entry.getValue();
				
				// se o atributo estiver habilitado apenas para escrita
				if (!attrField.haveRequestedPermission(AttributeAccessMode.READ))
					// ignora ele
					continue;
				
				// ajusta o valor do campo
				List<String> values = new ArrayList<>();
				for (int i = 0; i < attr.size(); i++)
					values.add(attr.get(i).toString());

				attrField.set(obj, values);		
			}
		}

		return obj;
	}

	/**
	 * Função que retorna o formato da <i>query</i> usada para buscar um tipo
	 * específico de entrada do LDAP. Esta <i>query</i>
	 * deve conter um marcador de {@link String} para que ela seja acrescida de
	 * uma <i>subquery</i> na função de busca, sendo que esta última pode ser uma
	 * <i>string</i> vazia. A <i>query</i> e o marcador deverão estar no formato
	 * suportado pela função {@link String#format(String, Object...)}.
	 * <p>Exemplo de <i>query</i>: <code>"(&(objectCategory=Person)%s)"</code>
	 * pode ser para retornar todos os usuários do AD.</p>
	 * @return O formato da <i>query</i> da entrada do LDAP.
	 */
	public abstract String getEntryQueryFormat();
}
