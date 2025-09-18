package com.innoweb.project.management.controller;

import com.innoweb.project.management.entity.Station;
import com.innoweb.project.management.repository.StationRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/stations")
public class StationController {

    private final StationRepository stationRepository;

    public StationController(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("stations", stationRepository.findAll());
        model.addAttribute("station", new Station());
        return "stations/list";
    }

    @PostMapping
    public String create(@ModelAttribute("station") Station station, BindingResult result) {
        if (result.hasErrors()) {
            return "stations/list";
        }
        stationRepository.save(station);
        return "redirect:/stations";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("station", stationRepository.findById(id).orElseThrow());
        return "stations/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("station") Station form, BindingResult result) {
        if (result.hasErrors()) {
            return "stations/form";
        }
        Station station = stationRepository.findById(id).orElseThrow();
        station.setName(form.getName());
        station.setLocation(form.getLocation());
        stationRepository.save(station);
        return "redirect:/stations";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        stationRepository.deleteById(id);
        return "redirect:/stations";
    }
}


