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
@Table(name = "admin_address")
public class AdminAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_address_id")
    private Long idAdminAddress;

    @Column(name = "settlement")
    String settlement;

    @Column(name = "number_limit")
    String numberLimit;



}
