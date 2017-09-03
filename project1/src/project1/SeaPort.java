package project1;

import java.util.ArrayList;

public class SeaPort extends Thing {
	private ArrayList<Dock> docks;
	private ArrayList<Ship> que; // list of ships waiting to dock
	private ArrayList<Ship> ships; // list of all ships at port
	private ArrayList<Person> persons; // people with skills at this port
}
