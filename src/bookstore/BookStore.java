/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bookstore;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author Anders
 */
public class BookStore extends Application {
    
    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("The Book Store");
        stage.setWidth(1220);
        stage.setMaxWidth(1220);
        stage.setHeight(700);
        stage.setMaxHeight(700);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("BookStoreGUI.fxml"));
        Parent root = loader.load();
        ((BookStoreController)loader.getController()).setStage(stage);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
