package com.innoweb.project.management.controller;

import com.innoweb.project.management.repository.IssueCategoryRepository;
import com.innoweb.project.management.repository.StationRepository;
import com.innoweb.project.management.service.TicketAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    private final TicketAnalyticsService analyticsService;
    private final StationRepository stationRepository;
    private final IssueCategoryRepository categoryRepository;

    public DashboardController(TicketAnalyticsService analyticsService,
                               StationRepository stationRepository,
                               IssueCategoryRepository categoryRepository) {
        this.analyticsService = analyticsService;
        this.stationRepository = stationRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public String page(Model model) {
        model.addAttribute("stations", stationRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        return "dashboard/index";
    }

    @GetMapping("/api/overview")
    public ResponseEntity<Map<String, Object>> overview(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(analyticsService.overview(from, to, stationId, categoryId));
    }

    @GetMapping("/api/by-date")
    public ResponseEntity<Map<String, Object>> byDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam String granularity,
            @RequestParam(required = false) Long stationId,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(analyticsService.byDate(from, to, granularity, stationId, categoryId));
    }

    @GetMapping("/api/by-station")
    public ResponseEntity<Map<String, Object>> byStation(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long categoryId) {
        return ResponseEntity.ok(analyticsService.byStation(from, to, categoryId));
    }

    @GetMapping("/api/by-category")
    public ResponseEntity<Map<String, Object>> byCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            @RequestParam(required = false) Long stationId) {
        return ResponseEntity.ok(analyticsService.byCategory(from, to, stationId));
    }
}


