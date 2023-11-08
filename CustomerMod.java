package Controller;

import Main.JDBC;
import Main.NameIDConversion;
import Main.PullDatabase;
import Model.Country;
import Model.Customer;
import Model.FirstLevelDivision;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
public class CustomerMod {
    public TextField customerIDField;
    public TextField customerNameField;
    public TextField customerAddressField;
    public TextField customerPhoneNumberField;
    @FXML
    private ComboBox<String> customerCountryComboBox;
    @FXML
    private ComboBox<String> customerDivisionComboBox;
    public TextField customerPostalCodeField;
    public Button customerSaveButton;
    public Button customerCancelButton;
    private static Customer select;

    /**
     * Populates the divisions combo box with the relevant first-level divisions based on the selected country.
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void loadDivisions(ActionEvent actionEvent) throws SQLException {
        int countryId = 0;
        ObservableList<Country> listOfCountries = PullDatabase.getCountriesFromDatabase();
        ObservableList<FirstLevelDivision> listOfDivisions = PullDatabase.getDivisionsFromDatabase();
        ObservableList<String> USDivisionsList = FXCollections.observableArrayList();
        ObservableList<String> UKDivisionsList = FXCollections.observableArrayList();
        ObservableList<String> CanadaDivisionsList = FXCollections.observableArrayList();

        for (FirstLevelDivision f : listOfDivisions) {
            if (f.getCountryId() == 1) {
                USDivisionsList.add(f.getName());
            } else if (f.getCountryId() == 2) {
                UKDivisionsList.add(f.getName());
            } else if (f.getCountryId() == 3) {
                CanadaDivisionsList.add(f.getName());
            }
        }
        String countrySelected = customerCountryComboBox.getSelectionModel().getSelectedItem();
        for (Country c : listOfCountries) {
            if (countrySelected.equals(c.getName())) {
                countryId = NameIDConversion.convertCountryNameToID(countrySelected);
                if (countryId == 1) {
                    customerDivisionComboBox.setItems(USDivisionsList);
                } else if (countryId == 2) {
                    customerDivisionComboBox.setItems(UKDivisionsList);
                } else if (countryId == 3) {
                    customerDivisionComboBox.setItems(CanadaDivisionsList);
                }
            }
        }
    }

    /**
     * Verifies if all fields are filled; the customer cannot be updated unless all fields are completed.
     *
     * @return
     */
    public Boolean allFieldsFilled() {
        if (!customerNameField.getText().isEmpty() && !customerAddressField.getText().isEmpty() && !customerPhoneNumberField.getText().isEmpty() && customerCountryComboBox.getValue() != null && customerDivisionComboBox.getValue() != null && !customerPostalCodeField.getText().isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * Manages the click action for the save button:
     *
     * Displays an error message if any fields are not filled.
     * Updates the customer in the database based on the customer ID upon successful save.
     * Updates the customer table in the main console if the save operation is successful.
     * Navigates to the main console and displays a message confirming the successful customer update.
     *
     * @param actionEvent
     * @throws SQLException
     * @throws IOException
     */
    public void saveOnClick(ActionEvent actionEvent) throws SQLException, IOException {
        if (allFieldsFilled()) {
            Connection conn = JDBC.getConnection();
            JDBC.setPreparedStatement(conn, "UPDATE customers SET Customer_ID = ?, Customer_Name = ?, Address = ?, Phone = ?, Postal_Code = ?, Last_Update = ?, Last_Updated_By = ?, Division_ID = ? WHERE Customer_ID = ?");
            PreparedStatement preparedStatement = JDBC.getPreparedStatement();
            preparedStatement.setInt(1, Integer.parseInt(customerIDField.getText()));
            preparedStatement.setString(2, customerNameField.getText());
            preparedStatement.setString(3, customerAddressField.getText());
            preparedStatement.setString(4, customerPhoneNumberField.getText());
            preparedStatement.setString(5, customerPostalCodeField.getText());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            preparedStatement.setString(7, "admin");
            preparedStatement.setInt(8, NameIDConversion.convertDivisionNameToId(customerDivisionComboBox.getValue()));
            preparedStatement.setInt(9, Integer.parseInt(customerIDField.getText()));
            preparedStatement.execute();

            Parent parent = FXMLLoader.load(getClass().getResource("../Views/MainScreen.fxml"));
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("The customer with ID: " + customerIDField.getText() + " was successfully updated");
            alert.showAndWait();
        } else {
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
     * Navigates to the main console screen.
     * Does not update the customer information upon clicking.
     *
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
     * Sets up the screen, loading all relevant information into appropriate fields for the selected customer.
     * Additionally, populates the division combo box with first-level divisions based on the selected customer's country.
     *
     * @throws SQLException
     */
    public void initialize() throws SQLException {
        select = MainViewController.getSelectedCustomer();
        ObservableList<Country> listOfCountries = PullDatabase.getCountriesFromDatabase();
        ObservableList<String> countryNamesList = FXCollections.observableArrayList();
        for (Country c : listOfCountries) {
            countryNamesList.add(c.getName());
        }
        customerCountryComboBox.setItems(countryNamesList);
        ObservableList<FirstLevelDivision> listOfDivisions = PullDatabase.getDivisionsFromDatabase();
        ObservableList<String> USDivisionsList = FXCollections.observableArrayList();
        ObservableList<String> UKDivisionsList = FXCollections.observableArrayList();
        ObservableList<String> CanadaDivisionsList = FXCollections.observableArrayList();
        ObservableList<Integer> USDivisionIDList = FXCollections.observableArrayList();
        ObservableList<Integer> UKDivisionIDList = FXCollections.observableArrayList();
        ObservableList<Integer> CanadaDivisionIDList = FXCollections.observableArrayList();
        for (FirstLevelDivision f : listOfDivisions) {
            if (f.getCountryId() == 1) {
                USDivisionsList.add(f.getName());
                USDivisionIDList.add(f.getId());
            } else if (f.getCountryId() == 2) {
                UKDivisionsList.add(f.getName());
                UKDivisionIDList.add(f.getId());
            } else if (f.getCountryId() == 3) {
                CanadaDivisionsList.add(f.getName());
                CanadaDivisionIDList.add(f.getId());
            }
        }
        if (USDivisionIDList.contains(select.getDivisionId())) {
            customerCountryComboBox.setValue("US");
            customerDivisionComboBox.setItems(USDivisionsList);
        } else if (UKDivisionIDList.contains(select.getDivisionId())) {
            customerCountryComboBox.setValue("UK");
            customerDivisionComboBox.setItems(UKDivisionsList);
        } else if (CanadaDivisionIDList.contains(select.getDivisionId())) {
            customerCountryComboBox.setValue("Canada");
            customerDivisionComboBox.setItems(CanadaDivisionsList);
        }
        customerIDField.setText(String.valueOf(select.getId()));
        customerNameField.setText(select.getName());
        customerAddressField.setText(select.getAddress());
        customerPhoneNumberField.setText(select.getPhone());
        customerDivisionComboBox.setValue(select.getDivisionName());
        customerPostalCodeField.setText(select.getPostalCode());
    }
}
