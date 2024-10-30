package org.javacours.data;
import org.javacours.exceptions.BookNotFoundException;
import org.javacours.models.Book;
import org.javacours.models.BookLoan;
import org.javacours.models.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;


class BookRepoTest {

    private BookRepo bookRepo;

    private DataLoader dl;

    @BeforeEach
    void setup() {
        try {
            dl = Mockito.mock(DataLoader.class);
            bookRepo = BookRepo.getBookRepo();
            bookRepo.setDataLoader(dl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Nested
    class getBooksTest{
        @Test
        void getBooks_ShouldReturnListOfBooks() {
            Book book1 = Book.builder()
                    .isbn("1234")
                    .title("Book 1")
                    .description("Description 1")
                    .authorName("Author 1")
                    .build();

            Book book2 = Book.builder()
                    .isbn("5678")
                    .title("Book 2")
                    .description("Description 2")
                    .authorName("Author 2")
                    .build();

            ArrayList<Book> expectedBooks = new ArrayList<>(Arrays.asList(book1, book2));
            Mockito.when(dl.getBooks()).thenReturn(expectedBooks);

            ArrayList<Book> actualBooks = bookRepo.getBooks();

            Assertions.assertNotNull(actualBooks);
            Assertions.assertEquals(2,actualBooks.size());
            Assertions.assertEquals(book1, actualBooks.getFirst());
        }

        @Test
        void getBooks_ShouldReturnEmptyListWhenNoBooks() {
            Mockito.when(dl.getBooks()).thenReturn(new ArrayList<>());

            ArrayList<Book> actualBooks = bookRepo.getBooks();

            Assertions.assertNotNull(actualBooks);
            Assertions.assertEquals(actualBooks.size(), 0);
        }
    }

    @Nested
    class getBookLoansTest {
        @Test
        void getBookLoans_ShouldReturnListOfBookLoans() {
            Book book1 = Book.builder()
                    .isbn("1234")
                    .title("Book 1")
                    .description("Description 1")
                    .authorName("Author 1")
                    .build();

            Book book2 = Book.builder()
                    .isbn("5678")
                    .title("Book 2")
                    .description("Description 2")
                    .authorName("Author 2")
                    .build();

            User user1 = User.builder().login("user1").build();
            User user2 = User.builder().login("user2").build();

            BookLoan loan1 = BookLoan.builder()
                    .book(book1)
                    .user(user1)
                    .loanDate(LocalDate.of(2024, 1, 1))
                    .returnDate(null)
                    .build();

            BookLoan loan2 = BookLoan.builder()
                    .book(book2)
                    .user(user2)
                    .loanDate(LocalDate.of(2024, 1, 2))
                    .returnDate(null)
                    .build();

            ArrayList<BookLoan> expectedBookLoans = new ArrayList<>(Arrays.asList(loan1, loan2));
            Mockito.when(dl.getBookLoans()).thenReturn(expectedBookLoans);

            ArrayList<BookLoan> actualBookLoans = bookRepo.getBookLoans();

            Assertions.assertNotNull(actualBookLoans);
            Assertions.assertEquals(2, actualBookLoans.size());
            Assertions.assertEquals(loan1, actualBookLoans.get(0));
            Assertions.assertEquals(loan2, actualBookLoans.get(1));
        }
    }

    @Test
    void addBook_ShouldCallDataLoaderAddBook() {
        Book book = Book.builder()
                .isbn("1234")
                .title("Book 1")
                .description("Description 1")
                .authorName("Author 1")
                .build();

        bookRepo.addBook(book);

        Mockito.verify(dl).addBook(book);
    }

    @Test
    void addBookLoan() {
    }

    @Test
    void getBookByIsbn_ShouldReturnBook_WhenBookExists() throws BookNotFoundException {
        Book book1 = Book.builder()
                .isbn("1234")
                .title("Book 1")
                .description("Description 1")
                .authorName("Author 1")
                .build();

        ArrayList<Book> expectedBooks = new ArrayList<>(Collections.singletonList(book1));

        Mockito.when(dl.getBooks()).thenReturn(expectedBooks);

        Book actualBook = bookRepo.getBookByIsbn("1234");

        Assertions.assertNotNull(actualBook);
        Assertions.assertEquals(book1, actualBook);
    }

    @Test
    void getBookByIsbn_ShouldThrowBookNotFoundException_WhenBookDoesNotExist() {
        Mockito.when(dl.getBooks()).thenReturn(new ArrayList<>());

        Assertions.assertThrows(BookNotFoundException.class, () -> {
            bookRepo.getBookByIsbn("9999");
        });
    }

    @Test
    void saveAllBooksToFile_ShouldCallDataLoaderSaveAllBooksToFile() throws IOException {
        String filePath = "thebestbookfile.txt";

        bookRepo.saveAllBooksToFile(filePath);

       Mockito.verify(dl).saveAllBooksToFile(filePath);
    }

    @Test
    void saveAllBooksToFile_ShouldThrowIOException_WhenIOExceptionOccurs() throws IOException {

        String filePath = "thebestbookfile.txt";
        Mockito.doThrow(new IOException("File not found")).when(dl).saveAllBooksToFile(filePath);

        IOException exception = Assertions.assertThrows(IOException.class, () -> {
            bookRepo.saveAllBooksToFile(filePath);
        });

        Assertions.assertEquals("File not found", exception.getMessage());
    }
}