package com.innoweb.project.management.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id")
    private Station station;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_category_id")
    private IssueCategory issueCategory;

    @Column(name = "raising_date_time", nullable = false)
    private LocalDateTime raisingDateTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TicketStatus status;

    @Column(name = "reporter_name", length = 100)
    private String reporterName;
}
