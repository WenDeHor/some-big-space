package com.myhome.repository;

import com.myhome.models.VideoBox;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface VideoBoxRepository extends JpaRepository<VideoBox, Integer> {
    List<VideoBox> findAllByIdUser(int idUser);
    Optional<VideoBox> findByIdAndIdUser(int idVideoBox, int idUser);
}
