package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;


@Entity
@Table(name = "composition")
public class Composition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "genre")
    private Genre genre;

    @Column(name = "publication")
    private PublicationType publicationType;

    @Column(name = "title_text")
    @Size( max = 150)
    private String titleText;

    @Column(name = "short_text")
    @Size( max = 1000)
    private String shortText;

    @Column(name = "full_text")
    @Size( max = 20000)
    private String fullText;

    @Column(name = "id_user")
    private int idUser;

    @Lob
    @Column(name = "Image", length = Integer.MAX_VALUE)
    private byte[] image;

    @Column(name = "convert")
    private String convert;

    public Composition() {
    }

    public Composition(PublicationType publicationType, String titleText, String fullText, String convert) {
        this.publicationType=publicationType;
        this.titleText = titleText;
        this.fullText = fullText;
        this.convert = convert;
    }

    public Composition(int id, Date date, Genre genre, PublicationType publicationType,
                       String titleText, String shortText, String fullText,
                       int idUser, String convert) {
        this.id = id;
        this.date = date;
        this.genre = genre;
        this.publicationType = publicationType;
        this.titleText = titleText;
        this.shortText = shortText;
        this.fullText = fullText;
        this.idUser = idUser;
        this.convert = convert;
    }

    public Composition(int id, Date date, Genre genre, String titleText,
                       String shortText, String fullText, String convert) {
        this.id = id;
        this.date = date;
        this.genre = genre;
        this.titleText = titleText;
        this.shortText = shortText;
        this.fullText = fullText;
        this.convert = convert;
    }

    public Composition(int id, Genre genre, String titleText,
                       String shortText, String convert) {
        this.id = id;
        this.genre = genre;
        this.titleText = titleText;
        this.shortText = shortText;
        this.convert = convert;
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

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public PublicationType getPublicationType() {
        return publicationType;
    }

    public void setPublicationType(PublicationType publicationType) {
        this.publicationType = publicationType;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
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

    public String getConvert() {
        return convert;
    }

    public void setConvert(String convert) {
        this.convert = convert;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Composition that = (Composition) o;
        return id == that.id &&
                idUser == that.idUser &&
                Objects.equals(date, that.date) &&
                genre == that.genre &&
                publicationType == that.publicationType &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(shortText, that.shortText) &&
                Objects.equals(fullText, that.fullText) &&
                Arrays.equals(image, that.image) &&
                Objects.equals(convert, that.convert);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, date, genre, publicationType, titleText, shortText, fullText, idUser, convert);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
