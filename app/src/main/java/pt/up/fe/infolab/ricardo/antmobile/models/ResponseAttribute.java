package pt.up.fe.infolab.ricardo.antmobile.models;

import java.util.ArrayList;

/**
 * Created by ricardo on 27-05-2016.
 */
public class ResponseAttribute {

    private ArrayList<ResponseData> data;
    private ResponseRelationship relationship;

    public ArrayList<ResponseData> getData() {
        return data;
    }

    public void setData(ArrayList<ResponseData> data) {
        this.data = data;
    }
}
