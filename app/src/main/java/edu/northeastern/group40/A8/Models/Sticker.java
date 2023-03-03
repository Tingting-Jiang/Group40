package edu.northeastern.group40.A8.Models;

public class Sticker implements ItemCheckedListener {
    private String stickerId;
    private String stickerName;
    private Boolean isChosen;

    public Sticker(String stickerId, String stickerName) {
        this.stickerId = stickerId;
        this.stickerName = stickerName;
        isChosen = false;
    }

    public Sticker() {}

    public String getStickerId() {
        return stickerId;
    }

    public String getStickerName() {
        return stickerName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[Name: ").append(stickerName).append(", ");
        sb.append("Id: ").append(stickerId).append("]");

        return sb.toString();
    }

    public void unChosen() {
        isChosen = false;
    }

    @Override
    public void onItemChecked(int position) {
        isChosen = true;

    }
}