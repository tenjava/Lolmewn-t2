package nl.lolmewn.tenjava.spells;

import java.util.ArrayList;
import java.util.List;
import nl.lolmewn.tenjava.Main;
import nl.lolmewn.tenjava.players.SpellsPlayer;
import nl.lolmewn.tenjava.spells.req.LearnRequirement;
import nl.lolmewn.tenjava.spells.req.RequirementType;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 *
 * @author Lolmewn
 */
public class Weather implements Spell {

    @Override
    public int getLearnChance() {
        return 95;
    }

    @Override
    public String getName() {
        return "Weather";
    }

    @Override
    public int getManacost() {
        return 1;
    }

    @Override
    public void cast(Main main, SpellsPlayer sPlayer) {
        Player player = main.getServer().getPlayer(sPlayer.getUuid());
        player.getWorld().setStorm(false);
        player.getWorld().setThundering(false);
        player.getWorld().setWeatherDuration(main.getConfig().getInt("spell.weather.duration", 600) * 20);
        player.setLevel(player.getLevel() - this.getManacost());
        sPlayer.cooldown(this);
    }

    @Override
    public List<String> getLore() {
        return new ArrayList<String>(){{
            this.add(ChatColor.BLUE + "Stop the thunder and the rain...");
            this.add(ChatColor.YELLOW + "and let the sun shine again!");
        }};
    }

    @Override
    public List<LearnRequirement> getLearnRequirements() {
        return new ArrayList<LearnRequirement>(){{
            this.add(new LearnRequirement(RequirementType.SCROLL, 3));
            this.add(new LearnRequirement(RequirementType.EXP, 1));
        }};
    }

    @Override
    public int getCooldown() {
        return 800;
    }

}
