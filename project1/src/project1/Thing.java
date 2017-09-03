package project1;

public class Thing implements Comparable<Thing> {
	
	private int index;
	private String name;
	private int parent;

	@Override
	public int compareTo(Thing o) {
		return 0;
	}
}
