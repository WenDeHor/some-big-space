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
@Table(name = "publication_post_admin")
public class PublicationPostAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_publication")
    private Long idPublication;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "title_text")
    @Size(min = 0, max = 5000)
    private String titleText;

    @Column(name = "full_text")
    @Size(min = 0, max = 10000)
    private String fullText;

    @Column(name = "address")
    private String address;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Lob
    @Column(name = "Image", length = Integer.MAX_VALUE, nullable = true)
    private byte[] image;

}
