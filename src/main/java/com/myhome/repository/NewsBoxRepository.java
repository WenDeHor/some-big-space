package com.myhome.repository;

import com.myhome.models.NewsBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NewsBoxRepository extends JpaRepository<NewsBox, Integer> {
    Iterable<NewsBox>findAllByIdUser(int idUser);
}
