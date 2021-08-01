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
@Table(name = "publication_user")
public class PublicationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_publication")
    private Long idPublication;

    //idUser+date+id_publication
//    @Column(name = "local_date")
//    private LocalDate localDate;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "title_text")
    @Size(min = 0, max = 1000)
    private String titleText;

    @Column(name = "full_text")
    @Size(min = 0, max = 3000)
    private String fullText;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    private String email;

//    @ManyToOne (optional=false, cascade=CascadeType.ALL)
//    @JoinColumn (name="id")
//    private User user;


//    @Enumerated(value = EnumType.STRING)
//    @Column(name = "genre_of_literature")
//    private GenreOfLiterature genreOfLiterature;

}
