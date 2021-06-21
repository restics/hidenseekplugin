package me.pixelizedgaming.hidenseek.players;

import org.bukkit.entity.Player;

public class Seeker extends CustomPlayer{
    private enum Abilities{
        SPEED_BOOST,
        DETECTOR,
    }

    public Seeker(Player p) {
        super(p);
    }
}
