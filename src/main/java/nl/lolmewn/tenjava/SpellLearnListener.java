package nl.lolmewn.tenjava;

import java.util.Random;
import nl.lolmewn.tenjava.players.SpellsPlayer;
import nl.lolmewn.tenjava.spells.Spell;
import nl.lolmewn.tenjava.spells.SpellType;
import nl.lolmewn.tenjava.spells.req.LearnRequirement;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Lolmewn
 */
public class SpellLearnListener implements Listener {

    private final Main plugin;

    public SpellLearnListener(Main plugin) {
        this.plugin = plugin;
    }

    public void pickup(InventoryClickEvent event) {
        if (!event.getInventory().getName().equals(plugin.getConfig().getString("inventory.name"))) {
            return;
        }
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        SpellsPlayer sp = plugin.getPlayerManager().get(event.getWhoClicked().getUniqueId());
        ItemStack stack = event.getCurrentItem();
        if (stack == null) {
            return;
        }
        Spell spell = this.findSpell(stack.getItemMeta().getDisplayName());
        if (spell == null) {
            plugin.getLogger().info("Spell not found, but is in inventory: " + stack.getItemMeta().getDisplayName());
            return;
        }
        if (sp.knowsSpell(spell)) {
            player.sendMessage(Messages.getMessage("already-know-spell"));
            return;
        }
        if (!hasRequirements(player, spell)) {
            player.sendMessage(Messages.getMessage("not-meets-requirements"));
            return;
        }
        takeRequirements(player, spell);
        if (!learnSpell(sp, spell)) {
            player.damage(1);
            player.sendMessage(Messages.getMessage("spell-learning-failed"));
        }else{
            player.sendMessage(Messages.getMessage("spell-learnt", new Pair("%spell%", spell.getName())));
        }
    }

    public Spell findSpell(String itemName) {
        for (SpellType type : SpellType.values()) {
            if (type.getSpell().getName().equals(itemName)) {
                return type.getSpell();
            }
        }
        return null;
    }

    public boolean learnSpell(SpellsPlayer player, Spell spell) {
        Random rant = new Random();
        if (rant.nextDouble() * 100 < spell.getLearnChance()) {
            player.learnSpell(spell);
            return true;
        }
        return false;
    }

    private boolean hasRequirements(Player player, Spell spell) {
        for (LearnRequirement req : spell.getLearnRequirements()) {
            switch (req.getType()) {
                case EXP:
                    if (player.getLevel() < (int) req.getValue()) {
                        return false;
                    }
                    break;
                case ITEMSTACK:
                    if (!player.getInventory().contains((ItemStack) req.getValue())) {
                        return false;
                    }
                    break;
                case SCROLL:
                    int required = (int) req.getValue();
                    for (ItemStack stack : player.getInventory().getContents()) {
                        if (stack.hasItemMeta() && stack.getItemMeta().getDisplayName().equals("Magical scroll")) {
                            required -= stack.getAmount();
                        }
                    }
                    if (required > 0) {
                        return false;
                    }
            }
        }
        return true;
    }

    private void takeRequirements(Player player, Spell spell) {
        for (LearnRequirement req : spell.getLearnRequirements()) {
            switch (req.getType()) {
                case EXP:
                    player.setLevel(player.getLevel() - (int) req.getValue());
                    break;
                case ITEMSTACK:
                    player.getInventory().removeItem((ItemStack) req.getValue());
                    break;
                case SCROLL:
                    int required = (int) req.getValue();
                    for (ItemStack stack : player.getInventory().getContents()) {
                        if (stack.hasItemMeta() && stack.getItemMeta().getDisplayName().equals("Magical scroll")) {
                            if (required < stack.getAmount()) {
                                stack.setAmount(stack.getAmount() - required);
                                required = 0;
                            } else {
                                stack.setAmount(0);
                                required -= stack.getAmount();
                            }
                        }
                        if (required <= 0) {
                            break;
                        }
                    }
            }
        }
    }

}