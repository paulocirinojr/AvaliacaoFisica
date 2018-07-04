package br.com.academia.controle;

import java.sql.Connection;

import br.com.academia.modelo.Usuario;
import br.com.academia.modelo.dao.CriaConexaoBD;
import br.com.academia.modelo.dao.InsercaoUsuario;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import static br.com.academia.utils.ManipulaDados.exibeAlerta;

public class LoginController {

    @FXML
    private PasswordField pfSenha;
    @FXML
    private TextField tfUser;

    InsercaoUsuario insercao = new InsercaoUsuario();
    Connection conexao = new CriaConexaoBD().conectaBD();

    @FXML
    private void efetuaLogin() {
    	Usuario user = new Usuario(tfUser.getText(), pfSenha.getText(), "");

    	if (insercao.verificaUsuario(user, conexao))
    		Main.trocaTela("principal");
    	else
    		exibeAlerta("ERRO", "Usuário ou senha incorretos!", AlertType.ERROR).showAndWait();
    }

} // class LoginController