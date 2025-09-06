package com.notesapp.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.notesapp.backend.model.Note;

public interface NoteRepository extends JpaRepository<Note, Long> {
  List<Note> findByOwnerId(Long ownerId);
  Optional<Note> findByShareToken(String token);
  
    // Add this method for public shared notes
  @Query("SELECT n FROM Note n WHERE n.shareToken = :token AND (n.shareExpireAt IS NULL OR n.shareExpireAt > CURRENT_TIMESTAMP)")
  Optional<Note> findValidSharedNote(@Param("token") String token);
}
