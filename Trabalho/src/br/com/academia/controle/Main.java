package br.com.academia.controle;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;

/**
 * Classe principal do programa. Inicializa a parte visual.
 * @author Paulo
 *
 */
public class Main extends Application {
	private static Stage janelaPrograma;
	private static Scene login, programaPrincipal;


	@Override
	public void start(Stage primaryStage) {
		try {
			janelaPrograma = primaryStage;
			Parent root = FXMLLoader.load(getClass().getResource("AtividadeFisica.fxml"));
			programaPrincipal = new Scene(root,1270,700);
			programaPrincipal.getStylesheets().add(getClass().getResource("application.css").toExternalForm());

			root = FXMLLoader.load(getClass().getResource("Login.fxml"));
			login = new Scene(root,300,200);

			janelaPrograma.setScene(login);
			janelaPrograma.setResizable(false);
			janelaPrograma.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public static void trocaTela(String tela){
		switch (tela) {
		case "principal":
			janelaPrograma.setTitle("Avaliação Física - LPV 2018");
			janelaPrograma.setScene(programaPrincipal);
			janelaPrograma.centerOnScreen();
			break;
		case "login":
			janelaPrograma.setTitle("Login - Avaliação Física - LPV 2018");
			janelaPrograma.setScene(login);
			janelaPrograma.centerOnScreen();
			break;
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
