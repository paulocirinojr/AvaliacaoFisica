package too.trabalho.paulo.classes;

import java.util.Calendar;

/**
 * Classe que obtém os dados do cliente.
 * @author Paulo
 *
 */
public class Aluno {
	private String nome, sexo, email, cpf, whatsapp;
	private float altura, peso;
	private Calendar dataNascimento;

	public Aluno() {
		nome = sexo = email = "";
		dataNascimento = Calendar.getInstance();
	}

	public Aluno(String nome, String sexo, String email, float altura, float peso, Calendar dataNascimento) {
		this.nome = nome;
		this.sexo = sexo;
		this.email = email;
		this.altura = altura;
		this.peso = peso;
		this.dataNascimento = dataNascimento;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public float getAltura() {
		return altura;
	}

	public void setAltura(float altura) {
		this.altura = altura;
	}

	public float getPeso() {
		return peso;
	}

	public void setPeso(float peso) {
		this.peso = peso;
	}

	public Calendar getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Calendar dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	@Override
	public String toString() {
		return String.format("------ Usuário ------\nNome: %s\nSexo: %sAltura: %1.2f m\nPeso: %1.1f Kg\nData de Nascimento: %s\nE-mail: %s"
				+ "\nCPF: %s\nWhatsapp: %s",
				nome, sexo, altura, peso, dataNascimento.toString(), email, cpf, whatsapp);
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getWhatsapp() {
		return whatsapp;
	}

	public void setWhatsapp(String whatsapp) {
		this.whatsapp = whatsapp;
	}



} // class Usuario
