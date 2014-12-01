package memoryGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * A panel that contains both the SimonButtonPanel which contains the MemoryPanel.
 * 
 * Contains the scoreAndLevel panel and the highscore panel.
 * 
 * @author Ryan Wilson
 * @author Thuy
 */
public class MemoryGamePanel extends JPanel implements Serializable
{	
	private static final long serialVersionUID = 2L;
	
	private List<SavedGame> savedGameList = new ArrayList<>();
	
	private SimonButtonPanel buttonPanel; 
	private JPanel scoreAndLevelPanel;
	private JPanel highScorePanel = new JPanel();

	private static JLabel scoreLabel = new JLabel();
	private static JLabel levelLabel = new JLabel("Level: 1");
	private static JButton endButton = new JButton("End/Save");
	private JTextArea highScoreArea = new JTextArea(5, 20);
	
	private static String playersName;
	private static boolean playerSubmittedScore = false;
	private String fileName = "src/memoryGame/sounds/applause-2.wav";
	
	private AudioStream applause;	
	
	/**
	 * In the constructor we ask the player's name and what game board size they want
	 * The levelScoreUpdateTimer keeps track of the score and level by calling the button panel's 
	 * getScore and getLevel methods
	 */
	public MemoryGamePanel()
	{
		setOpaque(true);
		setBackground(new Color(218,165,32));
		setLayout(new BorderLayout());
		
		try
		{
			playersName = JOptionPane.showInputDialog("What is the challenger's name?");
			if (playersName.equals(""))
				playersName = "Challenger Unknown";
		}
		catch(NullPointerException npe)
		{  
			// if the user clicks exit, then the game exits
			JOptionPane.showMessageDialog(null,  "Good bye");
			System.exit(0);
		}
		
		scoreAndLevelPanel = makeScoreAndLevelPanel();
		add(scoreAndLevelPanel, BorderLayout.NORTH);
		
		highScorePanel = setupHighScorePanel();
		add(highScorePanel, BorderLayout.WEST);
		
		// add the MemoryButtonPanel  <-----------------Ryan--------this is where your button panel would go
		buttonPanel = new SimonButtonPanel();
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // <--- if the memory panel an empty border so it looks nice
		add(buttonPanel, BorderLayout.CENTER);

		readHighScores();
		
		// make audio applauses by using an AudioPlayer and a FileInputStream
		try
		{
			InputStream applauseIn = new FileInputStream(fileName); 
			applause = new AudioStream(applauseIn);
			AudioPlayer.player.start(applause);
		} 
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Read in high scores and append it to text area using BufferedReader and FileReader
	 * the info is added to a list of SavedGame which stores the name, score and level of the player
	 */
	public void readHighScores()
	{
		//BufferedReader reader = null;
		try (BufferedReader reader = new BufferedReader(new FileReader("src/memoryGame/scores/MemoryScores.csv")))
		{
		 	String text;
		 	String[] lineParts;
		 	while ((text = reader.readLine()) != null)
			{
				lineParts = text.split(",");
		 		String savedName = lineParts[0];
				int savedScore = Integer.parseInt(lineParts[1]);
				int savedLevel = Integer.parseInt(lineParts[2]);
				savedGameList.add(new SavedGame(savedName, savedScore, savedLevel));
			}
			Collections.sort(savedGameList); 
			printTopScores();
		}  
		catch (NumberFormatException | IOException e1)
		{	
			JOptionPane.showMessageDialog(null, "No high scores yet, maybe you could be the first");
			e1.printStackTrace();
			return; 
		}
	}
	
	/**
	 * The top 6 saved games in the list gets appended to the JTextArea
	 */
	private void printTopScores()
	{
		for (int topSix = 0; topSix < savedGameList.size(); topSix++)
		{	
			if (topSix >= 6)
			{
				break; 
			}
			String savedName = savedGameList.get(topSix).getName();
			int savedScore = savedGameList.get(topSix).getScore();
			int savedLevel = savedGameList.get(topSix).getLevel();
			
			highScoreArea.append(String.format("    %s\n",savedName));
			highScoreArea.append(String.format("    Score: %d\n", savedScore));
			highScoreArea.append(String.format("    Level: %d\n\n", savedLevel));
			
			revalidate();
		}
	}  
	
	/** 
	 * The score and level labels are in the north
	 */
	private JPanel makeScoreAndLevelPanel()
	{
		JPanel scoreAndLevelPanel = new JPanel();
		scoreAndLevelPanel.setLayout(new GridLayout(1, 2, 75, 75));
		scoreAndLevelPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 10, 10));
		scoreAndLevelPanel.setBackground(new Color(240, 230, 140));

		levelLabel.setFont(new Font("Times Roman", Font.BOLD, 30));
		levelLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		scoreAndLevelPanel.add(levelLabel);
		
		scoreLabel.setFont(new Font("Times Roman", Font.BOLD, 30));
		scoreLabel.setHorizontalAlignment(SwingConstants.LEFT);
		scoreLabel.setText(String.format("%s's Score: %d", playersName, SimonButtonPanel.getTotalScore()));
		scoreAndLevelPanel.add(scoreLabel);
		
		return scoreAndLevelPanel;
	}

    /** 
     * The high score list is in the West.  There is a text area and a button to save the player's info
     */
	private JPanel setupHighScorePanel()
	{
		JPanel highScorePanel = new JPanel();
		highScorePanel.setLayout(new BoxLayout(highScorePanel, BoxLayout.PAGE_AXIS));
		highScorePanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 5, 20));
		highScorePanel.setBackground(new Color(240,230,140));
		
		highScoreArea = createHighScoreTextArea();
		highScorePanel.add(highScoreArea);
		
		JPanel endButtonPanel = createEndButtonPanel();
		highScorePanel.add(endButtonPanel); 

		add(highScorePanel, BorderLayout.WEST);
		return highScorePanel;
	}

	private JTextArea createHighScoreTextArea() 
	{
		JTextArea highScoreArea = new JTextArea();
		
		highScoreArea.append(String.format("\n             %s\n", "Top Six High Scores"));
		highScoreArea.append("**********************************************\n\n");
		highScoreArea.setBorder(BorderFactory.createLineBorder(new Color(24, 176, 226), 5));
		highScoreArea.setFont(new Font("Times Roman", Font.BOLD, 20));
		highScoreArea.setEditable(false);		
		return highScoreArea;
	}

	private JPanel createEndButtonPanel() 
	{
		JPanel endButtonPanel = new JPanel();
		endButtonPanel.setBackground(new Color(240,230,140));

		endButton.setFont(new Font("Times Roman", Font.BOLD, 15));
		endButton.setPreferredSize(new Dimension(140, 30));
		endButton.setHorizontalAlignment(JButton.CENTER);
		endButton.setEnabled(false);
		// add action listener to the end button - write to file the name, score and level of the player
		endButton.addActionListener(new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				playerSubmittedScore = true;
				try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("src/memoryGame/scores/MemoryScores.csv", true))))
				{
					if (MemoryPanel.isCorrectOrder())
					{
						writer.printf("%s,%d,%d%n", playersName, SimonButtonPanel.getTotalScore(), MemoryPanel.getLevel());
						savedGameList.add(new SavedGame(playersName, SimonButtonPanel.getTotalScore(), MemoryPanel.getLevel()));
					}
					else
					{
						writer.printf("%s,%d,%d%n", playersName, SimonButtonPanel.getTotalScore(), MemoryPanel.getLevel() - 1);
						savedGameList.add(new SavedGame(playersName, SimonButtonPanel.getTotalScore(), MemoryPanel.getLevel() - 1));
					}
					
					// after we save the score to file, we append the top scores to the score board
					highScoreArea.setText(null);
					highScoreArea.append(String.format("\n             %s\n", "Top Six High Scores"));
					highScoreArea.append("**********************************************\n\n");
					
					Collections.sort(savedGameList);
					printTopScores();
					
					endButton.setEnabled(false);
					
					validate();
				}
				catch(IOException ffe)
				{
					System.out.println("Memory Scores.txt not found");
				}
			}
		}); 
		endButtonPanel.add(endButton);
		
		return endButtonPanel;
	}
	
	public static JLabel getScoreLabel() 
	{
		return scoreLabel;
	}
	public static JLabel getLevelLabel() 
	{
		return levelLabel;
	}
	public static String getPlayersName() 
	{
		return playersName;
	}
	public static JButton getEndButton() 
	{
		return endButton;
	}
	public static void enableEndButton(boolean enable)
	{
		endButton.setEnabled(enable);
		
		if (playerSubmittedScore)
			endButton.setEnabled(false);
	}
	public static void setPlayerSubmittedScoreValue(boolean scoreSubmited)
	{
		playerSubmittedScore = scoreSubmited;
	}
}