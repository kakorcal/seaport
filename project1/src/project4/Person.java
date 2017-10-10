package project4;

import java.util.Scanner;

import javax.swing.table.DefaultTableModel;

public class Person extends Thing {
	private static final int COLUMN_PERSON = 0;
	private static final int COLUMN_SKILL = 1;
	private static final int COLUMN_PORT = 2;
	private static final int COLUMN_DOCK = 3;
	private static final int COLUMN_SHIP = 4;
	private static final int COLUMN_JOB = 5;
	private static final int COLUMN_REQUIREMENTS = 6;
	private static final int COLUMN_STATUS = 7;
	private static final int COLUMN_PERSON_INDEX = 8;
	private static final int COLUMN_PORT_INDEX = 9;
	private static final int COLUMN_DOCK_INDEX = 10;
	private static final int COLUMN_SHIP_INDEX = 11;
	private static final int COLUMN_JOB_INDEX = 12;
	private static final String NONE = "NONE";
	private static final String TERMINATED = "--";
	
	private int portIndex = -1;
	private String skill;
	private SeaPort port = null;
	private DefaultTableModel personTableModel = null;
	private int personTableRow = -1;
	
	public Person(Scanner sc, DefaultTableModel personTableModel, int personTableRow) {
		super(sc);
		this.skill = sc.next();
		this.portIndex = this.getParent();
		this.personTableModel = personTableModel;
		this.personTableRow = personTableRow;
	}
	
	public void buildPerson() {
		Object[] rowData = new Object[13];
		rowData[COLUMN_PERSON] = this.getName();
		rowData[COLUMN_SKILL] = this.getSkill(); 
		rowData[COLUMN_PORT] = port.getName();
		rowData[COLUMN_DOCK] = NONE;
		rowData[COLUMN_SHIP] = NONE;
		rowData[COLUMN_JOB] = NONE;
		rowData[COLUMN_REQUIREMENTS] = NONE;
		rowData[COLUMN_STATUS] = TERMINATED;
		rowData[COLUMN_PERSON_INDEX] = this.getIndex();
		rowData[COLUMN_PORT_INDEX] = port.getIndex();
		rowData[COLUMN_DOCK_INDEX] = NONE;
		rowData[COLUMN_SHIP_INDEX] = NONE;
		rowData[COLUMN_JOB_INDEX] = NONE;
		personTableModel.addRow(rowData);
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

	public SeaPort getPort() {
		return port;
	}

	public void setPort(SeaPort port) {
		this.port = port;
	}

	public DefaultTableModel getPersonTableModel() {
		return personTableModel;
	}

	public void setPersonTableModel(DefaultTableModel personTableModel) {
		this.personTableModel = personTableModel;
	}

	public int getPersonTableRow() {
		return personTableRow;
	}

	public void setPersonTableRow(int personTableRow) {
		this.personTableRow = personTableRow;
	}	
}
