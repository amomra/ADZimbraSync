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
	READ_WRITE;
	
	/**
	 * Informa se o modo de acesso atual possui o nível de permissão solicitado.
	 * @param perm A permissão solicitada. Caso a permissão solicitada seja
	 * {@link AttributeAccessMode#READ_WRITE}, será verificado se o modo de
	 * acesso permite leitura e escrita.
	 * @return Retorna <code>true</code> se o modo de acesso atual permite o
	 * nível de permissão solicitado. Caso contrário, retorna <code>false</code>.
	 */
	public boolean haveRequestedPermission(AttributeAccessMode perm) {
		// se forem iguais ou se estiver habilitado para leitura e escrita
		if (this.equals(READ_WRITE) || this.equals(perm))
			return true;
		return false;
	}
}
