package memoryGame;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;

/**
 * A JButton that has its own specific variables and ActionListener.
 * 
 * @author Ryan Wilson
 * @author Thuy
 */
public class MemoryButton extends JButton
{
	private static final long serialVersionUID = 3L;
	
	private static List<String> usersOrder = new ArrayList<>();
	
	private static int buttonClickScores = 0;
	private static int numberOfClicks = 0;
	private static int num = 0;
	private int buttonNumber = 0;

	private boolean isSelected = false;
	private boolean isClicked = false;
	
	public MemoryButton()
	{
		buttonNumber = num;
		num++;
		
		setBackground(Color.BLACK);
		setText(buttonNumber + "");
		if (!SimonButtonPanel.shouldFontBeAdjusted())
			setFont(new Font("Verdana", Font.BOLD, 30));
		else
			setFont(new Font("Verdana", Font.BOLD, 15));
		setBorder(BorderFactory.createRaisedBevelBorder());
		setForeground(Color.WHITE);		
		setEnabled(false);
		addActionListenerImplementation();
	}

	/**
	 * A method that contains the ActionListener to keep the constructor clean.
	 */
	private void addActionListenerImplementation() 
	{
		addActionListener(new ActionListener() 
		{	
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				if (numberOfClicks < MemoryPanel.getLevel() + 2)
				{
					setBackground(new Color(
							(int)(Math.random() * 256),
							(int)(Math.random() * 256),
							(int)(Math.random() * 256)));
					
					if (!isClicked)
					{
						isClicked = true;
						numberOfClicks++;
						if (isSelected)
							buttonClickScores+=10;

						usersOrder.add(getButtonNumber() + "");
					}
					else
					{
						isClicked = false;
						numberOfClicks--;
						setBackground(Color.BLACK);
						usersOrder.remove(getButtonNumber() + "");
						if (isSelected)
							buttonClickScores-=10;
					}
				}
				
				SimonButtonPanel.getClicksRemaining().setText(
						"Clicks Remaining : " + ((MemoryPanel.getLevel() + 2) - numberOfClicks));
				
				if (numberOfClicks == MemoryPanel.getLevel() + 2)
				{
					MemoryPanel.compareOrders();
					MemoryGamePanel.enableEndButton(true);
					buttonClickScores = 0;
				}
			}
		});
	}

	public static void setNumberOfClicks(int numberOfClicks) 
	{
		MemoryButton.numberOfClicks = numberOfClicks;
	}
	public static int getNumberOfClicks() 
	{
		return numberOfClicks;
	}
	public boolean isSelected()
	{
		return isSelected;
	}
	public void setSelected(boolean isSelected)
	{
		this.isSelected = isSelected;
	}
	public int getButtonNumber() 
	{
		return buttonNumber;
	}
	public static List<String> getUsersOrder() 
	{
		return usersOrder;
	}
	public void setClicked(boolean isClicked) 
	{
		this.isClicked = isClicked;
	}
	public static int getButtonClickScores() 
	{
		return buttonClickScores;
	}
	public static void setButtonClickScores(int buttonClickScores) 
	{
		MemoryButton.buttonClickScores = buttonClickScores;
	}
	public static void setNum(int num) 
	{
		MemoryButton.num = num;
	}
}
