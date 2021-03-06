package br.com.academia.modelo.dao;

import static br.com.academia.utils.ManipulaDados.exibeAlerta;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.academia.modelo.Aluno;
import javafx.scene.control.Alert.AlertType;

public class InsercaoAluno {
	public void insere(Aluno aluno, Connection conexaoBD){
		String sql = "INSERT INTO public.aluno(nome, sexo, altura, peso, datanascimento, email, cpf, whatsapp)" +
							 "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

		PreparedStatement preparedStatement;

			try {
				// Seta o statement a ser utilizado na opera��o.
				preparedStatement = conexaoBD.prepareStatement(sql);

				// Altera os campos (VALUES (???)) com os dados a serem inseridos.
				preparedStatement.setString(1, aluno.getNome());
				preparedStatement.setString(2, aluno.getSexo());
				preparedStatement.setFloat(3, aluno.getAltura());
				preparedStatement.setFloat(4, aluno.getPeso());

				Date dataNascimento;

				dataNascimento = new Date(aluno.getDataNascimento().getTimeInMillis());

				preparedStatement.setDate(5, dataNascimento);
				preparedStatement.setString(6, aluno.getEmail());
				preparedStatement.setString(7, aluno.getCpf());
				preparedStatement.setString(8, aluno.getWhatsapp());

				// Executa a opera��o.
				preparedStatement.execute();
			} catch (SQLException e) {
				exibeAlerta("ERRO", "Erro na inser��o do aluno no banco !", AlertType.ERROR).showAndWait();
				e.printStackTrace();
			}
	}

	public List<Aluno> listaAlunos(Connection conexao){
		List<Aluno> alunosList = new ArrayList<Aluno>();
		String sql = "SELECT * FROM aluno";
		Aluno aluno;

		try {
			PreparedStatement statement = conexao.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();

			while (rs.next()){
				aluno = new Aluno();

				aluno.setNome(rs.getString("nome"));
				aluno.setAltura(rs.getFloat("altura"));
				aluno.setCpf(rs.getString("cpf"));
				aluno.setEmail(rs.getString("email"));
				aluno.setPeso(rs.getFloat("peso"));
				aluno.setSexo(rs.getString("sexo"));
				aluno.setWhatsapp(rs.getString("whatsapp"));

				alunosList.add(aluno);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return alunosList;
	}

	public void remove(Aluno aluno, Connection conexaoBD){
		String sql = "DELETE FROM aluno WHERE whatsapp=?";
		PreparedStatement statement;
		try {
			statement = conexaoBD.prepareStatement(sql);

			statement.setString(1, aluno.getWhatsapp());

			statement.execute();
		} catch (SQLException e) {
			exibeAlerta("ERRO", "Erro na remo��o do aluno! Verifique se o mesmo est� sendo utilizado em alguma atividade"
					+ " cadastrada. As IDs de aluno fazem parte da chave prim�ria das atividades.", AlertType.ERROR).showAndWait();
		}
	} // remove()

	public Aluno pesquisaAluno (String nome, Connection conexao){
		Aluno aluno = new Aluno();
		String sql = "SELECT * FROM aluno WHERE nome=?";

		try {
			PreparedStatement statement = conexao.prepareStatement(sql);
			statement.setString(1, nome);

			ResultSet rs = statement.executeQuery();

			rs.next();

			aluno.setNome(rs.getString("nome"));
			aluno.setAltura(rs.getFloat("altura"));
			aluno.setCpf(rs.getString("cpf"));
			aluno.setEmail(rs.getString("email"));
			aluno.setPeso(rs.getFloat("peso"));
			aluno.setSexo(rs.getString("sexo"));
			aluno.setWhatsapp(rs.getString("whatsapp"));

		} catch (SQLException e) {
			exibeAlerta("ERRO", "Ocorreu um erro na busca do aluno!", AlertType.ERROR).showAndWait();
		}
		return aluno;
	} // pesquisaAluno()

	public boolean atualiza(Aluno alunoAntigo, Aluno alunoNovo, Connection conexao){
		String sql = "UPDATE aluno SET nome=?, sexo=?, altura=?, peso=?, email=?, cpf=?, whatsapp=? "
						+ "WHERE whatsapp=?";

		try {
			PreparedStatement statement = conexao.prepareStatement(sql);

			statement.setString(1, alunoNovo.getNome());
			statement.setString(2, alunoNovo.getSexo());
			statement.setFloat(3, alunoNovo.getAltura());
			statement.setFloat(4, alunoNovo.getPeso());
			statement.setString(5, alunoNovo.getEmail());
			statement.setString(6, alunoNovo.getCpf());
			statement.setString(7, alunoNovo.getWhatsapp());
			statement.setString(8, alunoAntigo.getWhatsapp());

			statement.execute();
		} catch (SQLException e) {
			exibeAlerta("ERRO", "Erro na atualiza��o do aluno no BD !", AlertType.ERROR).showAndWait();
		}
		return true;
	} // atualiza()


} // class InsercaoAluno
