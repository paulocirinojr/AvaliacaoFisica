package too.trabalho.paulo.app;

import static too.trabalho.paulo.classes.ManipulaDados.*;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import too.trabalho.paulo.bd.CriaConexaoBD;
import too.trabalho.paulo.bd.InsercaoAluno;
import too.trabalho.paulo.bd.InsercaoAtividadeCompleta;
import too.trabalho.paulo.bd.InsercaoUsuario;
import too.trabalho.paulo.bd.InsercaoAtividadeBasica;
import too.trabalho.paulo.classes.AtividadeFisica;
import too.trabalho.paulo.classes.ManipulaDados;
import too.trabalho.paulo.classes.Usuario;
import too.trabalho.paulo.classes.AtividadeCompleta;
import too.trabalho.paulo.classes.Aluno;
import too.trabalho.paulo.classes.AtividadeBasica;


/**
 * Classe utilizada para controlar todas as ações da interface gráfica.
 * @author Paulo
 */
public class AtividadeFisicaController {
	// Inicia a conexão do programa com o banco de dados.
	Connection conexaoBD = new CriaConexaoBD().conectaBD();

	// Variáveis estáticas utilizadas na criação dos gráficos.
	private final int NUMERO_GRAFICOS = 9, NUMERO_GRAFICOS_COMPLETOS = 6;

	// Elementos JavaFX.
    @FXML
    private Button btnImportar, btnPesquisaCliente, btnVisualizaRelatorio, btnAtualizaAluno, btnRemoveAluno, btnRemoveUsuario;
    @FXML
    private GridPane gridImportacao, gridPaneGrafico, gridDetalhes, gridBuscaAluno, gridBuscaAtividade, gridBuscaAtividadeAluno;
    @FXML
    private HBox iniciaPesquisa, hboxExercicio, relatorioBasico, hboxRelatorioDetalhado, hboxGraficoBasico, hboxGraficoLinha, hboxGraficoCompleto;
    @FXML
    private TextField campoBuscaCliente, tfPeso, tfNome, tfSexo, tfAltura, tfEmail, tfCPF, tfWpp, tfUser, tfSenha, tfPapel, tfAtividade, tfTempo, tfDuracao,
    							 tfDistancia, tfCalorias, tfPassos, tfAtividadeC, tfTempoC, tfDuracaoC, tfDistanciaC, tfCaloriasC, tfPassosC, tfVelMed, tfVelMax,
    							 tfMaiorEl, tfMenorEl, tfPesquisa, tfPesquisaAtividade, tfPesquisaAtividadeAluno;
    @FXML
    private ChoiceBox<String> escolheExercicio, escolheClienteGraficoBasico;
    @FXML
    private TextArea relatorio, relatorioDetalhado, tfBusca;
    @FXML
    private NumberAxis distanciaGraficoBasico;
    @FXML
    private CategoryAxis nomesGraficoDistanciaBasico;
    @FXML
    private TabPane tabGraficos, tabLinhas, tabGraficosCompletos;
    @FXML
    private DatePicker dataInicial, dataFinal, dataPesquisaInicio, dataPesquisaFinal, dataAtividadeInicio, dataAtividadeFinal;
    @FXML
    private AnchorPane telaEdicao, telaPesquisa;
    @FXML
    private SplitPane edicaoAluno, edicaoUsuario, edicaoAtividadeBasica, edicaoAtividadeCompleta;
    @FXML
    private Label totalCaloriasPerdidas, mediaCaloriasPerdidas, distanciaTotal, distanciaMedia, totalPassos;
    @FXML
    private TableView<Aluno> tabelaAlunos;
    @FXML
    private TableView<Usuario> tabelaUsuarios;
    @FXML
    private TableView<AtividadeBasica> tabelaAtivBasica;
    @FXML
    private TableView<AtividadeFisica> tabelaPesquisa;
    @FXML
    private TableView<AtividadeCompleta> tabelaAtivCompleta;
    @FXML
    private TableColumn<Aluno, Number> pesoAluno, alturaAluno;
    @FXML
    private TableColumn<Aluno, String> nomeAluno, sexoAluno, emailAluno, cpfAluno, wppAluno;
    @FXML
    private TableColumn<Usuario, String> userCol, senhaCol, papelCol;
    @FXML
    private TableColumn<AtividadeFisica, String> colPesq1, colPesq2;
    @FXML
    private TableColumn<AtividadeBasica, String> colTempo, colAtividade, colDuracao;
    @FXML
    private TableColumn<AtividadeBasica, Float> colCalorias, colDistancia;
    @FXML
    private TableColumn<AtividadeBasica, Integer> colPassos;
    @FXML
    private TableColumn<AtividadeCompleta, String> colAtividadeCompleta, colTempoCompleta, colDuracaoCompleta;
    @FXML
    private TableColumn<AtividadeCompleta, Float> colCaloriasCompleta, colDistanciaCompleta;
    @FXML
    private TableColumn<AtividadeCompleta, Integer> colPassosCompleta;
    @FXML
    private TableColumn<AtividadeCompleta, Float> colVelMed, colVelMax;
    @FXML
    private TableColumn<AtividadeCompleta, Double> colMaiorElv, colMenorElv;
    @FXML
    private BarChart<String, Number> graficoColunasDuracao, graficoColunasDistancia, graficoColunasCalorias, graficoColunasPassos, graficoColunasVelocidade, graficoColunasRitmo,
    															graficoColunasRitmoCompleto, graficoColunasDuracaoCompleto, graficoColunasDistanciaCompleto, graficoColunasCaloriasCompleto,
    															graficoColunasPassosCompleto, graficoColunasVelocidadeCompleto;
    @FXML
    private LineChart<String, Number> graficoLinhaDistancia, graficoLinhaCalorias, graficoLinhaPassos;


    @FXML
    private void initialize(){
    	pesoAluno.setCellValueFactory(new PropertyValueFactory<>("peso"));
    	alturaAluno.setCellValueFactory(new PropertyValueFactory<>("altura"));
    	nomeAluno.setCellValueFactory(new PropertyValueFactory<>("nome"));
    	sexoAluno.setCellValueFactory(new PropertyValueFactory<>("sexo"));
    	emailAluno.setCellValueFactory(new PropertyValueFactory<>("email"));
    	cpfAluno.setCellValueFactory(new PropertyValueFactory<>("cpf"));
    	wppAluno.setCellValueFactory(new PropertyValueFactory<>("whatsapp"));

    	userCol.setCellValueFactory(new PropertyValueFactory<>("usuario"));
    	senhaCol.setCellValueFactory(new PropertyValueFactory<>("senha"));
    	papelCol.setCellValueFactory(new PropertyValueFactory<>("papel"));

    	colTempo.setCellValueFactory(new PropertyValueFactory<>("tempo"));
    	colDuracao.setCellValueFactory(new PropertyValueFactory<>("duracao"));
    	colAtividade.setCellValueFactory(new PropertyValueFactory<>("atividade"));
    	colDistancia.setCellValueFactory(new PropertyValueFactory<>("distancia"));
    	colCalorias.setCellValueFactory(new PropertyValueFactory<>("caloriasPerdidas"));
    	colPassos.setCellValueFactory(new PropertyValueFactory<>("passos"));

    	colAtividadeCompleta.setCellValueFactory(new PropertyValueFactory<>("atividade"));
    	colTempoCompleta.setCellValueFactory(new PropertyValueFactory<>("tempo"));
    	colDuracaoCompleta.setCellValueFactory(new PropertyValueFactory<>("duracao"));
    	colCaloriasCompleta.setCellValueFactory(new PropertyValueFactory<>("caloriasPerdidas"));
    	colDistanciaCompleta.setCellValueFactory(new PropertyValueFactory<>("distancia"));
    	colPassosCompleta.setCellValueFactory(new PropertyValueFactory<>("passos"));
    	colVelMed.setCellValueFactory(new PropertyValueFactory<>("velocidadeMedia"));
    	colVelMax.setCellValueFactory(new PropertyValueFactory<>("velocidadeMaxima"));
    	colMaiorElv.setCellValueFactory(new PropertyValueFactory<>("maiorElevacao"));
    	colMenorElv.setCellValueFactory(new PropertyValueFactory<>("menorElevacao"));

    	colPesq1.setCellValueFactory(new PropertyValueFactory<>("atividade"));
    	colPesq2.setCellValueFactory(new PropertyValueFactory<>("duracao"));

    }

    List<AtividadeFisica> relatorioList;
    List<String> exercicios = new ArrayList<String>();
    List<AtividadeCompleta> atividadesList1;
    List<AtividadeBasica> atividadesList2;

    /**
     * Habilita o <code>GridPane</code> utilizado para importação dos arquivos.
     */
    @FXML
    private void exibeImportacao() {
    	limpaTela();
    	gridImportacao.setVisible(true);
    }

    /**
     * Abre a janela para escolha dos arquivos.
     */
    @FXML
    private void importarArquivos() {
    	FileChooser abreArquivos = new FileChooser();
    	// Altera o texto da barra de título.
    	abreArquivos.setTitle("Selecione os arquivos a serem importados...");

    	// Adiciona os filtros de extensão.
    	abreArquivos.getExtensionFilters().add(new ExtensionFilter("Arquivos Texto (*.txt)", "*.txt"));

    	// Adiciona os objetos File na lista de arquivos.
    	List<File> files = abreArquivos.showOpenMultipleDialog(btnImportar.getScene().getWindow());

    	// Se a lista for preenchida, chama o método para fazer a leitura dos arquivos.
    	if (files != null){
    		if (new ManipulaDados().abreArquivos(files, conexaoBD))
    			// Esconde o GridPane utilizado para importação de arquivos.
    			gridImportacao.setVisible(false);
    	}
    	// Caso a lista não tenha recebido nenhuma referência, nenhum arquivo foi selecionado. Exibe um alerta para o usuário.
    	else
    		exibeAlerta("INFORMAÇÃO", "Não foi selecionado nenhum arquivo.", AlertType.INFORMATION).showAndWait();
    }

    /**
     * Finaliza o programa.
     */
    @FXML
    private void sair(){
    	Stage stage = (Stage)btnImportar.getScene().getWindow();

    	stage.close();
    }

    /**
     * Exibe o campo de pesquisa de clientes para o relatório dos exercícios.
     */
    @FXML
    private void pesquisaClientes(){
    	limpaTela();
    	iniciaPesquisa.setVisible(true);
    } // pesquisaClientes

    /**
     * Obtém todos os exercícios de um cliente e exibe em um <code>ChoiceBox.</code>
     */
    @FXML
    private void pesquisaCliente() {
    	escolheExercicio.getItems().clear();
    	// Obtém o nome do cliente do campo de busca.
    	String nomeCliente = campoBuscaCliente.getText();

    	// Insere as atividades cadastradas no banco de dados nas listas.
    	List<AtividadeCompleta> atv1List = InsercaoAtividadeCompleta.listaAtividades(conexaoBD);
    	List<AtividadeBasica> atv2List = InsercaoAtividadeBasica.listaAtividades(conexaoBD);

    	// Lista de objetos string utilizada para inserir os nomes dos clientes no ChoiceBox
    	List<String> pesquisa = new ArrayList<String>();

    	// Objeto utilizado para formatar as datas corretamente.
    	SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");

    	relatorioList = new ArrayList<>();

    	String conteudo;

    	// Percorre as duas listas, compara o nome do cliente e adiciona na lista de atividades.
    	for (int i = 0 ; i < atv1List.size() ; i++){
    		if (atv1List.get(i).getUsuario().getNome().equalsIgnoreCase(nomeCliente))
    			relatorioList.add(atv1List.get(i));
    	}

    	for (int i = 0 ; i < atv2List.size() ; i++){
    		if (atv2List.get(i).getUsuario().getNome().equalsIgnoreCase(nomeCliente))
    			relatorioList.add(atv2List.get(i));
    	}

    	// Ordena a lista de atividades de acordo com a data em ordem crescente.
    	relatorioList.sort(new Comparator<AtividadeFisica>() {

			@Override
			public int compare(AtividadeFisica o1, AtividadeFisica o2) {
				return o1.getData().compareTo(o2.getData());
			}
		});

    	// Loop que obtém o nome dos exercícios que serão adicionados no ChoiceBox.
    	for (int i = 0 ; i < relatorioList.size() ; i++){
        	conteudo = new String();

    		conteudo = relatorioList.get(i).getUsuario().getNome() + " - " + relatorioList.get(i).getExercicio() + " - " + data.format(relatorioList.get(i).getData().getTime());

    		pesquisa.add(conteudo);
    	}

    	// Adiciona todos os exercícios obtidos anteriormente no ChoiceBox.
    	escolheExercicio.getItems().addAll(pesquisa);
    	hboxExercicio.setVisible(true);
    } // pesquisaCliente()

    /**
     * Habilita o TextArea do relatório e exibe de acordo com a atividade escolhida pelo usuário.
     */
    @FXML
    private void exibeRelatorio() {
    	limpaTela();
    	AtividadeFisica atividade;
    	String exercicio, data, comparadorD1, comparadorD2;

    	// Verifica se o usuário escolheu um exercício válido.
    	if (escolheExercicio.getValue() != null){
    		exercicio = escolheExercicio.getValue().substring(escolheExercicio.getValue().indexOf("-") + 2, escolheExercicio.getValue().lastIndexOf("-")-1);
    		data = escolheExercicio.getValue().substring(escolheExercicio.getValue().lastIndexOf("-") + 1);

    		Calendar date = Calendar.getInstance();
    		int[] arrayData = obtemData(data);
    		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
    		date.set(arrayData[2], arrayData[1]-1, arrayData[0]);

    		comparadorD1 = formatter.format(date.getTime());

    		// Loop que realiza a comparação das datas do exercício.
	    	for (int i = 0 ; i < relatorioList.size() ; i++){
	    		atividade = relatorioList.get(i);
	    		comparadorD2 = formatter.format(atividade.getData().getTime());
	    		if (atividade.getExercicio().equalsIgnoreCase(exercicio) && comparadorD1.equals(comparadorD2)){
	    				relatorioBasico.setVisible(true);
	    				relatorio.setText(obtemRelatorioExercicio(atividade, conexaoBD));
	    			}
	    	}
    	}
    	// Caso não tenha escolhido um exercício, exibe o alerta.
    	else{
    		exibeAlerta("ERRO", "Selecione o exercício desejado antes de prosseguir...", AlertType.ERROR).showAndWait();
    	}
    } // exibeRelatorio()

    /**
     * Habilita o TextArea do relatório detalhado.
     */
    @FXML
    private void relatorioDetalhado() {
    	limpaTela();
    	String relatorio = obtemRelatorioDetalhado(conexaoBD);

    	// Verifica se o relatório obteve resultados. Se positivo, exibe para o usuário.
    	if (relatorio.length() > 0){
    		relatorioDetalhado.setText(relatorio);
    		hboxRelatorioDetalhado.setVisible(true);
    	}
    	// Caso contrário, exibe o aviso.
    	else{
    		exibeAlerta("ERRO", "Não existe nenhuma atividade cadastrada!", AlertType.ERROR).showAndWait();
    	}
    } // relatorioDetalhado()

    /**
     * Limpa a tela do programa.
     */
    private void limpaTela(){
    	gridImportacao.setVisible(false);
    	iniciaPesquisa.setVisible(false);
    	hboxExercicio.setVisible(false);
    	relatorioBasico.setVisible(false);
    	hboxRelatorioDetalhado.setVisible(false);
    	gridPaneGrafico.setVisible(false);
    	hboxGraficoBasico.setVisible(false);
    	hboxGraficoLinha.setVisible(false);
    	hboxGraficoCompleto.setVisible(false);
    	gridDetalhes.setVisible(false);
    	telaEdicao.setVisible(false);
    	edicaoAluno.setVisible(false);
    	edicaoUsuario.setVisible(false);
    	edicaoAtividadeBasica.setVisible(false);
		edicaoAtividadeCompleta.setVisible(false);
		telaPesquisa.setVisible(false);

    } // limpaTela()


    /**
     * Habilita a área dos gráficos de colunas.
     */
    @FXML
    private void graficoColunasBasico() {
    	limpaTela();
    	gridPaneGrafico.setVisible(true);
    	listaUsuarios();
    	hboxGraficoBasico.setVisible(true);
    } // graficoColunasBasico()

    /**
     * Habilita a área dos gráficos de linhas.
     */
    @FXML
    private void graficoLinhas() {
    	limpaTela();
    	gridPaneGrafico.setVisible(true);
    	listaUsuarios();
    	hboxGraficoLinha.setVisible(true);
    }

    /**
     * Obtém o nome dos clientes e adiciona no ChoiceBox para escolha dos gráficos.
     */
    private void listaUsuarios(){
    	escolheClienteGraficoBasico.getItems().clear();
    	List<AtividadeCompleta> atividadesTipo1 = InsercaoAtividadeCompleta.listaAtividades(conexaoBD);
    	List<AtividadeBasica> atividadesTipo2 = InsercaoAtividadeBasica.listaAtividades(conexaoBD);
    	List<String> pesquisa = new ArrayList<String>();
    	String nomeCliente;
    	int achou;

    	for (int i = 0 ; i < atividadesTipo1.size() ; i++){
    		achou = 0;
    		nomeCliente = new String(atividadesTipo1.get(i).getUsuario().getNome());

    		if (pesquisa.size() > 0)
    			for (int k = 0 ; k < pesquisa.size() ; k++)
    				if (pesquisa.get(k).equals(nomeCliente))
    					achou = 1;

    		if (achou == 0)
    			pesquisa.add(nomeCliente);
    	}

    	for (int i = 0 ; i < atividadesTipo2.size() ; i++){
    		achou = 0;
    		nomeCliente = new String(atividadesTipo2.get(i).getUsuario().getNome());

    		if (pesquisa.size() > 0)
    			for (int k = 0 ; k < pesquisa.size() ; k++)
    				if (pesquisa.get(k).equals(nomeCliente))
    					achou = 1;

    		if (achou == 0)
    			pesquisa.add(nomeCliente);
    	}

    	escolheClienteGraficoBasico.getItems().addAll(pesquisa);
    }

    /**
     * Obtém a data do intervalo das atividades e lista as possíveis no ChoiceBox.
     */
    @FXML
    private void aplicaPeriodo() {
    	// Obtém o nome do cliente com base no ChoiceBox.
    	String nomeCliente = escolheClienteGraficoBasico.getValue();

    	// Preenche as listas de acordo com o nome do cliente.
    	atividadesList1 = InsercaoAtividadeCompleta.listaAtividadesPorCliente(nomeCliente, conexaoBD);
		atividadesList2 = InsercaoAtividadeBasica.listaAtividadesPorCliente(nomeCliente, conexaoBD);

		// Verifica se o usuário definiu as datas do intervalo.
		if (dataInicial.getValue() != null && dataFinal.getValue() != null){
			exercicios = listaAtividadesPeriodo(atividadesList1, atividadesList2, dataInicial.getValue(), dataFinal.getValue());
			relatorioList = listaAtividades(atividadesList1, atividadesList2);
		}
    } // aplicaPeriodo()

    /**
     * Habilita a área do gráfico de colunas com detalhes.
     */
    @FXML
    private void graficoColunasCompleto() {
    	limpaTela();
    	gridPaneGrafico.setVisible(true);
    	listaUsuarios();
    	hboxGraficoCompleto.setVisible(true);
    } // graficoColunasCompleto()

    /**
     * Com os dados obtidos anteriormente e as atividades já listadas, gera os gráficos do cliente.
     */
    @FXML
    private void gerarGraficos() {
    	String nomeCliente = escolheClienteGraficoBasico.getValue();

	    	// Verifica qual o gráfico está ativo no momento.
	    	if (hboxGraficoBasico.isVisible() || hboxGraficoLinha.isVisible()){
	    		if (hboxGraficoBasico.isVisible()){
	    			// Gera os gráficos de acordo com a escolha do usuário.
		        	for (int i = 0 ; i < NUMERO_GRAFICOS - 3 ; i++){
		    	    	switch (i + 1) {
		    			case 1:
		    				preencheGrafico(i+1, graficoColunasDuracao, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, null);
		    				break;
		    			case 2:
		    				preencheGrafico(i+1, graficoColunasDistancia, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, null);
		    				break;
		    			case 3:
		    				preencheGrafico(i+1, graficoColunasCalorias, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, null);
		    				break;
		    			case 4:
		    				preencheGrafico(i+1, graficoColunasPassos, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, null);
		    				break;
		    			case 5:
		    				preencheGrafico(i+1, graficoColunasVelocidade, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, null);
		    				break;
		    			case 6:
		    				preencheGrafico(i+1, graficoColunasRitmo, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, null);
		    				break;
		    	    	}
		        	}
		        	tabGraficos.setVisible(true);
	    		}
	    		else{
	    			if (hboxGraficoLinha.isVisible()){
		    			for (int i = 0 ; i < 3 ; i++){
			    	    	switch (i+1) {
				    			case 1:
				    				preencheGrafico(i+1, graficoLinhaDistancia, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente);
				    				break;
				    			case 2:
				    				preencheGrafico(i+1, graficoLinhaCalorias, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente);
				    				break;
				    			case 3:
				    				preencheGrafico(i+1, graficoLinhaPassos, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente);
				    				break;
			    	    	}
		    			}
		    			tabLinhas.setVisible(true);
	    			}
	    		}
	    	}
			    else{
					if (hboxGraficoCompleto.isVisible()){
						gridDetalhes.setVisible(true);

						Label[] linhas = {totalCaloriasPerdidas, mediaCaloriasPerdidas, distanciaTotal, distanciaMedia, totalPassos};

						for (int i = 0 ; i < NUMERO_GRAFICOS_COMPLETOS ; i++){
							// Altera o conteúdo do SwingNode.
			    	    	switch (i + NUMERO_GRAFICOS + 1) {
			    			case 10:
			    				preencheGrafico(i + NUMERO_GRAFICOS + 1, graficoColunasDuracaoCompleto, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, linhas);
			    				break;
			    			case 11:
			    				preencheGrafico(i + NUMERO_GRAFICOS + 1, graficoColunasDuracaoCompleto, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, linhas);
			    				break;
			    			case 12:
			    				preencheGrafico(i + NUMERO_GRAFICOS + 1, graficoColunasDuracaoCompleto, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, linhas);
			    				break;
			    			case 13:
			    				preencheGrafico(i + NUMERO_GRAFICOS + 1, graficoColunasDuracaoCompleto, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, linhas);
			    				break;
			    			case 14:
			    				preencheGrafico(i + NUMERO_GRAFICOS + 1, graficoColunasDuracaoCompleto, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, linhas);
			    				break;
			    			case 15:
			    				preencheGrafico(i + NUMERO_GRAFICOS + 1, graficoColunasDuracaoCompleto, conexaoBD, dataInicial.getValue(), dataFinal.getValue(), nomeCliente, linhas);
			    				break;
			    	    	}
			    	    	tabGraficosCompletos.setVisible(true);
						}
					}
    	else{
    		exibeAlerta("ERRO", "Erro na geração do gráfico. Verifique os dados antes de prosseguir...", AlertType.ERROR).showAndWait();
    	}
			    }
    } // gerarGrafico()

    /**
     * Abre a janela de destino do PDF.
     */
    @FXML
    private void exportaPDF() {
    	new ManipulaDados().exportaPDF();
    } // exportaPDF()

    @FXML
    private void edicaoAlunos() {
    	limpaTela();
        telaEdicao.setVisible(true);
        edicaoAluno.setVisible(true);

        atualizaTabelaAlunos();

    }

    private void atualizaTabelaAlunos() {
        ObservableList<Aluno> listaAlunos = FXCollections.observableList(new InsercaoAluno().listaAlunos(conexaoBD));

        tabelaAlunos.setItems(listaAlunos);
	}

    private void atualizaTabelaUsuarios() {
        ObservableList<Usuario> listaUsuarios = FXCollections.observableList(new InsercaoUsuario().listaUsers(conexaoBD));

        tabelaUsuarios.setItems(listaUsuarios);
	}

    private void atualizaTabelaAtivBasica() {
        ObservableList<AtividadeBasica> listaAtividades = FXCollections.observableList(InsercaoAtividadeBasica.listaAtividades(conexaoBD));

        tabelaAtivBasica.setItems(listaAtividades);
	}

    private void atualizaTabelaAtivCompleta() {
        ObservableList<AtividadeCompleta> listaAtividades = FXCollections.observableList(InsercaoAtividadeCompleta.listaAtividades(conexaoBD));

        tabelaAtivCompleta.setItems(listaAtividades);
	}

	@FXML
    private void edicaoAtividadeCompleta() {
    	limpaTela();
    	telaEdicao.setVisible(true);
		edicaoAtividadeCompleta.setVisible(true);
		atualizaTabelaAtivCompleta();
    }

    @FXML
    private void edicaoAtividadeBasica() {
    	limpaTela();
    	telaEdicao.setVisible(true);
    	edicaoAtividadeBasica.setVisible(true);
    	atualizaTabelaAtivBasica();
    }

    @FXML
    private void edicaoUsers() {
    	limpaTela();
    	telaEdicao.setVisible(true);
    	edicaoUsuario.setVisible(true);
    	atualizaTabelaUsuarios();
    }

    @FXML
    private void obtemAlunoTabela(MouseEvent mouse) {
    	if (mouse.getButton().equals(MouseButton.PRIMARY)){
    		if (mouse.getClickCount() == 2 && tabelaAlunos.getSelectionModel().getSelectedItem() != null){
    			Aluno aluno = tabelaAlunos.getSelectionModel().getSelectedItem();

    			tfNome.setText(aluno.getNome());
    			tfSexo.setText(aluno.getSexo());
    			tfAltura.setText(String.format("%1.2f", aluno.getAltura()));
    			tfPeso.setText(String.format("%1.2f", aluno.getPeso()));
    			tfEmail.setText(aluno.getEmail());
    			tfCPF.setText(aluno.getCpf());
    			tfWpp.setText(aluno.getWhatsapp());

    			btnRemoveAluno.setDisable(false);
    			btnAtualizaAluno.setDisable(false);
    		}
    	}
    }

    @FXML
    private void obtemUsuarioTabela(MouseEvent mouse) {
    	if (mouse.getButton().equals(MouseButton.PRIMARY)){
    		if (mouse.getClickCount() == 2 && tabelaUsuarios.getSelectionModel().getSelectedItem() != null){
    			Usuario usuario = tabelaUsuarios.getSelectionModel().getSelectedItem();

    			tfUser.setText(usuario.getUsuario());
    			tfSenha.setText(usuario.getSenha());
    			tfPapel.setText(usuario.getPapel());

    			btnRemoveUsuario.setDisable(false);

    		}
    	}
    }

    @FXML
    private void obtemAtivBasica(MouseEvent mouse) {
    	if (mouse.getButton().equals(MouseButton.PRIMARY)){
    		if (mouse.getClickCount() == 2 && tabelaAtivBasica.getSelectionModel().getSelectedItem() != null){
    			AtividadeBasica atividade = tabelaAtivBasica.getSelectionModel().getSelectedItem();

    			tfAtividade.setText(atividade.getExercicio());
    			tfTempo.setText(atividade.getTempo());
    			tfDuracao.setText(atividade.getDuracao());
    			tfDistancia.setText(String.format("%1.2f", atividade.getDistancia()));
    			tfCalorias.setText(String.format("%1.2f", atividade.getCaloriasPerdidas()));
    			tfPassos.setText(String.format("%d", atividade.getPassos()));
    		}
    	}
    }

    @FXML
    private void obtemAtivCompleta(MouseEvent mouse) {
    	if (mouse.getButton().equals(MouseButton.PRIMARY)){
    		if (mouse.getClickCount() == 2 && tabelaAtivCompleta.getSelectionModel().getSelectedItem() != null){
    			AtividadeCompleta atividade = tabelaAtivCompleta.getSelectionModel().getSelectedItem();

    			tfAtividadeC.setText(atividade.getExercicio());
    			tfTempoC.setText(atividade.getTempo());
    			tfDuracaoC.setText(atividade.getDuracao());
    			tfDistanciaC.setText(String.format("%1.2f", atividade.getDistancia()));
    			tfCaloriasC.setText(String.format("%1.2f", atividade.getCaloriasPerdidas()));
    			tfPassosC.setText(String.format("%d", atividade.getPassos()));
    			tfVelMed.setText(String.format("%1.2f", atividade.getVelocidadeMedia()));
    			tfVelMax.setText(String.format("%1.2f", atividade.getVelocidadeMaxima()));
    			tfMaiorEl.setText(String.format("%1.2f", atividade.getMaiorElevacao()));
    			tfMenorEl.setText(String.format("%1.2f", atividade.getMenorElevacao()));

    		}
    	}
    }

    @FXML
    private void remove(){
    	if (edicaoAluno.isVisible()){
	    	Aluno aluno = new Aluno();

	    	aluno.setWhatsapp(tfWpp.getText());

	    	new InsercaoAluno().remove(aluno, conexaoBD);

	    	atualizaTabelaAlunos();
    	}
    	else{
    		if (edicaoUsuario.isVisible()){
    			Usuario usuario = new Usuario();

    			usuario.setUsuario(tfUser.getText());

    			new InsercaoUsuario().remove(usuario, conexaoBD);

    			atualizaTabelaUsuarios();
    		}
    	}
    }

    @FXML
    private void atualiza(){

    }


    /**
     * Encerra a conexão com o BD.
     */
	@Override
	protected void finalize() throws Throwable {
		conexaoBD.close();
		super.finalize();
	}

	@FXML
	private void pesquisaAluno(){
		limpaTela();
		telaPesquisa.setVisible(true);
		gridBuscaAluno.setVisible(true);
	}

	@FXML
	private void pesquisaAtividade(){
		limpaTela();
		telaPesquisa.setVisible(true);
		gridBuscaAtividade.setVisible(true);

	}

	@FXML
	private void buscaAluno(){
		Aluno aluno = new InsercaoAluno().pesquisaAluno(tfPesquisa.getText(), conexaoBD);

		if (aluno != null){
			tfBusca.setText(aluno.toString());
			tfBusca.setVisible(true);
		}
	}

	@FXML
	private void buscaAtividade(){
		tabelaPesquisa.setVisible(true);
		List<AtividadeBasica> basicList = InsercaoAtividadeBasica.listaAtividadesPorPeriodo(tfPesquisaAtividade.getText(), dataPesquisaInicio.getValue(), dataPesquisaFinal.getValue(), conexaoBD);
		List<AtividadeCompleta> completaList = InsercaoAtividadeCompleta.listaAtividadesPorPeriodo(tfPesquisaAtividade.getText(), dataPesquisaInicio.getValue(), dataPesquisaFinal.getValue(), conexaoBD);
		List<AtividadeFisica> atividadesList = new ArrayList<>();

		for (int i = 0 ; i < basicList.size() ; i++)
			atividadesList.add(basicList.get(i));
		for (int i = 0 ; i < completaList.size() ; i++)
			atividadesList.add(completaList.get(i));

		ObservableList<AtividadeFisica> atividades = FXCollections.observableArrayList(atividadesList);

		tabelaPesquisa.setItems(atividades);
	}

    @FXML
    private void obtemAtivBusca(MouseEvent mouse) {
    	if (mouse.getButton().equals(MouseButton.PRIMARY)){
    		if (mouse.getClickCount() == 2 && tabelaPesquisa.getSelectionModel().getSelectedItem() != null){
    			AtividadeFisica atividade = tabelaPesquisa.getSelectionModel().getSelectedItem();

    			System.out.println(obtemRelatorioExercicio(atividade, conexaoBD));

	    		try{
	    					FXMLLoader loader = new FXMLLoader();
	    					loader.setLocation(getClass().getResource("Pesquisa.fxml"));
		    				Parent pesquisa = loader.load();
		    				PesquisaController janela = loader.getController();
		    				janela.setRelatorio(obtemRelatorioExercicio(atividade, conexaoBD));
		    				Scene scene = new Scene(pesquisa,600,400);
		    				Stage janelaPesquisa = new Stage();
		    				janelaPesquisa.setScene(scene);
		    				janelaPesquisa.show();
		    	} catch (IOException e) {
		    		e.printStackTrace();
				}
    		}
    	}
    } // obtemAtivBusca()

    @FXML
    private void pesquisaAtividadeAluno(){
    	limpaTela();
		telaPesquisa.setVisible(true);
		gridBuscaAtividadeAluno.setVisible(true);
    }
    @FXML
    private void buscaAtividadeAluno(){

    	List<AtividadeBasica> basicList = InsercaoAtividadeBasica.listaAtividadesPorCliente(tfPesquisaAtividadeAluno.getText(), dataAtividadeInicio.getValue(), dataAtividadeFinal.getValue(), conexaoBD);
		List<AtividadeCompleta> completaList = InsercaoAtividadeCompleta.listaAtividadesPorCliente(tfPesquisaAtividadeAluno.getText(), dataAtividadeInicio.getValue(), dataAtividadeFinal.getValue(), conexaoBD);
		List<AtividadeFisica> atividadesList = new ArrayList<>();

		for (int i = 0 ; i < basicList.size() ; i++)
			atividadesList.add(basicList.get(i));
		for (int i = 0 ; i < completaList.size() ; i++)
			atividadesList.add(completaList.get(i));

		ObservableList<AtividadeFisica> atividades = FXCollections.observableArrayList(atividadesList);

		tabelaPesquisa.setItems(atividades);
		tabelaPesquisa.setVisible(true);
    }



} // class AtividadeFisicaController
