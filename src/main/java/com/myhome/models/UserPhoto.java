package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Objects;


@Entity
@Table(name = "user_photo")
public class UserPhoto {
    public UserPhoto() {
    }

    public UserPhoto(String address, @Size(min = 0, max = 3000) String fullText, String name, String type, byte[] image) {
        this.address = address;
        this.fullText = fullText;
        this.name = name;
        this.type = type;
        this.image = image;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "address")
    private String address;

    @Column(name = "full_text")
    @Size(min = 0, max = 3000)
    private String fullText;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "Image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPhoto userPhoto = (UserPhoto) o;
        return Objects.equals(id, userPhoto.id) &&
                Objects.equals(address, userPhoto.address) &&
                Objects.equals(fullText, userPhoto.fullText) &&
                Objects.equals(name, userPhoto.name) &&
                Objects.equals(type, userPhoto.type) &&
                Arrays.equals(image, userPhoto.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, address, fullText, name, type);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "UserPhoto{" +
                "id=" + id +
                ", address='" + address + '\'' +
                ", fullText='" + fullText + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
