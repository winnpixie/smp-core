package io.github.winnpixie.smp;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;

public class Infoboard {
    private final SMPCore plugin;

    public Infoboard(SMPCore plugin) {
        this.plugin = plugin;
    }

    public void init() {
        plugin.getServer().getScheduler().runTaskTimer(plugin, () -> plugin.getServer().getOnlinePlayers().forEach(this::update), 0L, 0L);
    }

    public void register(Player player) {
        Scoreboard scoreboard = plugin.getServer().getScoreboardManager().getNewScoreboard();

        Objective hearts = scoreboard.registerNewObjective("sb_health", Criteria.HEALTH, "Hearts", RenderType.HEARTS);
        hearts.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        Objective titleBar = scoreboard.registerNewObjective("sb_title", Criteria.DUMMY, "World Time", RenderType.INTEGER);
        titleBar.setDisplaySlot(DisplaySlot.SIDEBAR);
        titleBar.getScore("------------").setScore(9);

        // Ping
        Team ping = scoreboard.registerNewTeam("ping");
        ping.setColor(ChatColor.BLUE);
        ping.setPrefix("\u00A7f> ");
        ping.addEntry("Ping");
        titleBar.getScore("Ping").setScore(8);

        // Saturation
        titleBar.getScore("\u00A7f>\u00A70").setScore(7);

        Team saturation = scoreboard.registerNewTeam("saturation");
        saturation.setColor(ChatColor.YELLOW);
        saturation.setPrefix("\u00A7f> ");
        saturation.addEntry("Sat.");
        titleBar.getScore("Sat.").setScore(6);

        // GPS
        titleBar.getScore(">\u00A71").setScore(5);

        Team direction = scoreboard.registerNewTeam("direction");
        direction.setColor(ChatColor.DARK_GRAY);
        direction.setPrefix("\u00A7f> ");
        direction.addEntry("Dir.");
        titleBar.getScore("Dir.").setScore(4);

        Team coordX = scoreboard.registerNewTeam("x");
        coordX.setColor(ChatColor.GRAY);
        coordX.setPrefix("\u00A7f> ");
        coordX.addEntry("X");
        titleBar.getScore("X").setScore(3);

        Team coordY = scoreboard.registerNewTeam("y");
        coordY.setColor(ChatColor.GRAY);
        coordY.setPrefix("\u00A7f> ");
        coordY.addEntry("Y");
        titleBar.getScore("Y").setScore(2);

        Team coordZ = scoreboard.registerNewTeam("z");
        coordZ.setColor(ChatColor.GRAY);
        coordZ.setPrefix("\u00A7f> ");
        coordZ.addEntry("Z");
        titleBar.getScore("Z").setScore(1);

        player.setScoreboard(scoreboard);
        update(player);
    }

    public void update(Player player) {
        Scoreboard scoreboard = player.getScoreboard();
        Objective titleBar = scoreboard.getObjective("sb_title");
        titleBar.setDisplayName(toTimeString(getTimeOfDay(player.getWorld())));

        Team ping = scoreboard.getTeam("ping");
        int latency = player.getPing();
        ping.setSuffix(":\u00A7f \u00A7%c%dms".formatted(getLatencyColor(latency), latency));

        Team saturation = scoreboard.getTeam("saturation");
        saturation.setSuffix("\u00A7f %.1f".formatted(player.getSaturation()));

        Team dir = scoreboard.getTeam("direction");
        dir.setSuffix("\u00A7f %s".formatted(player.getFacing().name()));

        Location location = player.getLocation();
        Team coordX = scoreboard.getTeam("x");
        coordX.setSuffix(":\u00A7f %.1f".formatted(location.getX()));

        Team coordY = scoreboard.getTeam("y");
        coordY.setSuffix(":\u00A7f %.1f".formatted(location.getY()));

        Team coordZ = scoreboard.getTeam("z");
        coordZ.setSuffix(":\u00A7f %.1f".formatted(location.getZ()));
    }

    private char getLatencyColor(int latency) {
        if (latency > 249) {
            return '4'; // Dark Red
        } else if (latency > 199) {
            return 'c'; // Red
        } else if (latency > 149) {
            return '6'; // Gold / Close enough to orange for my liking
        } else if (latency > 99) {
            return 'e'; // Yellow
        } else if (latency > 49) {
            return 'a'; // Green
        }

        return '2'; // Dark Green
    }

    private long getTimeOfDay(World world) {
        return (world.getFullTime() + 6000L) % 24000L;
    }

    private String toTimeString(double ticks) {
        double fTicks = (float) ticks;

        double hour = (fTicks / 24000.00) * 24.00;
        int iHour = (int) Math.floor(hour);

        double min = (hour - iHour) * 60.00;
        int iMin = (int) Math.floor(min);

        char meridiem = iHour < 12 ? 'A' : 'P';

        iHour = iHour % 12;
        if (iHour == 0) iHour = 12;

        return "%s:%s %cM".formatted(iHour < 10 ? "0" + iHour : iHour,
                iMin < 10 ? "0" + iMin : iMin, meridiem);
    }
}
