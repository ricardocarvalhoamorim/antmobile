package pt.up.fe.infolab.ricardo.antmobile.models;


import java.util.ArrayList;

public class ResponseData {
    private String label;
    private String uri;

    private Object value;


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        if (value instanceof String)
            return (String) value;

        else if (value instanceof ArrayList) {
            String objectText = "";

            ArrayList<String> attrs = (ArrayList<String>) value;
            for (String attr : attrs)
                objectText += attr + ", ";
            return objectText.substring(0, objectText.length()-1);
        }

        return "undefined";
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
