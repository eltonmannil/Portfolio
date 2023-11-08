package Controller;

import Main.JDBC;
import Main.PullDatabase;
import Model.Appointment;
import Model.Contact;
import Model.Report;
import Model.ReportByMonth;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDateTime;
public class ReportController {
    public TableColumn appointmentType;
    @FXML
    private Button backButton;
    @FXML
    private ComboBox<String> contactNameComboBox;
    @FXML
    private TableView<Appointment> contactScheduleTable;
    @FXML
    private TableColumn<Appointment, Integer> appointmentIdColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentTitleColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentDescriptionColumn;
    @FXML
    private TableColumn<Appointment, String> appointmentLocationColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentStartColumn;
    @FXML
    private TableColumn<Appointment, LocalDateTime> appointmentEndColumn;
    @FXML
    private TableColumn<Appointment, Integer> appointmentCustomerIdColumn;
    @FXML
    private TableView<ReportByMonth> appointmentByMonthTable;
    @FXML
    private TableColumn<ReportByMonth, String> appointmentMonthColumn;
    @FXML
    private TableColumn<ReportByMonth, String> appointmentTypeColumn;
    @FXML
    private TableColumn<ReportByMonth, Integer> typeTotalColumn;
    @FXML
    private TableView<Report> customerByDivisionTable;
    @FXML
    private TableColumn<Report, String> divisionNameColumn;
    @FXML
    private TableColumn<Report, Integer> divisionTotalColumn;
    @FXML
    private Button logoutButton;

    /**
     * Generates custom report data, specifically the total number of customers for each division:
     *
     * Utilizes an SQL statement to group data by division ID and perform a count operation for each division.
     * Displays division names and corresponding customer totals in the appropriate table.
     * @return
     * @throws SQLException
     */
    public static ObservableList<Report> generateCustomReport() throws SQLException{
        ObservableList <Report> customReport = FXCollections.observableArrayList();
        Connection conn = JDBC.getConnection();
        String sql1 = "SELECT first_level_divisions.Division, COUNT(*) AS divisionCount FROM customers INNER JOIN first_level_divisions ON customers.Division_ID = first_level_divisions.Division_ID WHERE first_level_divisions.Division_ID = customers.Division_ID GROUP BY first_level_divisions.Division_ID ORDER BY COUNT(*) DESC";
        JDBC.setPreparedStatement(conn, sql1);
        PreparedStatement preparedStatement = JDBC.getPreparedStatement();
        ResultSet resultSet = preparedStatement.executeQuery();
        while(resultSet.next()){
            String divisionName = resultSet.getString("Division");
            int divisionTotal = resultSet.getInt("divisionCount");
            Report report = new Report(divisionTotal, divisionName);
            customReport.add(report);
        }
        return customReport;
    }

    /**
     * Generates a report detailing the total appointments with the same type for each month:
     *
     * Achieved using an SQL statement that retrieves the month name and appointment type, and calculates the total appointments for each type.
     * Displays the month, appointment type, and corresponding total in the appropriate table.
     * @return
     * @throws SQLException
     */
    public static ObservableList<ReportByMonth> generateAppointmentByMonthAndType() throws SQLException{
        ObservableList<ReportByMonth> reportByMonths = FXCollections.observableArrayList();
        Connection conn = JDBC.getConnection();

        String sql = "SELECT MONTHNAME(Start) AS month, \n" +
                "appointments.Type, COUNT(appointments.Type) AS typeCount \n" +
                "FROM appointments \n" +
                "GROUP BY MONTHNAME(Start), appointments.Type\n" +
                "ORDER BY month DESC";
        JDBC.setPreparedStatement(conn, sql);
        PreparedStatement preparedStatement = JDBC.getPreparedStatement();
        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()){
            String monthName = resultSet.getString("month");
            String typeName = resultSet.getString("Type");
            int typeTotal = resultSet.getInt("typeCount");
            ReportByMonth reportByMonth = new ReportByMonth(monthName, typeName, typeTotal);
            reportByMonths.add(reportByMonth);
        }
        return reportByMonths;
    }



    /**
     * Manages the selection of the contact combo box in the contact schedule tab:
     *
     * Retrieves and loads all appointments associated with the selected contact into the appropriate display area.
     * @throws SQLException
     */
    public void contactScheduleDropDown() throws SQLException {
        ObservableList<Appointment> listOfAppointments = PullDatabase.getAppointmentsFromDatabase();
        ObservableList<Appointment> appointmentsFiltered = FXCollections.observableArrayList();
        String contactSelected = contactNameComboBox.getSelectionModel().getSelectedItem();
        for(Appointment a: listOfAppointments){
            if(contactSelected.equals(a.getContactName())){
                appointmentsFiltered.add(a);
            }
        }
        contactScheduleTable.setItems(appointmentsFiltered);
    }

    /**
     * Handles the click action for the "Back" button:
     *
     * Navigates back to the main console screen upon selection.
     * @param actionEvent
     * @throws IOException
     */
    public void backButtonOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../Views/MainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Manages the click action for the "Logout" button:
     *
     * Redirects to the login screen upon clicking.
     * @param actionEvent
     * @throws IOException
     */
    public void logoutOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("../views/Login.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Initializes screen
     * Loads data into appropriate tables
     * @throws SQLException
     */
    public void initialize () throws SQLException{
        ObservableList<Contact> listOfContacts = PullDatabase.getContactsFromDatabase();
        ObservableList<String> contactNamesList = FXCollections.observableArrayList();
        for(Contact c: listOfContacts){
            contactNamesList.add(c.getName());
        }
        contactNameComboBox.setItems(contactNamesList);
        appointmentIdColumn.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        appointmentTitleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        appointmentDescriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        appointmentLocationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        appointmentStartColumn.setCellValueFactory(new PropertyValueFactory<>("startTime"));
        appointmentEndColumn.setCellValueFactory(new PropertyValueFactory<>("endTime"));
        appointmentCustomerIdColumn.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        appointmentMonthColumn.setCellValueFactory(new PropertyValueFactory<>("month"));
        appointmentTypeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
        typeTotalColumn.setCellValueFactory(new PropertyValueFactory<>("typeTotal"));
        divisionNameColumn.setCellValueFactory(new PropertyValueFactory<>("divisionName"));
        divisionTotalColumn.setCellValueFactory(new PropertyValueFactory<>("divisionTotal"));
        appointmentByMonthTable.setItems(generateAppointmentByMonthAndType());
        customerByDivisionTable.setItems(generateCustomReport());
    }
}
