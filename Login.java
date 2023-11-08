package Controller;

import Main.PullDatabase;
import Model.Appointment;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class Login {
    @FXML
    private Label loginHeader;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label passwordLabel;
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private Label timeZoneLabel;
    @FXML
    private Label timeZoneDisplayLabel;
    @FXML
    private Button loginActionButton;
    @FXML
    private Button exitActionButton;

    /**
     * Manages the click action for the login button:
     *
     * Utilizes lambda functions to fetch usernames and passwords for each user from the database, eliminating the need for loops.
     * Displays an error message in the local machine's language if invalid credentials are entered.
     * Navigates to the main console screen upon successful login.
     * Logs both successful and unsuccessful login attempts in a text file, recording the date and time of the attempt.
     * Notifies the user if there is an upcoming appointment within the next 15 minutes, displaying the appointment ID and start time.
     * Displays a message if there are no upcoming appointments.
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void loginAction(ActionEvent actionEvent) throws SQLException, IOException {
        ResourceBundle localLanguage = ResourceBundle.getBundle("Speech/Known_Language", Locale.getDefault());
        ObservableList<User> listOfUsers = PullDatabase.getUsersFromDatabase();
        ObservableList<String> usernameList = FXCollections.observableArrayList();
        ObservableList<String> passwordList = FXCollections.observableArrayList();

            /*
            Lambda functions adding usernames and passwords to their respective lists
             */
        listOfUsers.forEach(user -> usernameList.add(user.getUsername()));
        listOfUsers.forEach(user -> passwordList.add(user.getPassword()));

        FileWriter txtLoggerFile = new FileWriter("login_activity.txt", true);
        if(usernameList.contains(usernameField.getText()) && passwordList.contains(passwordField.getText())){
            Parent parent = FXMLLoader.load(getClass().getResource("/Views/MainScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            txtLoggerFile.write("The user with username: " + usernameField.getText() + " successfully logged in at " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME) + " on " + LocalDate.now() + "\n\n");
            int appointmentId = 0;
            LocalDateTime upcomingStart = null;
            if(appointmentSoon()){
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("The appointment with ID: " + appointmentUpcoming(appointmentId) + " starts within the next 15 minutes! This appointment starts at " + upcomingStart(upcomingStart));
                alert.showAndWait();
            }
            else{
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("There are no upcoming appointments");
                alert.showAndWait();
            }
        }
        else if (!usernameList.contains(usernameField.getText()) || !passwordList.contains(passwordField.getText()) || usernameField.getText().isEmpty() || passwordField.getText().isEmpty()){
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setTitle(localLanguage.getString("Error"));
            a.setContentText(localLanguage.getString("IncorrectLogin"));
            a.showAndWait();
            txtLoggerFile.write("The user with username: " + usernameField.getText() + " failed to login at " + LocalTime.now().truncatedTo(ChronoUnit.SECONDS).format(DateTimeFormatter.ISO_LOCAL_TIME) + " on " + LocalDate.now() + "\n\n");
        }
        txtLoggerFile.close();
    }

    /**
     * Evaluates whether an appointment starts within the next 15 minutes by comparing the appointment start time with the local machine's time.
     * Returns a boolean value indicating if there is an upcoming appointment.
     * @throws SQLException
     */
    public Boolean appointmentSoon() throws SQLException {
        boolean upcoming = false;
        LocalDateTime timeNow = LocalDateTime.now();
        LocalDateTime timeAhead = LocalDateTime.now().plusMinutes(15);
        ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
        for(Appointment a: listOfAppointments){
            if(a.getStartTime().isEqual(timeAhead) || (a.getStartTime().isAfter(timeNow) && a.getStartTime().isBefore(timeAhead))){
                upcoming = true;
            }
        }
        return upcoming;
    }

    /**
     * Retrieves the appointment ID if an appointment is scheduled to start within the next 15 minutes.
     * @param appointmentId
     * @return appointmentId
     * @throws SQLException
     */
    public int appointmentUpcoming(int appointmentId) throws SQLException {
        ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
        for(Appointment a: listOfAppointments){
            if(appointmentSoon()){
                appointmentId = a.getAppointmentId();
            }
        }
        return appointmentId;
    }

    /**
     * Grabs the appointment's start date and time if it is scheduled to begin within the next 15 minutes.
     * @param start
     * @return
     * @throws SQLException
     */
    public LocalDateTime upcomingStart(LocalDateTime start) throws SQLException{
        ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
        for(Appointment a: listOfAppointments){
            if(appointmentSoon()){
                start = (LocalDateTime) a.getStartTime();
            }
        }
        return start;
    }

    /**
     * Handles the click action for the exit button:
     *
     * Closes the application when clicked.
     * @param actionEvent
     */
    public void exitAction(ActionEvent actionEvent){
        System.exit(0);
    }
    /**
     * Sets up the screen:
     *
     * Adjusts each text to the language of the local machine.
     * Displays the machine's local time zone.
     * @throws Exception
     */
    public void initialize() throws Exception{
        try{
            Locale locale = Locale.getDefault();
            Locale.setDefault(locale);
            ResourceBundle localLanguage = ResourceBundle.getBundle("Speech/Known_Language", Locale.getDefault());
            loginHeader.setText(localLanguage.getString("LoginLabel"));
            usernameLabel.setText(localLanguage.getString("UsernameLabel"));
            passwordLabel.setText(localLanguage.getString("PasswordLabel"));
            timeZoneLabel.setText(localLanguage.getString("TimeZoneLabel"));
            loginActionButton.setText(localLanguage.getString("LoginLabel"));
            exitActionButton.setText(localLanguage.getString("ExitButton"));
            timeZoneDisplayLabel.setText(String.valueOf(ZoneId.systemDefault()));
        }
        catch(MissingResourceException e){
            System.out.println(e);
        }
        catch(Exception e){
            System.out.println(e);
        }

    }

}

