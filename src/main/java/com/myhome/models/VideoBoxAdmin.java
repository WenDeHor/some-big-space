package com.myhome.models;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Objects;

//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
@Entity
@Table(name = "video_box_admin")
public class VideoBoxAdmin {
    public VideoBoxAdmin() {
    }

    public VideoBoxAdmin(String linkToVideo, @Size(min = 0, max = 3000) String titleText, String addressAdmin, Date date) {
        this.linkToVideo = linkToVideo;
        this.titleText = titleText;
        this.addressAdmin = addressAdmin;
        this.date = date;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "video_box_id")
    private Long idVideoBox;

    @Column(name = "link_to_video")
    private String linkToVideo;

    @Column(name = "title_text")
    @Size(min = 0, max = 3000)
    private String titleText;

    @Column(name = "address_admin")
    private String addressAdmin;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

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

    public String getAddressAdmin() {
        return addressAdmin;
    }

    public void setAddressAdmin(String addressAdmin) {
        this.addressAdmin = addressAdmin;
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
        return Objects.equals(idVideoBox, that.idVideoBox) &&
                Objects.equals(linkToVideo, that.linkToVideo) &&
                Objects.equals(titleText, that.titleText) &&
                Objects.equals(addressAdmin, that.addressAdmin) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idVideoBox, linkToVideo, titleText, addressAdmin, date);
    }

    @Override
    public String toString() {
        return "VideoBoxAdmin{" +
                "idVideoBox=" + idVideoBox +
                ", linkToVideo='" + linkToVideo + '\'' +
                ", titleText='" + titleText + '\'' +
                ", addressAdmin='" + addressAdmin + '\'' +
                ", date=" + date +
                '}';
    }
}
