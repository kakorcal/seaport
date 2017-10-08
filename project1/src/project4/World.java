package project4;

import java.util.HashMap;
import java.util.Scanner;

public class World extends Thing {
	private HashMap<Integer, SeaPort> ports = new HashMap<Integer, SeaPort>();
	private PortTime time;
	
	public World() {
		super();
	}
	
	// parses the line of string into individual class members
	public void process(String st) {
		System.out.println("Processing > " + st);
		
	    Scanner sc = new Scanner(st);
	    
	    if (!sc.hasNext()) {
	    	sc.close();
	    	return;
	    }
	    
	    switch (sc.next()) {
	    	case "port": 
	    		SeaPort seaport = new SeaPort(sc);
	    		ports.put(seaport.getIndex(), seaport);	
	            break;
	    	case "dock":
	    		assignDock(new Dock(sc), ports);
	    		break;
	    	case "pship":
	    		assignShip(new PassengerShip(sc), ports);
	    		break;
	    	case "cship":
	    		assignShip(new CargoShip(sc), ports);	    		
	    		break;
	    	case "person":
	    		assignPerson(new Person(sc), ports);
	    		break;
	        default:
	        	break;
	    }
	    
	    sc.close();
	}
	
	// the get and assign methods below are methods that help to create the data structure hierarchy
	public SeaPort getSeaPortByIndex(int index, HashMap<Integer, SeaPort> ports) {
		return ports.get(index);
	}
	
	public Dock getDockByIndex(int index, HashMap<Integer, Dock> docks) {
		return docks.get(index);
	}
	
	public Ship getShipByIndex(int index, HashMap<Integer, Ship> ships) {
		return ships.get(index);
	}
	
	public Person getPersonByIndex(Dock dock) {
		return null;
	}
	
	public void assignDock(Dock dock, HashMap<Integer, SeaPort> ports) {
		SeaPort port = getSeaPortByIndex(dock.getParent(), ports);
		port.getDocks().put(dock.getIndex(), dock);
	}
	
	// link ship to parent
	public void assignShip(Ship ship, HashMap<Integer, SeaPort> ports) {
		Dock dock = null;
		
		for(Integer key: ports.keySet()) {
			SeaPort port = ports.get(key);
			HashMap<Integer, Dock> docks = port.getDocks();
			dock = getDockByIndex(ship.getParent(), docks);
			if(dock != null) break;			
		}
		
	    if (dock == null) {
	       getSeaPortByIndex(ship.getParent(), ports).getShips().put(ship.getIndex(), ship);
	       getSeaPortByIndex(ship.getParent(), ports).getQue().put(ship.getIndex(), ship);
	    }else {
	       dock.setShip(ship);
	       getSeaPortByIndex(dock.getParent(), ports).getShips().put(ship.getIndex(), ship);
	    }
	}
			
	public void assignPerson(Person person, HashMap<Integer, SeaPort> ports) {
		getSeaPortByIndex(person.getParent(), ports).getPersons().put(person.getIndex(), person);
	}

	public String toString() {
		String st = ">>>>> The world:";
		for(SeaPort port: ports.values()) {
			st += port.toString();
		}
		
		return st;
	}

	public HashMap<Integer, SeaPort> getPorts() {
		return ports;
	}

	public void setPorts(HashMap<Integer, SeaPort> ports) {
		this.ports = ports;
	}

	public PortTime getTime() {
		return time;
	}

	public void setTime(PortTime time) {
		this.time = time;
	}	
}
