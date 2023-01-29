package com.myhome.repository;

import com.myhome.models.Evaluate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluateRepository extends JpaRepository<Evaluate, Long> {

    Optional<Evaluate> findByEmailAppraiserAndIdComposition(String emailAppraiser, Long idComposition);
    List<Evaluate>findAllByIdComposition(Long IdComposition);
}
