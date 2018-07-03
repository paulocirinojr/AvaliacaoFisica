package br.com.academia.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import br.com.academia.modelo.Aluno;
import br.com.academia.modelo.AtividadeBasica;
import br.com.academia.modelo.AtividadeCompleta;
import br.com.academia.modelo.AtividadeFisica;
import br.com.academia.modelo.Ritmo;
import br.com.academia.modelo.dao.InsercaoAtividadeBasica;
import br.com.academia.modelo.dao.InsercaoAtividadeCompleta;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
/**
 * Contém todos os métodos que serão utilizados para manipulação dos dados para inserção no banco de dados.
 * @author Paulo
 *
 */
public class ManipulaDados {
	public static final String DURACAO = "Duração do Exercício",
											 DISTANCIA = "Distância Percorrida",
											 CALORIAS = "Calorias Perdidas",
											 PASSOS = "Número de Passos",
											 VELOCIDADE_MEDIA = "Velocidade Média",
											 RITMO_MEDIO = "Ritmo Médio",
											 COMPARADOR_DATA = "Data",
											 COMPARADOR_DURACAO = "Duração (min.)",
											 COMPARADOR_DISTANCIA = "Distância (Km)",
											 COMPARADOR_CALORIAS = "Calorias (Kcal)",
											 COMPARADOR_PASSOS = "Passos",
											 COMPARADOR_VM = "Velocidade Média (Km/h)",
											 COMPARADOR_RM = "Ritmo Médio",
											 RELATORIO_DETALHADO = "*** Relatório Detalhado ***",
											 RELATORIO_EXERCICIO = "*** Relatório ***";

	public boolean abreArquivos(List<File> arquivos, Connection conexaoBD){
    	ArquivoTexto arquivo;
    	int tipoAtividade;
    	ArrayList<AtividadeCompleta> atividadesTipo1 = new ArrayList<>();
    	ArrayList<AtividadeBasica> atividadesTipo2 = new ArrayList<>();

    	for (int i = 0 ; i < arquivos.size() ; i++){
    		arquivo = new ArquivoTexto();

    		try {
    			// Abre o arquivo texto.
				arquivo.abrir(arquivos.get(i).getAbsolutePath());

				// Verifica o tipo de atividade.
				tipoAtividade = verificaAtividade(arquivo);

				arquivo.fechar();

				// Abre o arquivo novamente para obter os dados.
				arquivo.abrir(arquivos.get(i).getAbsolutePath());

				// Se a variável tipoAtividade recebe 1, a atividade é do tipo AtividadeCompleta.
				if (tipoAtividade == 1){
					// String que recebe todo o conteúdo do arquivo.
					String leituraArquivo = arquivo.ler();
					// Cria o objeto a ser preenchido.
					AtividadeCompleta atividade = new AtividadeCompleta();
					String dadosUsuario;

					// Recebe o nome do exercício.
					dadosUsuario = leituraDados(0, leituraArquivo);
					// Altera no objeto.
					atividade.setExercicio(dadosUsuario);

					// Recebe os dados do usuário.
					dadosUsuario = leituraDados(1, leituraArquivo);
					// Quebra os dados em linhas e adiciona em um array.
					String[] user = dadosUsuario.split("\n");

					// Cria o objeto Usuario a ser preenchido com os dados.
					Aluno usuario = new Aluno();

					// Variável utilizada para contar as linhas dos dados.
					int contLinha = 0;

					// Altera os dados do usuário de acordo com a linha.
					usuario.setNome(obtemNome(user[contLinha++]));
					usuario.setSexo(obtemSexo(user[contLinha++]));
					usuario.setAltura(obtemAltura(user[contLinha++]));
					usuario.setPeso(obtemPeso(user[contLinha++]));

					Calendar dataNascimento = Calendar.getInstance();
					int[] data = obtemData(user[contLinha++]);
					dataNascimento.set(data[2], data[1]-1, data[0]);
					usuario.setDataNascimento(dataNascimento);
					usuario.setEmail(obtemEmail(user[contLinha++]));
					usuario.setCpf(obtemCPF(user[contLinha++]));
					usuario.setWhatsapp(obtemWhatsapp(user[contLinha]));

					// Insere o objeto Usuario na atividade.
					atividade.setUsuario(usuario);

					// Obtém os detalhes do exercício.
					dadosUsuario = leituraDados(2, leituraArquivo);
					// Quebra os detalhes em um array.
					String[] ativ = dadosUsuario.split("\n");

					// Altera o valor do contador de linhas.
					contLinha = 0;

					Calendar dataAtividade = Calendar.getInstance();
					int[] dataAtiv = obtemData(ativ[contLinha++]);

					dataAtividade.set(dataAtiv[2], dataAtiv[1]-1, dataAtiv[0]);

					// Insere os dados no objeto.
					atividade.setData(dataAtividade);
					atividade.setTempo(obtemTempo(ativ[contLinha++]));
					atividade.setDuracao(obtemDuracao(ativ[contLinha++]));
					atividade.setDistancia(obtemDistancia(ativ[contLinha++]));
					atividade.setCaloriasPerdidas(obtemCalorias(ativ[contLinha++]));
					atividade.setPassos(obtemPassos(ativ[contLinha++]));
					atividade.setVelocidadeMedia(obtemVelocidade(ativ[contLinha++]));
					atividade.setVelocidadeMaxima(obtemVelocidade(ativ[contLinha++]));
					atividade.setRitmoMedio(obtemRitmo(ativ[contLinha++]));
					atividade.setRitmoMaximo(obtemRitmo(ativ[contLinha++]));
					atividade.setMenorElevacao(obtemElevacao(ativ[contLinha++]));
					atividade.setMaiorElevacao(obtemElevacao(ativ[contLinha++]));

					// Obtém os dados adicionais do exercício (ritmos).
					dadosUsuario = leituraDados(3, leituraArquivo);

					// Quebra os dados em um array.
					String[] ritmos = dadosUsuario.split("\n");
					// Objeto a ser utilizado no preenchimento dos dados.
					Ritmo ritmo;
					float KM;
					int min, seg;
					// Lista de ritmos do exercício.
					ArrayList<Ritmo> ritmosList = new ArrayList<>();

					// Loop percorrendo o array de ritmos.
					for (int k = 0 ; k < ritmos.length ; k++){
						ritmo = new Ritmo();

						KM = obtemKM(ritmos[k]);
						min = obtemMin(ritmos[k]);
						seg = obtemSeg(ritmos[k]);

						ritmo.setKM(KM);
						ritmo.setMinutos(min);
						ritmo.setSegundos(seg);

						// Insere o objeto na lista.
						ritmosList.add(ritmo);
					}

					// Insere a lista de ritmos no objeto atividade.
					atividade.setRitmo(ritmosList);

					// Insere a atividade na lista de atividades.
					atividadesTipo1.add(atividade);
				} // AtividadeCompleta
				else{
					// Se a variável tipoAtividade recebe 2, a atividade é do tipo AtividadeBasica.
					if (tipoAtividade == 2){
						// String que recebe todo o conteúdo do arquivo.
						String leituraArquivo = arquivo.ler();
						// Cria o objeto a ser preenchido.
						AtividadeBasica atividade = new AtividadeBasica();
						String dadosUsuario;

						// Recebe o nome do exercício.
						dadosUsuario = leituraDados(0, leituraArquivo);
						// Altera no objeto.
						atividade.setExercicio(dadosUsuario);

						// Recebe os dados do usuário.
						dadosUsuario = leituraDados(1, leituraArquivo);
						// Quebra os dados em linhas e adiciona em um array.
						String[] user = dadosUsuario.split("\n");

						// Cria o objeto Usuario a ser preenchido com os dados.
						Aluno usuario = new Aluno();

						// Variável utilizada para contar as linhas dos dados.
						int contLinha = 0;

						// Altera os dados do usuário de acordo com a linha.
						usuario.setNome(obtemNome(user[contLinha++]));
						usuario.setSexo(obtemSexo(user[contLinha++]));
						usuario.setAltura(obtemAltura(user[contLinha++]));
						usuario.setPeso(obtemPeso(user[contLinha++]));

						Calendar dataNascimento = Calendar.getInstance();
						int[] data = obtemData(user[contLinha++]);

						dataNascimento.set(data[2], data[1]-1, data[0]);

						usuario.setDataNascimento(dataNascimento);
						usuario.setEmail(obtemEmail(user[contLinha]));

						// Insere o objeto Usuario na atividade.
						atividade.setUsuario(usuario);

						// Obtém os detalhes do exercício.
						dadosUsuario = leituraDados(2, leituraArquivo);
						// Quebra os detalhes em um array.
						String[] ativ = dadosUsuario.split("\n");

						// Altera o valor do contador de linhas.
						contLinha = 0;

						Calendar dataAtividade = Calendar.getInstance();
						int[] dataAtiv = obtemData(ativ[contLinha++]);

						dataAtividade.set(dataAtiv[2], dataAtiv[1]-1, dataAtiv[0]);

						// Insere os dados no objeto.
						atividade.setData(dataAtividade);
						atividade.setTempo(obtemTempo(ativ[contLinha++]));
						atividade.setDuracao(obtemDuracao(ativ[contLinha++]));
						atividade.setDistancia(obtemDistancia(ativ[contLinha++]));
						atividade.setCaloriasPerdidas(obtemCalorias(ativ[contLinha++]));
						atividade.setPassos(obtemPassos(ativ[contLinha++]));
						atividadesTipo2.add(atividade);
					} // AtividadeBasica
				}
			} catch (FileNotFoundException e) {
				exibeAlerta("ERRO NA ABERTURA DOS ARQUIVOS", "Erro na abertura do(s) arquivo(s) !", AlertType.ERROR).showAndWait();
			} catch (IOException e) {
				exibeAlerta("ERRO NA LEITURA DOS ARQUIVOS", "Erro na leitura do(s) arquivo(s) !", AlertType.ERROR).showAndWait();
			}
    	} // for()

    	// Verifica se as listas de atividades possuem objetos a serem inseridos no banco.
    	if (atividadesTipo1.size() > 0)
    		InsercaoAtividadeCompleta.iniciaInsercao(atividadesTipo1, conexaoBD);
    	if (atividadesTipo2.size() > 0)
    		InsercaoAtividadeBasica.iniciaInsercao(atividadesTipo2, conexaoBD);

    	if (atividadesTipo1.size() > 0 || atividadesTipo2.size() > 0){
    		exibeAlerta("SUCESSO", "Dados inseridos no banco com sucesso !", AlertType.INFORMATION).showAndWait();
    	}
    	return false;
    } // abreArquivos()

    /**
     * Cria um objeto <code>Alert</code> com os dados recebidos do usuário.
     *
     * @param titulo Título da caixa de diálogo.
     * @param mensagem Mensagem a ser exibida.
     * @param tipo Tipo de alerta a ser inserido na caixa de diálogo.
     * @return alerta Retorna o objeto preenchido.
     */
    public static Alert exibeAlerta(String titulo, String mensagem, AlertType tipo){
    	Alert alerta = new Alert(tipo);
    	alerta.setTitle(titulo);
    	alerta.setContentText(mensagem);

    	return alerta;
    } // exibeAlerta()

	/**
	 * Verifica o tipo de atividade lido no arquivo.
	 * @param arquivo Arquivo texto escolhido pelo usuário.
	 * @return Retorna 1 caso seja uma atividade com mais detalhes (Corrida, Caminhada) ou 2 caso contrário.
	 */
    public static int verificaAtividade(ArquivoTexto arquivo){
    	String leitura = arquivo.ler();

    	// Verifica o arquivo em busca de dados mais específicos apenas contidos em atividades como caminhada.
    	if (leitura.contains("------ Ritmo ------")){
    		return 1;
    	}
    	// Atividades "básicas"
    	else{
    		return 2;
    	}
    } // verificaAtividade()

    /**
     * Realiza a leitura dos dados de acordo com o <code> tipoLeitura</code> informado pelo usuário.
     * @param tipoLeitura O usuário informa o tipo de leitura necessário.
     * <br>	Valor 0: Nome do exercício
     * <br>Valor 1: Dados do usuário
     * <br>Valor 2: Detalhes do exercício
     * <br>Valor 3: Detalhes adicionais do exercício<br><br>
     * @param dadosArquivo <code>String</code> que contém todos os dados contidos no arquivo lido.
     * @return Retorna a <code>String</code> requisitada pelo usuário.
     */
    public static String leituraDados(int tipoLeitura, String dadosArquivo){
    	String[] linhas = dadosArquivo.split("\n");
    	String dados;
    	int inicioLeitura=-1, fimLeitura=-1;

    	// Obtém o nome do exercício.
    	if (tipoLeitura == 0){
    		return linhas[0].substring(linhas[0].indexOf(":")+2);
    	}

    	// Ler dados usuário
    	if (tipoLeitura == 1){
    		for (int i = 0 ; i < linhas.length ; i++){
    			if (linhas[i].contains("------ Usuário ------"))
    				// Inicia a leitura dos dados após a linha acima.
    				inicioLeitura = i+1;
    			else{
    				if (linhas[i].contains("------ Detalhes do exercício ------")){
    					// Termina a leitura dos dados antes da linha acima.
    					fimLeitura = i-1;
    					break;
    				}
    			}
    		}
    	}

    		// Ler detalhes do exercício
    		if (tipoLeitura == 2){
    			for (int i = 0 ; i < linhas.length ; i++){
        			if (linhas[i].contains("------ Detalhes do exercício ------"))
        				// Inicia a leitura dos dados após a linha acima.
        				inicioLeitura = i+1;
        			else{
        				if (linhas[i].contains("------ Ritmo ------")){
        					// Termina a leitura dos dados antes da linha acima.
        					fimLeitura = i-1;
        					break;
        				}
        				else
        					fimLeitura = i+1;
        			}
    			}
    		}

    		if (tipoLeitura == 3){
    			for (int i = 0 ; i < linhas.length ; i++){
        			if (linhas[i].contains("------ Ritmo ------")){
        				// Inicia a leitura dos dados após a linha acima.
        				inicioLeitura = i+1;
        				// Termina a leitura no final do arquivo.
    					fimLeitura = linhas.length;
        				break;
        			}
    			}
    		}

    		if (inicioLeitura != -1 && fimLeitura != -1){
    			dados = "";
    			// Loop que percorre os dados de acordo com as posições informadas e concatena em uma String.
	    		for (int k = inicioLeitura ; k < fimLeitura ; k++)
	    				dados += linhas[k] + "\n";

	    		// Retorna os dados requisitados pelo usuário.
	    		return dados;
	    	}
    	return "";
    } // leituraDados()

    /**
     * Recebe uma linha no formato "Data de nascimento: DD/MM/AAAA", quebra a data e retorna um array de inteiros, separados por dia, mês e ano.
     * @param data <code>String</code> com os dados.
     * @return Retorna um array preenchido com os dados recebidos.
     */

    public static int[] obtemData(String data){
    	// Quebra a string usando o separador "/" e insere em um array de Strings.
    	String[] date = data.substring(data.indexOf(":")+2).split("/");

    	// Inicializa um array de inteiros para inserção dos dados.
    	int dt[] = new int[3];

    	// Loop percorrendo o array de Strings e inserindo os dados no array de inteiros, de acordo com a
    	// ordem dos dados: DD/MM/AAAA
    	for (int i = 0; i < date.length ; i++)
    		dt[i] = Integer.parseInt(date[i]);

    	return dt;
    } // obtemData()

    /**
     * Recebe a linha que contém o e-mail e retorna os dados prontos para o usuário.
     * @param email <code>String</code> que contém os dados.
     * @return Retorna o e-mail recebido como parâmetro.
     */

    public static String obtemEmail(String email){
    	return email.substring(email.indexOf(":")+2, email.length());
    } // obtemEmail()

    /**
     * Recebe a linha que contém o nome do cliente e retorna os dados prontos para o usuário.
     * @param nome <code>String</code> que contém os dados.
     * @return Retorna o nome do cliente recebido como parâmetro.
     */

    public static String obtemNome(String nome){
    	return nome.substring(nome.indexOf(":")+2,nome.length());
    } // obtemNome()

    /**
     * Recebe a linha que contém o sexo do cliente e retorna os dados prontos para o usuário.
     * @param sexo <code>String</code> que contém os dados.
     * @return Retorna o sexo do cliente recebido como parâmetro.
     */
    public static String obtemSexo(String sexo){
    	return sexo.substring(sexo.indexOf(":")+2,sexo.length());
    } // obtemSexo()

    public static String obtemCPF(String cpf){
    	return cpf.substring(cpf.indexOf(":")+2,cpf.length());
    } // obtemSexo()

    public static String obtemWhatsapp(String whatsapp){
    	return whatsapp.substring(whatsapp.indexOf(":")+2,whatsapp.length());
    } // obtemSexo()

    /**
     * Recebe a linha que contém a hora de início e término do exercício e retorna os dados prontos para o usuário.
     * @param tempo <code>String</code> que contém os dados.
     * @return Retorna a hora de início e término do exercício.
     */
    public static String obtemTempo(String tempo){
    	return tempo.substring(tempo.indexOf(":")+2,tempo.length());
    } // obtemTempo()

    /**
     * Recebe a linha que contém o tempo de duração do exercício e retorna os dados prontos para o usuário.
     * @param duracao <code>String</code> que contém os dados.
     * @return Retorna o tempo de duração do exercício.
     */
    public static String obtemDuracao(String duracao){
    	return duracao.substring(duracao.indexOf(":")+2,duracao.length());
    } // obtemDuracao()

    /**
     * Recebe a linha que contém a distância percorrida e retorna os dados prontos para o usuário.
     * @param distancia <code>String</code> que contém os dados.
     * @return Retorna a distância percorrida..
     */
    public static float obtemDistancia(String distancia){
    	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como parâmetro. Utiliza o método
    	 * substring para retirar o início da linha (Distância: ).
    	 */
    	StringBuilder strDistancia = new StringBuilder(distancia.substring(distancia.indexOf(":") + 2,distancia.indexOf("Km") - 1));
    	Float distance;

    	if (distancia.contains(","))
    		// Substitui a "," pelo "." para evitar exceção na criação do objeto Float.
    		strDistancia.replace(strDistancia.indexOf(","), strDistancia.indexOf(",")+1,	".");

    	distance = Float.parseFloat(strDistancia.toString());

    	return distance;
    } // obtemDistancia()

    /**
     * Recebe a linha que contém as calorias perdidas e retorna os dados prontos para o usuário.
     * @param calorias <code>String</code> que contém os dados.
     * @return Retorna as calorias perdidas no exercício.
     */
    public static Float obtemCalorias(String calorias){
    	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como parâmetro. Utiliza o método
    	 * substring para retirar o início da linha (Calorias perdidas: ) e o final (Kcal)..
    	 */
    	StringBuilder strCalorias = new StringBuilder(calorias.substring(calorias.indexOf(":")+2, calorias.indexOf("Kcal") -1));
    	Float cal = Float.parseFloat(strCalorias.toString());

    	return cal;
    } // obtemCalorias()

    /**
     * Recebe a linha que contém a quantidade de passos do cliente e retorna os dados prontos para o usuário.
     * @param passos <code>String</code> que contém os dados.
     * @return Retorna o número de passos dados pelo cliente.
     */
    public static Integer obtemPassos(String passos){
    	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como parâmetro. Utiliza o método
    	 * substring para retirar o início da linha (Passos: ).
    	 */
    	StringBuilder strPassos = new StringBuilder(passos.substring(passos.indexOf(":") + 2, passos.length()));

    	if (passos.contains("."))
	    	// Remove o "." se necessário
	    	strPassos.replace(strPassos.indexOf("."), strPassos.indexOf(".")+1,	"");
    	Integer p = Integer.parseInt(strPassos.toString());

    	return p;
    } // obtemPassos()

    /**
     * Recebe a linha que contém a velocidade (média ou máxima) e retorna os dados prontos para o usuário.
     * @param velocidade <code>String</code> que contém os dados.
     * @return Retorna a velocidade.
     */

    public static float obtemVelocidade(String velocidade){
    	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como parâmetro. Utiliza o método
    	 * substring para retirar o início da linha (Velocidade Média: || Velocidade Máxima:  ) e o final (Km/h).
    	 */
    	StringBuilder strVelocidade = new StringBuilder(velocidade.substring(velocidade.indexOf(":")+2, velocidade.indexOf("K") -1));
    	Float vel;

    	// Substitui a "," pelo "." para evitar exceção na criação do objeto Float.
    	strVelocidade.replace(strVelocidade.indexOf(","), strVelocidade.indexOf(",")+1,	".");

    	vel = Float.parseFloat(strVelocidade.toString());

    	return vel;
    } // obtemVelocidade()

    /**
     * Recebe a linha que contém a elevação (menor ou maior) e retorna os dados prontos para o usuário.
     * @param elevacao <code>String</code> que contém os dados.
     * @return Retorna a elevação.
     */

    public static double obtemElevacao(String elevacao){
    	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como parâmetro. Utiliza o método
    	 * substring para retirar o início da linha (Menor elevação: || Menor elevação: ) e o final (m).
    	 */
    	StringBuilder strElevacao = new StringBuilder(elevacao.substring(elevacao.indexOf(":")+2, elevacao.indexOf("m") -1));
    	// Remove o "."
		strElevacao.replace(strElevacao.indexOf("."), strElevacao.indexOf(".")+1,	"");
    	Double elevation = Double.parseDouble(strElevacao.toString());

    	return elevation;
    } // obtemElevacao()

    /**
     * Recebe a linha que contém a altura e retorna os dados prontos para o usuário.
     * @param linhaAltura <code>String</code> que contém os dados.
     * @return Retorna a altura.
     */
    public static Float obtemAltura(String linhaAltura){
       	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como parâmetro. Utiliza o método
    	 * substring para retirar o início da linha (Altura:  ) e o final (m).
    	 */
    	StringBuilder strAltura = new StringBuilder(linhaAltura.substring(linhaAltura.indexOf(":")+1, linhaAltura.indexOf("m") -1));

    	// Substitui a "," pelo "." para evitar exceção na criação do objeto Float.
		strAltura.replace(strAltura.indexOf(","), strAltura.indexOf(",")+1,	".");

		Float altura = Float.parseFloat(strAltura.toString());

		return altura;
    } // obtemAltura()

    /**
     * Recebe a linha que contém o peso e retorna os dados prontos para o usuário.
     * @param linhaPeso <code>String</code> que contém os dados.
     * @return Retorna o peso do cliente..
     */
    public static Float obtemPeso(String linhaPeso){
       	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como parâmetro. Utiliza o método
    	 * substring para retirar o início da linha (Peso:  ) e o final (Kg).
    	 */
    	StringBuilder strPeso = new StringBuilder(linhaPeso.substring(linhaPeso.indexOf(":")+1,linhaPeso.indexOf("Kg")-1));

    	// Substitui a "," pelo "." para evitar exceção na criação do objeto Float.
    	strPeso.replace(strPeso.indexOf(","), strPeso.indexOf(",")+1,	".");

    	Float peso = Float.parseFloat(strPeso.toString());

    	return peso;
    } // obtemPeso()

    /**
     * Recebe a linha que contém os dados que serão utilizados no objeto <code>Ritmo</code> e retorna os dados prontos para o usuário.
     * @param linhaKM <code>String</code> que contém os dados.
     * @return Retorna a KM.
     */
    public static Float obtemKM(String linhaKM){
       	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como parâmetro. Utiliza o método
    	 * substring para retirar o final do dado (Km:  ).
    	 */
    	StringBuilder strKM = new StringBuilder(linhaKM.substring(0,linhaKM.indexOf("K")-1));
    	if (linhaKM.contains(","))
        	// Substitui a "," pelo "." para evitar exceção na criação do objeto Float.
    		strKM.replace(strKM.indexOf(","), strKM.indexOf(",")+1,	".");
    	Float km = Float.parseFloat(strKM.toString());

    	return km;
    } // obtemKM()

    /**
     * Recebe a linha que contém os dados que serão utilizados no objeto <code>Ritmo</code> e retorna os dados prontos para o usuário.
     * @param linhaRitmo <code>String</code> que contém os dados.
     * @return Retorna o minuto.
     */
    public static Integer obtemMin(String linhaRitmo){
       	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como parâmetro. Utiliza o método
    	 * substring para quebrar a string e obter apenas o minuto.
    	 */
    	StringBuilder strMin = new StringBuilder(linhaRitmo.substring(linhaRitmo.indexOf(":")+2,linhaRitmo.indexOf("'")));
    	Integer min = Integer.parseInt(strMin.toString());

    	return min;
    } // obtemMin()

    /**
     * Recebe a linha que contém os dados que serão utilizados no objeto <code>Ritmo</code> e retorna os dados prontos para o usuário.
     * @param linhaRitmo <code>String</code> que contém os dados.
     * @return Retorna o segundo.
     */
    public static Integer obtemSeg(String linhaRitmo){
      	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como parâmetro. Utiliza o método
    	 * substring para quebrar a string e obter apenas o segundo.
    	 */
    	StringBuilder strSeg = new StringBuilder(linhaRitmo.substring(linhaRitmo.indexOf("'")+1,linhaRitmo.length() - 1));
    	Integer min = Integer.parseInt(strSeg.toString());

    	return min;
    } // obtemMin()

    /**
     * Obtém o <code>LocalTime</code> de acordo com o ritmo recebido como parâmetro.
     * @param linhaRitmo Linha que contém o ritmo a ser retornado como objeto.
     * @return ritmo Objeto criado e manipulado com os minutos e segundos.
     */

    public static LocalTime obtemRitmo(String linhaRitmo){
    	LocalTime ritmo;
    	int min, seg;

    	// Quebra os dados da linha e armazena nas respectivas variáveis.

    	// Ritmo <MM>'
    	min = Integer.parseInt(linhaRitmo.substring(linhaRitmo.indexOf(":")+2, linhaRitmo.indexOf("'")));

    	// Ritmo <SS>''
    	seg = Integer.parseInt(linhaRitmo.substring(linhaRitmo.indexOf("'")+1, linhaRitmo.indexOf("\"")));

    	// Inicializa o objeto ritmo com o tempo (0h:<min>:<seg>)
    	ritmo = LocalTime.of(0, min, seg);

    	return ritmo;
    } // obtemRitmo()

    /**
     * Gera todo o relatório a partir do objeto <code>AtividadeTipo1</code> recebido como parâmetro.
     *
     * Utiliza o modelo:
     *
								 Exercício:

								------ Usuário ------
								Nome:
								Sexo:
								Altura:
								Peso:
								Data de nascimento:
								E-mail:

								------ Detalhes do exercício ------
								Data:
								Tempo:
								Duração:
								Distância:
								Calorias perdidas:
								Passos:
								Velocidade média:
								Velocidade máxima:
								Ritmo médio:
								Ritmo máximo:
								Menor elevação:
								Maior elevação:

								------ Ritmo ------
								1 Km:
								2 Km:
								3 Km:
								4 Km:
								5 Km:
								6 Km:
     *
     *
     *
     * @param atividade Atividade a ser utilizada na criação do relatório.
     * @param conexaoBD Conexão com o banco de dados.
     * @return relatorio Retorna uma <code>String</code> que contém o relatório completo.
     */
    public static String obtemRelatorioExercicio(AtividadeFisica atividade, Connection conexaoBD){
    	String relatorio = "";
    	String ritmos = "";

    	relatorio += "Exercício: " + atividade.getExercicio() + System.lineSeparator();
    	relatorio += "\n------ Usuário ------" + System.lineSeparator();
		relatorio += "Nome: " + atividade.getUsuario().getNome() + System.lineSeparator();
		relatorio += "Sexo: " + atividade.getUsuario().getSexo() + System.lineSeparator();
		relatorio += "Altura: " + String.format("%1.2f m" + System.lineSeparator(), atividade.getUsuario().getAltura());
		relatorio += "Peso: " + String.format("%1.1f Kg" + System.lineSeparator(),atividade.getUsuario().getPeso());

		SimpleDateFormat dataNsc = new SimpleDateFormat("dd/MM/yyyy");
		relatorio += "Data de nascimento: " + dataNsc.format(atividade.getUsuario().getDataNascimento().getTime()) + System.lineSeparator();
		relatorio += "E-mail: " + atividade.getUsuario().getEmail() + System.lineSeparator();
		relatorio += "CPF: " + atividade.getUsuario().getCpf() + System.lineSeparator();
		relatorio += "Whatsapp: " + atividade.getUsuario().getWhatsapp() + System.lineSeparator();

		relatorio += "\n------ Detalhes do exercício ------" + System.lineSeparator();

		SimpleDateFormat dataEx = new SimpleDateFormat("dd/MM/yyyy");

		relatorio += "Data: " + dataEx.format(atividade.getData().getTime()) + System.lineSeparator();
		relatorio += "Tempo: " + atividade.getTempo() + System.lineSeparator();
		relatorio += "Duração: " + atividade.getDuracao() + System.lineSeparator();
		relatorio += "Distância: " + atividade.getDistancia() + " Km" + System.lineSeparator();
		relatorio += "Calorias perdidas: " + atividade.getCaloriasPerdidas() + " Kcal" + System.lineSeparator();
		relatorio += "Passos: " + atividade.getPassos() + System.lineSeparator();

		if (atividade instanceof AtividadeCompleta){
			AtividadeCompleta tipo1 = (AtividadeCompleta) atividade;

			relatorio += "Velocidade média: " + tipo1.getVelocidadeMedia() + "Km/h" + System.lineSeparator();
			relatorio += "Velocidade máxima: " + tipo1.getVelocidadeMaxima() + "Km/h" + System.lineSeparator();
			relatorio += "Ritmo médio: " + tipo1.getRitmoMedio() + System.lineSeparator();
			relatorio += "Ritmo máximo: " + tipo1.getRitmoMaximo() + System.lineSeparator();
			relatorio += "Menor elevação: " + tipo1.getMenorElevacao() + " m" + System.lineSeparator();
			relatorio += "Maior elevação: " + tipo1.getMaiorElevacao() + " m" + System.lineSeparator();
			relatorio += "\n------ Ritmo ------" + System.lineSeparator();

			List<Ritmo> ritmosList = InsercaoAtividadeCompleta.listaRitmos(InsercaoAtividadeCompleta.idAtividade(tipo1, conexaoBD), conexaoBD);
			Ritmo ritmo;

			for (int i = 0 ; i < ritmosList.size() ; i++){

				ritmo = ritmosList.get(i);

				ritmos += String.format("%1.2f Km: %02d'%02d''" + System.lineSeparator(), ritmo.getKM(),ritmo.getMinutos(),ritmo.getSegundos());
			}

			relatorio += ritmos;

		}

		// Gera o arquivo de relatório para ser utilzado posteriormente na geração do PDF.
		new ManipulaDados().geraRelatorio(atividade.getUsuario().getNome(),RELATORIO_EXERCICIO, relatorio);

    	return relatorio;

    } // obtemRelatorioExercicio()


    /**
     * Gera todo o relatório a partir do objeto <code>AtividadeTipo2</code> recebido como parâmetro.
     *
     * Utiliza o modelo:
     *
								 Exercício:

								------ Usuário ------
								Nome:
								Sexo:
								Altura:
								Peso:
								Data de nascimento:
								E-mail:

								------ Detalhes do exercício ------
								Data:
								Tempo:
								Duração:
								Distância:
								Calorias perdidas:
								Passos:
								Velocidade média:
								Velocidade máxima:
								Ritmo médio:
								Ritmo máximo:
								Menor elevação:
								Maior elevação:

								------ Ritmo ------
								1 Km:
								2 Km:
								3 Km:
								4 Km:
								5 Km:
								6 Km:
     *
     *
     *
     * @param atividade Atividade a ser utilizada na criação do relatório.
     * @param conexaoBD Conexão com o banco de dados.
     * @return relatorio Retorna uma <code>String</code> que contém o relatório completo.
     */
    public static String obtemRelatorioExercicio(AtividadeBasica atividade, Connection conexaoBD){
    	String relatorio = "";

    	relatorio += "Exercício: " + atividade.getExercicio() + System.lineSeparator();
    	relatorio += "\n------ Usuário ------" + System.lineSeparator();
		relatorio += "Nome: " + atividade.getUsuario().getNome() + System.lineSeparator();
		relatorio += "Sexo: " + atividade.getUsuario().getSexo() + System.lineSeparator();
		relatorio += "Altura: " + String.format("%1.2f m" + System.lineSeparator(), atividade.getUsuario().getAltura());
		relatorio += "Peso: " + String.format("%1.1f Kg" + System.lineSeparator(),atividade.getUsuario().getPeso());

		SimpleDateFormat dataNsc = new SimpleDateFormat("dd/MM/yyyy");
		relatorio += "Data de nascimento: " + dataNsc.format(atividade.getUsuario().getDataNascimento().getTime()) + System.lineSeparator();
		relatorio += "E-mail: " + atividade.getUsuario().getEmail() + System.lineSeparator();

		relatorio += "\n------ Detalhes do exercício ------" + System.lineSeparator();

		SimpleDateFormat dataEx = new SimpleDateFormat("dd/MM/yyyy");

		relatorio += "Data: " + dataEx.format(atividade.getData().getTime()) + System.lineSeparator();
		relatorio += "Tempo: " + atividade.getTempo() + System.lineSeparator();
		relatorio += "Duração: " + atividade.getDuracao() + System.lineSeparator();
		relatorio += "Distância: " + atividade.getDistancia() + " Km" + System.lineSeparator();
		relatorio += "Calorias perdidas: " + atividade.getCaloriasPerdidas() + " Kcal" + System.lineSeparator();
		relatorio += "Passos: " + atividade.getPassos() + System.lineSeparator();

		// Gera o arquivo de relatório para ser utilzado posteriormente na geração do PDF.
		new ManipulaDados().geraRelatorio(atividade.getUsuario().getNome(), RELATORIO_EXERCICIO, relatorio);

    	return relatorio;
    } // obtemRelatorioExercicio()

    /**
     * Cria e retorna um relatório detalhado de todos os clientes cadastrados no banco de dados.
     * Este relatório contém:
     *
     * - a maior duração de um exercício;
     * - a maior distância percorrida;
     * - o maior número de calorias perdidas;
     * - o maior número de passos dados;
     * - a velocidade mais rápida (máxima).
     *
     * @param conexaoBD Conexão com o banco de dados.
     * @return relatorioDetalhado Retorna uma <code>String</code> com os dados de todos os clientes.
     */
    public static String obtemRelatorioDetalhado(Connection conexaoBD){
    	String relatorioDetalhado = "";
    	List<AtividadeCompleta> atividade1List = InsercaoAtividadeCompleta.listaAtividades(conexaoBD), atividade1Cliente = new ArrayList<>();
    	List<AtividadeBasica> atividade2List = InsercaoAtividadeBasica.listaAtividades(conexaoBD), atividade2Cliente = new ArrayList<>();
    	List<String> listClientes = new ArrayList<String>();

    	String nome;
    	if (atividade1List.size() > 0 || atividade2List.size() > 0){
	    	// Obtém o e-mail do primeiro cliente da lista.
	    	nome = atividade1List.get(0).getUsuario().getNome();

	    	// Insere no arraylist.
	    	listClientes.add(nome);

	    	// Percorrendo a lista de atividadesTipo1 e adicionando o email dos clientes no array emailClientes.
	    	for (int i = 1 ; i < atividade1List.size() ; i++){
	    		// Altera o e-mail
	    		nome = new String(atividade1List.get(i).getUsuario().getNome());

	    		// Caso o nome não exista na lista emailClientes, insere o objeto na lista.
	    		if (Arrays.binarySearch(listClientes.toArray(), nome) < 0)
	    			listClientes.add(nome);
	    	}



	    	// Percorrendo a lista de atividadesTipo2 e adicionando o email dos clientes no array emailClientes.
	    	for (int i = 0 ; i < atividade2List.size() ; i++){
	    		// Altera o e-mail
	    		nome = new String(atividade2List.get(i).getUsuario().getNome());
	    		// Caso o nome não exista na lista emailClientes, insere o objeto na lista.
	    		if (Arrays.binarySearch(listClientes.toArray(), nome) < 0)
	    			listClientes.add(nome);
	    		}

			List<AtividadeCompleta> temp1 = null;
			List<AtividadeBasica> temp2 = null;

	    	for (int i = 0 ; i < listClientes.size() ; i++){
	    		// Obtém as listas de atividades por cliente.
	    		temp1 = InsercaoAtividadeCompleta.listaAtividadesPorCliente(listClientes.get(i), conexaoBD);
	    		temp2 = InsercaoAtividadeBasica.listaAtividadesPorCliente(listClientes.get(i), conexaoBD);

	    		if (temp1.size() > 0 || temp2.size() > 0){
	    			for (int j = 0 ; j < temp1.size() ; j++){
	    				atividade1Cliente.add(temp1.get(j));
	    			}
	    			for (int j = 0 ; j < temp2.size() ; j++){
	    				atividade2Cliente.add(temp2.get(j));
	    			}
	    		}
	    	}



	    	// Loop para percorrer toda lista de clientes.
	    	for (int i = 0 ; i < listClientes.size();i++){

	    		// Insere todos os dados no relatório.
	    			relatorioDetalhado += "**********************" + System.lineSeparator();
		    		relatorioDetalhado += "Cliente: " + atividade1Cliente.get(i).getUsuario().getNome() + System.lineSeparator();
		    		relatorioDetalhado += "\nMaior duração de um exercício: " + obtemMaiorDuracao(atividade1Cliente, atividade2Cliente) + System.lineSeparator();
		    		relatorioDetalhado += "Maior distância percorrida: " + obtemMaiorDistancia(atividade1Cliente, atividade2Cliente) + System.lineSeparator();
		    		relatorioDetalhado += "Maior número de calorias perdidas: " + obtemMaiorPerdaDeCalorias(atividade1Cliente, atividade2Cliente) + System.lineSeparator();
		    		relatorioDetalhado += "Maior número de passos dados: " + obtemMaiorNumeroDePassos(atividade1Cliente, atividade2Cliente) + System.lineSeparator();
		    		relatorioDetalhado += "Maior velocidade: " + obtemMaiorVelocidade(atividade1Cliente) + System.lineSeparator();
	    		}

			// Gera o arquivo de relatório para ser utilzado posteriormente na geração do PDF.
	    	new ManipulaDados().geraRelatorio("", RELATORIO_DETALHADO, relatorioDetalhado);
    	}

    	return relatorioDetalhado;
    } // obtemRelatorioDetalhado()

    /**
     * Cria um arquivo de relatório para uso posterior na geração do arquivo PDF.
     *
     * É possível a geração de dois tipo de relatório: relatório detalhado e relatório individual de um exercício.
     *
     * @param nome <code>String</code> com o nome do cliente.
     * @param tipoRelatorio <code>String</code> utilizada para verificação do tipo do relatório a ser gerado.
     * @param relatorio <code>String</code> com o conteúdo a ser gravado no arquivo.
     * @return <i>true<i> se criado com sucesso ou <i>false</i> caso falhe.
     */
    private boolean geraRelatorio(String nome, String tipoRelatorio, String relatorio){
    	ArquivoTexto arquivoRelatorio = new ArquivoTexto();
    	String nomeArquivo = "";

    	try {
    		// Verificação de tipo do relatório.
    		if (tipoRelatorio == RELATORIO_DETALHADO){
    			nomeArquivo = "arquivos" + File.separator + "relatorioDetalhado.txt";
    			arquivoRelatorio.criar(nomeArquivo);
    			arquivoRelatorio.abrir(nomeArquivo);
    		}
    		else{
    			if (tipoRelatorio == RELATORIO_EXERCICIO){
    				// Formatter utilizado para obter a hora do sistema, que será armazenado no nome do arquivo.
	    			DateTimeFormatter formater = DateTimeFormatter.ofPattern("hh:mm:ss");
	    			nomeArquivo = "arquivos" + File.separator + nome + " - rE" + LocalTime.now().format(formater).replace(":", "-") + ".txt";
	    			arquivoRelatorio.criar(nomeArquivo);
	    			arquivoRelatorio.abrir(nomeArquivo);
    			}
    			arquivoRelatorio.escrever(relatorio);
    			arquivoRelatorio.fechar();
    		}

			return true;
    	} catch (FileNotFoundException e) {
			Alert alerta = new Alert(AlertType.ERROR);
			alerta.setTitle("ERRO");
			alerta.setContentText("Erro na criação do arquivo de relatório !");
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return false;
    } // geraRelatorio()


    /**
     * Método utilizado para comparar as durações de todos os exercícios do cliente e retornar a maior duração entre todas
     * atividades realizadas.
     *
     * @param atividade1Cliente Lista com as <code>AtividadeTipo1</code>
     * @param atividade2Cliente Lista com as <code>AtividadeTipo2</code>
     *
     * @return maiorDuracao <code>String</code> com o valor da maior duração encontrada entre as atividades.
     */
	private static String obtemMaiorDuracao(List<AtividadeCompleta> atividade1Cliente, List<AtividadeBasica> atividade2Cliente){
    	String maiorDuracao;
    	String duracao;
    	String[] quebraDuracao;
    	List<String[]> listDuracoes = new ArrayList<String[]>();

    	// Percorre a lista atividade1Cliente e adiciona a duração na lista de durações.
    	for (int i = 0 ; i < atividade1Cliente.size() ; i++){
    		duracao = atividade1Cliente.get(i).getDuracao();
    		quebraDuracao = new String[duracao.split(":").length];
    		quebraDuracao = duracao.split(":");

    		listDuracoes.add(quebraDuracao);
    	}

    	// Percorre a lista atividade2Cliente e adiciona a duração na lista de durações.
    	for (int i = 0 ; i < atividade2Cliente.size() ; i++){
    		duracao = atividade2Cliente.get(i).getDuracao();
    		quebraDuracao = new String[duracao.split(":").length];
    		quebraDuracao = duracao.split(":");

    		listDuracoes.add(quebraDuracao);
    	}

    	Integer maiorH, maiorM, maiorS, h, m, s;

    	// Quebra a duração em Hora, Minuto e Segundo
    	maiorH = Integer.parseInt(listDuracoes.get(0)[0]);
    	maiorM = Integer.parseInt(listDuracoes.get(0)[1]);
    	maiorS = Integer.parseInt(listDuracoes.get(0)[2]);

    	// Loop comparando as durações
    	for (int i = 1 ; i < listDuracoes.size() ; i++){
    		h =  Integer.parseInt(listDuracoes.get(i)[0]);
    		m = Integer.parseInt(listDuracoes.get(i)[1]);
    		s = Integer.parseInt(listDuracoes.get(i)[2]);


    		if (h > maiorH){
    			maiorH = h;
    			maiorM = m;
    			maiorS = s;
    		}

    		else{
    			if (h == maiorH && m > maiorM){
    				maiorM = m;
    				maiorS = s;
    			}

    			else{
    				if (h == maiorH && m == maiorM && s > maiorS)
    					maiorS = s;
    			}
    		}
    	}

    	// Inicializa a string com a maior duração obtida no formato hh:mm:ss
    	maiorDuracao = String.format("%02d:%02d:%02d", maiorH,maiorM, maiorS);

    	return maiorDuracao;
    } // obtemMaiorDuracao()

	/**
	 * Método utilizado para comparar as distâncias percorridas em todos os exercícios do cliente e retornar a maior entre todas
     * atividades realizadas.
     *
     * @param atividade1Cliente Lista com as <code>AtividadeTipo1</code>
     * @param atividade2Cliente Lista com as <code>AtividadeTipo2</code>
	 * @return maiorDistancia <code>String</code> com a maior distância obtida nas comparações.
	 */
    private static String obtemMaiorDistancia(List<AtividadeCompleta> atividade1Cliente, List<AtividadeBasica> atividade2Cliente) {
		List<Float> distanciasList = new ArrayList<>();
		String maiorDistancia;

		// Loops inserindo todas as distâncias na lista.
		for (int i = 0 ; i < atividade1Cliente.size() ; i++)
			distanciasList.add(atividade1Cliente.get(i).getDistancia());

		for (int i = 0 ; i < atividade2Cliente.size() ; i++)
			distanciasList.add(atividade2Cliente.get(i).getDistancia());

		// Inicializa a maior distância com o primeiro objeto da lista.
		Float mDistancia = distanciasList.get(0);

		// Loop comparando todos os objetos e alterando (se necessário) o valor de mDistancia.
		for (int i = 1 ; i < distanciasList.size() ; i++){
			if (distanciasList.get(i) > mDistancia)
				mDistancia = distanciasList.get(i);
		}

		// Inicializa a string com o maior valor obtido no formato <0.00 Km>
		maiorDistancia = String.format("%1.2f Km", mDistancia);

		return maiorDistancia;
	} // obtemMaiorDistancia()

	/**
	 * Método utilizado para comparar as calorias perdidas em todos os exercícios do cliente e retornar a maior entre todas
     * atividades realizadas.
     *
     * @param atividade1Cliente Lista com as <code>AtividadeTipo1</code>
     * @param atividade2Cliente Lista com as <code>AtividadeTipo2</code>
	 * @return maiorPerda <code>String</code> com a maior perda de calorias obtida nas comparações.
	 */
	private static String obtemMaiorPerdaDeCalorias(List<AtividadeCompleta> atividade1Cliente, List<AtividadeBasica> atividade2Cliente) {
		List<Float> caloriasList = new ArrayList<>();
		String maiorPerda;

		// Loops inserindo todas as calorias na lista.
		for (int i = 0 ; i < atividade1Cliente.size() ; i++)
			caloriasList.add(atividade1Cliente.get(i).getCaloriasPerdidas());

		for (int i = 0 ; i < atividade2Cliente.size() ; i++)
			caloriasList.add(atividade2Cliente.get(i).getCaloriasPerdidas());

		// Inicializa a maior perda com o primeiro objeto da lista.
		Float maiorCaloria = caloriasList.get(0);

		// Loop comparando todos os objetos e alterando (se necessário) o valor de maiorCaloria.
		for (int i = 1 ; i < caloriasList.size() ; i++){
			if (caloriasList.get(i) > maiorCaloria)
				maiorCaloria = caloriasList.get(i);
		}

		// Inicializa a string com o maior valor obtido no formato <0.00 Kcal>
		maiorPerda = String.format("%1.2f Kcal", maiorCaloria);

		return maiorPerda;
	} // obtemMaiorPerdaDeCalorias()


	/**
	 * Método utilizado para comparar o número de passos dados pelo cliente em todos os exercícios e retornar a maior entre todas
     * atividades realizadas.
     *
     * @param atividade1Cliente Lista com as <code>AtividadeTipo1</code>
     * @param atividade2Cliente Lista com as <code>AtividadeTipo2</code>
	 * @return maiorNumPassos <code>String</code> com o maior número de passos dados.
	 */
	private static String obtemMaiorNumeroDePassos(List<AtividadeCompleta> atividade1Cliente, List<AtividadeBasica> atividade2Cliente) {
		List<Integer> passosList = new ArrayList<>();
		String maiorNumPassos;

		// Loops inserindo todas as contagens de passos na lista.
		for (int i = 0 ; i < atividade1Cliente.size() ; i++)
			passosList.add(atividade1Cliente.get(i).getPassos());

		for (int i = 0 ; i < atividade2Cliente.size() ; i++)
			passosList.add(atividade2Cliente.get(i).getPassos());


		// Inicializa o maior número de passos com o primeiro objeto da lista.
		Integer maiorPasso = passosList.get(0);

		// Loop comparando todos os objetos e alterando (se necessário) o valor de maiorPasso.
		for (int i = 1 ; i < passosList.size() ; i++){
			if (passosList.get(i) > maiorPasso)
				maiorPasso = passosList.get(i);
		}

		// Inicializa a string com o maior número de passos  obtido no formato nas comparações.
		maiorNumPassos = String.format("%d", maiorPasso);

		return maiorNumPassos;
	} // obtemMaiorNumeroDePassos()

	/**
	 * Método utilizado para comparar as velocidades dos exercícios e retornar a maior entre todas
     * atividades realizadas.
     *
     * @param atividade1Cliente Lista com as <code>AtividadeTipo1</code>
	 * @return maiorVelocidade <code>String</code> com a maior velocidade obtida com as comparações.
	 */
	private static String obtemMaiorVelocidade(List<AtividadeCompleta> atividade1Cliente) {
		List<Float> velocidadeList = new ArrayList<>();
		String maiorVelocidade;

		// Adiciona as velocidades na lista.
		for (int i = 0 ; i < atividade1Cliente.size() ; i++)
			velocidadeList.add(atividade1Cliente.get(i).getVelocidadeMaxima());

		// Inicializa o objeto com o primeiro valor da lista.
		Float velocidade = velocidadeList.get(0);

		// Loop comparando todas as velocidades da lista.
		for (int i = 1 ; i < velocidadeList.size() ; i++){
			if (velocidadeList.get(i) > velocidade)
				velocidade = velocidadeList.get(i);
		}

		// Inicializa a string com o maior valor obtido no formato (0.00 Km/h)
		maiorVelocidade = String.format("%1.2f Km/h", velocidade);

		return maiorVelocidade;
	} // obtemMaiorVelocidade()


	/**
	 * Método utilizado para criação dos gráficos. O método trabalha de acordo com o valor de <code>tipoGrafico</code>.
	 *<br>
	 * Valores possíveis:
	 * <br>- 1: Gráfico de colunas (Duração)
	 * <br>- 2: Gráfico de colunas (Distância)
	 * <br>- 3: Gráfico de colunas (Calorias)
	 * <br>- 4: Gráfico de colunas (Passos)
	 * <br>- 5: Gráfico de colunas (Velocidade Média)
	 * <br>- 6: Gráfico de colunas (Ritmo Médio)
	 * <br>- 7: Gráfico de linhas (Distância)
	 * <br>- 8: Gráfico de linhas (Calorias)
	 * <br>- 9: Gráfico de linhas (Passos)
	 * <br>- 10: Gráfico de colunas completo (Duração)
	 * <br>- 11: Gráfico de colunas completo (Distância)
	 * <br>- 12: Gráfico de colunas completo (Calorias)
	 * <br>- 13: Gráfico de colunas completo (Passos)
	 * <br>- 14: Gráfico de colunas completo (Velocidade Média)
	 * <br>- 15: Gráfico de colunas completo (Ritmo Médio)
	 *
	 * @param tipoGrafico Valor inteiro que determina o tipo de gráfico a ser gerado.
	 * @param nomeExercicio <code>String</code> com o nome do exercício a ser utilizado nos gráficos.
	 * @param atividadesList <code>List</code> com a relação das atividades do cliente, já separadas pelo período.
	 * @param linhas Array de objetos <code>Label</code>, que serão utilizados nos gráficos 10 à 15. Nos outros gráficos, deve receber <i>null</i>.
	 *
	 * @return grafico Retorna o objeto <code>JFreeChart</code> gerado.
	 *
	 */
	/*public static JFreeChart geraGraficos(int tipoGrafico, String nomeExercicio, List<AtividadeFisica> atividadesList, Label[] linhas){
		ManipulaDados manipulacao = new ManipulaDados();
		AtividadeFisica atividade = null;
		SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy");
		String exercicio = "", data = "";
		DefaultCategoryDataset dados = new DefaultCategoryDataset();
		JFreeChart grafico = null;
		int totalPassos = 0;
		boolean verificador = false;
		double distanciaMedia, distanciaTotal, mediaCalorias, totalCalorias;
		distanciaMedia = distanciaTotal = mediaCalorias = totalCalorias = 0;
		OutputStream saida;
		String nomeArquivo = "";

		// Loop utilizado para percorrer a lista de atividades.
		for (int i = 0 ; i < atividadesList.size() ;i++){
			atividade = atividadesList.get(i);
			exercicio = atividade.getExercicio();

			 Verifica se o nome do exercício é o mesmo recebido como parâmetro ou se a variável verificador é true.
			 * A variável verificador é utilizada para criação dos gráficos 10-15.

			if (atividade.getExercicio().equalsIgnoreCase(nomeExercicio) || verificador){
				data = formatadorData.format(atividade.getData().getTime());

				if (atividade instanceof AtividadeCompleta){
					AtividadeCompleta tipo1 = (AtividadeCompleta) atividade;
					if (tipoGrafico == 5)
						dados.addValue(tipo1.getVelocidadeMedia(), exercicio, data);
					else
						if (tipoGrafico == 6)
							dados.addValue(manipulacao.getRitmoMedio(tipo1.getRitmoMedio()), exercicio, data);
				}

				switch (tipoGrafico) {
				case 1:
					dados.addValue(manipulacao.getMinutosDuracao(atividade.getDuracao()), exercicio, data);
					grafico = ChartFactory.createBarChart3D(DURACAO, COMPARADOR_DATA, COMPARADOR_DURACAO, dados);
					break;
				case 2:
					dados.addValue(atividade.getDistancia(), exercicio, data);
					grafico = ChartFactory.createBarChart3D(DISTANCIA, COMPARADOR_DATA, COMPARADOR_DISTANCIA, dados);
					break;
				case 3:
					dados.addValue(atividade.getCaloriasPerdidas(), exercicio, data);
					grafico = ChartFactory.createBarChart3D(CALORIAS, COMPARADOR_DATA, COMPARADOR_CALORIAS, dados);
					break;
				case 4:
					dados.addValue(atividade.getPassos(), exercicio, data);
					grafico = ChartFactory.createBarChart3D(PASSOS, COMPARADOR_DATA, COMPARADOR_PASSOS, dados);
					break;
				case 5:
					grafico = ChartFactory.createBarChart3D(VELOCIDADE_MEDIA, COMPARADOR_DATA, COMPARADOR_VM, dados);
					break;
				case 6:
					grafico = ChartFactory.createBarChart3D(RITMO_MEDIO, COMPARADOR_DATA, COMPARADOR_RM, dados);
					break;
				case 7:
					dados.addValue(0, exercicio, data);
					dados.addValue(atividade.getDistancia(), exercicio, data);
					grafico = ChartFactory.createLineChart(DISTANCIA, COMPARADOR_DATA, COMPARADOR_DISTANCIA, dados);
					break;
				case 8:
					dados.addValue(0, exercicio, data);
					dados.addValue(atividade.getCaloriasPerdidas(), exercicio, data);
					grafico = ChartFactory.createLineChart(CALORIAS, COMPARADOR_DATA, COMPARADOR_CALORIAS, dados);
					break;
				case 9:
					dados.addValue(0, exercicio, data);
					dados.addValue(atividade.getPassos(), exercicio, data);
					grafico = ChartFactory.createLineChart(PASSOS, COMPARADOR_DATA, COMPARADOR_PASSOS, dados);
					break;
				}
			}
			else{
				// Procedimentos utilizados para geração do gráfico completo.
				if (tipoGrafico > 9){
					totalPassos += atividade.getPassos();
					distanciaTotal += atividade.getDistancia();
					totalCalorias += atividade.getCaloriasPerdidas();

					if ( i == atividadesList.size() - 1){
						distanciaMedia = distanciaTotal / atividadesList.size();
						mediaCalorias = totalCalorias / atividadesList.size();

						 Após a realização de todos os cálculos, o valor do for é reiniciado e a variável verificador é setada como true,
						 * autorizando a criação dos gráficos.

						i = -1;
						verificador = true;

						switch (tipoGrafico) {
						case 10:
							tipoGrafico = 1;
							break;
						case 11:
							tipoGrafico = 2;
							break;
						case 12:
							tipoGrafico = 3;
							break;
						case 13:
							tipoGrafico = 4;
							break;
						case 14:
							tipoGrafico = 5;
							break;
						case 15:
							tipoGrafico = 6;
							break;
						}
					}

					// Altera os labels do gráfico.
					linhas[0].setText(String.format("%1.2f Kcal", totalCalorias));
					linhas[1].setText(String.format("%1.2f Kcal", mediaCalorias));
					linhas[2].setText(String.format("%1.2f Km", distanciaTotal));
					linhas[3].setText(String.format("%1.2f", distanciaMedia));
					linhas[4].setText(String.format("%d", totalPassos));
				}
			}
		} // for ()

		// Define o nome do arquivo de saída para adição posterior ao PDF.
		switch (tipoGrafico) {
			case 1:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Duração).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 2:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Distância).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 3:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Calorias).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 4:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Passos).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 5:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Velocidade Média).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 6:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Ritmo Médio).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 7:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Linhas - Distância).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 8:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Linhas - Calorias).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 9:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Linhas - Passos).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;

		}

		try {
			saida = new FileOutputStream(nomeArquivo);
			// Exporta uma imagem .jpeg do gráfico.
			ChartUtilities.writeChartAsJPEG(saida, grafico, 680, 430);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return grafico;
} // geraGraficos()
*/


	public static void preencheGrafico(int tipoGrafico, BarChart<String, Number> grafico, Connection conexaoBD, LocalDate dataInicial, LocalDate dataFinal, String nomeCliente, Label[] linhas){
		List<AtividadeCompleta> atividadesList1 = InsercaoAtividadeCompleta.listaAtividadesPorCliente(nomeCliente, conexaoBD);
		List<AtividadeBasica> atividadesList2 = InsercaoAtividadeBasica.listaAtividadesPorCliente(nomeCliente, conexaoBD);
		Calendar dataInicialCalendar, dataFinalCalendar;
		AtividadeFisica atividade = null;
    	XYChart.Series<String, Number> exercicio;
		XYChart.Data<String, Number> dados;
		SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy");
		ManipulaDados manipulacao = new ManipulaDados();
		boolean verificador = false;
		int totalPassos = 0;
		double distanciaMedia, distanciaTotal, mediaCalorias, totalCalorias;
		distanciaMedia = distanciaTotal = mediaCalorias = totalCalorias = 0;

		grafico.getData().clear();

		dataInicialCalendar = Calendar.getInstance();
		dataInicialCalendar.set(dataInicial.getYear(), dataInicial.getMonthValue()-1, dataInicial.getDayOfMonth());

		dataFinalCalendar = Calendar.getInstance();
		dataFinalCalendar.set(dataFinal.getYear(), dataFinal.getMonthValue() - 1, dataFinal.getDayOfMonth());

		List<AtividadeFisica> atividadesList = new ArrayList<>();

		for (int i = 0 ; i < atividadesList1.size() ; i++){
			atividade = atividadesList1.get(i);
			if (!atividade.getData().before(dataInicialCalendar) || !atividade.getData().after(dataFinalCalendar))
				atividadesList.add(atividade);
		}


		for (int i = 0 ; i < atividadesList2.size() ; i++){
			atividade = atividadesList2.get(i);
			if (!atividade.getData().before(dataInicialCalendar) || !atividade.getData().after(dataFinalCalendar))
				atividadesList.add(atividadesList2.get(i));
		}

		for (int i = 0 ; i < atividadesList.size() ; i ++){
			if (tipoGrafico <=6 ||  verificador){
				dados = new XYChart.Data<>();
				exercicio = new XYChart.Series<>();
				atividade = atividadesList.get(i);

				exercicio.setName(atividade.getExercicio());

				if (atividade instanceof AtividadeCompleta){
					AtividadeCompleta tipo1 = (AtividadeCompleta) atividade;
					exercicio.setName(tipo1.getExercicio());
					if (tipoGrafico == 5)
						dados.setYValue(tipo1.getVelocidadeMedia());
					else
						if (tipoGrafico == 6){
							dados.setYValue(manipulacao.getRitmoMedio(tipo1.getRitmoMedio()));
						}
				}

				dados.setXValue(formatadorData.format(atividade.getData().getTime()));

				switch (tipoGrafico) {
				case 1:
					dados.setYValue(manipulacao.getMinutosDuracao(atividade.getDuracao()));
					break;
				case 2:
					dados.setYValue(atividade.getDistancia());
					break;
				case 3:
					dados.setYValue(atividade.getCaloriasPerdidas());
					break;
				case 4:
					dados.setYValue(atividade.getPassos());
					break;
				}

				exercicio.getData().add(dados);
				grafico.getData().add(exercicio);

		}
		else{
			// Procedimentos utilizados para geração do gráfico completo.
			if (tipoGrafico > 9){
				totalPassos += atividade.getPassos();
				distanciaTotal += atividade.getDistancia();
				totalCalorias += atividade.getCaloriasPerdidas();

				if ( i == atividadesList.size() - 1){
					distanciaMedia = distanciaTotal / atividadesList.size();
					mediaCalorias = totalCalorias / atividadesList.size();

					 /*Após a realização de todos os cálculos, o valor do for é reiniciado e a variável verificador é setada como true,
					 * autorizando a criação dos gráficos.*/

					i = -1;
					verificador = true;

					switch (tipoGrafico) {
					case 10:
						tipoGrafico = 1;
						break;
					case 11:
						tipoGrafico = 2;
						break;
					case 12:
						tipoGrafico = 3;
						break;
					case 13:
						tipoGrafico = 4;
						break;
					case 14:
						tipoGrafico = 5;
						break;
					case 15:
						tipoGrafico = 6;
						break;
					}
				}

				// Altera os labels do gráfico.
				linhas[0].setText(String.format("%1.2f Kcal", totalCalorias));
				linhas[1].setText(String.format("%1.2f Kcal", mediaCalorias));
				linhas[2].setText(String.format("%1.2f Km", distanciaTotal));
				linhas[3].setText(String.format("%1.2f", distanciaMedia));
				linhas[4].setText(String.format("%d", totalPassos));
			}
		}
		}

	} // preencheGrafico()

public static void preencheGrafico(int tipoGrafico, LineChart<String, Number> grafico, Connection conexaoBD, LocalDate dataInicial, LocalDate dataFinal, String nomeCliente){
	List<AtividadeCompleta> atividadesList1 = InsercaoAtividadeCompleta.listaAtividadesPorCliente(nomeCliente, conexaoBD);
	List<AtividadeBasica> atividadesList2 = InsercaoAtividadeBasica.listaAtividadesPorCliente(nomeCliente, conexaoBD);
	Calendar dataInicialCalendar, dataFinalCalendar;
	AtividadeFisica atividade;
	XYChart.Series<String, Number> exercicio;
	XYChart.Data<String, Number> dados;
	SimpleDateFormat formatadorData = new SimpleDateFormat("dd/MM/yyyy");
	ManipulaDados manipulacao = new ManipulaDados();

	grafico.getData().clear();

	dataInicialCalendar = Calendar.getInstance();
	dataInicialCalendar.set(dataInicial.getYear(), dataInicial.getMonthValue()-1, dataInicial.getDayOfMonth());

	dataFinalCalendar = Calendar.getInstance();
	dataFinalCalendar.set(dataFinal.getYear(), dataFinal.getMonthValue() - 1, dataFinal.getDayOfMonth());

	List<AtividadeFisica> atividadesList = new ArrayList<>();

	for (int i = 0 ; i < atividadesList1.size() ; i++){
		atividade = atividadesList1.get(i);
		if (!atividade.getData().before(dataInicialCalendar) || !atividade.getData().after(dataFinalCalendar))
			atividadesList.add(atividade);
	}


	for (int i = 0 ; i < atividadesList2.size() ; i++){
		atividade = atividadesList2.get(i);
		if (!atividade.getData().before(dataInicialCalendar) || !atividade.getData().after(dataFinalCalendar))
			atividadesList.add(atividadesList2.get(i));
	}

	for (int i = 0 ; i < atividadesList.size() ; i ++){
		dados = new XYChart.Data<>();
		exercicio = new XYChart.Series<>();

		atividade = atividadesList.get(i);

		exercicio.setName(atividade.getExercicio());

		if (atividade instanceof AtividadeCompleta){
			AtividadeCompleta tipo1 = (AtividadeCompleta) atividade;
			exercicio.setName(tipo1.getExercicio());
			if (tipoGrafico == 5)
				dados.setYValue(tipo1.getVelocidadeMedia());
			else
				if (tipoGrafico == 6){
					dados.setYValue(manipulacao.getRitmoMedio(tipo1.getRitmoMedio()));
				}
		}

		dados.setXValue(formatadorData.format(atividade.getData().getTime()));

		switch (tipoGrafico) {
		case 1:
			dados.setYValue(atividade.getDistancia());
			break;
		case 2:
			dados.setYValue(atividade.getCaloriasPerdidas());
			break;
		case 3:
			dados.setYValue(atividade.getPassos());
			break;
		}

		exercicio.getData().add(dados);
		grafico.getData().add(exercicio);
		}
	} // preencheGrafico()

	/**
	 * Percorre uma lista de atividades. Utilizado para preencher o componente ChoiceBox.
	 *
	 * @param atividadesList1 Lista de objetos com as atividades completas.
	 * @param atividadesList2 Lista de objetos com as atividades básicas.
	 * @param dataInicial Data de início do intervalo.
	 * @param dataFinal Data de fim do intervalo.
	 * @return Retorna uma lista com os objetos <code>String</code> obtidos na busca de todas as atividades, sem repetição.
	 */
	public static List<String> listaAtividadesPeriodo(List<AtividadeCompleta> atividadesList1, List<AtividadeBasica> atividadesList2, LocalDate dataInicial, LocalDate dataFinal){
		List<String> atividadesList = new ArrayList<>();
		AtividadeFisica atividade;
		Calendar dataInicialCalendar, dataFinalCalendar;
		String exercicio;

		dataInicialCalendar = Calendar.getInstance();
		dataInicialCalendar.set(dataInicial.getYear(), dataInicial.getMonthValue()-1, dataInicial.getDayOfMonth());

		dataFinalCalendar = Calendar.getInstance();
		dataFinalCalendar.set(dataFinal.getYear(), dataFinal.getMonthValue() - 1, dataFinal.getDayOfMonth());

		for (int i = 0 ; i < atividadesList1.size() ; i++){
			// Obtem o objeto atividade.
			atividade = atividadesList1.get(i);

			// Se a atividade estiver dentro da data recebida, cria um novo objeto string com o nome do exercício.
			if (!atividade.getData().before(dataInicialCalendar) || !atividade.getData().after(dataFinalCalendar)){
				exercicio = new String(atividade.getExercicio());
				// Realiza uma busca na lista. Se o exercício não for encontrado, é inserido.
				if (Arrays.binarySearch(atividadesList.toArray(), exercicio) < 0)
					atividadesList.add(exercicio);
			}
		}

		for (int i = 0 ; i < atividadesList2.size() ; i++){
			atividade = atividadesList2.get(i);
			if (!atividade.getData().before(dataInicialCalendar) || !atividade.getData().after(dataFinalCalendar)){
				exercicio = new String(atividade.getExercicio());
				if (Arrays.binarySearch(atividadesList.toArray(), exercicio) < 0)
					atividadesList.add(exercicio);
			}
		}
		return atividadesList;
	} // listaAtividadesPeriodo()

	/**
	 * Percorre as listas de Atividade Completa e Atividade Básica.
	 * @param atividadesList1 Lista com Atividade Completa.
	 * @param atividadesList2 Lista com Ativida de Básica..
	 * @return Retorna um único array com todas atividades.
	 */
	public static List<AtividadeFisica> listaAtividades(List<AtividadeCompleta> atividadesList1, List<AtividadeBasica> atividadesList2){
		List<AtividadeFisica> atividadesList = new ArrayList<>();
		AtividadeFisica atividade;

		for (int i = 0 ; i < atividadesList1.size() ; i++){
			atividade = atividadesList1.get(i);
			atividadesList.add(atividade);
		}

		for (int i = 0 ; i < atividadesList2.size() ; i++){
			atividade = atividadesList2.get(i);
			atividadesList.add(atividade);
		}
		return atividadesList;
	} // listaAtividadesPeriodo()

	/**
	 * Método que converte a duração do exercício para minutos.
	 * @param duracao <code>String</code> que contém a duração no formato hh:mm:ss
	 * @return totalMinutos inteiro com o valor total de minutos do exercício.
	 */
	private int getMinutosDuracao(String duracao){
		int totalMinutos, hora, min;

		hora = Integer.parseInt(duracao.substring(0, duracao.indexOf(":")));
		min = Integer.parseInt(duracao.substring(duracao.indexOf(":")+1, duracao.lastIndexOf(":")-1));

		totalMinutos = (hora * 60) + min;

		return totalMinutos;
	} // getMinutosDuracao()

	/**
	 * Método utilizado para converter o valor de ritmo para utilização no gráfico.
	 * @param ritmo
	 * @return
	 */
	private float getRitmoMedio(LocalTime ritmo){
		float ritmoMedio, min, seg;

		min = ritmo.getMinute();
		seg = ritmo.getSecond() / 60;

		ritmoMedio = min + seg;

		return ritmoMedio;
	} // getRitmoMedio()

	/**
	 * Realiza toda a operação de exportação dos dados para o PDF. Obtém os arquivos armazenados na pasta "arquivos" e insere no arquivo PDF.
	 * O nome do arquivo é gerado com a hora obtida do sistema.
	 *
	 * @return Retorna <i>true</i> se o arquivo for gerado com sucesso, <i>false</i> caso contrário.
	 */
	public boolean exportaPDF(){
		Document arquivoPDF = new Document();
    	File folder = new File("arquivos\\"), destino;
    	Image graficoImage;
    	Paragraph relatorio;
    	Alert alerta;
    	DirectoryChooser caminhoSalvar = new DirectoryChooser();
    	String caminhoDestino, extensao;
    	ArquivoTexto arquivoRelatorio;
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");

    	// Abre a janela para escolha do destino do arquivo.
    	caminhoSalvar.setTitle("Selecione o destino do arquivo PDF...");
    	destino = caminhoSalvar.showDialog(null);
    	caminhoDestino = destino.getAbsolutePath();

    	try {
    		// Cria o arquivo PDF.
			PdfWriter writer = PdfWriter.getInstance(arquivoPDF, new FileOutputStream(caminhoDestino + File.separator +
																						"PDF - " + LocalTime.now().format(formatter).replace(":", "-") + ".pdf"));

			arquivoPDF.open();

			// Percorre os arquivos da pasta.
			for (File arquivo : folder.listFiles()){
				extensao = arquivo.getName().substring(arquivo.getName().lastIndexOf(".")+1);

				// Verifica a extensão do arquivo. Caso seja txt, é um relatório. Insere os dados do relatório no PDF e exclui os arquivos.
				if (extensao.equals("txt")){
					arquivoRelatorio = new ArquivoTexto();
					arquivoRelatorio.abrir(arquivo.getAbsolutePath());
					relatorio = new Paragraph(arquivoRelatorio.ler());
					arquivoPDF.add(relatorio);
					arquivo.delete();
				}

			}

			for (File arquivo : folder.listFiles()){
				extensao = arquivo.getName().substring(arquivo.getName().lastIndexOf(".")+1);
				// Caso seja jpeg, é um gráfico. Insere as imagens no PDF e exclui os arquivos.
				if (extensao.equals("jpeg")){
					graficoImage = Image.getInstance("arquivos" + File.separator + arquivo.getName());
					arquivoPDF.setPageSize(graficoImage);
					arquivoPDF.newPage();
					graficoImage.setAbsolutePosition(0, 0);
					arquivoPDF.add(graficoImage);
				}
			}

			arquivoPDF.close();
			writer.close();

			alerta = new Alert(AlertType.CONFIRMATION);
			alerta.setTitle("SUCESSO");
			alerta.setContentText("PDF exportado com sucesso!");
			alerta.showAndWait();

			return true;

    	} catch (FileNotFoundException e) {
    		alerta = new Alert(AlertType.ERROR);
    		alerta.setTitle("ERRO");
    		alerta.setContentText("Falha na criação do arquivo PDF !" );
		} catch (DocumentException e) {
			alerta = new Alert(AlertType.ERROR);
			alerta.setTitle("ERRO");
			alerta.setContentText("Erro na manipulação do PDF !");
		} catch (IOException e) {
			alerta = new Alert(AlertType.ERROR);
			alerta.setTitle("ERRO");
			alerta.setContentText("Erro na leitura dos arquivos !" );
		}
    	return false;
	} // exportarPDF()

} // class ManipulaDados
