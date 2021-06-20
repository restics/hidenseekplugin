package me.pixelizedgaming.hidenseek;

import com.sun.javafx.webkit.UtilitiesImpl;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


public class EventListener implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        Player movingPlayer = e.getPlayer();
        if (GameManager.getSeekers().contains(movingPlayer) && GameManager.isIsCountDownPhase()){
            UtilityClass.teleportPlayer(movingPlayer, GameManager.getSeekerSpawn());
        }
    }

    @EventHandler
    public void onRightClick(PlayerInteractAtEntityEvent e){
        if (!(e.getRightClicked() instanceof Player)) return;
        Player p = e.getPlayer();
        Player clicked = (Player) e.getRightClicked();

        if (p.getInventory().getItemInMainHand().equals(UtilityClass.getSeekerWand())
        && GameManager.isIsMainPhase() && GameManager.getSeekers().contains(p)
                && GameManager.getHiders().contains(clicked)) {
            UtilityClass.broadcast(ChatColor.GOLD + clicked.getName() + " has been found!");
            UtilityClass.effectPlayer(clicked, new PotionEffect(PotionEffectType.BLINDNESS, 100, 1));
            GameManager.removeHider(clicked);
            UtilityClass.teleportPlayer(clicked, GameManager.getSeekerSpawn());
            if (GameManager.getHiders().size() < 1){
                GameManager.endGame("Seekers caught all hiders!", false);
                return;
            }
            if (GameManager.isIsInfectionMode()){
                GameManager.addSeeker(clicked);
                clicked.getInventory().addItem(UtilityClass.getSeekerWand());
            }
            else{
                clicked.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 1));
                clicked.setAllowFlight(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){
        if(!e.getPlayer().hasPermission("pixelizedhns.breakblocks")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onHungerDeplete(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e){
        if (!e.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){

        Player joined = e.getPlayer();
        //if player was a seeker/ hider, remove them

        GameManager.removeHider(joined);
        GameManager.removeSeeker(joined);
        joined.getInventory().clear();
        for(Player p: Bukkit.getOnlinePlayers()){
            p.setAllowFlight(false);
        }

        if (!GameManager.isIsRunning()) return;

        if (GameManager.getHiders().size() < 1){
            GameManager.endGame("Seekers caught all hiders!", false);
        }
        if (GameManager.getSeekers().size() < 1){
            UtilityClass.broadcast(ChatColor.RED + "All the seekers left! Ending game....");
            GameManager.endGame("All the seekers left!", true);
        }


    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(GameManager.isIsRunning()){
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().sendMessage(ChatColor.GREEN + "There's a game going on! Spectating...");
            e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 99999, 1));
        }


    }


}
