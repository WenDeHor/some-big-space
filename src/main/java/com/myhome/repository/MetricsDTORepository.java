package com.myhome.repository;

import com.myhome.models.MetricsDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface MetricsDTORepository extends JpaRepository<MetricsDTO, Long> {
    Optional<MetricsDTO> findByDate(LocalDate localDate);

}
