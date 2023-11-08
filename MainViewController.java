package Controller;

import Main.JDBC;
import Main.PullDatabase;
import Model.Appointment;
import Model.Customer;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
public class MainViewController {
    public RadioButton allAppointmentsRadioButton;
    public RadioButton currentMonthRadioButton;
    public RadioButton currentWeekRadioButton;
    public TableView<Appointment> appointmentTable;
    public TableColumn<Appointment, Integer> appointmentIDColumn;
    public TableColumn<Appointment, String> appointmentTitleColumn;
    public TableColumn<Appointment, String> appointmentTypeColumn;
    public TableColumn<Appointment, String> appointmentDescriptionColumn;
    public TableColumn<Appointment, String> appointmentLocationColumn;
    public TableColumn<Appointment, LocalDateTime> appointmentStartColumn;
    public TableColumn<Appointment, LocalDateTime> appointmentEndColumn;
    public TableColumn<Appointment, String> appointmentContactColumn;
    public TableColumn<Appointment, Integer> appointmentCustomerIDColumn;
    public TableColumn<Appointment, Integer> appointmentUserIDColumn;
    public Button addAppointmentButton;
    public Button updateAppointmentButton;
    public Button deleteAppointmentButton;
    public TableView<Customer> customerTable;
    public TableColumn<Customer, Integer> customerIDColumn;
    public TableColumn<Customer, String> customerNameColumn;
    public TableColumn<Customer, String> customerAddressColumn;
    public TableColumn<Customer, String> customerPhoneColumn;
    public TableColumn<Customer, String> customerDivisionColumn;
    public TableColumn<Customer, String> customerPostalCodeColumn;
    public Button addCustomerButton;
    public Button deleteCustomerButton;
    public Button reportsButton;
    public Button logoutButton;
    public ToggleGroup appointmentToggleGroup;
    public Button updateCustomerButton;

    /**
     * Manages the click action for the "All Appointments" radio button:
     *
     * Displays all appointments from the database in the appointments table.
     * @param actionEvent
     * @throws SQLException
     */
    public void allAppointmentsSelected(ActionEvent actionEvent) throws SQLException {
        ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
        appointmentTable.setItems(listOfAppointments);
    }

    /**
     * Handles the click action for the "Current Month" radio button:
     *
     * When selected, displays all appointments that fall within the current month of the local machine in the appointments table.
     * @param actionEvent
     * @throws SQLException
     */
    public void currentMonthSelected(ActionEvent actionEvent) throws SQLException{
        ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
        ObservableList<Appointment> currentMonthAppointments = FXCollections.observableArrayList();
        LocalDateTime timeNow = LocalDateTime.now();
        for(Appointment a: listOfAppointments){
            if(a.getStartTime().getYear() == timeNow.getYear() && (a.getStartTime().getMonth().equals(timeNow.getMonth()) || a.getEndTime().getMonth().equals(timeNow.getMonth()))){
                currentMonthAppointments.add(a);
            }
            appointmentTable.setItems(currentMonthAppointments);
        }
    }

    /**
     * Manages the click action for the "Current Week" radio button:
     *
     * When selected, displays all appointments scheduled for the current week of the local machine in the appointments table.
     * @param actionEvent
     * @throws SQLException
     */
    public void currentWeekSelected(ActionEvent actionEvent) throws SQLException{
        ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
        ObservableList<Appointment> currentWeekAppointments = FXCollections.observableArrayList();
        LocalDate currentDate = LocalDate.now();
        LocalDate weekStart = currentDate;
        LocalDate weekEnd = currentDate;
        if(currentDate.getDayOfWeek().equals(DayOfWeek.SUNDAY)){
            weekEnd = currentDate.plusDays(6);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.MONDAY)){
            weekStart = currentDate.minusDays(1);
            weekEnd = currentDate.plusDays(5);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.TUESDAY)){
            weekStart = currentDate.minusDays(2);
            weekEnd = currentDate.plusDays(4);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.WEDNESDAY)){
            weekStart = currentDate.minusDays(3);
            weekEnd = currentDate.plusDays(3);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.THURSDAY)){
            weekStart = currentDate.minusDays(4);
            weekEnd = currentDate.plusDays(2);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.FRIDAY)){
            weekStart = currentDate.minusDays(5);
            weekEnd = currentDate.plusDays(1);
        }
        else if(currentDate.getDayOfWeek().equals(DayOfWeek.SATURDAY)){
            weekStart = currentDate.minusDays(6);
        }
        LocalDate finalWeekStart = weekStart;
        LocalDate finalWeekEnd = weekEnd;
        for(Appointment appointment: listOfAppointments){
            LocalDate startDate = (LocalDate) appointment.getStartTime().toLocalDate();
            LocalDate endDate = (LocalDate) appointment.getEndTime().toLocalDate();
            if ((appointment.getStartTime().getYear() == LocalDateTime.now().getYear() || appointment.getEndTime().getYear() == LocalDateTime.now().getYear()) && (startDate.isEqual(finalWeekStart) || startDate.isEqual(finalWeekEnd) || endDate.isEqual(finalWeekStart) || endDate.isEqual(finalWeekEnd) || (endDate.isAfter(finalWeekStart) && endDate.isBefore(finalWeekEnd)) || (startDate.isAfter(finalWeekStart) && startDate.isBefore(finalWeekEnd)))) {
                currentWeekAppointments.add(appointment);
            }
            appointmentTable.setItems(currentWeekAppointments);
        }
    }

    /**
     * Stores all error messages specific to this screen. The displayed alert depends on the particular scenario encountered.
     * @param alertSelected
     */
    public void displayAlerts(int alertSelected){
        Alert alert = new Alert(Alert.AlertType.ERROR);

        switch(alertSelected){
            case 1:
                alert.setTitle("Error");
                alert.setHeaderText("No Appointment Selected!");
                alert.setContentText("Please select an appointment");
                alert.showAndWait();
                break;
            case 2:
                alert.setTitle("Error");
                alert.setHeaderText("No Customer Selected!");
                alert.setContentText("Please select a customer");
                alert.showAndWait();
                break;
        }
    }

    /**
     * Handles the click action for the "Add Appointment" button:
     *
     * Navigates to the "Add Appointment" screen upon clicking.
     * @param actionEvent
     * @throws IOException
     */
    public void addAppointmentOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/Views/AppointmentAdd.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Manages the click action for the "Update Appointment" button:
     *
     * Displays an error message if no appointment is selected.
     * Navigates to the "Modify Appointment" screen if an appointment is selected.
     * @param actionEvent
     * @throws IOException
     */
    public void updateAppointmentOnClick(ActionEvent actionEvent) throws IOException{
        selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
        if(selectedAppointment == null){
            displayAlerts(1);
        }
        else {
            Parent parent = FXMLLoader.load(getClass().getResource("/Views/AppointmentMod.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Handles the click action for the "Delete Appointment" button:
     *
     * Displays an error message if no appointment is selected.
     * Asks the user for confirmation if an appointment is selected.
     * Deletes the appointment from the database and the appointments table if the deletion is confirmed.
     * Displays a message confirming the successful deletion.
     * @param actionEvent
     * @throws SQLException
     */
    public void deleteAppointmentOnClick(ActionEvent actionEvent) throws SQLException {
        Appointment select = appointmentTable.getSelectionModel().getSelectedItem();
        if(select == null){
            displayAlerts(1);
        }
        else{
            Connection conn = JDBC.getConnection();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Are you sure that you want to delete the selected appointment?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                JDBC.setPreparedStatement(conn, "DELETE FROM appointments WHERE Appointment_ID=?");
                PreparedStatement preparedStatement = JDBC.getPreparedStatement();
                preparedStatement.setInt(1, select.getAppointmentId());
                preparedStatement.execute();
                ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
                appointmentTable.setItems(listOfAppointments);
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Information");
                a.setContentText("The appointment with ID: " + select.getAppointmentId() + " and type " + select.getType() + " has been successfully deleted.");
                a.showAndWait();
            }
        }
    }

    /**
     * Manages the click action for the "Add Customer" button:
     *
     * Navigates to the "Add Customer" screen upon clicking.
     * @param actionEvent
     * @throws IOException
     */
    public void addCustomerOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/Views/CustomerAdd.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Handles the click action for the "Update Customer" button:
     *
     * Displays an error message if no customer is selected.
     * Navigates to the "Modify Customer" screen if a customer is selected.
     * @param actionEvent
     * @throws IOException
     */
    public void updateCustomerOnClick(ActionEvent actionEvent) throws IOException{
        selectedCustomer = customerTable.getSelectionModel().getSelectedItem();
        if(selectedCustomer == null){
            displayAlerts(2);
        }
        else {
            Parent parent = FXMLLoader.load(getClass().getResource("/Views/CustomerMod.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        }
    }

    /**
     * Manages the click action for the "Delete Customer" button:
     *
     * Displays an error message if no customer is selected.
     * Confirms with the user if a customer is selected.
     * Deletes all associated appointments for the customer from the database and appropriate tables due to foreign key constraints.
     * Deletes the customer from the database.
     * Displays a message confirming the successful deletion.
     * @param actionEvent
     * @throws SQLException
     */
    public void deleteCustomerOnClick(ActionEvent actionEvent) throws SQLException {
        Customer select = customerTable.getSelectionModel().getSelectedItem();
        if(select == null){
            displayAlerts(2);
        }
        else{
            Connection conn = JDBC.getConnection();
            ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation");
            alert.setContentText("Are you sure that you want to delete the selected customer?");
            Optional<ButtonType> result = alert.showAndWait();
            if(result.isPresent() && result.get() == ButtonType.OK){
                for(Appointment a: listOfAppointments){
                    if(select.getId() == a.getCustomerId()){
                        JDBC.setPreparedStatement(conn, "DELETE FROM appointments WHERE Customer_ID = ?");
                        PreparedStatement appPrepared = JDBC.getPreparedStatement();
                        appPrepared.setInt(1, a.getCustomerId());
                        appPrepared.execute();
                    }
                }
                JDBC.setPreparedStatement(conn, "DELETE FROM customers WHERE Customer_ID=?");
                PreparedStatement preparedStatement = JDBC.getPreparedStatement();
                preparedStatement.setInt(1, select.getId());
                preparedStatement.execute();
                ObservableList<Customer> listOfCustomers = PullDatabase.getCustomersFromDatabase();
                ObservableList<Appointment> newlistOfAppointments = PullDatabase.getAppointmentsFromDatabase();
                customerTable.setItems(listOfCustomers);
                appointmentTable.setItems(newlistOfAppointments);
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setTitle("Information");
                a.setContentText("The customer with ID: " + select.getId() + " along with all associated appointments has been successfully deleted.");
                a.showAndWait();
            }
        }
    }
    /**
     * Retrieves the selected customer information from the screen, typically utilized when modifying a customer's details.
     */
    private static Appointment selectedAppointment;

    public static Appointment getSelectedAppointment(){
        return selectedAppointment;
    }

    /**
     * Obtains the customer information currently selected on the screen. Typically used when modifying customer details.
     */
    private static Customer selectedCustomer;
    public static Customer getSelectedCustomer(){
        return selectedCustomer;
    }

    /**
     * Sets up the screen by populating both the appointment and customer tables with the relevant data retrieved from the database.
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
        ObservableList<Customer> listOfCustomers = PullDatabase.getCustomersFromDatabase();
        appointmentIDColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        appointmentContactColumn.setCellValueFactory(new PropertyValueFactory<>("contactName"));
        appointmentUserIDColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        appointmentCustomerIDColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentTable.setItems(listOfAppointments);
        customerIDColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        customerAddressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        customerNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        customerPhoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        customerDivisionColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
        customerPostalCodeColumn.setCellValueFactory(new PropertyValueFactory<>("postalCode"));
        customerTable.setItems(listOfCustomers);
    }

    /**
     * Handles the click action for the "Reports" button:
     *
     * Navigates to the "Reports" screen upon clicking.
     * @param actionEvent
     * @throws IOException
     */
    public void reportsOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/Views/Report.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Manages the click action for the "Logout" button:
     *
     * Navigates to the login screen upon clicking.
     * @param actionEvent
     * @throws IOException
     */
    public void logoutOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/Views/Login.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}
