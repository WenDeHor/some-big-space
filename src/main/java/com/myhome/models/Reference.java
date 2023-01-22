package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Arrays;
import java.util.Objects;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Entity
@Table(name = "reference")
public class Reference {
    public Reference() {
    }

    public Reference(@Size(min = 0, max = 2000) String url, String address, @Size(min = 0, max = 1000) String titleText, String name, String type, byte[] image) {
        this.url = url;
        this.address = address;
        this.titleText = titleText;
        this.name = name;
        this.type = type;
        this.image = image;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "url")
    @Size(min = 0, max = 2000)
    private String url;

    @Column(name = "address")
    private String address;

    @Column(name = "title_text")
    @Size(min = 0, max = 1000)
    private String titleText;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
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
        Reference reference = (Reference) o;
        return Objects.equals(id, reference.id) &&
                Objects.equals(url, reference.url) &&
                Objects.equals(address, reference.address) &&
                Objects.equals(titleText, reference.titleText) &&
                Objects.equals(name, reference.name) &&
                Objects.equals(type, reference.type) &&
                Arrays.equals(image, reference.image);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, url, address, titleText, name, type);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "Reference{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", address='" + address + '\'' +
                ", titleText='" + titleText + '\'' +
                ", name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", image=" + Arrays.toString(image) +
                '}';
    }
}
