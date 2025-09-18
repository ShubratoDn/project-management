package com.innoweb.project.management.service;

import com.innoweb.project.management.repository.TicketRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TicketAnalyticsService {

    private final JdbcTemplate jdbcTemplate;

    public TicketAnalyticsService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<String, Object> overview(LocalDate from, LocalDate to, Long stationId, Long categoryId) {
        StringBuilder baseSql = new StringBuilder(
                "from tickets t where t.raising_date_time between ? and ?"
        );
        List<Object> params = new java.util.ArrayList<>();
        params.add(from.atStartOfDay());
        params.add(to.atTime(LocalTime.MAX));

        if (stationId != null) {
            baseSql.append(" and t.station_id = ?");
            params.add(stationId);
        }
        if (categoryId != null) {
            baseSql.append(" and t.issue_category_id = ?");
            params.add(categoryId);
        }

        // Total count
        String totalSql = "select count(*) " + baseSql.toString();
        Long total = jdbcTemplate.queryForObject(totalSql, params.toArray(), Long.class);

        // Status-wise counts
        String statusSql = "select t.status, count(*) as cnt " + baseSql.toString() + " group by t.status";
        List<Map<String, Object>> statusRows = jdbcTemplate.queryForList(statusSql, params.toArray());
        Map<String, Long> statusCounts = new HashMap<>();
        for (Map<String, Object> row : statusRows) {
            String status = String.valueOf(row.get("status"));
            Long cnt = ((Number)row.get("cnt")).longValue();
            statusCounts.put(status, cnt);
        }

        Map<String, Object> m = new HashMap<>();
        m.put("total", total);
        m.put("statusCounts", statusCounts);
        return m;
    }


    public Map<String, Object> byDate(LocalDate from, LocalDate to, String granularity, Long stationId, Long categoryId) {
        String dateExpr;
        switch (granularity) {
            case "week":  dateExpr = "yearweek(t.raising_date_time, 3)"; break;
            case "month": dateExpr = "date_format(t.raising_date_time, '%Y-%m')"; break;
            case "year":  dateExpr = "year(t.raising_date_time)"; break;
            default:      dateExpr = "date(t.raising_date_time)";
        }

        StringBuilder sql = new StringBuilder(
                "select " + dateExpr + " as bucket, count(*) as cnt from tickets t " +
                        "where t.raising_date_time between ? and ?"
        );

        List<Object> params = new java.util.ArrayList<>();
        params.add(from.atStartOfDay());
        params.add(to.atTime(LocalTime.MAX));

        if (stationId != null) {
            sql.append(" and t.station_id = ?");
            params.add(stationId);
        }
        if (categoryId != null) {
            sql.append(" and t.issue_category_id = ?");
            params.add(categoryId);
        }

        sql.append(" group by bucket order by bucket");

        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        Map<String, Object> res = new HashMap<>();
        res.put("rows", rows);
        return res;
    }


    public Map<String, Object> byStation(LocalDate from, LocalDate to, Long categoryId) {
        StringBuilder sql = new StringBuilder("select s.name as label, count(*) as cnt from tickets t join stations s on s.id=t.station_id where t.raising_date_time between ? and ?");
        if (categoryId != null) {
            sql.append(" and t.issue_category_id = ?");
        }
        sql.append(" group by s.name order by cnt desc");
        List<Object> params = new java.util.ArrayList<>();
        params.add(from.atStartOfDay());
        params.add(to.atTime(LocalTime.MAX));
        if (categoryId != null) params.add(categoryId);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        Map<String, Object> res = new HashMap<>();
        res.put("rows", rows);
        return res;
    }

    public Map<String, Object> byCategory(LocalDate from, LocalDate to, Long stationId) {
        StringBuilder sql = new StringBuilder("select c.name as label, count(*) as cnt from tickets t join issue_categories c on c.id=t.issue_category_id where t.raising_date_time between ? and ?");
        if (stationId != null) {
            sql.append(" and t.station_id = ?");
        }
        sql.append(" group by c.name order by cnt desc");
        List<Object> params = new java.util.ArrayList<>();
        params.add(from.atStartOfDay());
        params.add(to.atTime(LocalTime.MAX));
        if (stationId != null) params.add(stationId);
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        Map<String, Object> res = new HashMap<>();
        res.put("rows", rows);
        return res;
    }

    static class Where {
        Where(StringBuilder sql, Long stationId, Long categoryId) {
            if (stationId != null) sql.append(" and t.station_id = ?");
            if (categoryId != null) sql.append(" and t.issue_category_id = ?");
        }
    }
}


