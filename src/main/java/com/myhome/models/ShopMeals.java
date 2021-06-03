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
@Table(name = "shop_meals")
public class ShopMeals {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "full_text")
    @Size(min = 0, max = 3000)
    private String fullText;

    @Column(name = "address")
    private String address;
}
