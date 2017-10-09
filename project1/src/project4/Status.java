package project4;

import java.awt.Color;

public enum Status {
	JOB_RUNNING("RUNNING", Color.green),
	JOB_WAITING("WAITING", Color.yellow),
	JOB_SUSPENDED("SUSPENDED", Color.orange),
	JOB_DONE("DONE", Color.red),
	JOB_STOP("STOP", Color.white),
	JOB_CANCEL("CANCEL", Color.red),
	SHIP_RELEASED("RELEASED", Color.white),
	NONE("NONE", Color.white);
	
	private String status;
	private Color color;
	Status(String status, Color color) {
		this.status = status;
		this.color = color;
	}
	
	public String getStatus() {
		return status;
	}
	
	public void setStatus(String status) {
		this.status = status;
	}
	
	public Color getColor() {
		return color;
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
}
