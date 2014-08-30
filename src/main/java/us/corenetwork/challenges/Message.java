package us.corenetwork.challenges;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by tux on 27.07.14.
 */
public class Message {
    private final Setting setting;
    private String finalMessage;
    private HashMap<String, Object> debugVariables;

    private static boolean isDebug(CommandSender sender) {
        if (sender instanceof ConsoleCommandSender) {
            return false;
        }

        if (!sender.hasPermission("challenges.message.debug")) {
            return false;
        }

        return Settings.getBoolean(Setting.MESSAGE_DEBUG_MODE);
    }

    public static Message from(Setting setting) {
        return new Message(setting);
    }

    public Message(Setting setting) {
        this.setting = setting;

        finalMessage = Settings.getString(setting);
    }

    public Message variable(String key, Object value) {
        if (Settings.getBoolean(Setting.MESSAGE_DEBUG_MODE)) {
            if (debugVariables == null) {
                debugVariables = new HashMap<String, Object>();
            }
            debugVariables.put(key, value);
        }

        finalMessage = finalMessage.replaceAll("<" + key + ">", value.toString());

        return this;
    }

    public void send(CommandSender sender) {
        if (isDebug(sender)) {
            String lines[] = Util.getMessageLines(finalMessage);
            JSONArray extra = new JSONArray();
            String tooltip = ChatColor.AQUA + setting.getString();
            if (debugVariables != null) {
                for (Map.Entry<String, Object> e : debugVariables.entrySet()) {
                    tooltip += "\n<" + e.getKey() + "> = " + e.getValue();
                }
            }
            JSONObject hoverEvent = new JSONObject();
            hoverEvent.put("action", "show_text");
            hoverEvent.put("value", tooltip);
            for (String line : lines) {
                JSONObject message = new JSONObject();
                message.put("text", line);
                message.put("hoverEvent", hoverEvent);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName() + " " + message.toJSONString());
            }
        } else {
            Util.Message(finalMessage, sender);
        }
    }

    @Override
    public String toString() {
        return finalMessage;
    }

    public void broadcast(CommandSender ... exclude) {
        List<CommandSender> e = Arrays.asList(exclude);
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!e.contains(player)) {
                send(player);
            }
        }
        if (!e.contains(Bukkit.getConsoleSender())) {
            send(Bukkit.getConsoleSender());
        }
    }
}
