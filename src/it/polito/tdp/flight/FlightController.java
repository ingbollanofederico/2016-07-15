package it.polito.tdp.flight;

import java.net.URL;
import java.util.ResourceBundle;

import it.polito.tdp.flight.model.Airport;
import it.polito.tdp.flight.model.Model;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FlightController {

	private Model model;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private TextField txtDistanzaInput;

	@FXML
	private TextField txtPasseggeriInput;

	@FXML
	private TextArea txtResult;

	@FXML
	void doCreaGrafo(ActionEvent event) {
		
		try {
			Integer km = Integer.parseInt(this.txtDistanzaInput.getText());
			this.model.creaGrafo(km);
			
			boolean connesso = this.model.possibileRaggiungere();
			if(connesso) {
			this.txtResult.appendText("Possibile raggiungere ogni aeroporto\n");
			}else {
				this.txtResult.appendText("NON Possibile raggiungere ogni aeroporto\n");
			}
			
			Airport lontanoFiumicino = this.model.lontanoDa();
			this.txtResult.appendText("Il più lontano da fiumicino: "+lontanoFiumicino);
			
			
		}catch(NumberFormatException e) {
			this.txtResult.clear();
			this.txtResult.appendText("Devi inserire una distanza in km!\n");
		}
		
	}

	@FXML
	void doSimula(ActionEvent event) {
		
	}

	@FXML
	void initialize() {
		assert txtDistanzaInput != null : "fx:id=\"txtDistanzaInput\" was not injected: check your FXML file 'Untitled'.";
		assert txtPasseggeriInput != null : "fx:id=\"txtPasseggeriInput\" was not injected: check your FXML file 'Untitled'.";
		assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'Untitled'.";

	}

	public void setModel(Model model) {
		this.model = model;
	}
}
