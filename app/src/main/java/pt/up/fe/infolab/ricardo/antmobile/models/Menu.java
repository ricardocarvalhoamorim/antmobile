package pt.up.fe.infolab.ricardo.antmobile.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Menu implements Serializable{

    private ArrayList<ArrayList<MenuItem>> menu;
    private String name;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ArrayList<MenuItem>> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<ArrayList<MenuItem>> menu) {
        this.menu = menu;
    }

    public class MenuItem {
        private String field;
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getField() {
            return field;
        }

        public void setField(String field) {
            this.field = field;
        }
    }
}