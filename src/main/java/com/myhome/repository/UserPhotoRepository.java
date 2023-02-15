package com.myhome.repository;

import com.myhome.models.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Integer> {
    Optional<UserPhoto> findOneByIdUser(int idUser);
    Optional<UserPhoto> findOneByAddress(String address);
    void deleteByIdUser(int idUser);
}
