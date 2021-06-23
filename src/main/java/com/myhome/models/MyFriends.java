package com.myhome.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "my_friends")
public class MyFriends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_my_friends")
    private Long idMyFriends;

    @Column(name = "address_user")
    private String addressUser;

    @Column(name = "address_my_friends")
    private String addressMyFriends;

}
