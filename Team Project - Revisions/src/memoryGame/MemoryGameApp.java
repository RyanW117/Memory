package memoryGame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;

public class MemoryGameApp extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private MemoryGamePanel memoryPanel;
	private JMenuBar menuBar = new JMenuBar();
	
	public static void main(String[] args)
	{
		SwingUtilities.invokeLater(new Runnable() 
		{
			@Override
			public void run()
			{
				new MemoryGameApp();
			}
			
		});
	}
	
	public MemoryGameApp() 
	{
		super("Memory!");
		setVisible(true);
		setResizable(true);
		setExtendedState(MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setBackground(new Color(218,165,32));
		setJMenuBar(menuBar);
		JMenu newGame = createNewGameJMenu();
		menuBar.add(newGame);
		
		memoryPanel = new MemoryGamePanel();
		add(memoryPanel, BorderLayout.CENTER);
		
		validate();
	}

	private JMenu createNewGameJMenu() 
	{
		JMenuItem newGameItem = new JMenuItem("Start a new game challenge");
		
		JMenu newGame = new JMenu("      New game board size");
		newGame.setFont(new Font("Times Roman", Font.BOLD, 16)); 
		newGame.add(newGameItem);
		newGameItem.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				resetImportantVariables();
				new MemoryGameApp();
			}

			private void resetImportantVariables()
			{
				setVisible(false);
				MemoryButton.getUsersOrder().clear();
				MemoryPanel.getButtonList().clear();
				MemoryPanel.getCorrectOrder().clear();
				MemoryButton.setNum(0);
				MemoryButton.setNumberOfClicks(0);
				SimonButtonPanel.setTotalScore(0);
				MemoryPanel.setBoardSize(0);
				MemoryPanel.setLevel(1);
				SimonButtonPanel.setChangeButtonFont(false);
			}
		});
		
		return newGame;
	}
}