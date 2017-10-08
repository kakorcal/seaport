package project4;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;

/*
 * File: SeaPortProgram.java
 * Date: Sep 17, 2017
 * Author: Kenneth Korcal
 * Purpose: read data file, parse into data structure, and render the data into a GUI with searching and sorting capabilities
 * */

/*
 * application requirements
 * 1. create JFrame GUI
 * 2. user selects data file using JFileChooser 
 * 3. create data structure w/ JScrollPane and JTextArea
 * 4. add user search capabilities
 * 		JTextField to specify search target (name, index, skill)
 * 		don't create new data structure
 * 		you can create structure of found items as a return value
 * 5. use HashMaps instead of ArrayLists
 * 6. add sort capabilities (width, weight, draft, length, name)
 * 7. extend Project 2 to use the Swing class JTree effectively to display the contents of the data file
 * 8. create a thread for each job, cannot run until a ship has a dock, create a GUI to show the progress of each job.
 * 
 * */

public class SeaPortProgram extends JFrame {

	private static final long serialVersionUID = 1L;
//	private static final int WIDTH = 1100;
//	private static final int HEIGHT = 700;
	
	public static void main(String[] args) {
		SeaPortProgram app = new SeaPortProgram();
		app.display();
	}

	public SeaPortProgram() {
		super("Sea Port Program");
		setFrame();
		add(new MainPanel());
	}

	private void display() {
		setVisible(true);
	}

	private void setFrame() {
		//setSize(WIDTH, HEIGHT);
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/*
	 * Components:
	 * JFileChooser, JButton - for user to select file
	 * JScrollPane, JTextArea - for displaying output
	 * JTextField, JLabel, JButton, JRadioButton - for searching and sorting
	 * 
	 * */
	
	public class MainPanel extends JPanel {
		private static final long serialVersionUID = -8940075139888617038L;
		private World world;
		private JTextArea textAreaField = new JTextArea(11, 35);
		private JScrollPane textAreaScrollPane = new JScrollPane(textAreaField, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		private JButton newFileButton = new JButton("New File");
		
		private JLabel searchFieldLabel = new JLabel("Keyword:");
		private JTextField searchField = new JTextField();
		private JLabel searchOptionLabel = new JLabel("Option:");
		private String[] searchOptions = {"Name", "Index", "Skill"};
		private JButton searchButton = new JButton("Search");
		private JComboBox<String> searchOptionDropdown;
		
		private JLabel sortOrderLabel = new JLabel("Order:");
		private JLabel sortOptionLabel = new JLabel("Option:");
		private String[] sortOrder = {"Ascending", "Descending"};
		private String[] sortOptions = {"Weight", "Length", "Width", "Draft", "Name"};
		private JButton sortButton = new JButton("Sort");
	    private JComboBox<String> sortOrderDropdown;
	    private JComboBox<String> sortOptionDropdown;
	    
	    private JTree tree;
	    private JScrollPane treeScrollPane;
	    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("Root");
	    private DefaultTreeModel treeModel;

		public MainPanel() {
			
			/*
			 * Container
			 */
			this.setLayout(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			
			/*
			 * Display area
			 * */
			JPanel textAreaPanel = new JPanel();
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(newFileButton);
			buttonPanel.setLayout(new GridLayout(1, 0));
			textAreaField.setEditable(false);
			textAreaField.setBackground(getBackground());
			textAreaPanel.add(textAreaScrollPane);
			textAreaPanel.add(buttonPanel);
			textAreaPanel.setLayout(new BoxLayout(textAreaPanel, BoxLayout.Y_AXIS));
			textAreaPanel.setMinimumSize(new Dimension(325, this.getHeight()));
			
			/*
			 * Search area
			 * */
			JPanel filterAreaPanel = new JPanel();
			JPanel searchAreaPanel = new JPanel();
			JPanel searchFieldPanel = new JPanel();
			JPanel searchDropdownPanel = new JPanel();
			JPanel searchButtonPanel = new JPanel();
			searchAreaPanel.setBorder(BorderFactory.createTitledBorder("Search"));
			
			searchFieldPanel.add(searchFieldLabel);
			searchFieldPanel.add(searchField);
			
			searchOptionDropdown = new JComboBox<String>(searchOptions);
			searchDropdownPanel.add(searchOptionLabel);
			searchDropdownPanel.add(searchOptionDropdown);
			
			searchButtonPanel.add(searchButton);
			
			searchFieldPanel.setLayout(new GridLayout(0, 2));
			searchDropdownPanel.setLayout(new GridLayout(0, 2));
			searchButtonPanel.setLayout(new GridLayout(0, 1));
			
		    searchAreaPanel.add(searchFieldPanel);
		    searchAreaPanel.add(searchDropdownPanel);
		    searchAreaPanel.add(searchButtonPanel);
		    searchAreaPanel.setLayout(new BoxLayout(searchAreaPanel, BoxLayout.Y_AXIS));
		    
			/*
			 * Sort area
			 * */
			JPanel sortAreaPanel = new JPanel();
			JPanel sortOrderPanel = new JPanel();
			JPanel sortDropdownPanel = new JPanel();
			JPanel sortButtonPanel = new JPanel();
			sortAreaPanel.setBorder(BorderFactory.createTitledBorder("Sort"));
			
			sortOrderDropdown = new JComboBox<String>(sortOrder);
			sortOrderPanel.add(sortOrderLabel);
			sortOrderPanel.add(sortOrderDropdown);
			
			sortOptionDropdown = new JComboBox<String>(sortOptions);
			sortDropdownPanel.add(sortOptionLabel);
			sortDropdownPanel.add(sortOptionDropdown);
						
			sortButtonPanel.add(sortButton);
			
			sortOrderPanel.setLayout(new GridLayout(0, 2));
			sortDropdownPanel.setLayout(new GridLayout(0, 2));
			sortButtonPanel.setLayout(new GridLayout(0, 1));
			
			sortAreaPanel.add(sortOrderPanel);
			sortAreaPanel.add(sortDropdownPanel);
			sortAreaPanel.add(sortButtonPanel);
			sortAreaPanel.setLayout(new BoxLayout(sortAreaPanel, BoxLayout.Y_AXIS));
		    
			filterAreaPanel.add(searchAreaPanel);
			filterAreaPanel.add(sortAreaPanel);
			filterAreaPanel.setLayout(new BoxLayout(filterAreaPanel, BoxLayout.Y_AXIS));
			
			
			/*
			 * Tree area
			 * */
			
	        tree = new JTree(root);
	        tree.setRootVisible(false);
	        treeModel = (DefaultTreeModel) tree.getModel();

	        // https://stackoverflow.com/questions/14563433/jtree-set-background-of-node-to-non-opaque
	        tree.setCellRenderer(new DefaultTreeCellRenderer() {
				private static final long serialVersionUID = 1L;
			    public Color getBackgroundNonSelectionColor() { return null; }
	        });
	        tree.setBackground(this.getBackground());
	        treeScrollPane = new JScrollPane(tree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        treeScrollPane.setPreferredSize(new Dimension(275, this.getHeight()));
	        treeScrollPane.setMinimumSize(new Dimension(275, this.getHeight()));
	        treeScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
	        
	        
	        /*
	         * Job Threads
	         * */
	        JPanel jobPanel = new JPanel();
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));
	        jobPanel.add(new JLabel("Lorem Ipsum Dolor Emit"));
	        jobPanel.add(new JLabel("JOB_87_98_65"));
	        jobPanel.add(new JLabel("Resources"));
	        jobPanel.add(new JButton("Done"));
	        jobPanel.add(new JButton("Cancel"));

	        JPanel temp = new JPanel();
	        temp.setLayout(new GridLayout(20, 5));
	        jobPanel.setLayout(new GridLayout(20, 5));	        
	        JScrollPane jobScrollPane = new JScrollPane(jobPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	        jobScrollPane.setPreferredSize(new Dimension(this.getWidth(), 460));
			
			/*
			 * Event handlers
			 * */
		    FileButtonListener fileButtonListener = new FileButtonListener();
			newFileButton.addMouseListener(fileButtonListener);
			
			SearchButtonListener searchButtonListener = new SearchButtonListener();
			searchButton.addMouseListener(searchButtonListener);
			
			SortButtonListener sortButtonListener = new SortButtonListener();
			sortButton.addMouseListener(sortButtonListener);
			
			/*
			 * Main Layout 
			 * */
			
			constraints.fill = GridBagConstraints.BOTH;
			constraints.weightx = 0.1;
			constraints.gridx = 0;
			constraints.gridy = 0;
			// top, left, bottom, right
			constraints.insets = new Insets(2, 2, 1, 1);
		    this.add(treeScrollPane, constraints);
		    
			constraints.gridx = 1;
			constraints.gridy = 0;
		    constraints.insets = new Insets(2, 1, 1, 1);
			this.add(textAreaPanel, constraints);
			
			constraints.gridx = 2;
			constraints.gridy = 0;
			constraints.insets = new Insets(0, 0, 0, 0);
			this.add(filterAreaPanel, constraints);
			
			constraints.gridwidth = 3;
			constraints.weighty = 0.1;
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.insets = new Insets(1, 2, 2, 2);
			this.add(jobScrollPane, constraints);
		}
		
		
		// displays search result in separate dialog box
		private void displayMessage(String title, String message) {
			JTextArea textArea = new JTextArea(message);
			JScrollPane scrollPane = new JScrollPane(textArea);  
			textArea.setLineWrap(true);  
			textArea.setWrapStyleWord(true); 
			scrollPane.setPreferredSize(new Dimension(550, 430));
			JOptionPane.showMessageDialog(null, scrollPane, title, JOptionPane.PLAIN_MESSAGE);
		}
		
		// opens file and creates data structure with the world instance
		// https://www.youtube.com/watch?v=xkcs25Ustag
		private void openFile() {
			JFileChooser fileChooser = new JFileChooser(".");
			world = new World();
			
			try {
				if(fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
					File file = fileChooser.getSelectedFile();
					Scanner sc = new Scanner(file);
					world.setItems(new HashMap<Integer, Thing>());
					
					while(sc.hasNextLine()) {
						world.process(sc.nextLine());
					}
					
					sc.close();
					textAreaField.setText(world.toString());
					root.removeAllChildren();
					world.toTree(root);
					treeModel.reload(root);
				}else {
					textAreaField.setText("No file was selected");
					root.removeAllChildren();
					treeModel.reload(root);
				}
			}catch(Exception e) {
				textAreaField.setText("Failed to process file");
				root.removeAllChildren();
				treeModel.reload(root);
			}
		}
				
		class FileButtonListener extends MouseAdapter {
			public void mouseClicked(MouseEvent event) {
				openFile();
			}
		}
		
		class SortButtonListener extends MouseAdapter {
			public void mouseClicked(MouseEvent event) {
				String sortOrder = sortOrderDropdown.getSelectedItem().toString();
				String sortOption = sortOptionDropdown.getSelectedItem().toString(); 
				ArrayList<String> sortResults = sortWorld(sortOption, sortOrder);
				
				if(sortResults.isEmpty()) {
					displayMessage("Sort", "No results found");
				}else {
					String st = "Sort results: ";
					
					for(String result: sortResults) {
						st += "\n" + result;
					}
					
					displayMessage("Sort", st);					
				}
			}
		}
		
		private ArrayList<String> sortWorld(String sortOption, String sortOrder) {
			boolean asc = true;
			if(sortOrder.equals("Descending")) asc = false;			
			
			switch(sortOption) {
				case "Weight":
				case "Length":
				case "Width":
				case "Draft":
					return sortByShip(sortOption, asc);
				case "Name":
					return sortByName(asc);
				default:
					return sortByShip(sortOption, asc);
			}
		}
		
		// references
		// http://www.java67.com/2015/01/how-to-sort-hashmap-in-java-based-on.html
		// http://www.geeksforgeeks.org/comparator-interface-java/
		private ArrayList<String> sortByShip(String sortOption, boolean asc) {
			ArrayList<String> results = new ArrayList<String>();
			if(world == null) return results;
						
			// for each port, get queue, sort it and append to result
			for(SeaPort port: world.getPorts().values()) {
				String portName = port.getName();
				Integer portNumber = port.getIndex();
				HashMap<Integer, Ship> ships = port.getShips();
				ArrayList<Ship> listOfShips = new ArrayList<Ship>(ships.values());
				
				if(asc) {
					Collections.sort(listOfShips, getShipComparator(sortOption));
				}else {
					Collections.sort(listOfShips, getShipComparator(sortOption).reversed());					
				}

				String seaport = ">>> SeaPort: " + portName + " " + portNumber;
				seaport += "\n\n  --- List of all ships:";
				results.add(seaport);
				for(Ship ship: listOfShips) {
					results.add(ship.toString());
					results.add(ship.getShipDimensions());
				} 
			}
						
			return results;
		}
		
		private Comparator<Ship> getShipComparator(String sortOption) {
			switch(sortOption) {
				case "Weight":
					return new SortByShipWeight();
				case "Length":
					return new SortByShipLength();
				case "Width":
					return new SortByShipWidth();
				case "Draft":
					return new SortByShipDraft();
				default:
					return new SortByShipWidth();
			}
		}
				
        class SortByShipWeight implements Comparator<Ship> {
            public int compare(Ship a, Ship b) {
            	return Double.compare(a.getWeight(), b.getWeight());
            }
        }
        
        class SortByShipWidth implements Comparator<Ship> {
            public int compare(Ship a, Ship b) {
            	return Double.compare(a.getWeight(), b.getWeight());
            }
        }
        
        class SortByShipDraft implements Comparator<Ship> {
            public int compare(Ship a, Ship b) {
            	return Double.compare(a.getDraft(), b.getDraft());
            }
        }
        
        class SortByShipLength implements Comparator<Ship> {
            public int compare(Ship a, Ship b) {
            	return Double.compare(a.getLength(), b.getLength());
            }
        }
                 
        // sort ports by name
        // for each port in sorted port, sort docks que ships persons by name
        // append string to results
        private ArrayList<String> sortByName(boolean asc) {
			ArrayList<String> results = new ArrayList<String>();
			if(world == null) return results;
			
			
			ArrayList<SeaPort> ports = new ArrayList<SeaPort>(world.getPorts().values());
			
			if(asc) {
				Collections.sort(ports);
			}else {
				Collections.reverse(ports);
			}
			
			for(SeaPort port: ports) {
				HashMap<Integer, Dock> docks = port.getDocks();
				HashMap<Integer, Ship> ships = port.getShips();
				Queue<Integer> queue = port.getQueue();
				HashMap<Integer, Person> persons = port.getPersons();
				
				ArrayList<Dock> listOfDocks = new ArrayList<Dock>(docks.values());
				ArrayList<Ship> listOfShips = new ArrayList<Ship>(ships.values());
				ArrayList<Person> listOfPersons = new ArrayList<Person>(persons.values());
				ArrayList<Integer> listOfQueue = new ArrayList<Integer>();
				
				for(int shipIndex: queue) listOfQueue.add(shipIndex);
				
				
				if(asc) {
					Collections.sort(listOfDocks);
					Collections.sort(listOfShips);
					Collections.sort(listOfQueue);
					Collections.sort(listOfPersons);
				}else {
					Collections.reverse(listOfDocks);
					Collections.reverse(listOfShips);
					Collections.reverse(listOfQueue);
					Collections.reverse(listOfPersons);
				}
		        
				HashMap<Integer, Dock> sortedDocks = new HashMap<Integer, Dock>(listOfDocks.size());
				HashMap<Integer, Ship> sortedShips = new HashMap<Integer, Ship>(listOfShips.size());
				Queue<Integer> sortedQueue = new LinkedList<Integer>();
				HashMap<Integer, Person> sortedPersons = new HashMap<Integer, Person>(listOfPersons.size());
				
				for(int i = 0; i < listOfDocks.size(); i++) sortedDocks.put(listOfDocks.get(i).getIndex(), listOfDocks.get(i));
				for(int i = 0; i < listOfShips.size(); i++) sortedShips.put(listOfShips.get(i).getIndex(), listOfShips.get(i));
				for(int i = 0; i < listOfQueue.size(); i++) sortedQueue.add(listOfQueue.get(i));
				for(int i = 0; i < listOfPersons.size(); i++) sortedPersons.put(listOfPersons.get(i).getIndex(), listOfPersons.get(i));
				
				
				port.setDocks(sortedDocks);
				port.setQueue(sortedQueue);
				port.setShips(sortedShips);
				port.setPersons(sortedPersons);
				results.add(port.toString());
			}
			
			return results;
        }
        		
		class SearchButtonListener extends MouseAdapter {
			public void mouseClicked(MouseEvent event) {
				if(textAreaField.getText().isEmpty()) {
					displayMessage("Search", "Please choose a file to search");
				}else {
					String fieldName = searchOptionDropdown.getSelectedItem().toString();
					String target = searchField.getText();
					ArrayList<String> searchResults = searchWorld(fieldName, target);
					
					if(searchResults.isEmpty()) {
						displayMessage("Search", "No results found");					
					}else {
						String st = "Search results: ";
						
						for(String result: searchResults) {
							st += "\n" + result;
						}
						
						displayMessage("Search", st);		
					}	
				}				
			}
		}
				
		// the next 5 methods below are utility methods that search and accumulate the results in an arraylist
		private ArrayList<String> searchWorld(String fieldName, String target) {
			switch(fieldName) {
				case "Name":
					return searchByName(target);
				case "Index":
					return searchByIndex(target);
				case "Skill":
					return searchBySkill(target);
				default:
					return new ArrayList<String>();
			}
		}
		
		private ArrayList<String> searchByName(String target) {
			ArrayList<String> results = new ArrayList<String>();
			HashMap<Integer, SeaPort> ports = world.getPorts();
			
			for(SeaPort port: ports.values()) {
				HashMap<Integer, Dock> docks = port.getDocks();
				Queue<Integer> queue = port.getQueue();
				HashMap<Integer, Ship> ships = port.getShips();
				HashMap<Integer, Person> persons = port.getPersons();
				
				for(Dock dock: docks.values()) {
					if(dock.getName().contains(target)) results.add(dock.toString());
				}
				
				for(int shipIndex: queue) {
					Ship ship = ships.get(shipIndex);
					if(ship.getName().contains(target)) results.add(ship.toString());
				}
				
				for(Ship ship: ships.values()) {
					if(ship.getName().contains(target)) results.add(ship.toString());
				}
				
				for(Person person: persons.values()) {
					if(person.getName().contains(target)) results.add(person.toString());
				}
			}
			return results;
		}
		
		private ArrayList<String> searchByIndex(String target) {
			ArrayList<String> results = new ArrayList<String>();
			HashMap<Integer, SeaPort> ports = world.getPorts();
			
			for(SeaPort port: ports.values()) {
				HashMap<Integer, Dock> docks = port.getDocks();
				Queue<Integer> queue = port.getQueue();
				HashMap<Integer, Ship> ships = port.getShips();
				HashMap<Integer, Person> persons = port.getPersons();
				
				for(Dock dock: docks.values()) {
					String st = new StringBuilder().append(dock.getIndex()).toString();
					if(st.contains(target)) results.add(dock.toString());
				}
				
				for(int shipIndex: queue) {
					String st = new StringBuilder().append(shipIndex).toString();
					if(st.contains(target)) results.add(ships.get(shipIndex).toString());
				}
				
				for(Ship ship: ships.values()) {
					String st = new StringBuilder().append(ship.getIndex()).toString();
					if(st.contains(target)) results.add(ship.toString());
				}
				
				for(Person person: persons.values()) {
					String st = new StringBuilder().append(person.getIndex()).toString();
					if(st.contains(target)) results.add(person.toString());
				}
			}
			return results;
		}
		
		
		private ArrayList<String> searchBySkill(String target) {
			ArrayList<String> results = new ArrayList<String>();
			HashMap<Integer, SeaPort> ports = world.getPorts();
			
			for(SeaPort port: ports.values()) {
				HashMap<Integer, Person> persons = port.getPersons();
				
				for(Person person: persons.values()) {
					if(person.getSkill().contains(target)) results.add(person.toString());
				}
			}
			return results;
		}
	}
}
