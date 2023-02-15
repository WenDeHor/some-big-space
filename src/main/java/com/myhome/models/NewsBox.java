package com.myhome.models;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "news_box")
public class NewsBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "link_to_news")
    private String linkToNews;

    @Column(name = "title_text")
    private String titleText;

    @Column(name = "id_user")
    private int idUser;

    @Column(name = "date")
    private Date date;

    public NewsBox() {
    }

    public NewsBox(String linkToNews, String titleText, int idUser, Date date) {
        this.linkToNews = linkToNews;
        this.titleText = titleText;
        this.idUser = idUser;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLinkToNews() {
        return linkToNews;
    }

    public void setLinkToNews(String linkToNews) {
        this.linkToNews = linkToNews;
    }

    public String getTitleText() {
        return titleText;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsBox newsBox = (NewsBox) o;
        return id == newsBox.id &&
                idUser == newsBox.idUser &&
                Objects.equals(linkToNews, newsBox.linkToNews) &&
                Objects.equals(titleText, newsBox.titleText) &&
                Objects.equals(date, newsBox.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, linkToNews, titleText, idUser, date);
    }

    @Override
    public String toString() {
        return "NewsBox{" +
                "id=" + id +
                ", linkToNews='" + linkToNews + '\'' +
                ", titleText='" + titleText + '\'' +
                ", idUser=" + idUser +
                ", date=" + date +
                '}';
    }
}
