package com.puddingkc;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class KcBadMilk extends JavaPlugin implements Listener {

    private final Set<PotionEffectType> effectsSet = new HashSet<>();
    private boolean isBlacklistMode;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(this,this);
        getLogger().info("插件加载成功，作者QQ:3116078709");
    }

    private void loadConfig() {
        List<String> effectsList = getConfig().getStringList("effect");
        for (String effectName : effectsList) {
            PotionEffectType type = PotionEffectType.getByName(effectName.toUpperCase());
            if (type != null) {
                effectsSet.add(type);
            } else {
                getLogger().warning("药水效果 " + effectName + " 不存在");
            }
        }
        isBlacklistMode = getConfig().getBoolean("blacklist",false);
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.MILK_BUCKET) {
            event.setCancelled(true);
            replaceMilkBucket(event);
            handlePotionEffects(event);
        }
    }

    private void replaceMilkBucket(PlayerItemConsumeEvent event) {
        ItemStack itemInMainHand = event.getPlayer().getInventory().getItemInMainHand();
        ItemStack itemInOffHand = event.getPlayer().getInventory().getItemInOffHand();

        if (itemInMainHand.getType() == Material.MILK_BUCKET) {
            event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.BUCKET));
        } else if (itemInOffHand.getType() == Material.MILK_BUCKET) {
            event.getPlayer().getInventory().setItemInOffHand(new ItemStack(Material.BUCKET));
        }
    }

    private void handlePotionEffects(PlayerItemConsumeEvent event) {
        Set<PotionEffectType> effectsToRemove = new HashSet<>();

        for (PotionEffect effect : event.getPlayer().getActivePotionEffects()) {
            boolean shouldRemove = isBlacklistMode == effectsSet.contains(effect.getType());

            if (shouldRemove) {
                effectsToRemove.add(effect.getType());
            }
        }

        for (PotionEffectType effectType : effectsToRemove) {
            event.getPlayer().removePotionEffect(effectType);
        }
    }
}
