package com.myhome.repository;

import com.myhome.models.PublicationUser;
import com.myhome.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PublicationRepository extends JpaRepository<PublicationUser, Long> {
    Iterable<PublicationUser>findAllByEmail(String emailId);
    User findOneByEmail(String userId);


}
