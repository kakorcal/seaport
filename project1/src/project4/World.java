package project4;

import java.util.HashMap;
import java.util.Scanner;

public class World extends Thing {
	
	private static final int MAX_PORT_INDEX = 19999;
	private static final int MAX_DOCK_INDEX = 29999;
	private HashMap<Integer, SeaPort> ports = new HashMap<Integer, SeaPort>();
	private HashMap<Integer, Thing> items = new HashMap<Integer, Thing>(); 
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
	    		addPort(new SeaPort(sc));
	            break;
	    	case "dock":
	    		addDock(new Dock(sc));
	    		break;
	    	case "pship":
	    		addShip(new PassengerShip(sc));	    		
	    		break;
	    	case "cship":
	    		addShip(new CargoShip(sc));	    		
	    		break;
	    	case "person":
	    		addPerson(new Person(sc));
	    		break;
	    	case "job":
	    		addJob(new Job(sc));
	    		break;
	        default:
	        	break;
	    }
	    
	    sc.close();
	}
	
	public void addPort(SeaPort port) {
		ports.put(port.getIndex(), port);
		items.put(port.getIndex(), port);
	}
	
	public void addDock(Dock dock) {
		SeaPort port = ports.get(dock.getPortIndex());
		port.getDocks().put(dock.getIndex(), dock);
		items.put(dock.getIndex(), dock);
	}
	
	public void addShip(Ship ship) {
		SeaPort port = ports.get(ship.getPortIndex());
		
		if(port != null) {
			port.getShips().put(ship.getIndex(), ship);
			port.getQueue().add(ship.getIndex());
		}else {
			// find dock first then derive port
			Thing dock = items.get(ship.getDockIndex());
			port = ports.get(dock.getParent());
			port.getShips().put(ship.getIndex(), ship);
		}
		items.put(ship.getIndex(), ship);
	}
	
	public void addPerson(Person person) {
		SeaPort port = ports.get(person.getPortIndex());
		port.getPersons().put(person.getIndex(), person);
		items.put(person.getIndex(), person);
	}
	
	public void addJob(Job job) {
		// need to find ship and add the job
		Thing dock = items.get(job.getDockIndex());
		
		if(dock != null) {
			// in this case, we will run the same jobs for every ship that goes to the dock
			ports.get(dock.getParent())
				 .getDocks()
				 .get(dock.getIndex())
				 .getJobs()
				 .add(job);
		}else {
			Thing ship = items.get(job.getShipIndex());
			
			int parent = ship.getParent();
			if(parent <= MAX_PORT_INDEX) {
				ports.get(parent)
					 .getShips()
					 .get(ship.getIndex())
					 .getJobs()
					 .add(job);
			}else if (parent > MAX_PORT_INDEX && parent <= MAX_DOCK_INDEX){
				dock = items.get(parent);
				
				ports.get(dock.getParent())
					 .getShips()
					 .get(ship.getIndex())
					 .getJobs()	
					 .add(job);
			}
		}
		
		items.put(job.getIndex(), job);
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
	
//	public void assignDock(Dock dock, HashMap<Integer, SeaPort> ports) {
//		SeaPort port = getSeaPortByIndex(dock.getParent(), ports);
//		port.getDocks().put(dock.getIndex(), dock);
//	}
	
	// link ship to parent
//	public void assignShip(Ship ship, HashMap<Integer, SeaPort> ports) {
//		Dock dock = null;
//		
//		for(Integer key: ports.keySet()) {
//			SeaPort port = ports.get(key);
//			HashMap<Integer, Dock> docks = port.getDocks();
//			dock = getDockByIndex(ship.getParent(), docks);
//			if(dock != null) break;			
//		}
//		
//	    if (dock == null) {
//	       getSeaPortByIndex(ship.getParent(), ports).getShips().put(ship.getIndex(), ship);
//	       getSeaPortByIndex(ship.getParent(), ports).getQue().put(ship.getIndex(), ship);
//	    }else {
//	       dock.setShip(ship);
//	       getSeaPortByIndex(dock.getParent(), ports).getShips().put(ship.getIndex(), ship);
//	    }
//	}
//			
//	public void assignPerson(Person person, HashMap<Integer, SeaPort> ports) {
//		getSeaPortByIndex(person.getParent(), ports).getPersons().put(person.getIndex(), person);
//	}

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

	public HashMap<Integer, Thing> getItems() {
		return items;
	}

	public void setItems(HashMap<Integer, Thing> items) {
		this.items = items;
	}

	public PortTime getTime() {
		return time;
	}

	public void setTime(PortTime time) {
		this.time = time;
	}	
}
