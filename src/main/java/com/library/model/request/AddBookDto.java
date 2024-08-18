package com.library.model.request;

import com.library.enums.BookCategory;
import lombok.Builder;
import lombok.Data;
@Builder
@Data
public class AddBookDto {
    private String title;
    private String author;
    private String isbn;
    private String  description;
    private BookCategory category;
}
