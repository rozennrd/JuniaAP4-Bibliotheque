package org.javacours.services;

import org.javacours.data.BookRepo;
import org.javacours.exceptions.BookAlreadyLoanedException;
import org.javacours.exceptions.BookNotFoundException;
import org.javacours.exceptions.TooManyLoanedBooksException;
import org.javacours.models.Book;
import org.javacours.models.BookLoan;
import org.javacours.models.User;
import org.javacours.models.UserRole;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.mockito.Mockito.*;

public class BookServiceTest {
    private BookService bookService;
    private BookRepo bookRepo;
    private Book book;
    private User user;

    @BeforeEach
    void setup() throws IOException {
        bookRepo = Mockito.mock(BookRepo.class);
        bookService = BookService.getInstance();
        bookService.setBookRepo(bookRepo);
        user = User.builder()
                .password("password")
                .login("login")
                .build();
        book = Book.builder()
                .isbn("1234")
                .title("Test Book")
                .description("Test Description")
                .authorName("Test Author")
                .build();

        when(bookRepo.getBookByIsbn("1234")).thenReturn(book);
        when(bookRepo.getBooks()).thenReturn(new ArrayList<>(List.of(book)));
    }

    @Test
    void getInstance_ShouldReturnSameInstance() throws IOException {
        BookService instance1 = BookService.getInstance();
        BookService instance2 = BookService.getInstance();

        Assertions.assertSame(instance1, instance2);
    }

    @Test
    void loanBookToUser_ShouldAddBookLoan() throws BookNotFoundException {
        when(bookRepo.getBookLoans()).thenReturn(new ArrayList<>());

        bookService.loanBookToUser("1234", user);

        verify(bookRepo, times(1)).addBookLoan(any(BookLoan.class));
    }

    @Test
    void loanBookToUser_ShouldThrowBookNotFoundException() {
        when(bookRepo.getBookByIsbn("9999")).thenThrow(new BookNotFoundException("Book not found"));

        Assertions.assertThrows(BookNotFoundException.class, () -> {
            bookService.loanBookToUser("9999", user);
        });
    }

    @Test
    void loanBookToUser_ShouldThrowTooManyLoanedBooksException() {

        Book book = Book.builder().isbn("1234").title("Test Book").authorName("Author").description("Description").build();
        User user = User.builder().login("testUser").password("hashedPassword").role(UserRole.USER).build();

        BookLoan existingLoan = BookLoan.builder()
                .book(Book.builder().isbn("5677").title("Another Book").authorName("Another Author").description("Description").build())
                .user(user)
                .loanDate(LocalDate.now())
                .build();

        BookLoan additionalLoan1 = BookLoan.builder()
                .book(Book.builder().isbn("5678").title("Another Book").authorName("Another Author").description("Description").build())
                .user(user)
                .loanDate(LocalDate.now())
                .build();

        BookLoan additionalLoan2 = BookLoan.builder()
                .book(Book.builder().isbn("9101").title("Third Book").authorName("Third Author").description("Description").build())
                .user(user)
                .loanDate(LocalDate.now())
                .build();

        ArrayList<BookLoan> existingLoans = new ArrayList<>(List.of(existingLoan, additionalLoan1, additionalLoan2));

        when(bookRepo.getBookLoans()).thenReturn(existingLoans);
        Assertions.assertEquals(user, existingLoan.getUser());
        Assertions.assertNull(existingLoan.getReturnDate());
        Assertions.assertEquals(3, getNumberOfBooksPerUser(user));
        when(bookRepo.getBookByIsbn("1234")).thenReturn(book); // Ensure the book exists

        // Act & Assert
        Assertions.assertThrows(TooManyLoanedBooksException.class, () -> {
            bookService.loanBookToUser("1234", user);
        });
    }

    @Test
    void loanBookToUser_ShouldThrowBookAlreadyLoanedException() {
        // Arrange
        BookLoan existingLoan = BookLoan.builder()
                .book(book)
                .user(user)
                .loanDate(LocalDate.now())
                .build();
        ArrayList<BookLoan> existingLoans = new ArrayList<>();
        existingLoans.add(existingLoan);

        when(bookRepo.getBookLoans()).thenReturn(existingLoans);
        Assertions.assertThrows(BookAlreadyLoanedException.class, () -> {
            bookService.loanBookToUser("1234", user);
        });
    }


    @Test
    void addBook_ShouldAddBookToRepo() {
        bookService.addBook(book);
        verify(bookRepo, times(1)).addBook(book);
    }

    @Test
    void exportAllToFile_ShouldCallSaveAllBooksToFile() throws IOException {
        bookService.exportAllToFile("testFile.json");
        verify(bookRepo, times(1)).saveAllBooksToFile("testFile.json");
    }


    int getNumberOfBooksPerUser(User user){
        int nbOfBooks = 0;
        for (BookLoan bookLoan : bookRepo.getBookLoans()) {
            if (Objects.equals(bookLoan.getUser(), user) && bookLoan.getReturnDate() == null) {
                nbOfBooks++;
                System.out.println(bookLoan.getUser());
            }
        }
        return nbOfBooks;
    }
}
