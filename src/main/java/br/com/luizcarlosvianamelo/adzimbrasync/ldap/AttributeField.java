package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe que representa um atributo de uma entrada do LDAP representado por
 * um campo de uma classe Java. Este deve ser utilizado para retornar ou ajustar
 * o valor de um campo de um objeto que representa uma entidade do LDAP. Quando
 * estas operações forem solicitadas, esta classe irá verificar se a propriedade
 * {@link LDAPAttribute#attributeConverter()} está ajustada com o tipo do objeto
 * que irá realizar a conversão do valor.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class AttributeField {

	private Field field;

	private CustomAttributeConverter converter;
	private String attributeName;
	private AttributeAccessMode attributeAccessMode;
	private boolean useRawValue;

	/**
	 * Construtor da classe. Este faz a associação do atributo do LDAP ao campo
	 * de uma classe.
	 * @param field O campo da classe a ser associado. Este deverá estar anotado
	 * com a anotação {@link LDAPAttribute}.
	 * @throws IllegalArgumentException Lança exceção quando o campo passado não
	 * possui a anotação {@link LDAPAttribute}. Também será lançada esta exceção
	 * quando o conversor customizado do valor do atributo não implementar a
	 * interface {@link CustomAttributeConverter}.
	 * @throws IllegalAccessException Lança esta exceção quando o tipo do objeto
	 * de conversão não for acessível do contexto atual.
	 * @throws InstantiationException Lança esta exceção quando não for possível
	 * inicializar o objeto de conversão passado.
	 */
	AttributeField(Field field)
			throws IllegalArgumentException, IllegalAccessException, InstantiationException {
		this.field = field;

		// verifica se o campo é um atributo do LDAP
		LDAPAttribute ann = this.field.getAnnotation(LDAPAttribute.class);
		if (ann == null)
			throw new IllegalArgumentException(String.format("Field \"%s\" isn't a LDAP attribute", this.field.getName()));

		// ajusta o nome do campo
		if (ann.name().length() > 0)
			this.attributeName = ann.name();
		else
			this.attributeName = this.field.getName();

		this.attributeAccessMode = ann.accessMode();
		this.useRawValue = ann.raw();

		this.converter = null;

		// pega o objeto de conversão
		Class<?> converterType = ann.attributeConverter();
		/* 
		 * Verifica se o conversor foi ajustado e se este implementa a interface
		 * de conversão
		 */
		if (converterType != LDAPAttributeConverter.class) {
			// lança exceção se não for
			if (CustomAttributeConverter.class.isAssignableFrom(converterType))
				this.converter = (CustomAttributeConverter) converterType.newInstance();
			else
				throw new IllegalArgumentException(
						String.format("Class \"%s\" must implement the \"CustomAttributeConverter\" interface",
								converterType.getName()));
		}
	}

	/**
	 * Retorna o nome do atributo ao qual o campo está associado.
	 */
	public String getAttributeName() {
		return attributeName;
	}
	
	/**
	 * Informa se deverá ser utilizado o valor bruto do atributo.
	 */
	public boolean useRawValue() {
		return this.useRawValue;
	}

	/**
	 * Informa se o modo de acesso atual possui o nível de permissão solicitado.
	 * @param perm A permissão solicitada. Caso a permissão solicitada seja
	 * {@link AttributeAccessMode#READ_WRITE}, será verificado se o modo de
	 * acesso permite leitura e escrita.
	 * @return Retorna <code>true</code> se o modo de acesso atual permite o
	 * nível de permissão solicitado. Caso contrário, retorna <code>false</code>.
	 */
	public boolean haveRequestedPermission(AttributeAccessMode perm) {
		return this.attributeAccessMode.haveRequestedPermission(perm);
	}

	/**
	 * Função que retorna o valor do campo contido no objeto passado sem
	 * conversão para o tipo {@link String}.
	 * @param obj O objeto que terá o valor do campo retornado.
	 * @return Retorna o valor do campo.
	 * @throws Exception Lança exceção quando não for possível fazer a leitura
	 * do campo desejado.
	 */
	public Object getRaw(Object obj) throws Exception {
		this.field.setAccessible(true);
		return this.field.get(obj);
	}

	/**
	 * Retorna o valor do campo contido no objeto passado como um objeto do tipo
	 * {@link String}.
	 * @param obj O objeto que terá o valor do campo retornado.
	 * @return Retorna o valor do campo como um objeto {@link String}. Caso o
	 * campo for multivalorado, apenas o primeiro valor será retornado. Caso não
	 * consiga coletar o valor do campo ou este for igual a <code>null</code>,
	 * será retornado este valor.
	 */
	public String get(Object obj) {
		// retorna a lista de valores
		List<String> value = this.getAsList(obj);
		// retorna o primeiro elemento da lista
		if (value != null && value.size() > 0)
			return value.get(0);
		return null;
	}

	/**
	 * Retorna o valor do campo contido no objeto passado como um vetor do tipo
	 * {@link String}.
	 * @param obj O objeto que terá o valor do campo retornado.
	 * @return Retorna o valor do campo como um vetor de {@link String}. Caso
	 * não consiga coletar o valor do campo ou este for igual a
	 * <code>null</code>, será retornado este valor.
	 */
	public String[] getAsArray(Object obj) {
		// retorna a lista de valores
		List<String> value = this.getAsList(obj);
		// converte para um vetor
		if (value != null)
			return value.toArray(new String[value.size()]);
		return null;
	}

	/**
	 * Retorna o valor do campo contido no objeto passado como uma lista
	 * contendo objetos do tipo {@link String}.
	 * @param obj O objeto que terá o valor do campo retornado.
	 * @return Retorna o valor do campo como uma lista de {@link String}. Caso
	 * não consiga coletar o valor do campo ou este for igual a
	 * <code>null</code>, será retornado este valor.
	 */
	public List<String> getAsList(Object obj) {
		try {
			// faz a conversão dos valores
			List<String> values = null;
			// chama o conversor customizado caso ele tenha sido ajustado
			if (this.converter != null)
				values = this.converter.getFieldValue(this.field, obj);
			// caso contrário chama o conversor padrão
			else
				values = LDAPAttributeConverter.getFieldValue(this.field, obj);

			// retorna a lista convertida
			return values;
		} catch (Exception e) {
			// não faz nada
		}

		return null;
	}

	/**
	 * Função que ajusta o valor do campo do objeto com base nos valores
	 * passados.
	 * @param obj O objeto que terá o valor do campo ajustado.
	 * @param values O vetor contendo os valores a serem ajustados. Esta função
	 * fará a conversão dos valores para o tipo suportado pelo campo.
	 * @throws Exception Lança exceção quando não for possível ajustar o valor
	 * do campo.
	 */
	public void set(Object obj, String... values) throws Exception {
		// converte o vetor para list
		this.set(obj, Arrays.asList(values));
	}

	/**
	 * Função que ajusta o valor do campo do objeto com base nos valores
	 * passados.
	 * @param obj O objeto que terá o valor do campo ajustado.
	 * @param values A lista contendo os valores a serem ajustados. Esta função
	 * fará a conversão dos valores para o tipo suportado pelo campo.
	 * @throws Exception Lança exceção quando não for possível ajustar o valor
	 * do campo.
	 */
	public void set(Object obj, List<String> values) throws Exception {
		// chama o conversor customizado caso ele tenha sido ajustado
		if (this.converter != null)
			this.converter.setFieldValue(this.field, obj, values);
		// caso contrário, chama o conversor padrão
		else
			LDAPAttributeConverter.setFieldValue(this.field, obj, values);
	}
	
	/**
	 * Função que ajusta o valor do campo do objeto com base no valor
	 * passado.
	 * @param obj O objeto que terá o valor do campo ajustado.
	 * @param value O objeto a ser ajustado no campo.
	 * @throws Exception Lança exceção quando não for possível ajustar o valor
	 * do campo.
	 */
	public void set(Object obj, Object value) throws Exception {
		List<String> values = new ArrayList<>();
		
		Class<?> valueType = value.getClass();
		
		// verifica se o objeto é um vetor
		if (valueType.isArray()) {
			// preenche a lista
			int arraySize = Array.getLength(value);
			for (int i = 0; i < arraySize; i++)
				values.add(Array.get(value, i).toString());
		}
		// se for uma lista
		else if (Iterable.class.isAssignableFrom(valueType)) {
			Iterable<?> list = (Iterable<?>) value;
			for (Object val : list)
				values.add(val.toString());
		} else
			values.add(value.toString());
		// ajusta o valor do campo
		this.set(obj, values);
	}
}
