package org.javacours.models;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Builder
public class BookLoan {
    @NonNull
    private Book book;
    @NonNull
    private User user;
    @NonNull
    private LocalDate loanDate;

    // Can be null = books have not been returned yet
    @Setter
    private LocalDate returnDate;
}
