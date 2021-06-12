package com.myhome.repository;

import com.myhome.models.Reference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReferenceRepository extends JpaRepository<Reference, Long> {
    List<Reference> findAllByAddress(String address);
}
