package Model;

import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceDialog;

/**
 * Creates a country
 * @author Elton Mannil
 */
public class Country {
    private int id;
    private String name;

    /**
     * Constructor for each country
     * @param i
     * @param n
     */
    public Country(int i, String n){
        id = i;
        name = n;
    }

    public static void setItems(ObservableList<String> countryNamesList) {
    }

    public static ChoiceDialog<Object> getSelectionModel() {
        return null;
    }

    /*
    getter and setter functions
     */
    public void setId(int i){id = i;}

    public int getId(){return id;}

    public void setName(String n){name = n;}

    public String getName(){return name;}

}
