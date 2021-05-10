package com.myhome.repository;

import com.myhome.models.Letter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LetterRepository extends JpaRepository<Letter, Long> {
    Iterable<Letter> findAllByRecipientAddress(String email);

    Iterable<Letter> findAllBySenderAddress(String email);
}
