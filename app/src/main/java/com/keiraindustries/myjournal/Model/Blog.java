package com.keiraindustries.myjournal.Model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by keira on 1/4/19.
 */

public class Blog{
    private long entryDate;
    private long lastModDate;
    private String entryText;
    private String title;
    private String hashtags;

    public ArrayList<String> tags;
    public ArrayList<String> pictures;

    public Blog() {
        entryDate = 0L;
        lastModDate = 0L;
        entryText = "";
        title = "";
        hashtags = "";
        tags = new ArrayList<>();

        pictures = new ArrayList<>();
    }
    @SuppressWarnings("unchecked")
    public Blog(long entryDate, long lastModDate, String entryText, String title, ArrayList<String> tags, ArrayList<String> pictures, String hashtags) {
        this();
        this.entryDate = entryDate;
        this.lastModDate = lastModDate;
        this.entryText = entryText;
        this.title = title;
        if (tags != null) {this.tags = (ArrayList<String>) tags.clone();}
        if (pictures != null) {this.pictures = (ArrayList<String>) pictures.clone();}
        this.hashtags = hashtags;
    }

    public long getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(long entryDate) {
        this.entryDate = entryDate;
    }

    public String getEntryText() {
        return entryText;
    }

    public void setEntryText(String entryText) {
        this.entryText = entryText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHashtags() {
        return hashtags;
    }

    public void setHashtags(String hashtags) {
        this.hashtags = hashtags;
    }

    public long getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(long lastModDate) {
        this.lastModDate = lastModDate;
    }
}
