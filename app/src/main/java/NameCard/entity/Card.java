package NameCard.entity;

import Utils.NameCard;

/**
 * Created by ZTR on 1/2/16.
 */
public class Card {

    public String name;
    public String telephoneNum;
    public String address;
    public String occupation;
    public String email;
    public String photoPath;

    public Card(String name, String telephoneNum, String address, String occupation, String email, String photoPath) {
        this.name = name;
        this.telephoneNum = telephoneNum;
        this.address = address;
        this.occupation = occupation;
        this.email = email;
        this.photoPath = photoPath;
    }

    public Card(NameCard nameCard){
        this.name = nameCard.getName();
        this.telephoneNum = nameCard.getPhone();
        this.address = nameCard.getAddress();
        this.occupation = nameCard.getJob();
        this.email = nameCard.getEmail();
        this.photoPath = nameCard.getPhotoPath();
    }

}
