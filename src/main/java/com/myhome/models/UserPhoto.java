package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Objects;


@Entity
@Table(name = "user_photo")
public class UserPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "full_text")
    @Size(max = 3000)
    private String fullText;

    @Lob
    @Column(name = "Image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;

    @Column(name = "id_user")
    private int idUser;

    @Column(name = "address")
    private String address;

    public UserPhoto() {
    }

    public UserPhoto(@Size(max = 3000) String fullText, byte[] image, int idUser, String address) {
        this.fullText = fullText;
        this.image = image;
        this.idUser = idUser;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPhoto userPhoto = (UserPhoto) o;
        return id == userPhoto.id &&
                idUser == userPhoto.idUser &&
                Objects.equals(fullText, userPhoto.fullText) &&
                Arrays.equals(image, userPhoto.image) &&
                Objects.equals(address, userPhoto.address);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, fullText, idUser, address);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
