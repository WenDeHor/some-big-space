package com.myhome.repository;

import com.myhome.models.CookBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CookBookRepository extends JpaRepository<CookBook, Long> {
    List<CookBook> findAll();

   List<CookBook> findAllByEmail(String email);

    Optional<CookBook> findById(Long id);
}
