package models;

public class Seat {
    private int seat;
    private int room;

    public int getRoom() {
        return room;
    }

    public void setRoom(int room) {
        this.room = room;
    }

    public boolean isAvaliability() {
        return avaliability;
    }

    public void setAvaliability(boolean avaliability) {
        this.avaliability = avaliability;
    }

    private boolean avaliability;

    public int getSeat() {
        return seat;
    }

    public void setSeat(int seat) {
        this.seat = seat;
    }
}
