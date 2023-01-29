package com.myhome.repository;

import com.myhome.models.LetterToUSER;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LetterToUSERRepository extends JpaRepository<LetterToUSER, Long> {
    Iterable<LetterToUSER> findAllByRecipientAddress(String email);

    Iterable<LetterToUSER> findAllBySenderAddress(String email);
}
