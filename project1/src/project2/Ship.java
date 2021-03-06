package project2;

import java.util.ArrayList;
import java.util.Scanner;

public class Ship extends Thing {
	private PortTime arrivalTime;
	private PortTime dockTime;
	private double draft;
	private double length;
	private double weight;
	private double width;
	private ArrayList<Job> jobs;
	
	public Ship(Scanner sc) {
		super(sc);
		this.weight = sc.nextDouble();
		this.length = sc.nextDouble();
		this.width = sc.nextDouble();
		this.draft = sc.nextDouble();
	}

	public String toString() {
		return super.toString();
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
