package org.javacours.data;

import org.javacours.exceptions.BookNotFoundException;
import org.javacours.models.Book;
import org.javacours.models.BookLoan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;


public class BookRepo {
    private DataLoader dataLoader;
    private static BookRepo bookRepo;

    private BookRepo() throws IOException {
        dataLoader = DataLoader.getInstance();
    }

    public ArrayList<Book> getBooks() {
        return dataLoader.getBooks();
    }
    public ArrayList<BookLoan> getBookLoans() {
        return dataLoader.getBookLoans();
    }

    public static BookRepo getBookRepo() throws IOException {
        if (bookRepo == null) {
            bookRepo = new BookRepo();
        }
        return bookRepo;
    }

    public void addBook(Book book) {
        dataLoader.addBook(book);
    }

    public void addBookLoan(BookLoan bookLoan) {
        this.dataLoader.addBookLoan(bookLoan);
    }

    public Book getBookByIsbn(String isbn) throws BookNotFoundException {
        for(Book book: bookRepo.getBooks()) {
            if (Objects.equals(book.getIsbn(), isbn)) {
                return book;
            }
        }
        throw new BookNotFoundException("Aucun livre trouvé avec cet ISBN : " + isbn);
    }

    public void saveAllBooksToFile(String file) throws IOException {
        dataLoader.saveAllBooksToFile(file);
    }

    // Testing method to set a mock DataLoader (package-private)
    void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }
}
