package com.myhome.repository;

import com.myhome.models.MyFriends;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MyFriendsRepository extends JpaRepository<MyFriends, Long> {

    Iterable<MyFriends>findAllByAddressUser(String addressUser);
    Iterable<MyFriends>findAllByAddressMyFriends(String addressMyFriends);
   Optional<MyFriends> findAllByAddressUserAndAddressMyFriends(String userAddress, String myFriendsAddress);
}
