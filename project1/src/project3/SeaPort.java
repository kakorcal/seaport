package project3;

import java.util.HashMap;
import java.util.Scanner;

public class SeaPort extends Thing {
	private HashMap<Integer, Dock> docks = new HashMap<Integer, Dock>();
	private HashMap<Integer, Ship> que = new HashMap<Integer, Ship>(); // list of ships waiting to dock
	private HashMap<Integer, Ship> ships = new HashMap<Integer, Ship>(); // list of all ships at port
	private HashMap<Integer, Person> persons = new HashMap<Integer, Person>(); // people with skills at this port
	
	public SeaPort(Scanner sc) {
		super(sc);
	}

	public String toString() {
		String st = "\n\n>>> SeaPort: " + super.toString();

	    for (Dock md: docks.values()) {
	    	st += "\n  " + md.toString();
	    	st += "\n    Ship: " + md.getShip().toString();
	    }
	    
	    st += "\n\n  --- List of all ships in que:";
	    
	    for (Ship ms: que.values()) {
	    	st += "\n  " + ms.toString();	    	
	    }
	    
	    st += "\n\n  --- List of all ships:";
	    
	    for (Ship ms: ships.values()) {
	    	st += "\n  " + ms.toString();	    	
	    }
	    
	    st += "\n\n  --- List of all persons:";
	    
	    for (Person mp: persons.values()) {
	    	st += "\n  " + mp.toString();	    	
	    }
	    
	    return st;
	}

	public HashMap<Integer, Dock> getDocks() {
		return docks;
	}

	public HashMap<Integer, Ship> getQue() {
		return que;
	}

	public HashMap<Integer, Ship> getShips() {
		return ships;
	}

	public HashMap<Integer, Person> getPersons() {
		return persons;
	}

	public void setDocks(HashMap<Integer, Dock> docks) {
		this.docks = docks;
	}

	public void setQue(HashMap<Integer, Ship> que) {
		this.que = que;
	}

	public void setShips(HashMap<Integer, Ship> ships) {
		this.ships = ships;
	}

	public void setPersons(HashMap<Integer, Person> persons) {
		this.persons = persons;
	}	
}
