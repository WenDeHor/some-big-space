package com.myhome.models;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Entity
@Table(name = "video_box")
public class VideoBox {
    public VideoBox() {
    }

    public VideoBox(String linkToVideo, String titleText, String addressUser, LocalDate localDate) {
        this.linkToVideo = linkToVideo;
        this.titleText = titleText;
        this.addressUser = addressUser;
        this.localDate = localDate;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_box_id")
    private Long idVideoBox;

    @Column(name = "link_to_video")
    private String linkToVideo;

    @Column(name = "title_text")
    private String titleText;

    @Column(name = "address_user")
    private String addressUser;

    @Column(name = "local_date")
    private LocalDate localDate;

    public Long getIdVideoBox() {
        return idVideoBox;
    }

    public void setIdVideoBox(Long idVideoBox) {
        this.idVideoBox = idVideoBox;
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
        VideoBox videoBox = (VideoBox) o;
        return Objects.equals(idVideoBox, videoBox.idVideoBox) &&
                Objects.equals(linkToVideo, videoBox.linkToVideo) &&
                Objects.equals(titleText, videoBox.titleText) &&
                Objects.equals(addressUser, videoBox.addressUser) &&
                Objects.equals(localDate, videoBox.localDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVideoBox, linkToVideo, titleText, addressUser, localDate);
    }

    @Override
    public String toString() {
        return "VideoBox{" +
                "idVideoBox=" + idVideoBox +
                ", linkToVideo='" + linkToVideo + '\'' +
                ", titleText='" + titleText + '\'' +
                ", addressUser='" + addressUser + '\'' +
                ", localDate=" + localDate +
                '}';
    }
}







