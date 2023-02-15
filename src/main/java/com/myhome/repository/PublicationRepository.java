package com.myhome.repository;

import com.myhome.models.PublicationUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface PublicationRepository extends JpaRepository<PublicationUser, Integer> {
    List<PublicationUser> findAllByIdUser(int idUser);

    Optional<PublicationUser> findOneByIdUser(int idUser);


}
