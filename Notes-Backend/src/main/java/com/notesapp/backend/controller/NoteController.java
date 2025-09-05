package com.notesapp.backend.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.notesapp.backend.model.Note;
import com.notesapp.backend.model.User;
import com.notesapp.backend.repository.NoteRepository;
import com.notesapp.backend.repository.UserRepository;

@RestController
@RequestMapping("/api/notes")
public class NoteController {

  @Autowired NoteRepository noteRepo;
  @Autowired UserRepository userRepo;

  private User getCurrentUser(UserDetails principal) {
    return userRepo.findByUsername(principal.getUsername()).orElseThrow();
  }

  @GetMapping
  public List<Map<String,Object>> list(@AuthenticationPrincipal UserDetails principal) {
    User u = getCurrentUser(principal);
    List<Note> notes = noteRepo.findByOwnerId(u.getId());
    List<Map<String,Object>> out = new ArrayList<>();
    for (Note n : notes) {
      out.add(Map.of(
        "id", n.getId(),
        "title", n.getTitle(),
        "content", n.getContent(),
        "createdAt", n.getCreatedAt(),
        "updatedAt", n.getUpdatedAt(),
        "shared", n.getShareToken() != null
      ));
    }
    return out;
  }

  @PostMapping
  public Note create(@AuthenticationPrincipal UserDetails principal, @RequestBody Map<String,String> body) {
    User u = getCurrentUser(principal);
    Note n = new Note();
    n.setOwner(u);
    n.setTitle(Optional.ofNullable(body.get("title")).orElse(""));
    n.setContent(Optional.ofNullable(body.get("content")).orElse(""));
    return noteRepo.save(n);
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> get(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
    User u = getCurrentUser(principal);
    Note n = noteRepo.findById(id).orElseThrow();
    if (!n.getOwner().getId().equals(u.getId())) return ResponseEntity.status(403).build();
    return ResponseEntity.ok(n);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> update(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id, @RequestBody Map<String,String> body, @RequestHeader(value="If-Unmodified-Since", required=false) String ifUnmodifiedSince) {
    User u = getCurrentUser(principal);
    Note n = noteRepo.findById(id).orElseThrow();
    if (!n.getOwner().getId().equals(u.getId())) return ResponseEntity.status(403).build();

    // optional optimistic concurrency
    if (ifUnmodifiedSince != null) {
      Instant clientTs = Instant.parse(ifUnmodifiedSince);
      if (n.getUpdatedAt().isAfter(clientTs)) {
        return ResponseEntity.status(409).body(Map.of("message","Conflict: note changed"));
      }
    }

    n.setTitle(Optional.ofNullable(body.get("title")).orElse(n.getTitle()));
    n.setContent(Optional.ofNullable(body.get("content")).orElse(n.getContent()));
    n.setUpdatedAt(Instant.now());
    noteRepo.save(n);
    return ResponseEntity.ok(n);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id) {
    User u = getCurrentUser(principal);
    Note n = noteRepo.findById(id).orElseThrow();
    if (!n.getOwner().getId().equals(u.getId())) return ResponseEntity.status(403).build();
    noteRepo.delete(n);
    return ResponseEntity.ok(Map.of("message","deleted"));
  }

  @PostMapping("/share/{id}")
  public ResponseEntity<?> share(@AuthenticationPrincipal UserDetails principal, @PathVariable Long id, @RequestBody(required=false) Map<String,String> body) {
    System.err.println("NoteController.share()");
	  
	  User u = getCurrentUser(principal);
    Note n = noteRepo.findById(id).orElseThrow();
    if (!n.getOwner().getId().equals(u.getId())) return ResponseEntity.status(403).build();
    String token = UUID.randomUUID().toString();
    n.setShareToken(token);
    if (body != null && body.containsKey("expireAt")) {
      n.setShareExpireAt(Instant.parse(body.get("expireAt")));
    } else {
      n.setShareExpireAt(null);
    }
    noteRepo.save(n);
    String frontendBase = "http://localhost:3000"; // Update this for production
    return ResponseEntity.ok(Map.of("token", token, "shareUrl", frontendBase + "/s/" + token));
  }
  
  @GetMapping("/share/public/{token}")
  public ResponseEntity<?> getPublicSharedNote(@PathVariable String token) {
      System.err.println("NoteController.getPublicSharedNote() with token: " + token);
      
      Optional<Note> noteOptional = noteRepo.findValidSharedNote(token);
      
      if (noteOptional.isEmpty()) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
              .body(Map.of("error", "Note not found or link has expired"));
      }
      
      Note note = noteOptional.get();
      return ResponseEntity.ok(Map.of(
          "id", note.getId(),
          "title", note.getTitle(),
          "content", note.getContent(),
          "createdAt", note.getCreatedAt(),
          "owner", Map.of("username", note.getOwner().getUsername())
      ));
  }
  
  private User getCurrentUser1(UserDetails principal) {
      return userRepo.findByUsername(principal.getUsername())
          .orElseThrow(() -> new RuntimeException("User not found"));
  }
  
}

