package edu.northeastern.group40.A8.Models;

public class User implements ItemCheckedListener {
    public String nickname;
    public String userId;
    public Boolean isChosen;

    public User(String nickname, String userId) {
        this.nickname = nickname;
        this.userId = userId;
        this.isChosen = false;
    }

    public User() {}

    public String getNickname() {

        return nickname + (isChosen ? "(chosen)" : "");
    }

    public String getUserId() {
        return userId;
    }


    @Override
    public void onItemChecked(int position) {
        this.isChosen = !isChosen;

    }
}
