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
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Long idAddress;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "number")
    int number;

    @Column(name = "settlement")
    String settlement;

    @Column(name = "currency_code")
    String currencyCode; //settlement+number

    @Column(name = "user_id")
       private Long idUser;

}
