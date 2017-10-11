package project4;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.table.DefaultTableModel;

public class Job extends Thing implements Runnable {
	
	private int dockIndex = -1;
	private int shipIndex = -1;
	private double duration;
	private ArrayList<String> requirements = new ArrayList<String>();
	private ArrayList<String> requirementsCopy = null;
	private Thread thread = null;
	private SeaPort port = null;
	private int requiredResourcesCount = 0;
	private ArrayList<Person> resources = new ArrayList<Person>();
	private DefaultTableModel jobTableModel = null;
	private DefaultTableModel personTableModel = null;
	private int jobTableRow = -1;
    private boolean goFlag = true;
    private boolean noKillFlag = true;
    private boolean allSkillsFound = false;
    private int tryCount = 0;
	
	public Job(Scanner sc, DefaultTableModel jobTableModel, DefaultTableModel personTableModel, int jobTableRow) {
		super(sc);
		
		this.duration = sc.nextDouble();
		
		while(sc.hasNext()) {
			requirements.add(sc.next());
		}
		
		this.requiredResourcesCount = requirements.size();
		this.requirementsCopy = new ArrayList<String>(requirements);
		this.jobTableModel = jobTableModel;
		this.personTableModel = personTableModel;
		this.jobTableRow = jobTableRow;
		this.thread = new Thread(this, this.getName());
		
		int parent = this.getParent();
		if(parent > Constant.MAX_PORT_INDEX && parent <= Constant.MAX_DOCK_INDEX) {
			this.dockIndex = parent;
		}else if (parent > Constant.MAX_DOCK_INDEX && parent <= Constant.MAX_SHIP_INDEX){
			this.shipIndex = parent;
		}
	}
	
	// case where no job is required and ship is in dock
	public Job(int shipIndex, int dockIndex, SeaPort port) {
		this.shipIndex = shipIndex;
		this.dockIndex = dockIndex;
		this.port = port;
		this.thread = new Thread(this);
	}
	
	public void buildJob() {
		Object[] rowData = new Object[12];
		rowData[Constant.JOB_NAME] = this.getName();
		rowData[Constant.JOB_PORT] = port.getName(); 
		rowData[Constant.JOB_PROGRESS_BAR] = 0;
		rowData[Constant.JOB_STATUS_BUTTON] = Constant.IDLE;
		rowData[Constant.JOB_CANCEL_BUTTON] = Constant.JOB_CANCEL;
		rowData[Constant.JOB_INDEX] = this.getIndex();
		rowData[Constant.JOB_PORT_INDEX] = port.getIndex();
				
		if(requirements.size() == 0) {
			rowData[Constant.JOB_REQUIREMENTS] = Constant.NONE;			
		}else {
			rowData[Constant.JOB_REQUIREMENTS] = getRequirementsList(requirements);
		}
		
		Ship ship = port.getShips().get(shipIndex);
		Dock dock = port.getDocks().get(ship.getDockIndex());
		
		if(dock == null) {
			rowData[Constant.JOB_DOCK] = Constant.NONE;
			rowData[Constant.JOB_DOCK_INDEX] = Constant.NONE;
		}else {
			rowData[Constant.JOB_DOCK] = dock.getName();
			rowData[Constant.JOB_DOCK_INDEX] = dock.getIndex(); 
		}
		
		rowData[Constant.JOB_SHIP] = ship.getName();
		rowData[Constant.JOB_SHIP_INDEX] = ship.getIndex(); 

		jobTableModel.addRow(rowData);
	}
			  			
	public void run() {
		try {
			Thread.sleep(10);
		} catch (InterruptedException e1) { e1.printStackTrace(); }
		
		// initially check if ship requires no job and is already at the dock
		synchronized(port) {
			if(shipIndex != -1 && dockIndex != -1) {
				Queue<Integer> queue = port.getQueue();
				
				while(!queue.isEmpty()) {
					Integer queueShipIndex = queue.poll();
					Ship ship = port.getShips().get(queueShipIndex);
					ArrayList<Job> jobs = ship.getJobs();
					
					if(!jobs.isEmpty()) {
						port.getDocks().get(dockIndex).setShipIndex(queueShipIndex);
						port.getShips().get(queueShipIndex).setDockIndex(dockIndex);
						port.getShips().get(shipIndex).setDockIndex(-1);
						port.getShips().get(queueShipIndex).setParent(dockIndex);
						port.getShips().get(shipIndex).setParent(-1);
						port.setQueue(queue);
						break;
					}else {
						Dock skipped = port.getDocks().get(ship.getDockIndex());
						System.out.println(
								"Skipping Ship: " + ship.getName() + 
								", Dock: " + (skipped == null ? Constant.NONE: skipped.getName())+ 
								", Port: " + port.getName());
					}
				}
				port.notifyAll();
				return;
			}
			port.notifyAll();
		}
				
		// check to see if the ship is in dock
		synchronized(port) {
			updateJobTable(Constant.JOB_WAITING_PORT, jobTableRow, Constant.JOB_STATUS_BUTTON);
			
			while(shipInQueue()) {
				try {
					port.wait();
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
			
		}
		
		// acquiring the resources
		synchronized(port) {
			Ship ship = port.getShips().get(shipIndex);
			String dockName = port.getDocks().get(ship.getDockIndex()).getName();
			updateJobTable(dockName, jobTableRow, Constant.JOB_DOCK);
			updateJobTable(Constant.JOB_WAITING_DOCK, jobTableRow, Constant.JOB_STATUS_BUTTON);
			
			if(requiredResourcesCount == 0) {
				allSkillsFound = true;
			}else {
				registerResources();
				
				while(acquiringResources()) {
					try {
						port.wait(5000);
					} catch (InterruptedException e) { e.printStackTrace(); }
				}
				
				if(requirements.size() == resources.size()) {
					allSkillsFound = true;
				}				
			}
		}
					
		if(allSkillsFound) {
			doJob();						
		}else {
			updateJobTable(Constant.JOB_SKILL_NOT_FOUND, jobTableRow, Constant.JOB_STATUS_BUTTON);
		}
		
		// release the resources / ship, and assign queue ship to dock
		synchronized(port) {
			Ship ship = port.getShips().get(shipIndex);
			int dockIndex = ship.getDockIndex();
			ArrayList<Job> jobs = removeJob(ship.getJobs());
			
			if(requiredResourcesCount != 0) {
				for(Person resource: resources) {
					int personTableRow  = resource.getPersonTableRow();
					port.getPool().get(resource.getIndex()).setEmployment(-1);
					
					updatePersonTable(Constant.IDLE, personTableRow, Constant.PERSON_DOCK);
					updatePersonTable(Constant.IDLE, personTableRow, Constant.PERSON_SHIP);
					updatePersonTable(Constant.IDLE, personTableRow, Constant.PERSON_JOB);
					updatePersonTable(Constant.IDLE, personTableRow, Constant.PERSON_REQUIREMENTS);
					updatePersonTable(Constant.PERSON_UNASSIGNED_JOB, personTableRow, Constant.PERSON_STATUS);  
				}
			}
			
			port.getShips().get(shipIndex).setJobs(jobs);
			updateJobTable(Constant.SHIP_RELEASED, jobTableRow, Constant.JOB_DOCK);
			
			if(jobs.isEmpty()) {
				Queue<Integer> queue = port.getQueue();
				
				while(!queue.isEmpty()) {
					Integer queueShipIndex = queue.poll();
					Ship queueShip = port.getShips().get(queueShipIndex);
					ArrayList<Job> queueJob = queueShip.getJobs();
					
					if(!queueJob.isEmpty()) {
						port.getDocks().get(dockIndex).setShipIndex(queueShipIndex);
						port.getShips().get(queueShipIndex).setDockIndex(dockIndex);
						port.getShips().get(shipIndex).setDockIndex(-1);
						port.getShips().get(queueShipIndex).setParent(dockIndex);
						port.getShips().get(shipIndex).setParent(-1);
						port.setQueue(queue);
						break;
					}else {
						System.out.println(
								"Skipping Ship: " + queueShip.getName() + 
								", Dock: " + Constant.NONE +
								", Port: " + port.getName());
					}
				}
			}
			port.notifyAll();
		}
	}
	
	private void registerResources() {
		HashMap<Integer, Person> pool = port.getPool();
		
		for(int i = 0; i < requirements.size(); i++) {
			String requirement = requirements.get(i);
		
			for(Person person: pool.values()) {
				if(requirement.equals(person.getSkill())) {
					resources.add(person);
					break;
				}
			}
		}
	}
		
	private boolean acquiringResources() {
		System.out.println("Acquiring Resources: tryCount " + tryCount);
		tryCount++;		
		if(tryCount < 15) {
			for(Person resource: resources) {
				Person person = port.getPool().get(resource.getIndex());
				if(person.getEmployment() == -1) {
					port.getPool().get(resource.getIndex()).setEmployment(this.getIndex());
					resource.setEmployment(this.getIndex());
					
					int row = person.getPersonTableRow();
					Ship ship = port.getShips().get(shipIndex);
					int dockIndex = ship.getDockIndex();
					Dock dock = port.getDocks().get(dockIndex);
					
					updatePersonTable(dock.getName(), row, Constant.PERSON_DOCK);
					updatePersonTable(ship.getName(), row, Constant.PERSON_SHIP);
					updatePersonTable(this.getName(), row, Constant.PERSON_JOB);
					updatePersonTable(getRequirementsList(requirementsCopy), row, Constant.PERSON_REQUIREMENTS);
					updatePersonTable(Constant.PERSON_ASSIGNED_JOB, row, Constant.PERSON_STATUS);
				}
			}
			
			for(Person resource: resources) {
				if(resource.getEmployment() != this.getIndex()) {
					return true;
				}
			}
			
			return false;			
		}else {
			// release the resources and try again
			System.out.println("Resetting Resources: " + this.getName());
			tryCount = 0;
			for(Person resource: resources) {
				Person person = port.getPool().get(resource.getIndex());
				if(person.getEmployment() == this.getIndex()) {
					port.getPool().get(resource.getIndex()).setEmployment(-1);
					resource.setEmployment(-1);
					
					int row = person.getPersonTableRow();
					
					updatePersonTable(Constant.IDLE, row, Constant.PERSON_DOCK);
					updatePersonTable(Constant.IDLE, row, Constant.PERSON_SHIP);
					updatePersonTable(Constant.IDLE, row, Constant.PERSON_JOB);
					updatePersonTable(Constant.IDLE, row, Constant.PERSON_REQUIREMENTS);
					updatePersonTable(Constant.PERSON_UNASSIGNED_JOB, row, Constant.PERSON_STATUS);
				}
			}
						
			return true;
		}
	}
			
	private void doJob() {				
	    long time = System.currentTimeMillis();
	    long startTime = time;
	    long stopTime = (long) (time + duration);
	    
	    Ship ship = port.getShips().get(shipIndex);
	    System.out.println(
	    		"Running Job: " + this.getName() + 
	    		", Ship: " + ship.getName() + 
	    		", Dock: " + port.getDocks().get(ship.getDockIndex()).getName() +
	    		", Port: " + port.getName());
	    
	    if(requiredResourcesCount != 0) {
	    	for(Person person: resources) {
	    		updatePersonTable(Constant.PERSON_WORKING, person.getPersonTableRow(), Constant.PERSON_STATUS);
	    	}	    	
	    }
	    
	    while(time < stopTime && noKillFlag) {
	    	try {
	    		Thread.sleep(500);
	    	} catch(InterruptedException e) {}
	    	
	    	if(goFlag) {
				updateJobTable(Constant.JOB_RUNNING, jobTableRow, Constant.JOB_STATUS_BUTTON);
	    		time += 10;
	    		updateJobTable((int)(((time - startTime) / duration) * 100), jobTableRow, Constant.JOB_PROGRESS_BAR);
	    	} else {
	    		updateJobTable(Constant.JOB_SUSPENDED, jobTableRow, Constant.JOB_STATUS_BUTTON);
	    		
	    	    if(requiredResourcesCount != 0) {    	
	    	    	for(Person person: resources) {
	    	    		updatePersonTable(Constant.JOB_SUSPENDED, person.getPersonTableRow(), Constant.PERSON_STATUS);
	    	    	}
	    	    }
	    	}
	    }
	    
	    if(noKillFlag) {
	    	updateJobTable(100, jobTableRow, Constant.JOB_PROGRESS_BAR);
	    	updateJobTable(Constant.JOB_DONE, jobTableRow, Constant.JOB_STATUS_BUTTON);
	    	
    	    if(requiredResourcesCount != 0) {
    	    	for(Person person: resources) {
    	    		updatePersonTable(Constant.JOB_DONE, person.getPersonTableRow(), Constant.PERSON_STATUS);
    	    	}    	
    	    }
	    }else {
	    	updateJobTable(Constant.JOB_CANCELED, jobTableRow, Constant.JOB_STATUS_BUTTON);
	    	updateJobTable(Constant.IDLE, jobTableRow, Constant.JOB_CANCEL_BUTTON);
	    	
    	    if(requiredResourcesCount != 0) {
    	    	for(Person person: resources) {
    	    		updatePersonTable(Constant.JOB_CANCELED, person.getPersonTableRow(), Constant.PERSON_STATUS);
    	    	}    	
    	    }
	    }
	}
	
	private ArrayList<Job> removeJob(ArrayList<Job> jobs) {
		int currentIndex = this.getIndex();
		int key = -1;
		
		for(int i = 0; i < jobs.size(); i++) {
			int jobIndex = jobs.get(i).getIndex();
			if(currentIndex == jobIndex) key = i;
		}
		
		if(key != -1) {
			Job removed = jobs.remove(key);
			System.out.println("Removing Job: " + removed.getName() + " Jobs Remaining: " + jobs.size());
		}
		
		return jobs;
	}
	
	private boolean shipInQueue() {
		Ship ship = port.getShips().get(shipIndex);
		System.out.println("Attempting to dock: " + ship.getName());
		if(ship.getDockIndex() == -1) {
			return true;
		}else {
			return false;
		}
	}
	 	
	public void setKillFlag () {
		noKillFlag = false;
	}
	
	public void updateJobTable(Object value, int row, int column) {
		jobTableModel.setValueAt(value, row, column);
	}
	
	public void updatePersonTable(Object value, int row, int column) {
		personTableModel.setValueAt(value, row, column);
	}
	
	public String getRequirementsList(ArrayList<String> reqs) {
		String st = "";
		for(String requirement: reqs) {
			st += requirement + " ";
		}
		
		return st;
	}

	public String toString() {
		return super.toString();
	}

	public int getDockIndex() {
		return dockIndex;
	}

	public void setDockIndex(int dockIndex) {
		this.dockIndex = dockIndex;
	}

	public int getShipIndex() {
		return shipIndex;
	}

	public void setShipIndex(int shipIndex) {
		this.shipIndex = shipIndex;
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

	public Thread getThread() {
		return thread;
	}

	public void setThread(Thread thread) {
		this.thread = thread;
	}

	public SeaPort getPort() {
		return port;
	}

	public void setPort(SeaPort port) {
		this.port = port;
	}

	public DefaultTableModel getJobTableModel() {
		return jobTableModel;
	}

	public void setTableModel(DefaultTableModel jobTableModel) {
		this.jobTableModel = jobTableModel;
	}

	public DefaultTableModel getPersonTableModel() {
		return personTableModel;
	}

	public void setPersonTableModel(DefaultTableModel personTableModel) {
		this.personTableModel = personTableModel;
	}

	public void setJobTableModel(DefaultTableModel jobTableModel) {
		this.jobTableModel = jobTableModel;
	}

	public int getJobTableRow() {
		return jobTableRow;
	}

	public void setJobTableRow(int jobTableRow) {
		this.jobTableRow = jobTableRow;
	}

	public boolean isGoFlag() {
		return goFlag;
	}

	public void setGoFlag(boolean goFlag) {
		this.goFlag = goFlag;
	}

	public boolean isNoKillFlag() {
		return noKillFlag;
	}

	public void setNoKillFlag(boolean noKillFlag) {
		this.noKillFlag = noKillFlag;
	}

	public ArrayList<Person> getResources() {
		return resources;
	}

	public void setResources(ArrayList<Person> resources) {
		this.resources = resources;
	}
}
