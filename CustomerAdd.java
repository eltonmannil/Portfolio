package Controller;

import Main.JDBC;
import Main.NameIDConversion;
import Main.PullDatabase;
import Model.Country;
import Model.FirstLevelDivision;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class CustomerAdd {
    @FXML private TextField newCustomerId;
    @FXML private TextField newCustomerName;
    @FXML private TextField newCustomerAddress;
    @FXML private TextField newCustomerPhoneNumber;
    @FXML private ComboBox<String> newCustomerCountry;
    @FXML private ComboBox<String> newCustomerDivision;
    @FXML private TextField newCustomerPostalCode;
    @FXML private Button saveNewCustomerButton;
    @FXML private Button cancelNewCustomerButton;

    /**
     * Populates the combo box with first-level divisions based on the user's selection.
     * @param actionEvent
     * @throws SQLException
     */
    @FXML
    public void loadDivisions(ActionEvent actionEvent) throws SQLException{
        int countryId = 0;
        ObservableList<Country> listOfCountries = PullDatabase.getCountriesFromDatabase();
        ObservableList<FirstLevelDivision> listOfDivisions = PullDatabase.getDivisionsFromDatabase();
        ObservableList<String> USDivisionsList = FXCollections.observableArrayList();
        ObservableList<String> UKDivisionsList = FXCollections.observableArrayList();
        ObservableList<String> CanadaDivisionsList = FXCollections.observableArrayList();
        for(FirstLevelDivision f: listOfDivisions){
            if(f.getCountryId() == 1){
                USDivisionsList.add(f.getName());
            }
            else if(f.getCountryId() == 2){
                UKDivisionsList.add(f.getName());
            }
            else if(f.getCountryId() == 3){
                CanadaDivisionsList.add(f.getName());
            }
        }
        String countrySelected = (String) newCustomerCountry.getSelectionModel().getSelectedItem();
        for(Country c: listOfCountries) {
            if(countrySelected.equals(c.getName())) {
                countryId = NameIDConversion.convertCountryNameToID(countrySelected);
                if (countryId == 1) {
                    newCustomerDivision.setItems(USDivisionsList);
                } else if (countryId == 2) {
                    newCustomerDivision.setItems(UKDivisionsList);
                } else if (countryId == 3) {
                    newCustomerDivision.setItems(CanadaDivisionsList);
                }
            }
        }
    }

    /**
     * Verifies that all fields are filled; a customer cannot be added if any field is empty.
     * @return
     */
    public Boolean allFieldsFilled(){
        if(!newCustomerName.getText().isEmpty() && !newCustomerAddress.getText().isEmpty() && !newCustomerPhoneNumber.getText().isEmpty() && newCustomerCountry.getValue() != null && newCustomerDivision.getValue() != null && !newCustomerPostalCode.getText().isEmpty()){
            return true;
        }
        return false;
    }

    /**
     * Manages the click action for the save button:
     *
     * Adds a new customer to the customers table in the database.
     * Displays an error message if any fields are not filled.
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    @FXML
    public void saveOnClick(ActionEvent actionEvent) throws SQLException, IOException {
        if(allFieldsFilled()){
            int newCustomerId = (int) (Math.random() *100);
            Connection conn = JDBC.getConnection();
            JDBC.setPreparedStatement(conn, "INSERT INTO customers (Customer_ID, Customer_Name, Address, Phone, Postal_Code, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES (?,?,?,?,?,?,?,?,?,?)");
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();
            preparedStatement.setInt(1, newCustomerId);
            preparedStatement.setString(2, newCustomerName.getText());
            preparedStatement.setString(3, newCustomerAddress.getText());
            preparedStatement.setString(4, newCustomerPhoneNumber.getText());
            preparedStatement.setString(5, newCustomerPostalCode.getText());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(7, "admin");
            preparedStatement.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(9, "admin");
            preparedStatement.setInt(10, NameIDConversion.convertDivisionNameToId(newCustomerDivision.getValue()));
            preparedStatement.execute();

            Parent parent = FXMLLoader.load(getClass().getResource("/Views/MainScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("A customer with ID: " + newCustomerId + " was successfully added");
            alert.showAndWait();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("One or more fields empty!");
            alert.setContentText("Please fill out all required fields");
            alert.showAndWait();
        }

    }

    /**
     * Handles the click action for the cancel button:
     *
     * Navigates to the main console screen when clicked.
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    public void cancelOnClick(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(getClass().getResource("/Views/MainScreen.fxml"));
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    /**
     * Sets up the screen and populates the relevant combo boxes (country, division).
     * Includes a lambda function that handles action events for the save and cancel buttons,
     * accurately assigning the correct function to each button along with the appropriate exceptions.
     * Eliminates the manual assignment of functions in Scene Builder.
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        ObservableList<Country> listOfCountries = PullDatabase.getCountriesFromDatabase();
        ObservableList<String> countryNamesList = FXCollections.observableArrayList();
        for(Country c: listOfCountries){
            countryNamesList.add(c.getName());
        }
        newCustomerCountry.setItems(countryNamesList);
        /*
        Lambda expression to handle click event for the save button
         */
        saveNewCustomerButton.setOnAction(actionEvent -> {
            try {
                saveOnClick(actionEvent);
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        cancelNewCustomerButton.setOnAction(actionEvent -> {
            try {
                cancelOnClick(actionEvent);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void saveNewCustomerOnClick(ActionEvent actionEvent) {
    }

    public void cancelNewCustomerOnClick(ActionEvent actionEvent) {
    }
}