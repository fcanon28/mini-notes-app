package com.practice.mini_notes.controller;

import com.practice.mini_notes.dto.NoteRequest;
import com.practice.mini_notes.model.Note;
import com.practice.mini_notes.repository.NotesRepo;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

@RestController
public class NotesController {
    @Autowired
    NotesRepo notesRepo;

    //Create
    @PostMapping("/note")
    public ResponseEntity<?> addNote(@Valid @RequestBody NoteRequest noteRequest) {
        Note note = new Note();
        note.setTitle(noteRequest.getTitle());
        note.setContent(noteRequest.getContent());
        Note saved = notesRepo.save(note);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); //201
    }

//    //Get all
//    @GetMapping("/notes")
//    public ResponseEntity<List<Note>> getAllNotes() {
//        List<Note> notes = notesRepo.findAll();
//        return ResponseEntity.ok(notes); // 200 OK
//    }

    //Get all but with search param
    @GetMapping("/notes")
    public ResponseEntity<?> getNotes(@RequestParam(required = false) String search,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(defaultValue = "desc") String sort) {
        Sort sortDirection = sort.equalsIgnoreCase("asc") ? Sort.by("createdAt").ascending() : Sort.by("createdAt").descending();
        Pageable pageable = PageRequest.of(page, size, sortDirection);
        Page<Note> resultPage;
//        List<Note> notes;
        if(search != null && !search.isBlank()) {
            resultPage = notesRepo.findByTitleContainingIgnoreCaseAndIsDeletedFalse(search, pageable);
        } else {
            resultPage = notesRepo.findByIsDeletedFalse(pageable);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("notes", resultPage.getContent());
        response.put("currentPage", resultPage.getNumber());
        response.put("totalItems", resultPage.getTotalElements());
        response.put("totalPages", resultPage.getTotalPages());
        response.put("hasNext", resultPage.hasNext());
        response.put("hasPrevious", resultPage.hasPrevious());

        return ResponseEntity.ok(response);
    }

    //Get by ID
    @GetMapping("/note/{id}")
    public ResponseEntity<?> getNoteById(@PathVariable String id) {
        Optional<Note> noteOpt = notesRepo.findById(id);
        if (noteOpt.isPresent()) {
            return ResponseEntity.ok(noteOpt.get()); //200
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note with ID " +id+ " not found"); //404
        }
    }

//    //Get by Title - does not work when multiple records have same words
//    @GetMapping("/note/title/{title}")
//    public ResponseEntity<?> getNoteByTitle(@PathVariable String title) {
//        Optional<Note> note = notesRepo.findNoteByTitle(title);
//        if (note.isPresent()) {
//            return ResponseEntity.ok(note.get());
//        } else {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note with title " + title +" not found"); //404
//        }
//    }

    //Update a note by ID
    @PutMapping("/note/{id}")
    public ResponseEntity<?> updateNote(@PathVariable String id, @Valid @RequestBody NoteRequest updatedNote) {
        //find note by ID
        Optional<Note> noteOpt = notesRepo.findById(id);
        // check if present
        if (noteOpt.isPresent()) {
            Note noteToUpdate = noteOpt.get(); //unwrap the Optional first
            //update fields
            noteToUpdate.setTitle(updatedNote.getTitle());
            noteToUpdate.setContent(updatedNote.getContent());
            noteToUpdate.setUpdatedAt(LocalDateTime.now());

            notesRepo.save(noteToUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(noteToUpdate); //200
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note with ID " + id + " not found"); //404
        }
    }

    //Delete a note by ID
    @DeleteMapping("/note/{id}")
    public ResponseEntity<?> softDeleteNote(@PathVariable String id) {
        Optional<Note> note = notesRepo.findById(id);
        if(note.isPresent()) {
            Note n = note.get();
            n.setDeleted(true);
            notesRepo.save(n);
//            notesRepo.deleteById(id);
            return ResponseEntity.ok("Note soft-deleted.");
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Note not found"); //404
    }

    //Post multiple notes
    @PostMapping("/notes")
    public ResponseEntity<?> addNotes(@Valid @RequestBody List<@Valid NoteRequest> notes) {
        List<Note> noteEntities = notes.stream().map(dto -> {
            Note note = new Note();
            note.setTitle(dto.getTitle());
            note.setContent(dto.getContent());
            return note;
        }).toList();
        List<Note> saved = notesRepo.saveAll(noteEntities);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved); //201
    }

}
