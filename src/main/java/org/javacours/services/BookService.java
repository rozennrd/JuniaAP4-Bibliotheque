package org.javacours.services;

import lombok.Setter;
import org.javacours.data.BookRepo;
import org.javacours.exceptions.BookAlreadyLoanedException;
import org.javacours.exceptions.BookNotFoundException;
import org.javacours.exceptions.TooManyLoanedBooksException;
import org.javacours.models.Book;
import org.javacours.models.BookLoan;
import org.javacours.models.User;

import java.io.IOException;
import java.time.LocalDate;

import java.util.Objects;
import java.util.Scanner;

public class BookService {

    private static BookService bookService;
    @Setter
    private BookRepo bookRepo;

    private BookService() throws IOException {
        bookRepo = BookRepo.getBookRepo();
    }
    public static BookService getInstance() throws IOException {
        if(bookService == null) {
            bookService = new BookService();
        }
        return bookService;
    }

    private void checkAllConditionsForUserLoan(Book book, User user) {
        int nbOfBooks = 0;
        for (BookLoan bookLoan : bookRepo.getBookLoans()) {
            if (Objects.equals(bookLoan.getUser(), user) && bookLoan.getReturnDate() == null) {
                nbOfBooks ++;
                int MAX_LOANED_BOOKS_PER_USER = 3;
                if (nbOfBooks >= MAX_LOANED_BOOKS_PER_USER) {
                    throw new TooManyLoanedBooksException("Vous ne pouvez pas emprunter plus de trois livres simultanément");
                }
            }
            if (Objects.equals(bookLoan.getBook().getTitle(), book.getTitle())
                    && Objects.equals(bookLoan.getBook().getAuthorName(), book.getAuthorName())
                    && Objects.equals(bookLoan.getBook().getDescription(), book.getDescription())) {
                throw new BookAlreadyLoanedException("Vous ne pouvez pas emprunter plusieurs fois le même livre");
            }
        }
    }

    public void loanBookToUser(String isbn, User user) throws BookNotFoundException {
        Book book;
        try {
            book = bookRepo.getBookByIsbn(isbn);
        }catch (BookNotFoundException e) {
            System.out.println(e.getMessage());
            throw e;
        }

        checkAllConditionsForUserLoan(book, user);

        BookLoan bookLoan = BookLoan.builder()
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .build();
        bookRepo.addBookLoan(bookLoan);
    }

    public String getCatalogue() {
        return bookRepo.getBooks().toString();
    }

    public void displayBookByIsbn() {
        try {
           Scanner scanner = new Scanner(System.in);
           System.out.println("Entrez l'isbn du livre à consulter :");
           String isbn = scanner.nextLine();
           bookRepo.getBookByIsbn(isbn);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void addBook(Book book) {
        bookRepo.addBook(book);
    }

    public void exportAllToFile(String file) throws IOException {
        bookRepo.saveAllBooksToFile(file);
    }
}
