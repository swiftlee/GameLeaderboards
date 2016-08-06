package org.royalmc.GL;

import static org.royalmc.GL.ScoreboardUtil.ScoreType.BORDER;
import static org.royalmc.GL.ScoreboardUtil.ScoreType.GAME_TIME;
import static org.royalmc.GL.ScoreboardUtil.ScoreType.LOSSES;
import static org.royalmc.GL.ScoreboardUtil.ScoreType.PLAYERS_LEFT;
import static org.royalmc.GL.ScoreboardUtil.ScoreType.SPECTATORS;
import static org.royalmc.GL.ScoreboardUtil.ScoreType.TEAM_KILLS;
import static org.royalmc.GL.ScoreboardUtil.ScoreType.TOTAL_KILLS;
import static org.royalmc.GL.ScoreboardUtil.ScoreType.WINS;
import static org.royalmc.GL.ScoreboardUtil.ScoreType.YOUR_KILLS;
import static org.royalmc.GL.TextUtils.formatText;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.royalmc.GL.SQLStorage;
import org.royalmc.GL.SQLStorage.DataColumn;
//import org.royalmc.Framework.Team;
import org.royalmc.GL.TextAnimations;
//import org.royalmc.UHC.GamePlayers;
import org.royalmc.GL.GameLeaderboards;

public class ScoreboardUtil {

	private static HashMap<UUID,Scoreboard> playerBoards = new HashMap<>();
	private static HashMap<UUID,Objective> playerSidebars = new HashMap<>();
	private static boolean usingGameSidebar = false;

	public static void giveNewBoard(GameLeaderboards plugin, Player player){
		Scoreboard playerBoard = Bukkit.getScoreboardManager().getNewScoreboard();
		playerBoards.put(player.getUniqueId(),playerBoard);
		player.setScoreboard(playerBoard);

		Objective sidebarObjective = playerBoard.registerNewObjective("sidebarObjective", "dummy");
		sidebarObjective.setDisplaySlot(DisplaySlot.SIDEBAR);
		new TextAnimations(plugin, false, sidebarObjective);
		sidebarObjective.getScore(formatText(WINS+"???")).setScore(10);
		sidebarObjective.getScore(formatText("&7")).setScore(9);
		sidebarObjective.getScore(formatText(TOTAL_KILLS+"???")).setScore(8);
		sidebarObjective.getScore(formatText(ScoreType.MODES_UNLOCK+"")).setScore(7);
		sidebarObjective.getScore(formatText(ScoreType.ACHIEVEMENTS+ "?/4")).setScore(6);
		sidebarObjective.getScore(formatText("&2")).setScore(5);
		sidebarObjective.getScore(formatText(ScoreType.CRYSTALS+"???")).setScore(4);
		sidebarObjective.getScore(formatText(ScoreType.COINS+"???")).setScore(3);
		//String hubAddress = plugin.getConfig().getString("hubAddress");
		//if(hubAddress != null && !hubAddress.isEmpty()){
		sidebarObjective.getScore(formatText("&5")).setScore(2);

		new TextAnimations(plugin, true, sidebarObjective);
		//}
		playerSidebars.put(player.getUniqueId(), sidebarObjective);
		SQLStorage.queryPlayerSQLData(plugin, player, DataColumn.TOTAL_KILLS, false, false, false, 0, false);
		SQLStorage.queryPlayerSQLData(plugin, player, DataColumn.TOTAL_WINS, false, false, false, 0, false);
		SQLStorage.queryPlayerSQLData(plugin, player, DataColumn.TOTAL_COINS, false, false, false, 0, false);
		SQLStorage.queryPlayerSQLData(plugin, player, DataColumn.TOTAL_CRYSTALS, false, false, false, 0, false);
		SQLStorage.queryPlayerSQLData(plugin, player, null, false, false, false, 0, true);
		
		//SQLStorage.queryPlayerSQLData(plugin, player, DataColumn.TOTAL_LOSSES, false);
	}

	public static void setScore(Player player, ScoreType scoreType, String value){
		if((usingGameSidebar && (scoreType==GAME_TIME || scoreType==PLAYERS_LEFT || scoreType==YOUR_KILLS || scoreType==TEAM_KILLS || scoreType==SPECTATORS || scoreType==BORDER)) ||
				(!usingGameSidebar && (scoreType==WINS || scoreType==LOSSES || scoreType==TOTAL_KILLS
				|| scoreType == ScoreType.MODES_UNLOCK || scoreType==ScoreType.ACHIEVEMENTS || scoreType == ScoreType.CRYSTALS || scoreType == ScoreType.COINS))
				){
			Objective sidebarObjective = playerSidebars.get(player.getUniqueId());
			if(sidebarObjective != null){
				for(String oldEntry : sidebarObjective.getScoreboard().getEntries()){
					if(oldEntry.startsWith(scoreType.toString())){
						sidebarObjective.getScoreboard().resetScores(oldEntry);
						if(scoreType == WINS)
							sidebarObjective.getScore(scoreType+value).setScore(10);
						else if(scoreType == TOTAL_KILLS)
							sidebarObjective.getScore(scoreType+value).setScore(8);
						else if(scoreType == ScoreType.CRYSTALS)
							sidebarObjective.getScore(scoreType+value).setScore(4);
						else if(scoreType == ScoreType.COINS)
							sidebarObjective.getScore(scoreType+value).setScore(3);
						else if(scoreType == ScoreType.ACHIEVEMENTS)
							sidebarObjective.getScore(scoreType+value).setScore(6);
						else
							sidebarObjective.getScore(scoreType+value).setScore(1);

						return;
					}
				}
				sidebarObjective.getScore(scoreType+value).setScore(1);
			}
		}
	}

	public enum ScoreType {

		//Lobby Score Types
		WINS(		 "&0&fWins: &a"),
		LOSSES(		 "&1&fLosses: &4"),
		TOTAL_KILLS( "&3&fSolo Kills: &a"),
		MODES_UNLOCK("&5&fModes Unlocked: &7"),
		ACHIEVEMENTS("&5&fAchievements: &7"),
		//GAMES_PLAYED("&5&fGames Played: &7"),
		CRYSTALS(    "&5&fUHC Crystals: &7"),
		COINS(       "&5&fUHC Coins: &7"),

		//In-Game Score Types
		GAME_TIME( "&0&fGame Time: &7"),
		PLAYERS_LEFT("&2&fPlayers Left: &7"),
		YOUR_KILLS("&4&fYour Kills: &7"),
		TEAM_KILLS("&5&fTeam Kills: &7"),
		SPECTATORS("&6&fSpectators: &7"),
		BORDER("&7&fBorder: &7");

		private final String valuePrefix;

		ScoreType(String valuePrefix){
			this.valuePrefix = formatText(valuePrefix);
		}

		@Override
		public String toString(){
			return valuePrefix;
		}
	}
}
