package memoryGame;

/**
 * Contains information about saved games.
 * 
 * @author Thuy
 */
public class SavedGame implements Comparable<SavedGame> 
{
	public String savedName;
	public int savedScore;
	public int level; 
	
	public SavedGame(String playersName, int score, int level)
	{
		this.savedName = playersName;
		this.savedScore = score;
		this.level = level;
	}
	
	@Override
	public String toString()
	{
		return String.format("%s, score = %d, level = %d", savedName, savedScore, level);
	}
	
	@Override
	public int compareTo(SavedGame other)
	{
		if (this.level < other.level)
			return 1;
		else if (this.level > other.level)
			return -1;
		else
		{
			if (this.savedScore < other.savedScore)
				return 1;
			else if (this.savedScore > other.savedScore)
				return -1; 
			else
				return 0;
		}
	}
	
	public String getName()
	{
		return savedName;
	}
	public int getScore()
	{
		return savedScore;
	}
	public int getLevel()
	{
		return level;
	}
}
