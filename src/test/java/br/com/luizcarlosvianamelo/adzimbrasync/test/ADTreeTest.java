package br.com.luizcarlosvianamelo.adzimbrasync.test;

import java.io.IOException;

import javax.naming.AuthenticationException;

import org.junit.Test;

import br.com.luizcarlosvianamelo.adzimbrasync.ad.ADTree;

public class ADTreeTest {

	private TestProperties prop;

	public ADTreeTest() throws IOException {
		// carrega as propriedades do teste
		this.prop = new TestProperties();
	}
	
	@Test
	public void testConnect() throws Exception {
		
		// formata o nome do usu�rio do AD
		String username = String.format("%s@%s",
				this.prop.getADTestUserAccountName(),
				this.prop.getADTestUserDomain());

		// cria o objeto da conex�o
		ADTree adTree = new ADTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				// conecta com um usu�rio do AD
				username,
				this.prop.getADTestUserPassword());

		try {
			// realiza a conex�o
			adTree.connect();

			// desconecta
			adTree.disconnect();

		} catch (AuthenticationException ex) {
			// s� para debugar
			throw ex;
		}
	}

}
