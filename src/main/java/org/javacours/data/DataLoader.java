package org.javacours.data;

import com.alibaba.fastjson2.JSON;

import com.alibaba.fastjson2.JSONObject;
import org.javacours.exceptions.UserNotFoundException;
import org.javacours.models.Book;
import org.javacours.models.BookLoan;
import org.javacours.models.User;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;



public class DataLoader {
    private final String SAVE_FILE_NAME = "src/main/resources/catalogue_livres.json";
    private static ArrayList<User> users;
    private static ArrayList<Book> books;
    private static ArrayList<BookLoan> bookLoans;
    private static DataLoader dataLoader;

    private DataLoader() throws IOException {
        try {

            String jsonString = new String(Files.readAllBytes(Paths.get(SAVE_FILE_NAME)));
            JSONObject jsonObject = JSON.parseObject(jsonString);

            users = new ArrayList<>(JSON.parseArray(jsonObject.getString("users"), User.class));
            books = new ArrayList<>(JSON.parseArray(jsonObject.getString("books"), Book.class));
            bookLoans = new ArrayList<>(JSON.parseArray(jsonObject.getString("bookLoans"), BookLoan.class));

        } catch (IOException e) {
            throw e;
        }

    }

    public static DataLoader getInstance() throws IOException {
        if (dataLoader == null) {
            dataLoader = new DataLoader();
        }
        return dataLoader;
    }

    /* As those are ArrayLists, modification of objects is done directly in the repo */

    public ArrayList<Book> getBooks() {
        return new ArrayList<>();
    }

    public ArrayList<BookLoan> getBookLoans() {
        return new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void addBookLoan(BookLoan bookLoan) {
        bookLoans.add(bookLoan);
    }

    public User getUser(String name, String password) {

        for (User user: users) {
            if (Objects.equals(user.getLogin(), name) && Objects.equals(user.getPassword(), password)) {
                return user;
            }
        }
        throw new UserNotFoundException("Pas d'utilisateur avec ce nom d'utilisateur et ce mot de passe");
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void saveData() throws IOException {
        try {

            JSONObject rootObject = new JSONObject();

            rootObject.put("users", users);
            rootObject.put("books", books);
            rootObject.put("bookLoans", bookLoans);

            try (FileWriter writer = new FileWriter(SAVE_FILE_NAME)) {
                writer.write(JSON.toJSONString(rootObject));
            }

        } catch (IOException e) {
            throw e;
        }
    }
    public void saveAllBooksToFile(String file) throws IOException {
        try {
            JSONObject rootObject = new JSONObject();

            rootObject.put("books", books);

            try (FileWriter writer = new FileWriter(file)) {
                writer.write(JSON.toJSONString(rootObject));
            }

        } catch (IOException e) {
            throw e;
        }
    }

}
