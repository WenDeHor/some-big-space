package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "cook_book")
public class CookBook {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "title_text")
    @Size(min = 0, max = 1000)
    private String titleText;

    @Column(name = "full_text")
    @Size(min = 0, max = 3000)
    private String fullText;

    @Column(name = "id_user")
    private int idUser;

    @Lob
    @Column(name = "Image", length = Integer.MAX_VALUE)
    private byte[] image;

    public CookBook() {
    }

    public CookBook(Date date, String titleText,
                    String fullText, int idUser, byte[] image) {
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
        this.idUser = idUser;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
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
        CookBook cookBook = (CookBook) o;
        return id == cookBook.id &&
                idUser == cookBook.idUser &&
                Objects.equals(date, cookBook.date) &&
                Objects.equals(titleText, cookBook.titleText) &&
                Objects.equals(fullText, cookBook.fullText) &&
                Arrays.equals(image, cookBook.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, date, titleText, fullText, idUser);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
