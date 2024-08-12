package com.library.controllers;

import com.library.model.request.AddBookDto;
import com.library.model.response.AppResponse;
import com.library.services.LibraryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/books")
public class LibraryController {
    private final LibraryService bookService;

    @PostMapping("/add")
    public AppResponse addBook(@RequestBody AddBookDto bookDto, @RequestParam String username) {
        return bookService.addBook(bookDto, username);
    }

    @PutMapping("/update/{bookId}")
    public AppResponse updateBook(@RequestBody AddBookDto bookDto, @RequestParam String username, @PathVariable Long bookId) {
        return bookService.updateBook(bookDto, username, bookId);
    }

    @PostMapping("/borrow")
    public AppResponse borrowBook(@RequestParam String username, @RequestParam Long bookId, @RequestParam String librarianUsername) {
        return bookService.borrowBook(username, bookId, librarianUsername);
    }

    @PostMapping("/return")
    public AppResponse returnBook(@RequestParam String username, @RequestParam Long bookId) {
        return bookService.returnBook(username, bookId);
    }

    @GetMapping("/borrowed/paginated")
    public AppResponse getPaginatedBorrowedBooks(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy) {
        return bookService.getPaginatedBorrowedBooks(page, size, sortBy);
    }

    @GetMapping("/paginated")
    public AppResponse getPaginatedBooks(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy) {
        return bookService.getPaginatedBooks(page, size, sortBy);
    }
}
