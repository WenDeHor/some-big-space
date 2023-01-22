package com.myhome.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Entity
@Table(name = "news_box")
public class NewsBox  {
    public NewsBox() {
    }

    public NewsBox(String linkToNews, String titleText, String addressUser, LocalDate localDate) {
        this.linkToNews = linkToNews;
        this.titleText = titleText;
        this.addressUser = addressUser;
        this.localDate = localDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "news_box_id")
    private Long idNewsBox;

    @Column(name = "link_to_news")
    private String linkToNews;

    @Column(name = "title_text")
    private String titleText;

    @Column(name = "address_user")
    private String addressUser;

    @Column(name = "local_date")
    private LocalDate localDate;

    public Long getIdNewsBox() {
        return idNewsBox;
    }

    public void setIdNewsBox(Long idNewsBox) {
        this.idNewsBox = idNewsBox;
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

    public String getAddressUser() {
        return addressUser;
    }

    public void setAddressUser(String addressUser) {
        this.addressUser = addressUser;
    }

    public LocalDate getLocalDate() {
        return localDate;
    }

    public void setLocalDate(LocalDate localDate) {
        this.localDate = localDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsBox newsBox = (NewsBox) o;
        return Objects.equals(idNewsBox, newsBox.idNewsBox) &&
                Objects.equals(linkToNews, newsBox.linkToNews) &&
                Objects.equals(titleText, newsBox.titleText) &&
                Objects.equals(addressUser, newsBox.addressUser) &&
                Objects.equals(localDate, newsBox.localDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idNewsBox, linkToNews, titleText, addressUser, localDate);
    }

    @Override
    public String toString() {
        return "NewsBox{" +
                "idNewsBox=" + idNewsBox +
                ", linkToNews='" + linkToNews + '\'' +
                ", titleText='" + titleText + '\'' +
                ", addressUser='" + addressUser + '\'' +
                ", localDate=" + localDate +
                '}';
    }
}
