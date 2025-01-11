// Package declaration: This specifies the folder structure where the class resides.
// The 'tester' package is commonly used for testing purposes.
package tester;

// Import the Customer class from the model package.
import model.Customer;

/**
 * Driver.java
 * This class serves as a test harness for verifying the correctness of the Customer class.
 * It tests the email validation logic, checks that valid emails work as expected,
 * and ensures that invalid emails throw an IllegalArgumentException.
 */
public class Driver {

    /**
     * Main method: This is the entry point for running the test cases.
     * It creates instances of the Customer class to validate email inputs.
     */
    public static void main(String[] args) {
        System.out.println("Testing Customer Email Validation:");

        // Test case 1: Creating a customer with a valid email address.
        System.out.println("\n--- Valid Email Test ---");
        try {
            // Creating a Customer object with a valid email.
            Customer validCustomer = new Customer("first", "second", "j@domain.com");
            System.out.println("Valid Customer Created: " + validCustomer);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage()); // Should not throw an error for valid email.
        }

        // Test case 2: Creating a customer with an invalid email address.
        System.out.println("\n--- Invalid Email Test ---");
        try {
            // Creating a Customer object with an invalid email.
            Customer invalidCustomer = new Customer("first", "second", "invalidEmail");
            System.out.println("Invalid Customer Created: " + invalidCustomer);
        } catch (IllegalArgumentException e) {
            // Expecting this to catch an exception due to invalid email format.
            System.out.println("Error: " + e.getMessage()); // Should print an error message for invalid email.
        }
    }
}
