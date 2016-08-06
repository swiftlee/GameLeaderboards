package org.royalmc.GL;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;

public class Game_Profile_Config 
{
	GameLeaderboards plugin;

	public static File newConfig3;
	public static FileConfiguration Game_Profile;

	public Game_Profile_Config(GameLeaderboards plugin){
		this.plugin = plugin;
	}

	public static void BaseSave()
	{
		try
		{
			Game_Profile.save(newConfig3);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	public static void BaseDefaults() {
		if(Game_Profile_Config.Game_Profile.get("Item_To_Click_To_Open_GUI") == null)
		{
			try {
				Game_Profile.addDefault("Item_To_Click_To_Open_Game_Profile.name", "&4&lGame Profile");
				Game_Profile.addDefault("Item_To_Click_To_Open_GUI.Game_Profile_slot_Number", 5);
				Game_Profile.addDefault("Item_To_Click_To_Open_GUI.lore1", "&aGame Data");
				Game_Profile.addDefault("Item_To_Click_To_Open_GUI.lore2", "&4Coins");
				Game_Profile.addDefault("Item_To_Click_To_Open_GUI.lore3", "&1Crystals");

				Game_Profile.addDefault("GUI_Name", "&4&lGame Profile");

				Game_Profile.addDefault("gp1.item", Material.PRISMARINE_CRYSTALS.name());
				Game_Profile.addDefault("gp1.name", "&b&lCrystals");
				Game_Profile.addDefault("gp1.lore1", "&aBalance:");
				Game_Profile.addDefault("gp1.slot", 10);

				Game_Profile.addDefault("gp2.item", Material.ITEM_FRAME.name());
				Game_Profile.addDefault("gp2.name", "&d&lAchievements");
				Game_Profile.addDefault("gp2.lore1", "&aClaim your");
				Game_Profile.addDefault("gp2.lore2", "&aachievements here!");
				Game_Profile.addDefault("gp2.slot", 16);

				Game_Profile.addDefault("gp3.item", Material.CHEST.name());
				Game_Profile.addDefault("gp3.name", "&6&lGamemodes");
				Game_Profile.addDefault("gp3.lore1", "&aPurchase your game-modes");
				Game_Profile.addDefault("gp3.lore2", "&ahere!");
				Game_Profile.addDefault("gp3.slot", 22);

				Game_Profile.addDefault("gp4.item", Material.NAME_TAG.name());
				Game_Profile.addDefault("gp4.name", "&e&lCoins");
				Game_Profile.addDefault("gp4.lore1", "&aBalance:");
				//add in coin balance vis SQL
				Game_Profile.addDefault("gp4.slot", 28);

				Game_Profile.addDefault("gp5.item", Material.BONE.name());
				Game_Profile.addDefault("gp5.name", "&4&lDeath Effects");
				Game_Profile.addDefault("gp5.lore1", "&aPurchase your death effects");
				Game_Profile.addDefault("gp5.lore2", "&ahere!");
				Game_Profile.addDefault("gp5.slot", 34);

				Game_Profile.addDefault("gp6.item", Material.AIR.name());
				Game_Profile.addDefault("gp6.name", "&8NOT_CONFIGURBLE");
				Game_Profile.addDefault("gp6.lore1", "&aNOT_CONFIGURBLE");
				Game_Profile.addDefault("gp6.lore2", "&4NOT_CONFIGURBLE");
				Game_Profile.addDefault("gp6.lore3", "&1NOT_CONFIGURBLE");
				Game_Profile.addDefault("gp6.slot", 0);

				Game_Profile.addDefault("gp7.item", Material.AIR.name());
				Game_Profile.addDefault("gp7.name", "&8NOT_CONFIGURBLE");
				Game_Profile.addDefault("gp7.lore1", "&aNOT_CONFIGURBLE");
				Game_Profile.addDefault("gp7.lore2", "&4NOT_CONFIGURBLE");
				Game_Profile.addDefault("gp7.lore3", "&1NOT_CONFIGURBLE");
				Game_Profile.addDefault("gp7.slot", 0);

				Game_Profile.createSection("deathGUI");

				Game_Profile.addDefault("deathGUI.effect1.item", Material.REDSTONE_BLOCK.name());
				Game_Profile.addDefault("deathGUI.effect1.name", "&b&lHeart");
				Game_Profile.addDefault("deathGUI.effect1.lore1", "&aPlayed when you kill a player in-game!");
				Game_Profile.addDefault("deathGUI.effect1.slot", 10);
				Game_Profile.addDefault("deathGUI.effect1.price", 550);

				Game_Profile.addDefault("deathGUI.effect2.item", Material.LAPIS_BLOCK.name());
				Game_Profile.addDefault("deathGUI.effect2.name", "&d&lRain");
				Game_Profile.addDefault("deathGUI.effect2.lore1", "&aPlayed when you kill a player in-game!");
				Game_Profile.addDefault("deathGUI.effect2.slot", 11);
				Game_Profile.addDefault("deathGUI.effect2.price", 550);
				
				Game_Profile.addDefault("deathGUI.effect3.item", Material.BLAZE_POWDER.name());
				Game_Profile.addDefault("deathGUI.effect3.name", "&6&lFire Ball");
				Game_Profile.addDefault("deathGUI.effect3.lore1", "&aPlayed when you kill a player in-game!");
				Game_Profile.addDefault("deathGUI.effect3.slot", 12);
				Game_Profile.addDefault("deathGUI.effect3.price", 550);
				
				Game_Profile.addDefault("deathGUI.effect4.item", Material.VINE.name());
				Game_Profile.addDefault("deathGUI.effect4.name", "&a&lSpirit");
				Game_Profile.addDefault("deathGUI.effect4.lore1", "&aPlayed when you kill a player in-game!");
				Game_Profile.addDefault("deathGUI.effect4.slot", 13);
				Game_Profile.addDefault("deathGUI.effect4.price", 550);
				
				Game_Profile.addDefault("deathGUI.effect5.item", Material.ARROW.name());
				Game_Profile.addDefault("deathGUI.effect5.name", "&c&lJumpMan");
				Game_Profile.addDefault("deathGUI.effect5.lore1", "&aPlayed when you kill a player in-game!");
				Game_Profile.addDefault("deathGUI.effect5.slot", 14);
				Game_Profile.addDefault("deathGUI.effect5.price", 550);

				Game_Profile.addDefault("deathGUI.effect6.item", Material.LAVA_BUCKET.name());
				Game_Profile.addDefault("deathGUI.effect6.name", "&1&lFlame");
				Game_Profile.addDefault("deathGUI.effect6.lore1", "&aPlayed when you kill a player in-game!");
				Game_Profile.addDefault("deathGUI.effect6.slot", 15);
				Game_Profile.addDefault("deathGUI.effect6.price", 550);

				Game_Profile.addDefault("deathGUI.effect7.item", Material.TNT.name());
				Game_Profile.addDefault("deathGUI.effect7.name", "&4&lRekt");
				Game_Profile.addDefault("deathGUI.effect7.lore1", "&aPlayed when you kill a player in-game!");
				Game_Profile.addDefault("deathGUI.effect7.slot", 16);
				Game_Profile.addDefault("deathGUI.effect7.price", 550);

				Game_Profile.addDefault("diamondless.price", 250);
				Game_Profile.addDefault("diamondless.item", "DIAMOND");
				Game_Profile.addDefault("diamondless.name", "&b&lDiamondless");
				List<String> diamondlessLore = new ArrayList<String>();
				diamondlessLore.add("&eThis game mode removes diamonds from gameplay!");
				diamondlessLore.add("&b&lPrice: {price} &e&lCoins".replace("{price}", String.valueOf(Game_Profile.getInt("diamondless.price"))));
				Game_Profile.addDefault("diamondless.lore", (List<String>)diamondlessLore);
				Game_Profile.addDefault("diamondless.slot", 11);

				Game_Profile.addDefault("goldless.price", 250);
				Game_Profile.addDefault("goldless.item", "GOLD_INGOT");
				Game_Profile.addDefault("goldless.name", "&6&lGoldless");
				List<String> goldlessLore = new ArrayList<String>();
				goldlessLore.add("&eThis game mode removes gold from gameplay!");
				goldlessLore.add("&b&lPrice: {price} &e&lCoins".replace("{price}", String.valueOf(Game_Profile.getInt("goldless.price"))));
				Game_Profile.addDefault("goldless.lore", (List<String>)goldlessLore);
				Game_Profile.addDefault("goldless.slot", 13);

				Game_Profile.addDefault("horseless.price", 250);
				Game_Profile.addDefault("horseless.item", "SADDLE");
				Game_Profile.addDefault("horseless.name", "&3&lHorseless");
				List<String> horselessLore = new ArrayList<String>();
				horselessLore.add("&eThis game mode removes horses from gameplay!");
				horselessLore.add("&b&lPrice: {price} &e&lCoins".replace("{price}", String.valueOf(Game_Profile.getInt("horseless.price"))));
				Game_Profile.addDefault("horseless.lore", (List<String>)horselessLore);
				Game_Profile.addDefault("horseless.slot", 15);

				Game_Profile.addDefault("rodless.price", 250);
				Game_Profile.addDefault("rodless.item", "FISHING_ROD");
				Game_Profile.addDefault("rodless.name", "&a&lRodless");
				List<String> rodlessLore = new ArrayList<String>();
				rodlessLore.add("&eThis game mode removes fishing rods from gameplay!");
				rodlessLore.add("&b&lPrice: {price} &e&lCoins".replace("{price}", String.valueOf(Game_Profile.getInt("rodless.price"))));
				Game_Profile.addDefault("rodless.lore", (List<String>)rodlessLore);
				Game_Profile.addDefault("rodless.slot", 30);

				Game_Profile.addDefault("doubleOres.price", 250);
				Game_Profile.addDefault("doubleOres.item", "COAL_ORE");
				Game_Profile.addDefault("doubleOres.name", "&8&lDouble Ores");
				List<String> doubleOresLore = new ArrayList<String>();
				doubleOresLore.add("&eThis game mode allows double the ores drop from ores!");
				doubleOresLore.add("&b&lPrice: {price} &e&lCoins".replace("{price}", String.valueOf(Game_Profile.getInt("doubleOres.price"))));
				Game_Profile.addDefault("doubleOres.lore", (List<String>)doubleOresLore);
				Game_Profile.addDefault("doubleOres.slot", 32);
				
				Game_Profile.addDefault("swimmer.item", "SAND");
				Game_Profile.addDefault("swimmer.name", "&b&lSwimmer Achievement");
				Game_Profile.addDefault("swimmer.reward", 50);
				List<String> swimmerLore = new ArrayList<String>();
				swimmerLore.add("&eSwim in water for 3 minutes!");
				Game_Profile.addDefault("swimmer.lore", (List<String>)swimmerLore);
				Game_Profile.addDefault("swimmer.slot", 12);
				
				Game_Profile.addDefault("walker.item", "GRASS");
				Game_Profile.addDefault("walker.name", "&b&lWalker Achievement");
				Game_Profile.addDefault("walker.reward", 50);
				List<String> walkerLore = new ArrayList<String>();
				walkerLore.add("&eWalk for 1000 blocks!");
				Game_Profile.addDefault("walker.lore", (List<String>)walkerLore);
				Game_Profile.addDefault("walker.slot", 14);
				
				Game_Profile.addDefault("fighter.item", "DIAMOND_SWORD");
				Game_Profile.addDefault("fighter.name", "&b&lFighter Achievement");
				Game_Profile.addDefault("fighter.reward", 50);
				List<String> fighterLore = new ArrayList<String>();
				fighterLore.add("&eKill 50 players!");
				Game_Profile.addDefault("fighter.lore", (List<String>)fighterLore);
				Game_Profile.addDefault("fighter.slot", 30);

				Game_Profile.addDefault("bowking.item", "BOW");
				Game_Profile.addDefault("bowking.name", "&b&lBow King Achievement");
				Game_Profile.addDefault("bowking.reward", 50);
				List<String> bowKingLore = new ArrayList<String>();
				bowKingLore.add("&eShoot 100 arrows at people!");
				Game_Profile.addDefault("bowking.lore", (List<String>)bowKingLore);
				Game_Profile.addDefault("bowking.slot", 32);
				
				Game_Profile.options().copyDefaults(true);
				Game_Profile.save(newConfig3);
				try {
					Game_Profile.load(newConfig3);
				} catch (InvalidConfigurationException e) {
					e.printStackTrace();
				}
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}
}