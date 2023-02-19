package com.myhome.repository;

import com.myhome.models.Comments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentsRepository extends JpaRepository<Comments, Integer> {
    List<Comments> findAllById(int id);
    List<Comments> findAllByIdComposition(int idComposition);
}
