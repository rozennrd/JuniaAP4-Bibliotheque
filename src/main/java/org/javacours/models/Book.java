package org.javacours.models;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class Book {
    private String isbn;
    private String title;
    private String description;
    private String authorName;
    private double price;
    private int numberOfCopies;
}
