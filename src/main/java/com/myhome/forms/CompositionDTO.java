package com.myhome.forms;

import com.myhome.models.Genre;

import java.util.Date;
import java.util.Objects;


public class CompositionDTO {
    private int id;
    private Date date;
    private String titleText;
    private String shortText;
    private String fullText;
    private String url;
    private String image;
    private Genre genre;

    public CompositionDTO() {
    }

    public CompositionDTO(int id, String titleText) {
        this.id = id;
        this.titleText = titleText;
    }

    public CompositionDTO(int id, Date date, String titleText, String shortText, String image) {
        this.id = id;
        this.date = date;
        this.titleText = titleText;
        this.shortText = shortText;
        this.image = image;
    }

    public CompositionDTO(int id, String titleText, String shortText, Genre genre, String image) {
        this.id = id;
        this.titleText = titleText;
        this.shortText = shortText;
        this.genre = genre;
        this.image = image;
    }

    public CompositionDTO(Date date, String titleText, String fullText,  String image) {
        this.date = date;
        this.titleText = titleText;
        this.fullText = fullText;
        this.image = image;
    }

    public CompositionDTO(int id, String titleText, String fullText, String image) {
        this.id = id;
        this.titleText = titleText;
        this.fullText = fullText;
        this.image = image;
    }

    public CompositionDTO(int id, String titleText, String shortText, String fullText, String image) {
        this.id = id;
        this.titleText = titleText;
        this.shortText = shortText;
        this.fullText = fullText;
        this.image = image;
    }


    public CompositionDTO(int id, Date date, String titleText, String shortText, String image, Genre genre) {
        this.id = id;
        this.date = date;
        this.titleText = titleText;
        this.shortText = shortText;
        this.image = image;
        this.genre = genre;
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

    public String getShortText() {
        return shortText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Genre getGenre() {
        return genre;
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullText() {
        return fullText;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CompositionDTO that = (CompositionDTO) o;
        return id == that.id &&
                Objects.equals(date, that.date) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(shortText, that.shortText) &&
                Objects.equals(fullText, that.fullText) &&
                Objects.equals(url, that.url) &&
                Objects.equals(image, that.image) &&
                genre == that.genre;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date, titleText, shortText, fullText, url, image, genre);
    }
}
