package me.pixelizedgaming.hidenseek.players;

import org.bukkit.entity.Player;

public class Hider extends CustomPlayer{
    private enum Abilities{
        PUNCH_BOW,
        FREEZE_WAND
    }
    public Hider(Player p) {
        super(p);
    }
}
