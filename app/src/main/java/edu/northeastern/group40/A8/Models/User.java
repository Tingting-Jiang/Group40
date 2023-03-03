package edu.northeastern.group40.A8.Models;

import java.util.HashMap;

public class User implements ItemCheckedListener {
    public String nickname;
    public String userId;
    public Boolean isChosen;
    public HashMap<String, Integer> stickerSend;

    public User(String nickname, String userId, HashMap<String, Integer> stickerSend) {
        this.nickname = nickname;
        this.userId = userId;
        this.isChosen = false;
        this.stickerSend = stickerSend;
    }

    public User() {}

    public String getNickname() {

        return nickname + (isChosen ? "(chosen)" : "");
    }

    public HashMap<String, Integer> getStickerSend() {
        return stickerSend;
    }

    public String getUserId() {
        return userId;
    }


    @Override
    public void onItemChecked(int position) {
        this.isChosen = !isChosen;

    }
}
