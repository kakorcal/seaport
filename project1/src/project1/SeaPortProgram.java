package project1;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Scanner;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/*
 * File: SeaPortProgram.java
 * Date: Sep 3, 2017
 * Author: Kenneth Korcal
 * Purpose:
 * 
 * */

/*
 * application requirements
 * 1. create JFrame GUI
 * 2. user selects data file using JFileChooser 
 * 3. create data structure w/ JScrollPane and JTextArea
 * 4. add user search capabilities
 * 		• JTextField to specify search target (name, index, skill)
 * 		• don't create new data structure
 * 		• you can create structure of found items as a return value
 * */

public class SeaPortProgram extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int WIDTH = 580;
	private static final int HEIGHT = 240;
	
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
		private World world = new World();
		private JTextArea textAreaField = new JTextArea(10, 35);
		private JScrollPane textAreaScrollPane = new JScrollPane(textAreaField, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		private JButton newFileButton = new JButton("New File");
		private JTextField searchField = new JTextField();
		private JRadioButton nameButton = new JRadioButton("Name", true);
		private JRadioButton indexButton = new JRadioButton("Index");
		private JRadioButton  skillButton = new JRadioButton("Skill");
		private JButton searchButton = new JButton("Search");

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
			
			/*
			 * Search area
			 * */
			JPanel searchAreaPanel = new JPanel();
		    ButtonGroup searchButtonGroup = new ButtonGroup();   
		    searchButtonGroup.add(nameButton);
		    searchButtonGroup.add(indexButton);
		    searchButtonGroup.add(skillButton);
		    searchAreaPanel.add(searchField);
		    searchAreaPanel.add(nameButton);
		    searchAreaPanel.add(indexButton);
		    searchAreaPanel.add(skillButton);
		    searchAreaPanel.add(searchButton, BorderLayout.CENTER);
		    searchAreaPanel.setLayout(new BoxLayout(searchAreaPanel, BoxLayout.Y_AXIS));
			
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
			container.add(searchAreaPanel, constraints);
			add(container);
		}

		private void displayMessage(String title, String message) {
			JOptionPane pane = new JOptionPane(message);
			JDialog dialog = pane.createDialog(new MainPanel(), title);
			dialog.setVisible(true);
			dialog.setLocationRelativeTo(null);
			dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
		}
		
		// https://www.youtube.com/watch?v=xkcs25Ustag
		private void openFile() {
			JFileChooser fileChooser = new JFileChooser(".");
			
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
				
			}
		}
	}
}
