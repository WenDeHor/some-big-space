package com.myhome.repository;

import com.myhome.models.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
	Optional<Image> findByName(String name);
	Optional<Image> findById(Long id);
}
