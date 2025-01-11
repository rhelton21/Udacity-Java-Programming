package model;

import java.util.Date;

/**
 * Reservation.java
 * This class represents a reservation in the hotel reservation system.
 * A reservation associates a customer with a specific room and includes the check-in and check-out dates.
 */
public class Reservation {
    // The customer who made the reservation
    private final Customer customer;
    // The room reserved by the customer
    private final IRoom room;
    // The check-in date for the reservation
    private final Date checkInDate;
    // The check-out date for the reservation
    private final Date checkOutDate;

    /**
     * Constructor for creating a new Reservation object.
     * 
     * @param customer     the customer making the reservation
     * @param room         the room reserved
     * @param checkInDate  the check-in date for the reservation
     * @param checkOutDate the check-out date for the reservation
     */
    public Reservation(Customer customer, IRoom room, Date checkInDate, Date checkOutDate) {
        this.customer = customer;           // Assign customer to the reservation
        this.room = room;                   // Assign room to the reservation
        this.checkInDate = checkInDate;      // Assign check-in date
        this.checkOutDate = checkOutDate;    // Assign check-out date
    }

    /**
     * Gets the customer associated with this reservation.
     * 
     * @return the customer who made the reservation
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Gets the room associated with this reservation.
     * 
     * @return the reserved room
     */
    public IRoom getRoom() {
        return room;
    }

    /**
     * Gets the check-in date of the reservation.
     * 
     * @return the check-in date
     */
    public Date getCheckInDate() {
        return checkInDate;
    }

    /**
     * Gets the check-out date of the reservation.
     * 
     * @return the check-out date
     */
    public Date getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * Provides a string representation of the Reservation object.
     * 
     * @return a string containing the reservation details (customer, room, check-in, and check-out dates)
     */
    @Override
    public String toString() {
        return "Reservation{" +
                "customer=" + customer +
                ", room=" + room +
                ", checkInDate=" + checkInDate +
                ", checkOutDate=" + checkOutDate +
                '}';
    }
}
