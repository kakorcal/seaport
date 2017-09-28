package project3;

import java.util.ArrayList;
import java.util.Scanner;

public class Job extends Thing {
	private double duration;
	private ArrayList<String> requirements;
	
	public Job(Scanner sc) {
		super(sc);
	}

	public String toString() {
		return "";
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
