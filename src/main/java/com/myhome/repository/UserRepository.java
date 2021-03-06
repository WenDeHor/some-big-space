package com.myhome.repository;


import com.myhome.models.Role;
import com.myhome.models.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
//    LinkedList<User>findLastByCounter();
    Integer findOneByCounter(int count);
    Optional<User> findOneByEmail(String email);
    Optional<User> findOneByRole(Role role);
    Optional<User> findAllByAddress(String addressUser);
    Optional<User> findOneByIdUser(Long email);
    Optional<User> findOneByLogin(String login);
    Optional<User> findAllByEmail(String email);
    Optional<User> findAllByLogin(String login);


}
