package project1;

import java.util.ArrayList;
import java.util.Scanner;

public class World extends Thing {
	private ArrayList<SeaPort> ports = new ArrayList<SeaPort>();
	private PortTime time;
	
	public World() {
		super();
	}
	
	public void process(String st) {
		System.out.println("Processing > " + st);
		
	    Scanner sc = new Scanner(st);
	    
	    if (!sc.hasNext()) {
	    	sc.close();
	    	return;
	    }
	    
	    switch (sc.next()) {
	    	case "port": 
	    		ports.add(new SeaPort(sc));		
	            break;
	    	case "dock":
	    		assignDock(new Dock(sc));
	    		break;
	    	case "pship":
	    		assignShip(new PassengerShip(sc));
	    		break;
	    	case "cship":
	    		assignShip(new CargoShip(sc));	    		
	    		break;
	    	case "person":
	    		assignPerson(new Person(sc));
	    		break;
	        default:
	        	break;
	    }
	    
	    sc.close();
	}
		
	public SeaPort getSeaPortByIndex(int index) {
		for(SeaPort msp: ports) {
			if(msp.getIndex() == index) {
				return msp;
			}
		}
		return null;
	}
	
	public Dock getDockByIndex(int index) {
		for(SeaPort msp: ports) {
			ArrayList<Dock> docks = msp.getDocks();
			for(Dock md: docks){
				if(md.getIndex() == index) {
					return md;
				}
			}
		}
		return null;
	}
	
	public Ship getShipByIndex(int index) {
		for(SeaPort msp: ports) {
			ArrayList<Ship> ships = msp.getShips();
			for(Ship ms: ships){
				if(ms.getIndex() == index) {
					return ms;
				}
			}
		}
		return null;
	}
	
	public Person getPersonByIndex(Dock dock) {
		return null;
	}
	
	public void assignDock(Dock dock) {
		getSeaPortByIndex(dock.getParent()).getDocks().add(dock);
	}
	
	// link ship to parent
	public void assignShip(Ship ms) {
	    Dock md = getDockByIndex(ms.getParent());
	    if (md == null) {
	       getSeaPortByIndex(ms.getParent()).getShips().add(ms);
	       getSeaPortByIndex (ms.getParent()).getQue().add(ms);
	    }else {
	       md.setShip(ms);
	       getSeaPortByIndex(md.getParent()).getShips().add(ms);
	    }
	}
			
	public void assignPerson(Person person) {
		getSeaPortByIndex(person.getParent()).getPersons().add(person);
	}

	public String toString() {
		return ">>>>> The world:" + ports.get(0).toString();
	}	
}
