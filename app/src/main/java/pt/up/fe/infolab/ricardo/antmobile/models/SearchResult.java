package pt.up.fe.infolab.ricardo.antmobile.models;

import java.util.ArrayList;

/**
 * Created by ricardo on 12/3/15.
 */
public class SearchResult {

    private String uri;
    private String description;
    private String link;
    private ArrayList<String> sources;
    private String date;
    private EntityType type;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public ArrayList<String> getSources() {
        return sources;
    }

    public void setSources(ArrayList<String> sources) {
        this.sources = sources;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
