package org.royalmc.GL;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;

public class MathUtils {

  /**
   * 
   * @author Jon/MC_USER:SwiftLee ROTATION MATRICES: ------------------------
   * 
   *         Rx(α) = (below) --> X-Axis Rotation Matrix
   * 
   *         [ 1 0 0 ] * [x] <-- Not part of the matrix, just shows how we get the rotateAboutX()
   *         method [** x = x **] [ 0 cos(α) -sin(α) ] * [y] <-- ^ (this will give y-coordinate) [ 0
   *         sin(α) cos(α) ] * [z] <-- ^^ (this will give z-coordinate)
   * 
   *         x-coord = x y-coord = (cos(α) * y) - (sin(α) * z) z-coord = (sin(α) * y) + (cos(α) * z)
   * 
   *         ------------------------
   * 
   *         Ry(β) = (below) --> Y-Axis Rotation Matrix
   * 
   *         [ cos(β) 0 sin(β) ] * [x] <-- Not part of the matrix, just shows how we get the
   *         rotateAboutY() method [**(this will give x-coordinate)**] [ 0 1 0 ] * [y] <-- ^ y = y [
   *         -sin(β) 0 cos(β) ] * [z] <-- ^^ (this will give z-coordinate)
   * 
   *         x-coord = (cos(β) * x) + (sin(β) * z) y = y z-coord = (-sin(β) * x) + (cos(β) * z)
   * 
   *         ------------------------
   * 
   *         Rz(γ) = (below) --> Z-Axis Rotation Matrix
   * 
   *         [ cos(γ) -sin(γ) 0 ] * [x] <-- Not part of the matrix, just shows how we get the
   *         rotateAboutZ() method [**(this will give x-coordinate)**] [ sin(γ) cos(γ) 0 ] * [y] <--
   *         ^ (this will give y-coordinate) [ 0 0 1 ] * [z] <-- ^^ z = z
   * 
   *         x-coord = (cos(γ) * x) - (sin(γ) * y) y-coord = (sin(γ) * x) + (cos(γ) * y) z = z
   * 
   *         ------------------------
   * 
   */

  /**
   * Beam travels in the positive z direction eg. x = cos(t); y = sin(t); z = t; MainRotateFunction
   */

  public static Vector rotateFunction(Vector v, Location loc) {
    double yawRadians = Math.toRadians(loc.getYaw());
    double pitchRadians = Math.toRadians(loc.getPitch());

    v = rotateAboutX(v, pitchRadians);
    v = rotateAboutY(v, -yawRadians); // -yawRadians because Minecraft is weird with yaw... No
    // explanation.

    return v;
  }

  public static Vector rotateAboutX(Vector vect, double α) {
    // x stays the same, no need to create a variable
    double y = cos(α) * vect.getY() - sin(α) * vect.getZ();
    double z = sin(α) * vect.getY() + cos(α) * vect.getZ();

    return vect.setY(y).setZ(z);
  }

  public static Vector rotateAboutY(Vector vect, double β) {
    // y stays the same, no need to create a variable
    double x = cos(β) * vect.getX() + sin(β) * vect.getZ();
    double z = -sin(β) * vect.getX() + cos(β) * vect.getZ();

    return vect.setX(x).setZ(z);
  }

  public static Vector rotateAboutZ(Vector vect, double γ) {
    // z stays the same, no need to create a variable
    double x = cos(γ) * vect.getX() - sin(γ) * vect.getY();
    double y = sin(γ) * vect.getX() + cos(γ) * vect.getY();

    return vect.setX(x).setY(y);
  }


  public static void drawSinWave(String effect, GameLeaderboards plugin, Player p, double period,
      double amplitude, double intervalX, double offsetY, boolean playFromPlayerSight) {

    new BukkitRunnable() {

      Location loc = p.getLocation();
      double x = 0;
      double y = 0;
      double z = 0;
      double θ = 0;

      public void run() {

        θ += intervalX;
        x += Math.toRadians(intervalX);
        y = (amplitude * sin(θ)) + offsetY;


        if (playFromPlayerSight) {
          x += loc.getDirection().normalize().getX();
          y += loc.getDirection().normalize().getY();
          z += loc.getDirection().normalize().getZ();
        } else {
          z = 0;
        }

        loc.add(x, y, z);
        loc.getWorld().playEffect(loc, Effect.valueOf(effect), 1);
        loc.subtract(x, y, z);

        if (θ >= period - intervalX) {
          this.cancel();
        }

      }
    }.runTaskTimer(plugin, 0, 1);
  }

  public static void drawCosWave(String effect, GameLeaderboards plugin, Player p, double period,
      double amplitude, double intervalX, double offsetY, boolean playFromPlayerSight) {

    new BukkitRunnable() {

      Location loc = p.getLocation();
      double x = 0;
      double y = 0;
      double z = 0;
      double θ = 0;

      public void run() {

        θ += intervalX;
        x += Math.toRadians(intervalX);
        y = (amplitude * cos(θ)) + offsetY;


        if (playFromPlayerSight) {
          x += loc.getDirection().normalize().getX();
          y += loc.getDirection().normalize().getY();
          z += loc.getDirection().normalize().getZ();
        } else {
          z = 0;
        }

        loc.add(x, y, z);
        loc.getWorld().playEffect(loc, Effect.valueOf(effect), 1);
        loc.subtract(x, y, z);

        if (θ >= period - intervalX) {
          this.cancel();
        }

      }
    }.runTaskTimer(plugin, 0, 1);
  }

  public static void drawCircle(String effect, Player p, double radius, double offsetX,
      double offsetY, double offsetZ, int timesToDrawCircle, boolean playFromPlayerSight) {

    Location loc = p.getLocation().add(offsetX, offsetY, offsetZ);
    double x = 0;
    double y = 0;
    double z = 0;

    for (double θ = 0; θ < timesToDrawCircle * 360; θ++) {

      x = cos(θ) * radius;
      z = sin(θ) * radius;

      if (playFromPlayerSight) {
        x += loc.getDirection().normalize().getX();
        y += loc.getDirection().normalize().getY();
        z += loc.getDirection().normalize().getZ();
      } else {
        y = 0;
      }

      loc.add(x, y, z);
      p.getWorld().playEffect(loc, Effect.valueOf(effect), 1);
      loc.subtract(x, y, z);
      θ++;
    }
  }

  static double y = 0;

  public static void drawSpiral(String effect, Player p, Plugin plugin, double radius)
      throws Exception {

    Location loc = p.getLocation();
    for (double θ = 0; θ <= 200; θ += 0.05) {
      double x = radius * Math.cos(θ);
      double z = radius * Math.sin(θ);

      new BukkitRunnable() {
        public void run() {
          y += 0.5;
        }
      }.runTaskTimer(plugin, 0, (long) θ);

      PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();

      setValue(packet, "a", EnumParticle.FIREWORKS_SPARK);
      setValue(packet, "b", (float) (loc.getX() + x));
      setValue(packet, "c", (float) (loc.getY() + θ));
      setValue(packet, "d", (float) (loc.getZ() + z));
      setValue(packet, "e", 0);
      setValue(packet, "f", 0);
      setValue(packet, "g", 0);
      setValue(packet, "h", 0);
      setValue(packet, "i", 1);

      for (Player online : Bukkit.getOnlinePlayers()) {
        ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
      }
      if (y == 200) {
        y = 0;
      }
    }
  }

  public void broadcastParticles(PacketPlayOutWorldParticles packet) {
    for (Player online : Bukkit.getOnlinePlayers()) {
      ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
    }
  }

  static void setValue(Object instance, String fieldName, Object value) throws Exception {
    Field field = instance.getClass().getDeclaredField(fieldName);
    field.setAccessible(true);
    field.set(instance, value);
  }

  
  /**For fake NPCs ------------------------------------------------------------*/
  public static boolean isFacingNPC(Player p, Location npcLoc, int distance) {

    List<Block> lineOfSight = p.getLineOfSight((HashSet<Material>) null, distance);

    for (Block b : lineOfSight) {

      if (b.getX() == npcLoc.getBlockX() && b.getZ() == npcLoc.getBlockZ()) {
        return true;
      }

    }
    return false;
  }
}
