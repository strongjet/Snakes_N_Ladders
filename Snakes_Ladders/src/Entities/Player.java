package Entities;

import java.io.Serializable;

public class Player implements Serializable {

    public String username;
    public String skin;
    public int position, score;

//    public Player(String username, int position, String skin, String score) {
//
//        this.username = username;
//        this.position = position;
//        this.skin = skin;
//    }

    public String getUsername() {
        return this.username;
    }

    public int getPosition() {
        return this.position;
    }

    public String getSkin() {
        return this.skin;
    }

    public int getScore() {
        return this.score;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public void setSkin(String skin) {
        this.skin = skin;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
