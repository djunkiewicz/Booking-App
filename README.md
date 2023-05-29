# Booking-App
 A CRUD-type application used to manage apartment reservations, extended by the ability to generate PDF summary report containing the sum of bookings and revenues for each apartment in a given period.
 
 
 1. Functionalities:
- adding and deleting reservations,
- partial or full update of the reservation,
- downloading information about all reservations from the database,
- downloading information about reservations for a specific name,
- generating a PDF report containing detailed information about reservations and income in a given period of time.


2. Assumptions:
- data exchange format: JSON,
- use of embedded HSQL database,
- it is not possible to add two reservations for overlapping dates,
- editing the rental period of an existing reservation cannot collide with another reservation,
- you cannot delete or update a reservation that does not exist,
- you cannot add a reservation without crucial information, such as: rental period, apartment ID and tenant ID,
- if you do not specify the cost when adding a reservation, it will be calculated automatically based on the rental period.

The "Postman" software was used to test the application. Below are screenshots showing how to call some functions.

-> downloading information about all reservations from the database:
![getAllReservations](https://github.com/djunkiewicz/git-kurs/assets/121723243/3d7805f4-aab5-47b5-9ac7-4bf4b4ecaf06)

-> adding new reservation:
![saveNewReservation](https://github.com/djunkiewicz/git-kurs/assets/121723243/2c7a5ce4-cb60-47b8-aa84-f79d28636732)

-> generating a PDF report:
![Report](https://github.com/djunkiewicz/git-kurs/assets/121723243/48fec5e4-a07d-4b4d-bde2-9a006f956ff8)

-> downloading information about reservations for a specific name:
![getReservationsByTenantName](https://github.com/djunkiewicz/git-kurs/assets/121723243/3b2258da-724b-423e-b44c-b6c9a19e2f2c)

All functionalities made in the REST API architecture are:

![allFunctionalities](https://github.com/djunkiewicz/git-kurs/assets/121723243/a1b7c142-1565-45f4-9be1-099e2f180b54)

The above-mentioned functionalities are covered by unit tests in accordance with good programming practices. Libraries used in the test environment: JUnit5, Hamcrest.
