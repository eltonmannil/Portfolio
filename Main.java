package Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**This class creates an app that displays messages.Main function that starts the application
 *  @author Elton Mannil
 */



public class Main extends Application {
    /** This is the main method, loads the login screen when the program is run.
     * @param stage
     * @throws IOException
     */
    @Override
    public void start(Stage stage) throws IOException {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Speech/Known_Language", Locale.getDefault());
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/Views/Login.fxml"));
        stage.setTitle(resourceBundle.getString("Title"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.show();
    }

    /** This method connects to the database.
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception{
        JDBC.openConnection();
        launch(args);
        JDBC.closeConnection();
    }
}