package Controller;

import Main.ConversionTime;
import Main.JDBC;
import Main.NameIDConversion;
import Main.PullDatabase;
import Model.Appointment;
import Model.Contact;
import Model.Customer;
import Model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.awt.*;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.sql.SQLException;

public class AppointmentAdd {
    public TextField newAppointmentId;
    public TextField newAppointmentTitle;
    public TextField newAppointmentType;
    public TextField newAppointmentDescription;
    public TextField newAppointmentLocation;
    public DatePicker newAppointmentStartDate;
    public ComboBox<LocalTime> newAppointmentStartTime;
    public DatePicker newAppointmentEndDate;
    public ComboBox<LocalTime> newAppointmentEndTime;
    public ComboBox<Integer> newCustomerId;
    public ComboBox<Integer> newUserId;
    public ComboBox<String> newAppointmentContact;
    public Button saveNewAppointmentButton;
    public Button cancelNewAppointmentButton;

    /**
     * This section encompasses specific error checks tailored for this screen, each error being selected according to the relevant scenarios.
     * @param alertSelected
     */
    public void displayAlert(int alertSelected) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        switch (alertSelected) {
            case 1:
                alert.setTitle("Error");
                alert.setHeaderText("Selected time is outside of business operations!");
                alert.setContentText("The business operates between 8 am to 10 pm Eastern Standard Time (EST)");
                alert.showAndWait();
                break;
            case 2:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start/end date!");
                alert.setContentText("Appointments must start and end on the same day due to business constraints");
                alert.showAndWait();
                break;
            case 3:
                alert.setTitle("Error");
                alert.setHeaderText("Invalid start date!");
                alert.setContentText("The start date of an appointment cannot occur after the end date");
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
     * Saves a new appointment with a unique ID, while performing error checks for business hours, days, invalid entries, and overlaps.
     * Utilizes error messages from the displayAlerts function and updates the appointment data in the database table.
     *
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    public void saveNewAppointmentOnClick(ActionEvent actionEvent) throws SQLException, IOException {
        if (allFieldsFilled()) {
            int appointmentId = (int) (Math.random() * 100);
            LocalTime convertedStartTime = ConversionTime.convertToEasternTime(newAppointmentStartDate.getValue(), newAppointmentStartTime.getValue());
            LocalTime convertedEndTime = ConversionTime.convertToEasternTime(newAppointmentEndDate.getValue(), newAppointmentEndTime.getValue());
            if (convertedStartTime.getHour() < 8 || convertedStartTime.getHour() > 22 || convertedEndTime.getHour() < 8 || convertedEndTime.getHour() > 22 || ((convertedStartTime.getHour() == 22) && convertedStartTime.getMinute() > 0) || ((convertedEndTime.getHour() == 22) && convertedEndTime.getMinute() > 0)) {
                displayAlert(1);
                return;
            }
            if (!newAppointmentStartDate.getValue().isEqual(newAppointmentEndDate.getValue())) {
                displayAlert(2);
                return;
            }
            if (newAppointmentStartDate.getValue().isBefore(LocalDate.now())) {
                displayAlert(8);
                return;
            }
            if (newAppointmentStartDate.getValue().equals(LocalDate.now()) && newAppointmentStartTime.getValue().isBefore(LocalTime.now())) {
                displayAlert(7);
                return;
            }
            if (newAppointmentStartDate.getValue().isAfter(newAppointmentEndDate.getValue())) {
                displayAlert(3);
                return;
            }
            if (newAppointmentStartTime.getValue().isAfter(newAppointmentEndTime.getValue())) {
                displayAlert(4);
                return;
            }
            if (newAppointmentStartTime.getValue().equals(newAppointmentEndTime.getValue())) {
                displayAlert(5);
                return;
            }
            LocalDateTime startDateTime = LocalDateTime.of(newAppointmentStartDate.getValue(), newAppointmentStartTime.getValue());
            LocalDateTime endDateTime = LocalDateTime.of(newAppointmentEndDate.getValue(), newAppointmentEndTime.getValue());
            if (appointmentOverlap()) {
                displayAlert(6);
                return;
            }

            Connection conn = JDBC.getConnection();
            JDBC.setPreparedStatement(conn, "INSERT INTO appointments (Appointment_ID, Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Updated_By, Customer_ID, User_ID, Contact_ID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();
            preparedStatement.setInt(1, appointmentId);
            preparedStatement.setString(2, newAppointmentTitle.getText());
            preparedStatement.setString(3, newAppointmentDescription.getText());
            preparedStatement.setString(4, newAppointmentLocation.getText());
            preparedStatement.setString(5, newAppointmentType.getText());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(startDateTime));
            preparedStatement.setTimestamp(7, Timestamp.valueOf(endDateTime));
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(9, newUserId.getValue());
            preparedStatement.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setInt(11, newUserId.getValue());
            preparedStatement.setInt(12, newCustomerId.getValue());
            preparedStatement.setInt(13, newUserId.getValue());
            preparedStatement.setInt(14, NameIDConversion.convertContactNameToID(newAppointmentContact.getValue()));
            preparedStatement.execute();

            Parent parent = FXMLLoader.load(getClass().getResource("../views/MainScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("An appointment with ID: " + appointmentId + " was successfully added");
            alert.showAndWait();
            return;
        } else {
            displayAlert(9);
        }
    }

    /**
     * Handles the click action for the cancel button, directing to the main console without adding an appointment.
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void cancelNewAppointmentOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/Views/MainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Verifies if all fields on the screen are filled, ensuring a new appointment can only be added when no field is empty.
     * @return
     */
    public Boolean allFieldsFilled() {
        if (!newAppointmentTitle.getText().isEmpty() && !newAppointmentType.getText().isEmpty() && !newAppointmentDescription.getText().isEmpty() && !newAppointmentLocation.getText().isEmpty() && newAppointmentStartDate.getValue() != null && newAppointmentStartTime.getValue() != null && newAppointmentEndDate.getValue() != null && newAppointmentEndTime.getValue() != null && newCustomerId.getValue() != null && newUserId.getValue() != null && newAppointmentContact.getValue() != null) {
            return true;
        }
        return false;
    }

    /**
     * Examines whether the new appointment times overlap with existing appointments, considering conflicting start and end times with other scheduled appointments.
     * @return
     * @throws SQLException
     */
    public Boolean appointmentOverlap() throws SQLException {
        ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
        LocalDateTime startDateTime = LocalDateTime.of(newAppointmentStartDate.getValue(), newAppointmentStartTime.getValue());
        LocalDateTime endDateTime = LocalDateTime.of(newAppointmentEndDate.getValue(), newAppointmentEndTime.getValue());
        for (Appointment a : listOfAppointments) {
            if ((startDateTime.isBefore(a.getStartTime()) && endDateTime.isAfter(a.getEndTime())) || (startDateTime.isAfter(a.getStartTime()) && endDateTime.isBefore(a.getEndTime())) || ((startDateTime.isAfter(a.getStartTime()) && startDateTime.isBefore(a.getEndTime())) && endDateTime.isAfter(a.getEndTime())) || ((endDateTime.isBefore(a.getEndTime()) && endDateTime.isAfter(a.getStartTime())) && startDateTime.isBefore(a.getStartTime())) || (startDateTime.equals(a.getStartTime()) && endDateTime.equals(a.getEndTime())) || ((startDateTime.isAfter(a.getStartTime()) && startDateTime.isBefore(a.getEndTime())) && endDateTime.isEqual(a.getEndTime())) || ((endDateTime.isBefore(a.getEndTime()) && endDateTime.isAfter(a.getStartTime())) && startDateTime.isEqual(a.getStartTime())) || (startDateTime.isBefore(a.getStartTime()) && endDateTime.isEqual(a.getEndTime())) || (startDateTime.isEqual(a.getStartTime()) && endDateTime.isAfter(a.getEndTime()))) {
                return true;
            }
        }
        return false;
    }
    /**
     * This function initializes the form by populating the combo boxes with relevant values, including start/end times, contacts, and user/customer IDs.
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        ObservableList<Customer> listOfCustomers = PullDatabase.getCustomersFromDatabase();
        ObservableList<Integer> customerIDList = FXCollections.observableArrayList();
        for (Customer c : listOfCustomers) {
            customerIDList.add(c.getId());
        }
        ObservableList<User> listOfUsers = PullDatabase.getUsersFromDatabase();
        ObservableList<Integer> userIdList = FXCollections.observableArrayList();
        for (User u : listOfUsers) {
            userIdList.add(u.getId());
        }
        ObservableList<Contact> listOfContacts = PullDatabase.getContactsFromDatabase();
        ObservableList<String> contactNamesList = FXCollections.observableArrayList();
        for (Contact c : listOfContacts) {
            contactNamesList.add(c.getName());
        }
        newCustomerId.setItems(customerIDList);
        newUserId.setItems(userIdList);
        newAppointmentContact.setItems(contactNamesList);

        ObservableList<LocalTime> appointmentTimes = FXCollections.observableArrayList();
        LocalTime firstTime = LocalTime.MIN.plusHours(1);
        LocalTime lastTime = LocalTime.MAX.minusHours(1);
        while (firstTime.isBefore(lastTime)) {
            appointmentTimes.add(firstTime);
            firstTime = firstTime.plusMinutes(15);
        }
        newAppointmentStartTime.setItems(appointmentTimes);
        newAppointmentEndTime.setItems(appointmentTimes);
    }
}
