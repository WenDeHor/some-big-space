package com.myhome.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "video_box_admin")
public class VideoBoxAdmin {
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
}
