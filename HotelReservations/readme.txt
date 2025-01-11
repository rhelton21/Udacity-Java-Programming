HomeReservations README

Project Overview
The HomeReservations project is a Java-based console application that allows users to make hotel room reservations, manage customer accounts, and perform admin-level functions such as viewing all customers, rooms, and reservations. The application is structured using object-oriented programming principles and demonstrates effective use of collections, polymorphism, exception handling, and user interaction.

Table of Contents:
1. Features
2. Class Structure
3. Instructions for Compilation and Execution
4. Menu Options
5. Error Handling
6. Key Points for Review
7. Additional Notes

1. Features
- Create customer accounts.
- Search for available rooms based on check-in and check-out dates.
- Book rooms and view reservation details.
- Admin features:
  - View all customer accounts.
  - View all rooms and reservations.
  - Add new rooms to the system.
- Prevent booking of the same room by different customers for overlapping dates.
- Validation of user input such as email addresses.

2. Class Structure
Model Classes:
Customer.java: Represents a customer with attributes for firstName, lastName, and email. Includes validation for the email format.
IRoom.java (Interface): Provides method signatures:
  - getRoomNumber()
  - getRoomPrice()
  - getRoomType()
  - isFree()
Room.java: Implements the IRoom interface. Attributes:
  - roomNumber: A String representing the room number.
  - price: A Double representing the room price.
  - roomType: A RoomType enumeration (SINGLE, DOUBLE).
FreeRoom.java: Subclass of Room. Sets price to 0.0 and overrides the toString() method.
Reservation.java: Represents a reservation with the following:
  - customer: A Customer object.
  - room: An IRoom object.
  - checkInDate and checkOutDate: Date objects for the reservation period.

Service Classes:
CustomerService.java:
  - addCustomer(String email, String firstName, String lastName)
  - getCustomer(String email)
  - getAllCustomers(): Returns a collection of customers.
ReservationService.java:
  - addRoom(IRoom room)
  - getARoom(String roomId)
  - reserveARoom(Customer customer, IRoom room, Date checkInDate, Date checkOutDate)
  - findRooms(Date checkInDate, Date checkOutDate): Searches for available rooms.
  - printAllReservation(): Prints all reservations.

API Classes:
HotelResource.java (For customers):
  - createACustomer(String email, String firstName, String lastName)
  - bookARoom(String customerEmail, IRoom room, Date checkInDate, Date checkOutDate)
  - getCustomersReservations(String customerEmail): Returns customer reservations.
AdminResource.java (For admin):
  - addRoom(List<IRoom> rooms)
  - getAllRooms()
  - getAllCustomers()
  - displayAllReservations()

UI Classes:
MainMenu.java:
  - Displays the main menu with options for room reservation, account creation, and admin features.
AdminMenu.java:
  - Displays the admin menu with options for viewing all customers, rooms, and reservations, and adding rooms.
HotelApplication.java:
  - The main driver class with the public static void main(String[] args) method to launch the application.

3. Instructions for Compilation and Execution
Compiling the Project:
Navigate to the project directory containing the source files.

Run the following commands:

javac -d . model/*.java service/*.java api/*.java ui/*.java

This will compile all Java files and create the necessary .class files.

Running the Project:
To run the program, execute the following:

java ui.HotelApplication

This will launch the hotel reservation application.

4. Menu Options
Main Menu:
1. Find and reserve a room:
   - Enter the check-in and check-out dates.
   - Displays available rooms.
   - Prompts to book a room and enter customer details.
2. See my reservations:
   - Displays all reservations for a specified email.
3. Create an account:
   - Prompts for email, first name, and last name to create a customer account.
4. Admin:
   - Opens the admin menu.
5. Exit:
   - Exits the application.

Admin Menu:
1. See all Customers:
   - Displays all customer details.
2. See all Rooms:
   - Displays details of all rooms.
3. See all Reservations:
   - Displays all reservations.
4. Add a Room:
   - Allows the admin to add a new room by specifying the room number, price, and type.
5. Back to Main Menu:
   - Returns to the main menu.

5. Error Handling
- Invalid Menu Inputs: Displays an error message: "Error: Invalid Input" and prompts again.
- Date Format Errors: Handles ParseException if dates are not entered in MM/dd/yyyy format.
- Email Validation: Throws an IllegalArgumentException if the email format is incorrect.
- Null Checks: Prevents operations on null objects.
- Reservation Conflicts: Prevents booking a room for overlapping dates.

6. Key Points for Review
- Object-Oriented Programming: Implements polymorphism with IRoom and inheritance (FreeRoom extends Room).
- Encapsulation: Private attributes with getter methods.
- Collections: Map and List are used for storing customers and reservations.
- Singleton Pattern: Service classes use static references to maintain a single instance.
- Switch Statements: Used for user input handling.
- Regular Expressions: Used in the Customer class to validate email input.

7. Additional Notes
- Testing the Project:
  - Run the Driver.java or HotelApplication.java to ensure the customer creation and email validation work as expected.
  - Try invalid emails to verify the IllegalArgumentException.

- Customization Ideas:
  - Add a feature to recommend rooms if no rooms are available for the selected dates.
  - Provide sample test data for customers and rooms.
  - Allow users to specify how far out the recommendation should search.

This concludes the README file for the HomeReservations project.
