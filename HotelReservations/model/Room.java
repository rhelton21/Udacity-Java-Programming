package model;

/**
 * Room.java
 * This class represents a room in the hotel reservation system.
 * A room has a room number, a price per night, and a room type (SINGLE or DOUBLE).
 * It implements the `IRoom` interface, providing the necessary method implementations.
 */
public class Room implements IRoom {
    // The unique number identifying the room
    private final String roomNumber;
    // The price of the room per night
    private final Double price;
    // The type of the room (SINGLE or DOUBLE)
    private final RoomType roomType;

    /**
     * Constructor for creating a new Room object.
     * 
     * @param roomNumber the unique room number
     * @param price      the price of the room per night
     * @param roomType   the type of the room (SINGLE or DOUBLE)
     */
    public Room(String roomNumber, Double price, RoomType roomType) {
        this.roomNumber = roomNumber;  // Assign room number
        this.price = price;            // Assign room price
        this.roomType = roomType;      // Assign room type
    }

    /**
     * Gets the room number.
     * 
     * @return the room number
     */
    @Override
    public String getRoomNumber() {
        return roomNumber;
    }

    /**
     * Gets the price of the room.
     * 
     * @return the price per night for the room
     */
    @Override
    public Double getRoomPrice() {
        return price;
    }

    /**
     * Gets the type of the room.
     * 
     * @return the type of the room (SINGLE or DOUBLE)
     */
    @Override
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * Checks if the room is free (i.e., has a price of $0.0).
     * 
     * @return true if the room is free, false otherwise
     */
    @Override
    public boolean isFree() {
        return price == 0.0; // Returns true if the price is 0.0
    }

    /**
     * Provides a string representation of the Room object.
     * 
     * @return a string containing the room details (room number, price, and type)
     */
    @Override
    public String toString() {
        return "Room{" +
                "roomNumber=\"" + roomNumber + "\"" +
                ", price=" + price +
                ", roomType=" + roomType +
                '}';
    }
}
