package com.library.repositories;

import com.library.model.entities.BorrowedBooks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BorrowedBookRepository extends JpaRepository<BorrowedBooks, Long> {
    BorrowedBooks findByBookIdAndBorrowerUserName(Long borrowedBookID, String borrowerUserName);
}
