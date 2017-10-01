package project3;

import java.util.ArrayList;
import java.util.Scanner;

public class Job extends Thing {
	private double duration;
	private ArrayList<String> requirements = new ArrayList<String>();
	
	public Job(Scanner sc) {
		super(sc);
		this.duration = sc.nextDouble();
		
		while(sc.hasNext()) {
			requirements.add(sc.next());
		}		
	}

	public String toString() {
		String st = "Job: " + super.toString();
		
		if(!requirements.isEmpty()) {
			for(int i = 0; i < requirements.size(); i++) {
				st += " " + requirements.get(i);
			}
		}
		
		return st;
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
