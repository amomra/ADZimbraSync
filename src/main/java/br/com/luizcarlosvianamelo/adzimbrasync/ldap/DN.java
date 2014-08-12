package br.com.luizcarlosvianamelo.adzimbrasync.ldap;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Classe que representa um <i>distinguished name</i> - DN do LDAP. Um DN
 * corresponde a uma chave utilizada para referenciar uma única entrada do LDAP.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class DN {

	private List<RDN> rdns;

	/**
	 * Construtor da classe.
	 */
	public DN() {
		this.rdns = new ArrayList<>();
	}

	/**
	 * Função que verifica se o DN é igual a um outro objeto. Quando o objeto
	 * passado é um outro {@link DN}, é feita a comparação entre o valor textual
	 * dos dois ignorando a caixa das letras.
	 * @return Retorna <code>true</code> caso os DNs forem igual. Caso
	 * contrário, retorna <code>false</code>. Esta função sempre retorna
	 * <code>false</code> quando o objeto passado não for um DN.
	 */
	@Override
	public boolean equals(Object obj) {
		// retorna false caso o objeto passado for null
		// verifica se o objeto é do tipo DN
		if (obj instanceof DN) {
			// compara as string desconsiderando a caixa dos caracteres
			return this.toString().equalsIgnoreCase(obj.toString());
		}
		return false;
	}

	/**
	 * Função que retorna a representação textual do DN.
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		// adiciona o primeiro se existir
		if (this.getNumberOfLevels() > 0) {
			builder.append(this.getLeafRDN().toString());

			for (int i = 1; i < this.getNumberOfLevels(); i++) {
				builder.append(String.format(",%s", this.rdns.get(i).toString()));
			}
		}

		return builder.toString();
	}

	/**
	 * Retorna o {@link RDN} folha que está contido no DN, ou seja, o nó no
	 * nível mais baixo.
	 */
	public RDN getLeafRDN() {
		if (this.getNumberOfLevels() > 0)
			return this.rdns.get(0);
		return null;
	}
	
	/**
	 * Função que inclui um RDN no nível especificado.
	 * @param rdn O {@link RDN} a ser inserido.
	 * @param level O nível onde RDN será inserido, onde o valor zero representa
	 * o nível mais baixo. Após a inserção, o nível dos elementos acima do
	 * elemento inserido será incrementado.
	 */
	public void insertAtLevel(RDN rdn, int level) {
		this.rdns.add(level, rdn);
	}

	/**
	 * Função que retorna o RDN contido no nível desejado.
	 * @param level O nível do RDN a ser retornado, sendo que o nível mais baixo
	 * é zero.
	 * @return O {@link RDN} incluso no nível. Caso o nível passado não for
	 * válido, é retornado o valor <code>null</code>.
	 */
	public RDN getLevel(int level) {
		if (this.getNumberOfLevels() > level)
			return this.rdns.get(level);
		return null;
	}

	/**
	 * Função que retorna o número de níveis de RDN.
	 */
	public int getNumberOfLevels() {
		return this.rdns.size();
	}

	/**
	 * Função que, a partir de uma string contendo um DN, gera um objeto
	 * {@link DN} correspondente.
	 * @param dn O DN a ser utilizado para geração do objeto.
	 * @return Retorna o objeto {@link DN} correspondente. Caso o DN passado
	 * for inválido, a função retorna <code>false</code>. 
	 */
	public static DN parse(String dn) {
		
		// faz a separação do DN
		StringTokenizer tokenizer = new StringTokenizer(dn, ",");
		if (tokenizer.countTokens() > 0) {
			DN newDn = new DN();
			
			// cria os RDN
			while (tokenizer.hasMoreTokens()) {
				// verifica a posição do símbolo '='
				String token = tokenizer.nextToken();
				int pos = token.indexOf('=');
				// se não tiver o símbolo, retorna null porque o DN está inválido
				if (pos < 0)
					return null;
				// pega o nome e  o valor do atributo
				String attrName = token.substring(0, pos);
				String attrValue = token.substring(pos + 1);
				
				newDn.rdns.add(new RDN(attrName, attrValue));
			}
			
			return newDn;
		}
		return null;
	}
	
}
