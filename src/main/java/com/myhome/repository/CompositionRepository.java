package com.myhome.repository;

import com.myhome.models.Composition;
import com.myhome.models.PublicationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompositionRepository extends JpaRepository<Composition, Integer> {

    List<Composition> findAllByIdUser(int idUser);

    List<Composition> findAllByPublicationType(PublicationType publicationType);

    Optional<Composition> findOneById(int id);

    List<Composition> findAllByIdIn(List<Integer> userIds);
}
