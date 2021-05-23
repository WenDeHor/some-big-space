package com.myhome.repository;

import com.myhome.models.FileDB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileDBRepository extends JpaRepository<FileDB, String> {

//    Iterable<FileDB> findAllByAddress(String email);

}
