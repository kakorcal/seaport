package project2;

import java.util.Scanner;

public class Thing implements Comparable<Thing> {
	private String name;
	private int index;
	private int parent;
	
	// default constructor
	public Thing() {
		this.name = null;
		this.index = 0;
		this.parent = 0;
	}
	
	public Thing(Scanner sc) {
		this.name = sc.next();
		this.index = sc.nextInt();
		this.parent = sc.nextInt();
	}

	public int compareTo(Thing o) {
		return name.compareToIgnoreCase(o.name);
	}
	
	public String toString() {
		return name + " " + index;
	}

	public String getName() {
		return name;
	}

	public int getIndex() {
		return index;
	}

	public int getParent() {
		return parent;
	}
}
