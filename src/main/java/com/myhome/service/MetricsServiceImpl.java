package com.myhome.service;

import com.myhome.forms.OneElement;
import com.myhome.models.Marker;
import com.myhome.models.MetricsDTO;
import com.myhome.models.MetricsData;
import com.myhome.repository.MetricsDTORepository;
import com.myhome.repository.MetricsDataRepository;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class MetricsServiceImpl implements MetricsService {
    private final MetricsDataRepository metricsDataRepository;
    private final MetricsDTORepository metricsDTORepository;

    private final long PERIOD_METRICS = 30L;
    private static final String INCOGNITO = "Incognito";

    public MetricsServiceImpl(MetricsDataRepository metricsDataRepository, MetricsDTORepository metricsDTORepository) {
        this.metricsDataRepository = metricsDataRepository;
        this.metricsDTORepository = metricsDTORepository;
    }

    @Override
    public void startMetricsCheck(HttpServletRequest request, String urlName) {
        MetricsData metricsData = new MetricsData();
        metricsData.setCurrencyCode(Currency.getInstance(Locale.getDefault()));
        metricsData.setUserName(getUserName(request));
        metricsData.setDate(LocalDate.now());
        metricsData.setAcceptLanguage(request.getHeader("accept-language"));
        metricsData.setURLName(urlName);
        metricsData.setRemoteAddr(getRemoteAddr(request));
        metricsData.setMarker(Marker.NO_FILTERED.name());
        metricsDataRepository.save(metricsData);
    }

    @Override
    public List<MetricsDTO> findAllMetricsDTOs() {
        return metricsDTORepository.findAll();
    }

    @Override
    public void filterDays() {
        LocalDate localDate = LocalDate.now();
        LocalDate startDay = localDate.minusDays(PERIOD_METRICS);
        List<MetricsData> listNoFiltered = getListNoFiltered();
        for (int i = 0; i < PERIOD_METRICS; i++) {
            LocalDate incrementDay = startDay.plusDays(i);
            long countUsers = countUsersPerDay(incrementDay, listNoFiltered);
            if (countUsers != 0L) {
                MetricsDTO metricsDTO = new MetricsDTO();
                metricsDTO.setDate(incrementDay);
                metricsDTO.setCountUsers(countUsers);
                metricsDTO.setCountOfVotes(countURLsPerDay(incrementDay, listNoFiltered));
                Optional<MetricsDTO> byDate = metricsDTORepository.findByDate(incrementDay);
                if (!byDate.isPresent()) {
                    saveMetricsDTOByDay(metricsDTO);
                }
                updateMetricsData(incrementDay);
            }
        }
    }

    private void saveMetricsDTOByDay(MetricsDTO metricsDTO) {
        metricsDTORepository.save(metricsDTO);
    }

    private void updateMetricsData(LocalDate incrementDay) {
        List<MetricsData> metricsPerDay = getListNoFiltered().stream()
                .filter(d -> d.getDate().equals(incrementDay))
                .collect(toList());
        for (MetricsData value : metricsPerDay) {
            Optional<MetricsData> metricsData = metricsDataRepository.findById(value.getId());
            if (metricsData.isPresent()) {
                MetricsData data = metricsData.get();
                data.setMarker(Marker.FILTERED.name());
                metricsDataRepository.save(data);
            }
        }
    }

    private long countUsersPerDay(LocalDate day, List<MetricsData> listNoFiltered) {
        return listNoFiltered.stream()
                .filter(d -> d.getDate().equals(day))
                .map(cr -> new OneElement(cr.getUserName()))
                .distinct()
                .count();
    }

    private long countURLsPerDay(LocalDate day, List<MetricsData> listNoFiltered) {
        return listNoFiltered.stream()
                .filter(d -> d.getDate().equals(day))
                .map(cr -> new OneElement(cr.getURLName()))
                .count();
    }

    private List<MetricsData> getListNoFiltered() {
        return metricsDataRepository.findAll().stream()
                .filter(el -> el.getMarker().equals(Marker.NO_FILTERED.name()))
                .collect(toList());
    }

    private String getUserName(HttpServletRequest request) {
        if (request.getRemoteUser() == null) {
            return INCOGNITO;
        } else {
            return request.getRemoteUser();
        }
    }

    private String getRemoteAddr(HttpServletRequest request) {
        String remoteAddr = "";
        remoteAddr = request.getHeader("X-FORWARDED-FOR");
        if (remoteAddr == null || "".equals(remoteAddr)) {
            remoteAddr = request.getRemoteAddr();
        }
        return remoteAddr;
    }
}
