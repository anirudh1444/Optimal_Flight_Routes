# Optimal_Flight_Routes

This program efficiently finds the shortest path between two airports given user-inputted constraints (price, number of layovers, dates, seating preferences, and most importantly, excluding Boeing 737s). It uses MongoDB to store all the airports and flights, avoiding the redundant work of rereading files. It supports both CSV file reading to quickly insert multiple airports quickly. A user can also create and delete their own airports or flights, allowing easy manipulation of the database. 
