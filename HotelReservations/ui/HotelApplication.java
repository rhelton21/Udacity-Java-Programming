package ui;

/**
 * HotelApplication.java
 * This class serves as the entry point for the hotel reservation application.
 * It starts the application and displays the main menu to the user.
 * The `MainMenu.displayMainMenu()` method is called to allow the user to interact with
 * the application features such as finding rooms, making reservations, and admin options.
 */
public class HotelApplication {

    /**
     * The main method that starts the hotel reservation application.
     * Displays a welcome message and calls the main menu to display the options.
     * 
     * @param args Command line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Display a welcome message to the user
        System.out.println("Welcome to the Hotel Reservation Application");

        // Call the main menu to display options for user interaction
        MainMenu.displayMainMenu();
    }
}
