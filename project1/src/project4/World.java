package project4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;

public class World extends Thing {
	
	private static final int MAX_PORT_INDEX = 19999;
	private static final int MAX_DOCK_INDEX = 29999;
	private HashMap<Integer, SeaPort> ports = new HashMap<Integer, SeaPort>();
	private HashMap<Integer, Thing> items = new HashMap<Integer, Thing>();
	private int jobCount = 0;
	private int personCount = 0;
	private PortTime time;
	
	public World() {
		super();
	}
	
	// parses the line of string into individual class members
	public void process(String st, DefaultTableModel jobTableModel, DefaultTableModel personTableModel) {
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
	    		addPerson(new Person(sc, personTableModel, personCount));
	    		personCount++;
	    		break;
	    	case "job":
	    		addJob(new Job(sc, jobTableModel, personTableModel, jobCount));
	    		jobCount++;
	    		break;
	        default:
	        	break;
	    }
	    
	    sc.close();
	}
	
	public void toTree(DefaultMutableTreeNode root) {
		for(int portKey: ports.keySet()) {
			SeaPort port = ports.get(portKey);
			HashMap<Integer, Dock> docks = port.getDocks();
			Queue<Integer> queue = port.getQueue();
			HashMap<Integer, Ship> ships = port.getShips();
			HashMap<Integer, Person> persons = port.getPersons();
			DefaultMutableTreeNode portNode = new DefaultMutableTreeNode("SeaPort " + port.getIndex());
			DefaultMutableTreeNode docksNode = new DefaultMutableTreeNode("Docks");
			DefaultMutableTreeNode queueNode = new DefaultMutableTreeNode("Queue");
			DefaultMutableTreeNode shipsNode = new DefaultMutableTreeNode("Ships");
			DefaultMutableTreeNode personsNode = new DefaultMutableTreeNode("Persons");
			portNode.add(new DefaultMutableTreeNode("Name: " + port.getName()));
			portNode.add(new DefaultMutableTreeNode("Index: " + port.getIndex()));
			
			for(int dockKey: docks.keySet()) {
				Dock dock = docks.get(dockKey);
				Ship ship = port.getShips().get(dock.getShipIndex());
				
				DefaultMutableTreeNode dockNode = new DefaultMutableTreeNode(dock.getName());
				dockNode.add(new DefaultMutableTreeNode("Name: " + dock.getName()));
				dockNode.add(new DefaultMutableTreeNode("Index: " + dock.getIndex()));
				dockNode.add(new DefaultMutableTreeNode("Parent: " + dock.getParent()));
				DefaultMutableTreeNode shipNode = new DefaultMutableTreeNode("Ship");
				String shipType = ship.shipType();
				DefaultMutableTreeNode shipTypeNode = new DefaultMutableTreeNode(shipType);
				shipTypeNode.add(new DefaultMutableTreeNode("Name: " + ship.getName()));
				shipTypeNode.add(new DefaultMutableTreeNode("Index: " + ship.getIndex()));
				shipTypeNode.add(new DefaultMutableTreeNode("Parent: " + ship.getParent()));
				shipTypeNode.add(new DefaultMutableTreeNode("Draft: " + ship.getDraft()));
				shipTypeNode.add(new DefaultMutableTreeNode("Length: " + ship.getLength()));
				shipTypeNode.add(new DefaultMutableTreeNode("Weight: " + ship.getWeight()));
				shipTypeNode.add(new DefaultMutableTreeNode("Width: " + ship.getWidth()));
				
				if(shipType.equals(Ship.CARGO)) {
					CargoShip cargoShip = (CargoShip) ship;
					shipNode.add(new DefaultMutableTreeNode("Type: " + shipType));
					shipTypeNode.add(new DefaultMutableTreeNode("Cargo value: " + cargoShip.getCargoValue()));
					shipTypeNode.add(new DefaultMutableTreeNode("Cargo volume: " + cargoShip.getCargoVolume()));
					shipTypeNode.add(new DefaultMutableTreeNode("Cargo weight: " + cargoShip.getCargoWeight()));
				}else if(shipType.equals(Ship.PASSENGER)) {
					PassengerShip passengerShip = (PassengerShip) ship;
					shipNode.add(new DefaultMutableTreeNode("Type: " + shipType));
					shipTypeNode.add(new DefaultMutableTreeNode("Number of occupied rooms: " + passengerShip.getNumberOfOccupiedRooms()));
					shipTypeNode.add(new DefaultMutableTreeNode("Number of passengers: " + passengerShip.getNumberOfPassengers()));
					shipTypeNode.add(new DefaultMutableTreeNode("Number of rooms: " + passengerShip.getNumberOfRooms()));
				}
				
				shipNode.add(shipTypeNode);				
				dockNode.add(shipNode);
				docksNode.add(dockNode);
			}
			
			for(int queueKey: queue) {
				Ship ship = port.getShips().get(queueKey);
				DefaultMutableTreeNode shipNode = new DefaultMutableTreeNode(ship.getName());
				shipNode.add(new DefaultMutableTreeNode("Name: " + ship.getName()));
				shipNode.add(new DefaultMutableTreeNode("Index: " + ship.getIndex()));
				shipNode.add(new DefaultMutableTreeNode("Parent: " + ship.getParent()));
				shipNode.add(new DefaultMutableTreeNode("Draft: " + ship.getDraft()));
				shipNode.add(new DefaultMutableTreeNode("Length: " + ship.getLength()));
				shipNode.add(new DefaultMutableTreeNode("Weight: " + ship.getWeight()));
				shipNode.add(new DefaultMutableTreeNode("Width: " + ship.getWidth()));
				
				String shipType = ship.shipType();
				if(shipType.equals(Ship.CARGO)) {
					CargoShip cargoShip = (CargoShip) ship;
					shipNode.add(new DefaultMutableTreeNode("Type: " + shipType));
					shipNode.add(new DefaultMutableTreeNode("Cargo value: " + cargoShip.getCargoValue()));
					shipNode.add(new DefaultMutableTreeNode("Cargo volume: " + cargoShip.getCargoVolume()));
					shipNode.add(new DefaultMutableTreeNode("Cargo weight: " + cargoShip.getCargoWeight()));
				}else if(shipType.equals(Ship.PASSENGER)) {
					PassengerShip passengerShip = (PassengerShip) ship;
					shipNode.add(new DefaultMutableTreeNode("Type: " + shipType));
					shipNode.add(new DefaultMutableTreeNode("Number of occupied rooms: " + passengerShip.getNumberOfOccupiedRooms()));
					shipNode.add(new DefaultMutableTreeNode("Number of passengers: " + passengerShip.getNumberOfPassengers()));
					shipNode.add(new DefaultMutableTreeNode("Number of rooms: " + passengerShip.getNumberOfRooms()));
				}
				
				queueNode.add(shipNode);
			}
			
			for(int shipKey: ships.keySet()) {
				Ship ship = ships.get(shipKey);
				DefaultMutableTreeNode shipNode = new DefaultMutableTreeNode(ship.getName());
				shipNode.add(new DefaultMutableTreeNode("Name: " + ship.getName()));
				shipNode.add(new DefaultMutableTreeNode("Index: " + ship.getIndex()));
				shipNode.add(new DefaultMutableTreeNode("Parent: " + ship.getParent()));
				shipNode.add(new DefaultMutableTreeNode("Draft: " + ship.getDraft()));
				shipNode.add(new DefaultMutableTreeNode("Length: " + ship.getLength()));
				shipNode.add(new DefaultMutableTreeNode("Weight: " + ship.getWeight()));
				shipNode.add(new DefaultMutableTreeNode("Width: " + ship.getWidth()));
				
				String shipType = ship.shipType();
				if(shipType.equals(Ship.CARGO)) {
					CargoShip cargoShip = (CargoShip) ship;
					shipNode.add(new DefaultMutableTreeNode("Type: " + shipType));
					shipNode.add(new DefaultMutableTreeNode("Cargo value: " + cargoShip.getCargoValue()));
					shipNode.add(new DefaultMutableTreeNode("Cargo volume: " + cargoShip.getCargoVolume()));
					shipNode.add(new DefaultMutableTreeNode("Cargo weight: " + cargoShip.getCargoWeight()));
				}else if(shipType.equals(Ship.PASSENGER)) {
					PassengerShip passengerShip = (PassengerShip) ship;
					shipNode.add(new DefaultMutableTreeNode("Type: " + shipType));
					shipNode.add(new DefaultMutableTreeNode("Number of occupied rooms: " + passengerShip.getNumberOfOccupiedRooms()));
					shipNode.add(new DefaultMutableTreeNode("Number of passengers: " + passengerShip.getNumberOfPassengers()));
					shipNode.add(new DefaultMutableTreeNode("Number of rooms: " + passengerShip.getNumberOfRooms()));
				}
				shipsNode.add(shipNode);
			}
			
			for(int personKey: persons.keySet()) {
				Person person = persons.get(personKey);
				DefaultMutableTreeNode personNode = new DefaultMutableTreeNode(person.getName());
				personNode.add(new DefaultMutableTreeNode("Name: " + person.getName()));
				personNode.add(new DefaultMutableTreeNode("Index: " + person.getIndex()));
				personNode.add(new DefaultMutableTreeNode("Parent: " + person.getParent()));
				personNode.add(new DefaultMutableTreeNode("Skill: " + person.getSkill()));
				personsNode.add(personNode);
			}
			
			portNode.add(docksNode);
			portNode.add(queueNode);
			portNode.add(shipsNode);
			portNode.add(personsNode);
			root.add(portNode);
		}
	}
	
	public void runJobs() {
		for(int portKey: ports.keySet()) {
			SeaPort port = ports.get(portKey);
			HashMap<Integer, Ship> ships = port.getShips();
						
			for(int shipKey: ships.keySet()) {
				Ship ship = ships.get(shipKey);
				ArrayList<Job> jobs = ship.getJobs();
				
				if(!jobs.isEmpty()) {
					for(Job job: jobs) {
						job.getThread().start();
					}
				}else {
					// create a job that immediately finishes so 
					// that another ship in the queue can goto dock
					int dockKey = ship.getDockIndex();
					if(dockKey != -1) {
						new Job(shipKey, dockKey, port).getThread().start();						
					}
				}
			}
		}
	}
	
	// the get and add methods below are methods that help to create the data structure hierarchy
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
		person.setPort(port);
		person.buildPerson();
	}
	
	public void addJob(Job job) {
		// need to find ship and add the job
		Thing dock = items.get(job.getDockIndex());
		SeaPort port = null;
		
		if(dock != null) {
			// in this case (aSPab.txt) all ships without a dock will not be assigned jobs
			port = ports.get(dock.getParent());
			
			int shipIndex = port.getDocks().get(dock.getIndex()).getShipIndex();
			
			job.setShipIndex(shipIndex);
			job.setDockIndex(-1);
			
			port.getShips()
				.get(shipIndex)
				.getJobs()
				.add(job);
		}else {
			Thing ship = items.get(job.getShipIndex());
			
			int parent = ship.getParent();
			if(parent <= MAX_PORT_INDEX) {
				port = ports.get(parent);
				
			    port.getShips()
					.get(ship.getIndex())
					.getJobs()
					.add(job);
			}else if (parent > MAX_PORT_INDEX && parent <= MAX_DOCK_INDEX){
				dock = items.get(parent);
				port = ports.get(dock.getParent());
				
				port.getShips()
					.get(ship.getIndex())
					.getJobs()	
					.add(job);
			}
		}
		
		items.put(job.getIndex(), job);
		job.setPort(port);
		job.buildJob();
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

	public int getJobCount() {
		return jobCount;
	}

	public void setJobCount(int jobCount) {
		this.jobCount = jobCount;
	}

	public int getPersonCount() {
		return personCount;
	}

	public void setPersonCount(int personCount) {
		this.personCount = personCount;
	}	
}
