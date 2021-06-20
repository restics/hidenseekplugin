package me.pixelizedgaming.hidenseek;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class UtilityClass{
    private static JavaPlugin plugin = null;
    public static void setMainClass(JavaPlugin plugin) {
        UtilityClass.plugin = plugin;
    }

    private static class TeleporterRunnable implements Runnable {
        private final Player player;
        private final Location location;
        private TeleporterRunnable(Player player, Location location) {
            this.player = player;
            this.location = location;
        }
        @Override
        public void run() {
            if (location != null) {
                player.teleport(location);
            }
        }
    }

    private static class EffectorRunnable implements Runnable {
        private final Player player;
        private final PotionEffect pe;
        private EffectorRunnable(Player player, PotionEffect pe) {
            this.player = player;
            this.pe = pe;
        }
        @Override
        public void run() {
            if (pe != null) {
                player.addPotionEffect(pe);
            }
        }
    }

    private static class RemoveEffectRunnable implements Runnable {
        private final Player player;
        private RemoveEffectRunnable(Player player) {
            this.player = player;
        }
        @Override
        public void run() {
            for(PotionEffect pe: player.getActivePotionEffects())
                player.removePotionEffect(pe.getType());
        }
    }

    public static void teleportPlayer(Player p, Location loc){
        TeleporterRunnable tr = new TeleporterRunnable(p, loc);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, tr);
    }

    public static void effectPlayer(Player p, PotionEffect pe){
        EffectorRunnable tr = new EffectorRunnable(p, pe);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, tr);
    }

    public static void clearPotionEffects(Player p){
        RemoveEffectRunnable tr = new RemoveEffectRunnable(p);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, tr);
    }

    public static void broadcast(String message){
        Bukkit.getServer().broadcastMessage(message);
    }

    public static void sendActionBar(Player p, String msg){
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(msg));
    }

    public static ItemStack getSeekerWand(){
        ItemStack seekerWand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta seekerWandMeta = seekerWand.getItemMeta();
        seekerWandMeta.setDisplayName(ChatColor.DARK_AQUA + "" + ChatColor.BOLD + "Seeker Wand");
        List<String> lore = new ArrayList<>();
        lore.add(ChatColor.AQUA + "Right-click a hider to find them!");
        seekerWandMeta.setLore(lore);
        seekerWand.setItemMeta(seekerWandMeta);
        return seekerWand;
    }

    public static void saveDefault(){
        broadcast("saved lmao");
        plugin.saveConfig();
    }



}
