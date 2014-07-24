package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import java.util.List;
import java.util.UUID;

public class ADUser {

	private UUID objectGUID;
	private String sAMAccountName;
	private String givenName;
	private String name;
	private String sn;
	private String userPrincipalName;
	private String mail;
	
	private List<ADGroup> memberOfGroups;
	
	public ADUser() {
	}

	public UUID getObjectGUID() {
		return objectGUID;
	}

	public void setObjectGUID(UUID objectGUID) {
		this.objectGUID = objectGUID;
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

	public List<ADGroup> getMemberOfGroups() {
		return this.memberOfGroups;
	}
}
