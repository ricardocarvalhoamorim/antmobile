package pt.up.fe.infolab.ricardo.antmobile.models;

import java.util.ArrayList;

/**
 * Created by ricardo on 7/28/15.
 */
public class SigarraIndividual {

    private double score;
    private String uri;
    private String description;
    private ArrayList<SigarraAttribute> attributes;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public double getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ArrayList<SigarraAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<SigarraAttribute> attributes) {
        this.attributes = attributes;
    }


}
