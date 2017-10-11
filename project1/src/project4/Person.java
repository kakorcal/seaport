package project4;

import java.util.Scanner;

import javax.swing.table.DefaultTableModel;

public class Person extends Thing {
	
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
		rowData[Constant.PERSON_NAME] = this.getName();
		rowData[Constant.PERSON_SKILL] = this.getSkill(); 
		rowData[Constant.PERSON_PORT] = port.getName();
		rowData[Constant.PERSON_DOCK] = Constant.IDLE;
		rowData[Constant.PERSON_SHIP] = Constant.IDLE;
		rowData[Constant.PERSON_JOB] = Constant.IDLE;
		rowData[Constant.PERSON_REQUIREMENTS] = Constant.IDLE;
		rowData[Constant.PERSON_STATUS] = Constant.PERSON_UNASSIGNED_JOB;
		rowData[Constant.PERSON_INDEX] = this.getIndex();
		rowData[Constant.PERSON_PORT_INDEX] = port.getIndex();
		rowData[Constant.PERSON_DOCK_INDEX] = Constant.NONE;
		rowData[Constant.PERSON_SHIP_INDEX] = Constant.NONE;
		rowData[Constant.PERSON_JOB_INDEX] = Constant.NONE;
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
