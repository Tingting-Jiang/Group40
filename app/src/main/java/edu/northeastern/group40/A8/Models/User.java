package edu.northeastern.group40.A8.Models;

import java.util.HashMap;
import java.util.Map;

public class User implements ItemCheckedListener {
    private String nickname;
    private String email;
    private String userId;
    private HashMap<String, Integer> stickerSend;


    public User(String nickname, String userId, HashMap<String, Integer> stickerSend, String email) {
        this.nickname = nickname;
        this.userId = userId;
        this.stickerSend = stickerSend;
        this.email = email;
    }

    public User() {}

    public String getNickname() {

        return nickname;
    }


    public HashMap<String, Integer> getStickerSend() {
        return stickerSend;
    }

    public String getUserId() {
        return userId;
    }

    public void setStickerSend(HashMap<String, Integer> updatedStickerSend) {
        this.stickerSend = updatedStickerSend;
    }

    public String displayStickerSend() {
        StringBuilder sb = new StringBuilder();
        sb.append("You've sent: ");
        for (Map.Entry<String, Integer> pairs: stickerSend.entrySet()) {
            sb.append(pairs.getKey());
            sb.append(": ").append(pairs.getValue()).append(", ");
        }
        return sb.toString();
    }

    public String getEmail() {
        return email;
    }



    @Override
    public void onItemChecked(int position) {

    }
}
