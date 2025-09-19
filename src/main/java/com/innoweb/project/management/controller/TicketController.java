package com.innoweb.project.management.controller;

import com.innoweb.project.management.entity.Ticket;
import com.innoweb.project.management.entity.TicketStatus;
import com.innoweb.project.management.repository.IssueCategoryRepository;
import com.innoweb.project.management.repository.StationRepository;
import com.innoweb.project.management.repository.TicketRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Objects;

@Controller
@RequestMapping("/tickets")
public class TicketController {

    private final TicketRepository ticketRepository;
    private final StationRepository stationRepository;
    private final IssueCategoryRepository issueCategoryRepository;

    public TicketController(TicketRepository ticketRepository,
                            StationRepository stationRepository,
                            IssueCategoryRepository issueCategoryRepository) {
        this.ticketRepository = ticketRepository;
        this.stationRepository = stationRepository;
        this.issueCategoryRepository = issueCategoryRepository;
    }

    @GetMapping
    public String list(
        @RequestParam(value = "q", required = false) String q,
        @RequestParam(value = "status", required = false) java.util.List<TicketStatus> status,
        @RequestParam(value = "stationId", required = false) java.util.List<Long> stationId,
        @RequestParam(value = "categoryId", required = false) java.util.List<Long> categoryId,
        @RequestParam(value = "orderBy", defaultValue = "raisingDateTime") String orderBy,
        @RequestParam(value = "orderDir", defaultValue = "desc") String orderDir,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        Model model) {

        Specification<Ticket> spec = Specification.where(null);
        if (q != null && !q.isBlank()) {
            String like = "%" + q.trim().toLowerCase() + "%";
            spec = spec.and((root, cq, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like)
            ));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, cq, cb) -> root.get("status").in(status));
        }
        if (stationId != null && !stationId.isEmpty()) {
            spec = spec.and((root, cq, cb) -> root.join("station").get("id").in(stationId));
        }
        if (categoryId != null && !categoryId.isEmpty()) {
            spec = spec.and((root, cq, cb) -> root.join("issueCategory").get("id").in(categoryId));
        }

        Sort.Direction direction = "asc".equalsIgnoreCase(orderDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = (orderBy == null || orderBy.isBlank()) ? "raisingDateTime" : orderBy;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Ticket> ticketPage = (spec == null) ? ticketRepository.findAll(pageable) : ticketRepository.findAll(spec, pageable);

        model.addAttribute("ticketPage", ticketPage);
        model.addAttribute("q", q);
    model.addAttribute("statusFilter", status);
    model.addAttribute("stationId", stationId);
    model.addAttribute("categoryId", categoryId);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("orderDir", orderDir);
        model.addAttribute("size", size);
        model.addAttribute("stations", stationRepository.findAll());
        model.addAttribute("categories", issueCategoryRepository.findAll());
        model.addAttribute("statuses", TicketStatus.values());
        return "tickets/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        Ticket ticket = new Ticket();
        ticket.setRaisingDateTime(LocalDateTime.now());
        ticket.setStatus(TicketStatus.OPEN);
        model.addAttribute("ticket", ticket);
        model.addAttribute("stations", stationRepository.findAll());
        model.addAttribute("categories", issueCategoryRepository.findAll());
        model.addAttribute("statuses", TicketStatus.values());
        return "tickets/form";
    }

    @PostMapping
    public String create(@ModelAttribute("ticket") Ticket ticket, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("stations", stationRepository.findAll());
            model.addAttribute("categories", issueCategoryRepository.findAll());
            model.addAttribute("statuses", TicketStatus.values());
            return "tickets/form";
        }
        if (ticket.getRaisingDateTime() == null) {
            ticket.setRaisingDateTime(LocalDateTime.now());
        }
        ticketRepository.save(ticket);
        return "redirect:/tickets";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        model.addAttribute("ticket", ticket);
        model.addAttribute("stations", stationRepository.findAll());
        model.addAttribute("categories", issueCategoryRepository.findAll());
        model.addAttribute("statuses", TicketStatus.values());
        return "tickets/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("ticket") Ticket form, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("stations", stationRepository.findAll());
            model.addAttribute("categories", issueCategoryRepository.findAll());
            model.addAttribute("statuses", TicketStatus.values());
            return "tickets/form";
        }
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        ticket.setTitle(form.getTitle());
        ticket.setDescription(form.getDescription());
        ticket.setStation(form.getStation());
        ticket.setIssueCategory(form.getIssueCategory());
        ticket.setRaisingDateTime(form.getRaisingDateTime());
        ticket.setStatus(form.getStatus());
        ticket.setReporterName(form.getReporterName());
        ticketRepository.save(ticket);
        return "redirect:/tickets";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        ticketRepository.deleteById(id);
        return "redirect:/tickets";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam("status") TicketStatus status,
                               @RequestParam(value = "redirect", required = false) String redirect) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        ticket.setStatus(status);
        ticketRepository.save(ticket);
        return (redirect != null && !redirect.isBlank()) ? "redirect:" + redirect : "redirect:/tickets";
    }

    @PostMapping(path = "/{id}/status", params = "ajax=true")
    @ResponseBody
    public ResponseEntity<?> updateStatusAjax(@PathVariable Long id, @RequestParam("status") TicketStatus status) {
        Ticket ticket = ticketRepository.findById(id).orElseThrow();
        ticket.setStatus(status);
        ticketRepository.save(ticket);
        return ResponseEntity.ok().body(ticket.getStatus());
    }

    /**
     * AJAX endpoint for ticket table and pagination fragment
     */
    @GetMapping("/fragment")
    public String listFragment(
        @RequestParam(value = "q", required = false) String q,
        @RequestParam(value = "status", required = false) java.util.List<TicketStatus> status,
        @RequestParam(value = "stationId", required = false) java.util.List<Long> stationId,
        @RequestParam(value = "categoryId", required = false) java.util.List<Long> categoryId,
        @RequestParam(value = "orderBy", defaultValue = "raisingDateTime") String orderBy,
        @RequestParam(value = "orderDir", defaultValue = "desc") String orderDir,
        @RequestParam(value = "page", defaultValue = "0") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        Model model) {
        Specification<Ticket> spec = Specification.where(null);
        if (q != null && !q.isBlank()) {
            String like = "%" + q.trim().toLowerCase() + "%";
            spec = spec.and((root, cq, cb) -> cb.or(
                    cb.like(cb.lower(root.get("title")), like),
                    cb.like(cb.lower(root.get("description")), like)
            ));
        }
        if (status != null && !status.isEmpty()) {
            spec = spec.and((root, cq, cb) -> root.get("status").in(status));
        }
        if (stationId != null && !stationId.isEmpty()) {
            spec = spec.and((root, cq, cb) -> root.join("station").get("id").in(stationId));
        }
        if (categoryId != null && !categoryId.isEmpty()) {
            spec = spec.and((root, cq, cb) -> root.join("issueCategory").get("id").in(categoryId));
        }
        Sort.Direction direction = "asc".equalsIgnoreCase(orderDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = (orderBy == null || orderBy.isBlank()) ? "raisingDateTime" : orderBy;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
        Page<Ticket> ticketPage = (spec == null) ? ticketRepository.findAll(pageable) : ticketRepository.findAll(spec, pageable);
        model.addAttribute("ticketPage", ticketPage);
        model.addAttribute("q", q);
        model.addAttribute("statusFilter", status);
        model.addAttribute("stationId", stationId);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("orderBy", orderBy);
        model.addAttribute("orderDir", orderDir);
        model.addAttribute("size", size);
        model.addAttribute("stations", stationRepository.findAll());
        model.addAttribute("categories", issueCategoryRepository.findAll());
        model.addAttribute("statuses", TicketStatus.values());
        return "tickets/list :: ticketTableFragment";
    }
}


