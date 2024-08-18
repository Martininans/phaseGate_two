package com.library.services;

import com.library.enums.UserType;
import com.library.model.entities.Book;
import com.library.model.entities.BorrowedBooks;
import com.library.model.entities.User;
import com.library.model.request.AddBookDto;
import com.library.model.response.AppResponse;
import com.library.repositories.BookRepository;
import com.library.repositories.BorrowedBookRepository;
import com.library.repositories.UserRepository;
import com.library.util.PaginatedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LibraryServiceImpl implements LibraryService {

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BorrowedBookRepository borrowedBookRepository;

    @Override
    public AppResponse addBook(AddBookDto bookDto, String username) {
        if (!hasPermissionToAddOrUpdateBook(username)) {
            return new AppResponse("User not authorized to add books", HttpStatus.FORBIDDEN.toString(), null);
        }

        try {
            Book book = new Book();
            book.setTitle(bookDto.getTitle());
            book.setAuthor(bookDto.getAuthor());
            book.setIsbn(bookDto.getIsbn());
            book.setDescription(bookDto.getDescription());
            book.setCategory(bookDto.getCategory());

            Book newBook = bookRepository.save(book);
            return new AppResponse("Book added successfully", HttpStatus.OK.toString(), newBook);
        } catch (Exception e) {
            log.error("Error occurred while adding book", e);
            return new AppResponse("Error occurred while adding book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), null);
        }
    }

    @Override
    public AppResponse updateBook(AddBookDto bookDto, String username, Long bookId) {
        if (!hasPermissionToAddOrUpdateBook(username)) {
            return new AppResponse("User not authorized to update books", HttpStatus.FORBIDDEN.toString(), null);
        }

        try {
            Book book = bookRepository.findById(bookId).orElse(null);
            if (book == null) {
                return new AppResponse("Book not found", HttpStatus.NOT_FOUND.toString(), null);
            }

            book.setTitle(bookDto.getTitle());
            book.setAuthor(bookDto.getAuthor());
            book.setIsbn(bookDto.getIsbn());
            book.setDescription(bookDto.getDescription());
            book.setCategory(bookDto.getCategory());

            Book updatedBook = bookRepository.save(book);
            return new AppResponse("Book updated successfully", HttpStatus.OK.toString(), updatedBook);
        } catch (Exception e) {
            log.error("Error occurred while updating book", e);
            return new AppResponse("Error occurred while updating book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), null);
        }
    }

    @Override
    public AppResponse borrowBook(String username, Long bookId, String librarianUsername) {
        if (!hasPermissionToBorrowBook(username, bookId)) {
            return new AppResponse("User not eligible to borrow books", HttpStatus.FORBIDDEN.toString(), null);
        }

        try {
            Optional<User> librarian = userRepository.findByUsername(librarianUsername);
            if (librarian.isEmpty() || !librarian.get().getType().equals(UserType.MEMBER)) {
                return new AppResponse("Librarian not found or not authorized", HttpStatus.BAD_REQUEST.toString(), null);
            }

            Book book = bookRepository.findById(bookId).orElse(null);
            if (book == null) {
                return new AppResponse("Book not found with ID: " + bookId, HttpStatus.NOT_FOUND.toString(), null);
            }

            BorrowedBooks borrowedBooks = new BorrowedBooks();
            borrowedBooks.setBookAuthor(book.getAuthor());
            borrowedBooks.setBookTitle(book.getTitle());
            borrowedBooks.setBookId(bookId);
            borrowedBooks.setBorrowerUserName(username);
            borrowedBooks.setHasBeenReturned(false);
            borrowedBooks.setLibrarianUserName(librarianUsername);

            BorrowedBooks borrowedBook = borrowedBookRepository.save(borrowedBooks);
            return new AppResponse("Book borrowed successfully", HttpStatus.OK.toString(), borrowedBook);
        } catch (Exception e) {
            log.error("Error occurred while borrowing book", e);
            return new AppResponse("Error while borrowing book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), null);
        }
    }

    @Override
    public AppResponse returnBook(String username, Long bookId) {
        if (!canReturnBooks(username, bookId)) {
            return new AppResponse("User not eligible to return books", HttpStatus.FORBIDDEN.toString(), null);
        }

        try {
            BorrowedBooks borrowedBook = borrowedBookRepository.findByBookIdAndBorrowerUserName(bookId, username);
            if (borrowedBook == null) {
                return new AppResponse("Borrowed book record not found", HttpStatus.NOT_FOUND.toString(), null);
            }

            borrowedBook.setHasBeenReturned(true);
            borrowedBookRepository.save(borrowedBook);

            return new AppResponse("Book returned successfully", HttpStatus.OK.toString(), borrowedBook);
        } catch (Exception e) {
            log.error("Error occurred while returning the book", e);
            return new AppResponse("Error occurred while returning the book: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), null);
        }
    }

    @Override
    public AppResponse getPaginatedBorrowedBooks(int page, int size, String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            Page<BorrowedBooks> borrowedBooksPage = borrowedBookRepository.findAll(pageable);

            PaginatedResponse<BorrowedBooks> response = new PaginatedResponse<>(
                    borrowedBooksPage.getContent(),
                    borrowedBooksPage.getNumber(),
                    borrowedBooksPage.getSize(),
                    borrowedBooksPage.getTotalPages(),
                    borrowedBooksPage.getTotalElements()
            );

            return new AppResponse("Paginated list of borrowed books fetched successfully", HttpStatus.OK.toString(), response);
        } catch (Exception e) {
            log.error("Error occurred while fetching paginated borrowed books", e);
            return new AppResponse("Error occurred while fetching paginated borrowed books: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), null);
        }
    }

    @Override
    public AppResponse getPaginatedBooks(int page, int size, String sortBy) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
            Page<Book> bookPage = bookRepository.findAll(pageable);

            PaginatedResponse<Book> response = new PaginatedResponse<>(
                    bookPage.getContent(),
                    bookPage.getNumber(),
                    bookPage.getSize(),
                    bookPage.getTotalPages(),
                    bookPage.getTotalElements()
            );

            return new AppResponse("Paginated list of books fetched successfully", HttpStatus.OK.toString(), response);
        } catch (Exception e) {
            log.error("Error occurred while fetching paginated books", e);
            return new AppResponse("Error occurred while fetching paginated books: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.toString(), null);
        }
    }

    private boolean hasPermissionToAddOrUpdateBook(String username) {
        List<User> userList = userRepository.findAll();
        log.info("users:   {}", userList);
        Optional<User> currentUserOpt = userRepository.findByUsername(username);
        log.info("user:   {}",currentUserOpt);

        return currentUserOpt.map(user -> user.getType().equals("ADMIN")).orElse(false);
    }

    private boolean hasPermissionToBorrowBook(String username, Long bookId) {
        Optional<User> currentUserOpt = userRepository.findByUsername(username);
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<BorrowedBooks> borrowedBooksOpt = Optional.ofNullable(borrowedBookRepository.findByBookIdAndBorrowerUserName(bookId, username));

        return currentUserOpt.map(user -> !user.getType().equals(UserType.MEMBER)
                && (borrowedBooksOpt.isEmpty() || borrowedBooksOpt.get().isHasBeenReturned())
                && bookOpt.isPresent()).orElse(false);
    }

    private boolean canReturnBooks(String username, Long bookId) {
        Optional<User> currentUserOpt = userRepository.findByUsername(username);
        Optional<Book> bookOpt = bookRepository.findById(bookId);
        Optional<BorrowedBooks> borrowedBooksOpt = Optional.ofNullable(borrowedBookRepository.findByBookIdAndBorrowerUserName(bookId, username));

        return currentUserOpt.map(user -> !user.getType().equals(UserType.MEMBER)
                && borrowedBooksOpt.map(BorrowedBooks::isHasBeenReturned).orElse(false)
                && bookOpt.isPresent()).orElse(false);
    }
}
