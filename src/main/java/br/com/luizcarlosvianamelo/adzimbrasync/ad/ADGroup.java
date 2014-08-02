package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import java.util.List;

import com.unboundid.ldap.sdk.SearchResultEntry;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPConverter;

/**
 * Classe que representa uma entrada do AD que simboliza um grupo. Este grupo
 * pode ser um grupo de segurança ou uma lista de distribuição. Um objeto
 * desta classe pode ser obtido a partir do resultado de uma consulta do LDAP
 * através da chamada da função de conversão
 * {@link LDAPConverter#convert(Class, SearchResultEntry) convert}.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADGroup extends ADEntry {
	
	@LDAPAttribute( name = "member" )
	private List<String> members;
	
	/**
	 * Construtor padrão da classe. Este inicializa os atributos com o valor
	 * padrão <code>null</code>.
	 */
	public ADGroup() {
	}

	/**
	 * Retorna a lista contendo os DNs das entradas que pertencem ao grupo.
	 */
	public List<String> getMembers() {
		return members;
	}

	/**
	 * Ajusta a lista contendo os DNs das entradas que pertencem ao grupo.
	 */
	public void setMembers(List<String> members) {
		this.members = members;
	}
	
	/**
	 * Função privada que verifica se uma entrada com o determinado DN pertence
	 * a este grupo.
	 * @param dn O DN da entrada a ser checada.
	 * @return Retorna <code>true</code> caso a entrada com o DN passado
	 * pertencer ao grupo. Caso contrário, retorna <code>false</code>.
	 */
	private boolean isMember(String dn) {
		// verifica a lista de membros do grupo
		if (this.members != null) {
			for (String memberDn : this.members)
				// se o DN estiver contido na lista
				if (memberDn.equals(dn))
					return true;
		}
		return false;
	}

	/**
	 * Função que verifica se uma entrada do AD pertence a este grupo.
	 * @param adEntry A entrada a ser checada.
	 * @return Retorna <code>true</code> caso a entrada pertencer ao grupo. Caso
	 * contrário, retorna <code>false</code>.
	 */
	public boolean isMember(ADEntry adEntry) {
		// verifica se o DN da entrada está na lista
		return this.isMember(adEntry.getDistinguishedName());
	}
}
