package org.royalmc.GL;

import static org.royalmc.GL.TextUtils.formatText;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.EulerAngle;

public class UpdateStats {

	GameLeaderboards plugin;
	public UpdateStats(GameLeaderboards plugin)
	{
		this.plugin = plugin;
		updateStats();
	}

	public void updateStats()
	{
		World w = Bukkit.getWorld(plugin.getConfig().getString("worldName"));

		//Location of armorstands
		Location num1Loc = new Location(w, plugin.getConfig().getDouble("locations.num1.x"), plugin.getConfig().getDouble("locations.num1.y"), plugin.getConfig().getDouble("locations.num1.z"), (float)plugin.getConfig().getDouble("pitch"), 0);
		Location num2Loc = new Location(w, plugin.getConfig().getDouble("locations.num2.x"), plugin.getConfig().getDouble("locations.num2.y"), plugin.getConfig().getDouble("locations.num2.z"), (float)plugin.getConfig().getDouble("pitch"), 0);
		Location num3Loc = new Location(w, plugin.getConfig().getDouble("locations.num3.x"), plugin.getConfig().getDouble("locations.num3.y"), plugin.getConfig().getDouble("locations.num3.z"), (float)plugin.getConfig().getDouble("pitch"), 0);
		Location num4Loc = new Location(w, plugin.getConfig().getDouble("locations.num4.x"), plugin.getConfig().getDouble("locations.num4.y"), plugin.getConfig().getDouble("locations.num4.z"), (float)plugin.getConfig().getDouble("pitch"), 0);
		Location num5Loc = new Location(w, plugin.getConfig().getDouble("locations.num5.x"), plugin.getConfig().getDouble("locations.num5.y"), plugin.getConfig().getDouble("locations.num5.z"), (float)plugin.getConfig().getDouble("pitch"), 0);
		//End location of armorstands

		ArmorStand num1 = w.spawn(num1Loc, ArmorStand.class);
		
		for(Entity e : num1.getNearbyEntities(10, 10, 10))
		{
			if(e.getType() == EntityType.ARMOR_STAND)
			{
				e.remove();
			}
		}
		
		ArmorStand num2 = w.spawn(num2Loc, ArmorStand.class);
		ArmorStand num3 = w.spawn(num3Loc, ArmorStand.class);
		ArmorStand num4 = w.spawn(num4Loc, ArmorStand.class);
		ArmorStand num5 = w.spawn(num5Loc, ArmorStand.class);

		ArrayList<ArmorStand> stands = new ArrayList<>();
		if(stands.size() == 0)
		{
			stands.add(num1);
			stands.add(num2);
			stands.add(num3);
			stands.add(num4);
			stands.add(num5);
		}
		else if(stands.size() > 0 && stands.size() != 5)
		{
			stands.clear();
			stands.add(num1);
			stands.add(num2);
			stands.add(num3);
			stands.add(num4);
			stands.add(num5);
		}

		ArrayList<Integer> wins = new ArrayList<>();
		ArrayList<String> names = new ArrayList<>();

		try
		{
			String query = "SELECT playerUUID, TotalWins FROM {tableName} ORDER BY TotalWins DESC LIMIT 5;".replace("{tableName}", plugin.getConfig().getString("tablename"));
			PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);
			ResultSet set = statement.executeQuery();

			while(set.next())
			{	

				String playername = "";
				playername = Bukkit.getOfflinePlayer(UUID.fromString(set.getString("playerUUID"))).getName();
				names.add(playername);
				int win = 0;
				win = set.getInt("TotalWins");
				wins.add(win);

			}

		} 
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		String c = plugin.getConfig().getString("colors.name");
		String cRank = plugin.getConfig().getString("colors.rank");
		String cWins = plugin.getConfig().getString("colors.wins");
		String customName = "";

		ArrayList<String> customNames = new ArrayList<>();

		for(int i = 0; i < names.size(); i++)
		{
			customName = "";
			customName += "{rank}_".replace("{rank}", getRank((i+1), cRank)); 
			customName += c + names.get(i) + "_";
			customName += cWins + wins.get(i) + " Wins";
			customNames.add(customName);
		}

		int count = 0;
		for(ArmorStand s : stands)
		{
			
			if(s.getLocation().getBlock().getType() == Material.AIR || s.getLocation().getBlock().getType() == Material.ARMOR_STAND)
			{
				ArmorStand invis1 = w.spawn(s.getLocation().add(0,.5f,0), ArmorStand.class);
				ArmorStand invis2 = w.spawn(s.getLocation().add(0,0.26f,0), ArmorStand.class);
				ArmorStand invis3 = w.spawn(s.getLocation().add(0,0.02f,0), ArmorStand.class);

				String name1 = "";
				String name2 = "";
				String name3 = "";

				int i = 0;
				for(String str : customNames.get(count).split("_"))
				{
					if(i==0)
					{
						name1 = str;
						i++;
					}
					else if(i == 1)
					{
						name2 = str;
						i++;
					}
					else if(i == 2)
					{
						name3 = str;
						i = 0;
					}
				}

				s.setSmall(true);
				s.setCustomNameVisible(false);
				s.setVisible(true);
				s.setArms(true);
				s.setBasePlate(true);
				s.setRightArmPose(new EulerAngle(0,0,0));
				s.setHeadPose(new EulerAngle(0, 0, 0));
				s.setItemInHand(getWeapon(count+1));
				s.setChestplate(getChestPlate(count+1));
				s.setLeggings(getPants(count+1));
				s.setBoots(getBoots(count+1));
				s.setGravity(false);

				ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
				SkullMeta skullMeta = (SkullMeta) head.getItemMeta();
				skullMeta.setOwner(names.get(count));
				head.setItemMeta(skullMeta);
				s.setHelmet(head);


				invis1.setVisible(false);
				invis1.setSmall(true);
				invis1.setCustomNameVisible(true);
				invis1.setCustomName(formatText(name1));
				invis1.setGravity(false);

				invis2.setVisible(false);
				invis2.setSmall(true);
				invis2.setCustomNameVisible(true);
				invis2.setCustomName(formatText(name2));
				invis2.setGravity(false);

				invis3.setVisible(false);
				invis3.setSmall(true);
				invis3.setCustomNameVisible(true);
				invis3.setGravity(false);
				invis3.setCustomName(formatText(name3));

				count++;
				
				if(count >= stands.size())
				{
					customNames.clear();
					wins.clear();
					names.clear();
				}
			}
		}
	}

	public String getRank(int rankPlace, String colorCode)
	{
		if(rankPlace == 1)
			return colorCode + "1st";
		else if(rankPlace == 2)
			return colorCode + "2nd";
		else if(rankPlace == 3)
			return colorCode + "3rd";
		else if(rankPlace == 4)
			return colorCode + "4th";
		else if(rankPlace == 5)
			return colorCode + "5th";
		else
			return "";

	}

	public ItemStack getWeapon(int rankPlace)
	{
		if(rankPlace == 1)
			return new ItemStack(Material.AIR, 1);
		else if(rankPlace == 2)
			return new ItemStack(Material.AIR);
		else if(rankPlace == 3)
			return new ItemStack(Material.AIR);
		else if(rankPlace == 4)
			return new ItemStack(Material.AIR);
		else if(rankPlace == 5)
			return new ItemStack(Material.AIR);

		return new ItemStack(Material.AIR);
	}

	public ItemStack getChestPlate(int rankPlace)
	{
		if(rankPlace == 1)
			return new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
		else if(rankPlace == 2)
			return new ItemStack(Material.GOLD_CHESTPLATE, 1);
		else if(rankPlace == 3)
			return new ItemStack(Material.IRON_CHESTPLATE, 1);
		else if(rankPlace == 4)
			return new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
		else if(rankPlace == 5)
			return new ItemStack(Material.LEATHER_CHESTPLATE, 1);

		return new ItemStack(Material.AIR);
	}

	public ItemStack getPants(int rankPlace)
	{
		if(rankPlace == 1)
			return new ItemStack(Material.DIAMOND_LEGGINGS, 1);
		else if(rankPlace == 2)
			return new ItemStack(Material.GOLD_LEGGINGS, 1);
		else if(rankPlace == 3)
			return new ItemStack(Material.IRON_LEGGINGS, 1);
		else if(rankPlace == 4)
			return new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
		else if(rankPlace == 5)
			return new ItemStack(Material.LEATHER_LEGGINGS, 1);

		return new ItemStack(Material.AIR);
	}

	public ItemStack getBoots(int rankPlace)
	{
		if(rankPlace == 1)
			return new ItemStack(Material.DIAMOND_BOOTS, 1);
		else if(rankPlace == 2)
			return new ItemStack(Material.GOLD_BOOTS, 1);
		else if(rankPlace == 3)
			return new ItemStack(Material.IRON_BOOTS, 1);
		else if(rankPlace == 4)
			return new ItemStack(Material.CHAINMAIL_BOOTS, 1);
		else if(rankPlace == 5)
			return new ItemStack(Material.LEATHER_BOOTS, 1);

		return new ItemStack(Material.AIR);
	}
}
