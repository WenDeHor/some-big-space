package com.myhome.repository;

import com.myhome.models.MetricsData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MetricsDataRepository extends JpaRepository<MetricsData, Long> {
    Optional<MetricsData> findByDateAndId(LocalDate localDate, Long id);
    Optional<MetricsData> findById(Long id);

}
