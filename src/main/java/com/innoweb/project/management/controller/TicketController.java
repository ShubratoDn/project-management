package com.innoweb.project.management.controller;

import com.innoweb.project.management.entity.Ticket;
import com.innoweb.project.management.entity.TicketStatus;
import com.innoweb.project.management.repository.IssueCategoryRepository;
import com.innoweb.project.management.repository.StationRepository;
import com.innoweb.project.management.repository.TicketRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
    public String list(Model model) {
        model.addAttribute("tickets", ticketRepository.findAll());
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
}


