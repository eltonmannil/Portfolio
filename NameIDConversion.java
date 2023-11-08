package Main;

import Model.Contact;
import Model.Country;
import Model.FirstLevelDivision;
import javafx.collections.ObservableList;

import java.sql.SQLException;

/**
 * Handles various operations for name-to-id and id-to-name conversions, facilitating mapping between names and corresponding IDs.
 * @author Elton Mannil
 */
public class NameIDConversion {
    /**
     * Converts a given contact ID into a contact name.
     * Allowing the display of the contact name in the appointments table of the Main View Controller.
     * @param contactID
     * @return
     * @throws SQLException
     */
    public static String convertContactIDToName(int contactID) throws SQLException {
        String contactName = "";
        ObservableList<Contact> allContacts = PullDatabase.getContactsFromDatabase();
        for (Contact c: allContacts){
            if(c.getId() == contactID){
                contactName = c.getName();
            }
        }
        return contactName;
    }

    /**
     * Converts a provided contact name to the corresponding contact ID.
     * This conversion is necessary for storing appointment information in the database, as the table requires the contact ID as a reference.
     * @param contactName
     * @return
     * @throws SQLException
     */
    public static int convertContactNameToID(String contactName) throws SQLException{
        int contactID = 0;
        ObservableList<Contact> allContacts = PullDatabase.getContactsFromDatabase();
        for (Contact c: allContacts){
            if(c.getName().equals(contactName)){
                contactID = c.getId();
            }
        }
        return contactID;
    }

    /**
     * Converts a provided division ID into the corresponding division name.
     * This conversion is used to display the division name in the customers table of the Main View Controller.
     * @param divisionID
     * @return
     * @throws SQLException
     */
    public static String convertDivisionIDToName(int divisionID) throws SQLException{
        String divisionName = "";
        ObservableList<FirstLevelDivision> allFirstLevelDivisions = PullDatabase.getDivisionsFromDatabase();
        for(FirstLevelDivision f: allFirstLevelDivisions){
            if(f.getId() == divisionID){
                divisionName = f.getName();
            }
        }
        return divisionName;
    }

    /**
     * Converts a provided division name to the corresponding division ID.
     * This conversion is necessary for storing division IDs in the customers table of the database.
     * @param divisionName
     * @return
     * @throws SQLException
     */
    public static int convertDivisionNameToId(String divisionName) throws SQLException{
        int divisionId = 0;
        ObservableList<FirstLevelDivision> allFirstLevelDivisions = PullDatabase.getDivisionsFromDatabase();
        for(FirstLevelDivision f: allFirstLevelDivisions){
            if(f.getName().equals(divisionName)){
                divisionId = f.getId();
            }
        }
        return divisionId;
    }

    /**
     * Converts a provided country name to the corresponding country ID.
     * This conversion is utilized as a tool in the function controlling the customer country dropdown, ensuring seamless mapping between country names and their respective IDs.
     * @param countryName
     * @return
     * @throws SQLException
     */
    public static int convertCountryNameToID(String countryName) throws SQLException{
        int countryId = 0;
        ObservableList<Country> allCountries = PullDatabase.getCountriesFromDatabase();
        for(Country c: allCountries){
            if(c.getName().equals(countryName)){
                countryId = c.getId();
            }
        }
        return countryId;
    }

}
