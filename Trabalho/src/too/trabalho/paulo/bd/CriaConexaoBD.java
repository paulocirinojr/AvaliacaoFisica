package too.trabalho.paulo.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilizada para realizar a conex�o do app com o banco de dados.
 * @author Paulo
 *
 */
public class CriaConexaoBD {
		Connection conexao = null;

		/**
		 * Cria a conex�o com o banco de dados.
		 * @return Retorna o objeto {@link Connection} criado.
		 */
		public Connection conectaBD(){
			try {
				// Realiza a conex�o com o banco, utilizado o driver postgres.
				conexao = DriverManager.getConnection("jdbc:postgresql://localhost/avaliacaofisica","postgres","admin");
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("ERRO NA CONEX�O COM O BANCO DE DADOS !!!");
			}
			return conexao;
		} // conectaBD()

} // class CriaConexaoBD
