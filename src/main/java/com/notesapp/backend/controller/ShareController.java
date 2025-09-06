package com.notesapp.backend.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notesapp.backend.repository.NoteRepository;

@RestController
@RequestMapping("/s")
public class ShareController {
  @Autowired NoteRepository noteRepo;

  @GetMapping("/{token}")
  public Object viewShared(@PathVariable String token) {
    var opt = noteRepo.findByShareToken(token);
    if (opt.isEmpty()) return Map.of("message","not found");
    var n = opt.get();
    if (n.getShareExpireAt() != null && n.getShareExpireAt().isBefore(Instant.now())) {
      return Map.of("message","expired");
    }
    return Map.of(
      "id", n.getId(),
      "title", n.getTitle(),
      "content", n.getContent(),
      "createdAt", n.getCreatedAt(),
      "owner", n.getOwner().getUsername()
    );
  }
}
