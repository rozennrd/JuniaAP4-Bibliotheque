package org.javacours;

import org.javacours.data.DataLoader;
import org.javacours.models.Book;
import org.javacours.models.User;
import org.javacours.models.UserRole;
import org.javacours.services.BookService;
import org.javacours.services.UserService;

import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;

public class Main {
    static Scanner scanner = new Scanner(System.in);
    static String input;
    static UserService userService;
    static BookService bookService;

    public static void main(String[] args) {

        DataLoader dataLoader;
       // Init data
       try {
           dataLoader = DataLoader.getInstance();
           userService = UserService.getInstance();
           bookService = BookService.getInstance();
       } catch (IOException e) {
           System.out.println("Les données n'ont pas pu être chargées : " + e.getMessage());
           return;
       }

       // App
        do {
           appAccueil();
       } while (!Objects.equals(input, "0"));

        // Save data
        try {
            dataLoader.saveData();
            System.out.println("Les données ont bien été enregistrées");

        } catch (IOException e) {
            System.out.println("Les données n'ont pas pu être enregistrées : " + e.getMessage());
        }
    }

    // method always inverted because we check if invalid, is kept this way for readability
    public static boolean appAccueilInputIsValid(String input) {
        return (Objects.equals(input, "1")
                || Objects.equals(input, "2")
                || Objects.equals(input, "3")
                || Objects.equals(input, "4")
                || Objects.equals(input, "0"));
    }

    public static void appAccueil() {
        do {
            System.out.println(menuAccueil());
            input = scanner.nextLine();
            if(!appAccueilInputIsValid(input)) {
                System.out.println("Entrez une valeur valide");
            }
        } while (!appAccueilInputIsValid(input));

        // Auth
        if (Objects.equals(input, "1")) {
            try {
                User currentUser = userService.login(scanner);
                if (Objects.equals(currentUser.getRole(), UserRole.ADMIN)) {
                    do {
                        appAdmin(currentUser);
                    } while (!Objects.equals(input, "0"));
                } else {
                    do {
                        appUser(currentUser);
                    } while (!Objects.equals(input, "0"));
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
        // List all books
        if (Objects.equals(input, "2")) {
            System.out.println(bookService.getCatalogue());
        }
        // See book by isbn
        if (Objects.equals(input, "3")) {
            bookService.displayBookByIsbn();
        }
        // List all books
        if (Objects.equals(input, "4")) {
            userService.addUserFromConsole(scanner);
        }
    }

    public static boolean userInputValid(String input){
        return Objects.equals(input, "1")
                || Objects.equals(input, "2")
                || Objects.equals(input, "3")
                || Objects.equals(input, "0");
    }

    public static void appUser(User currentUser) {

        do {
            System.out.println(menuUser(currentUser));
            input = scanner.nextLine();
            if(!userInputValid(input)) {
                System.out.println("Entrez une valeur valide");
            }
        } while (!userInputValid(input));

        // Borrow a book
        if (Objects.equals(input, "1")) {
            String isbn = scanner.nextLine();
            try {
                bookService.loanBookToUser(isbn, currentUser);
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        // Show all books
        if (Objects.equals(input, "2")) {
            System.out.println(bookService.getCatalogue());
        }
        // See book by isbn
        if (Objects.equals(input, "3")) {
            bookService.displayBookByIsbn();
        }
    }

    public static boolean adminInputValid(String input){
        return Objects.equals(input, "1")
                || Objects.equals(input, "2")
                || Objects.equals(input, "3")
                || Objects.equals(input, "4")
                || Objects.equals(input, "0");
    }

    public static void appAdmin(User currentUser) {

        do {
            System.out.println(menuAdmin(currentUser));
            input = scanner.nextLine();
            if(!adminInputValid(input)) {
                System.out.println("Entrez une valeur valide");
            }
        } while (!adminInputValid(input));

        // add book
        if (Objects.equals(input, "1")) {
            // Reading inputs from the console
            System.out.print("Enter ISBN: ");
            String isbn = scanner.nextLine();

            System.out.print("Enter Title: ");
            String title = scanner.nextLine();

            System.out.print("Enter Description: ");
            String description = scanner.nextLine();

            System.out.print("Enter Author Name: ");
            String authorName = scanner.nextLine();

            System.out.print("Enter Price: ");
            double price = scanner.nextDouble();

            System.out.print("Enter Number of Copies: ");
            int numberOfCopies = scanner.nextInt();

            Book book = Book.builder()
                    .title(title)
                    .authorName(authorName)
                    .description(description)
                    .isbn(isbn)
                    .price(price)
                    .numberOfCopies(numberOfCopies)
                    .build();

            bookService.addBook(book);
            System.out.println("Le livre a bien été ajouté à la collection");
        }
        // Show all books
        if (Objects.equals(input, "2")) {
            System.out.println(bookService.getCatalogue());
        }
        // See book by isbn
        if (Objects.equals(input, "3")) {
            bookService.displayBookByIsbn();
        }

        // export all books to file
        if (Objects.equals(input, "5")) {
            System.out.println("Entrez l'emplacement du fichier à écrire et le nom du fichier");
            String file = scanner.nextLine();
            try {
                bookService.exportAllToFile(file);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }


    public static String menuAccueil() {
        return """
                *************** Bienvenue à la Bibliothèque *************
                1. S'identifier
                2. Consulter le catalogue de livres
                3. Détails d'un livre par ISBN
                4. S'inscrire à la bibliothèque
                0. Quitter
                *********************************************************\n
                """;
    }

    public static String menuUser(User user) {
        return "*************** Bienvenue " + user.getLogin() +" *********************\n" +
                "1. Emprunter un livre\n"+
                "2. Consulter le catalogue de livres\n"+
                "3. Détails d'un livre par ISBN\n"+
                "0. Quitter\n"+
                "*********************************************************\n";
    }

    public static String menuAdmin(User user) {
        return "*************** Bienvenue " + user.getLogin() +" *********************\n" +
                "1. Ajouter un livre\n" +
                "2. Consulter le catalogue de livres\n" +
                "3. Détails d'un livre par ISBN\n"+
                "4. Exporter le catalogue\n"+
                "0. Quitter\n"+
                "*********************************************************\n";
    }
}