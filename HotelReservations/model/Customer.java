package model;

import java.util.regex.Pattern;

/**
 * Customer.java
 * This class represents a customer in the hotel reservation system.
 * Each customer has a first name, last name, and email address.
 * The email address is validated to ensure it follows a valid format.
 */
public class Customer {
    // Customer's first name (immutable)
    private final String firstName;
    // Customer's last name (immutable)
    private final String lastName;
    // Customer's email address (immutable, must be valid format)
    private final String email;

    /**
     * Constructor for creating a new Customer object.
     * 
     * @param firstName the customer's first name
     * @param lastName  the customer's last name
     * @param email     the customer's email address
     *                  (validated to ensure proper format)
     * @throws IllegalArgumentException if the email format is invalid
     */
    public Customer(String firstName, String lastName, String email) {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format: " + email);
        }
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    /**
     * Validates the format of the email address using a regular expression.
     * 
     * @param email the email address to validate
     * @return true if the email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        // Regular expression for validating email addresses
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
        return Pattern.matches(emailRegex, email);
    }

    /**
     * Gets the customer's first name.
     * 
     * @return the customer's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Gets the customer's last name.
     * 
     * @return the customer's last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Gets the customer's email address.
     * 
     * @return the customer's email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Provides a string representation of the Customer object.
     * 
     * @return a string containing the customer's details
     */
    @Override
    public String toString() {
        return "Customer{" +
                "firstName=\"" + firstName + "\"" +
                ", lastName=\"" + lastName + "\"" +
                ", email=\"" + email + "\"" +
                '}';
    }
}
