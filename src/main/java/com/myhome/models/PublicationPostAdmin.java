package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "publication_post_admin")
public class PublicationPostAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "title_text")
    @Size(max = 5000)
    private String titleText;

    @Column(name = "full_text")
    @Size(max = 10000)
    private String fullText;

    @Lob
    @Column(name = "Image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;


    public PublicationPostAdmin() {
    }

    public PublicationPostAdmin(int id, Date date, String titleText, String fullText) {
        this.id = id;
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
    }

    public PublicationPostAdmin(Date date, String titleText, String fullText, byte[] image) {
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
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
        PublicationPostAdmin that = (PublicationPostAdmin) o;
        return id == that.id &&
                Objects.equals(date, that.date) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(fullText, that.fullText) &&
                Arrays.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, date, titleText, fullText);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
