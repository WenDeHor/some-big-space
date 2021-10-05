package com.myhome.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long idUser;

    @Column(name = "login")
    private String login;//REGISTRATION PAGE

    @Column(name = "settlement")
    private String settlement;//REGISTRATION PAGE

    @Column(name = "email")
    private String email;//REGISTRATION PAGE

    @Column(name = "password")
    private String password;//REGISTRATION PAGE

    @Column(name = "address")
    private String address;

    @Column(name = "currency_code")
    private String currencyCode;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "state")
    private State state;

    @Column(name = "counter")
    private Integer counter;



    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date")
    private Date date;


//    @OneToMany( mappedBy = "user", fetch = FetchType.EAGER)
//    private List<PublicationUser> publicationUsers;

//    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")//, cascade = CascadeType.REMOVE, orphanRemoval = true)
//    @OrderBy("dateTime DESC")
//    @JsonIgnore
//    protected List<PublicationUser> publicationUsers;

}
