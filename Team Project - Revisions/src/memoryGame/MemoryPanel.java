package memoryGame;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

/**
 * A panel that contains a list of MemoryButton buttons and that
 * implements the Runnable interface that is used for multithreading.
 * 
 * @author RyanWilson
 * @author Thuy
 */
public class MemoryPanel extends JPanel implements Runnable
{
	private static final long serialVersionUID = 4L;
	
	private static List<String> correctOrder = new ArrayList<>();
	private static List<MemoryButton> buttonList = new ArrayList<>();
	
	private volatile static boolean levelStart = true;
	private volatile static boolean fail = false;
	private static boolean isCorrectOrder = false;
	
	private static int level = 1;
	private static int boardSize;
	
	public MemoryPanel(int brdSize)
	{
		boardSize = brdSize * brdSize;
		setLayout(new GridLayout(brdSize, brdSize, 15, 15));
		setBorder(new EmptyBorder(10, 10, 10, 10));
		setBackground(new Color(57, 15, 115));
				
		createButtonsAccordingToBoardSize();
		assignSelectedButton();
	}
	
	/**
	 * Changes the state of random buttons to 'isSelected=true'
	 * 
	 * If a button is 'isSelected', then that button is one of the buttons
	 * that must be pressed by the user to complete the level.  The number
	 * of buttons that are changed are proportional to the current level.
	 * The correct 'isSelected' button sequence is stored in the 
	 * correctOrder Integer arrayList.
	 */
	private void assignSelectedButton()
	{
		int randomNumber;
		
		for (int i = 0; i < level + 2; i++)
		{
			randomNumber = (int)(Math.random() * boardSize);
			
			if (!buttonList.get(randomNumber).isSelected())
				buttonList.get(randomNumber).setSelected(true);
			else
				i--;
		}
		
		for (int i = 0; i < boardSize; i++)
			if (buttonList.get(i).isSelected())
				correctOrder.add(buttonList.get(i).getButtonNumber() + "");
		
		Collections.shuffle(correctOrder);
	}
	
	/**
	 * A method that resets variables to proceed to the next level.
	 * 
	 * Activated when the user presses the 'start' or the 'next level' button.  
	 * This method resets the state of all necessary variables so that the user can
	 * proceed to the next level.
	 */
	public void startGame()
	{
		MemoryButton.setNumberOfClicks(0);
		MemoryButton.setButtonClickScores(0);
		correctOrder.clear();
		MemoryButton.getUsersOrder().clear();
		isCorrectOrder = false;
		
		for (int i = 0; i < boardSize; i++)
		{
			buttonList.get(i).setBackground(Color.BLACK);
			buttonList.get(i).setSelected(false);
			buttonList.get(i).setClicked(false);
			buttonList.get(i).setEnabled(true);
		}
		
		level++;
		assignSelectedButton();
	}
	
	/**
	 * Compares the correct button sequence with that which the user chose.
	 */
	public static void compareOrders()
	{
		for (int i = 0; i < buttonList.size(); i++)
			buttonList.get(i).setEnabled(false);
		
		if (correctOrder.equals(MemoryButton.getUsersOrder()))
		{
			isCorrectOrder = true;
			if (MemoryButton.getUsersOrder().size() == boardSize)
			{
				JOptionPane.showMessageDialog(null,
						"Level " + level + " Accomplished\n\n" +
						"WOW, you actually beat the game!\n\n" +
						"You beat a " + (int)Math.sqrt(boardSize) + "X" + (int)Math.sqrt(boardSize) + "!!!");
				SimonButtonPanel.getNextLevelButton().setEnabled(false);
				MemoryGamePanel.getEndButton().setEnabled(true);
			}
			else
			{
				JOptionPane.showMessageDialog(null,
						"Level " + level + " Accomplished\n\n" +
						"press \"Start Next Level\" to go to level  " + (level + 1));
				SimonButtonPanel.nextLevelIsAbleToStart();
			}
			
		}
		else
		{
			isCorrectOrder = false;
			JOptionPane.showMessageDialog(null,
					"You lost!!\n\n" +
					"Level : " + level +
					"\nScore : " + SimonButtonPanel.getTotalScore() +
					"\nCorrect Order :    " + correctOrder.toString().substring(1, (correctOrder.toString().length() - 1)) +
					"\nPlayer  Order :    " + MemoryButton.getUsersOrder().toString().substring(1, (MemoryButton.getUsersOrder().toString().length() - 1)) +
					"\n\nPress the \"New Game\" button to start a new game.");
				
			fail = true;
		}
		SimonButtonPanel.updateScore(isCorrectOrder);
	}
	
	/**
	 * The code that is run when a thread is started.
	 * 
	 * This run method, when executed, continually checks whether a new level
	 * starts.  If so, the user will see an animation identifying the correct
	 * order sequence of the buttons.
	 */
	@Override
	public void run()
	{
		while (true)
		{
			if (levelStart)
			{
				try
				{
					showPlayerCorrectOrderAnimation(true);
				}
				catch (InterruptedException e)
				{
					JOptionPane.showMessageDialog(null, "Thread Stopped Abruptly");
					e.printStackTrace();
				}
				
				levelStart = false;
			}
			
			if (fail)
			{
				try 
				{
					showPlayerCorrectOrderAnimation(false);
				}
				catch (InterruptedException e) 
				{
					JOptionPane.showMessageDialog(null, "Thread Stopped Abruptly");
					e.printStackTrace();
				}
				
				fail = false;
			}
		}
	}

	private void showPlayerCorrectOrderAnimation(boolean nextLevel) throws InterruptedException 
	{
//		Disables all buttons when animation is running
		for (int btn = 0; btn < buttonList.size(); btn++)
			buttonList.get(btn).setEnabled(false);
		
		Color color;
//		The amount of time the thread waits before all of the animations starts.
		Thread.sleep(500);
		
		for (int i = 0; i < level + 2; i++)
		{	
			for (int j = 1; j <= 127; j++)
			{
				color = (nextLevel ? new Color(j * 2, j * 2, 0) : new Color(j * 2, 0, 0));
				buttonList.get(Integer.parseInt(correctOrder.get(i))).setBackground(color);
				if (nextLevel)
					Thread.sleep(4);
				else
					Thread.sleep(7);
			}
			if (nextLevel)
			{
				for (int j = 1; j <= 127; j++)
				{
					color = new Color(255 - j * 2, 255 - j * 2, 0);
					buttonList.get(Integer.parseInt(correctOrder.get(i))).setBackground(color);
					Thread.sleep(4);
				}
			}
			
			if (nextLevel)
				buttonList.get(Integer.parseInt(correctOrder.get(i))).setBackground(Color.BLACK);
//			The time between button animations.
			Thread.sleep(75);
		}
		
//		Re-enable the buttons after all of the animations are done.
		if (nextLevel)
			for (int i = 0; i < buttonList.size(); i++)
				buttonList.get(i).setEnabled(true);
	}
	
	public static void activateLevel()
	{
		levelStart = true;
	}
	public static void setLevelStart(boolean startLevel) 
	{
		levelStart = startLevel;
	}
	public static int getLevel()
	{
		return level;
	}
	public static List<MemoryButton> getButtonList() 
	{
		return buttonList;
	}
	private void createButtonsAccordingToBoardSize()
	{
		for (int i = 0; i < boardSize; i++)
		{
			buttonList.add(new MemoryButton());
			add(buttonList.get(i));
		}
	}
	public static int getBoardSize()
	{
		return boardSize;
	}
	public static void setLevel(int level) 
	{
		MemoryPanel.level = level;
	}
	public static boolean isCorrectOrder()
	{
		return isCorrectOrder;
	}
	public static List<String> getCorrectOrder()
	{
		return correctOrder;
	}
	public static void setBoardSize(int boardSize) 
	{
		MemoryPanel.boardSize = boardSize;
	}
}