package project4;

import java.util.ArrayList;
import java.util.Scanner;

public class Ship extends Thing {
	
	public static final String CARGO = "Cargo";
	public static final String PASSENGER = "Passenger";
	private static final int MAX_PORT_INDEX = 19999;
	private static final int MAX_DOCK_INDEX = 29999;
	private int portIndex = -1;
	private int dockIndex = -1;
	private PortTime arrivalTime;
	private PortTime dockTime;
	private double draft;
	private double length;
	private double weight;
	private double width;
	private ArrayList<Job> jobs = new ArrayList<Job>();
	
	public Ship(Scanner sc) {
		super(sc);
		this.weight = sc.nextDouble();
		this.length = sc.nextDouble();
		this.width = sc.nextDouble();
		this.draft = sc.nextDouble();
		
		int parent = this.getParent();
		if(parent <= MAX_PORT_INDEX) {
			this.portIndex = parent;
		}else if (parent > MAX_PORT_INDEX && parent <= MAX_DOCK_INDEX){
			this.dockIndex = parent;
		}
	}

	public String toString() {
		return super.toString();
	}
	
	public String shipType() {
		return null;
	}
	
	public int getPortIndex() {
		return portIndex;
	}

	public void setPortIndex(int portIndex) {
		this.portIndex = portIndex;
	}

	public int getDockIndex() {
		return dockIndex;
	}

	public void setDockIndex(int dockIndex) {
		this.dockIndex = dockIndex;
	}

	public String getShipDimensions() {
		String st = "  Weight: " + weight + "\n";
		st += "  Length: " + length + "\n";
		st += "  Draft: " + draft + "\n";
		st += "  Width: " + width + "\n";
		return st;
	}

	public PortTime getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(PortTime arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public PortTime getDockTime() {
		return dockTime;
	}

	public void setDockTime(PortTime dockTime) {
		this.dockTime = dockTime;
	}

	public double getDraft() {
		return draft;
	}

	public void setDraft(double draft) {
		this.draft = draft;
	}

	public double getLength() {
		return length;
	}

	public void setLength(double length) {
		this.length = length;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public ArrayList<Job> getJobs() {
		return jobs;
	}

	public void setJobs(ArrayList<Job> jobs) {
		this.jobs = jobs;
	}	
}
