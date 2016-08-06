package org.royalmc.GL;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.royalmc.GL.ScoreboardUtil.ScoreType;

public class SQLStorage {

	public static void queryPlayerSQLData(GameLeaderboards plugin, Player player, DataColumn column, boolean increment, boolean addCoins, boolean addCrystals, int amt, boolean achievements){
		new BukkitRunnable(){public void run(){
			try{
				//Only perform insert/update if the value is changing
				if(increment){
					String query = "INSERT INTO {tablename} (playerUUID, {columnLabel}) VALUES ('{uuid}', 1) ON DUPLICATE KEY UPDATE {columnLabel} = {columnLabel} + 1;".replace("{tablename}", plugin.getConfig().getString("tablename")).replace("{uuid}", player.getUniqueId().toString()).replace("{columnLabel}", column.toString());
					PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
					statement.execute();
				}

				if(addCoins)
				{
					String query = "INSERT INTO {tablename} (playerUUID, TotalCoins) VALUES ('{uuid}', 1) ON DUPLICATE KEY UPDATE {columnLabel} = {columnLabel} + {amt};".replace("{tablename}", plugin.getConfig().getString("tablename")).replace("{uuid}", player.getUniqueId().toString()).replace("{columnLabel}", column.toString()).replace("{amt}", String.valueOf(amt));
					PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
					statement.execute();
				}

				if(addCrystals)
				{
					String query = "INSERT INTO {tablename} (playerUUID, TotalCrystals) VALUES ('{uuid}', 1) ON DUPLICATE KEY UPDATE {columnLabel} = {columnLabel} + {amt};".replace("{tablename}", plugin.getConfig().getString("tablename")).replace("{uuid}", player.getUniqueId().toString()).replace("{columnLabel}", column.toString()).replace("{amt}", String.valueOf(amt));
					PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
					statement.execute();
				}

				//Always query, keep the scoreboard info up-to-date
				if(column != null)
				{
					String query = "SELECT {columnLabel} FROM {tablename} WHERE playerUUID = '{uuid}';".replace("{tablename}", plugin.getConfig().getString("tablename")).replace("{uuid}", player.getUniqueId().toString()).replace("{columnLabel}", column.toString());
					PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
					ResultSet set = statement.executeQuery();

					int value = 0;//Default to 0
					if(set.first()){//Set to first row in the set if available, else do nothing
						value = set.getInt(column.toString());
					}



					//Pass value back to the main thread and update scoreboard
					if(column==DataColumn.TOTAL_KILLS){updateScoreboardTotalKills(plugin, player, value);}
					else if(column==DataColumn.TOTAL_WINS){updateScoreboardTotalWins(plugin, player, value);}
					else if(column==DataColumn.TOTAL_LOSSES){updateScoreboardTotalLosses(plugin, player, value);}
					else if(column==DataColumn.TOTAL_COINS){updateScoreboardTotalCoins(plugin, player, value);}
					else if(column==DataColumn.TOTAL_CRYSTALS){updateScoreboardTotalCrystals(plugin, player, value);}
				}
				else
				{
					if(achievements && column==null){updateScoreboardTotalAchievements(plugin, player);}
				}
			}
			catch (NullPointerException e){
				plugin.getLogger().warning("NullPointerException: No SQL database connection, cannot process "+column+".");
			}
			catch (Exception e){
				e.printStackTrace();
			}
		}}.runTaskAsynchronously(plugin);
	}

	public static int getAchievements(GameLeaderboards plugin, Player player)
	{
		ArrayList<String> columns = new ArrayList<>();
		if(columns.isEmpty())
		{
			columns.add(DataColumn.SWIMMER_ACHIEVEMENT.toString());
			columns.add(DataColumn.WALKER_ACHIEVEMENT.toString());
			columns.add(DataColumn.FIGHTER_ACHIEVEMENT.toString());
			columns.add(DataColumn.BOWKING_ACHIEVEMENT.toString());
		}

		int achievements = 0;

		String query = "";

		for(int i = 0; i < columns.size(); i++)
		{

			query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{uuid}", player.getUniqueId().toString())
					.replace("{columnLabel}", columns.get(i));

			try
			{
				PreparedStatement st = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = st.executeQuery();
				if(set.first())
				{
					boolean addAchievementCompleted = set.getBoolean(columns.get(i));
					if(addAchievementCompleted)
						achievements++;
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
		}

		return achievements;
	}

	//SQL table column enum
	public enum DataColumn {
		TOTAL_KILLS, TOTAL_WINS, TOTAL_LOSSES, TOTAL_COINS, TOTAL_CRYSTALS, DIAMONDLESS, GOLDLESS, HORSELESS, RODLESS, DOUBLEORES, ACHIEVEMENTS_AVAILABLE,
		HEART, RAIN, FIREBALL, SPIRIT, JUMPMAN, FLAME, REKT, SWIMMER_ACHIEVEMENT, WALKER_ACHIEVEMENT, FIGHTER_ACHIEVEMENT, BOWKING_ACHIEVEMENT;
		@Override
		public String toString(){
			if(this==TOTAL_KILLS){return "TotalKills";}
			else if(this==TOTAL_WINS){return "TotalWins";}
			else if(this==TOTAL_LOSSES){return "TotalLosses";}
			else if(this==TOTAL_COINS){return "TotalCoins";}
			else if(this==TOTAL_CRYSTALS){return "TotalCrystals";}
			else if(this==DIAMONDLESS){return "Diamondless";}
			else if(this==GOLDLESS){return "Goldless";}
			else if(this==HORSELESS){return "Horseless";}
			else if(this==RODLESS){return "Rodless";}
			else if(this==DOUBLEORES){return "DoubleOres";}
			else if(this==ACHIEVEMENTS_AVAILABLE){return "AchievementsAvailable";}
			else if(this==HEART){return "Heart";}
			else if(this==RAIN){return "Rain";}
			else if(this==FIREBALL){return "FireBall";}
			else if(this==SPIRIT){return "Spirit";}
			else if(this==JUMPMAN){return "JumpMan";}
			else if(this==FLAME){return "Flame";}
			else if(this==REKT){return "Rekt";}
			else if(this==SWIMMER_ACHIEVEMENT){return "SwimmerAchievement";}
			else if(this==WALKER_ACHIEVEMENT){return "WalkerAchievement";}
			else if(this==FIGHTER_ACHIEVEMENT){return "FighterAchievement";}
			else if(this==BOWKING_ACHIEVEMENT){return "BowKingAchievement";}
			else{return name();}
		}
	}

	//The following updater methods are called inside the async query methods in order to re-sync with the main thread.
	//The scheduled synchronous tasks will apply the new data to the player's scoreboard in a thread-safe manner.

	//Updater method
	private static void updateScoreboardTotalKills(GameLeaderboards plugin, Player player, int kills){
		new BukkitRunnable(){public void run(){
			ScoreboardUtil.setScore(player, ScoreType.TOTAL_KILLS, String.valueOf(kills));
		}}.runTask(plugin);
	}
	//Updater method
	private static void updateScoreboardTotalWins(GameLeaderboards plugin, Player player, int wins){
		new BukkitRunnable(){public void run(){
			//.cacheWins(player, wins);
			ScoreboardUtil.setScore(player, ScoreType.WINS, String.valueOf(wins));
		}}.runTask(plugin);
	}
	//Updater method
	private static void updateScoreboardTotalLosses(GameLeaderboards plugin, Player player, int losses){
		new BukkitRunnable(){public void run(){
			//GamePlayers.cacheLosses(player, losses);
			ScoreboardUtil.setScore(player, ScoreType.LOSSES, String.valueOf(losses));
		}}.runTask(plugin);
	}
	private static void updateScoreboardTotalCoins(GameLeaderboards plugin, Player player, int coins){
		new BukkitRunnable(){public void run(){
			//GamePlayers.cacheLosses(player, coins);
			ScoreboardUtil.setScore(player, ScoreType.COINS, String.valueOf(coins));
		}}.runTask(plugin);
	}
	private static void updateScoreboardTotalCrystals(GameLeaderboards plugin, Player player, int crystals){
		new BukkitRunnable(){public void run(){
			//GamePlayers.cacheLosses(player, crystals);
			ScoreboardUtil.setScore(player, ScoreType.CRYSTALS, String.valueOf(crystals));
		}}.runTask(plugin);
	}

	private static void updateScoreboardTotalAchievements(GameLeaderboards plugin, Player player)
	{
		new BukkitRunnable(){public void run(){
			//GamePlayers.cacheLosses(player, crystals);
			ScoreboardUtil.setScore(player, ScoreType.ACHIEVEMENTS, String.valueOf(getAchievements(plugin, player) + "/4"));
		}}.runTask(plugin);
	}
}
