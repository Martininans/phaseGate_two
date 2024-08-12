package com.library.services;

import com.library.model.request.AddBookDto;
import com.library.model.response.AppResponse;

public interface LibraryService {
    AppResponse addBook(AddBookDto bookDto, String username);
    AppResponse updateBook(AddBookDto bookDto, String username, Long bookId);
    AppResponse borrowBook(String username, Long bookId, String librarianUsername);
    AppResponse returnBook(String username, Long bookId);
    AppResponse getPaginatedBorrowedBooks(int page, int size, String sortBy);
    AppResponse getPaginatedBooks(int page, int size, String sortBy);

}
