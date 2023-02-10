package com.myhome.repository;

import com.myhome.models.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Long> {
    List<Friends>findAllByIdUser(Long id);

}
