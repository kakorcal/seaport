package project1;

import java.util.Scanner;

public class Person extends Thing {
	private String skill;
	
	public Person(Scanner sc) {
		super(sc);
		this.skill = sc.next();
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
}
