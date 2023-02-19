package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Objects;

@Entity
@Table(name = "reference")
public class Reference {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "url")
    @Size(max = 2000)
    private String url;

    @Column(name = "id_user")
    private int idUser;

    @Column(name = "title_text")
    @Size(max = 1000)
    private String titleText;

    @Lob
    @Column(name = "Image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;

    public Reference() {
    }

    public Reference(@Size(max = 2000) String url, int idUser, @Size(max = 1000) String titleText, byte[] image) {
        this.url = url;
        this.idUser = idUser;
        this.titleText = titleText;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
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
        Reference reference = (Reference) o;
        return id == reference.id &&
                idUser == reference.idUser &&
                Objects.equals(url, reference.url) &&
                Objects.equals(titleText, reference.titleText) &&
                Arrays.equals(image, reference.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, url, idUser, titleText);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
