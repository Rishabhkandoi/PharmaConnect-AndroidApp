package com.example.shailesh.dashboard;

/**
 * Created by Harshit Budhraja on 30-03-2018.
 */

public class Article {

    private String date = "";
    private String heading = "";
    private String thumb = "";
    private String link = "";
    private String desc = "";

    public Article(String date, String heading, String thumb, String link, String desc) {
        this.date = date;
        this.heading = heading;
        this.thumb = thumb;
        this.link = link;
        this.desc = desc;
    }

    public String getDate() {
        return date;
    }

    public String getHeading() {
        return heading;
    }

    public String getThumb() {
        return thumb;
    }

    public String getLink() {
        return link;
    }

    public String getDesc() {
        return desc;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
