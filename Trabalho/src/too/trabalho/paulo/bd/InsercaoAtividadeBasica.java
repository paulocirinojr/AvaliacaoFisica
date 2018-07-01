package too.trabalho.paulo.bd;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javafx.scene.control.Alert.AlertType;
import too.trabalho.paulo.classes.AtividadeFisica;
import too.trabalho.paulo.classes.AtividadeBasica;
import too.trabalho.paulo.classes.Aluno;

import static too.trabalho.paulo.classes.ManipulaDados.exibeAlerta;

/**
 * Classe com os métodos necessários para manipulação dos dados no BD.
 * @author Paulo
 *
 */
public class InsercaoAtividadeBasica {

	/**
	 * Realiza a inserção do objeto na tabela.
	 * @param atividade Objeto com os dados da atividade.
	 * @param conexaoBD Conexão ativa com o banco de dados.
	 */
	public void insere(AtividadeBasica atividade, Connection conexaoBD){
		String sql = "INSERT INTO atividade_basica(id_aluno, data, tempo, atividade, duracao, distancia, calorias, passos)"
				+ " VALUES (?,?,?,?,?,?,?,?)";

		PreparedStatement preparedStatement;

			try {
				// Seta o statement a ser utilizado na operação.
				preparedStatement = conexaoBD.prepareStatement(sql);

				// Altera os campos (VALUES (???)) com os dados a serem inseridos.
				String getID = "SELECT * FROM aluno WHERE whatsapp=?";
				PreparedStatement statement;

				statement = conexaoBD.prepareStatement(getID);

				// utiliza o e-mail do cliente na busca.
				statement.setString(1, atividade.getUsuario().getWhatsapp());

				ResultSet rs = statement.executeQuery();

				rs.next();

				int idUsuario = rs.getInt("id");

				preparedStatement.setInt(1, idUsuario);

				Date dataEx;

				dataEx = new Date(atividade.getData().getTimeInMillis());
				preparedStatement.setDate(2, dataEx);
				preparedStatement.setString(3, atividade.getTempo());
				preparedStatement.setString(4, atividade.getExercicio());
				preparedStatement.setString(5, atividade.getDuracao());
				preparedStatement.setFloat(6, atividade.getDistancia());
				preparedStatement.setFloat(7, atividade.getCaloriasPerdidas());
				preparedStatement.setInt(8, atividade.getPassos());

				// Executa a operação.
				preparedStatement.execute();
			} catch (SQLException e) {
				exibeAlerta("ERRO", "Erro na inserção da atividade no dados no banco !", AlertType.ERROR).showAndWait();
				e.printStackTrace();
			}
	} // insere()

	/**
	 * Realiza a inserção de vários objetos, por meio de um <code>List</code>.
	 * @param list Lista de atividades a serem inseridas no banco de dados.
	 * @param conexaoBD Conexão ativa com o BD
	 */
	public static void iniciaInsercao(List<AtividadeBasica> list, Connection conexaoBD){
		AtividadeFisica atividade;
		AtividadeBasica at2;
		InsercaoAtividadeBasica insercao = new InsercaoAtividadeBasica();

		// Percorre a lista de objetos, chamando o método insere para inserir cada objeto da lista no banco.
		for (int i = 0; i < list.size() ; i++){
			atividade = list.get(i);
			if (atividade instanceof AtividadeBasica){
				at2 = (AtividadeBasica) atividade;
				insercao.insere(at2, conexaoBD);
			}
		}
	} // iniciaInsercao()

	/**
	 * Retorna uma lista de objetos <code>AtividadeBasica</code> com todas as atividades cadastradas no banco de dados.
	 * @param conexaoBD Conexão ativa com o banco de dados.
	 * @return atividadesList <code>List</code> com os objetos obtidos na consulta.
	 */
	public static List<AtividadeBasica> listaAtividades(Connection conexaoBD){
		ArrayList<AtividadeBasica> atividadesList = new ArrayList<>();
		AtividadeBasica atividade;
		Aluno usuario;
		String requisicaoDados = "SELECT * FROM atividade_basica";

		try {
			// Seta o statement a ser utilizado na operação.
			PreparedStatement statement = conexaoBD.prepareStatement(requisicaoDados), statementAluno;
			/*O objeto ResultSet recebe os dados requisitados pela operação SELECT do banco de dados.
			 * O método executeQuery solicita a execução do comando.
			 */
			ResultSet rs = statement.executeQuery(), rsAluno;

			// Loop ativo enquanto o ResultSet recebe objetos válidos.
			while (rs.next()){
				// Cria os objetos a serem preenchidos.
				atividade = new AtividadeBasica();
				usuario = new Aluno();

				atividade.setTempo(rs.getString("tempo"));
				atividade.setDuracao(rs.getString("duracao"));
				atividade.setDistancia(rs.getFloat("distancia"));
				atividade.setCaloriasPerdidas(rs.getFloat("calorias"));
				atividade.setPassos(rs.getInt("passos"));
				atividade.setExercicio(rs.getString("atividade"));

				Calendar dataExcercicio = Calendar.getInstance(), dataNascimento;
				dataExcercicio.setTime(rs.getDate("data"));
				atividade.setData(dataExcercicio);


				requisicaoDados = "SELECT * FROM aluno WHERE id=?";
				statementAluno = conexaoBD.prepareStatement(requisicaoDados);
				statementAluno.setInt(1, rs.getInt("id_aluno"));
				rsAluno = statementAluno.executeQuery();
				rsAluno.next();

				usuario.setNome(rsAluno.getString("nome"));
				usuario.setSexo(rsAluno.getString("sexo"));
				usuario.setAltura(rsAluno.getFloat("altura"));
				usuario.setPeso(rsAluno.getFloat("peso"));
				usuario.setEmail(rsAluno.getString("email"));
				usuario.setCpf(rsAluno.getString("cpf"));
				usuario.setWhatsapp(rsAluno.getString("whatsapp"));

				dataNascimento = Calendar.getInstance();
				dataNascimento.setTime(rsAluno.getDate("datanascimento"));
				usuario.setDataNascimento(dataNascimento);

				atividade.setUsuario(usuario);

				// Insere a atividade na lista.
				atividadesList.add(atividade);
			}

		} catch (SQLException e) {
			exibeAlerta("ERRO FATAL", "Erro na requisição dos dados no banco !", AlertType.ERROR).showAndWait();
			e.printStackTrace();
		}
		return atividadesList;
	} // listaAtividades()

	/**
	 * Retorna uma lista com todas as atividades de um cliente cadastrado no banco de dados.
	 * @param email <code>String</code> com a chave da pesquisa no banco.
	 * @param conexaoBD Conexão ativa com o banco de dados.
	 * @return atividadesList <code>List</code> com os objetos obtidos na consulta.
	 */
	public static List<AtividadeBasica> listaAtividadesPorCliente(String nome, Connection conexaoBD){
		ArrayList<AtividadeBasica> atividadesList = new ArrayList<>();
		AtividadeBasica atividade;
		Aluno usuario = null;
		int idAluno;
		String requisicaoDados = "SELECT * FROM atividade_basica WHERE id_aluno=?",
				requisicaoAluno = "SELECT * FROM aluno WHERE nome=?";

		try {
			// Seta o statement a ser utilizado na operação.
			PreparedStatement statementAluno = conexaoBD.prepareStatement(requisicaoAluno),
					   statement = conexaoBD.prepareStatement(requisicaoDados);

			statementAluno.setString(1, nome);

			/*O objeto ResultSet recebe os dados requisitados pela operação SELECT do banco de dados.
			 * O método executeQuery solicita a execução do comando.
			 */
			ResultSet rs = statementAluno.executeQuery();

			if (rs.next()){
				usuario = new Aluno();

				// Altera cada dado do objeto com base nos resultados da busca no BD.
				usuario.setNome(rs.getString("nome"));
				usuario.setSexo(rs.getString("sexo"));
				usuario.setAltura(rs.getFloat("altura"));
				usuario.setPeso(rs.getFloat("peso"));
				Calendar dataNasc = Calendar.getInstance();
				dataNasc.setTime(rs.getDate("datanascimento"));
				usuario.setDataNascimento(dataNasc);
				usuario.setEmail(rs.getString("email"));
				usuario.setCpf(rs.getString("cpf"));
				usuario.setWhatsapp(rs.getString("whatsapp"));

				idAluno = rs.getInt("id");
				statement.setInt(1, idAluno);
				rs.close();

				rs = statement.executeQuery();
			}

			// Loop ativo enquanto o ResultSet recebe objetos válidos.
			while (rs.next()){
				// Cria os objetos a serem preenchidos.
				atividade = new AtividadeBasica();

				atividade.setTempo(rs.getString("tempo"));
				atividade.setDuracao(rs.getString("duracao"));
				atividade.setDistancia(rs.getFloat("distancia"));
				atividade.setCaloriasPerdidas(rs.getFloat("calorias"));
				atividade.setPassos(rs.getInt("passos"));
				atividade.setExercicio(rs.getString("atividade"));

				Calendar dataExercicio = Calendar.getInstance();
				dataExercicio.setTime(rs.getDate("data"));
				atividade.setData(dataExercicio);

				atividade.setUsuario(usuario);

				// Insere a atividade na lista.
				atividadesList.add(atividade);
			}

		} catch (SQLException e) {
			exibeAlerta("ERRO FATAL", "Erro na requisição dos dados no banco !", AlertType.ERROR).showAndWait();
			e.printStackTrace();
		}
		return atividadesList;
	} // listaAtividadesPorCliente()

} // class InsercaoAtividade2
