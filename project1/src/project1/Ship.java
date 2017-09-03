package project1;

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
	}

	public String toString() {
		return "";
	}	
}
