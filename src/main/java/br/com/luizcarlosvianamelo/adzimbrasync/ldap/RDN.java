package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

/**
 * Classe que representa um <i>relative distinguished name</i> - RDN do LDAP. Um
 * RDN representa um nível do DN.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class RDN {

	private String attributeName;
	private String attributeValue;

	/**
	 * Construtor da classe.
	 */
	public RDN() {
		this.attributeName = "";
		this.attributeValue = "";
	}
	
	/**
	 * Construtor parametrizado da classe. Inicializa o objeto com os valores
	 * passados.
	 * @param attributeName O nome do atributo a ser utilizado no RDN.
	 * @param attributeValue O valor do atributo.
	 */
	public RDN(String attributeName, String attributeValue) {
		this.attributeName = attributeName;
		this.attributeValue = attributeValue;
	}
	
	/**
	 * Função que retorna a representação textual do RDN;
	 */
	@Override
	public String toString() {
		if (this.attributeName.length() > 0 &&
				this.attributeValue.length() > 0)
			return String.format("%s=%s", this.attributeName, this.attributeValue);
		return "";
	}

	/**
	 * Retorna o nome do atributo utilizado no RDN.
	 */
	public String getAttributeName() {
		return attributeName;
	}

	/**
	 * Ajusta o nome do atributo utilizado no RDN.
	 */
	public void setAttributeName(String attributeName) {
		this.attributeName = attributeName;
	}

	/**
	 * Retorna o valor do atributo utilizado no RDN.
	 */
	public String getAttributeValue() {
		return attributeValue;
	}
	
	/**
	 * Ajusta o valor do atributo utilizado no RDN.
	 */
	public void setAttributeValue(String attributeValue) {
		this.attributeValue = attributeValue;
	}
}
