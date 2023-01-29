package com.myhome.repository;

import com.myhome.models.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Long> {
    List<Comments> findAllByIdComposition(Long idComposition);
}
