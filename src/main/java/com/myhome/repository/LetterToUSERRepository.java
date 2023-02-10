package com.myhome.repository;

import com.myhome.models.LetterToUSER;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LetterToUSERRepository extends JpaRepository<LetterToUSER, Long> {
    List<LetterToUSER> findAllByRecipientAddress(String email);

    List<LetterToUSER> findAllBySenderAddress(String email);
}
