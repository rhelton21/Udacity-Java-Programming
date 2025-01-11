package ui;

import api.AdminResource;
import model.IRoom;
import model.Room;
import model.RoomType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * AdminMenu.java
 * This class represents the administrative menu of the hotel reservation application.
 * It provides options for administrators to:
 * - View all customer accounts
 * - View all rooms in the system
 * - View all reservations
 * - Add new rooms to the system
 * - Return to the main menu
 * 
 * The class interacts with the AdminResource to manage admin-related features and
 * handles user input via the console.
 */
public class AdminMenu {
    // Singleton instance of AdminResource to handle admin-related actions
    private static final AdminResource adminResource = AdminResource.getInstance();
    // Scanner to read input from the user
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Displays the admin menu and handles user input for various administrative actions.
     */
    public static void displayAdminMenu() {
        String choice; // Stores the user's choice
        do {
            // Display the admin menu options
            System.out.println("========== Admin Menu ==========");
            System.out.println("1. See all Customers");
            System.out.println("2. See all Rooms");
            System.out.println("3. See all Reservations");
            System.out.println("4. Add a Room");
            System.out.println("5. Back to Main Menu");
            System.out.print("Please enter your choice: ");
            choice = scanner.nextLine(); // Read the user's choice

            // Perform actions based on the user's choice
            switch (choice) {
                case "1":
                    System.out.println("All Customers:");
                    // Print all customers to the console
                    adminResource.getAllCustomers().forEach(System.out::println);
                    break;
                case "2":
                    System.out.println("All Rooms:");
                    // Print all rooms to the console
                    adminResource.getAllRooms().forEach(System.out::println);
                    break;
                case "3":
                    System.out.println("All Reservations:");
                    // Display all reservations
                    adminResource.displayAllReservations();
                    break;
                case "4":
                    // Call the method to add a room
                    addRoom();
                    break;
                case "5":
                    System.out.println("Returning to Main Menu...");
                    break;
                default:
                    // Handle invalid menu options
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (!choice.equals("5")); // Loop until the user chooses to return to the main menu
    }

    /**
     * Handles the process of adding rooms to the system.
     * Prompts the admin to enter room details (room number, price, type).
     * Allows adding multiple rooms until the admin chooses to stop.
     */
    private static void addRoom() {
        String continueAdding = "y"; // Default to "yes" for adding the first room

        while (continueAdding.equalsIgnoreCase("y")) {
            System.out.println("Enter room number: ");
            String roomNumber = scanner.nextLine(); // Read room number

            System.out.println("Enter room price: ");
            double price;
            while (true) {
                try {
                    // Parse the price input as a double
                    price = Double.parseDouble(scanner.nextLine());
                    break;
                } catch (NumberFormatException e) {
                    // Handle invalid price input
                    System.out.println("Invalid price. Please enter a numeric value: ");
                }
            }

            RoomType roomType = null;
            while (roomType == null) {
                System.out.println("Enter room type (1 for SINGLE, 2 for DOUBLE): ");
                String typeInput = scanner.nextLine(); // Read room type input
                switch (typeInput) {
                    case "1":
                        roomType = RoomType.SINGLE; // Assign SINGLE for input "1"
                        break;
                    case "2":
                        roomType = RoomType.DOUBLE; // Assign DOUBLE for input "2"
                        break;
                    default:
                        // Handle invalid room type input
                        System.out.println("Invalid input. Please enter 1 for SINGLE or 2 for DOUBLE.");
                        break;
                }
            }

            // Create a list of rooms to add to the system
            List<IRoom> rooms = new ArrayList<>();
            rooms.add(new Room(roomNumber, price, roomType));
            // Add the room using adminResource
            adminResource.addRoom(rooms);
            System.out.println("Room added successfully.");

            // Ask the user if they want to add another room
            while (true) {
                System.out.println("Would you like to add another room? (Y/N): ");
                continueAdding = scanner.nextLine();
                if (continueAdding.equalsIgnoreCase("y") || continueAdding.equalsIgnoreCase("n")) {
                    // Valid input, exit the prompt loop
                    break;
                } else {
                    // Handle invalid Y/N input
                    System.out.println("Invalid input. Please enter Y for yes or N for no.");
                }
            }
        }
    }
}
