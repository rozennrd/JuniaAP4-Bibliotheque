package org.javacours.models;

import lombok.*;

@Getter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class Book {
    private String isbn;
    private String title;
    private String description;
    private String authorName;
    private double price;
    private int numberOfCopies;
}
