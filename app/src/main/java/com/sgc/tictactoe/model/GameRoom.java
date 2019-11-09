package com.sgc.tictactoe.model;

public class GameRoom {

    private String roomId;

    private int countUser = 1;

    private int sizeField;

    public GameRoom(String roomId, int sizeField) {
        this.sizeField = sizeField;
        this.roomId = roomId;
    }

    public GameRoom( ) {
        this.sizeField = 3;
    }


    public int getSizeField() {
        return sizeField;
    }

    public void setSizeField(int sizeField) {
        this.sizeField = sizeField;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getCountUser() {
        return countUser;
    }

    public void setCountUser(int countUser) {
        this.countUser = countUser;
    }
}
