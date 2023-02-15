package com.myhome.repository;

import com.myhome.models.MetricsData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface MetricsDataRepository extends JpaRepository<MetricsData, Integer> {
    Optional<MetricsData> findByDateAndId(Date date, int id);

    Optional<MetricsData> findById(int id);

}
