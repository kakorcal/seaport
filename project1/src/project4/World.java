package project4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.JPanel;
import javax.swing.tree.DefaultMutableTreeNode;

public class World extends Thing {
	private HashMap<Integer, SeaPort> ports = new HashMap<Integer, SeaPort>();
	private PortTime time;
	
	public World() {
		super();
	}
	
	// parses the line of string into individual class members
	public void process(String st, JPanel jobsContainer) {
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
	    	case "job":
	    		assignJob(new Job(sc, jobsContainer, this), ports);
	    		break;
	        default:
	        	break;
	    }
	    
	    sc.close();
	}
	
	public void toTree(DefaultMutableTreeNode root) {
		for(Integer portKey: ports.keySet()) {
			SeaPort port = ports.get(portKey);
			HashMap<Integer, Dock> docks = port.getDocks();
			HashMap<Integer, Ship> ques = port.getQue();
			HashMap<Integer, Ship> ships = port.getShips();
			HashMap<Integer, Person> persons = port.getPersons();
			DefaultMutableTreeNode portNode = new DefaultMutableTreeNode("SeaPort " + port.getIndex());
			DefaultMutableTreeNode docksNode = new DefaultMutableTreeNode("Docks");
			DefaultMutableTreeNode quesNode = new DefaultMutableTreeNode("Que");
			DefaultMutableTreeNode shipsNode = new DefaultMutableTreeNode("Ships");
			DefaultMutableTreeNode personsNode = new DefaultMutableTreeNode("Persons");
			portNode.add(new DefaultMutableTreeNode(port.getName()));
			portNode.add(new DefaultMutableTreeNode("Index: " + port.getIndex()));
			
			for(Integer dockKey: docks.keySet()) {
				Dock dock = docks.get(dockKey);
				Ship ship = dock.getShip();
				
				DefaultMutableTreeNode dockNode = new DefaultMutableTreeNode(dock.getName());
				dockNode.add(new DefaultMutableTreeNode(dock.getName()));
				dockNode.add(new DefaultMutableTreeNode("Index: " + dock.getIndex()));
				dockNode.add(new DefaultMutableTreeNode("Parent: " + dock.getParent()));
				DefaultMutableTreeNode shipNode = new DefaultMutableTreeNode("Ship");
				DefaultMutableTreeNode shipTypeNode = new DefaultMutableTreeNode(ship.shipType());
				shipTypeNode.add(new DefaultMutableTreeNode(ship.getName()));
				shipTypeNode.add(new DefaultMutableTreeNode("Index: " + ship.getIndex()));
				shipTypeNode.add(new DefaultMutableTreeNode("Parent: " + ship.getParent()));
				shipTypeNode.add(new DefaultMutableTreeNode(ship.getDraft()));
				shipTypeNode.add(new DefaultMutableTreeNode(ship.getLength()));
				shipTypeNode.add(new DefaultMutableTreeNode(ship.getWeight()));
				shipTypeNode.add(new DefaultMutableTreeNode(ship.getWidth()));
				shipNode.add(shipTypeNode);				
				dockNode.add(shipNode);
				docksNode.add(dockNode);
			}
			
			for(Integer queKey: ques.keySet()) {
				Ship ship = ques.get(queKey);
				DefaultMutableTreeNode shipNode = new DefaultMutableTreeNode(ship.getName());
				shipNode.add(new DefaultMutableTreeNode(ship.getName()));
				shipNode.add(new DefaultMutableTreeNode("Index: " + ship.getIndex()));
				shipNode.add(new DefaultMutableTreeNode("Parent: " + ship.getParent()));
				shipNode.add(new DefaultMutableTreeNode(ship.getDraft()));
				shipNode.add(new DefaultMutableTreeNode(ship.getLength()));
				shipNode.add(new DefaultMutableTreeNode(ship.getWeight()));
				shipNode.add(new DefaultMutableTreeNode(ship.getWidth()));
				quesNode.add(shipNode);
			}
			
			for(Integer shipKey: ships.keySet()) {
				Ship ship = ships.get(shipKey);
				DefaultMutableTreeNode shipTypeNode = new DefaultMutableTreeNode(ship.getName());
				shipTypeNode.add(new DefaultMutableTreeNode(ship.shipType()));
				shipTypeNode.add(new DefaultMutableTreeNode(ship.getName()));
				shipTypeNode.add(new DefaultMutableTreeNode("Index: " + ship.getIndex()));
				shipTypeNode.add(new DefaultMutableTreeNode("Parent: " + ship.getParent()));
				shipTypeNode.add(new DefaultMutableTreeNode(ship.getDraft()));
				shipTypeNode.add(new DefaultMutableTreeNode(ship.getLength()));
				shipTypeNode.add(new DefaultMutableTreeNode(ship.getWeight()));
				shipTypeNode.add(new DefaultMutableTreeNode(ship.getWidth()));
				
				ArrayList<Job> jobs = ship.getJobs();
				if(!jobs.isEmpty()) {
					DefaultMutableTreeNode jobsNode = new DefaultMutableTreeNode("Jobs");
					
					for(Job job: jobs) {
						DefaultMutableTreeNode jobNode = new DefaultMutableTreeNode(job.getName());
						jobNode.add(new DefaultMutableTreeNode(job.getName()));
						jobNode.add(new DefaultMutableTreeNode("Index: " + job.getIndex()));
						jobNode.add(new DefaultMutableTreeNode("Parent: " + job.getParent()));
						jobNode.add(new DefaultMutableTreeNode(job.getDuration()));
						
						ArrayList<String> requirements = job.getRequirements();
						if(!requirements.isEmpty()) {
							String st = "";
							for(String requirement: requirements) {
								st += requirement + " ";
							}
							
							jobNode.add(new DefaultMutableTreeNode(st));
						}
						
						jobsNode.add(jobNode);
					}
					
					shipTypeNode.add(jobsNode);
				}
				
				shipsNode.add(shipTypeNode);
			}
			
			for(Integer personKey: persons.keySet()) {
				Person person = persons.get(personKey);
				DefaultMutableTreeNode personNode = new DefaultMutableTreeNode(person.getName());
				personNode.add(new DefaultMutableTreeNode(person.getName()));
				personNode.add(new DefaultMutableTreeNode("Index: " + person.getIndex()));
				personNode.add(new DefaultMutableTreeNode("Parent: " + person.getParent()));
				personNode.add(new DefaultMutableTreeNode(person.getSkill()));
				personsNode.add(personNode);
			}
			
			portNode.add(docksNode);
			portNode.add(quesNode);
			portNode.add(shipsNode);
			portNode.add(personsNode);
			root.add(portNode);
		}
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
	
	public void assignJob(Job job, HashMap<Integer, SeaPort> ports) {
		// need to find dock, get the ship, add job to ship
		Dock dock = null;
		
		for(Integer key: ports.keySet()) {
			SeaPort port = ports.get(key);
			HashMap<Integer, Dock> docks = port.getDocks();
			dock = getDockByIndex(job.getParent(), docks);
			if(dock != null) break;			
		}
		
		if(dock != null) {
			dock.getShip().getJobs().add(job);
		}else {
			Ship ship = null;
			
			for(Integer key: ports.keySet()) {
				SeaPort port = ports.get(key);
				HashMap<Integer, Ship> ships = port.getShips();
				ship = getShipByIndex(job.getParent(), ships);
				if(ship != null) break;			
			}
			
			if(ship != null) {
				ship.getJobs().add(job);
			}
		}
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
