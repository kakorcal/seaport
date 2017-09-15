package project2;

import java.util.ArrayList;
import java.util.Scanner;

public class SeaPort extends Thing {
	private ArrayList<Dock> docks = new ArrayList<Dock>();
	private ArrayList<Ship> que = new ArrayList<Ship>(); // list of ships waiting to dock
	private ArrayList<Ship> ships = new ArrayList<Ship>(); // list of all ships at port
	private ArrayList<Person> persons = new ArrayList<Person>(); // people with skills at this port
	
	public SeaPort(Scanner sc) {
		super(sc);
	}

	public String toString() {
		String st = "\n\n>>> SeaPort: " + super.toString();

	    for (Dock md: docks) {
	    	st += "\n  " + md.toString();
	    	st += "\n    Ship: " + md.getShip().toString();
	    }
	    
	    st += "\n\n  --- List of all ships in que:";
	    
	    for (Ship ms: que) {
	    	st += "\n  " + ms.toString();	    	
	    }
	    
	    st += "\n\n  --- List of all ships:";
	    
	    for (Ship ms: ships) {
	    	st += "\n  " + ms.toString();	    	
	    }
	    
	    st += "\n\n  --- List of all persons:";
	    
	    for (Person mp: persons) {
	    	st += "\n  " + mp.toString();	    	
	    }
	    
	    return st;
	}

	public ArrayList<Dock> getDocks() {
		return docks;
	}

	public ArrayList<Ship> getQue() {
		return que;
	}

	public ArrayList<Ship> getShips() {
		return ships;
	}

	public ArrayList<Person> getPersons() {
		return persons;
	}	
}
