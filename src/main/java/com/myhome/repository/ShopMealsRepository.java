package com.myhome.repository;

import com.myhome.models.ShopMeals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ShopMealsRepository extends JpaRepository<ShopMeals, Integer> {

    List<ShopMeals> findAllByIdUser(int idUser);

    Optional<ShopMeals> findByIdAndIdUser(int idShopMeals, int idUser);
}
