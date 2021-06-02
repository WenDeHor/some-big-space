package com.myhome.repository;

import com.myhome.models.CookBook;
import com.myhome.models.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long> {
//    Optional<Menu> findAllByEmail(String address);
    List<Menu> findAllByAddress(String address);
}
