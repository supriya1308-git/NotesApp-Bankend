package com.notesapp.backend.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "notes")
public class Note {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "owner_id", nullable = false)
  private User owner;

  @Column(length = 255)
  private String title = "";

  @Lob
  private String content;

  private Instant createdAt = Instant.now();
  private Instant updatedAt = Instant.now();

  @Column(length = 36, unique = true)
  private String shareToken;

  private Instant shareExpireAt;

  @PreUpdate
  public void preUpdate() {
    updatedAt = Instant.now();
  }

  // getters & setters
}
