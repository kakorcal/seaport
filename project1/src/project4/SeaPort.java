package project4;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class SeaPort extends Thing {
	private HashMap<Integer, Dock> docks = new HashMap<Integer, Dock>();
	private HashMap<Integer, Ship> ships = new HashMap<Integer, Ship>(); // list of all ships at port
	private HashMap<Integer, Person> persons = new HashMap<Integer, Person>(); // people with skills at this port
	private Queue<Integer> queue = new LinkedList<Integer>(); // queue of ships waiting to dock
	private BlockingDeque<Person> pool = new LinkedBlockingDeque<Person>();
	
	public SeaPort(Scanner sc) {
		super(sc);
	}
	
	public String toString() {
		String st = "\n\n>>> SeaPort: " + super.toString();

	    for (Dock md: docks.values()) {
	    	st += "\n  " + md.toString();
	    	st += "\n    Ship: " + ships.get(md.getShipIndex()).toString();
	    }
	    
	    st += "\n\n  --- List of all ships in que:";
	    
	    for(int shipIndex: queue) {
	    	st += "\n  " + ships.get(shipIndex).toString(); 
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

	public Queue<Integer> getQueue() {
		return queue;
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

	public void setQueue(Queue<Integer> queue) {
		this.queue = queue;
	}

	public void setShips(HashMap<Integer, Ship> ships) {
		this.ships = ships;
	}

	public void setPersons(HashMap<Integer, Person> persons) {
		this.persons = persons;
	}

	public BlockingDeque<Person> getPool() {
		return pool;
	}

	public void setPool(BlockingDeque<Person> pool) {
		this.pool = pool;
	}
}
