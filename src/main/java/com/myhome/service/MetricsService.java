package com.myhome.service;

import com.myhome.models.MetricsDTO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface MetricsService {
    void startMetricsCheck(HttpServletRequest request, String urlName);
    void filterDays();
    List<MetricsDTO> findAllMetricsDTOs();
}
