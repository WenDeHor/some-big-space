package com.myhome.repository;

import com.myhome.models.CookBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface CookBookRepository extends JpaRepository<CookBook, Long> {
    List<CookBook> findAll();

   List<CookBook> findAllByEmail(String email);

    Optional<CookBook> findById(Long id);
}
