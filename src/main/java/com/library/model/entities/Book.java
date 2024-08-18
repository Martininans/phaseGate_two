package com.library.model.entities;

import com.library.enums.BookCategory;
import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "library_user")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "author")
    private String author;
    @Column(name = "isbn")
    private String isbn;
    @Column(name = "description")
    private String  description;
    @Column(name = "category")
    private BookCategory category;
}
