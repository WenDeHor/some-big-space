package com.myhome.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "friends")
public class Friends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "id_user")
    private int idUser;

    @Column(name = "id_friend")
    private int idFriend;

    @Column(name = "address_friend")
    private String addressFriend;

    public Friends() {
    }

    public Friends(int idUser, int idFriend, String addressFriend) {
        this.idUser = idUser;
        this.idFriend = idFriend;
        this.addressFriend = addressFriend;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdFriend() {
        return idFriend;
    }

    public void setIdFriend(int idFriend) {
        this.idFriend = idFriend;
    }

    public String getAddressFriend() {
        return addressFriend;
    }

    public void setAddressFriend(String addressFriend) {
        this.addressFriend = addressFriend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friends friends = (Friends) o;
        return id == friends.id &&
                idUser == friends.idUser &&
                idFriend == friends.idFriend &&
                Objects.equals(addressFriend, friends.addressFriend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUser, idFriend, addressFriend);
    }
}
