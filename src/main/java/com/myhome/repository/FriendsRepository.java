package com.myhome.repository;

import com.myhome.models.Friends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FriendsRepository extends JpaRepository<Friends, Integer> {
    List<Friends> findAllByIdUser(int id);

    Optional<Friends> findByIdFriendAndIdUser(int idFriends, int idUser);

}
