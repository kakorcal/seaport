package project1;

import java.util.Scanner;

public class Dock extends Thing {
	private Ship ship;
	
	public Dock(Scanner sc) {
		super(sc);
	}

	public String toString() {
		return "";
	}

	public Ship getShip() {
		return ship;
	}

	public void setShip(Ship ship) {
		this.ship = ship;
	}	
}
