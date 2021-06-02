package com.myhome.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "menu")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "breakfast")
    @Size(min = 0, max = 1000)
    private String breakfast;

    @Column(name = "dinner")
    @Size(min = 0, max = 1000)
    private String dinner;

    @Column(name = "supper")
    @Size(min = 0, max = 1000)
    private String supper;

    @Column(name = "address")
    private String address;
//
//    @Column(name = "email")
//    private String email;
}
