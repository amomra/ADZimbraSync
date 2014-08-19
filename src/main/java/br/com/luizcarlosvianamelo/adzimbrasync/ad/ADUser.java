package br.com.luizcarlosvianamelo.adzimbrasync.ad;

import javax.naming.directory.Attributes;

import br.com.luizcarlosvianamelo.adzimbrasync.ldap.AttributeAccessMode;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlosvianamelo.adzimbrasync.ldap.LDAPEntry;

/**
 * Classe que representa uma entrada do AD que simboliza um usuário. Este
 * usuário pode ser usado para fazer a autenticação na árvore do LDAP caso ele
 * tenha permissão para tal. Um objeto
 * desta classe pode ser obtido a partir do resultado de uma consulta do LDAP
 * através da chamada da função de conversão
 * {@link LDAPEntry#parseEntry(Class, Attributes) parseEntry}.
 *  
 * @author Luiz Carlos Viana Melo
 *
 */
public class ADUser extends ADEntry {

	// identificação do usuário ----------------------------
	
	@LDAPAttribute
	private String givenName;

	@LDAPAttribute
	private String initials;
	
	@LDAPAttribute( name = "sn" )
	private String surname;
	
	@LDAPAttribute
	private String displayName;
	
	@LDAPAttribute
	private String description;
	
	@LDAPAttribute
	private String info;
	
	@LDAPAttribute(
			name = "unicodePwd",
			accessMode = AttributeAccessMode.WRITE,
			raw = true )
	private byte[] password;
	
	// telefones -------------------------------------------
	
	@LDAPAttribute
	private String telephoneNumber;
	
	@LDAPAttribute
	private String homePhone;
	
	@LDAPAttribute
	private String mobile;
	
	@LDAPAttribute
	private String pager;
	
	@LDAPAttribute( name = "facsimileTelephoneNumber" )
	private String faxNumber;
	
	// endereço --------------------------------------------
	
	@LDAPAttribute
	private String streetAddress;
	
	@LDAPAttribute( name = "l")
	private String city;
	
	@LDAPAttribute( name = "st")
	private String state;
	
	@LDAPAttribute
	private String postalCode;
	
	@LDAPAttribute( name = "co" )
	private String country;
	
	// dados da empresa ------------------------------------
	
	@LDAPAttribute
	private String company;
	
	@LDAPAttribute
	private String title;

	/**
	 * Construtor da classe. Este inicializa os atributos com o valor
	 * padrão <code>null</code>.
	 */
	public ADUser() {
	}

	/**
	 * Função que retorna a string de formatação para busca dos usuários
	 * contidos na árvore do AD.
	 */
	@Override
	public String getEntryQueryFormat() {
		return "(&(objectCategory=Person)%s)";
	}

	/**
	 * Retorna o nome completo do usuário.
	 */
	public String getGivenName() {
		return givenName;
	}

	/**
	 * Ajusta o nome completo do usuário.
	 */
	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	/**
	 * Retorna as iniciais do usuário.
	 */
	public String getInitials() {
		return initials;
	}

	/**
	 * Ajusta as iniciais do usuário.
	 */
	public void setInitials(String initials) {
		this.initials = initials;
	}

	/**
	 * Retorna o sobrenome (surname) do usuário.
	 */
	public String getSurname() {
		return surname;
	}

	/**
	 * Ajusta o sobrenome (surname) do usuário.
	 */
	public void setSurname(String sn) {
		this.surname = sn;
	}

	/**
	 * Retorna o nome de exibição do usuário.
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Ajusta o nome de exibição do usuário.
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	/**
	 * Retorna a descrição do usuário.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Ajusta a descrição do usuário.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	/**
	 * Retorna as informações adicionais do usuário.
	 */
	public String getInfo() {
		return info;
	}

	/**
	 * Ajusta as informações adicionais do usuário.
	 */
	public void setInfo(String info) {
		this.info = info;
	}

	/**
	 * Retorna a senha do usuário.
	 */
	public String getPassword() {
		try {
			// codifica a senha se ela for diferente de nulo
			if (this.password != null) {
				// remove as aspas do início e fim
				String quotedPassword = new String(this.password, "UTF-16LE");
				
				return quotedPassword.substring(1, quotedPassword.length() - 1);
			}
		} catch (Exception e) {}
		return null;
	}

	/**
	 * Ajusta a senha do usuário.
	 */
	public void setPassword(String password) {
		/*
		 * A senha deverá ser codificada em UTF-16 e entre aspas para que ela
		 * seja alterada no servidor.
		 */
		try {
			// codifica a senha para o tipo suportado
			String quotedPassword = String.format("\"%s\"",password);

			this.password = quotedPassword.getBytes("UTF-16LE");
		} catch (Exception e) {
			this.password = null;
		}
	}
	
	/**
	 * Retorna o número de telefone do usuário.
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * Ajusta o número de telefone do usuário.
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	/**
	 * Retorna o número de telefone da casa do usuário.
	 */
	public String getHomePhone() {
		return homePhone;
	}

	/**
	 * Ajusta o número de telefone da casa do usuário.
	 */
	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}

	/**
	 * Retorna o número de telefone móvel do usuário.
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * Ajusta o número de telefone móvel do usuário.
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * Retorna o número do pager do usuário.
	 */
	public String getPager() {
		return pager;
	}

	/**
	 * Ajusta o número do pager do usuário.
	 */
	public void setPager(String pager) {
		this.pager = pager;
	}

	/**
	 * Retorna o número de fax do usuário.
	 */
	public String getFaxNumber() {
		return faxNumber;
	}

	/**
	 * Ajusta o número de fax do usuário.
	 */
	public void setFaxNumber(String faxNumber) {
		this.faxNumber = faxNumber;
	}
	
	/**
	 * Retorna a rua do endereço do usuário.
	 */
	public String getStreetAddress() {
		return streetAddress;
	}

	/**
	 * Ajusta a rua do endereço do usuário.
	 */
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}

	/**
	 * Retorna a cidade do endereço do usuário.
	 */
	public String getCity() {
		return city;
	}

	/**
	 * Ajusta a cidade do endereço do usuário.
	 */
	public void setCity(String city) {
		this.city = city;
	}

	/**
	 * Retorna o estado do endereço do usuário.
	 */
	public String getState() {
		return state;
	}

	/**
	 * Ajusta o estado do endereço do usuário.
	 */
	public void setState(String state) {
		this.state = state;
	}

	/**
	 * Retorna o código postal do endereço do usuário.
	 */
	public String getPostalCode() {
		return postalCode;
	}

	/**
	 * Ajusta o código postal do endereço do usuário.
	 */
	public void setPostalCode(String postalCode) {
		this.postalCode = postalCode;
	}

	/**
	 * Retorna o nome do país do usuário.
	 */
	public String getCountry() {
		return country;
	}

	/**
	 * Ajusta o nome do país do usuário.
	 */
	public void setCountry(String country) {
		this.country = country;
	}
			
	/**
	 * Retorna o nome da compania.
	 */
	public String getCompany() {
		return company;
	}

	/**
	 * Ajusta o nome da compania.
	 */
	public void setCompany(String company) {
		this.company = company;
	}
	
	/**
	 * Retorna o cargo do usuário.
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Ajusta o cargo do usuário.
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
