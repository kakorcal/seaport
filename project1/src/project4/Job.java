package project4;

import java.util.ArrayList;
import java.util.Scanner;

public class Job extends Thing {
	
	private static final int MAX_PORT_INDEX = 19999;
	private static final int MAX_DOCK_INDEX = 29999;
	private static final int MAX_SHIP_INDEX = 49999;
	private int dockIndex;
	private int shipIndex;
	private double duration;
	private ArrayList<String> requirements = new ArrayList<String>();
	
	public Job(Scanner sc) {
		super(sc);
		
		this.duration = sc.nextDouble();
		
		while(sc.hasNext()) {
			requirements.add(sc.next());
		}
		
		int parent = this.getParent();
		if(parent > MAX_PORT_INDEX && parent <= MAX_DOCK_INDEX) {
			this.dockIndex = parent;
		}else if (parent > MAX_DOCK_INDEX && parent <= MAX_SHIP_INDEX){
			this.shipIndex = parent;
		}
	}

	public String toString() {
		return "";
	}

	public int getDockIndex() {
		return dockIndex;
	}

	public void setDockIndex(int dockIndex) {
		this.dockIndex = dockIndex;
	}

	public int getShipIndex() {
		return shipIndex;
	}

	public void setShipIndex(int shipIndex) {
		this.shipIndex = shipIndex;
	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public ArrayList<String> getRequirements() {
		return requirements;
	}

	public void setRequirements(ArrayList<String> requirements) {
		this.requirements = requirements;
	}	
}
