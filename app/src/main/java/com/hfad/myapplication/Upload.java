package com.hfad.myapplication;

public class Upload {

    private String Title, Document;

    public Upload() {}

    Upload(String name, String imageUrl) {
        Title = name;
        Document = imageUrl;
    }

    public String getTitle() {
        return Title;
    }

    public String getDocument() {
        return Document;
    }

}

