package org.example.matchinggameclient.model;

public class Invitation {
    private User sender;

    public Invitation(User sender)
    {
        this.sender = sender;
    }
    
    public int getSenderID()
    {
    	return sender.getID();
    }

    public String getSenderName()
    {
        return sender.getUsername();
    }

    public int getSenderStars()
    {
        return sender.getStar();
    }

    public int getSenderRank()
    {
        return sender.getRank();
    }
}
