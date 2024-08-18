package com.library.model.entities;


import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@Setter
@Entity
@Table(name = "borrowed_book")
public class BorrowedBooks {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "borrower_username", nullable = false)
    private String borrowerUserName;

    @Column(name = "librarian_username", nullable = false)
    private String librarianUserName;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

    @Column(name = "book_title", nullable = false)
    private String bookTitle;

    @Column(name = "book_author", nullable = false)
    private String bookAuthor;

    @Column(name = "has_returned", nullable = false)
    private boolean hasBeenReturned;

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

}
