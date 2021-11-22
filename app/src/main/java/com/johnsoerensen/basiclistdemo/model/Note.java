package com.johnsoerensen.basiclistdemo.model;

import java.util.Date;

public class Note {

    private String text;
    private Date date;
    private String docID;
    private String imageID;

    public Note(String text, Date date, String docID, String imageID) {
        this.text = text;
        this.date = date;
        this.docID = docID;
        this.imageID = imageID;
    }

    public Note(String text, Date date, String docID) {
        this(text, date, docID, null);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getImageID() {
        return imageID;
    }

    public void setImageID(String imageID) {
        this.imageID = imageID;
    }
}
