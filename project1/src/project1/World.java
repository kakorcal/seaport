package project1;

import java.util.ArrayList;
import java.util.Scanner;

public class World extends Thing {
	private ArrayList<SeaPort> ports;
	private PortTime time;
	
	public World(Scanner sc) {
		super(sc);
	}
	
	public void process(String st) {
		
	}
	
	public Ship getShipByIndex(int idx) {
		return null;
	}
	
	// link ship to parent
	public void assignShip(Ship ms) {
		
	}

	public String toString() {
		return "";
	}	
}
