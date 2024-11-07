package org.example.matchinggameclient.model;

public class Card {

    private int id;

    private String image;

    public Card() {}

    public Card(int id, String image) {
        this.id = id;
        this.image = image;
    }

    public String saveCard(String image){
        String sql = String.format("INSERT INTO Cards (image) VALUES ('%s')", image);
        return sql;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "(" + id + ", " + image + ")";
    }
}
