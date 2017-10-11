package project4;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.BlockingDeque;

import javax.swing.table.DefaultTableModel;

public class Job extends Thing implements Runnable {
	
	private int dockIndex = -1;
	private int shipIndex = -1;
	private double duration;
	private ArrayList<String> requirements = new ArrayList<String>();
	private ArrayList<String> requirementsCopy = null;
	private Thread thread = null;
	private SeaPort port = null;
	private BlockingDeque<Person> pool = null;
	private ArrayList<Person> resources = new ArrayList<Person>();;
	private int requiredResourcesCount = 0;
	private DefaultTableModel jobTableModel = null;
	private DefaultTableModel personTableModel = null;
	private int jobTableRow = -1;
    private boolean goFlag = true;
    private boolean noKillFlag = true;
	
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
			while(shipInQueue()) {
				updateJobTable(Constant.JOB_WAITING_PORT, jobTableRow, Constant.JOB_STATUS_BUTTON);
				try {
					port.wait();
				} catch (InterruptedException e) { e.printStackTrace(); }
			}
			
			Ship ship = port.getShips().get(shipIndex);
			String dockName = port.getDocks().get(ship.getDockIndex()).getName();
			updateJobTable(dockName, jobTableRow, Constant.JOB_DOCK);			
		}
		
		// check to see if possible to complete job
		synchronized(port) {
			if(requiredResourcesCount == 0) {
				port.notifyAll();
				return;
			}
			
			int skillCount = 0;
			for(Person person: pool) {
				String skill = person.getSkill();
				for(String requirement: requirements) {
					if(requirement.equals(skill)) {
						skillCount++;
					}
				}
			}
			
			if(skillCount < requiredResourcesCount) {
				updateJobTable(Constant.JOB_SKILL_NOT_FOUND, jobTableRow, Constant.JOB_STATUS_BUTTON);
				assignNewShip();				
				port.notifyAll();
				return;
			}
			port.notifyAll();
		}
		
		
		if(requiredResourcesCount == 0) {
			System.out.println("Resources not required to complete job: " + this.getName());
			doJob();
		}else {
			acquireResources();
			doJob();
			releaseResources();			
		}
		
		// release the ship, assign queue ship to dock
		synchronized(port) {
			assignNewShip();
			port.notifyAll();
		}
	}
	
	private void assignNewShip() {
		Ship ship = port.getShips().get(shipIndex);
		int dockIndex = ship.getDockIndex();
		ArrayList<Job> jobs = removeJob(ship.getJobs());
		
		port.getShips()
			.get(shipIndex)
			.setJobs(jobs);
		
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
	}
	
	private void acquireResources() {		
		updateJobTable(Constant.JOB_WAITING_DOCK, jobTableRow, Constant.JOB_STATUS_BUTTON);
						
		while(!requirements.isEmpty() && resources.size() < requiredResourcesCount) {			
			try {
				Person person = pool.takeFirst();
				System.out.println("Taking person from pool: " 
						+ person.getName() 
						+ ", Job: " + this.getName()
						+ ", Port: " + port.getIndex() 
						+ ", Req: " + requirements.toString());
				
				String skill = person.getSkill();
				int skillIndex = -1;					
				
				for(int i = 0; i < requirements.size(); i++) {
					if(skill.equals(requirements.get(i))) {
						skillIndex = i;
					}								
				}
				
				if(skillIndex != -1) {
					requirements.remove(skillIndex);
					resources.add(person);
					
					int row = person.getPersonTableRow();
					Ship ship = port.getShips().get(shipIndex);
					int dockIndex = ship.getDockIndex();
					Dock dock = port.getDocks().get(dockIndex);
					
					updatePersonTable(dock.getName(), row, Constant.PERSON_DOCK);
					updatePersonTable(ship.getName(), row, Constant.PERSON_SHIP);
					updatePersonTable(this.getName(), row, Constant.PERSON_JOB);
					updatePersonTable(getRequirementsList(requirementsCopy), row, Constant.PERSON_REQUIREMENTS);
					updatePersonTable(Constant.PERSON_ASSIGNED_JOB, row, Constant.PERSON_STATUS);
				}else {
					System.out.println("Taking person from pool: " 
							+ person.getName() 
							+ ", Job: " + this.getName()
							+ ", Port: " + port.getIndex() 
							+ ", Req: " + requirements.toString());
					pool.addLast(person);
				}
			} catch (InterruptedException e) { e.printStackTrace(); }
		}
	}
	
	public void releaseResources() { 
		for(int i = 0; i < resources.size(); i++) {
			Person person = resources.get(i);		
			int row = person.getPersonTableRow();
			pool.add(person);
			System.out.println("Releasing resource: " + person.getName());
			
			updatePersonTable(Constant.IDLE, row, Constant.PERSON_DOCK);
			updatePersonTable(Constant.IDLE, row, Constant.PERSON_SHIP);
			updatePersonTable(Constant.IDLE, row, Constant.PERSON_JOB);
			updatePersonTable(Constant.IDLE, row, Constant.PERSON_REQUIREMENTS);
			updatePersonTable(Constant.PERSON_UNASSIGNED_JOB, row, Constant.PERSON_STATUS);		
		}
		
		resources = null;
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
	    
	    for(Person person: resources) {
	    	updatePersonTable(Constant.PERSON_WORKING, person.getPersonTableRow(), Constant.PERSON_STATUS);
	    }
	    
	    while(time < stopTime && noKillFlag) {
	    	try {
	    		Thread.sleep(1000);
	    	} catch(InterruptedException e) {}
	    	
	    	if(goFlag) {
				updateJobTable(Constant.JOB_RUNNING, jobTableRow, Constant.JOB_STATUS_BUTTON);
	    		time += 10;
	    		updateJobTable((int)(((time - startTime) / duration) * 100), jobTableRow, Constant.JOB_PROGRESS_BAR);
	    	} else {
	    		updateJobTable(Constant.JOB_SUSPENDED, jobTableRow, Constant.JOB_STATUS_BUTTON);
	    		
	    	    for(Person person: resources) {
	    	    	updatePersonTable(Constant.JOB_SUSPENDED, person.getPersonTableRow(), Constant.PERSON_STATUS);
	    	    }
	    	}
	    }
	    
	    if(noKillFlag) {
	    	updateJobTable(100, jobTableRow, Constant.JOB_PROGRESS_BAR);
	    	updateJobTable(Constant.JOB_DONE, jobTableRow, Constant.JOB_STATUS_BUTTON);
	    }else {
	    	updateJobTable(Constant.JOB_CANCELED, jobTableRow, Constant.JOB_STATUS_BUTTON);
	    	updateJobTable(Constant.IDLE, jobTableRow, Constant.JOB_CANCEL_BUTTON);
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

	public BlockingDeque<Person> getPool() {
		return pool;
	}

	public void setPool(BlockingDeque<Person> pool) {
		this.pool = pool;
	}
}
