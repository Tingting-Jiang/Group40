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
        if (stickerSend == null ||stickerSend.size() == 0) {
            return "Nothing";
        }
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Integer> pairs: stickerSend.entrySet()) {
            sb.append(pairs.getKey());
            sb.append(": ").append(pairs.getValue()).append(", ");
        }
        int lastComma = sb.length()-2;
        sb.deleteCharAt(lastComma);
        return sb.toString();
    }

    public String getEmail() {
        return email;
    }



    @Override
    public void onItemChecked(int position) {

    }
}
