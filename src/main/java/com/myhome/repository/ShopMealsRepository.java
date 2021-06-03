package com.myhome.repository;

import com.myhome.models.CookBook;
import com.myhome.models.ShopMeals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShopMealsRepository extends JpaRepository<ShopMeals, Long> {

    List<ShopMeals> findAllByAddress(String address);
}
