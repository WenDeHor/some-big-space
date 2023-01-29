package com.myhome.repository;

import com.myhome.models.Composition;
import com.myhome.models.PublicationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompositionRepository  extends JpaRepository<Composition, Long> {
    List<Composition> findAllByEmail(String email);
    List<Composition> findAllByPublicationType(PublicationType publicationType);
    Optional<Composition> findOneById(Long id);
}
