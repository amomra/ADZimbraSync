package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import java.util.Date;
import java.util.List;

import javax.naming.directory.SearchResult;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPConverter;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPDateParser;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPEntry;

/**
 * Classe que representa uma entrada da �rvore LDAP do Active Directory. Esta
 * entrada pode representar v�rias entidades do AD como, por exemplo,
 * computadores, usu�rios e grupos. Esta classe cont�m os atributos da �rvore
 * que s�o comuns aos v�rios tipos de entidades suportadas pelo AD. Um objeto
 * desta classe pode ser obtido a partir do resultado de uma consulta do LDAP
 * atrav�s da chamada da fun��o de convers�o
 * {@link LDAPConverter#convert(Class, SearchResult) convert}.
 * 
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADEntry extends LDAPEntry {
	
	@LDAPAttribute
	protected String distinguishedName;
	
	@LDAPAttribute
	protected String sAMAccountName;
	
	@LDAPAttribute
	protected String cn;
	
	@LDAPAttribute
	protected String name;
	
	@LDAPAttribute
	protected String mail;
	
	@LDAPAttribute( name = "memberOf" )
	protected List<String> memberOfGroups;
	
	@LDAPAttribute( attributeParser = LDAPDateParser.class )
	protected Date whenChanged;
	
	/**
	 * Construtor padr�o da classe. Os atributos desta s�o inicializadas com o
	 * valor padr�o <code>null</code>.
	 */
	public ADEntry() {
		
	}
	
	/**
	 * Retorna o DN da entrada do AD.
	 */
	public String getDistinguishedName() {
		return distinguishedName;
	}

	/**
	 * Ajusta o DN da entrada do AD.
	 */
	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	/**
	 * Retorna o nome da conta associada a entrada.
	 */
	public String getsAMAccountName() {
		return sAMAccountName;
	}

	/**
	 * Ajusta o nome da conta associada a entrada.
	 */
	public void setsAMAccountName(String sAMAccountName) {
		this.sAMAccountName = sAMAccountName;
	}

	/**
	 * Retorna o nome comun da entrada.
	 */
	public String getCn() {
		return cn;
	}

	/**
	 * Ajusta o nome comun da entrada.
	 */
	public void setCn(String cn) {
		this.cn = cn;
	}

	/**
	 * Retorna o nome da entrada.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Ajusta o nome da entrada.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Retorna o endere�o de e-mail da entrada.
	 */
	public String getMail() {
		return mail;
	}

	/**
	 * Ajusta o endere�o de e-mail da entrada.
	 */
	public void setMail(String mail) {
		this.mail = mail;
	}

	/**
	 * Retorna a lista de DNs dos grupos aos quais a entrada pertence.
	 */
	public List<String> getMemberOfGroups() {
		return memberOfGroups;
	}

	/**
	 * Ajusta a lista de DNs dos grupos aos quais a entrada pertence.
	 */
	public void setMemberOfGroups(List<String> memberOfGroups) {
		this.memberOfGroups = memberOfGroups;
	}
	
	/**
	 * Retorna a data e hor�rio da �ltima modifica��o da entrada.
	 */
	public Date getWhenChanged() {
		return whenChanged;
	}

	/**
	 * Ajusta a data e hor�rio da �ltima modifica��o da entrada.
	 */
	public void setWhenChanged(Date whenChanged) {
		this.whenChanged = whenChanged;
	}

	/**
	 * Fun��o privada que verifica se a entrada pertence a um determinado grupo
	 * a partir do seu DN.
	 * @param groupDn O DN do grupo a ser checado.
	 * @return Retorna <code>true</code> caso a entrada pertencer ao grupo. Caso
	 * contr�rio, retorna <code>false</code>.
	 */
	private boolean isMemberOf(String groupDn) {
		// verifica a lista de grupos aos quais este grupo pertence
		if (this.memberOfGroups != null) {
			for (String memberOfGroupDn : this.memberOfGroups)
				// se o DN estiver contido na lista
				if (memberOfGroupDn.equals(groupDn))
					return true;
		}
		return false;
	}

	/**
	 * Fun��o que verifica se a entrada pertence ao grupo.
	 * @param group O grupo a ser checado.
	 * @return Retorna <code>true</code> caso a entrada pertencer ao grupo. Caso
	 * contr�rio, retorna <code>false</code>.
	 */
	public boolean isMemberOf(ADGroup group) {
		// verifica se o DN do grupo est� na lista
		return this.isMemberOf(group.getDistinguishedName());
	}
}
