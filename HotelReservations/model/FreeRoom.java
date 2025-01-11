package model;

/**
 * FreeRoom.java
 * This class represents a type of room that is free (i.e., has a price of $0.0).
 * It extends the `Room` class, inheriting its properties and behaviors.
 * The `FreeRoom` class overrides the `toString()` method to indicate that the price is "FREE".
 */
public class FreeRoom extends Room {

    /**
     * Constructor for creating a new FreeRoom object.
     * 
     * @param roomNumber the room number for the free room
     * @param roomType   the type of the room (SINGLE or DOUBLE)
     */
    public FreeRoom(String roomNumber, RoomType roomType) {
        // Call the parent `Room` constructor with a price of 0.0
        super(roomNumber, 0.0, roomType);
    }

    /**
     * Provides a string representation of the FreeRoom object.
     * Overrides the `toString()` method to display "price=FREE" instead of a numeric price.
     * 
     * @return a string containing the details of the free room
     */
    @Override
    public String toString() {
        return "FreeRoom{" +
                "roomNumber=\"" + getRoomNumber() + "\"" +
                ", roomType=" + getRoomType() +
                ", price=FREE" +
                '}';
    }
}
