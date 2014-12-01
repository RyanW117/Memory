package memoryGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The main panel that contains the MemoryPanel and the next level
 * button and the new game button.
 * 
 * This class controls the MemoryPanel and buttons.  It also changes
 * the score.
 * 
 * @author Ryan Wilson
 * @author Thuy
 */
public class SimonButtonPanel extends JPanel
{	
	private static final long serialVersionUID = 5L;

	private static JButton nextLevelButton;
	private JButton newGameButton;
	private static JLabel clicksRemaining;
	
	private MemoryPanel memoryPanel;
	private JPanel controlPanel;
	private Thread timerThread;
	private static int totalScore = 0;
	
	private static boolean changeButtonFont = false;
	private static int boardSize;
	
	public SimonButtonPanel()
	{
		setLayout(new BorderLayout(10, 10));
		setOpaque(true);
		setBackground(new Color(218,165,32));  // gold
		
		boardSize = createBoardSize();
		memoryPanel = new MemoryPanel(boardSize);
		add(memoryPanel, BorderLayout.CENTER);
		
		controlPanel = makeControlPanel();
		add(controlPanel, BorderLayout.SOUTH);
		
		timerThread = new Thread(memoryPanel);
	}
	
	private JPanel makeControlPanel()
	{
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new GridLayout(1, 3, 10, 10));
		controlPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(24, 176, 226), 5),
				BorderFactory.createEmptyBorder(5, 1, 5, 1)));
		controlPanel.setBackground(Color.white);
		
		nextLevelButton = createNextLevelButton();
		controlPanel.add(nextLevelButton);
		
		clicksRemaining = createClicksRemainingLabel();
		controlPanel.add(clicksRemaining);

		newGameButton = createNewGameButton();
		controlPanel.add(newGameButton);
		
		return controlPanel;
	}

	private JLabel createClicksRemainingLabel() 
	{
		JLabel clicksRemaining = new JLabel("Clicks Remaining: " + MemoryButton.getNumberOfClicks());
		clicksRemaining.setFont(new Font("Times Roman", Font.BOLD, 30)); 
		clicksRemaining.setHorizontalAlignment(SwingConstants.CENTER);
		return clicksRemaining;
	}

	private JButton createNewGameButton() 
	{
		JButton newGameButton = new JButton("New Game!");
		newGameButton.setPreferredSize(new Dimension(270, 50));
		newGameButton.setFont(new Font("Times Roman", Font.BOLD, 30)); 
		newGameButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int confirm = JOptionPane.showConfirmDialog(null, "Does the challenger request a new game?", "New Game Request", JOptionPane.OK_CANCEL_OPTION);
				if (!(confirm == JOptionPane.OK_OPTION))
					return;
				
				MemoryPanel.setLevel(0);
				memoryPanel.startGame();
				MemoryPanel.setLevel(0);
				totalScore = 0;
				MemoryGamePanel.enableEndButton(true);
				MemoryGamePanel.setPlayerSubmittedScoreValue(false);
				
				MemoryGamePanel.getScoreLabel().setText(MemoryGamePanel.getPlayersName() + "'s Score: " + totalScore);
				MemoryGamePanel.getLevelLabel().setText("Level: " + (MemoryPanel.getLevel() + 1));
				MemoryGamePanel.getEndButton().setEnabled(false);
				nextLevelButton.setEnabled(true);
				nextLevelButton.setText("Start Game");
				
				for (int i = 0; i < MemoryPanel.getButtonList().size(); i++)
					MemoryPanel.getButtonList().get(i).setEnabled(false);
			}
		});
		
		return newGameButton;
	}

	private JButton createNextLevelButton() 
	{
		final JButton nextLevelButton = new JButton("Start Game");
		nextLevelButton.setPreferredSize(new Dimension(400, 50));
		nextLevelButton.setFont(new Font("Times Roman", Font.ITALIC, 30));
		nextLevelButton.addActionListener(new ActionListener()
		{
			boolean threadStarted = false;
			@Override
			public void actionPerformed(ActionEvent e)
			{
				nextLevelButton.setText("Start Next Level");
				nextLevelButton.setEnabled(false);
								
				if (!threadStarted)
				{
					timerThread.start();
					MemoryPanel.setLevel(1);
					threadStarted = true;
				}
				else
					memoryPanel.startGame();
				
				MemoryPanel.activateLevel();
				
				MemoryGamePanel.getLevelLabel().setText("Level: " + MemoryPanel.getLevel());
				clicksRemaining.setText("Clicks Remaining : " + (MemoryPanel.getLevel() + 2));
			}
		});
		
		return nextLevelButton;
	}
		
	public int createBoardSize()
	{
		String answer;
		int boardSize = 0;
		answer = JOptionPane.showInputDialog("What Memory board size would you like? (2x2 to 25x25)");
		do
		{
			if (answer.equals(""))
				return 4; 
			else
			{
				try
				{
					boardSize = Integer.parseInt(answer); 
					if (boardSize < 2 || boardSize > 25)
						answer = JOptionPane.showInputDialog("Not a valid board size, Pick a valid size.");
					else if (boardSize >= 2 || boardSize <= 25)
						if (boardSize >= 14)
							changeButtonFont = true;
				}
				catch (Exception e)
				{
					answer = JOptionPane.showInputDialog("Not a valid board size, Pick a valid size.");
				}
			}
		} while (boardSize < 2 || boardSize > 25);
		
		return boardSize;
	}
	
	/**
	 * Gives points to the player depending on the current level and boardSize (difficulty)
	 * 
	 * @param isCorrectOrder
	 */
	public static void updateScore(boolean isCorrectOrder)
	{
		if (isCorrectOrder)
		{
			if (boardSize <= 9)
				totalScore += MemoryButton.getButtonClickScores() +
						(int)(50 * MemoryPanel.getLevel() * Double.parseDouble("1.0" + boardSize * 5));
			else if (boardSize == 25)
				totalScore += MemoryButton.getButtonClickScores() + (int)(50 * MemoryPanel.getLevel() * 6);
			else
				totalScore += MemoryButton.getButtonClickScores() +
						(int)(50 * MemoryPanel.getLevel() * Double.parseDouble("2." + boardSize * 4));
		}
		else
			totalScore += MemoryButton.getButtonClickScores();
		
		MemoryGamePanel.getScoreLabel().setText(MemoryGamePanel.getPlayersName() + "'s Score : " + totalScore);
	}

	public static int getTotalScore() 
	{
		return totalScore;
	}
	public static void setTotalScore(int totalScore) 
	{
		SimonButtonPanel.totalScore = totalScore;
	}
	public static JLabel getClicksRemaining() 
	{
		return clicksRemaining;
	}
	public static JButton getNextLevelButton() 
	{
		return nextLevelButton;
	}
	public static boolean shouldFontBeAdjusted()
	{
		return changeButtonFont;
	}
	public static void setChangeButtonFont(boolean changeButtonFont) 
	{
		SimonButtonPanel.changeButtonFont = changeButtonFont;
	}
	public static void nextLevelIsAbleToStart()
	{
		nextLevelButton.setEnabled(true);
	}
}