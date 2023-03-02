package edu.northeastern.group40.A8.Models;

public class Sticker implements ItemCheckedListener{
    private String imageId;

    public Sticker(String imageId) {
        this.imageId = imageId;
    }

    public String getImageId() {
        return imageId;
    }

    @Override
    public void onItemChecked(int position) {

    }
}