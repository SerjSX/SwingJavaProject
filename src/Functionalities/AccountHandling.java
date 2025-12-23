package Functionalities;

import Exceptions.AccountAlreadyExistsException;
import Exceptions.InsufficientInputException;
import Exceptions.InsufficientPasswordException;
import Exceptions.InvalidRegisterInputException;

import javax.swing.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;


public class AccountHandling {
    public static String loginCheck(String userName, char[] password) {

        try {
            BufferedReader file = new BufferedReader(new FileReader("src/UserData/accounts.data"));
            String line = file.readLine();

            while (line != null) {
                String[] lineSplit = line.split(",");

                String storedUserName = lineSplit[2];
                String storedsaltStr = lineSplit[3];
                String storedhashStr = lineSplit[4];

                boolean decryptPasswordAndCompareResult = decryptPasswordAndCompare(storedsaltStr, storedhashStr, new String(password));

                if (storedUserName.equals(userName) && decryptPasswordAndCompareResult) {
                    return storedUserName;
                }

                line = file.readLine();
            }

            file.close();
        } catch (IOException ignored) {

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    public static void registerAttempt(String userName, String firstName, String lastName, char[] password) throws InvalidRegisterInputException, NoSuchAlgorithmException, InsufficientInputException, InsufficientPasswordException, AccountAlreadyExistsException {
        String stringedPassword = new String(password);

        if (userName.isBlank() || firstName.isBlank() || lastName.isBlank() || stringedPassword.isBlank()) {
            throw new InvalidRegisterInputException();
        }

        if (userName.length() < 3 || firstName.length() < 3 || lastName.length() < 3) {
            throw new InsufficientInputException();
        }

        if (stringedPassword.length() < 7) {
            throw new InsufficientPasswordException();
        }

        try {
            if (accountExists(userName)) {
                throw new AccountAlreadyExistsException();
            }

            String[] encryptedPasswordResult = encryptPassword(stringedPassword);
            String saltStr = encryptedPasswordResult[0];
            String hashStr = encryptedPasswordResult[1];

            PrintWriter pw = new PrintWriter(new FileWriter("src/UserData/accounts.data", true), true);
            pw.println(firstName + "," + lastName + "," + userName + "," + saltStr + "," + hashStr);

            pw.close();
        } catch (IOException ignored) {

        }
    }

    private static boolean accountExists(String username) {
        try {
            BufferedReader file = new BufferedReader(new FileReader("src/UserData/accounts.data"));
            String line = file.readLine();

            while (line != null) {
                String lineUsername = line.split(",")[2];

                if (lineUsername.equals(username)) {
                    return true;
                }

                line = file.readLine();
            }
        } catch (IOException ignored) {

        }

        return false;
    }

    private static String[] encryptPassword(String password) throws NoSuchAlgorithmException {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);

        //Hashing the password passed with salt
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);
        byte[] hashedPassword = md.digest(password.getBytes());

        //Encoding the hashed password to base64 for storage
        String saltStr = Base64.getEncoder().encodeToString(salt);
        String hashStr = Base64.getEncoder().encodeToString(hashedPassword);

        //Returning the format to store in file: username,saltStr,hashStr
        return new String[]{saltStr, hashStr};
    }

    private static boolean decryptPasswordAndCompare(String saltStr, String hashStr, String passwordAttempt) throws NoSuchAlgorithmException {
        //Read salt str and hash str bytes
        byte[] salt = Base64.getDecoder().decode(saltStr);
        byte[] storedHash = Base64.getDecoder().decode(hashStr);

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt);

        // Getting the bytes and hash of the passed password attempt
        byte[] attemptHash = md.digest(passwordAttempt.getBytes());

        // Comparing the hashes of both the stored password and the user's attempted password in the password field attempt with the password stored
        // in the file we're looping at
        return MessageDigest.isEqual(attemptHash, storedHash);
    }
}
