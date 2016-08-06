package org.royalmc.GL;

import org.bukkit.ChatColor;

public class TextUtils {

	public static String formatText(String text){
		if(text==null){return "null";}
		return ChatColor.translateAlternateColorCodes('&', text);
	}

	public static String useS(double value){
		if(value != 1){return "s";}
		return "";
	}

	public static String hasHave(int count){
		if(count==1){return "has";}
		return "have";
	}

	public static String prefix(String message, GameLeaderboards plugin){
		return  formatText(plugin.getConfig().getString("prefix") + " " + message);
	}

	public static String formatTime(long seconds){
		String result = "";
		long hours = 0;
		long minutes = 0;
		if(seconds >= 60*60){
			hours = seconds / (60*60);
			seconds -= hours*(60*60);
		}
		if(seconds >= 60){
			minutes = seconds / 60;
			seconds -= minutes*60;
		}

		if(hours > 0){
			result = hours+"h:";
			if(minutes < 10){result += "0";}
		}
		result += minutes+"m:";
		if(seconds < 10){result += "0";}
		result += seconds + "s";
		return result;
	}
}
