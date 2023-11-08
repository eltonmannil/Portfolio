package Controller;

import Main.ConversionTime;
import Main.JDBC;
import Main.NameIDConversion;
import Main.PullDatabase;
import Model.Appointment;
import Model.Contact;
import Model.Customer;
import Model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
public class AppointmentMod {
    @FXML
    private TextField appointmentIDField;
    @FXML
    private TextField appointmentTitleField;
    @FXML
    private TextField appointmentTypeField;
    @FXML
    private TextField appointmentDescriptionField;
    @FXML
    private TextField locationField;
    @FXML
    private DatePicker startDatePicker;
    @FXML
    private ComboBox<LocalTime> startTimeComboBox;
    @FXML
    private DatePicker endDatePicker;
    @FXML
    private ComboBox<LocalTime> endTimeComboBox;
    @FXML
    private ComboBox<Integer> customerComboBox;
    @FXML
    private ComboBox<Integer> userComboBox;
    @FXML
    private ComboBox<String> contactComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;
    private static Appointment selectedAppointment;

    /**
     * Contains all error messages for this page
     * @param alertSelected
     */
    public void displayAlert(int alertSelected){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        switch(alertSelected) {
            case 1:
                alert.setTitle("Error");
                alert.setHeaderText("Selected time is outside of business operations!");
                alert.setContentText("Business operation times are from 8am-10pm EST");
                alert.showAndWait();
                break;
            case 2:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start/end date!");
                alert.setContentText("Appointments must start and end in the same day due to business operation constraints");
                alert.showAndWait();
                break;
            case 3:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start date!");
                alert.setContentText("An appointment cannot have a start date after an end date");
                alert.showAndWait();
                break;
            case 4:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start time!");
                alert.setContentText("An appointment cannot have a start time after an end time");
                alert.showAndWait();
                break;
            case 5:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start time!");
                alert.setContentText("An appointment cannot have the same start time and end time");
                alert.showAndWait();
                break;
            case 6:
                alert.setTitle("Error");
                alert.setHeaderText("Appointment overlap!");
                alert.setContentText("This appointment overlaps with an existing appointment");
                alert.showAndWait();
                break;
            case 7:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start time!");
                alert.setContentText("An appointment cannot have a start time before the current time");
                alert.showAndWait();
                break;
            case 8:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start date!");
                alert.setContentText("An appointment cannot have a start date before the current date");
                alert.showAndWait();
                break;
            case 9:
                alert.setTitle("Error");
                alert.setHeaderText("One or more fields empty!");
                alert.setContentText("Please fill out all required fields");
                alert.showAndWait();
                break;
        }
    }

    /**
     * Verifies that all fields are filled; updating the appointment is not allowed if any field is left empty.
     * @return
     */
    public Boolean allFieldsFilled(){
        if(!appointmentTitleField.getText().isEmpty() && !appointmentTypeField.getText().isEmpty() && !appointmentDescriptionField.getText().isEmpty() && !locationField.getText().isEmpty() && startDatePicker.getValue() != null && startTimeComboBox.getValue() != null && endDatePicker.getValue() != null && endTimeComboBox.getValue() != null && customerComboBox.getValue() != null && userComboBox.getValue() != null && contactComboBox.getValue() != null){
            return true;
        }
        return false;
    }

    /**
     * Examines whether the appointment dates/times overlap with existing appointments, considering conflicting start and end times with other scheduled appointments.
     * @return
     * @throws SQLException
     */
    public Boolean appointmentOverlap() throws SQLException {
        ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
        LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), startTimeComboBox.getValue());
        LocalDateTime endDateTime = LocalDateTime.of(endDatePicker.getValue(), endTimeComboBox.getValue());
        for(Appointment a: listOfAppointments){
            if((!appointmentIDField.getText().equals(String.valueOf(a.getAppointmentId()))) && ((startDateTime.isBefore(a.getStartTime()) && endDateTime.isAfter(a.getEndTime())) || (startDateTime.isAfter(a.getStartTime()) && endDateTime.isBefore(a.getEndTime())) || ((startDateTime.isAfter(a.getStartTime()) && startDateTime.isBefore(a.getEndTime())) && endDateTime.isAfter(a.getEndTime())) || ((endDateTime.isBefore(a.getEndTime()) && endDateTime.isAfter(a.getStartTime())) && startDateTime.isBefore(a.getStartTime())) || (startDateTime.equals(a.getStartTime()) && endDateTime.equals(a.getEndTime())) || ((startDateTime.isAfter(a.getStartTime()) && startDateTime.isBefore(a.getEndTime())) && endDateTime.isEqual(a.getEndTime())) || ((endDateTime.isBefore(a.getEndTime()) && endDateTime.isAfter(a.getStartTime())) && startDateTime.isEqual(a.getStartTime())) || (startDateTime.isBefore(a.getStartTime()) && endDateTime.isEqual(a.getEndTime())) || (startDateTime.isEqual(a.getStartTime()) && endDateTime.isAfter(a.getEndTime())))){
                return true;
            }
        }
        return false;
    }

    /**
     * Handles the click action for the save button:
     * Updates the appointment in the database using the appointment ID.
     * Updates the appointment table in the main console.
     * Displays an error message if any field is not filled.
     * Loads the main console if the save operation is successful.
     * Displays a message confirming the successful appointment update.
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void saveOnClick(ActionEvent actionEvent) throws SQLException, IOException {
        if(allFieldsFilled()){
            LocalTime convertedStartTime = ConversionTime.convertToEasternTime(startDatePicker.getValue(), startTimeComboBox.getValue());
            LocalTime convertedEndTime = ConversionTime.convertToEasternTime(endDatePicker.getValue(), endTimeComboBox.getValue());
            if(convertedStartTime.getHour() < 8|| convertedStartTime.getHour() > 22 || convertedEndTime.getHour() < 8 || convertedEndTime.getHour() > 22 || ((convertedStartTime.getHour() == 22) && convertedStartTime.getMinute() > 0) || ((convertedEndTime.getHour() == 22) && convertedEndTime.getMinute() > 0)){
                displayAlert(1);
                return;
            }
            if(!startDatePicker.getValue().isEqual(endDatePicker.getValue())){
                displayAlert(2);
                return;
            }
            if(startDatePicker.getValue().isBefore(LocalDate.now())){
                displayAlert(8);
                return;
            }
            if(startDatePicker.getValue().equals(LocalDate.now()) && startTimeComboBox.getValue().isBefore(LocalTime.now())){
                displayAlert(7);
                return;
            }
            if(startDatePicker.getValue().isAfter(endDatePicker.getValue())){
                displayAlert(3);
                return;
            }
            if(startTimeComboBox.getValue().isAfter(endTimeComboBox.getValue())){
                displayAlert(4);
                return;
            }
            if(startTimeComboBox.getValue().equals(endTimeComboBox.getValue())){
                displayAlert(5);
                return;
            }
            LocalDateTime startDateTime = LocalDateTime.of(startDatePicker.getValue(), startTimeComboBox.getValue());
            LocalDateTime endDateTime = LocalDateTime.of(endDatePicker.getValue(), endTimeComboBox.getValue());
            if(appointmentOverlap()){
                displayAlert(6);
                return;
            }
            Connection conn = JDBC.getConnection();
            JDBC.setPreparedStatement(conn, "UPDATE appointments SET Appointment_ID = ?, Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Last_Update = ?, Last_Updated_By = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?");
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();
            preparedStatement.setInt(1, Integer.parseInt(appointmentIDField.getText()));
            preparedStatement.setString(2, appointmentTitleField.getText());
            preparedStatement.setString(3, appointmentDescriptionField.getText());
            preparedStatement.setString(4, locationField.getText());
            preparedStatement.setString(5, appointmentTypeField.getText());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(startDateTime));
            preparedStatement.setTimestamp(7, Timestamp.valueOf(endDateTime));
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(9, userComboBox.getValue());
            preparedStatement.setInt(10, customerComboBox.getValue());
            preparedStatement.setInt(11, userComboBox.getValue());
            preparedStatement.setInt(12, NameIDConversion.convertContactNameToID(contactComboBox.getValue()));
            preparedStatement.setInt(13, Integer.parseInt(appointmentIDField.getText()));
            preparedStatement.execute();

            Parent parent = FXMLLoader.load(getClass().getResource("../views/MainScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("The appointment with ID: " + appointmentIDField.getText() + " was successfully updated");
            alert.showAndWait();
            return;

        }
        else {
            displayAlert(9);
        }
    }

    /**
     * Manages the click action for the cancel button:
     *
     * Navigates to the main console.
     * Does not update the customer information upon clicking.
     * @param actionEvent
     * @throws IOException
     */
    public void cancelOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../Views/MainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Sets up the form by populating the relevant fields with appointment information retrieved from the selected appointment.
     * @throws SQLException
     */
    public void initialize() throws SQLException{
        selectedAppointment = MainViewController.getSelectedAppointment();
        ObservableList<Customer> listOfCustomers = PullDatabase.getCustomersFromDatabase();
        ObservableList<User> listOfUsers = PullDatabase.getUsersFromDatabase();
        ObservableList<Contact> listOfContacts = PullDatabase.getContactsFromDatabase();
        ObservableList<Integer> customerIdList = FXCollections.observableArrayList();
        ObservableList<Integer> userIdList = FXCollections.observableArrayList();
        ObservableList<String> contactNameList = FXCollections.observableArrayList();
        for(Customer c: listOfCustomers){
            customerIdList.add(c.getId());
        }
        for(User u : listOfUsers){
            userIdList.add(u.getId());
        }
        for(Contact c: listOfContacts){
            contactNameList.add(c.getName());
        }
        customerComboBox.setItems(customerIdList);
        userComboBox.setItems(userIdList);
        contactComboBox.setItems(contactNameList);
        ObservableList<LocalTime> appointmentTimes = FXCollections.observableArrayList();
        LocalTime firstTime = LocalTime.MIN.plusHours(1);
        LocalTime lastTime = LocalTime.MAX.minusHours(1);
        while(firstTime.isBefore(lastTime)){
            appointmentTimes.add(firstTime);
            firstTime = firstTime.plusMinutes(15);
        }
        startTimeComboBox.setItems(appointmentTimes);
        endTimeComboBox.setItems(appointmentTimes);

        appointmentIDField.setText(String.valueOf(selectedAppointment.getAppointmentId()));
        appointmentTitleField.setText(selectedAppointment.getTitle());
        appointmentTypeField.setText(selectedAppointment.getType());
        appointmentDescriptionField.setText(selectedAppointment.getDescription());
        locationField.setText(selectedAppointment.getLocation());
        startDatePicker.setValue(selectedAppointment.getStartTime().toLocalDate());
        startTimeComboBox.setValue(selectedAppointment.getStartTime().toLocalTime());
        endDatePicker.setValue(selectedAppointment.getEndTime().toLocalDate());
        endTimeComboBox.setValue(selectedAppointment.getEndTime().toLocalTime());
        customerComboBox.setValue(selectedAppointment.getCustomerId());
        userComboBox.setValue(selectedAppointment.getUserId());
        contactComboBox.setValue(selectedAppointment.getContactName());
    }

}
