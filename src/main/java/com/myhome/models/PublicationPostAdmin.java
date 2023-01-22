package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Entity
@Table(name = "publication_post_admin")
public class PublicationPostAdmin {
    public PublicationPostAdmin() {
    }

    public PublicationPostAdmin(Date date, @Size(min = 0, max = 5000) String titleText, @Size(min = 0, max = 10000) String fullText, String address, String name, String type, byte[] image) {
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
        this.address = address;
        this.name = name;
        this.type = type;
        this.image = image;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_publication")
    private Long idPublication;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "title_text")
    @Size(min = 0, max = 5000)
    private String titleText;

    @Column(name = "full_text")
    @Size(min = 0, max = 10000)
    private String fullText;

    @Column(name = "address")
    private String address;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "Image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;

    public Long getIdPublication() {
        return idPublication;
    }

    public void setIdPublication(Long idPublication) {
        this.idPublication = idPublication;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
        PublicationPostAdmin that = (PublicationPostAdmin) o;
        return Objects.equals(idPublication, that.idPublication) &&
                Objects.equals(date, that.date) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(fullText, that.fullText) &&
                Objects.equals(address, that.address) &&
                Objects.equals(name, that.name) &&
                Objects.equals(type, that.type) &&
                Arrays.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(idPublication, date, titleText, fullText, address, name, type);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "PublicationPostAdmin{" +
                "idPublication=" + idPublication +
                ", date=" + date +
                ", titleText='" + titleText + '\'' +
                ", fullText='" + fullText + '\'' +
                ", address='" + address + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
