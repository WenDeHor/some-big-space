package com.myhome.repository;

import com.myhome.models.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
@Repository
public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
    Optional<UserPhoto> findOneByAddress(String address);
    void deleteByAddress(String address);
}
