package too.trabalho.paulo.classes;

/**
 * Classe utilizada para manipulação dos dados de ritmo das atividades.
 * @author Paulo
 *
 */
public final class Ritmo {
	private float KM;
	private int minutos, segundos;

	public Ritmo(float kM, int minutos, int segundos) {
		KM = kM;
		this.minutos = minutos;
		this.segundos = segundos;
	}

	public Ritmo() {
		KM = 0;
		minutos = 0;
		segundos = 0;
	}

	public float getKM() {
		return KM;
	}

	public void setKM(float kM) {
		KM = kM;
	}

	public int getMinutos() {
		return minutos;
	}

	public void setMinutos(int minutos) {
		this.minutos = minutos;
	}

	public int getSegundos() {
		return segundos;
	}

	public void setSegundos(int segundos) {
		this.segundos = segundos;
	}

} // class Ritmo
