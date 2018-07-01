package too.trabalho.paulo.classes;

import java.util.Calendar;

/**
 * Sub-classe da classe <code>AtividadeFisica</code>, utilizada em exerc�cios mais simples.
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
	 * @param data Objeto <code>Calendar</code> com a data do exerc�cio.
	 * @param tempo Tempo de realiza��o do exerc�cio.
	 * @param duracao Dura��o do exerc�cio.
	 * @param caloriasPerdidas Total de calorias perdidas no exerc�cio.
	 * @param passos Total de passos.
	 * @param distancia Dist�ncia percorrida no exerc�cio.
	 */
	public AtividadeBasica(Aluno usuario, Calendar data, String tempo, String duracao, float caloriasPerdidas,
			int passos, float distancia) {
		super(usuario, data, tempo, duracao, caloriasPerdidas, passos, distancia);
	}

	@Override
	public String toString() {
		return String.format("Exerc�cio: %s", super.toString());
	}

} // class AtividadeBasica
