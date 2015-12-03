package pt.up.fe.infolab.ricardo.antmobile.models;

import java.util.ArrayList;

/**
 * Created by ricardo on 12/3/15.
 */
public class ResponseAttribute {
    private boolean hasSummary;
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

    public boolean isHasSummary() {
        return hasSummary;
    }

    public void setHasSummary(boolean hasSummary) {
        this.hasSummary = hasSummary;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
