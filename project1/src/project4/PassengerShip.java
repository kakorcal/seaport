package project4;

import java.util.Scanner;

public class PassengerShip extends Ship {
	private int numberOfOccupiedRooms;
	private int numberOfPassengers;
	private int numberOfRooms;
	
	public PassengerShip(Scanner sc) {
		super(sc);
		this.numberOfOccupiedRooms = sc.nextInt();
		this.numberOfPassengers = sc.nextInt();
		this.numberOfRooms = sc.nextInt();
	}

	public String toString() {
		return "Passenger Ship: " + super.toString();
	}
	
	public String shipType() {
		return "Passenger Ship";
	}
	
	public int getNumberOfOccupiedRooms() {
		return numberOfOccupiedRooms;
	}

	public void setNumberOfOccupiedRooms(int numberOfOccupiedRooms) {
		this.numberOfOccupiedRooms = numberOfOccupiedRooms;
	}

	public int getNumberOfPassengers() {
		return numberOfPassengers;
	}

	public void setNumberOfPassengers(int numberOfPassengers) {
		this.numberOfPassengers = numberOfPassengers;
	}

	public int getNumberOfRooms() {
		return numberOfRooms;
	}

	public void setNumberOfRooms(int numberOfRooms) {
		this.numberOfRooms = numberOfRooms;
	}	
}
