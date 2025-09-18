package com.innoweb.project.management.repository;

import com.innoweb.project.management.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}


