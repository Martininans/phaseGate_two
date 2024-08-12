package com.library.util;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginatedResponse<T> {
    private List<T> content;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private long totalElements;
}
