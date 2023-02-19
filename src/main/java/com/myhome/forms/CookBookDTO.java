package com.myhome.forms;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class CookBookDTO {
    private int id;
    private Date date;
    private String titleText;
    private String fullText;
    private int idUser;
    private byte[] image;
    private String convert;

    public CookBookDTO() {
    }

    public CookBookDTO(int id, Date date, String titleText, String fullText, int idUser, byte[] image, String convert) {
        this.id = id;
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
        this.idUser = idUser;
        this.image = image;
        this.convert = convert;
    }

    public CookBookDTO(int id, Date date, String titleText, String fullText, String convert) {
        this.id = id;
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
        this.convert = convert;
    }

    public CookBookDTO(int id, String titleText, String fullText, String convert) {
        this.id = id;
        this.titleText = titleText;
        this.fullText = fullText;
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
        CookBookDTO that = (CookBookDTO) o;
        return id == that.id &&
                idUser == that.idUser &&
                Objects.equals(date, that.date) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(fullText, that.fullText) &&
                Arrays.equals(image, that.image) &&
                Objects.equals(convert, that.convert);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id, date, titleText, fullText, idUser, convert);
        result = 31 * result + Arrays.hashCode(image);
        return result;
    }
}
