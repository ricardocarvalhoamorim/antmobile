package pt.up.fe.infolab.ricardo.antmobile.models;

import java.util.ArrayList;

public class Decoration {
    private String photo;
    private ArrayList<ResponseAttribute> attributes;
    private ArrayList<ArrayList<ResponseAttribute>> levelTwoAttributes;

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public ArrayList<ResponseAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<ResponseAttribute> attributes) {
        this.attributes = attributes;
    }


    public ArrayList<ArrayList<ResponseAttribute>> getLevelTwoAttributes() {
        return levelTwoAttributes;
    }

    public void setLevelTwoAttributes(ArrayList<ArrayList<ResponseAttribute>> levelTwoAttributes) {
        this.levelTwoAttributes = levelTwoAttributes;
    }
}
