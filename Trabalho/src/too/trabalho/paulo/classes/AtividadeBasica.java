package too.trabalho.paulo.classes;

import java.util.Calendar;

/**
 * Sub-classe da classe <code>AtividadeFisica</code>, utilizada em exercícios mais simples.
 * @author Paulo
 */
public final class AtividadeBasica extends AtividadeFisica {

	/**
	 * Construtor default.
	 */
	public AtividadeBasica() {
		super();
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
	public AtividadeBasica(Aluno usuario, Calendar data, String tempo, String duracao, float caloriasPerdidas,
			int passos, float distancia) {
		super(usuario, data, tempo, duracao, caloriasPerdidas, passos, distancia);
	}

	@Override
	public String toString() {
		return String.format("Exercício: %s", super.toString());
	}

} // class AtividadeBasica
