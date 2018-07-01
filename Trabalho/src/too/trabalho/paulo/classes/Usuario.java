package too.trabalho.paulo.classes;

public class Usuario {
	private String usuario, senha, papel;

	public Usuario() {

	}

	public Usuario(String usuario, String senha, String papel) {
		this.usuario = usuario;
		this.senha = senha;
		this.papel = papel;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getPapel() {
		return papel;
	}

	public void setPapel(String papel) {
		this.papel = papel;
	}

} // class Usuario
