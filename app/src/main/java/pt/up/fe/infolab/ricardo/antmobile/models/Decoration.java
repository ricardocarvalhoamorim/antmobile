package pt.up.fe.infolab.ricardo.antmobile.models;

import java.util.ArrayList;

public class Decoration {
    private String photo;
    private ResponseAttribute attributes;
    private ArrayList<ResponseAttribute> levelTwoAttributes;


    public ArrayList<ResponseAttribute> getLevelTwoAttributes() {
        return levelTwoAttributes;
    }

    public void setLevelTwoAttributes(ArrayList<ResponseAttribute> levelTwoAttributes) {
        this.levelTwoAttributes = levelTwoAttributes;
    }

    public ResponseAttribute getAttributes() {
        return attributes;
    }

    public void setAttributes(ResponseAttribute attributes) {
        this.attributes = attributes;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }
}
