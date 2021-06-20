package me.pixelizedgaming.hidenseek;

import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameManager implements Runnable {

    private static int gameCountdown = 300; // time in seconds
    private static int countdown = 30; //countdown time in seconds

    private static ArrayList<Player> Seekers;
    private static ArrayList<Player> Hiders;
    private static Location seekerSpawn;
    private static Location hiderSpawn;
    private static FileConfiguration config;

    private static boolean isInfectionMode;
    private static volatile boolean isRunning;
    private static boolean isCountDownPhase;
    private static boolean isMainPhase;

    public static boolean isIsMainPhase() {
        return isMainPhase;
    }

    public static void setIsMainPhase(boolean isMainPhase) {
        GameManager.isMainPhase = isMainPhase;
    }

    public static boolean isIsInfectionMode() {
        return isInfectionMode;
    }

    public static void setIsInfectionMode(boolean isInfectionMode) {
        GameManager.isInfectionMode = isInfectionMode;
    }

    public static Location getSeekerSpawn() {
        return seekerSpawn;
    }

    public static void setSeekerSpawn(Location seekerSpawn) {
        GameManager.seekerSpawn = seekerSpawn;
    }

    public static Location getHiderSpawn() {
        return hiderSpawn;
    }

    public static void setHiderSpawn(Location hiderSpawn) {
        GameManager.hiderSpawn = hiderSpawn;
    }

    public static boolean isIsRunning() {
        return isRunning;
    }

    public static void setIsRunning(boolean isRunning) {
        GameManager.isRunning = isRunning;
    }

    public static boolean isIsCountDownPhase() {
        return isCountDownPhase;
    }

    public static void setIsCountDownPhase(boolean isCountDownPhase) {
        GameManager.isCountDownPhase = isCountDownPhase;
    }

    public static ArrayList<Player> getSeekers() {
        return Seekers;
    }

    public static ArrayList<Player> getHiders() {
        return Hiders;
    }

    public static void removeSeeker(Player p){
        Seekers.remove(p);
    }

    public static void removeHider(Player p){
        Hiders.remove(p);
    }

    public static void addSeeker(Player p){
        Seekers.add(p);
        p.setCustomName(ChatColor.RED + p.getName());
    }

    public static void addHider(Player p){
        Hiders.add(p);
        p.setCustomName(ChatColor.GREEN + p.getName());
    }



    private static Thread gameThread;


    /**
     * chewsday
     * @param config
     */
    public static void init(FileConfiguration config){




        Seekers = new ArrayList<>();
        Hiders = new ArrayList<>();
        isInfectionMode = true;
        UtilityClass.saveDefault();
        GameManager.config = config;

        String[] seekerCoordsString = Objects.requireNonNull(config.getString("SeekerSpawn")).split(" ");
        String seekerSpawnWorld = config.getString("SeekerSpawnWorld");
        assert seekerSpawnWorld != null;

        seekerSpawn = new Location(Bukkit.getWorld(seekerSpawnWorld), Integer.parseInt(seekerCoordsString[0]), Integer.parseInt(seekerCoordsString[1]), Integer.parseInt(seekerCoordsString[2]));

        String[] hiderCoordsString = Objects.requireNonNull(config.getString("HiderSpawn")).split(" ");
        String hiderSpawnWorld = config.getString("HiderSpawnWorld");
        assert hiderSpawnWorld != null;
        hiderSpawn = new Location(Bukkit.getWorld(hiderSpawnWorld), Integer.parseInt(hiderCoordsString[0]), Integer.parseInt(hiderCoordsString[1]), Integer.parseInt(hiderCoordsString[2]));


        UtilityClass.broadcast( "SeekerSpawn: " + config.getString("SeekerSpawn") + ", HiderSpawn:" +  config.getString("HiderSpawn"));
        UtilityClass.broadcast( "In-Memory SeekerSpawn: " + seekerSpawn + ", In-Memory HiderSpawn:" +  seekerSpawn);
    }

    /**
     * Starts the game. If a seeker isn't already selected, randomly selects one.
     * @param cs - command sender to return messages to
     */
    public static void startGame(CommandSender cs){
        if (isRunning){
            cs.sendMessage(ChatColor.RED + "Game is already running!");
        }
        else if (hiderSpawn == null || seekerSpawn == null){
            cs.sendMessage(ChatColor.RED + "Make sure you set the spawn locations right!");
            cs.sendMessage(ChatColor.RED + "Do /setspawnlocation [seeker/hider] at the appropriate locations.");
        }
        else if (Bukkit.getOnlinePlayers().size() < 3){
            cs.sendMessage(ChatColor.RED + "Not enough players to start! Make sure there's at least 3 players online!");
        }
        else{
            isRunning = true;
            if(gameThread == null){
                gameThread = new Thread(new GameManager());
                gameThread.start();
            }
        }
    }

    public static void setPlayerAsSeeker(Player p){
        if (p.isOnline()) {
            Seekers.clear();
            Seekers.add(p);
            for (Player hider: Bukkit.getOnlinePlayers()){
                if (!hider.equals(p)){
                    Hiders.add(hider);
                }
            }
            UtilityClass.broadcast(ChatColor.GOLD + p.getName() + ChatColor.YELLOW + " was selected to be a seeker!");
        }
    }

    public static void randomPlayerAsSeeker(){
        int random = new Random().nextInt(Bukkit.getOnlinePlayers().size());
        Player player = ((List<Player>) Bukkit.getOnlinePlayers()).get(random);
        setPlayerAsSeeker(player);
    }

    public static void setSeekerSpawnLocation(Location l){
        seekerSpawn = l;
        String locString = l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ();
        config.set("SeekerSpawn", locString);
        config.set("SeekerSpawnWorld", Objects.requireNonNull(l.getWorld()).getName());
        UtilityClass.saveDefault();
    }

    public static void setHiderSpawnLocation(Location l){
        hiderSpawn = l;
        String locString = l.getBlockX() + " " + l.getBlockY() + " " + l.getBlockZ();
        config.set("HiderSpawn", locString);
        config.set("HiderSpawnWorld", Objects.requireNonNull(l.getWorld()).getName());
        UtilityClass.saveDefault();
    }

    public static void toggleInfectionMode() {
        isInfectionMode = !isInfectionMode;
        UtilityClass.broadcast(ChatColor.GOLD + "Infection mode: " + ChatColor.YELLOW + isInfectionMode);
    }


    @Override
    public void run() {
        /**
         * countdown phase
         */
        Scoreboard score = Bukkit.getScoreboardManager().getMainScoreboard();
        score.getTeam("hide").setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.NEVER);


        //if no seekers pick random one
        if (Seekers.size() == 0){
            randomPlayerAsSeeker();
        }
        UtilityClass.broadcast(ChatColor.GREEN + "Game is starting!!!");

        for (Player p : Seekers){
            score.getTeam("hide").addEntry(p.getName());
            UtilityClass.teleportPlayer(p,seekerSpawn);
        }

        for (Player p : Hiders){
            score.getTeam("hide").addEntry(p.getName());
            UtilityClass.teleportPlayer(p,hiderSpawn);
        }

        for(Player s: Seekers){
            s.getInventory().addItem(UtilityClass.getSeekerWand());
            UtilityClass.effectPlayer(s, new PotionEffect(PotionEffectType.BLINDNESS,countdown * 20,0 ));
        }

        isCountDownPhase = true;
        try {
            synchronized (gameThread) {
                for (int cd = countdown; cd > 0; cd--) {
                    gameThread.wait(1000);
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        UtilityClass.sendActionBar(p,ChatColor.GREEN + "Seekers will be released in " + ChatColor.DARK_GREEN + cd + ChatColor.GREEN + " seconds!");
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isCountDownPhase = false;

        isMainPhase = true;
        try {
            synchronized (gameThread) {
                for (int cd = gameCountdown; cd > 0; cd--) {
                    if (!isMainPhase){
                        return;
                    }
                    gameThread.wait(1000);
                    for(Player p : Bukkit.getOnlinePlayers()) {
                        UtilityClass.sendActionBar(p,ChatColor.GREEN + "There are " + ChatColor.DARK_GREEN + cd + ChatColor.GREEN + " seconds until the hiders win!");
                    }
                    if (cd == 120){
                        UtilityClass.broadcast(ChatColor.GREEN + "2 minutes remaining! Giving all seekers speed 2!");
                        for (Player p : Seekers){
                            UtilityClass.effectPlayer(p, new PotionEffect(PotionEffectType.SPEED, 120 * 20,2));
                        }
                    }

                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        isMainPhase = false;
        endGame("Seekers ran out of time!", true);
    }

    public static void endGame(String reason, boolean isHiderWin){
        isRunning = false;
        isMainPhase = false;
        isCountDownPhase = false;
        gameThread = null;

        if(isHiderWin){
            for(Player p: Seekers){
                p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "GAME OVER", ChatColor.YELLOW + reason,10,140,20);
            }
            for(Player p: Seekers){
                p.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "GAME OVER", ChatColor.YELLOW + reason,10,140,20);
            }
        }
        else{
            for(Player p: Seekers){
                p.sendTitle(ChatColor.GREEN + "" + ChatColor.BOLD + "GAME OVER", ChatColor.YELLOW + reason,10,140,20);
            }
            for(Player p: Seekers){
                p.sendTitle(ChatColor.RED + "" + ChatColor.BOLD + "GAME OVER", ChatColor.YELLOW + reason,10,140,20);
            }
        }
        for(Player p: Bukkit.getOnlinePlayers()){
            p.getInventory().clear();
            UtilityClass.clearPotionEffects(p);
            p.setAllowFlight(false);
        }

        for(Player p: Hiders){
            UtilityClass.effectPlayer(p, new PotionEffect(PotionEffectType.GLOWING, 100, 1));
        }



        Seekers.clear();
        Hiders.clear();

        //this shit doesnt work please help me
        new BukkitRunnable(){
            @Override
            public void run() {
                for(int i = 5; i > 0; i--){
                    for(Player p: Bukkit.getOnlinePlayers()) {
                        UtilityClass.sendActionBar(p, ChatColor.GOLD + "Teleporting to spawn in " + ChatColor.YELLOW + i + ChatColor.GOLD + " seconds!");
                    }
                    try {
                        wait(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.runTaskLater(Bukkit.getPluginManager().getPlugin("HideNSeek"), 100);
        for(Player p: Bukkit.getOnlinePlayers()){
            UtilityClass.teleportPlayer(p, hiderSpawn);
        }

    }

}




