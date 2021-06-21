package me.pixelizedgaming.hidenseek;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


/**
 * theres more static here than a van de graff generator
 */

public final class HideNSeek extends JavaPlugin {

    @Override
    public void onEnable() {

        System.out.println("I just shit myself (hide and seek plugin enabled)");
        Objects.requireNonNull(getCommand("setseeker")).setTabCompleter(new TabComplete()); //this shit still doesnt work
        UtilityClass.setMainClass(this);
        getServer().getPluginManager().registerEvents(new EventListener(), this);

        getConfig().addDefault("SeekerSpawn", "0 0 0");
        getConfig().addDefault("SeekerSpawnWorld", "world");
        getConfig().addDefault("HiderSpawn", "0 0 0");
        getConfig().addDefault("HiderSpawnWorld", "world");
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        GameManager.init(getConfig());
    }


    public void loadConfig(){

    }


    @Override
    public void onDisable() {
        System.out.println("I just unshit myself (hide and seek plugin disabled)");
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        switch(command.getName().toLowerCase()){
            case "setseeker":
                if (args.length == 0){
                    sender.sendMessage(ChatColor.RED + "You need to specify a player!");
                }
                else{
                    Player seeker = Bukkit.getPlayerExact(args[0]);
                    if (seeker != null){
                        GameManager.setPlayerAsSeeker(seeker);

                    }else{
                        sender.sendMessage(ChatColor.RED + "This player does not exist!");
                    }
                }
                break;

            case "startgame":
                GameManager.startGame(sender);
                break;

            case "setspawnlocation":
                if (!(sender instanceof Player)) return true;
                Player p = (Player) sender;
                if (args.length == 0){
                    sender.sendMessage(ChatColor.RED + "Usage: /setspawnlocation (seekers/hiders)");
                }else{
                    if (args[0].equalsIgnoreCase("seeker")) {
                        GameManager.setSeekerSpawnLocation(p.getLocation());
                        p.sendMessage(ChatColor.GREEN + "Set Seeker spawn to: "
                                + p.getLocation().getX() + ", "
                                + p.getLocation().getY() + ", "
                                + p.getLocation().getZ());
                    }
                    else if (args[0].equalsIgnoreCase("hider")) {
                        GameManager.setHiderSpawnLocation(p.getLocation());
                        p.sendMessage(ChatColor.GREEN + "Set Hider spawn to: "
                                + p.getLocation().getX() + ", "
                                + p.getLocation().getY() + ", "
                                + p.getLocation().getZ());
                    }
                    else{
                        sender.sendMessage(ChatColor.RED + "Usage: /setspawnlocation (seekers/hiders)");
                    }
                }
                break;
            case "infectionmode":
                GameManager.toggleInfectionMode();
                break;
            case "endgame":
                GameManager.endGame("Game has been stopped by admin", true);
                break;
            case "hnsreload":
                GameManager.init(getConfig());
                break;
            case "stuck":
                if (!(sender instanceof Player)) return true;
                Player player = (Player) sender;
                UtilityClass.teleportPlayer(player,GameManager.getHiderSpawn());
            case "debug":

            case "hnshelp":
                sender.sendMessage(ChatColor.GREEN + "");
            default: //displays help menu



        }
        return true;
    }



}
