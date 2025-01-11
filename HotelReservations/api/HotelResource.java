package api;

import model.*;
import service.CustomerService;
import service.ReservationService;
import java.util.Collection;
import java.util.Date;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * HotelResource.java
 * This class provides an API for customers to interact with the hotel reservation system.
 * It serves as a bridge between the user interface and the service layer for customer-related operations.
 * 
 * Functionality provided by this class includes:
 * - Retrieving customer information
 * - Creating a new customer account
 * - Retrieving room information
 * - Booking a room for a customer
 * - Retrieving customer reservations
 * - Finding available rooms for a specific date range
 * 
 * The class follows the singleton design pattern to ensure a single instance is used throughout the system.
 */
public class HotelResource {
    // Singleton instance of HotelResource
    private static final HotelResource INSTANCE = new HotelResource();
    // Instance of CustomerService to manage customer-related operations
    private final CustomerService customerService = CustomerService.getInstance();
    // Instance of ReservationService to manage reservation-related operations
    private final ReservationService reservationService = ReservationService.getInstance();
    // Email validation regex pattern
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Ensures that only the singleton instance is used.
     */
    private HotelResource() {}

    /**
     * Returns the singleton instance of `HotelResource`.
     * 
     * @return the single instance of `HotelResource`
     */
    public static HotelResource getInstance() {
        return INSTANCE;
    }

    /**
     * Validates the email format.
     * 
     * @param email the email to validate
     * @return true if the email format is valid, false otherwise
     */
    private boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * Retrieves a customer by their email.
     * 
     * @param email the email address of the customer to retrieve
     * @return the Customer object if found, or null if not found
     */
    public Customer getCustomer(String email) {
        if (!isValidEmail(email)) {
            System.out.println("Error: Invalid email format. Please enter a valid email.");
            return null;
        }

        Customer customer = customerService.getCustomer(email);
        if (customer == null) {
            System.out.println("Error: No customer found with email " + email);
        }
        return customer;
    }

    /**
     * Creates a new customer account.
     * 
     * @param email     the email address of the new customer
     * @param firstName the first name of the customer
     * @param lastName  the last name of the customer
     */
    public void createACustomer(String email, String firstName, String lastName) {
        if (!isValidEmail(email)) {
            System.out.println("Error: Invalid email format. Cannot create account.");
            return;
        }

        try {
            customerService.addCustomer(email, firstName, lastName); // Add the customer using CustomerService
            System.out.println("Account created successfully for " + firstName + " " + lastName);
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Retrieves a room by its room number.
     * 
     * @param roomNumber the room number
     * @return the IRoom object if found, or null if not found
     */
    public IRoom getRoom(String roomNumber) {
        IRoom room = reservationService.getARoom(roomNumber);
        if (room == null) {
            System.out.println("Error: No room found with number " + roomNumber);
        }
        return room;
    }

    /**
     * Books a room for a customer based on their email.
     * Prevents booking if the customer doesn't exist or if the room is unavailable.
     * 
     * @param customerEmail the email of the customer making the reservation
     * @param room          the room to be reserved
     * @param checkInDate   the check-in date for the reservation
     * @param checkOutDate  the check-out date for the reservation
     * @return the created Reservation object if successful, or null if the customer or room doesn't exist
     */
    public Reservation bookARoom(String customerEmail, IRoom room, Date checkInDate, Date checkOutDate) {
        if (!isValidEmail(customerEmail)) {
            System.out.println("Error: Invalid email format. Cannot proceed with booking.");
            return null;
        }

        Customer customer = customerService.getCustomer(customerEmail); // Get the customer by email
        if (customer == null) {
            System.out.println("Error: Cannot book a room. No customer found with email " + customerEmail);
            return null; // Return null if the customer doesn't exist
        }

        if (room == null) {
            System.out.println("Error: The room to book does not exist.");
            return null;
        }

        Reservation reservation = reservationService.reserveARoom(customer, room, checkInDate, checkOutDate);
        if (reservation == null) {
            System.out.println("Error: Room " + room.getRoomNumber() + " is already reserved for the selected dates.");
        } else {
            System.out.println("Reservation successful for " + customer.getFirstName() + " " + customer.getLastName());
        }
        return reservation;
    }

    /**
     * Retrieves all reservations made by a specific customer.
     * 
     * @param customerEmail the email of the customer
     * @return a collection of the customer's reservations
     */
    public Collection<Reservation> getCustomersReservations(String customerEmail) {
        if (!isValidEmail(customerEmail)) {
            System.out.println("Error: Invalid email format. Cannot retrieve reservations.");
            return Collections.emptyList();
        }

        Customer customer = customerService.getCustomer(customerEmail); // Get the customer by email
        if (customer == null) {
            System.out.println("Error: No reservations found for the email " + customerEmail);
            return Collections.emptyList(); // Return empty list if no customer is found
        }
        return reservationService.getCustomersReservation(customer); // Retrieve their reservations
    }

    /**
     * Finds all available rooms for the given date range.
     * 
     * @param checkInDate  the check-in date
     * @param checkOutDate the check-out date
     * @return a collection of available rooms for the specified date range
     */
    public Collection<IRoom> findARoom(Date checkInDate, Date checkOutDate) {
        Collection<IRoom> availableRooms = reservationService.findRooms(checkInDate, checkOutDate);
        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms for the selected dates.");
        }
        return availableRooms;
    }
}
