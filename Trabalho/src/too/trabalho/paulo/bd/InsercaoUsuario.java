package too.trabalho.paulo.bd;

import static too.trabalho.paulo.classes.ManipulaDados.exibeAlerta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.Alert.AlertType;
import too.trabalho.paulo.classes.Aluno;
import too.trabalho.paulo.classes.Usuario;

public class InsercaoUsuario {
	public void insere(Usuario usuario, Connection conexaoBD){
		String sql = "INSERT INTO usuario(usuario, senha, papel) VALUES (?,?,?)";

		PreparedStatement preparedStatement;

			try {
				// Seta o statement a ser utilizado na operação.
				preparedStatement = conexaoBD.prepareStatement(sql);

				// Altera os campos (VALUES (???)) com os dados a serem inseridos.
				preparedStatement.setString(1, usuario.getUsuario());
				preparedStatement.setString(2, usuario.getSenha());
				preparedStatement.setString(3, usuario.getPapel());

				// Executa a operação.
				preparedStatement.execute();
			} catch (SQLException e) {
				exibeAlerta("ERRO", "Erro na inserção do usuário no banco !", AlertType.ERROR).showAndWait();
				e.printStackTrace();
			}
	} // insere()

	public List<Usuario> listaUsers(Connection conexao){
		List<Usuario> usersList = new ArrayList<Usuario>();
		String sql = "SELECT * FROM usuario";
		Usuario user;

		try {
			PreparedStatement statement = conexao.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();

			while (rs.next()){
				user = new Usuario();

				user.setUsuario(rs.getString("usuario"));
				user.setSenha(rs.getString("senha"));
				user.setPapel(rs.getString("papel"));

				usersList.add(user);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return usersList;
	}

}
