package com.myhome.repository;

import com.myhome.models.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Integer> {

    List<Menu> findAllByIdUser(int idUser);

    Optional<Menu> findByIdAndIdUser(int idMenu, int idUser);
}
