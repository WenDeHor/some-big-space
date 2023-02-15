package com.myhome.repository;

import com.myhome.models.Family;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FamilyRepository extends JpaRepository<Family, Integer> {
    List<Family>findAllByIdUser(int idUser);

}
