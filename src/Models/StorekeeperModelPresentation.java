package Models;

import Exceptions.EditException;
import javafx.beans.property.SimpleStringProperty;


/**
 * Created by Anna Barinova on 07.04.2017.
 */

public class StorekeeperModelPresentation {
    private final SimpleStringProperty Quality;
    private final SimpleStringProperty Value;

    public int getCountField(){return 2;}

    public StorekeeperModelPresentation(String quality, String value){
        Quality = new SimpleStringProperty(quality);
        Value = new SimpleStringProperty(value);
    }

    public String getQuality() {
        return Quality.get();
    }

    public SimpleStringProperty qualityProperty() {
        return Quality;
    }

    public void setQuality(String quality) {
        this.Quality.set(quality);
    }

    public String getValue() {
        return Value.get();
    }

    public SimpleStringProperty valueUsualProperty() {
        return Value;
    }

    public void setValue(String valueUsual) {
        this.Value.set(valueUsual);
    }


    public void applyEdit(String str, int column) throws EditException {
        if(column==0) setQuality( str);
        if(column==1) setValue(str);
    }
}
