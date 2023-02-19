package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "video_box_admin")
public class VideoBoxAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "link_to_video")
    private String linkToVideo;

    @Column(name = "title_text")
    @Size(max = 3000)
    private String titleText;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    public VideoBoxAdmin() {
    }

    public VideoBoxAdmin(String linkToVideo, @Size(max = 3000) String titleText, Date date) {
        this.linkToVideo = linkToVideo;
        this.titleText = titleText;
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
        VideoBoxAdmin that = (VideoBoxAdmin) o;
        return id == that.id &&
                Objects.equals(linkToVideo, that.linkToVideo) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, linkToVideo, titleText, date);
    }
}
