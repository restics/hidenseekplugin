package me.pixelizedgaming.hidenseek.players;

import org.bukkit.entity.Player;

public abstract class CustomPlayer {
    private Player player;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public CustomPlayer(Player p){
        player = p;
    }





}
