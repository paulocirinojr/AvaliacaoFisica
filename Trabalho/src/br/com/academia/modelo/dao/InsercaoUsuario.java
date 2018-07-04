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
	} // listaUsers()

	public void remove(Usuario usuario, Connection conexao){
		String sql = "DELETE FROM usuario WHERE usuario=?";

		try {

			PreparedStatement statement = conexao.prepareStatement(sql);

			statement.setString(1, usuario.getUsuario());

			statement.execute();
		} catch (SQLException e) {
			exibeAlerta("ERRO", "Erro na remoção do usuário do BD.", AlertType.ERROR).showAndWait();
		}
	} // remove()

	public boolean atualiza(Usuario usuarioAntigo, Usuario usuarioNovo, Connection conexao){
		String sql = "UPDATE usuario SET usuario=?, senha=?, papel=? WHERE usuario=?";

		try {
			PreparedStatement statement = conexao.prepareStatement(sql);

			statement.setString(1, usuarioNovo.getUsuario());
			statement.setString(2, usuarioNovo.getSenha());
			statement.setString(3, usuarioNovo.getPapel());
			statement.setString(4, usuarioAntigo.getUsuario());

			statement.execute();
		} catch (SQLException e) {
			exibeAlerta("ERRO", "Erro na atualização do usuário no BD !", AlertType.ERROR).showAndWait();
		}
		return true;
	} // atualiza()

	public boolean verificaUsuario(Usuario usuario, Connection conexao){
		String sql = "SELECT * FROM usuario WHERE usuario=?";

		try {
			PreparedStatement statement = conexao.prepareStatement(sql);
			statement.setString(1, usuario.getUsuario());

			ResultSet rs = statement.executeQuery();

			if (rs.next()){
				if (rs.getString("senha").equals(usuario.getSenha()))
					return true;
			}
		} catch (SQLException e) {
			exibeAlerta("ERRO", "Erro na verificação do usuário!", AlertType.ERROR).showAndWait();
		}
		return false;
	}

} // class InsercaoUsuario
