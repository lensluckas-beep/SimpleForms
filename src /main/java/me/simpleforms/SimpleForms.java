package me.simpleforms;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

public class SimpleForms extends JavaPlugin {

    private final HashMap<UUID, String> forms = new HashMap<>();

    @Override
    public void onEnable() {
        getLogger().info("SimpleForms enabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player p)) return true;

        if (cmd.getName().equalsIgnoreCase("setform")) {
            if (args.length != 1) {
                p.sendMessage("§cUsage: /setform <bat|frog|cat>");
                return true;
            }

            String form = args[0].toLowerCase();
            if (!form.equals("bat") && !form.equals("frog") && !form.equals("cat")) {
                p.sendMessage("§cInvalid form.");
                return true;
            }

            forms.put(p.getUniqueId(), form);
            p.sendMessage("§aForm set to §e" + form);
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("transform")) {
            String form = forms.get(p.getUniqueId());
            if (form == null) {
                p.sendMessage("§cYou haven't selected a form!");
                return true;
            }

            // Clear old effects
            p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
            p.setAllowFlight(false);
            p.setFlying(false);

            switch (form) {

                case "bat" -> {
                    p.setAllowFlight(true);
                    p.setFlying(true);

                    // Damage while flying
                    Bukkit.getScheduler().runTaskTimer(this, task -> {
                        if (!p.isOnline() || !p.isFlying()) {
                            task.cancel();
                            return;
                        }

                        if (p.getHealth() > 1.0) {
                            p.damage(1.0); // half heart
                        }
                    }, 40L, 40L); // every 2 seconds

                    p.sendMessage("§dYou transformed into a Bat!");
                }

                case "frog" -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 2));
                    p.sendMessage("§aYou transformed into a Frog!");
                }

                case "cat" -> {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                    p.sendMessage("§6You transformed into a Cat!");
                }
            }
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("untransform")) {
            p.setAllowFlight(false);
            p.setFlying(false);
            p.getActivePotionEffects().forEach(e -> p.removePotionEffect(e.getType()));
            p.sendMessage("§7You returned to human form.");
            return true;
        }

        return false;
    }
}
