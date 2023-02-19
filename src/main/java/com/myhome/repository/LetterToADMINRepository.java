package com.myhome.repository;


import com.myhome.models.LetterToADMIN;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface LetterToADMINRepository extends JpaRepository<LetterToADMIN, Integer> {
    List<LetterToADMIN>findAllByAddressUser(String userAddress);

}
