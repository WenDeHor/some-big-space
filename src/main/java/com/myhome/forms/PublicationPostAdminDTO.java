package com.myhome.forms;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class PublicationPostAdminDTO {
    private int id;
    private Date date;
    private String titleText;
    private String fullText;
    private byte[] image;
    private String convert;

    public PublicationPostAdminDTO() {
    }

    public PublicationPostAdminDTO(int id, Date date, String titleText, String fullText, String convert) {
        this.id = id;
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
        this.convert = convert;
    }

    public PublicationPostAdminDTO(int id, String titleText, String fullText, String convert) {
        this.id = id;
        this.titleText = titleText;
        this.fullText = fullText;
        this.convert = convert;
    }

    public PublicationPostAdminDTO( Date date, String titleText, String fullText, String convert) {
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
        this.convert = convert;
    }

    public PublicationPostAdminDTO(Date date, String titleText, String fullText, byte[] image, String convert) {
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
        this.image = image;
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
        PublicationPostAdminDTO that = (PublicationPostAdminDTO) o;
        return id == that.id &&
                Objects.equals(date, that.date) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(fullText, that.fullText) &&
                Arrays.equals(image, that.image) &&
                Objects.equals(convert, that.convert);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, date, titleText, fullText, convert);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }

    @Override
    public String toString() {
        return "PublicationPostAdmin{" +
                "id=" + id +
                ", date=" + date +
                ", titleText='" + titleText + '\'' +
                ", fullText='" + fullText + '\'' +
                ", image=" + Arrays.toString(image) +
                ", convert='" + convert + '\'' +
                '}';
    }
}
