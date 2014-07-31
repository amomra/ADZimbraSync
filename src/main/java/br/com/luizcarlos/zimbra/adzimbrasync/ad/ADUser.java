package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import java.util.List;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPEntry;

public class ADUser extends LDAPEntry {
	
	@LDAPAttribute
	private String distinguishedName;
	
	@LDAPAttribute
	private String sAMAccountName;
	
	@LDAPAttribute
	private String givenName;
	
	@LDAPAttribute
	private String name;
	
	@LDAPAttribute
	private String sn;
	
	@LDAPAttribute
	private String userPrincipalName;
	
	@LDAPAttribute
	private String mail;
	
	@LDAPAttribute( name = "memberOf" )
	private List<String> memberOfGroups;
	
	public ADUser() {
	}

	public String getDistinguishedName() {
		return distinguishedName;
	}

	public void setDistinguishedName(String distinguishedName) {
		this.distinguishedName = distinguishedName;
	}

	public String getsAMAccountName() {
		return sAMAccountName;
	}

	public void setsAMAccountName(String sAMAccountName) {
		this.sAMAccountName = sAMAccountName;
	}

	public String getGivenName() {
		return givenName;
	}

	public void setGivenName(String givenName) {
		this.givenName = givenName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}

	public String getUserPrincipalName() {
		return userPrincipalName;
	}

	public void setUserPrincipalName(String userPrincipalName) {
		this.userPrincipalName = userPrincipalName;
	}
	
	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public List<String> getMemberOfGroups() {
		return memberOfGroups;
	}

	public void setMemberOfGroups(List<String> memberOfGroups) {
		this.memberOfGroups = memberOfGroups;
	}
}
