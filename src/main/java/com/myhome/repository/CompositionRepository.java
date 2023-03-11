package com.myhome.repository;

import com.myhome.models.Composition;
import com.myhome.models.PublicationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.util.List;
import java.util.Optional;
//@EnableJpaRepositories
public interface CompositionRepository extends JpaRepository<Composition, Integer> {

    List<Composition> findAllByIdUser(int idUser);
    Optional<Composition> findAllByIdUserAndId(int idUser, int idComposition);
    Optional<Composition> findOneByIdUserAndId(int idUser, int idComposition);

    List<Composition> findAllByPublication(PublicationType publicationType);
    List<Composition> findAllByPublicationAndIdUserNot(PublicationType publicationType, int idUser);

    Optional<Composition> findOneById(int id);

    List<Composition> findAllByIdIn(List<Integer> userIds);
    List<Composition> findAllByIdUserIn(List<Integer> userIds);
}
