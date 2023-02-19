package com.myhome.forms;

import java.util.Objects;

public class ReferenceDTO {
    private int id;
    private String url;
    private int idUser;
    private String titleText;
    private String image;

    public ReferenceDTO(int id, String url, int idUser, String titleText, String image) {
        this.id = id;
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
        ReferenceDTO that = (ReferenceDTO) o;
        return id == that.id &&
                idUser == that.idUser &&
                Objects.equals(url, that.url) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(image, that.image);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, url, idUser, titleText, image);
    }
}
