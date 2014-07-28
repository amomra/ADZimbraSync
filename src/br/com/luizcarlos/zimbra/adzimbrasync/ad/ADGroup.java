package br.com.luizcarlos.zimbra.adzimbrasync.ad;

import java.util.List;
import java.util.UUID;

import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPAttribute;
import br.com.luizcarlos.zimbra.adzimbrasync.ldap.LDAPEntry;

@LDAPEntry( uidAttribute = "objectGUID")
public class ADGroup {

	@LDAPAttribute
	private UUID objectGUID;
	
	@LDAPAttribute
	private String cn;
	
	@LDAPAttribute
	private String mail;
	
	@LDAPAttribute( name = "member" )
	private List<ADUser> members;
	
	public ADGroup() {
		
	}

	public UUID getObjectGUID() {
		return objectGUID;
	}

	public void setObjectGUID(UUID objectGUID) {
		this.objectGUID = objectGUID;
	}

	public String getCn() {
		return cn;
	}

	public void setCn(String cn) {
		this.cn = cn;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public List<ADUser> getMembers() {
		return members;
	}

}
