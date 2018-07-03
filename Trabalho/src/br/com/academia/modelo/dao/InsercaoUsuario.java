package br.com.academia.modelo.dao;

import static br.com.academia.utils.ManipulaDados.exibeAlerta;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.academia.modelo.Usuario;
import javafx.scene.control.Alert.AlertType;

public class InsercaoUsuario {
	public void insere(Usuario usuario, Connection conexaoBD){
		String sql = "INSERT INTO usuario(usuario, senha, papel) VALUES (?,?,?)";

		PreparedStatement preparedStatement;

			try {
				// Seta o statement a ser utilizado na opera��o.
				preparedStatement = conexaoBD.prepareStatement(sql);

				// Altera os campos (VALUES (???)) com os dados a serem inseridos.
				preparedStatement.setString(1, usuario.getUsuario());
				preparedStatement.setString(2, usuario.getSenha());
				preparedStatement.setString(3, usuario.getPapel());

				// Executa a opera��o.
				preparedStatement.execute();
			} catch (SQLException e) {
				exibeAlerta("ERRO", "Erro na inser��o do usu�rio no banco !", AlertType.ERROR).showAndWait();
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

	public void remove(Usuario usuario, Connection conexao){
		String sql = "DELETE FROM usuario WHERE usuario=?";

		try {

			PreparedStatement statement = conexao.prepareStatement(sql);

			statement.setString(1, usuario.getUsuario());

			statement.execute();
		} catch (SQLException e) {
			exibeAlerta("ERRO", "Erro na remo��o do usu�rio do BD.", AlertType.ERROR).showAndWait();
		}
	}

}