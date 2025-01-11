package model;

/**
 * IRoom.java
 * This interface defines the blueprint for a room in the hotel reservation system.
 * Any class implementing this interface must provide implementations for the following methods:
 * - `getRoomNumber()`: Returns the room number.
 * - `getRoomPrice()`: Returns the price per night for the room.
 * - `getRoomType()`: Returns the type of the room (SINGLE or DOUBLE).
 * - `isFree()`: Returns whether the room is free (i.e., has a price of $0.0).
 */
public interface IRoom {

    /**
     * Gets the room number.
     * 
     * @return the room number as a string
     */
    String getRoomNumber();

    /**
     * Gets the price of the room.
     * 
     * @return the price per night for the room as a double
     */
    Double getRoomPrice();

    /**
     * Gets the type of the room.
     * 
     * @return the type of the room (SINGLE or DOUBLE)
     */
    RoomType getRoomType();

    /**
     * Checks if the room is free.
     * 
     * @return true if the room is free (i.e., price is $0.0), false otherwise
     */
    boolean isFree();
}
