package com.myhome.repository;

import com.myhome.models.Evaluate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluateRepository extends JpaRepository<Evaluate, Integer> {

    Optional<Evaluate> findByIdAppraiserAndIdComposition(int idAppraiser, int idComposition);

    List<Evaluate> findAllByIdComposition(int IdComposition);
}
