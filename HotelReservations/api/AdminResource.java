package api;

import model.*;
import service.CustomerService;
import service.ReservationService;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * AdminResource.java
 * This class provides an API for administrators to manage the hotel reservation system.
 * It serves as a bridge between the user interface and the service layer.
 * 
 * Functionality provided by this class includes:
 * - Retrieving customer information
 * - Adding new rooms to the system
 * - Retrieving all rooms and customers
 * - Displaying all reservations in the system
 * 
 * The class follows the singleton design pattern to ensure a single instance is used throughout the system.
 */
public class AdminResource {
    // Singleton instance of AdminResource
    private static final AdminResource INSTANCE = new AdminResource();
    // Instance of CustomerService to manage customer-related operations
    private final CustomerService customerService = CustomerService.getInstance();
    // Instance of ReservationService to manage reservation-related operations
    private final ReservationService reservationService = ReservationService.getInstance();

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Ensures that only the singleton instance is used.
     */
    private AdminResource() {}

    /**
     * Returns the singleton instance of `AdminResource`.
     * 
     * @return the single instance of `AdminResource`
     */
    public static AdminResource getInstance() {
        return INSTANCE;
    }

    /**
     * Retrieves a customer by their email.
     * 
     * @param email the email address of the customer to retrieve
     * @return the Customer object if found, or null if not found
     */
    public Customer getCustomer(String email) {
        return customerService.getCustomer(email);
    }

    /**
     * Adds a list of rooms to the system.
     * 
     * @param rooms a list of rooms to be added
     */
    public void addRoom(List<IRoom> rooms) {
        for (IRoom room : rooms) {
            reservationService.addRoom(room); // Add each room to the reservation service
        }
    }

    /**
     * Retrieves all rooms in the system.
     * 
     * @return a collection of all rooms
     */
    public Collection<IRoom> getAllRooms() {
        // Find all rooms (dummy dates used to fetch the entire room list)
        return reservationService.findRooms(new Date(), new Date());
    }

    /**
     * Retrieves all customers in the system.
     * 
     * @return a collection of all customers
     */
    public Collection<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    /**
     * Displays all reservations in the system by printing them to the console.
     */
    public void displayAllReservations() {
        reservationService.printAllReservation(); // Print all reservations
    }
}
