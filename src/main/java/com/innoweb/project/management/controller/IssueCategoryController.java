package com.innoweb.project.management.controller;

import com.innoweb.project.management.entity.IssueCategory;
import com.innoweb.project.management.repository.IssueCategoryRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/categories")
public class IssueCategoryController {

    private final IssueCategoryRepository issueCategoryRepository;

    public IssueCategoryController(IssueCategoryRepository issueCategoryRepository) {
        this.issueCategoryRepository = issueCategoryRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("categories", issueCategoryRepository.findAll());
        model.addAttribute("category", new IssueCategory());
        return "categories/list";
    }

    @PostMapping
    public String create(@ModelAttribute("category") IssueCategory category, BindingResult result) {
        if (result.hasErrors()) {
            return "categories/list";
        }
        issueCategoryRepository.save(category);
        return "redirect:/categories";
    }

    @PostMapping(path = "/ajax")
    @ResponseBody
    public ResponseEntity<?> createAjax(@RequestParam("name") String name,
                                        @RequestParam(value = "description", required = false) String description) {
        IssueCategory category = IssueCategory.builder().name(name).description(description).build();
        IssueCategory savedCategory  = issueCategoryRepository.save(category);
        return ResponseEntity.ok().body(new java.util.HashMap<String, Object>() {{
            put("id", savedCategory.getId());
            put("name", savedCategory.getName());
        }});
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("category", issueCategoryRepository.findById(id).orElseThrow());
        return "categories/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id, @ModelAttribute("category") IssueCategory form, BindingResult result) {
        if (result.hasErrors()) {
            return "categories/form";
        }
        IssueCategory category = issueCategoryRepository.findById(id).orElseThrow();
        category.setName(form.getName());
        category.setDescription(form.getDescription());
        issueCategoryRepository.save(category);
        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        issueCategoryRepository.deleteById(id);
        return "redirect:/categories";
    }
}


