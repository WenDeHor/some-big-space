package com.myhome.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "video_box")
public class VideoBox {
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
}







