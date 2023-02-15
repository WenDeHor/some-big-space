package com.myhome.repository;

import com.myhome.models.VideoBoxAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoBoxAdminRepository extends JpaRepository<VideoBoxAdmin, Integer> {
    List<VideoBoxAdmin> findAllById(int userId);
}
