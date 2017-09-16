package project2;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
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

/*
 * File: SeaPortProgram.java
 * Date: Sep 3, 2017
 * Author: Kenneth Korcal
 * Purpose: read data file, parse into data structure, and render the data into a GUI with searching capabilities
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
 * */

public class SeaPortProgram extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 740;
	private static final int HEIGHT = 250;
	
	public static void main(String[] args) {
		SeaPortProgram app = new SeaPortProgram();
		app.display();
	}

	public SeaPortProgram() {
		super("Sea Port Program");
		setFrame(WIDTH, HEIGHT);
		add(new MainPanel());
	}

	private void display() {
		setVisible(true);
	}

	private void setFrame(int width, int height) {
		setSize(width, height);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	/*
	 * Components:
	 * JFileChooser, JButton - for user to select file
	 * JScrollPane, JTextArea - for displaying output
	 * JTextField, JLabel, JButton, JRadioButton - for searching
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
		private JComboBox<String> searchDropdown;
		
		private JLabel sortOrderLabel = new JLabel("Order:");
		private JLabel sortOptionLabel = new JLabel("Option:");
		private String[] sortOrder = {"Ascending", "Descending"};
		private String[] sortOptions = {"None", "Weight", "Length", "Width", "Draft", "Name"};
		private JButton sortButton = new JButton("Sort");
	    private JComboBox<String> sortOrderDropdown;
	    private JComboBox<String> sortOptionDropdown;

		public MainPanel() {
			
			/*
			 * Container
			 */
			JPanel container = new JPanel(new GridBagLayout());
			GridBagConstraints constraints = new GridBagConstraints();
			
			/*
			 * Display area
			 * */
			JPanel textAreaPanel = new JPanel();
			JPanel buttonPanel = new JPanel();
			buttonPanel.add(newFileButton);
			buttonPanel.setAlignmentX(CENTER_ALIGNMENT);
			textAreaField.setEditable(false);
			textAreaField.setBackground(getBackground());
			textAreaPanel.add(textAreaScrollPane);
			textAreaPanel.add(buttonPanel);
			textAreaPanel.setLayout(new BoxLayout(textAreaPanel, BoxLayout.Y_AXIS));
			
			
			JPanel filterAreaPanel = new JPanel();
			/*
			 * Search area
			 * */
			JPanel searchAreaPanel = new JPanel();
			JPanel searchFieldPanel = new JPanel();
			JPanel searchDropdownPanel = new JPanel();
			JPanel searchButtonPanel = new JPanel();
			searchAreaPanel.setBorder(BorderFactory.createTitledBorder("Search"));
			
			searchFieldPanel.add(searchFieldLabel);
			searchFieldPanel.add(searchField);
			
			searchDropdown = new JComboBox<String>(searchOptions);
			searchDropdownPanel.add(searchOptionLabel);
			searchDropdownPanel.add(searchDropdown);
			
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
			 * Event handlers
			 * */
		    FileButtonListener fileButtonListener = new FileButtonListener();
			newFileButton.addMouseListener(fileButtonListener);
			
			SearchButtonListener searchButtonListener = new SearchButtonListener();
			searchButton.addMouseListener(searchButtonListener);
			
			/*
			 * Main Layout 
			 * */
		    constraints.anchor = GridBagConstraints.PAGE_START;
		    constraints.insets = new Insets(3,0,0,10);
			container.add(textAreaPanel, constraints);
			constraints.insets = new Insets(0,0,0,0);
			container.add(filterAreaPanel, constraints);
			add(container);
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
					
					while(sc.hasNextLine()) {
						world.process(sc.nextLine());
					}
					
					sc.close();
					textAreaField.setText(world.toString());
				}else {
					textAreaField.setText("No file was selected");
				}
			}catch(Exception e) {
				textAreaField.setText("Failed to process file");
			}
		}
				
		class FileButtonListener extends MouseAdapter {
			public void mouseClicked(MouseEvent event) {
				openFile();
			}
		}
		
		class SearchButtonListener extends MouseAdapter {
			public void mouseClicked(MouseEvent event) {
				if(textAreaField.getText().isEmpty()) {
					displayMessage("Search", "Please choose a file to search");
				}else {
					String fieldName = searchDropdown.getSelectedItem().toString();
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
			ArrayList<SeaPort> ports = world.getPorts();
			
			for(SeaPort port: ports) {
				ArrayList<Dock> docks = port.getDocks();
				ArrayList<Ship> ques = port.getQue();
				ArrayList<Ship> ships = port.getShips();
				ArrayList<Person> persons = port.getPersons();
				
				for(Dock dock: docks) {
					if(dock.getName().contains(target)) results.add(dock.toString());
				}
				
				for(Ship que: ques) {
					if(que.getName().contains(target)) results.add(que.toString());
				}
				
				for(Ship ship: ships) {
					if(ship.getName().contains(target)) results.add(ship.toString());
				}
				
				for(Person person: persons) {
					if(person.getName().contains(target)) results.add(person.toString());
				}
			}
			return results;
		}
		
		private ArrayList<String> searchByIndex(String target) {
			ArrayList<String> results = new ArrayList<String>();
			ArrayList<SeaPort> ports = world.getPorts();
			
			for(SeaPort port: ports) {
				ArrayList<Dock> docks = port.getDocks();
				ArrayList<Ship> ques = port.getQue();
				ArrayList<Ship> ships = port.getShips();
				ArrayList<Person> persons = port.getPersons();
				
				for(Dock dock: docks) {
					String st = new StringBuilder().append(dock.getIndex()).toString();
					if(st.contains(target)) results.add(dock.toString());
				}
				
				for(Ship que: ques) {
					String st = new StringBuilder().append(que.getIndex()).toString();
					if(st.contains(target)) results.add(que.toString());
				}
				
				for(Ship ship: ships) {
					String st = new StringBuilder().append(ship.getIndex()).toString();
					if(st.contains(target)) results.add(ship.toString());
				}
				
				for(Person person: persons) {
					String st = new StringBuilder().append(person.getIndex()).toString();
					if(st.contains(target)) results.add(person.toString());
				}
			}
			return results;
		}
		
		
		private ArrayList<String> searchBySkill(String target) {
			ArrayList<String> results = new ArrayList<String>();
			ArrayList<SeaPort> ports = world.getPorts();
			
			for(SeaPort port: ports) {
				ArrayList<Person> persons = port.getPersons();
				
				for(Person person: persons) {
					if(person.getSkill().contains(target)) results.add(person.toString());
				}
			}
			return results;
		}
	}
}
