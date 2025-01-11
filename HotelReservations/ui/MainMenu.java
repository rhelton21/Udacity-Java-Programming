package ui;

import api.HotelResource;
import model.IRoom;
import model.Reservation;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;       // Import List interface
import java.util.ArrayList;  // Import ArrayList implementation
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * MainMenu.java
 * This class represents the main menu of the hotel reservation application.
 * It allows users to interact with the system by selecting various options such as:
 * - Finding and reserving a room
 * - Viewing existing reservations
 * - Creating a new customer account
 * - Accessing the admin menu for additional features
 * 
 * The class uses the HotelResource to manage hotel-related services and interacts
 * with the user through console input and output.
 */
public class MainMenu {
    private static final HotelResource hotelResource = HotelResource.getInstance(); // Singleton instance to access hotel services
    private static final Scanner scanner = new Scanner(System.in); // Scanner to read user input
    private static final SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEE MMM dd yyyy"); // Date format for display
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"; // Regex pattern for email validation

    /**
     * Displays the main menu and handles user input.
     */
    public static void displayMainMenu() {
        String choice; // Variable to store user's menu choice
        do {
            // Display main menu options
            System.out.println("========== Main Menu ==========");
            System.out.println("1. Find and reserve a room");
            System.out.println("2. See my reservations");
            System.out.println("3. Create an account");
            System.out.println("4. Admin");
            System.out.println("5. Exit");
            System.out.print("Please enter your choice: ");
            choice = scanner.nextLine(); // Read the user's choice

            switch (choice) {
                case "1":
                    findAndReserveRoom(); // Call the findAndReserveRoom() method to handle room reservations
                    break;
                case "2":
                    seeMyReservations(); // Display the user's reservations
                    break;
                case "3":
                    createAccount(); // Call method to create an account
                    break;
                case "4":
                    AdminMenu.displayAdminMenu(); // Call AdminMenu for admin-related actions
                    break;
                case "5":
                    System.out.println("Exiting the application. Goodbye!");
                    break; // Exit the program
                default:
                    System.out.println("Error: Invalid Input");
                    break;
            }
        } while (!choice.equals("5")); // Loop until the user selects "Exit"
    }

    /**
     * Method to handle creating a customer account.
     */
    private static void createAccount() {
        System.out.println("Enter Email format: name@domain.com");
        String email = scanner.nextLine();
        while (!Pattern.matches(EMAIL_REGEX, email)) {
            System.out.println("Error: Invalid email format. Please try again.");
            System.out.println("Enter Email format: name@domain.com");
            email = scanner.nextLine();
        }

        System.out.println("First Name");
        String firstName = scanner.nextLine();
        System.out.println("Last Name");
        String lastName = scanner.nextLine();

        try {
            hotelResource.createACustomer(email, firstName, lastName);
            System.out.println("Account created successfully!");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Method to display the user's reservations.
     */
    private static void seeMyReservations() {
        System.out.println("Enter your email (format: name@domain.com): ");
        String email = scanner.nextLine();
        while (!Pattern.matches(EMAIL_REGEX, email)) {
            System.out.println("Error: Invalid email format. Please try again.");
            System.out.println("Enter your email (format: name@domain.com): ");
            email = scanner.nextLine();
        }

        List<Reservation> reservations = new ArrayList<>(hotelResource.getCustomersReservations(email));
        if (reservations.isEmpty()) {
            System.out.println("No reservations found.");
        } else {
            System.out.println("Your Reservations:");
            for (Reservation reservation : reservations) {
                System.out.println("Reservation for " + reservation.getCustomer().getFirstName() + " " + reservation.getCustomer().getLastName());
                System.out.println("Room: " + reservation.getRoom().getRoomNumber() + " - " + reservation.getRoom().getRoomType());
                System.out.println("Price: $" + reservation.getRoom().getRoomPrice() + " per night");
                System.out.println("Check-in Date: " + displayDateFormat.format(reservation.getCheckInDate()));
                System.out.println("Check-out Date: " + displayDateFormat.format(reservation.getCheckOutDate()));
                System.out.println("------------------------------------------------");
            }
        }
    }

    /**
     * Method to handle finding and reserving a room.
     */
    private static void findAndReserveRoom() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date checkInDate = null;
        Date checkOutDate = null;

        System.out.println("Enter Check-In Date (MM/dd/yyyy), example 02/01/2020: ");
        while (checkInDate == null) {
            try {
                checkInDate = dateFormat.parse(scanner.nextLine());
            } catch (ParseException e) {
                System.out.println("Error: Invalid Input. Please enter date in MM/dd/yyyy format.");
            }
        }

        System.out.println("Enter Check-Out Date (MM/dd/yyyy), example 02/10/2020: ");
        while (checkOutDate == null) {
            try {
                checkOutDate = dateFormat.parse(scanner.nextLine());
            } catch (ParseException e) {
                System.out.println("Error: Invalid Input. Please enter date in MM/dd/yyyy format.");
            }
        }

        List<IRoom> availableRooms = new ArrayList<>(hotelResource.findARoom(checkInDate, checkOutDate));

        if (availableRooms.isEmpty()) {
            System.out.println("No available rooms for the selected dates.");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(checkInDate);
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            Date newCheckInDate = calendar.getTime();

            calendar.setTime(checkOutDate);
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            Date newCheckOutDate = calendar.getTime();

            List<IRoom> recommendedRooms = new ArrayList<>(hotelResource.findARoom(newCheckInDate, newCheckOutDate));

            if (!recommendedRooms.isEmpty()) {
                System.out.println("\nRecommended rooms for alternative dates:");
                System.out.println("New Check-In Date: " + dateFormat.format(newCheckInDate));
                System.out.println("New Check-Out Date: " + dateFormat.format(newCheckOutDate));
                for (IRoom room : recommendedRooms) {
                    System.out.println("Room Number: " + room.getRoomNumber() + " " + room.getRoomType() + " Room Price: $" + room.getRoomPrice());
                }
            } else {
                System.out.println("No recommended rooms available.");
            }
        } else {
            System.out.println("Available rooms:");
            for (IRoom room : availableRooms) {
                System.out.println("Room Number: " + room.getRoomNumber() + " " + room.getRoomType() + " Room Price: $" + room.getRoomPrice());
            }

            System.out.println("Would you like to book a room? (Y/N): ");
            String bookRoomResponse = scanner.nextLine();
            if (bookRoomResponse.equalsIgnoreCase("y")) {
                System.out.println("Enter your email (format: name@domain.com): ");
                String email = scanner.nextLine();
                while (!Pattern.matches(EMAIL_REGEX, email)) {
                    System.out.println("Error: Invalid email format. Please enter a valid email.");
                    System.out.println("Enter your email (format: name@domain.com): ");
                    email = scanner.nextLine();
                }

                if (hotelResource.getCustomer(email) == null) {
                    System.out.println("Error: No customer found with this email. Please create an account first.");
                    return;
                }

                System.out.println("Enter the room number to reserve:");
                String roomNumber = scanner.nextLine();

                try {
                    Reservation reservation = hotelResource.bookARoom(email, hotelResource.getRoom(roomNumber), checkInDate, checkOutDate);
                    if (reservation != null) {
                        System.out.println("\nReservation Confirmed:");
                        System.out.println("Reservation for " + reservation.getCustomer().getFirstName() + " " + reservation.getCustomer().getLastName());
                        System.out.println("Room: " + reservation.getRoom().getRoomNumber() + " - " + reservation.getRoom().getRoomType());
                        System.out.println("Price: $" + reservation.getRoom().getRoomPrice() + " per night");
                        System.out.println("Check-in Date: " + displayDateFormat.format(checkInDate));
                        System.out.println("Check-out Date: " + displayDateFormat.format(checkOutDate));
                    } else {
                        System.out.println("Error: This room is already reserved for the selected dates.");
                    }
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                System.out.println("Returning to the main menu...");
            }
        }
    }
}
