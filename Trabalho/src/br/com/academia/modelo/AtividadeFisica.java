package br.com.academia.modelo;

import java.util.Calendar;

/**
 * Super-classe que obtém as informações comuns para ambos os tipos de atividade.
 * @author Paulo
 */
public class AtividadeFisica {
	private Aluno aluno;
	private Calendar data;
	private String tempo, duracao, atividade;
	private float caloriasPerdidas, distancia;
	private int passos;

	public AtividadeFisica() {
		aluno = null;
		data = Calendar.getInstance();
	}

	public AtividadeFisica(Aluno usuario, Calendar data, String tempo, String duracao, float caloriasPerdidas,
			int passos, float distancia) {
		this.aluno = usuario;
		this.data = data;
		this.tempo = tempo;
		this.duracao = duracao;
		this.caloriasPerdidas = caloriasPerdidas;
		this.passos = passos;
		this.distancia = distancia;
	}

	public Aluno getUsuario() {
		return aluno;
	}

	public void setUsuario(Aluno usuario) {
		this.aluno = usuario;
	}

	public Calendar getData() {
		return data;
	}

	public void setData(Calendar data) {
		this.data = data;
	}

	public String getTempo() {
		return tempo;
	}

	public void setTempo(String tempo) {
		this.tempo = tempo;
	}

	public String getDuracao() {
		return duracao;
	}

	public void setDuracao(String duracao) {
		this.duracao = duracao;
	}

	public float getCaloriasPerdidas() {
		return caloriasPerdidas;
	}

	public void setCaloriasPerdidas(float caloriasPerdidas) {
		this.caloriasPerdidas = caloriasPerdidas;
	}

	public int getPassos() {
		return passos;
	}

	public float getDistancia() {
		return distancia;
	}

	public void setDistancia(float distancia) {
		this.distancia = distancia;
	}

	public void setPassos(int passos) {
		this.passos = passos;
	}

	public String getExercicio() {
		return atividade;
	}

	public void setExercicio(String exercicio) {
		this.atividade = exercicio;
	}

	@Override
	public String toString() {
		return String.format("%s\n\n------ Detalhes do exercício ------\nData: %s\nTempo: %s\nDuração %s\nDistância: %1.2f Km\n"
				+ "Calorias perdidas: %1.1f\nPassos: %d", aluno.toString(), data.toString(), tempo, duracao, distancia, caloriasPerdidas, passos);
	}

} // class AtividadeFisica
