package project4;

import java.util.Scanner;

public class Person extends Thing {
	
	private int portIndex;
	private String skill;
	
	public Person(Scanner sc) {
		super(sc);
		this.skill = sc.next();
		this.portIndex = this.getParent();
	}

	public String getSkill() {
		return skill;
	}

	public void setSkill(String skill) {
		this.skill = skill;
	}

	public String toString() {
		return "Person: " + super.toString() + " " + skill;
	}

	public int getPortIndex() {
		return portIndex;
	}

	public void setPortIndex(int portIndex) {
		this.portIndex = portIndex;
	}	
}
