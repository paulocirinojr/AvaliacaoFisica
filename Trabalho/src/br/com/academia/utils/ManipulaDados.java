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
 * Cont�m todos os m�todos que ser�o utilizados para manipula��o dos dados para inser��o no banco de dados.
 * @author Paulo
 *
 */
public class ManipulaDados {
	public static final String DURACAO = "Dura��o do Exerc�cio",
											 DISTANCIA = "Dist�ncia Percorrida",
											 CALORIAS = "Calorias Perdidas",
											 PASSOS = "N�mero de Passos",
											 VELOCIDADE_MEDIA = "Velocidade M�dia",
											 RITMO_MEDIO = "Ritmo M�dio",
											 COMPARADOR_DATA = "Data",
											 COMPARADOR_DURACAO = "Dura��o (min.)",
											 COMPARADOR_DISTANCIA = "Dist�ncia (Km)",
											 COMPARADOR_CALORIAS = "Calorias (Kcal)",
											 COMPARADOR_PASSOS = "Passos",
											 COMPARADOR_VM = "Velocidade M�dia (Km/h)",
											 COMPARADOR_RM = "Ritmo M�dio",
											 RELATORIO_DETALHADO = "*** Relat�rio Detalhado ***",
											 RELATORIO_EXERCICIO = "*** Relat�rio ***";

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

				// Se a vari�vel tipoAtividade recebe 1, a atividade � do tipo AtividadeCompleta.
				if (tipoAtividade == 1){
					// String que recebe todo o conte�do do arquivo.
					String leituraArquivo = arquivo.ler();
					// Cria o objeto a ser preenchido.
					AtividadeCompleta atividade = new AtividadeCompleta();
					String dadosUsuario;

					// Recebe o nome do exerc�cio.
					dadosUsuario = leituraDados(0, leituraArquivo);
					// Altera no objeto.
					atividade.setExercicio(dadosUsuario);

					// Recebe os dados do usu�rio.
					dadosUsuario = leituraDados(1, leituraArquivo);
					// Quebra os dados em linhas e adiciona em um array.
					String[] user = dadosUsuario.split("\n");

					// Cria o objeto Usuario a ser preenchido com os dados.
					Aluno usuario = new Aluno();

					// Vari�vel utilizada para contar as linhas dos dados.
					int contLinha = 0;

					// Altera os dados do usu�rio de acordo com a linha.
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

					// Obt�m os detalhes do exerc�cio.
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

					// Obt�m os dados adicionais do exerc�cio (ritmos).
					dadosUsuario = leituraDados(3, leituraArquivo);

					// Quebra os dados em um array.
					String[] ritmos = dadosUsuario.split("\n");
					// Objeto a ser utilizado no preenchimento dos dados.
					Ritmo ritmo;
					float KM;
					int min, seg;
					// Lista de ritmos do exerc�cio.
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
					// Se a vari�vel tipoAtividade recebe 2, a atividade � do tipo AtividadeBasica.
					if (tipoAtividade == 2){
						// String que recebe todo o conte�do do arquivo.
						String leituraArquivo = arquivo.ler();
						// Cria o objeto a ser preenchido.
						AtividadeBasica atividade = new AtividadeBasica();
						String dadosUsuario;

						// Recebe o nome do exerc�cio.
						dadosUsuario = leituraDados(0, leituraArquivo);
						// Altera no objeto.
						atividade.setExercicio(dadosUsuario);

						// Recebe os dados do usu�rio.
						dadosUsuario = leituraDados(1, leituraArquivo);
						// Quebra os dados em linhas e adiciona em um array.
						String[] user = dadosUsuario.split("\n");

						// Cria o objeto Usuario a ser preenchido com os dados.
						Aluno usuario = new Aluno();

						// Vari�vel utilizada para contar as linhas dos dados.
						int contLinha = 0;

						// Altera os dados do usu�rio de acordo com a linha.
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

						// Obt�m os detalhes do exerc�cio.
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
     * Cria um objeto <code>Alert</code> com os dados recebidos do usu�rio.
     *
     * @param titulo T�tulo da caixa de di�logo.
     * @param mensagem Mensagem a ser exibida.
     * @param tipo Tipo de alerta a ser inserido na caixa de di�logo.
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
	 * @param arquivo Arquivo texto escolhido pelo usu�rio.
	 * @return Retorna 1 caso seja uma atividade com mais detalhes (Corrida, Caminhada) ou 2 caso contr�rio.
	 */
    public static int verificaAtividade(ArquivoTexto arquivo){
    	String leitura = arquivo.ler();

    	// Verifica o arquivo em busca de dados mais espec�ficos apenas contidos em atividades como caminhada.
    	if (leitura.contains("------ Ritmo ------")){
    		return 1;
    	}
    	// Atividades "b�sicas"
    	else{
    		return 2;
    	}
    } // verificaAtividade()

    /**
     * Realiza a leitura dos dados de acordo com o <code> tipoLeitura</code> informado pelo usu�rio.
     * @param tipoLeitura O usu�rio informa o tipo de leitura necess�rio.
     * <br>	Valor 0: Nome do exerc�cio
     * <br>Valor 1: Dados do usu�rio
     * <br>Valor 2: Detalhes do exerc�cio
     * <br>Valor 3: Detalhes adicionais do exerc�cio<br><br>
     * @param dadosArquivo <code>String</code> que cont�m todos os dados contidos no arquivo lido.
     * @return Retorna a <code>String</code> requisitada pelo usu�rio.
     */
    public static String leituraDados(int tipoLeitura, String dadosArquivo){
    	String[] linhas = dadosArquivo.split("\n");
    	String dados;
    	int inicioLeitura=-1, fimLeitura=-1;

    	// Obt�m o nome do exerc�cio.
    	if (tipoLeitura == 0){
    		return linhas[0].substring(linhas[0].indexOf(":")+2);
    	}

    	// Ler dados usu�rio
    	if (tipoLeitura == 1){
    		for (int i = 0 ; i < linhas.length ; i++){
    			if (linhas[i].contains("------ Usu�rio ------"))
    				// Inicia a leitura dos dados ap�s a linha acima.
    				inicioLeitura = i+1;
    			else{
    				if (linhas[i].contains("------ Detalhes do exerc�cio ------")){
    					// Termina a leitura dos dados antes da linha acima.
    					fimLeitura = i-1;
    					break;
    				}
    			}
    		}
    	}

    		// Ler detalhes do exerc�cio
    		if (tipoLeitura == 2){
    			for (int i = 0 ; i < linhas.length ; i++){
        			if (linhas[i].contains("------ Detalhes do exerc�cio ------"))
        				// Inicia a leitura dos dados ap�s a linha acima.
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
        				// Inicia a leitura dos dados ap�s a linha acima.
        				inicioLeitura = i+1;
        				// Termina a leitura no final do arquivo.
    					fimLeitura = linhas.length;
        				break;
        			}
    			}
    		}

    		if (inicioLeitura != -1 && fimLeitura != -1){
    			dados = "";
    			// Loop que percorre os dados de acordo com as posi��es informadas e concatena em uma String.
	    		for (int k = inicioLeitura ; k < fimLeitura ; k++)
	    				dados += linhas[k] + "\n";

	    		// Retorna os dados requisitados pelo usu�rio.
	    		return dados;
	    	}
    	return "";
    } // leituraDados()

    /**
     * Recebe uma linha no formato "Data de nascimento: DD/MM/AAAA", quebra a data e retorna um array de inteiros, separados por dia, m�s e ano.
     * @param data <code>String</code> com os dados.
     * @return Retorna um array preenchido com os dados recebidos.
     */

    public static int[] obtemData(String data){
    	// Quebra a string usando o separador "/" e insere em um array de Strings.
    	String[] date = data.substring(data.indexOf(":")+2).split("/");

    	// Inicializa um array de inteiros para inser��o dos dados.
    	int dt[] = new int[3];

    	// Loop percorrendo o array de Strings e inserindo os dados no array de inteiros, de acordo com a
    	// ordem dos dados: DD/MM/AAAA
    	for (int i = 0; i < date.length ; i++)
    		dt[i] = Integer.parseInt(date[i]);

    	return dt;
    } // obtemData()

    /**
     * Recebe a linha que cont�m o e-mail e retorna os dados prontos para o usu�rio.
     * @param email <code>String</code> que cont�m os dados.
     * @return Retorna o e-mail recebido como par�metro.
     */

    public static String obtemEmail(String email){
    	return email.substring(email.indexOf(":")+2, email.length());
    } // obtemEmail()

    /**
     * Recebe a linha que cont�m o nome do cliente e retorna os dados prontos para o usu�rio.
     * @param nome <code>String</code> que cont�m os dados.
     * @return Retorna o nome do cliente recebido como par�metro.
     */

    public static String obtemNome(String nome){
    	return nome.substring(nome.indexOf(":")+2,nome.length());
    } // obtemNome()

    /**
     * Recebe a linha que cont�m o sexo do cliente e retorna os dados prontos para o usu�rio.
     * @param sexo <code>String</code> que cont�m os dados.
     * @return Retorna o sexo do cliente recebido como par�metro.
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
     * Recebe a linha que cont�m a hora de in�cio e t�rmino do exerc�cio e retorna os dados prontos para o usu�rio.
     * @param tempo <code>String</code> que cont�m os dados.
     * @return Retorna a hora de in�cio e t�rmino do exerc�cio.
     */
    public static String obtemTempo(String tempo){
    	return tempo.substring(tempo.indexOf(":")+2,tempo.length());
    } // obtemTempo()

    /**
     * Recebe a linha que cont�m o tempo de dura��o do exerc�cio e retorna os dados prontos para o usu�rio.
     * @param duracao <code>String</code> que cont�m os dados.
     * @return Retorna o tempo de dura��o do exerc�cio.
     */
    public static String obtemDuracao(String duracao){
    	return duracao.substring(duracao.indexOf(":")+2,duracao.length());
    } // obtemDuracao()

    /**
     * Recebe a linha que cont�m a dist�ncia percorrida e retorna os dados prontos para o usu�rio.
     * @param distancia <code>String</code> que cont�m os dados.
     * @return Retorna a dist�ncia percorrida..
     */
    public static float obtemDistancia(String distancia){
    	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como par�metro. Utiliza o m�todo
    	 * substring para retirar o in�cio da linha (Dist�ncia: ).
    	 */
    	StringBuilder strDistancia = new StringBuilder(distancia.substring(distancia.indexOf(":") + 2,distancia.indexOf("Km") - 1));
    	Float distance;

    	if (distancia.contains(","))
    		// Substitui a "," pelo "." para evitar exce��o na cria��o do objeto Float.
    		strDistancia.replace(strDistancia.indexOf(","), strDistancia.indexOf(",")+1,	".");

    	distance = Float.parseFloat(strDistancia.toString());

    	return distance;
    } // obtemDistancia()

    /**
     * Recebe a linha que cont�m as calorias perdidas e retorna os dados prontos para o usu�rio.
     * @param calorias <code>String</code> que cont�m os dados.
     * @return Retorna as calorias perdidas no exerc�cio.
     */
    public static Float obtemCalorias(String calorias){
    	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como par�metro. Utiliza o m�todo
    	 * substring para retirar o in�cio da linha (Calorias perdidas: ) e o final (Kcal)..
    	 */
    	StringBuilder strCalorias = new StringBuilder(calorias.substring(calorias.indexOf(":")+2, calorias.indexOf("Kcal") -1));
    	Float cal = Float.parseFloat(strCalorias.toString());

    	return cal;
    } // obtemCalorias()

    /**
     * Recebe a linha que cont�m a quantidade de passos do cliente e retorna os dados prontos para o usu�rio.
     * @param passos <code>String</code> que cont�m os dados.
     * @return Retorna o n�mero de passos dados pelo cliente.
     */
    public static Integer obtemPassos(String passos){
    	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como par�metro. Utiliza o m�todo
    	 * substring para retirar o in�cio da linha (Passos: ).
    	 */
    	StringBuilder strPassos = new StringBuilder(passos.substring(passos.indexOf(":") + 2, passos.length()));

    	if (passos.contains("."))
	    	// Remove o "." se necess�rio
	    	strPassos.replace(strPassos.indexOf("."), strPassos.indexOf(".")+1,	"");
    	Integer p = Integer.parseInt(strPassos.toString());

    	return p;
    } // obtemPassos()

    /**
     * Recebe a linha que cont�m a velocidade (m�dia ou m�xima) e retorna os dados prontos para o usu�rio.
     * @param velocidade <code>String</code> que cont�m os dados.
     * @return Retorna a velocidade.
     */

    public static float obtemVelocidade(String velocidade){
    	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como par�metro. Utiliza o m�todo
    	 * substring para retirar o in�cio da linha (Velocidade M�dia: || Velocidade M�xima:  ) e o final (Km/h).
    	 */
    	StringBuilder strVelocidade = new StringBuilder(velocidade.substring(velocidade.indexOf(":")+2, velocidade.indexOf("K") -1));
    	Float vel;

    	// Substitui a "," pelo "." para evitar exce��o na cria��o do objeto Float.
    	strVelocidade.replace(strVelocidade.indexOf(","), strVelocidade.indexOf(",")+1,	".");

    	vel = Float.parseFloat(strVelocidade.toString());

    	return vel;
    } // obtemVelocidade()

    /**
     * Recebe a linha que cont�m a eleva��o (menor ou maior) e retorna os dados prontos para o usu�rio.
     * @param elevacao <code>String</code> que cont�m os dados.
     * @return Retorna a eleva��o.
     */

    public static double obtemElevacao(String elevacao){
    	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como par�metro. Utiliza o m�todo
    	 * substring para retirar o in�cio da linha (Menor eleva��o: || Menor eleva��o: ) e o final (m).
    	 */
    	StringBuilder strElevacao = new StringBuilder(elevacao.substring(elevacao.indexOf(":")+2, elevacao.indexOf("m") -1));
    	// Remove o "."
		strElevacao.replace(strElevacao.indexOf("."), strElevacao.indexOf(".")+1,	"");
    	Double elevation = Double.parseDouble(strElevacao.toString());

    	return elevation;
    } // obtemElevacao()

    /**
     * Recebe a linha que cont�m a altura e retorna os dados prontos para o usu�rio.
     * @param linhaAltura <code>String</code> que cont�m os dados.
     * @return Retorna a altura.
     */
    public static Float obtemAltura(String linhaAltura){
       	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como par�metro. Utiliza o m�todo
    	 * substring para retirar o in�cio da linha (Altura:  ) e o final (m).
    	 */
    	StringBuilder strAltura = new StringBuilder(linhaAltura.substring(linhaAltura.indexOf(":")+1, linhaAltura.indexOf("m") -1));

    	// Substitui a "," pelo "." para evitar exce��o na cria��o do objeto Float.
		strAltura.replace(strAltura.indexOf(","), strAltura.indexOf(",")+1,	".");

		Float altura = Float.parseFloat(strAltura.toString());

		return altura;
    } // obtemAltura()

    /**
     * Recebe a linha que cont�m o peso e retorna os dados prontos para o usu�rio.
     * @param linhaPeso <code>String</code> que cont�m os dados.
     * @return Retorna o peso do cliente..
     */
    public static Float obtemPeso(String linhaPeso){
       	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como par�metro. Utiliza o m�todo
    	 * substring para retirar o in�cio da linha (Peso:  ) e o final (Kg).
    	 */
    	StringBuilder strPeso = new StringBuilder(linhaPeso.substring(linhaPeso.indexOf(":")+1,linhaPeso.indexOf("Kg")-1));

    	// Substitui a "," pelo "." para evitar exce��o na cria��o do objeto Float.
    	strPeso.replace(strPeso.indexOf(","), strPeso.indexOf(",")+1,	".");

    	Float peso = Float.parseFloat(strPeso.toString());

    	return peso;
    } // obtemPeso()

    /**
     * Recebe a linha que cont�m os dados que ser�o utilizados no objeto <code>Ritmo</code> e retorna os dados prontos para o usu�rio.
     * @param linhaKM <code>String</code> que cont�m os dados.
     * @return Retorna a KM.
     */
    public static Float obtemKM(String linhaKM){
       	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como par�metro. Utiliza o m�todo
    	 * substring para retirar o final do dado (Km:  ).
    	 */
    	StringBuilder strKM = new StringBuilder(linhaKM.substring(0,linhaKM.indexOf("K")-1));
    	if (linhaKM.contains(","))
        	// Substitui a "," pelo "." para evitar exce��o na cria��o do objeto Float.
    		strKM.replace(strKM.indexOf(","), strKM.indexOf(",")+1,	".");
    	Float km = Float.parseFloat(strKM.toString());

    	return km;
    } // obtemKM()

    /**
     * Recebe a linha que cont�m os dados que ser�o utilizados no objeto <code>Ritmo</code> e retorna os dados prontos para o usu�rio.
     * @param linhaRitmo <code>String</code> que cont�m os dados.
     * @return Retorna o minuto.
     */
    public static Integer obtemMin(String linhaRitmo){
       	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como par�metro. Utiliza o m�todo
    	 * substring para quebrar a string e obter apenas o minuto.
    	 */
    	StringBuilder strMin = new StringBuilder(linhaRitmo.substring(linhaRitmo.indexOf(":")+2,linhaRitmo.indexOf("'")));
    	Integer min = Integer.parseInt(strMin.toString());

    	return min;
    } // obtemMin()

    /**
     * Recebe a linha que cont�m os dados que ser�o utilizados no objeto <code>Ritmo</code> e retorna os dados prontos para o usu�rio.
     * @param linhaRitmo <code>String</code> que cont�m os dados.
     * @return Retorna o segundo.
     */
    public static Integer obtemSeg(String linhaRitmo){
      	/* Cria um objeto StringBuilder, inicializando com os dados recebidos como par�metro. Utiliza o m�todo
    	 * substring para quebrar a string e obter apenas o segundo.
    	 */
    	StringBuilder strSeg = new StringBuilder(linhaRitmo.substring(linhaRitmo.indexOf("'")+1,linhaRitmo.length() - 1));
    	Integer min = Integer.parseInt(strSeg.toString());

    	return min;
    } // obtemMin()

    /**
     * Obt�m o <code>LocalTime</code> de acordo com o ritmo recebido como par�metro.
     * @param linhaRitmo Linha que cont�m o ritmo a ser retornado como objeto.
     * @return ritmo Objeto criado e manipulado com os minutos e segundos.
     */

    public static LocalTime obtemRitmo(String linhaRitmo){
    	LocalTime ritmo;
    	int min, seg;

    	// Quebra os dados da linha e armazena nas respectivas vari�veis.

    	// Ritmo <MM>'
    	min = Integer.parseInt(linhaRitmo.substring(linhaRitmo.indexOf(":")+2, linhaRitmo.indexOf("'")));

    	// Ritmo <SS>''
    	seg = Integer.parseInt(linhaRitmo.substring(linhaRitmo.indexOf("'")+1, linhaRitmo.indexOf("\"")));

    	// Inicializa o objeto ritmo com o tempo (0h:<min>:<seg>)
    	ritmo = LocalTime.of(0, min, seg);

    	return ritmo;
    } // obtemRitmo()

    /**
     * Gera todo o relat�rio a partir do objeto <code>AtividadeTipo1</code> recebido como par�metro.
     *
     * Utiliza o modelo:
     *
								 Exerc�cio:

								------ Usu�rio ------
								Nome:
								Sexo:
								Altura:
								Peso:
								Data de nascimento:
								E-mail:

								------ Detalhes do exerc�cio ------
								Data:
								Tempo:
								Dura��o:
								Dist�ncia:
								Calorias perdidas:
								Passos:
								Velocidade m�dia:
								Velocidade m�xima:
								Ritmo m�dio:
								Ritmo m�ximo:
								Menor eleva��o:
								Maior eleva��o:

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
     * @param atividade Atividade a ser utilizada na cria��o do relat�rio.
     * @param conexaoBD Conex�o com o banco de dados.
     * @return relatorio Retorna uma <code>String</code> que cont�m o relat�rio completo.
     */
    public static String obtemRelatorioExercicio(AtividadeFisica atividade, Connection conexaoBD){
    	String relatorio = "";
    	String ritmos = "";

    	relatorio += "Exerc�cio: " + atividade.getExercicio() + System.lineSeparator();
    	relatorio += "\n------ Usu�rio ------" + System.lineSeparator();
		relatorio += "Nome: " + atividade.getUsuario().getNome() + System.lineSeparator();
		relatorio += "Sexo: " + atividade.getUsuario().getSexo() + System.lineSeparator();
		relatorio += "Altura: " + String.format("%1.2f m" + System.lineSeparator(), atividade.getUsuario().getAltura());
		relatorio += "Peso: " + String.format("%1.1f Kg" + System.lineSeparator(),atividade.getUsuario().getPeso());

		SimpleDateFormat dataNsc = new SimpleDateFormat("dd/MM/yyyy");
		relatorio += "Data de nascimento: " + dataNsc.format(atividade.getUsuario().getDataNascimento().getTime()) + System.lineSeparator();
		relatorio += "E-mail: " + atividade.getUsuario().getEmail() + System.lineSeparator();
		relatorio += "CPF: " + atividade.getUsuario().getCpf() + System.lineSeparator();
		relatorio += "Whatsapp: " + atividade.getUsuario().getWhatsapp() + System.lineSeparator();

		relatorio += "\n------ Detalhes do exerc�cio ------" + System.lineSeparator();

		SimpleDateFormat dataEx = new SimpleDateFormat("dd/MM/yyyy");

		relatorio += "Data: " + dataEx.format(atividade.getData().getTime()) + System.lineSeparator();
		relatorio += "Tempo: " + atividade.getTempo() + System.lineSeparator();
		relatorio += "Dura��o: " + atividade.getDuracao() + System.lineSeparator();
		relatorio += "Dist�ncia: " + atividade.getDistancia() + " Km" + System.lineSeparator();
		relatorio += "Calorias perdidas: " + atividade.getCaloriasPerdidas() + " Kcal" + System.lineSeparator();
		relatorio += "Passos: " + atividade.getPassos() + System.lineSeparator();

		if (atividade instanceof AtividadeCompleta){
			AtividadeCompleta tipo1 = (AtividadeCompleta) atividade;

			relatorio += "Velocidade m�dia: " + tipo1.getVelocidadeMedia() + "Km/h" + System.lineSeparator();
			relatorio += "Velocidade m�xima: " + tipo1.getVelocidadeMaxima() + "Km/h" + System.lineSeparator();
			relatorio += "Ritmo m�dio: " + tipo1.getRitmoMedio() + System.lineSeparator();
			relatorio += "Ritmo m�ximo: " + tipo1.getRitmoMaximo() + System.lineSeparator();
			relatorio += "Menor eleva��o: " + tipo1.getMenorElevacao() + " m" + System.lineSeparator();
			relatorio += "Maior eleva��o: " + tipo1.getMaiorElevacao() + " m" + System.lineSeparator();
			relatorio += "\n------ Ritmo ------" + System.lineSeparator();

			List<Ritmo> ritmosList = InsercaoAtividadeCompleta.listaRitmos(InsercaoAtividadeCompleta.idAtividade(tipo1, conexaoBD), conexaoBD);
			Ritmo ritmo;

			for (int i = 0 ; i < ritmosList.size() ; i++){

				ritmo = ritmosList.get(i);

				ritmos += String.format("%1.2f Km: %02d'%02d''" + System.lineSeparator(), ritmo.getKM(),ritmo.getMinutos(),ritmo.getSegundos());
			}

			relatorio += ritmos;

		}

		// Gera o arquivo de relat�rio para ser utilzado posteriormente na gera��o do PDF.
		new ManipulaDados().geraRelatorio(atividade.getUsuario().getNome(),RELATORIO_EXERCICIO, relatorio);

    	return relatorio;

    } // obtemRelatorioExercicio()


    /**
     * Gera todo o relat�rio a partir do objeto <code>AtividadeTipo2</code> recebido como par�metro.
     *
     * Utiliza o modelo:
     *
								 Exerc�cio:

								------ Usu�rio ------
								Nome:
								Sexo:
								Altura:
								Peso:
								Data de nascimento:
								E-mail:

								------ Detalhes do exerc�cio ------
								Data:
								Tempo:
								Dura��o:
								Dist�ncia:
								Calorias perdidas:
								Passos:
								Velocidade m�dia:
								Velocidade m�xima:
								Ritmo m�dio:
								Ritmo m�ximo:
								Menor eleva��o:
								Maior eleva��o:

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
     * @param atividade Atividade a ser utilizada na cria��o do relat�rio.
     * @param conexaoBD Conex�o com o banco de dados.
     * @return relatorio Retorna uma <code>String</code> que cont�m o relat�rio completo.
     */
    public static String obtemRelatorioExercicio(AtividadeBasica atividade, Connection conexaoBD){
    	String relatorio = "";

    	relatorio += "Exerc�cio: " + atividade.getExercicio() + System.lineSeparator();
    	relatorio += "\n------ Usu�rio ------" + System.lineSeparator();
		relatorio += "Nome: " + atividade.getUsuario().getNome() + System.lineSeparator();
		relatorio += "Sexo: " + atividade.getUsuario().getSexo() + System.lineSeparator();
		relatorio += "Altura: " + String.format("%1.2f m" + System.lineSeparator(), atividade.getUsuario().getAltura());
		relatorio += "Peso: " + String.format("%1.1f Kg" + System.lineSeparator(),atividade.getUsuario().getPeso());

		SimpleDateFormat dataNsc = new SimpleDateFormat("dd/MM/yyyy");
		relatorio += "Data de nascimento: " + dataNsc.format(atividade.getUsuario().getDataNascimento().getTime()) + System.lineSeparator();
		relatorio += "E-mail: " + atividade.getUsuario().getEmail() + System.lineSeparator();

		relatorio += "\n------ Detalhes do exerc�cio ------" + System.lineSeparator();

		SimpleDateFormat dataEx = new SimpleDateFormat("dd/MM/yyyy");

		relatorio += "Data: " + dataEx.format(atividade.getData().getTime()) + System.lineSeparator();
		relatorio += "Tempo: " + atividade.getTempo() + System.lineSeparator();
		relatorio += "Dura��o: " + atividade.getDuracao() + System.lineSeparator();
		relatorio += "Dist�ncia: " + atividade.getDistancia() + " Km" + System.lineSeparator();
		relatorio += "Calorias perdidas: " + atividade.getCaloriasPerdidas() + " Kcal" + System.lineSeparator();
		relatorio += "Passos: " + atividade.getPassos() + System.lineSeparator();

		// Gera o arquivo de relat�rio para ser utilzado posteriormente na gera��o do PDF.
		new ManipulaDados().geraRelatorio(atividade.getUsuario().getNome(), RELATORIO_EXERCICIO, relatorio);

    	return relatorio;
    } // obtemRelatorioExercicio()

    /**
     * Cria e retorna um relat�rio detalhado de todos os clientes cadastrados no banco de dados.
     * Este relat�rio cont�m:
     *
     * - a maior dura��o de um exerc�cio;
     * - a maior dist�ncia percorrida;
     * - o maior n�mero de calorias perdidas;
     * - o maior n�mero de passos dados;
     * - a velocidade mais r�pida (m�xima).
     *
     * @param conexaoBD Conex�o com o banco de dados.
     * @return relatorioDetalhado Retorna uma <code>String</code> com os dados de todos os clientes.
     */
    public static String obtemRelatorioDetalhado(Connection conexaoBD){
    	String relatorioDetalhado = "";
    	List<AtividadeCompleta> atividade1List = InsercaoAtividadeCompleta.listaAtividades(conexaoBD), atividade1Cliente = new ArrayList<>();
    	List<AtividadeBasica> atividade2List = InsercaoAtividadeBasica.listaAtividades(conexaoBD), atividade2Cliente = new ArrayList<>();
    	List<String> listClientes = new ArrayList<String>();

    	String nome;
    	if (atividade1List.size() > 0 || atividade2List.size() > 0){
	    	// Obt�m o e-mail do primeiro cliente da lista.
	    	nome = atividade1List.get(0).getUsuario().getNome();

	    	// Insere no arraylist.
	    	listClientes.add(nome);

	    	// Percorrendo a lista de atividadesTipo1 e adicionando o email dos clientes no array emailClientes.
	    	for (int i = 1 ; i < atividade1List.size() ; i++){
	    		// Altera o e-mail
	    		nome = new String(atividade1List.get(i).getUsuario().getNome());

	    		// Caso o nome n�o exista na lista emailClientes, insere o objeto na lista.
	    		if (Arrays.binarySearch(listClientes.toArray(), nome) < 0)
	    			listClientes.add(nome);
	    	}



	    	// Percorrendo a lista de atividadesTipo2 e adicionando o email dos clientes no array emailClientes.
	    	for (int i = 0 ; i < atividade2List.size() ; i++){
	    		// Altera o e-mail
	    		nome = new String(atividade2List.get(i).getUsuario().getNome());
	    		// Caso o nome n�o exista na lista emailClientes, insere o objeto na lista.
	    		if (Arrays.binarySearch(listClientes.toArray(), nome) < 0)
	    			listClientes.add(nome);
	    		}

			List<AtividadeCompleta> temp1 = null;
			List<AtividadeBasica> temp2 = null;

	    	for (int i = 0 ; i < listClientes.size() ; i++){
	    		// Obt�m as listas de atividades por cliente.
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

	    		// Insere todos os dados no relat�rio.
	    			relatorioDetalhado += "**********************" + System.lineSeparator();
		    		relatorioDetalhado += "Cliente: " + atividade1Cliente.get(i).getUsuario().getNome() + System.lineSeparator();
		    		relatorioDetalhado += "\nMaior dura��o de um exerc�cio: " + obtemMaiorDuracao(atividade1Cliente, atividade2Cliente) + System.lineSeparator();
		    		relatorioDetalhado += "Maior dist�ncia percorrida: " + obtemMaiorDistancia(atividade1Cliente, atividade2Cliente) + System.lineSeparator();
		    		relatorioDetalhado += "Maior n�mero de calorias perdidas: " + obtemMaiorPerdaDeCalorias(atividade1Cliente, atividade2Cliente) + System.lineSeparator();
		    		relatorioDetalhado += "Maior n�mero de passos dados: " + obtemMaiorNumeroDePassos(atividade1Cliente, atividade2Cliente) + System.lineSeparator();
		    		relatorioDetalhado += "Maior velocidade: " + obtemMaiorVelocidade(atividade1Cliente) + System.lineSeparator();
	    		}

			// Gera o arquivo de relat�rio para ser utilzado posteriormente na gera��o do PDF.
	    	new ManipulaDados().geraRelatorio("", RELATORIO_DETALHADO, relatorioDetalhado);
    	}

    	return relatorioDetalhado;
    } // obtemRelatorioDetalhado()

    /**
     * Cria um arquivo de relat�rio para uso posterior na gera��o do arquivo PDF.
     *
     * � poss�vel a gera��o de dois tipo de relat�rio: relat�rio detalhado e relat�rio individual de um exerc�cio.
     *
     * @param nome <code>String</code> com o nome do cliente.
     * @param tipoRelatorio <code>String</code> utilizada para verifica��o do tipo do relat�rio a ser gerado.
     * @param relatorio <code>String</code> com o conte�do a ser gravado no arquivo.
     * @return <i>true<i> se criado com sucesso ou <i>false</i> caso falhe.
     */
    private boolean geraRelatorio(String nome, String tipoRelatorio, String relatorio){
    	ArquivoTexto arquivoRelatorio = new ArquivoTexto();
    	String nomeArquivo = "";

    	try {
    		// Verifica��o de tipo do relat�rio.
    		if (tipoRelatorio == RELATORIO_DETALHADO){
    			nomeArquivo = "arquivos" + File.separator + "relatorioDetalhado.txt";
    			arquivoRelatorio.criar(nomeArquivo);
    			arquivoRelatorio.abrir(nomeArquivo);
    		}
    		else{
    			if (tipoRelatorio == RELATORIO_EXERCICIO){
    				// Formatter utilizado para obter a hora do sistema, que ser� armazenado no nome do arquivo.
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
			alerta.setContentText("Erro na cria��o do arquivo de relat�rio !");
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	return false;
    } // geraRelatorio()


    /**
     * M�todo utilizado para comparar as dura��es de todos os exerc�cios do cliente e retornar a maior dura��o entre todas
     * atividades realizadas.
     *
     * @param atividade1Cliente Lista com as <code>AtividadeTipo1</code>
     * @param atividade2Cliente Lista com as <code>AtividadeTipo2</code>
     *
     * @return maiorDuracao <code>String</code> com o valor da maior dura��o encontrada entre as atividades.
     */
	private static String obtemMaiorDuracao(List<AtividadeCompleta> atividade1Cliente, List<AtividadeBasica> atividade2Cliente){
    	String maiorDuracao;
    	String duracao;
    	String[] quebraDuracao;
    	List<String[]> listDuracoes = new ArrayList<String[]>();

    	// Percorre a lista atividade1Cliente e adiciona a dura��o na lista de dura��es.
    	for (int i = 0 ; i < atividade1Cliente.size() ; i++){
    		duracao = atividade1Cliente.get(i).getDuracao();
    		quebraDuracao = new String[duracao.split(":").length];
    		quebraDuracao = duracao.split(":");

    		listDuracoes.add(quebraDuracao);
    	}

    	// Percorre a lista atividade2Cliente e adiciona a dura��o na lista de dura��es.
    	for (int i = 0 ; i < atividade2Cliente.size() ; i++){
    		duracao = atividade2Cliente.get(i).getDuracao();
    		quebraDuracao = new String[duracao.split(":").length];
    		quebraDuracao = duracao.split(":");

    		listDuracoes.add(quebraDuracao);
    	}

    	Integer maiorH, maiorM, maiorS, h, m, s;

    	// Quebra a dura��o em Hora, Minuto e Segundo
    	maiorH = Integer.parseInt(listDuracoes.get(0)[0]);
    	maiorM = Integer.parseInt(listDuracoes.get(0)[1]);
    	maiorS = Integer.parseInt(listDuracoes.get(0)[2]);

    	// Loop comparando as dura��es
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

    	// Inicializa a string com a maior dura��o obtida no formato hh:mm:ss
    	maiorDuracao = String.format("%02d:%02d:%02d", maiorH,maiorM, maiorS);

    	return maiorDuracao;
    } // obtemMaiorDuracao()

	/**
	 * M�todo utilizado para comparar as dist�ncias percorridas em todos os exerc�cios do cliente e retornar a maior entre todas
     * atividades realizadas.
     *
     * @param atividade1Cliente Lista com as <code>AtividadeTipo1</code>
     * @param atividade2Cliente Lista com as <code>AtividadeTipo2</code>
	 * @return maiorDistancia <code>String</code> com a maior dist�ncia obtida nas compara��es.
	 */
    private static String obtemMaiorDistancia(List<AtividadeCompleta> atividade1Cliente, List<AtividadeBasica> atividade2Cliente) {
		List<Float> distanciasList = new ArrayList<>();
		String maiorDistancia;

		// Loops inserindo todas as dist�ncias na lista.
		for (int i = 0 ; i < atividade1Cliente.size() ; i++)
			distanciasList.add(atividade1Cliente.get(i).getDistancia());

		for (int i = 0 ; i < atividade2Cliente.size() ; i++)
			distanciasList.add(atividade2Cliente.get(i).getDistancia());

		// Inicializa a maior dist�ncia com o primeiro objeto da lista.
		Float mDistancia = distanciasList.get(0);

		// Loop comparando todos os objetos e alterando (se necess�rio) o valor de mDistancia.
		for (int i = 1 ; i < distanciasList.size() ; i++){
			if (distanciasList.get(i) > mDistancia)
				mDistancia = distanciasList.get(i);
		}

		// Inicializa a string com o maior valor obtido no formato <0.00 Km>
		maiorDistancia = String.format("%1.2f Km", mDistancia);

		return maiorDistancia;
	} // obtemMaiorDistancia()

	/**
	 * M�todo utilizado para comparar as calorias perdidas em todos os exerc�cios do cliente e retornar a maior entre todas
     * atividades realizadas.
     *
     * @param atividade1Cliente Lista com as <code>AtividadeTipo1</code>
     * @param atividade2Cliente Lista com as <code>AtividadeTipo2</code>
	 * @return maiorPerda <code>String</code> com a maior perda de calorias obtida nas compara��es.
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

		// Loop comparando todos os objetos e alterando (se necess�rio) o valor de maiorCaloria.
		for (int i = 1 ; i < caloriasList.size() ; i++){
			if (caloriasList.get(i) > maiorCaloria)
				maiorCaloria = caloriasList.get(i);
		}

		// Inicializa a string com o maior valor obtido no formato <0.00 Kcal>
		maiorPerda = String.format("%1.2f Kcal", maiorCaloria);

		return maiorPerda;
	} // obtemMaiorPerdaDeCalorias()


	/**
	 * M�todo utilizado para comparar o n�mero de passos dados pelo cliente em todos os exerc�cios e retornar a maior entre todas
     * atividades realizadas.
     *
     * @param atividade1Cliente Lista com as <code>AtividadeTipo1</code>
     * @param atividade2Cliente Lista com as <code>AtividadeTipo2</code>
	 * @return maiorNumPassos <code>String</code> com o maior n�mero de passos dados.
	 */
	private static String obtemMaiorNumeroDePassos(List<AtividadeCompleta> atividade1Cliente, List<AtividadeBasica> atividade2Cliente) {
		List<Integer> passosList = new ArrayList<>();
		String maiorNumPassos;

		// Loops inserindo todas as contagens de passos na lista.
		for (int i = 0 ; i < atividade1Cliente.size() ; i++)
			passosList.add(atividade1Cliente.get(i).getPassos());

		for (int i = 0 ; i < atividade2Cliente.size() ; i++)
			passosList.add(atividade2Cliente.get(i).getPassos());


		// Inicializa o maior n�mero de passos com o primeiro objeto da lista.
		Integer maiorPasso = passosList.get(0);

		// Loop comparando todos os objetos e alterando (se necess�rio) o valor de maiorPasso.
		for (int i = 1 ; i < passosList.size() ; i++){
			if (passosList.get(i) > maiorPasso)
				maiorPasso = passosList.get(i);
		}

		// Inicializa a string com o maior n�mero de passos  obtido no formato nas compara��es.
		maiorNumPassos = String.format("%d", maiorPasso);

		return maiorNumPassos;
	} // obtemMaiorNumeroDePassos()

	/**
	 * M�todo utilizado para comparar as velocidades dos exerc�cios e retornar a maior entre todas
     * atividades realizadas.
     *
     * @param atividade1Cliente Lista com as <code>AtividadeTipo1</code>
	 * @return maiorVelocidade <code>String</code> com a maior velocidade obtida com as compara��es.
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
	 * M�todo utilizado para cria��o dos gr�ficos. O m�todo trabalha de acordo com o valor de <code>tipoGrafico</code>.
	 *<br>
	 * Valores poss�veis:
	 * <br>- 1: Gr�fico de colunas (Dura��o)
	 * <br>- 2: Gr�fico de colunas (Dist�ncia)
	 * <br>- 3: Gr�fico de colunas (Calorias)
	 * <br>- 4: Gr�fico de colunas (Passos)
	 * <br>- 5: Gr�fico de colunas (Velocidade M�dia)
	 * <br>- 6: Gr�fico de colunas (Ritmo M�dio)
	 * <br>- 7: Gr�fico de linhas (Dist�ncia)
	 * <br>- 8: Gr�fico de linhas (Calorias)
	 * <br>- 9: Gr�fico de linhas (Passos)
	 * <br>- 10: Gr�fico de colunas completo (Dura��o)
	 * <br>- 11: Gr�fico de colunas completo (Dist�ncia)
	 * <br>- 12: Gr�fico de colunas completo (Calorias)
	 * <br>- 13: Gr�fico de colunas completo (Passos)
	 * <br>- 14: Gr�fico de colunas completo (Velocidade M�dia)
	 * <br>- 15: Gr�fico de colunas completo (Ritmo M�dio)
	 *
	 * @param tipoGrafico Valor inteiro que determina o tipo de gr�fico a ser gerado.
	 * @param nomeExercicio <code>String</code> com o nome do exerc�cio a ser utilizado nos gr�ficos.
	 * @param atividadesList <code>List</code> com a rela��o das atividades do cliente, j� separadas pelo per�odo.
	 * @param linhas Array de objetos <code>Label</code>, que ser�o utilizados nos gr�ficos 10 � 15. Nos outros gr�ficos, deve receber <i>null</i>.
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

			 Verifica se o nome do exerc�cio � o mesmo recebido como par�metro ou se a vari�vel verificador � true.
			 * A vari�vel verificador � utilizada para cria��o dos gr�ficos 10-15.

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
				// Procedimentos utilizados para gera��o do gr�fico completo.
				if (tipoGrafico > 9){
					totalPassos += atividade.getPassos();
					distanciaTotal += atividade.getDistancia();
					totalCalorias += atividade.getCaloriasPerdidas();

					if ( i == atividadesList.size() - 1){
						distanciaMedia = distanciaTotal / atividadesList.size();
						mediaCalorias = totalCalorias / atividadesList.size();

						 Ap�s a realiza��o de todos os c�lculos, o valor do for � reiniciado e a vari�vel verificador � setada como true,
						 * autorizando a cria��o dos gr�ficos.

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

					// Altera os labels do gr�fico.
					linhas[0].setText(String.format("%1.2f Kcal", totalCalorias));
					linhas[1].setText(String.format("%1.2f Kcal", mediaCalorias));
					linhas[2].setText(String.format("%1.2f Km", distanciaTotal));
					linhas[3].setText(String.format("%1.2f", distanciaMedia));
					linhas[4].setText(String.format("%d", totalPassos));
				}
			}
		} // for ()

		// Define o nome do arquivo de sa�da para adi��o posterior ao PDF.
		switch (tipoGrafico) {
			case 1:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Dura��o).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 2:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Dist�ncia).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 3:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Calorias).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 4:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Passos).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 5:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Velocidade M�dia).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 6:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Ritmo M�dio).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
				break;
			case 7:
				nomeArquivo = String.format("arquivos\\%s - %s - %s (Linhas - Dist�ncia).jpeg", atividade.getUsuario().getNome(), atividade.getExercicio(), data.replaceAll("/", "-"));
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
			// Exporta uma imagem .jpeg do gr�fico.
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
			// Procedimentos utilizados para gera��o do gr�fico completo.
			if (tipoGrafico > 9){
				totalPassos += atividade.getPassos();
				distanciaTotal += atividade.getDistancia();
				totalCalorias += atividade.getCaloriasPerdidas();

				if ( i == atividadesList.size() - 1){
					distanciaMedia = distanciaTotal / atividadesList.size();
					mediaCalorias = totalCalorias / atividadesList.size();

					 /*Ap�s a realiza��o de todos os c�lculos, o valor do for � reiniciado e a vari�vel verificador � setada como true,
					 * autorizando a cria��o dos gr�ficos.*/

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

				// Altera os labels do gr�fico.
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
	 * @param atividadesList2 Lista de objetos com as atividades b�sicas.
	 * @param dataInicial Data de in�cio do intervalo.
	 * @param dataFinal Data de fim do intervalo.
	 * @return Retorna uma lista com os objetos <code>String</code> obtidos na busca de todas as atividades, sem repeti��o.
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

			// Se a atividade estiver dentro da data recebida, cria um novo objeto string com o nome do exerc�cio.
			if (!atividade.getData().before(dataInicialCalendar) || !atividade.getData().after(dataFinalCalendar)){
				exercicio = new String(atividade.getExercicio());
				// Realiza uma busca na lista. Se o exerc�cio n�o for encontrado, � inserido.
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
	 * Percorre as listas de Atividade Completa e Atividade B�sica.
	 * @param atividadesList1 Lista com Atividade Completa.
	 * @param atividadesList2 Lista com Ativida de B�sica..
	 * @return Retorna um �nico array com todas atividades.
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
	 * M�todo que converte a dura��o do exerc�cio para minutos.
	 * @param duracao <code>String</code> que cont�m a dura��o no formato hh:mm:ss
	 * @return totalMinutos inteiro com o valor total de minutos do exerc�cio.
	 */
	private int getMinutosDuracao(String duracao){
		int totalMinutos, hora, min;

		hora = Integer.parseInt(duracao.substring(0, duracao.indexOf(":")));
		min = Integer.parseInt(duracao.substring(duracao.indexOf(":")+1, duracao.lastIndexOf(":")-1));

		totalMinutos = (hora * 60) + min;

		return totalMinutos;
	} // getMinutosDuracao()

	/**
	 * M�todo utilizado para converter o valor de ritmo para utiliza��o no gr�fico.
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
	 * Realiza toda a opera��o de exporta��o dos dados para o PDF. Obt�m os arquivos armazenados na pasta "arquivos" e insere no arquivo PDF.
	 * O nome do arquivo � gerado com a hora obtida do sistema.
	 *
	 * @return Retorna <i>true</i> se o arquivo for gerado com sucesso, <i>false</i> caso contr�rio.
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

				// Verifica a extens�o do arquivo. Caso seja txt, � um relat�rio. Insere os dados do relat�rio no PDF e exclui os arquivos.
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
				// Caso seja jpeg, � um gr�fico. Insere as imagens no PDF e exclui os arquivos.
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
    		alerta.setContentText("Falha na cria��o do arquivo PDF !" );
		} catch (DocumentException e) {
			alerta = new Alert(AlertType.ERROR);
			alerta.setTitle("ERRO");
			alerta.setContentText("Erro na manipula��o do PDF !");
		} catch (IOException e) {
			alerta = new Alert(AlertType.ERROR);
			alerta.setTitle("ERRO");
			alerta.setContentText("Erro na leitura dos arquivos !" );
		}
    	return false;
	} // exportarPDF()

} // class ManipulaDados
