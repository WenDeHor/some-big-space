package com.myhome.models;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "video_box")
public class VideoBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "link_to_video")
    private String linkToVideo;

    @Column(name = "title_text")
    private String titleText;

    @Column(name = "id_user")
    private int idUser;

    @Column(name = "date")
    private Date date;

    public VideoBox() {
    }

    public VideoBox(String linkToVideo, String titleText, int idUser, Date date) {
        this.linkToVideo = linkToVideo;
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

    public String getLinkToVideo() {
        return linkToVideo;
    }

    public void setLinkToVideo(String linkToVideo) {
        this.linkToVideo = linkToVideo;
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
        VideoBox videoBox = (VideoBox) o;
        return id == videoBox.id &&
                idUser == videoBox.idUser &&
                Objects.equals(linkToVideo, videoBox.linkToVideo) &&
                Objects.equals(titleText, videoBox.titleText) &&
                Objects.equals(date, videoBox.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, linkToVideo, titleText, idUser, date);
    }

    @Override
    public String toString() {
        return "VideoBox{" +
                "id=" + id +
                ", linkToVideo='" + linkToVideo + '\'' +
                ", titleText='" + titleText + '\'' +
                ", idUser=" + idUser +
                ", date=" + date +
                '}';
    }
}







