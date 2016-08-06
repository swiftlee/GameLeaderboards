package org.royalmc.GL;

import static org.royalmc.GL.TextUtils.formatText;

import java.io.File;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.royalmc.GL.SQLStorage.DataColumn;

public class GameLeaderboards extends JavaPlugin implements Listener {

  private static MySQL mysql;
  private final GameLeaderboards plugin = this;

  ArrayList<String> queries;

  @Override
  public void onEnable() {

    // Create the plugin config
    saveDefaultConfig();
    reloadConfig();

    Bukkit.getPluginManager().registerEvents(this, plugin);
    Bukkit.getPluginManager().registerEvents(new Game_Profile(plugin), plugin);

    // MYSQL SETUP

    /**
     * @param hostname = ip or specified host
     * @param port = default port (3306)
     * @param databasename = your database name
     * @param username = username for your database
     * @param password = password for your database
     */
    
    mysql = new MySQL(getConfig().getString("hostname"), getConfig().getString("port"),
        getConfig().getString("databasename"), getConfig().getString("username"),
        getConfig().getString("password"));

    try {
      /* connection = */mysql.openConnection();

    } catch (Exception e1) {
      e1.printStackTrace();
    }

    try {

      String baseQuery =
          ("ALTER TABLE {tableName} ADD COLUMN {columnLabel} BOOLEAN NOT NULL DEFAULT FALSE;-")
              .replace("{tableName}", getConfig().getString("tablename"));

      queries = new ArrayList<>();

      String query = baseQuery.replace("{columnLabel}", DataColumn.DIAMONDLESS.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.GOLDLESS.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.HORSELESS.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.RODLESS.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.DOUBLEORES.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.ACHIEVEMENTS_AVAILABLE.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.HEART.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.RAIN.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.FIREBALL.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.SPIRIT.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.JUMPMAN.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.FLAME.toString())
          + baseQuery.replace("{columnLabel}", DataColumn.REKT.toString());


      for (String str : query.split("-")) {
        queries.add(str);
      }

      for (String queryList : queries) {
        // System.out.println(queryList);
        PreparedStatement statement = mysql.getConnection().prepareStatement(queryList);
        statement.execute();
      }
      queries.clear();

    } catch (Exception e) {

    }

    // END MYSQL SETUP

    Game_Profile_Config.newConfig3 = new File(plugin.getDataFolder(), "Game_Profile_Config.yml");
    Game_Profile_Config.Game_Profile =
        YamlConfiguration.loadConfiguration(Game_Profile_Config.newConfig3);
    Game_Profile_Config.BaseDefaults();

    Game_Profile.AddSlots();
  }

  ////////////////////////////////////////////////////////////////////////////////////////////
  
  @EventHandler
  public void onPlayerInteract(PlayerInteractEvent e) throws Exception {
    if (e.getAction() == Action.LEFT_CLICK_AIR) {
      //MathUtils.drawSinWave(Effect.WATERDRIP.toString().toUpperCase(), plugin, e.getPlayer(), Math.PI * 8, 2, Math.PI / 8, 1, true);
     // MathUtils.drawCosWave(Effect.WATERDRIP.toString().toUpperCase(), plugin, e.getPlayer(), Math.PI * 8, 2, Math.PI / 8, 1, true);
      MathUtils.drawCircle(Effect.COLOURED_DUST.toString().toUpperCase(), e.getPlayer(), 5, 0, 1, 0, 1, false);
      MathUtils.drawSpiral(Effect.COLOURED_DUST.toString().toUpperCase(), e.getPlayer(), plugin, 1);
    }
  }
  
  /////////////////////////////////////////////////////////////////////////////////////////////////

  public MySQL getMySQL() {
    return mysql;
  }

  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent e) {
    Bukkit.getServer().getScheduler().runTaskLaterAsynchronously(this, new Runnable() {
      public void run() {
        try {
          String query =
              ("INSERT INTO AData (playerUUID) VALUES ('{uuid}') ON DUPLICATE KEY UPDATE playerUUID = playerUUID;")
                  .replace("{uuid}", e.getPlayer().getUniqueId().toString());

          PreparedStatement statement = plugin.getMySQL().getConnection().prepareStatement(query);

          statement = getMySQL().getConnection().prepareStatement(query);
          statement.execute();

          query =
              ("INSERT INTO SpeedUHCTable (playerUUID) VALUES ('{uuid}') ON DUPLICATE KEY UPDATE playerUUID = playerUUID;")
                  .replace("{uuid}", e.getPlayer().getUniqueId().toString());
          statement = getMySQL().getConnection().prepareStatement(query);
          statement.execute();

          query = ("SELECT {columnLabel} FROM SpeedUHCTable WHERE playerUUID = '{uuid}';")
              .replace("{columnLabel}", DataColumn.ACHIEVEMENTS_AVAILABLE.toString())
              .replace("{uuid}", e.getPlayer().getUniqueId().toString());

          statement = plugin.getMySQL().getConnection().prepareStatement(query);
          ResultSet set = statement.executeQuery();

          boolean hasAchievementClaimable;


          if (set.first()) {
            hasAchievementClaimable = set.getBoolean(DataColumn.ACHIEVEMENTS_AVAILABLE.toString());

            if (hasAchievementClaimable) {
              e.getPlayer().sendMessage(formatText("&8+--------------------------------+"));
              e.getPlayer().sendMessage(formatText("&aYou have claimable achievements!"
                  + "\n&7Click the player head to select the &aAchievements &7menu!"));
            }
          }
        } catch (SQLException e1) {
          e1.printStackTrace();
        }
      }
    }, 2);
    
    ScoreboardUtil.giveNewBoard(plugin, e.getPlayer());
  }


  boolean isRunning = false;
  int taskId = 0;

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase("updateLeaderboard") && sender.isOp()) {
      new UpdateStats(this);
      sender.sendMessage(formatText("&8+--------------------------------+"));
      sender.sendMessage(formatText("&6[&bRoyalMC GameLeaderBoards&6] &aUpdated stats manually!"));
      return true;
    } else if (command.getName().equalsIgnoreCase("rlc") && sender.isOp()) {
      this.reloadConfig();
      sender.sendMessage(formatText("&8+--------------------------------+"));
      sender.sendMessage(
          formatText("&6[&bRoyalMC GameLeaderBoards&6] &aReloaded GameLeaderboards config."));
      return true;
    } else if (command.getName().equalsIgnoreCase("updateAuto") && sender.isOp()) {
      if (args.length < 1) {
        if (this.getConfig().getBoolean("autoUpdateStats")) {
          @SuppressWarnings("deprecation")
          int id = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new BukkitRunnable() {
            public void run() {
              if (isRunning == false) {
                isRunning = true;
              }
              new UpdateStats(plugin);
            }
          }, 0, this.getConfig().getLong("autoInterval"));

          sender.sendMessage(formatText("&8+--------------------------------+"));
          sender.sendMessage(
              formatText("&6[&bRoyalMC GameLeaderBoards&6] &aNow updating stats automatically!"));

          taskId = id;
        } else {
          sender.sendMessage(formatText("&8+--------------------------------+"));
          sender.sendMessage(formatText(
              "&6[&bRoyalMC GameLeaderBoards&6] &cTo update stats automatically do: \n&7/updateAuto &atrue"));
        }
      } else if (args.length == 1) {
        if (args[0].equalsIgnoreCase("true")) {
          plugin.getConfig().set("autoUpdateStats", true);
          plugin.saveConfig();
          plugin.reloadConfig();
          sender.sendMessage(formatText("&8+--------------------------------+"));
          sender.sendMessage(formatText(
              "&6[&bRoyalMC GameLeaderBoards&6] &7autoUpdateStats in config has been set to: &atrue"));
        } else if (args[0].equalsIgnoreCase("false")) {
          plugin.getConfig().set("autoUpdateStats", false);
          plugin.saveConfig();
          plugin.reloadConfig();

          if (taskId != 0) {
            Bukkit.getScheduler().cancelTask(taskId);
            sender.sendMessage(formatText("&8+--------------------------------+"));
            sender.sendMessage(formatText(
                "&6[&bRoyalMC GameLeaderBoards&6] &7autoUpdateStats in config has been set to: &cfalse"));
            sender.sendMessage(formatText(
                "\n&6[&bRoyalMC GameLeaderBoards&6] &cStats will no longer automatically update!"));
            taskId = 0;
          } else {
            sender.sendMessage(formatText("&8+--------------------------------+"));
            sender.sendMessage(formatText(
                "&6[&bRoyalMC GameLeaderBoards&6] &7autoUpdateStats in config has been set to: &cfalse"));
          }
        } else {
          sender.sendMessage(formatText("&8+--------------------------------+"));
          sender.sendMessage(
              formatText("&6[&bRoyalMC GameLeaderBoards&6] &c/updateAuto <true/false>"));
        }
      } else {
        sender.sendMessage(formatText("&8+--------------------------------+"));
        sender
            .sendMessage(formatText("&6[&bRoyalMC GameLeaderBoards&6] &c/updateAuto <true/false>"));
      }

      return true;
    } else if (command.getName().equalsIgnoreCase("setLocation") && sender.isOp()) {
      if (args.length == 0) {
        if (sender instanceof Player) {
          Player p = (Player) sender;
          sender.sendMessage(formatText("&8+--------------------------------+"));
          p.sendMessage(formatText("&6[&bRoyalMC GameLeaderBoards&6] &c/setlocation <1-5>"));
        } else {
          sender.sendMessage(formatText("&8+--------------------------------+"));
          sender.sendMessage(formatText(
              "&6[&bRoyalMC GameLeaderBoards&6] &cThis command cannot be executed through the console."));
        }
      } else if (args.length > 1) {
        if (sender instanceof Player) {
          Player p = (Player) sender;
          sender.sendMessage(formatText("&8+--------------------------------+"));
          p.sendMessage(formatText("&6[&bRoyalMC GameLeaderBoards&6] &c/setlocation <1-5>"));
        } else {
          sender.sendMessage(formatText("&8+--------------------------------+"));
          sender.sendMessage(formatText(
              "&6[&bRoyalMC GameLeaderBoards&6] &cThis command cannot be executed through the console."));
        }
      } else {
        if (sender instanceof Player) {
          Player p = (Player) sender;
          if (String.valueOf(args[0]).equals("1")) {
            plugin.getConfig().set("locations.num1.x", p.getLocation().getBlock().getX() + 0.5);
            plugin.getConfig().set("locations.num1.y", p.getLocation().getBlock().getY());
            plugin.getConfig().set("locations.num1.z", p.getLocation().getBlock().getZ() + 0.5);
            try {
              plugin.saveConfig();
              plugin.reloadConfig();
              sender.sendMessage(formatText("&8+--------------------------------+"));
              p.sendMessage(formatText(
                  "&6[&bRoyalMC GameLeaderBoards&6] &aSuccessfully saved new location!"));
              // new UpdateStats(this);
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else if (String.valueOf(args[0]).equals("2")) {
            plugin.getConfig().set("locations.num2.x", p.getLocation().getBlock().getX() + 0.5);
            plugin.getConfig().set("locations.num2.y", p.getLocation().getBlock().getY());
            plugin.getConfig().set("locations.num2.z", p.getLocation().getBlock().getZ() + 0.5);
            try {
              plugin.saveConfig();
              plugin.reloadConfig();
              sender.sendMessage(formatText("&8+--------------------------------+"));
              p.sendMessage(formatText(
                  "&6[&bRoyalMC GameLeaderBoards&6] &aSuccessfully saved new location!"));
              // new UpdateStats(this);
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else if (String.valueOf(args[0]).equals("3")) {
            plugin.getConfig().set("locations.num3.x", p.getLocation().getBlock().getX() + 0.5);
            plugin.getConfig().set("locations.num3.y", p.getLocation().getBlock().getY());
            plugin.getConfig().set("locations.num3.z", p.getLocation().getBlock().getZ() + 0.5);
            try {
              plugin.saveConfig();
              plugin.reloadConfig();
              sender.sendMessage(formatText("&8+--------------------------------+"));
              p.sendMessage(formatText(
                  "&6[&bRoyalMC GameLeaderBoards&6] &aSuccessfully saved new location!"));
              // new UpdateStats(this);
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else if (String.valueOf(args[0]).equals("4")) {
            plugin.getConfig().set("locations.num4.x", p.getLocation().getBlock().getX() + 0.5);
            plugin.getConfig().set("locations.num4.y", p.getLocation().getBlock().getY());
            plugin.getConfig().set("locations.num4.z", p.getLocation().getBlock().getZ() + 0.5);
            try {
              plugin.saveConfig();
              plugin.reloadConfig();
              sender.sendMessage(formatText("&8+--------------------------------+"));
              p.sendMessage(formatText(
                  "&6[&bRoyalMC GameLeaderBoards&6] &aSuccessfully saved new location!"));
              // new UpdateStats(this);
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else if (String.valueOf(args[0]).equals("5")) {
            plugin.getConfig().set("locations.num5.x", p.getLocation().getBlock().getX() + 0.5);
            plugin.getConfig().set("locations.num5.y", p.getLocation().getBlock().getY());
            plugin.getConfig().set("locations.num5.z", p.getLocation().getBlock().getZ() + 0.5);
            try {
              plugin.saveConfig();
              plugin.reloadConfig();
              sender.sendMessage(formatText("&8+--------------------------------+"));
              p.sendMessage(formatText(
                  "&6[&bRoyalMC GameLeaderBoards&6] &aSuccessfully saved new location!"));
              // new UpdateStats(this);
            } catch (Exception e) {
              e.printStackTrace();
            }
          } else {
            sender.sendMessage(formatText("&8+--------------------------------+"));
            p.sendMessage(formatText("&6[&bRoyalMC GameLeaderBoards&6] &c/setlocation <1-5>"));
          }
        } else {
          sender.sendMessage(formatText("&8+--------------------------------+"));
          sender.sendMessage(formatText(
              "&6[&bRoyalMC GameLeaderBoards&6] &cThis command cannot be executed through the console."));
        }
      }

      return true;
    } else if (command.getName().equalsIgnoreCase("rotatearmorstands")) {
      if (args.length == 0) {
        sender.sendMessage(formatText("&8+--------------------------------+"));
        sender.sendMessage(
            formatText("&6[&bRoyalMC GameLeaderBoards&6] &c/rotatearmorstands <ANGLE>"));
      } else if (args.length == 1) {
        for (int i = 1; i <= 360; i++) {
          if (args[0].equals(String.valueOf(i)) || args[0].equals(String.valueOf(0))) {
            sender.sendMessage(formatText("&8+--------------------------------+"));
            sender.sendMessage(formatText(
                "&6[&bRoyalMC GameLeaderBoards&6] &aSuccessfully set rotation in config for armor stands!"));
            plugin.getConfig().set("pitch", Integer.valueOf(args[0]));
            plugin.saveConfig();
            plugin.reloadConfig();
            // new UpdateStats(this);
            break;
          }
        }

        if (Integer.valueOf(args[0]) > 360) {
          sender.sendMessage(formatText("&8+--------------------------------+"));
          sender.sendMessage(formatText(
              "&6[&bRoyalMC GameLeaderBoards&6] &cThe angle specified must be between 0-360!"));
        }
      } else {
        sender.sendMessage(formatText("&8+--------------------------------+"));
        sender.sendMessage(
            formatText("&6[&bRoyalMC GameLeaderBoards&6] &c/rotatearmorstands <ANGLE>"));
      }
      return true;
    }
    return false;
  }


}
