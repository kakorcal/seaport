package project4;

import java.util.ArrayList;
import java.util.Scanner;

public class Dock extends Thing {
	
	private int portIndex = -1;
	private int shipIndex = -1;
	private ArrayList<Job> jobs = new ArrayList<Job>();
	
	public Dock(Scanner sc) {
		super(sc);
		this.portIndex = this.getParent();
		this.shipIndex = sc.nextInt();
		System.out.println("dock shipIndex " + shipIndex);
	}

	public String toString() {
		return "Dock: " + super.toString();
	}

	public int getPortIndex() {
		return portIndex;
	}

	public void setPortIndex(int portIndex) {
		this.portIndex = portIndex;
	}

	public int getShipIndex() {
		return shipIndex;
	}

	public void setShipIndex(int shipIndex) {
		this.shipIndex = shipIndex;
	}

	public ArrayList<Job> getJobs() {
		return jobs;
	}

	public void setJobs(ArrayList<Job> jobs) {
		this.jobs = jobs;
	}
}
