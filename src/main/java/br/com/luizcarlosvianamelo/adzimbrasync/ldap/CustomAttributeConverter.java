package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Interface a ser implementada pelas classes que poderão ser utilizada para
 * converter o valor de um atributo do LDAP para um objeto Java e ajustar o
 * valor do campo de um objeto com este.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public interface CustomAttributeConverter {
	
	/**
	 * Função que faz a conversão do valor do campo para uma lista de
	 * <i>strings</i> conforme a lógica definida na classe que implementa
	 * este método.
	 * @param field O campo da classe a ser convertido.
	 * @param obj O objeto que terá o valor do campo convertido.
	 * @return Retorna a lista de {@link String} com os valores do campo. Cada
	 * elemento desta contém o valor contido na lista ou vetor convertido para
	 * <i>string</i>. Caso o campo não seja uma lista, a lista a ser retornada
	 * conterá apenas um valor. Caso o valor do campo seja <code>null</code>,
	 * será retornado este valor.
	 * @throws Exception Lança exceção quando não for possível converter o valor
	 * do campo.
	 */
	public List<String> getFieldValue(Field field, Object obj) throws Exception;
	
	/**
	 * Função que faz o parser dos valores lidos do LDAP e armazena o resultado
	 * no campo desejado conforme a lógica de conversão definida pela
	 * implementação deste método.
	 * @param field O campo do objeto que irá receber o resultado da conversão.
	 * @param obj O objeto que terá o seu campo alterado com o resultado da
	 * conversão.
	 * @param values A lista de valores lidos do LDAP.
	 * @throws Exception Lança uma exceção quando não for possível ajustar o
	 * valor do campo com o valor do atributo.
	 */
	public void setFieldValue(Field field, Object obj, List<String> values) throws Exception;
}
