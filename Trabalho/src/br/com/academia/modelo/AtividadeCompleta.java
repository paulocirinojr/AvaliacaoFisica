package br.com.academia.modelo;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Sub-classe da classe <code>AtividadeFisica</code>, utilizada em exercícios mais detalhados.
 * @author Paulo
 */
public final class AtividadeCompleta extends AtividadeFisica {
	private float velocidadeMedia, velocidadeMaxima;
	private double menorElevacao, maiorElevacao;
	private LocalTime ritmoMedio, ritmoMaximo;
	private ArrayList<Ritmo> ritmo;

	/**
	 * Construtor default.
	 */
	public AtividadeCompleta() {
		super();
		ritmo = new ArrayList<Ritmo>();
	}

	/**
	 * Construtor sobrecarregado.
	 *
	 * @param usuario Objeto <code>Usuario</code> com os dados do cliente.
	 * @param data Objeto <code>Calendar</code> com a data do exercício.
	 * @param tempo Tempo de realização do exercócio.
	 * @param duracao Duração do exercício.
	 * @param caloriasPerdidas Total de calorias perdidas no exercício.
	 * @param passos Total de passos.
	 * @param distancia Distância percorrida no exercício.
	 */
	public AtividadeCompleta(Aluno usuario, Calendar data, String tempo, String duracao, float caloriasPerdidas,
			int passos, float distancia) {
		super(usuario, data, tempo, duracao, caloriasPerdidas, passos, distancia);
	}

	/**
	 * Construtor sobrecarregado.
	 *
	 * @param velocidadeMedia Velocidade média alcançada na atividade.
	 * @param velocidadeMaxima Velocidade máxima alcançada na atividade.
	 * @param menorElevacao Menor elevação alcançada na atividade.
	 * @param maiorElevacao Maior elevação alcançada na atividade.
	 * @param ritmo Lista com objetos <code>Ritmo</code>, contendo todos os ritmos por KM.
	 * @param ritmoMedio Ritmo médio do cliente na atividade.
	 * @param ritmoMaximo Ritmo máximo do cliente na atividade.
	 */
	public AtividadeCompleta(float velocidadeMedia, float velocidadeMaxima, double menorElevacao,
			double maiorElevacao, ArrayList<Ritmo> ritmo, LocalTime ritmoMedio, LocalTime ritmoMaximo) {
		super();
		this.velocidadeMedia = velocidadeMedia;
		this.velocidadeMaxima = velocidadeMaxima;
		this.menorElevacao = menorElevacao;
		this.maiorElevacao = maiorElevacao;
		this.ritmo = ritmo;
		this.ritmoMedio = ritmoMedio;
		this.ritmoMaximo = ritmoMaximo;

	}

	/**
	 * Obtém o valor de ritmoMedio.
	 * @return ritmoMedio
	 */
	public LocalTime getRitmoMedio() {
		return ritmoMedio;
	}

	/**
	 * Obtém o valor de ritmoMaximo.
	 * @return ritmoMaximo
	 */
	public LocalTime getRitmoMaximo() {
		return ritmoMaximo;
	}

	/**
	 * Altera o valor do objeto ritmoMedio.
	 * @param ritmoMedio Objeto <code>LocalTime</code> com o dado do ritmo.
	 */
	public void setRitmoMedio(LocalTime ritmoMedio) {
		this.ritmoMedio = ritmoMedio;
	}

	/**
	 * Altera o valor de ritmoMaximo.
	 * @param ritmoMaximo Objeto <code>LocalTime</code> com o dado do ritmo.
	 */
	public void setRitmoMaximo(LocalTime ritmoMaximo) {
		this.ritmoMaximo = ritmoMaximo;
	}

	/**
	 * Obtém o valor de velocidadeMedia.
	 * @return velocidadeMedia
	 */
	public float getVelocidadeMedia() {
		return velocidadeMedia;
	}

	/**
	 * Altera o valor de velocidadeMedia
	 * @param velocidadeMedia Objeto com o valor da velocidade média.
	 */
	public void setVelocidadeMedia(float velocidadeMedia) {
		this.velocidadeMedia = velocidadeMedia;
	}

	/**
	 * Obtém o valor de velocidadeMaxima.
	 * @return velocidadeMaxima Valor da velocidade máxima.
	 */
	public float getVelocidadeMaxima() {
		return velocidadeMaxima;
	}

	/**
	 * Altera o valor de velocidadeMaxima.
	 * @param velocidadeMaxima Objeto com o valor da velocidade máxima.
	 */
	public void setVelocidadeMaxima(float velocidadeMaxima) {
		this.velocidadeMaxima = velocidadeMaxima;
	}

	/**
	 * Obtém o valor de menorElevacao.
	 * @return menorElevacao Valor da menor elevação.
	 */
	public double getMenorElevacao() {
		return menorElevacao;
	}

	/**
	 * Altera o valor de menorElevacao.
	 * @param menorElevacao Objeto com o valor da menor elevação.
	 */
	public void setMenorElevacao(double menorElevacao) {
		this.menorElevacao = menorElevacao;
	}

	/**
	 * Obtém o valor de maiorElevacao.
	 * @return maiorElevacao Valor da maior elevação.
	 */
	public double getMaiorElevacao() {
		return maiorElevacao;
	}

	/**
	 * Altera o valor de maiorElevacao.
	 * @param maiorElevacao Objeto com o valor da maior elevação.
	 */
	public void setMaiorElevacao(double maiorElevacao) {
		this.maiorElevacao = maiorElevacao;
	}

	/**
	 * Retorna o <code>ArrayList</code> com os objetos <code>Ritmo</code> da atividade.
	 * @return Lista de ritmos da atividade.
	 */
	public ArrayList<Ritmo> getRitmo() {
		return ritmo;
	}

	/**
	 * Altera a lista de objetos <code>Ritmo</code>.
	 * @param ritmo Lista de objetos <code>Ritmo</code>.
	 */
	public void setRitmo(ArrayList<Ritmo> ritmo) {
		this.ritmo = ritmo;
	}

	@Override
	public String toString() {
		return String.format("Exercício: %s\n\n%s\nVelocidade Média: %1.1f Km/h\nVelocidade Máxima: %1.1f Km/h"
				+ "\nRitmo Médio: %s\nRitmo Máximo: %s\nMenor Elevação: %1.1f\nMaior Elevação: %1.1f\n\n------ Ritmo ------\n%s",
				super.toString(), velocidadeMedia, velocidadeMaxima, ritmoMedio, ritmoMaximo, menorElevacao, maiorElevacao, ritmo);
	}

} // class AtividadeCompleta
