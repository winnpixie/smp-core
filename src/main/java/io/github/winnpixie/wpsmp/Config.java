package io.github.winnpixie.wpsmp;

import io.github.winnpixie.hukkit.configs.Link;

import java.util.List;

public class Config {
    // Connections
    // Connections::Join
    @Link(path = "connections.join.whitelist-ignore-full")
    public static boolean WHITELIST_IGNORE_FULL;

    @Link(path = "connections.join.broadcast")
    public static boolean BROADCAST_JOINS;

    @Link(path = "connections.join.message")
    public static String JOIN_MESSAGE;

    @Link(path = "connections.join.first-timer-message")
    public static String FIRST_TIMER_MESSAGE;

    @Link(path = "connections.join.message-of-the-day")
    public static List<String> MOTD_LINES;

    // Connections::Quit
    @Link(path = "connections.quit.broadcast")
    public static boolean BROADCAST_QUITS;

    @Link(path = "connections.quit.message")
    public static String QUIT_MESSAGE;

    // Player
    // Player::Chat
    @Link(path = "player.chat.message-format")
    public static String CHAT_FORMAT;

    @Link(path = "player.chat.format-colors")
    public static boolean FORMAT_CHAT_COLORS;

    @Link(path = "player.chat.green-text")
    public static boolean CHAT_GREEN_TEXT;

    // Player::Combat
    @Link(path = "player.combat.hot-hands")
    public static boolean HOT_HANDS;

    // Player::Death
    @Link(path = "player.death.philza-mode")
    public static boolean PHILZA_DEATHS;

    @Link(path = "player.death.drop-head")
    public static boolean DROP_HEAD_ON_DEATH;

    // Player::Interactions
    @Link(path = "player.interactions.head-pats")
    public static boolean HEAD_PATS;

    @Link(path = "player.interactions.parry-projectiles")
    public static boolean PARRY_SOUNDS;

    // World
    @Link(path = "world.allow-nether-roof")
    public static boolean ALLOW_NETHER_ROOF;

    @Link(path = "world.sitting")
    public static boolean ALLOW_SITTING;

    @Link(path = "world.remove-repair-limit")
    public static boolean REMOVE_REPAIR_LIMIT;
}