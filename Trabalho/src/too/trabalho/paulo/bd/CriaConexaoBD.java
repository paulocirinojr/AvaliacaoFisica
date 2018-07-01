package too.trabalho.paulo.bd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilizada para realizar a conexão do app com o banco de dados.
 * @author Paulo
 *
 */
public class CriaConexaoBD {
		Connection conexao = null;

		/**
		 * Cria a conexão com o banco de dados.
		 * @return Retorna o objeto {@link Connection} criado.
		 */
		public Connection conectaBD(){
			try {
				// Realiza a conexão com o banco, utilizado o driver postgres.
				conexao = DriverManager.getConnection("jdbc:postgresql://localhost/avaliacaofisica","postgres","admin");
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println("ERRO NA CONEXÃO COM O BANCO DE DADOS !!!");
			}
			return conexao;
		} // conectaBD()

} // class CriaConexaoBD
