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
		
		// formata o nome do usuário do AD
		String username = String.format("%s@%s",
				this.prop.getADTestUserAccountName(),
				this.prop.getADTestUserDomain());

		// cria o objeto da conexão
		ADTree adTree = new ADTree(
				this.prop.getLDAPUrl(),
				this.prop.getLDAPSearchBase(),
				// conecta com um usuário do AD
				username,
				this.prop.getADTestUserPassword());

		try {
			// realiza a conexão
			adTree.connect();

			// desconecta
			adTree.disconnect();

		} catch (AuthenticationException ex) {
			// só para debugar
			throw ex;
		}
	}

}
