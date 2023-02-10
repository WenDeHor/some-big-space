package com.myhome.repository;


import com.myhome.models.Role;
import com.myhome.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

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
    List<User> findAllByLogin(String login);
    List<User> findAllByLoginLike(String login);
    List<User> findAllByLoginContaining(String login);
    List<User> findAllByLoginContains(String login);
    List<User> findAllByLoginIsContaining(String login);
    List<User> findAllByLoginContainingIgnoreCase(String login);


}
