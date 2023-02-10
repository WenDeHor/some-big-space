package com.myhome.models;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "friends")
public class Friends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "id_user")
    private Long idUser;

    @Column(name = "id_friend")
    private Long idFriend;

    @Column(name = "address_friend")
    private String addressFriend;

    public Friends() {
    }

    public Friends(Long idUser, Long idFriend, String addressFriend) {
        this.idUser = idUser;
        this.idFriend = idFriend;
        this.addressFriend = addressFriend;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUser() {
        return idUser;
    }

    public void setIdUser(Long idUser) {
        this.idUser = idUser;
    }

    public Long getIdFriend() {
        return idFriend;
    }

    public void setIdFriend(Long idFriend) {
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
        return Objects.equals(id, friends.id) &&
                Objects.equals(idUser, friends.idUser) &&
                Objects.equals(idFriend, friends.idFriend) &&
                Objects.equals(addressFriend, friends.addressFriend);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idUser, idFriend, addressFriend);
    }

    @Override
    public String toString() {
        return "Friends{" +
                "id=" + id +
                ", idUser=" + idUser +
                ", idFriend=" + idFriend +
                ", addressFriend='" + addressFriend + '\'' +
                '}';
    }
}
