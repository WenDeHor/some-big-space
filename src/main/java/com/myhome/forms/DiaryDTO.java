package com.myhome.forms;

import java.util.Date;
import java.util.Objects;

public class DiaryDTO {
    private int id;
    private Date date;
    private String titleText;
    private String fullText;
    private int idUser;
    private String image;

    public DiaryDTO() {
    }

    public DiaryDTO(int id, Date date, String titleText, String fullText, int idUser, String image) {
        this.id = id;
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
        this.idUser = idUser;
        this.image = image;
    }

    public DiaryDTO(int id, String titleText, String fullText, String image) {
        this.id = id;
        this.titleText = titleText;
        this.fullText = fullText;
        this.image = image;
    }

    public DiaryDTO(String titleText, String fullText, String image) {
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

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DiaryDTO diaryDTO = (DiaryDTO) o;
        return id == diaryDTO.id &&
                idUser == diaryDTO.idUser &&
                Objects.equals(date, diaryDTO.date) &&
                Objects.equals(titleText, diaryDTO.titleText) &&
                Objects.equals(fullText, diaryDTO.fullText) &&
                Objects.equals(image, diaryDTO.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, titleText, fullText, idUser, image);
    }
}
