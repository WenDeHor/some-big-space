package com.myhome.repository;

import com.myhome.models.MyFriends;
import com.myhome.models.PublicationUser;
import com.myhome.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface PublicationRepository extends JpaRepository<PublicationUser, Long> {
    Iterable<PublicationUser>findAllByEmail(String emailId);
    Iterable<PublicationUser> findAllByAddress(String address);
    User findOneByEmail(String userId);


}
