package service;

import model.*;
import java.util.*;

/**
 * ReservationService.java
 * This class handles all reservation-related operations for the hotel reservation system.
 * It follows the singleton design pattern to ensure a single instance of the reservation service.
 * 
 * Functionality provided by this class includes:
 * - Adding rooms to the system
 * - Reserving rooms for customers
 * - Finding available rooms for a given date range
 * - Retrieving customer reservations
 * - Printing all reservations in the system
 */
public class ReservationService {
    // Singleton instance of ReservationService
    private static final ReservationService INSTANCE = new ReservationService();
    // Map to store rooms with the room number as the key
    private final Map<String, IRoom> rooms = new HashMap<>();
    // List to store all reservations
    private final List<Reservation> reservations = new ArrayList<>();

    /**
     * Private constructor to prevent instantiation from outside the class.
     * Ensures that only the singleton instance is used.
     */
    private ReservationService() {}

    /**
     * Returns the singleton instance of `ReservationService`.
     * 
     * @return the single instance of `ReservationService`
     */
    public static ReservationService getInstance() {
        return INSTANCE;
    }

    /**
     * Adds a room to the system.
     * 
     * @param room the room to be added
     */
    public void addRoom(IRoom room) {
        if (rooms.containsKey(room.getRoomNumber())) {
            System.out.println("Error: Room number " + room.getRoomNumber() + " already exists.");
        } else {
            rooms.put(room.getRoomNumber(), room); // Add the room to the map using its room number as the key
            System.out.println("Room " + room.getRoomNumber() + " added successfully.");
        }
    }

    /**
     * Retrieves a room from the system by its room number.
     * 
     * @param roomId the room number
     * @return the room if found, or null if not found
     */
    public IRoom getARoom(String roomId) {
        if (!rooms.containsKey(roomId)) {
            System.out.println("Error: Room " + roomId + " does not exist.");
        }
        return rooms.get(roomId);
    }

    /**
     * Creates a reservation for a customer for a specified room and date range.
     * Ensures that the room is not already reserved for the same date range.
     * 
     * @param customer     the customer making the reservation
     * @param room         the room to be reserved
     * @param checkInDate  the check-in date
     * @param checkOutDate the check-out date
     * @return the created reservation, or null if the room is already reserved for the date range
     */
    public Reservation reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {
        if (customer == null) {
            System.out.println("Error: Customer is null. Cannot proceed with reservation.");
            return null;
        }
        if (room == null) {
            System.out.println("Error: Room does not exist.");
            return null;
        }

        // Check for overlapping reservations
        for (Reservation reservation : reservations) {
            if (reservation.getRoom().equals(room) &&
                !(checkOutDate.before(reservation.getCheckInDate()) || checkInDate.after(reservation.getCheckOutDate()))) {
                System.out.println("Error: The room " + room.getRoomNumber() + " is already reserved for the selected dates.");
                return null; // Room is already reserved for the same date range
            }
        }

        // Create and add the reservation if the room is available
        Reservation reservation = new Reservation(customer, room, checkInDate, checkOutDate);
        reservations.add(reservation);
        System.out.println("Reservation confirmed for room " + room.getRoomNumber() + " from " + checkInDate + " to " + checkOutDate);
        return reservation;
    }

    /**
     * Finds all available rooms for the given date range.
     * 
     * @param checkInDate  the check-in date
     * @param checkOutDate the check-out date
     * @return a collection of available rooms
     */
    public Collection<IRoom> findRooms(Date checkInDate, Date checkOutDate) {
        List<IRoom> availableRooms = new ArrayList<>(rooms.values());
        for (Reservation reservation : reservations) {
            // Check if the reservation overlaps with the requested date range
            if (!(checkOutDate.before(reservation.getCheckInDate()) || checkInDate.after(reservation.getCheckOutDate()))) {
                availableRooms.remove(reservation.getRoom());
            }
        }

        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available for the selected dates.");
        }
        return availableRooms; // Return the list of available rooms
    }

    /**
     * Retrieves all reservations made by a specific customer.
     * 
     * @param customer the customer whose reservations are to be retrieved
     * @return a collection of the customer's reservations
     */
    public Collection<Reservation> getCustomersReservation(Customer customer) {
        if (customer == null) {
            System.out.println("Error: Customer does not exist. Cannot retrieve reservations.");
            return Collections.emptyList();
        }

        List<Reservation> customerReservations = new ArrayList<>();
        for (Reservation reservation : reservations) {
            if (reservation.getCustomer().equals(customer)) {
                customerReservations.add(reservation); // Add the reservation if it belongs to the customer
            }
        }

        if (customerReservations.isEmpty()) {
            System.out.println("No reservations found for " + customer.getFirstName() + " " + customer.getLastName());
        }
        return customerReservations;
    }

    /**
     * Prints all reservations in the system to the console.
     */
    public void printAllReservation() {
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            System.out.println("All Reservations:");
            for (Reservation reservation : reservations) {
                System.out.println(reservation);
            }
        }
    }
}
