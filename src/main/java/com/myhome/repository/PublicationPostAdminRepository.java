package com.myhome.repository;

import com.myhome.models.PublicationPostAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PublicationPostAdminRepository extends JpaRepository<PublicationPostAdmin, Long> {
    Optional<PublicationPostAdmin> findByIdPublication(Long IdPublication);

}
