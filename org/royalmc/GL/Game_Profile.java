package org.royalmc.GL;

import static org.royalmc.GL.TextUtils.formatText;
import static org.royalmc.GL.TextUtils.formatTime;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.royalmc.GL.SQLStorage.DataColumn;
import org.royalmc.GL.ScoreboardUtil.ScoreType;

public class Game_Profile implements Listener
{
	GameLeaderboards plugin;
	public Game_Profile(GameLeaderboards plugin)
	{
		this.plugin = plugin;
	}

	static ItemStack GP_GUI_Item;
	static ItemStack GP_Item_1;
	static ItemStack GP_Item_2;
	static ItemStack GP_Item_3;
	static ItemStack GP_Item_4;
	static ItemStack GP_Item_5;
	static ItemStack GP_Item_6;
	static ItemStack GP_Item_7;

	static ItemStack slot1;
	static ItemStack slot2;
	static ItemStack slot3;
	static ItemStack slot4;
	static ItemStack slot5;
	static ItemStack slot6;
	static ItemStack slot7;
	static ItemStack slot8;
	static ItemStack slot9;

	static Inventory GP_INV;
	static Inventory GP_Achievements;
	static Inventory GP_Gamemodes;
	static Inventory gpDeathEffects;


	public static void AddSlots()
	{
		GP_Item_1 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("gp1.item")));
		GP_Item_2 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("gp2.item")));
		GP_Item_3 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("gp3.item")));
		GP_Item_4 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("gp4.item")));
		GP_Item_5 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("gp5.item")));
		GP_Item_6 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("gp6.item")));
		GP_Item_7 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("gp7.item")));

		slot1 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("deathGUI.effect1.item")));                 
		slot2 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("deathGUI.effect2.item")));  
		slot3 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("deathGUI.effect3.item")));  
		slot4 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("deathGUI.effect4.item")));  
		slot5 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("deathGUI.effect5.item")));  
		slot6 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("deathGUI.effect6.item")));  
		slot7 = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("deathGUI.effect7.item")));  
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static ItemStack PlayerSkull(UUID player)
	{
		SkullMeta meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);

		meta.setOwner(Bukkit.getPlayer(player).getName());

		meta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "Lobby");
		if(Bukkit.getPlayer(player) != null)
		{
			meta.setLore(Arrays.asList(ChatColor.WHITE + "" + ChatColor.BOLD + "Click To :  " + ChatColor.YELLOW + "" + ChatColor.BOLD + "Go To Lobby"));
		}

		ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (byte) 3);

		stack.setItemMeta(meta);

		return stack;
	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void openGameProfileGUI(Player p)
	{
		GP_INV = Bukkit.createInventory(p, 45, ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("Item_To_Click_To_Open_Game_Profile.name")));

		if(GP_Item_1.getType() != Material.AIR)
		{
			ItemStack Slot = GP_Item_1;
			ItemMeta im = GP_Item_1.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp1.name")));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp1.lore1"))));

			int crystals = 0;

			String query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", DataColumn.TOTAL_CRYSTALS.toString())
					.replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					crystals = set.getInt(DataColumn.TOTAL_CRYSTALS.toString());
					loreList.add(formatText("&a&l" + String.valueOf(crystals)));
				}
			}
			catch(SQLException e)
			{

			}

			im.setLore(loreList);
			Slot.setItemMeta(im);
		}

		if(GP_Item_2.getType() != Material.AIR)
		{
			ItemStack Slot = GP_Item_2;
			ItemMeta im = GP_Item_2.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp2.name")));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp2.lore1"))));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp2.lore2"))));

			boolean hasAchievementClaimable;

			String query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", DataColumn.ACHIEVEMENTS_AVAILABLE.toString())
					.replace("{uuid}", p.getUniqueId().toString());

			try
			{
				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					hasAchievementClaimable = set.getBoolean(DataColumn.ACHIEVEMENTS_AVAILABLE.toString());

					if(hasAchievementClaimable)
					{
						loreList.add(formatText("&bAchievement available!"));
					}
					else
					{
						loreList.add(formatText("&7No achievement available."));
					}
				}
			}
			catch(SQLException e1)
			{
				e1.printStackTrace();
			}

			im.setLore(loreList);
			Slot.setItemMeta(im);
		}

		if(GP_Item_3.getType() != Material.AIR)
		{
			ItemStack Slot = GP_Item_3;
			ItemMeta im = GP_Item_3.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp3.name")));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp3.lore1"))));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp3.lore2"))));
			//loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp3.lore3"))));
			im.setLore(loreList);
			Slot.setItemMeta(im);
		}

		if(GP_Item_4.getType() != Material.AIR)
		{
			ItemStack Slot = GP_Item_4;
			ItemMeta im = GP_Item_4.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp4.name")));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp4.lore1"))));
			//COINS SQL
			String query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", DataColumn.TOTAL_COINS.toString())
					.replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement st = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = st.executeQuery();
				if(set.first())
				{
					int coins = set.getInt(DataColumn.TOTAL_COINS.toString());
					loreList.add(formatText("&a&l" + String.valueOf(coins)));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			im.setLore(loreList);
			Slot.setItemMeta(im);
		}

		if(GP_Item_5.getType() != Material.AIR)
		{
			ItemStack Slot = GP_Item_5;
			ItemMeta im = GP_Item_5.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp5.name")));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp5.lore1"))));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp5.lore2"))));
			im.setLore(loreList);
			Slot.setItemMeta(im);
		}

		if(GP_Item_6.getType() != Material.AIR)
		{
			ItemStack Slot = GP_Item_6;
			ItemMeta im = GP_Item_6.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp6.name")));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp6.lore1"))));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp6.lore2"))));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp6.lore3"))));
			im.setLore(loreList);
			Slot.setItemMeta(im);
		}

		if(GP_Item_7.getType() != Material.AIR)
		{
			ItemStack Slot = GP_Item_7;
			ItemMeta im = GP_Item_7.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp7.name")));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp7.lore1"))));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp7.lore2"))));
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp7.lore3"))));
			im.setLore(loreList);
			Slot.setItemMeta(im);
		}

		GP_INV.setItem(Game_Profile_Config.Game_Profile.getInt("gp1.slot"), GP_Item_1);
		GP_INV.setItem(Game_Profile_Config.Game_Profile.getInt("gp2.slot"), GP_Item_2);
		GP_INV.setItem(Game_Profile_Config.Game_Profile.getInt("gp3.slot"), GP_Item_3);
		GP_INV.setItem(Game_Profile_Config.Game_Profile.getInt("gp4.slot"), GP_Item_4);
		GP_INV.setItem(Game_Profile_Config.Game_Profile.getInt("gp5.slot"), GP_Item_5);
		GP_INV.setItem(Game_Profile_Config.Game_Profile.getInt("gp6.slot"), GP_Item_6);
		GP_INV.setItem(Game_Profile_Config.Game_Profile.getInt("gp7.slot"), GP_Item_7);


		p.openInventory(GP_INV);

	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void openAchievementsGUI(Player p)
	{
		GP_Achievements = Bukkit.createInventory(p, 45, ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp2.name")));


		ItemStack swimmer = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("swimmer.item")));
		ItemMeta swimmerMeta = swimmer.getItemMeta();
		swimmerMeta.setDisplayName(formatText(Game_Profile_Config.Game_Profile.getString("swimmer.name")));
		swimmerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		List<String> lore = Game_Profile_Config.Game_Profile.getStringList("swimmer.lore");
		if(lore != null)
		{
			for(int i = 0; i < lore.size(); i++){

				lore.set(i, formatText(lore.get(i)));
			}

			String query =  "";
			try
			{
				query = "SELECT TimeSwimmer FROM AData WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					int time = set.getInt("TimeSwimmer");
					if((time + 86400) >= (System.currentTimeMillis()/1000) && time > 0)
					{
						lore.add(formatText("&eTime until available: &7" + formatTime( (time+86400) - (System.currentTimeMillis()/1000) )));
					}
					else
					{
						lore.add(formatText("&eReward: &7" + Game_Profile_Config.Game_Profile.getInt("swimmer.reward") + " coins"));
					}
				}

				query = "SELECT SwimmerAchievement FROM SpeedUHCTable WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
				statement = plugin.getMySQL().getConnection().prepareStatement(query);
				set = statement.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean("SwimmerAchievement");

					if(unlocked)
						lore.add(formatText("&a&lClaim me!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			swimmerMeta.setLore(lore);
		}
		lore.clear();
		swimmer.setItemMeta(swimmerMeta);

		ItemStack walker = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("walker.item")));
		ItemMeta walkerMeta = walker.getItemMeta();
		walkerMeta.setDisplayName(formatText(Game_Profile_Config.Game_Profile.getString("walker.name")));
		walkerMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		lore = Game_Profile_Config.Game_Profile.getStringList("walker.lore");
		if(lore != null)
		{
			for(int i = 0; i < lore.size(); i++){

				lore.set(i, formatText(lore.get(i)));
			}

			String query =  "";
			try
			{
				query = "SELECT TimeWalker FROM AData WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					int time = set.getInt("TimeWalker");
					if((time + 86400) >= (System.currentTimeMillis()/1000) && time > 0)
					{
						lore.add(formatText("&eTime until available: &7" + formatTime( (time+86400) - (System.currentTimeMillis()/1000) )));
					}
					else
					{
						lore.add(formatText("&eReward: &7" + Game_Profile_Config.Game_Profile.getInt("walker.reward") + " coins"));
					}
				}

				query = "SELECT WalkerAchievement FROM SpeedUHCTable WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
				statement = plugin.getMySQL().getConnection().prepareStatement(query);
				set = statement.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean("WalkerAchievement");

					if(unlocked)
						lore.add(formatText("&a&lClaim me!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			walkerMeta.setLore(lore);
		}
		lore.clear();
		walker.setItemMeta(walkerMeta);

		ItemStack fighter = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("fighter.item")));
		ItemMeta fighterMeta = fighter.getItemMeta();
		fighterMeta.setDisplayName(formatText(Game_Profile_Config.Game_Profile.getString("fighter.name")));
		fighterMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		lore = Game_Profile_Config.Game_Profile.getStringList("fighter.lore");
		if(lore != null)
		{
			for(int i = 0; i < lore.size(); i++){

				lore.set(i, formatText(lore.get(i)));
			}

			String query =  "";
			try
			{
				query = "SELECT FighterTime FROM AData WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					int time = set.getInt("FighterTime");
					if((time + 86400) >= (System.currentTimeMillis()/1000) && time > 0)
					{
						lore.add(formatText("&eTime until available: &7" + formatTime( (time+86400) - (System.currentTimeMillis()/1000) )));
					}
					else
					{
						lore.add(formatText("&eReward: &7" + Game_Profile_Config.Game_Profile.getInt("fighter.reward") + " coins"));
					}
				}

				query = "SELECT FighterAchievement FROM SpeedUHCTable WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
				statement = plugin.getMySQL().getConnection().prepareStatement(query);
				set = statement.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean("FighterAchievement");

					if(unlocked)
						lore.add(formatText("&a&lClaim me!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			fighterMeta.setLore(lore);
		}
		lore.clear();
		fighter.setItemMeta(fighterMeta);

		ItemStack bowking = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("bowking.item")));
		ItemMeta bowkingMeta = bowking.getItemMeta();
		bowkingMeta.setDisplayName(formatText(Game_Profile_Config.Game_Profile.getString("bowking.name")));
		bowkingMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
		lore = Game_Profile_Config.Game_Profile.getStringList("bowking.lore");
		if(lore != null)
		{
			for(int i = 0; i < lore.size(); i++){

				lore.set(i, formatText(lore.get(i)));
			}

			String query =  "";
			try
			{

				query = "SELECT BowKingTime FROM AData WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());

				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					int time = set.getInt("BowKingTime");
					if((time + 86400) >= (System.currentTimeMillis()/1000) && time > 0)
					{
						lore.add(formatText("&eTime until available: &7" + formatTime( (time+86400) - (System.currentTimeMillis()/1000) )));
					}
					else
					{
						lore.add(formatText("&eReward: &7" + Game_Profile_Config.Game_Profile.getInt("bowking.reward") + " coins"));
					}
				}

				query = "SELECT BowKingAchievement FROM SpeedUHCTable WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
				statement = plugin.getMySQL().getConnection().prepareStatement(query);
				set = statement.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean("BowKingAchievement");

					if(unlocked)
						lore.add(formatText("&a&lClaim me!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			bowkingMeta.setLore(lore);
		}
		lore.clear();
		bowking.setItemMeta(bowkingMeta);

		GP_Achievements.setItem(Game_Profile_Config.Game_Profile.getInt("swimmer.slot"), swimmer);
		GP_Achievements.setItem(Game_Profile_Config.Game_Profile.getInt("walker.slot"), walker);
		GP_Achievements.setItem(Game_Profile_Config.Game_Profile.getInt("fighter.slot"), fighter);
		GP_Achievements.setItem(Game_Profile_Config.Game_Profile.getInt("bowking.slot"), bowking);

		p.openInventory(GP_Achievements);

	}
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void openGamemodesGUI(Player p, GameLeaderboards plugin)
	{
		GP_Gamemodes = Bukkit.createInventory(p, 54, ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp3.name")));


		//THIS IS SQL STUFF U SET HERE  SO SET SQL STUFF AND ADD THE ITEMS IN HERE HOW U WANT TO
		ItemStack diamondless = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("diamondless.item")));
		ItemMeta diamondlessMeta = diamondless.getItemMeta();
		diamondlessMeta.setDisplayName(formatText(Game_Profile_Config.Game_Profile.getString("diamondless.name")));
		List<String> lore = Game_Profile_Config.Game_Profile.getStringList("diamondless.lore");
		if(lore != null)
		{
			for(int i = 0; i < lore.size(); i++){

				lore.set(i, formatText(lore.get(i)));
			}

			String query =  "SELECT Diamondless FROM SpeedUHCTable WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					boolean hasPurchased = set.getBoolean("Diamondless");

					if(hasPurchased)
						lore.add(formatText("&aUnlocked!"));
					else
						lore.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			diamondlessMeta.setLore(lore);
		}
		diamondless.setItemMeta(diamondlessMeta);

		ItemStack goldless = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("goldless.item")));
		ItemMeta goldlessMeta = goldless.getItemMeta();
		goldlessMeta.setDisplayName(formatText(Game_Profile_Config.Game_Profile.getString("goldless.name")));
		List<String> lore1 = Game_Profile_Config.Game_Profile.getStringList("goldless.lore");
		if(lore1 != null)
		{
			for(int i = 0; i < lore1.size(); i++){

				lore1.set(i, formatText(lore1.get(i)));
			}

			String query =  "SELECT Goldless FROM SpeedUHCTable WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					boolean hasPurchased = set.getBoolean("Goldless");

					if(hasPurchased)
						lore1.add(formatText("&aUnlocked!"));
					else
						lore1.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			goldlessMeta.setLore(lore1);
		}
		goldless.setItemMeta(goldlessMeta);

		ItemStack horseless = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("horseless.item")));
		ItemMeta horselessMeta = horseless.getItemMeta();
		horselessMeta.setDisplayName(formatText(Game_Profile_Config.Game_Profile.getString("horseless.name")));
		List<String> lore2 = Game_Profile_Config.Game_Profile.getStringList("horseless.lore");
		if(lore2 != null)
		{
			for(int i = 0; i < lore2.size(); i++){

				lore2.set(i, formatText(lore2.get(i)));
			}

			String query =  "SELECT Horseless FROM SpeedUHCTable WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					boolean hasPurchased = set.getBoolean("Horseless");

					if(hasPurchased)
						lore2.add(formatText("&aUnlocked!"));
					else
						lore2.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			horselessMeta.setLore(lore2);
		}
		horseless.setItemMeta(horselessMeta);

		ItemStack rodless = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("rodless.item")));
		ItemMeta rodlessMeta = rodless.getItemMeta();
		rodlessMeta.setDisplayName(formatText(Game_Profile_Config.Game_Profile.getString("rodless.name")));
		List<String> lore3 = Game_Profile_Config.Game_Profile.getStringList("rodless.lore");
		if(lore3 != null)
		{
			for(int i = 0; i < lore3.size(); i++){

				lore3.set(i, formatText(lore3.get(i)));
			}
			String query =  "SELECT Rodless FROM SpeedUHCTable WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					boolean hasPurchased = set.getBoolean("Rodless");

					if(hasPurchased)
						lore3.add(formatText("&aUnlocked!"));
					else
						lore3.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			rodlessMeta.setLore(lore3);
		}
		rodless.setItemMeta(rodlessMeta);

		ItemStack doubleOres = new ItemStack(Material.valueOf(Game_Profile_Config.Game_Profile.getString("doubleOres.item")));
		ItemMeta doubleOresMeta = doubleOres.getItemMeta();
		doubleOresMeta.setDisplayName(formatText(Game_Profile_Config.Game_Profile.getString("doubleOres.name")));
		List<String> lore5 = Game_Profile_Config.Game_Profile.getStringList("doubleOres.lore");
		if(lore5 != null)
		{
			for(int i = 0; i < lore5.size(); i++){

				lore5.set(i, formatText(lore5.get(i)));
			}
			String query =  "SELECT DoubleOres FROM SpeedUHCTable WHERE playerUUID = '{uuid}';".replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();

				if(set.first())
				{
					boolean hasPurchased = set.getBoolean("DoubleOres");

					if(hasPurchased)
						lore5.add(formatText("&aUnlocked!"));
					else
						lore5.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}

			doubleOresMeta.setLore(lore5);
		}
		doubleOres.setItemMeta(doubleOresMeta);

		////////////////////////////////////////////////////////////

		GP_Gamemodes.setItem(Game_Profile_Config.Game_Profile.getInt("diamondless.slot"), diamondless);
		GP_Gamemodes.setItem(Game_Profile_Config.Game_Profile.getInt("goldless.slot"), goldless);
		GP_Gamemodes.setItem(Game_Profile_Config.Game_Profile.getInt("horseless.slot"), horseless);
		GP_Gamemodes.setItem(Game_Profile_Config.Game_Profile.getInt("rodless.slot"), rodless);
		GP_Gamemodes.setItem(Game_Profile_Config.Game_Profile.getInt("doubleOres.slot"), doubleOres);

		p.openInventory(GP_Gamemodes);

	}
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void openDeathEffectsGUI(Player p)
	{
		gpDeathEffects = Bukkit.createInventory(p, 27, ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp5.name")));

		gpDeathEffects.clear();

		String query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
				.replace("{columnLabel}", DataColumn.HEART.toString())
				.replace("{uuid}", p.getUniqueId().toString());

		if(slot1.getType() != Material.AIR)
		{
			ItemStack Slot = slot1;
			ItemMeta im = slot1.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("deathGUI.effect1.lore1"))));
			//SQL FOR UNLOCKED
			try
			{
				PreparedStatement st = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = st.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean(DataColumn.HEART.toString());
					if(unlocked)
						loreList.add(formatText("&aUnlocked!"));
					else
						loreList.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			im.setLore(loreList);
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("deathGUI.effect1.name")));
			Slot.setItemMeta(im);
		}

		if(slot2.getType() != Material.AIR)
		{
			ItemStack Slot = slot2;
			ItemMeta im = slot2.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("deathGUI.effect2.lore1"))));
			//SQL FOR UNLOCKED
			query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", DataColumn.RAIN.toString())
					.replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement st = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = st.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean(DataColumn.RAIN.toString());
					if(unlocked)
						loreList.add(formatText("&aUnlocked!"));
					else
						loreList.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			im.setLore(loreList);
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("deathGUI.effect2.name")));
			Slot.setItemMeta(im);
		}

		if(slot3.getType() != Material.AIR)
		{
			ItemStack Slot = slot3;
			ItemMeta im = slot3.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("deathGUI.effect3.lore1"))));
			//SQL FOR UNLOCKED
			query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", DataColumn.FIREBALL.toString())
					.replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement st = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = st.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean(DataColumn.FIREBALL.toString());
					if(unlocked)
						loreList.add(formatText("&aUnlocked!"));
					else
						loreList.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			im.setLore(loreList);
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("deathGUI.effect3.name")));
			Slot.setItemMeta(im);
		}

		if(slot4.getType() != Material.AIR)
		{
			ItemStack Slot = slot4;
			ItemMeta im = slot4.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("deathGUI.effect4.lore1"))));
			//SQL FOR UNLOCKED
			query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", DataColumn.SPIRIT.toString())
					.replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement st = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = st.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean(DataColumn.SPIRIT.toString());
					if(unlocked)
						loreList.add(formatText("&aUnlocked!"));
					else
						loreList.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			im.setLore(loreList);
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("deathGUI.effect4.name")));
			Slot.setItemMeta(im);
		}

		if(slot5.getType() != Material.AIR)
		{
			ItemStack Slot = slot5;
			ItemMeta im = slot5.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("deathGUI.effect5.lore1"))));
			//SQL FOR UNLOCKED
			query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", DataColumn.JUMPMAN.toString())
					.replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement st = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = st.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean(DataColumn.JUMPMAN.toString());
					if(unlocked)
						loreList.add(formatText("&aUnlocked!"));
					else
						loreList.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			im.setLore(loreList);
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("deathGUI.effect5.name")));
			Slot.setItemMeta(im);
		}

		if(slot6.getType() != Material.AIR)
		{
			ItemStack Slot = slot6;
			ItemMeta im = slot6.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("deathGUI.effect6.lore1"))));
			//SQL FOR UNLOCKED
			query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", DataColumn.FLAME.toString())
					.replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement st = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = st.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean(DataColumn.FLAME.toString());
					if(unlocked)
						loreList.add(formatText("&aUnlocked!"));
					else
						loreList.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			im.setLore(loreList);
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("deathGUI.effect6.name")));
			Slot.setItemMeta(im);
		}

		if(slot7.getType() != Material.AIR)
		{
			ItemStack Slot = slot7;
			ItemMeta im = slot7.getItemMeta();
			ArrayList<String> loreList = new ArrayList<String>();
			loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("deathGUI.effect7.lore1"))));
			//SQL FOR UNLOCKED
			query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", DataColumn.REKT.toString())
					.replace("{uuid}", p.getUniqueId().toString());
			try
			{
				PreparedStatement st = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = st.executeQuery();

				if(set.first())
				{
					boolean unlocked = set.getBoolean(DataColumn.REKT.toString());
					if(unlocked)
						loreList.add(formatText("&aUnlocked!"));
					else
						loreList.add(formatText("&cPurchase to unlock!"));
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			im.setLore(loreList);
			im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("deathGUI.effect7.name")));
			Slot.setItemMeta(im);
		}


		gpDeathEffects.setItem(Game_Profile_Config.Game_Profile.getInt("deathGUI.effect1.slot"),slot1);
		gpDeathEffects.setItem(Game_Profile_Config.Game_Profile.getInt("deathGUI.effect2.slot"),slot2);
		gpDeathEffects.setItem(Game_Profile_Config.Game_Profile.getInt("deathGUI.effect3.slot"),slot3);
		gpDeathEffects.setItem(Game_Profile_Config.Game_Profile.getInt("deathGUI.effect4.slot"),slot4);
		gpDeathEffects.setItem(Game_Profile_Config.Game_Profile.getInt("deathGUI.effect5.slot"),slot5);
		gpDeathEffects.setItem(Game_Profile_Config.Game_Profile.getInt("deathGUI.effect6.slot"),slot6);
		gpDeathEffects.setItem(Game_Profile_Config.Game_Profile.getInt("deathGUI.effect7.slot"),slot7);

		notNeededButadded(p, gpDeathEffects);

		p.openInventory(gpDeathEffects);

	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//############################################################################################################################################################//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public void notNeededButadded(Player p, Inventory inv)
	{
		ItemStack Pane = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) RandomNumberPicker());
		ItemMeta meta = Pane.getItemMeta();
		meta.setDisplayName(formatText("&a"));
		Pane.setItemMeta(meta);

		new BukkitRunnable()
		{
			int i = 0;
			public void run()
			{
				i++;

				if(i < inv.getSize())
				{
					if(inv.firstEmpty() != -1)
						inv.setItem(inv.firstEmpty(), Pane);
				}
				else
				{
					cancel();
				}
			}
		}.runTaskTimer(plugin, 0, 2);
	}

	private static int RandomNumberPicker() 
	{
		int RandomSlotPicker;
		ArrayList<Integer> NP = new ArrayList<Integer>();

		NP.add(0);
		NP.add(1);
		NP.add(2);
		NP.add(3);
		NP.add(4);
		NP.add(5);
		NP.add(6);
		NP.add(7);
		NP.add(8);
		NP.add(9);
		NP.add(10);
		NP.add(11);
		NP.add(12);
		NP.add(13);
		NP.add(14);
		NP.add(15);
		Random random = new Random();
		int randomn = random.nextInt(NP.size()-2)+1;
		int selector = NP.get(randomn);
		RandomSlotPicker = selector;
		if(RandomSlotPicker < 0)
			RandomSlotPicker += Math.abs(RandomSlotPicker);
		return RandomSlotPicker;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//############################################################################################################################################################//
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e)
	{
		ItemStack Item2 = PlayerSkull(e.getPlayer().getUniqueId());

		ItemStack Slot2 = Item2;
		ItemMeta im2 = Slot2.getItemMeta();
		ArrayList<String> loreList2 = new ArrayList<String>();
		im2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("Item_To_Click_To_Open_Game_Profile.name")));
		loreList2.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("Item_To_Click_To_Open_GUI.lore1"))));
		loreList2.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("Item_To_Click_To_Open_GUI.lore2"))));
		loreList2.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("Item_To_Click_To_Open_GUI.lore3"))));
		im2.setLore(loreList2);
		Slot2.setItemMeta(im2);

		e.getPlayer().getInventory().setItem(Game_Profile_Config.Game_Profile.getInt("Item_To_Click_To_Open_GUI.Game_Profile_Slot_Number"), Item2);
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void onPlayerClickGUIItem(PlayerInteractEvent e)
	{
		ItemStack Item2 = PlayerSkull(e.getPlayer().getUniqueId());

		ItemStack Slot2 = Item2;
		ItemMeta im2 = Slot2.getItemMeta();
		ArrayList<String> loreList2 = new ArrayList<String>();
		im2.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("Item_To_Click_To_Open_Game_Profile.name")));
		loreList2.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("Item_To_Click_To_Open_GUI.lore1"))));
		loreList2.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("Item_To_Click_To_Open_GUI.lore2"))));
		loreList2.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("Item_To_Click_To_Open_GUI.lore3"))));
		im2.setLore(loreList2);
		Slot2.setItemMeta(im2);

		GP_GUI_Item = Item2;

		if (e.getPlayer().getItemInHand().getType() == GP_GUI_Item.getType())
		{
			openGameProfileGUI(e.getPlayer());
		}
	}
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@EventHandler
	public void PlayerClick(InventoryClickEvent e)
	{
		Player p = (Player)e.getWhoClicked();

		if(e.getInventory().equals(GP_INV))
		{
			if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) 
			{
				if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR) 
				{
					if(GP_Item_2.getType() != Material.AIR)
					{
						ItemStack Slot = GP_Item_2;
						ItemMeta im = GP_Item_2.getItemMeta();
						ArrayList<String> loreList = new ArrayList<String>();
						im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp2.name")));
						loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp2.lore1"))));
						loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp2.lore2"))));
						//loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp2.lore3"))));
						im.setLore(loreList);
						Slot.setItemMeta(im);

						if(e.getCurrentItem().getType() == GP_Item_2.getType())
						{
							e.setCancelled(true);
							e.getWhoClicked().closeInventory();
							openAchievementsGUI(p);
						}
					}

					if(GP_Item_3.getType() != Material.AIR)
					{
						ItemStack Slot = GP_Item_3;
						ItemMeta im = GP_Item_3.getItemMeta();
						ArrayList<String> loreList = new ArrayList<String>();
						im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp3.name")));
						loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp3.lore1"))));
						loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp3.lore2"))));
						//loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp3.lore3"))));
						im.setLore(loreList);
						Slot.setItemMeta(im);

						if(e.getCurrentItem().getType() == GP_Item_3.getType())
						{
							e.setCancelled(true);
							p.closeInventory();
							openGamemodesGUI(p, plugin);
						}
					}

					if(GP_Item_5.getType() != Material.AIR)
					{
						ItemStack Slot = GP_Item_5;
						ItemMeta im = GP_Item_5.getItemMeta();
						ArrayList<String> loreList = new ArrayList<String>();
						im.setDisplayName(ChatColor.translateAlternateColorCodes('&', Game_Profile_Config.Game_Profile.getString("gp5.name")));
						loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp5.lore1"))));
						loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp5.lore2"))));
						//loreList.add((ChatColor.translateAlternateColorCodes('&',Game_Profile_Config.Game_Profile.getString("gp3.lore3"))));
						im.setLore(loreList);
						Slot.setItemMeta(im);

						if(e.getCurrentItem().getType() == GP_Item_5.getType())
						{
							e.setCancelled(true);
							p.closeInventory();
							openDeathEffectsGUI(p);
						}
					}
				}

				if(e.getCurrentItem().getType() == GP_Item_1.getType())
				{
					e.setCancelled(true);
				}
				if(e.getCurrentItem().getType() == GP_Item_5.getType())
				{
					e.setCancelled(true);
				}
				if(e.getCurrentItem().getType() == GP_Item_4.getType())
				{
					e.setCancelled(true);
				}
			}
		}

		if(e.getInventory().getName() != null)
		{
			if(e.getInventory().getName().equals(formatText(Game_Profile_Config.Game_Profile.getString("gp3.name"))))
			{
				if(e.getCurrentItem() != null)
				{
					if(e.getCurrentItem().hasItemMeta())
					{
						if(e.getCurrentItem().getItemMeta().hasDisplayName())
						{
							if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("diamondless.name"))))
							{
								e.setCancelled(true);
								queryCoins(plugin, p, "diamondless.price", DataColumn.DIAMONDLESS, formatText("&b&l"));
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("goldless.name"))))
							{
								e.setCancelled(true);
								queryCoins(plugin, p, "goldless.price", DataColumn.GOLDLESS, formatText("&6&l"));
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("horseless.name"))))
							{
								e.setCancelled(true);
								queryCoins(plugin, p, "horseless.price", DataColumn.HORSELESS, formatText("&3&l"));
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("rodless.name"))))
							{
								e.setCancelled(true);
								queryCoins(plugin, p, "rodless.price", DataColumn.RODLESS, formatText("&a&l"));
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("doubleOres.name"))))
							{
								e.setCancelled(true);
								queryCoins(plugin, p, "doubleOres.price", DataColumn.DOUBLEORES, formatText("&8&l"));
							}
						}
					}
				}
				e.setCancelled(true);
			}

			if(e.getInventory().getName().equals(formatText(Game_Profile_Config.Game_Profile.getString("gp5.name"))))
			{
				if(e.getCurrentItem() != null)
				{
					if(e.getCurrentItem().hasItemMeta())
					{
						if(e.getCurrentItem().getItemMeta().hasDisplayName())
						{
							if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("deathGUI.effect1.name"))))
							{
								queryCrystals(plugin, p, "deathGUI.effect1.price", DataColumn.HEART, formatText("&b&l"));
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("deathGUI.effect2.name"))))
							{
								queryCrystals(plugin, p, "deathGUI.effect1.price", DataColumn.RAIN, formatText("&d&l"));
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("deathGUI.effect3.name"))))
							{
								queryCrystals(plugin, p, "deathGUI.effect1.price", DataColumn.FIREBALL, formatText("&6&l"));
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("deathGUI.effect4.name"))))
							{
								queryCrystals(plugin, p, "deathGUI.effect1.price", DataColumn.SPIRIT, formatText("&a&l"));
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("deathGUI.effect5.name"))))
							{
								queryCrystals(plugin, p, "deathGUI.effect1.price", DataColumn.JUMPMAN, formatText("&c&l"));
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("deathGUI.effect6.name"))))
							{
								queryCrystals(plugin, p, "deathGUI.effect1.price", DataColumn.FLAME, formatText("&1&l"));
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("deathGUI.effect7.name"))))
							{
								queryCrystals(plugin, p, "deathGUI.effect1.price", DataColumn.REKT, formatText("&4&l"));
							}
						}
					}
				}
				e.setCancelled(true);
			}

			if(e.getInventory().getName().equals(formatText(Game_Profile_Config.Game_Profile.getString("gp2.name"))))
			{
				if(e.getCurrentItem() != null)
				{
					if(e.getCurrentItem().hasItemMeta())
					{
						if(e.getCurrentItem().getItemMeta().hasDisplayName())
						{
							if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("swimmer.name"))))
							{
								queryAchievement(plugin, p, DataColumn.SWIMMER_ACHIEVEMENT, "swimmer.reward");
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("walker.name"))))
							{
								queryAchievement(plugin, p, DataColumn.WALKER_ACHIEVEMENT, "walker.reward");
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("fighter.name"))))
							{
								queryAchievement(plugin, p, DataColumn.FIGHTER_ACHIEVEMENT, "fighter.reward");
							}
							else if(e.getCurrentItem().getItemMeta().getDisplayName().equals(formatText(Game_Profile_Config.Game_Profile.getString("bowking.name"))))
							{
								queryAchievement(plugin, p, DataColumn.BOWKING_ACHIEVEMENT, "bowking.reward");
							}
						}
					}
				}
				e.setCancelled(true);
			}
		}
	}
	
	public void queryAchievement(GameLeaderboards plugin, Player p, DataColumn achievement, String configReward)
	{
		String query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
				.replace("{columnLabel}", achievement.toString())
				.replace("{uuid}", p.getUniqueId().toString());
		try
		{
			PreparedStatement st = plugin.getMySQL().getConnection().prepareStatement(query);
			ResultSet set = st.executeQuery();
			
			if(set.first())
			{
				boolean isClaimable = set.getBoolean(achievement.toString());
				if(isClaimable)
				{
					query = ("INSERT INTO SpeedUHCTable (playerUUID, {columnLabel}) VALUES ('{uuid}', FALSE) ON DUPLICATE KEY UPDATE {columnLabel} = FALSE;")
							.replace("{columnLabel}", achievement.toString())
							.replace("{uuid}", p.getUniqueId().toString());
					st = plugin.getMySQL().getConnection().prepareStatement(query);
					st.execute();
					
					if(SQLStorage.getAchievements(plugin, p) <= 0)
					{
						query = ("INSERT INTO SpeedUHCTable (playerUUID, AchievementsAvailable) VALUES ('{uuid}', FALSE) ON DUPLICATE KEY UPDATE AchievementsAvailable = FALSE;")
								.replace("{uuid}", p.getUniqueId().toString());
						st = plugin.getMySQL().getConnection().prepareStatement(query);
						st.execute();
						
						SQLStorage.queryPlayerSQLData(plugin, p, null, false, false, false, 0, true);
						
					}
					else
					{
						SQLStorage.queryPlayerSQLData(plugin, p, null, false, false, false, 0, true);
					}
					
					query = ("INSERT INTO SpeedUHCTable (playerUUID, TotalCoins) VALUES ('{uuid}', {reward}) ON DUPLICATE KEY UPDATE TotalCoins = TotalCoins + {reward};")
							.replace("{uuid}", p.getUniqueId().toString())
							.replace("{reward}", String.valueOf(Game_Profile_Config.Game_Profile.getInt(configReward)));
					
					st = plugin.getMySQL().getConnection().prepareStatement(query);
					st.execute();
					
					SQLStorage.queryPlayerSQLData(plugin, p, DataColumn.TOTAL_COINS, false, false, false, 0, false);
					
					p.sendMessage(formatText("&8+--------------------------------+"));
					p.sendMessage(formatText("&a&lYou have earned: " + Game_Profile_Config.Game_Profile.getInt(configReward) + " &e&lcoins"));
					
					p.closeInventory();
				}
			}
			else
				return;
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}

	public void queryCoins(GameLeaderboards plugin, Player p, String configPrice, DataColumn gameMode, String gameModeColor)
	{
		String query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';").replace("{columnLabel}", DataColumn.TOTAL_COINS.toString())
				.replace("{uuid}", p.getUniqueId().toString());

		try
		{

			String hasAlreadyPurchasedQuery = ("Select {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", gameMode.toString())
					.replace("{uuid}", p.getUniqueId().toString());
			PreparedStatement hasPurchasedStatement = plugin.getMySQL().getConnection().prepareStatement(hasAlreadyPurchasedQuery);
			ResultSet hasPurchasedSet = hasPurchasedStatement.executeQuery();

			boolean hasPurcahsed = false;

			if(hasPurchasedSet.first())
			{
				hasPurcahsed = hasPurchasedSet.getBoolean(gameMode.toString());
			}

			if(hasPurcahsed == false)
			{

				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();
				if(set.first())
				{
					int minPrice = Game_Profile_Config.Game_Profile.getInt(configPrice);
					int currentCoins = set.getInt(DataColumn.TOTAL_COINS.toString());
					boolean hasEnough = (currentCoins >= minPrice ? true : false);

					if(hasEnough)
					{
						query = ("INSERT INTO SpeedUHCTable (playerUUID, TotalCoins) VALUES ('{uuid}', 0) ON DUPLICATE KEY UPDATE {columnLabel} = {columnLabel} - {amt};")
								.replace("{uuid}", p.getUniqueId().toString())
								.replace("{columnLabel}", DataColumn.TOTAL_COINS.toString())
								.replace("{amt}", String.valueOf(minPrice));

						statement = plugin.getMySQL().getConnection().prepareStatement(query);
						statement.execute();

						query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
								.replace("{columnLabel}", DataColumn.TOTAL_COINS.toString())
								.replace("{uuid}", p.getUniqueId().toString());

						statement = plugin.getMySQL().getConnection().prepareStatement(query);
						set = statement.executeQuery();
						if(set.first())
						{
							currentCoins = set.getInt(DataColumn.TOTAL_COINS.toString());
						}

						ScoreboardUtil.setScore(p, ScoreType.COINS, String.valueOf(currentCoins));

						query = ("INSERT INTO SpeedUHCTable (playerUUID, {columnLabel}) VALUES ('{uuid}', TRUE) ON DUPLICATE KEY UPDATE {columnLabel} = TRUE;")
								.replace("{columnLabel}", gameMode.toString())
								.replace("{uuid}", p.getUniqueId().toString());
						statement = plugin.getMySQL().getConnection().prepareStatement(query);
						statement.execute();

						p.sendMessage(formatText("&8+--------------------------------+"));
						p.sendMessage(formatText("&aSuccessful purchase!\n&aYou now have game mode {color}{gamemode} &aunlocked!").replace("{gamemode}", gameMode.toString())
								.replace("{color}", gameModeColor));
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 10, 1);

						p.closeInventory();
					}	
					else
					{
						p.closeInventory();
						p.sendMessage(formatText("&8+--------------------------------+"));
						p.sendMessage(formatText("&8[&eWarning&8] &cYou do not have enough coins!"));
						String grammar = ((minPrice - currentCoins) > 1 ? "coins" : "coin");
						p.sendMessage(formatText("&7You need &e{amt} &7more {coins(s)}!").replace("{amt}", String.valueOf(minPrice - currentCoins))
								.replace("{coins(s)}", grammar));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 10, 1);
					}
				}
			}
			else
			{
				p.closeInventory();
				p.sendMessage(formatText("&8+--------------------------------+"));
				p.sendMessage(formatText("&cYou have already purchased this game mode!"));
				p.playSound(p.getLocation(), Sound.NOTE_BASS, 10, 1);
			}
		}
		catch(SQLException e1)
		{
			e1.printStackTrace();
		}
	}

	public void queryCrystals(GameLeaderboards plugin, Player p, String configPrice, DataColumn particle, String gameModeColor)
	{
		String query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';").replace("{columnLabel}", DataColumn.TOTAL_CRYSTALS.toString())
				.replace("{uuid}", p.getUniqueId().toString());

		try
		{

			String hasAlreadyPurchasedQuery = ("Select {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
					.replace("{columnLabel}", particle.toString())
					.replace("{uuid}", p.getUniqueId().toString());
			PreparedStatement hasPurchasedStatement = plugin.getMySQL().getConnection().prepareStatement(hasAlreadyPurchasedQuery);
			ResultSet hasPurchasedSet = hasPurchasedStatement.executeQuery();

			boolean hasPurcahsed = false;

			if(hasPurchasedSet.first())
			{
				hasPurcahsed = hasPurchasedSet.getBoolean(particle.toString());
			}

			if(hasPurcahsed == false)
			{

				PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
				ResultSet set = statement.executeQuery();
				if(set.first())
				{
					int minPrice = Game_Profile_Config.Game_Profile.getInt(configPrice);
					int currentcrystals = set.getInt(DataColumn.TOTAL_CRYSTALS.toString());
					boolean hasEnough = (currentcrystals >= minPrice ? true : false);

					if(hasEnough)
					{
						query = ("INSERT INTO SpeedUHCTable (playerUUID, Totalcrystals) VALUES ('{uuid}', 0) ON DUPLICATE KEY UPDATE {columnLabel} = {columnLabel} - {amt};")
								.replace("{uuid}", p.getUniqueId().toString())
								.replace("{columnLabel}", DataColumn.TOTAL_CRYSTALS.toString())
								.replace("{amt}", String.valueOf(minPrice));

						statement = plugin.getMySQL().getConnection().prepareStatement(query);
						statement.execute();

						query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
								.replace("{columnLabel}", DataColumn.TOTAL_CRYSTALS.toString())
								.replace("{uuid}", p.getUniqueId().toString());

						statement = plugin.getMySQL().getConnection().prepareStatement(query);
						set = statement.executeQuery();
						if(set.first())
						{
							currentcrystals = set.getInt(DataColumn.TOTAL_CRYSTALS.toString());
						}

						ScoreboardUtil.setScore(p, ScoreType.CRYSTALS, String.valueOf(currentcrystals));

						query = ("INSERT INTO SpeedUHCTable (playerUUID, {columnLabel}) VALUES ('{uuid}', TRUE) ON DUPLICATE KEY UPDATE {columnLabel} = TRUE;")
								.replace("{columnLabel}", particle.toString())
								.replace("{uuid}", p.getUniqueId().toString());
						statement = plugin.getMySQL().getConnection().prepareStatement(query);
						statement.execute();

						p.sendMessage(formatText("&8+--------------------------------+"));
						p.sendMessage(formatText("&aSuccessful purchase!\n&aYou now have effect {color}{effect} &aunlocked!").replace("{effect}", particle.toString())
								.replace("{color}", gameModeColor));

						p.closeInventory();
						p.playSound(p.getLocation(), Sound.LEVEL_UP, 10, 1);
					}	
					else
					{
						p.closeInventory();
						p.sendMessage(formatText("&8+--------------------------------+"));
						p.sendMessage(formatText("&8[&eWarning&8] &cYou do not have enough crystals!"));
						String grammar = ((minPrice - currentcrystals) > 1 ? "crystals" : "crystal");
						p.sendMessage(formatText("&7You need &e{amt} &7more {crystals(s)}!").replace("{amt}", String.valueOf(minPrice - currentcrystals))
								.replace("{crystals(s)}", grammar));
						p.playSound(p.getLocation(), Sound.NOTE_BASS, 10, 1);
					}
				}
			}
			else
			{
				p.closeInventory();
				p.sendMessage(formatText("&8+--------------------------------+"));
				p.sendMessage(formatText("&cYou have already purchased this effect!"));
			}
		}
		catch(SQLException e1)
		{
			e1.printStackTrace();
		}
	}
}