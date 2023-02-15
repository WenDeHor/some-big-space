package com.myhome.repository;


import com.myhome.models.Role;
import com.myhome.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findOneByEmail(String email);
    Optional<User> findOneByRole(Role role);
    Optional<User> findAllByAddress(String addressUser);
    Optional<User> findOneById(int id);
    Optional<User> findOneByLogin(String login);
    Optional<User> findAllByEmail(String email);
    List<User> findAllByLogin(String login);
    List<User> findAllByLoginLike(String login);
    List<User> findAllByLoginContaining(String login);
    List<User> findAllByLoginContains(String login);
    List<User> findAllByLoginIsContaining(String login);
    List<User> findAllByLoginContainingIgnoreCase(String login);


}
