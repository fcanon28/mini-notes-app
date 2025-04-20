package com.practice.mini_notes.repository;

import com.practice.mini_notes.model.Note;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotesRepo extends MongoRepository<Note, String> {
//    this was for Get by Title
//    @Query("{'title': { $regex: ?0, $options: 'i' }}")
//    Optional<Note> findNoteByTitle(String title);

//    Page<Note> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    //Find all notes that are NOT soft-deleted
    Page<Note> findByIsDeletedFalse(Pageable pageable);

    // Search by title and also exclude soft-deleted notes
    Page<Note> findByTitleContainingIgnoreCaseAndIsDeletedFalse(String title, Pageable pageable);

    //Get by ID but only if not deleted
    Optional<Note> findByIdAndIsDeletedFalse(String id);


}