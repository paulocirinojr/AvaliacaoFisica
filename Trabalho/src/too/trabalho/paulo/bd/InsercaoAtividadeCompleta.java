package too.trabalho.paulo.bd;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javafx.scene.control.Alert.AlertType;
import too.trabalho.paulo.classes.AtividadeFisica;
import too.trabalho.paulo.classes.AtividadeCompleta;
import too.trabalho.paulo.classes.Ritmo;
import too.trabalho.paulo.classes.Aluno;

import static too.trabalho.paulo.classes.ManipulaDados.exibeAlerta;

/**
 * Classe utilizada para manipulação de dados do banco de dados baseados na <code>AtividadeCompleta</code>.
 * @author Paulo
 *
 */
public class InsercaoAtividadeCompleta {
	/**
	 * Realiza a inserção de dados no banco de dados.
	 * @param atividade Objeto que contém todos os dados necessários a serem adicionados no BD.
	 * @param conexaoBD Conexão ativa com o BD.
	 */
	public void insere(AtividadeCompleta atividade, Connection conexaoBD){
		String sql = "INSERT INTO atividade_completa(atividade, duracao, distancia, calorias, passos, velocidade_media, velocidade_maxima,"
				+ " menor_elevacao, maior_elevacao, ritmo_medio, ritmo_maximo, id_aluno, data, tempo) "
				+ "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

		PreparedStatement preparedStatement, statement;

			try {
				// Seta o statement a ser utilizado na operação.
				preparedStatement = conexaoBD.prepareStatement(sql);

				// Altera os campos (VALUES (???)) com os dados a serem inseridos.
				preparedStatement.setString(1, atividade.getExercicio());
				preparedStatement.setString(2, atividade.getDuracao());
				preparedStatement.setFloat(3, atividade.getDistancia());
				preparedStatement.setFloat(4, atividade.getCaloriasPerdidas());
				preparedStatement.setInt(5, atividade.getPassos());
				preparedStatement.setFloat(6, atividade.getVelocidadeMedia());
				preparedStatement.setFloat(7, atividade.getVelocidadeMaxima());
				preparedStatement.setDouble(8, atividade.getMenorElevacao());
				preparedStatement.setDouble(9, atividade.getMaiorElevacao());

				// Objeto LocalTime utilizado para converter o ritmo.
				LocalTime time = atividade.getRitmoMedio();
				int min = time.getMinute(), seg = time.getSecond();
				preparedStatement.setString(10, String.format("%02d:%02d", min, seg));

				time = atividade.getRitmoMaximo();
				min = time.getMinute();
				time.getSecond();
				preparedStatement.setString(11, String.format("%02d:%02d", min, seg));

				String getID = "SELECT * FROM aluno WHERE whatsapp=?";

				statement = conexaoBD.prepareStatement(getID);

				// utiliza o e-mail do cliente na busca.
				statement.setString(1, atividade.getUsuario().getWhatsapp());

				ResultSet rs = statement.executeQuery();
				int idUsuario = -1;

				if (rs.next())
					 idUsuario = rs.getInt("id");
				else{
					Aluno usuario = atividade.getUsuario();
					new InsercaoAluno().insere(usuario, conexaoBD);
					rs = statement.executeQuery();
					rs.next();
					idUsuario = rs.getInt("id");
				}

				preparedStatement.setInt(12, idUsuario);

				// Objetos Date próprios do SQL.
				Date dataEx;

				// Objetos java.sql.Date são inicializados com o método getTimeInMillis() da classe Calendar.
				dataEx = new Date(atividade.getData().getTimeInMillis());
				preparedStatement.setDate(13, dataEx);
				preparedStatement.setString(14, atividade.getTempo());

				// Executa a inserção.
				preparedStatement.execute();

				// Obtém a ID da atividade atual para iniciar a inserção de todos os ritmos do exercício.
				getID = "SELECT * FROM atividade_completa WHERE id_aluno=? AND data=? AND tempo=?";
				PreparedStatement statementRitmo;

				statementRitmo = conexaoBD.prepareStatement(getID);

				// utiliza o e-mail do cliente na busca.
				statementRitmo.setInt(1, idUsuario);
				statementRitmo.setDate(2, dataEx);
				statementRitmo.setString(3, atividade.getTempo());

				ResultSet rsRitmo = statementRitmo.executeQuery();

				rsRitmo.next();

				int idAtividade = rsRitmo.getInt("id");

				// Loop que realiza a inserção de cada ritmo do exercício.
				for (int i = 0 ; i < atividade.getRitmo().size() ; i++)
					insereRitmo(idAtividade, atividade.getRitmo().get(i), conexaoBD);

			} catch (SQLException e) {
				exibeAlerta("ERRO FATAL", "Erro na inserção dos dados no banco ! Verifique se o exercício já  foi cadastrado.", AlertType.ERROR).showAndWait();
				e.printStackTrace();
			}
	} // insere()

	/**
	 * Método utilizado em conjunto com a inserção de atividades do cliente. Insere todos os ritmos em uma tabela de ritmos.
	 * @param idAtividade ID do exercício do usuário cadastrado no banco de dados.
	 * @param ritmo Objeto ritmo que contém os dados a serem cadastrados.
	 * @param conexaoBD Conexão ativa com o banco de dados.
	 */
	private void insereRitmo(int idAtividade, Ritmo ritmo, Connection conexaoBD){
		String queryRitmo = "INSERT INTO ritmos(id_atividade, km, minuto, segundo)"
				+ "VALUES (?, ?, ?, ?)";

		PreparedStatement statementRitmo;

		try {
			// Seta o statement a ser utilizado na operação.
			statementRitmo = conexaoBD.prepareStatement(queryRitmo);

			// Altera os campos (VALUES (???)) com os dados a serem inseridos.
			statementRitmo.setInt(1, idAtividade);
			statementRitmo.setFloat(2, ritmo.getKM());
			statementRitmo.setInt(3, ritmo.getMinutos());
			statementRitmo.setInt(4, ritmo.getSegundos());

			// Executa a inserção.
			statementRitmo.execute();
		} catch (SQLException e) {
			exibeAlerta("ERRO FATAL", "Erro na inserção dos ritmos no banco !", AlertType.ERROR).showAndWait();
			e.printStackTrace();
		}
	} // insereRitmo()

	/**
	 * Método utilizado para realizar inserção de grande quantidade de dados. Recebe uma lista de objetos.
	 * @param atividadesList Lista com os objetos a serem inseridos no BD.
	 * @param conexaoBD Conexão ativa com o BD
	 */
	public static void iniciaInsercao(List<AtividadeCompleta> atividadesList, Connection conexaoBD){
		AtividadeFisica atividade;
		AtividadeCompleta at1;
		InsercaoAtividadeCompleta insercao = new InsercaoAtividadeCompleta();

		// Percorre a lista de objetos, chamando o método insere para inserir cada objeto da lista no banco.
		for (int i = 0; i < atividadesList.size() ; i++){
			atividade = atividadesList.get(i);
			if (atividade instanceof AtividadeCompleta){
				at1 = (AtividadeCompleta) atividade;
				insercao.insere(at1, conexaoBD);
			}
		}
	} // iniciaInsercao()

	/**
	 * Retorna todos os registros de <code>AtividadeCompleta</code> do banco de dados.
	 * @param conexaoBD Conexão ativa com o banco de dados.
	 * @return atividadesList Lista com todos objetos obtidos do banco de dados.
	 */
	public static List<AtividadeCompleta> listaAtividades(Connection conexaoBD){
		ArrayList<AtividadeCompleta> atividadesList = new ArrayList<>();
		AtividadeCompleta atividade;
		Aluno usuario;
		String requisicaoDados = "SELECT * FROM atividade_completa";

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
				atividade = new AtividadeCompleta();
				usuario = new Aluno();

				atividade.setCaloriasPerdidas(rs.getFloat("calorias"));
				Calendar dataExcercicio = Calendar.getInstance(), dataNascimento;
				dataExcercicio.setTime(rs.getDate("data"));
				atividade.setData(dataExcercicio);
				atividade.setDistancia(rs.getFloat("distancia"));
				atividade.setDuracao(rs.getString("duracao"));
				atividade.setExercicio(rs.getString("atividade"));
				atividade.setMaiorElevacao(rs.getFloat("maior_elevacao"));
				atividade.setMenorElevacao(rs.getFloat("menor_elevacao"));
				atividade.setPassos(rs.getInt("passos"));

				String ritmo = rs.getString("ritmo_medio");
				String min, seg;

				min = ritmo.substring(0,ritmo.indexOf(":"));
				seg = ritmo.substring(ritmo.indexOf(":")+1,ritmo.length());

				LocalTime time = LocalTime.of(0, Integer.parseInt(min), Integer.parseInt(seg));

				atividade.setRitmoMedio(time);

				ritmo = rs.getString("ritmo_maximo");
				min = ritmo.substring(0,ritmo.indexOf(":"));
				seg = ritmo.substring(ritmo.indexOf(":")+1,ritmo.length());
				time = LocalTime.of(0, Integer.parseInt(min), Integer.parseInt(seg));

				atividade.setRitmoMaximo(time);
				atividade.setTempo(rs.getString("tempo"));

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
	 * Retorna todos os registros de <code>AtividadeCompleta</code> do banco de dados de acordo com o nome do cliente
	 * recebido como parâmetro..
	 * @param nome Nome do cliente a ser buscado no banco de dados.
	 * @param conexaoBD Conexão ativa com o banco de dados.
	 * @return atividadesList Lista com todas as atividades obtidas na consulta.
	 */
	public static List<AtividadeCompleta> listaAtividadesPorCliente(String nome, Connection conexaoBD){
		ArrayList<AtividadeCompleta> atividadesList = new ArrayList<>();
		AtividadeCompleta atividade;
		Aluno usuario = null;
		SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
		String requisicaoDados = "SELECT * FROM atividade_completa WHERE id_aluno=?",
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

				statement.setInt(1, rs.getInt("id"));
				rs.close();

				rs = statement.executeQuery();
			}



			// Loop ativo enquanto o ResultSet recebe objetos válidos.
			while (rs.next()){
				// Cria os objetos a serem preenchidos.
				atividade = new AtividadeCompleta();

				atividade.setUsuario(usuario);

				Calendar dataEx = Calendar.getInstance();
				data.format(rs.getDate("data"));
				Date date = new Date(data.getCalendar().getTimeInMillis());
				dataEx.setTime(date);

				atividade.setData(dataEx);
				atividade.setTempo(rs.getString("tempo"));
				atividade.setDuracao(rs.getString("duracao"));
				atividade.setDistancia(rs.getFloat("distancia"));
				atividade.setCaloriasPerdidas(rs.getFloat("calorias"));
				atividade.setPassos(rs.getInt("passos"));
				atividade.setVelocidadeMedia(rs.getFloat("velocidade_media"));
				atividade.setVelocidadeMaxima(rs.getFloat("velocidade_maxima"));
				atividade.setMenorElevacao(rs.getFloat("menor_elevacao"));
				atividade.setMaiorElevacao(rs.getFloat("maior_elevacao"));
				atividade.setExercicio(rs.getString("atividade"));

				String ritmo = rs.getString("ritmo_medio");
				String min, seg;

				min = ritmo.substring(0,ritmo.indexOf(":"));
				seg = ritmo.substring(ritmo.indexOf(":")+1,ritmo.length());

				LocalTime time = LocalTime.of(0, Integer.parseInt(min), Integer.parseInt(seg));

				atividade.setRitmoMedio(time);

				ritmo = rs.getString("ritmo_maximo");
				min = ritmo.substring(0,ritmo.indexOf(":"));
				seg = ritmo.substring(ritmo.indexOf(":")+1,ritmo.length());

				time = LocalTime.of(0, Integer.parseInt(min), Integer.parseInt(seg));

				// Adiciona o objeto na lista de atividades.
				atividadesList.add(atividade);
			}
		} catch (SQLException e) {
			exibeAlerta("ERRO FATAL", "Erro na requisição dos dados no banco !", AlertType.ERROR).showAndWait();
			e.printStackTrace();
		}
		return atividadesList;
	} // listaAtividades()

	/**
	 * Obtém a ID do exercício de acordo com a chaves primárias <code>email, data e tempo</code>.
	 * @param atividade Objeto onde serão obtidas as chaves de busca.
	 * @param conexaoBD Conexão ativa com o banco de dados.
	 * @return Retorna a ID do exercício.
	 */
	public static int idAtividade(AtividadeCompleta atividade, Connection conexaoBD){
		String sql = "SELECT * FROM atividade_completa WHERE duracao=? AND data=? AND tempo=?";
		PreparedStatement statement;
		ResultSet rs;

		try {
			// Seta o statement a ser utilizado na operação.
			statement = conexaoBD.prepareStatement(sql);

			// Altera os campos ((???)) com os dados a serem inseridos.
			statement.setString(1, atividade.getDuracao());
			SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
			date.format(atividade.getData().getTime());
			Date data = new Date(date.getCalendar().getTimeInMillis());

			statement.setDate(2, data);
			statement.setString(3, atividade.getTempo());

			rs = statement.executeQuery();

			// Verifica se a busca foi bem sucedida.
			if (rs.next())
				return rs.getInt("id");

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	} // idUsuario()

	/**
	 * Retorna uma lista com todos os ritmos de acordo com a id do exercício.
	 * @param idExercicio ID utilizada na busca.
	 * @param conexaoBD Conexão ativa com o banco de dados.
	 * @return ritmosList Lista com os objetos <code>Ritmo</code> da atividade.
	 */
	public static List<Ritmo> listaRitmos (int idExercicio, Connection conexaoBD){
		String sql = "SELECT * FROM ritmos WHERE id_atividade=?";
		PreparedStatement statement;
		ResultSet rs;
		List<Ritmo> ritmosList = new ArrayList<Ritmo>();
		Ritmo ritmo;

		System.out.println("id = " + idExercicio);

		try {
			// Seta o statement a ser utilizado na operação.
			statement = conexaoBD.prepareStatement(sql);

			statement.setInt(1, idExercicio);

			rs = statement.executeQuery();

			// Loop ativo enquanto o ResultSet recebe objetos válidos.
			while (rs.next()){
				System.out.println("entrou");
				// Cria o objeto a ser preenchido.
				ritmo = new Ritmo();

				ritmo.setKM(rs.getFloat("km"));
				ritmo.setMinutos(rs.getInt("minuto"));
				ritmo.setSegundos(rs.getInt("segundo"));

				// Adiciona o objeto na lista.
				ritmosList.add(ritmo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ritmosList;
	} // listaRitmos()
} // class InsercaoAtividade1
