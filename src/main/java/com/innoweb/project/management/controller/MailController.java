package com.innoweb.project.management.controller;

import com.innoweb.project.management.service.OutlookMailService;
import com.microsoft.graph.models.Message;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/emails")
public class MailController {

    private final OutlookMailService outlookMailService;

    public MailController(OutlookMailService outlookMailService) {
        this.outlookMailService = outlookMailService;
    }

    @GetMapping
    public String list(Model model) {
        List<Message> messages = outlookMailService.listInboxTop(25);
        model.addAttribute("messages", messages);
        return "emails/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable("id") String id, Model model) {
        Message message = outlookMailService.getMessage(id);
        model.addAttribute("message", message);
        return "emails/detail";
    }
}


