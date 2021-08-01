package com.myhome.repository;

import com.myhome.models.VideoBoxAdmin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface VideoBoxAdminRepository extends JpaRepository<VideoBoxAdmin, Long> {
    Iterable<VideoBoxAdmin>findAllByAddressAdmin(String addressAdmin);
    Optional<VideoBoxAdmin> findByIdVideoBox(Long IdVideoBox);

    Iterable<VideoBoxAdmin> findFirstByDate(Date date);
}
