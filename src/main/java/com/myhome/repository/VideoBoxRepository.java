package com.myhome.repository;

import com.myhome.models.VideoBox;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoBoxRepository extends JpaRepository<VideoBox, Integer> {
    Iterable<VideoBox> findAllByIdUser(int idUser);
}
