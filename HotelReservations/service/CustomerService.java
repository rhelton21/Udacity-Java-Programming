package service;

import model.Customer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * CustomerService.java
 * This class is responsible for managing customer-related operations.
 * It follows the singleton design pattern to ensure there is only one instance of `CustomerService`.
 * 
 * Functionality provided by this class includes:
 * - Adding a new customer to the system
 * - Retrieving a customer by their email
 * - Getting a collection of all registered customers
 */
public class CustomerService {
    // Singleton instance of CustomerService
    private static final CustomerService INSTANCE = new CustomerService();
    // A map to store customers, where the key is the email and the value is the Customer object
    private final Map<String, Customer> customers = new HashMap<>();

    /**
     * Private constructor to prevent instantiation from outside the class.
     * This ensures that the singleton instance is used.
     */
    private CustomerService() {}

    /**
     * Returns the singleton instance of `CustomerService`.
     * 
     * @return the single instance of `CustomerService`
     */
    public static CustomerService getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a new customer to the system.
     * 
     * @param email     the customer's email (used as a unique identifier)
     * @param firstName the customer's first name
     * @param lastName  the customer's last name
     */
    public void addCustomer(String email, String firstName, String lastName) {
        // Create a new Customer object with the provided details
        Customer customer = new Customer(firstName, lastName, email);
        // Add the customer to the map, with their email as the key
        customers.put(email, customer);
    }

    /**
     * Retrieves a customer from the system by their email.
     * 
     * @param customerEmail the email of the customer to retrieve
     * @return the Customer object if found, or null if not found
     */
    public Customer getCustomer(String customerEmail) {
        return customers.get(customerEmail);
    }

    /**
     * Retrieves all customers registered in the system.
     * 
     * @return a collection of all Customer objects
     */
    public Collection<Customer> getAllCustomers() {
        return customers.values();
    }
}
