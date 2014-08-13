package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

/**
 * Tipo enumerado que define o modo de acesso dos atributos no LDAP. O ajuste
 * deste nos campos das classes deve considerar o <i>schema</i> da base. 
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public enum AttributeAccessMode {
	/**
	 * Atributo habilitado apenas para leitura.
	 */
	READ,
	
	/**
	 * Atributo habilitado apenas para escrita.
	 */
	WRITE,
	
	/**
	 * Atributo habilitado para leitura e escrita.
	 */
	READ_WRITE
}
