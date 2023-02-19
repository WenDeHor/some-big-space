package com.myhome.repository;

import com.myhome.models.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    List<Diary> findAllByIdUser(int idUser);

    Optional<Diary> findByIdUserAndId(int idUser, int idDiary);
}
