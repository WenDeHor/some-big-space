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
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "local_date", nullable = false)
    private Date localDate;

    @Column(name = "genre")
    private Genre genre;

    @Column(name = "publication")
    private PublicationType publicationType;

    @Column(name = "title_text")
    @Size(min = 0, max = 150)
    private String titleText;

    @Column(name = "short_text")
    @Size(min = 0, max = 1000)
    private String shortText;

    @Column(name = "full_text")
    @Size(min = 0, max = 20000)
    private String fullText;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "Image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;

    @Column(name = "convert")
    private String convert;

    public Composition() {
    }

    public Composition(String titleText, String fullText, String convert) {
        this.titleText = titleText;
        this.fullText = fullText;
        this.convert = convert;
    }

    public Composition(Long id, Date localDate, Genre genre, PublicationType publicationType, String titleText,String shortText,  String fullText, String email, String address, Long userId, String name, String type, String convert) {
        this.id = id;
        this.localDate = localDate;
        this.genre = genre;
        this.publicationType = publicationType;
        this.titleText = titleText;
        this.shortText = shortText;
        this.fullText = fullText;
        this.email = email;
        this.address = address;
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.convert = convert;
    }

    public Composition(Long id, Date localDate, Genre genre, String titleText,String shortText,  String fullText, String convert) {
        this.id = id;
        this.localDate = localDate;
        this.genre = genre;
        this.titleText = titleText;
        this.shortText = shortText;
        this.fullText = fullText;
        this.convert = convert;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getLocalDate() {
        return localDate;
    }

    public void setLocalDate(Date localDate) {
        this.localDate = localDate;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
        return Objects.equals(id, that.id) &&
                Objects.equals(localDate, that.localDate) &&
                genre == that.genre &&
                publicationType == that.publicationType &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(shortText, that.shortText) &&
                Objects.equals(fullText, that.fullText) &&
                Objects.equals(email, that.email) &&
                Objects.equals(address, that.address) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Arrays.equals(image, that.image) &&
                Objects.equals(convert, that.convert);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, localDate, genre, publicationType, titleText, shortText, fullText, email, address, userId, name, type, convert);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "Composition{" +
                "id=" + id +
                ", localDate=" + localDate +
                ", genre=" + genre +
                ", publicationType=" + publicationType +
                ", titleText='" + titleText + '\'' +
                ", shortText='" + shortText + '\'' +
                ", fullText='" + fullText + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", userId=" + userId +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", image=" + Arrays.toString(image) +
                ", convert='" + convert + '\'' +
                '}';
    }
}
