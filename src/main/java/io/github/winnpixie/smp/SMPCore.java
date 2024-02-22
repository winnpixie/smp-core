package io.github.winnpixie.smp;

import io.github.winnpixie.hukkit.Hukkit;
import io.github.winnpixie.hukkit.configs.AnnotatedConfigurationManager;
import io.github.winnpixie.hukkit.configs.adapters.BukkitAdapter;
import io.github.winnpixie.smp.commands.*;
import io.github.winnpixie.smp.commands.admin.SMPCoreCommand;
import io.github.winnpixie.smp.listeners.ConnectionListener;
import io.github.winnpixie.smp.listeners.EntityActionListener;
import io.github.winnpixie.smp.listeners.PlayerActionListener;
import io.github.winnpixie.smp.listeners.WorldEventListener;
import io.github.winnpixie.smp.playerdata.PlayerManager;
import org.bukkit.plugin.java.JavaPlugin;

public class SMPCore extends JavaPlugin {
    public final AnnotatedConfigurationManager configManager = new AnnotatedConfigurationManager();
    public final PlayerManager playerManager = new PlayerManager();
    public final Infoboard infoboard = new Infoboard(this);

    @Override
    public void onEnable() {
        super.saveDefaultConfig();

        configManager.setAdapter(new BukkitAdapter(super.getConfig())).linkClass(Config.class).load();

        infoboard.init();

        Hukkit.addListener(new ConnectionListener(this));
        Hukkit.addListener(new EntityActionListener(this));
        Hukkit.addListener(new PlayerActionListener(this));
        Hukkit.addListener(new WorldEventListener(this));

        Hukkit.addCommand(new SMPCoreCommand(this));
        Hukkit.addCommand(new BottleExperienceCommand(this));
        Hukkit.addCommand(new HatCommand(this));
        Hukkit.addCommand(new PingCommand(this));
        Hukkit.addCommand(new SeenCommand(this));
        Hukkit.addCommand(new SitCommand(this));
        Hukkit.addCommand(new ToggleLogStrippingCommand(this));

        getLogger().info("Init complete");
    }

    @Override
    public void onDisable() {
        this.saveConfig();

        getLogger().info("Unload complete");
    }
}
