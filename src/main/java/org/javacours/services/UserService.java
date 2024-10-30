package org.javacours.services;

import lombok.Setter;
import org.javacours.data.DataLoader;
import org.javacours.exceptions.UserNotFoundException;
import org.javacours.models.User;
import org.javacours.models.UserRole;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;


public class UserService {
    private static UserService userService;
    @Setter
    private DataLoader dataLoader;
    private UserService() throws IOException {
        this.dataLoader = DataLoader.getInstance();
    }

    public static UserService getInstance() throws IOException {
        if(userService == null) {
            userService = new UserService();
        }
        return userService;
    }

    public String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            // Convert byte array to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de hash du mot de passe", e);
        }
    }

    public void addUser(String login, String password) {
        User user = User.builder()
                .login(login)
                .password(hashPassword(password))
                .role(UserRole.USER)// Store hashed password
                .build();
        dataLoader.addUser(user);
    }

    // If you're reading from System.in
    public void addUserFromConsole(Scanner scanner) {


        System.out.print("Entrez votre login: \n");
        String login = scanner.nextLine();
        System.out.print("Entrez votre mot de passe: \n");
        String password = scanner.nextLine();
        try {
            addUser(login, password);
            System.out.println("L'utilisateur a bien été ajouté. Vous pouvez vous connecter.");
        } catch (Exception e ){
            System.out.println(e.getMessage());
        }


    }

    public User login(Scanner scanner) {

        System.out.print("Enter login: ");
        String login = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            User user = dataLoader.getUser(login, hashPassword(password));
            System.out.println("Bienvenue " + user.getLogin());
            return user;
        } catch (UserNotFoundException e) {
            System.out.println("Erreur: " + e.getMessage());
            return null;
        }
    }
}
