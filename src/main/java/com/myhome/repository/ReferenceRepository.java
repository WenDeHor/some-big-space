package com.myhome.repository;

import com.myhome.models.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Integer> {
    List<Reference> findAllByIdUser(int idUser);
    Optional<Reference> findByIdUserAndId(int idUser, int idDiary);
}
