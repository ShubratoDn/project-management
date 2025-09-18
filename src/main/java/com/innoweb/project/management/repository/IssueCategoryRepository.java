package com.innoweb.project.management.repository;

import com.innoweb.project.management.entity.IssueCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IssueCategoryRepository extends JpaRepository<IssueCategory, Long> {
}


