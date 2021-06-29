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
@Table(name = "news_box")
public class NewsBox  {
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

}
