package com.hfad.myapplication;

public class uploadForProfilePic {
    String DpImage,Name;

    public uploadForProfilePic(String dpImage, String name) {
        DpImage = dpImage;
        Name = name;
    }

    public String getDpImage() {
        return DpImage;
    }

    public void setDpImage(String dpImage) {
        DpImage = dpImage;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
