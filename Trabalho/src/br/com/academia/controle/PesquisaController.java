package br.com.academia.controle;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

public class PesquisaController {

    @FXML
    private TextArea taBusca;

	public void setRelatorio(String relatorio) {
		taBusca.setText(relatorio);
	}





}
