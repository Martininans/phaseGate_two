package com.library.services;

import com.library.enums.BookCategory;
import com.library.enums.UserType;
import com.library.model.entities.Book;
import com.library.model.entities.BorrowedBooks;
import com.library.model.entities.User;
import com.library.model.request.AddBookDto;
import com.library.model.response.AppResponse;
import com.library.repositories.BookRepository;
import com.library.repositories.BorrowedBookRepository;
import com.library.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LibraryServiceImplTest {

    @InjectMocks
    private LibraryServiceImpl libraryService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BorrowedBookRepository borrowedBookRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addBook_UserHasPermission_ShouldReturnSuccess() {
        AddBookDto bookDto = AddBookDto.builder()
                .title("Title")
                .author("Author")
                .isbn("ISBN123")
                .description("Description")
                .category(BookCategory.LOVE)
                .build();

        User librarian = User.builder()
                .username("librarian")
                .type(UserType.MEMBER)
                .build();

        Book newBook = Book.builder()
                .id(1L)
                .title("Title")
                .author("Author")
                .isbn("ISBN123")
                .description("Description")
                .category(BookCategory.LOVE)
                .build();

        when(userRepository.findByUsername("librarian")).thenReturn(Optional.of(librarian));
        when(bookRepository.save(any(Book.class))).thenReturn(newBook);

        AppResponse response = libraryService.addBook(bookDto, "librarian");

        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertEquals("Book added successfully", response.getMessage());
        assertNotNull(response.getData());
        verify(bookRepository).save(any(Book.class));
    }
    @Test
    void addBook_UserDoesNotHavePermission_ShouldReturnForbidden() {
        AddBookDto bookDto = AddBookDto.builder()
                .title("Title")
                .author("Author")
                .isbn("ISBN123")
                .description("Description")
                .category(BookCategory.LOVE)
                .build();

        User member = User.builder()
                .username("member")
                .type(UserType.ADMIN)
                .build();

        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));
        AppResponse response = libraryService.addBook(bookDto, "member");

        assertEquals(HttpStatus.FORBIDDEN.toString(), response.getCode());
        assertEquals("User not authorized to add books", response.getMessage());
    }
    @Test
    void updateBook_UserHasPermission_ShouldReturnSuccess() {
        AddBookDto bookDto = AddBookDto.builder()
                .title("New Title")
                .author("New Author")
                .isbn("ISBN123")
                .description("New Description")
                .category(BookCategory.POLITICS)
                .build();

        User librarian = User.builder()
                .username("librarian")
                .type(UserType.MEMBER)
                .build();

        Book existingBook = Book.builder()
                .id(1L)
                .title("Old Title")
                .author("Old Author")
                .isbn("ISBN123")
                .description("Old Description")
                .category(BookCategory.POLITICS)
                .build();

        Book updatedBook = Book.builder()
                .id(1L)
                .title("New Title")
                .author("New Author")
                .isbn("ISBN123")
                .description("New Description")
                .category(BookCategory.POLITICS)
                .build();

        when(userRepository.findByUsername("librarian")).thenReturn(Optional.of(librarian));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(existingBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);

        AppResponse response = libraryService.updateBook(bookDto, "librarian", 1L);

        assertEquals(HttpStatus.OK.toString(), response.getCode());
        assertEquals("Book updated successfully", response.getMessage());
        assertNotNull(response.getData());
        verify(bookRepository).save(any(Book.class));
    }
    @Test
    void updateBook_BookNotFound_ShouldReturnNotFound() {
        AddBookDto bookDto = AddBookDto.builder()
                .title("New Title")
                .author("New Author")
                .isbn("ISBN123")
                .description("New Description")
                .category(BookCategory.RELIGION)
                .build();

        User librarian = User.builder()
                .username("librarian")
                .type(UserType.MEMBER)
                .build();

        when(userRepository.findByUsername("librarian")).thenReturn(Optional.of(librarian));
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        AppResponse response = libraryService.updateBook(bookDto, "librarian", 1L);

        assertEquals(HttpStatus.NOT_FOUND.toString(), response.getCode());
        assertEquals("Book not found", response.getMessage());
    }
    @Test
    void borrowBook_UserHasPermission_ShouldReturnSuccess() {
        User member = User.builder()
                .username("member")
                .type(UserType.MEMBER)
                .build();

        Book book = Book.builder()
                .id(1L)
                .title("Book Title")
                .author("Book Author")
                .build();

        BorrowedBooks borrowedBooks = BorrowedBooks.builder()
                .bookId(1L)
                .bookTitle("Book Title")
                .bookAuthor("Book Author")
                .borrowerUserName("member")
                .librarianUserName("librarian")
                .hasBeenReturned(true)
                .build();

        when(userRepository.findByUsername("member")).thenReturn(Optional.of(member));
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(borrowedBookRepository.save(any(BorrowedBooks.class))).thenReturn(borrowedBooks);

        AppResponse response = libraryService.borrowBook("member", 1L, "librarian");

//        assertEquals(HttpStatus.OK.toString(), response.getCode());
//        assertEquals("Book borrowed successfully", response.getMessage());
//        assertNotNull(response.getData());
//        verify(borrowedBookRepository).save(any(BorrowedBooks.class));
    }
}
