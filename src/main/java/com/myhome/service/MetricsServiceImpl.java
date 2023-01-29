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
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

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

//TODO
//    private Long id;
//    private Currency currencyCode; //UAH
//    private String userName; //adminOfMyHome@storyflow.link or Incognito
//    private Long countPerDay; //0 - 1000...
//    private Date date; //2022-07-02 18:39:31.024000
//    private String userAgent; //Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36
//    private String acceptLanguage; //en-US,en;q=0.9,uk;q=0.8
//    private String URLName; ///users/read-competitive-one-composition-index/{id}
//    private String userPrincipal; //UsernamePasswordAuthenticationToken [Principal=com.studio.stories.security.details.UserDetailsImpl@618c5414,
//    // Credentials=[PROTECTED], Authenticated=true, Details=WebAuthenticationDetails [RemoteIpAddress=0:0:0:0:0:0:0:1, SessionId=null], Granted Authorities=[ADMIN]]
//    private String remoteAddr; //0:0:0:0:0:0:0:1

//    private String getPrincipal(HttpServletRequest request) {
//        String principal = "";
//        if (request.getUserPrincipal() == null) {
//            return principal = INCOGNITO;
//        } else {
//            return request.getUserPrincipal().toString();
//        }
//    }

//    public Map<String, String> getRequestHeadersInMap(HttpServletRequest request) {
//        Map<String, String> result = new HashMap<>();
//        Enumeration headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String key = (String) headerNames.nextElement();
//            String value = request.getHeader(key);
//            result.put(key, value);
//        }
//        return result;
//    }
//
//    public String createQuery(String pastDay) {
//        String select = "SELECT DISTINCT ON (user_name) date, url_name, count(user_name) as count_users, count(url_name) as count_of_votes "; //  String select = "SELECT date, url_name, count(distinct user_name) as user_name, count(url_name) as url_name ";
//        String fromTable = "FROM metrics_data ";
//        String where = ""; //  String where = "WHERE date =  DATE_ADD( NOW(), INTERVAL " + pastDay + " DAY) ";
//        String groupBy = "GROUP BY DATE(date) "; //    String groupBy = "GROUP BY url_name ";
//        String orderBy = ""; //  String orderBy = "ORDER BY date ";
//        String limit = "";
//        return select + fromTable + where + groupBy + orderBy + limit;
//    }

//    private Map<String, List<MetricsDTO>> monitoringMetricsIncognito() {
//        List<MetricsData> all = metricsDataRepository.findAll();
//        return metricsDataRepository.findAll().stream()
//                .filter(el -> el.getMarker().equals(Marker.NO_FILTERED.name()))
//                .sorted(Comparator.comparing(MetricsData::getDate).reversed())
//                .map(el -> new MetricsDTO(el.getURLName(), el.getDate(), el.)
//                        .collect(groupingBy(MetricsDTO::getDate, toList()));
//    }
