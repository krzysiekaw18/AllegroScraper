package pl.krzysztofstuglik.allegroapp.controllers;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;


import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {

    @FXML
    TextField fieldUrl;

    @FXML
    Button buttonShowPrice;

    @FXML
    Label labelPrice;

    @FXML
    Label labelState;

    @FXML
    ProgressIndicator progressBar;

    private double lastPriceValue;
    private ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    public void initialize(URL location, ResourceBundle resources) {
        progressBar.setVisible(false);
        buttonShowPrice.setOnMouseClicked(s -> {
            loadData(fieldUrl.getText());
        });
    }

    private void setData(double price, String status) {
        Platform.runLater(() -> {
            progressBar.setVisible(false);
            labelPrice.setVisible(true);
            labelState.setVisible(true);

            labelPrice.setText(String.valueOf(price));
            labelState.setText(status);
        });
    }

    private void loadData(String url) {
        progressBar.setVisible(true);
        labelPrice.setVisible(true);
        labelState.setVisible(true);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            try {
                double price = getPrice(url);
                String status = getState(url);

                if (lastPriceValue != price) {
                    JOptionPane.showMessageDialog(null, "Cena uległa zmianie");
                    lastPriceValue = price;
                } else {
                    System.out.println("Cene nie uległa zmianie");
                }
                setData(price, status);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private double getPrice(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element price = doc.selectFirst(".m-price--primary");
        String text = price.text();
        return Double.valueOf(text.replaceAll("[^0-9,]", "").replace(",", "."));
    }

    private String getState(String url) throws IOException {
        Document doc = Jsoup.connect(url).get();
        Element state = doc.selectFirst("._09810109");
        String text = state.text();
        return String.valueOf(text);
    }
}
