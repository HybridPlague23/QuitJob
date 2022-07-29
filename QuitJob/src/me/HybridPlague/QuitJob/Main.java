package me.HybridPlague.QuitJob;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import me.clip.placeholderapi.PlaceholderAPI;

public class Main extends JavaPlugin {

	Map<String, Long> cooldowns = new HashMap<String, Long>();

	@Override
	public void onEnable() {
		this.getCommand("quitjob").setTabCompleter(new QuitJobTab(this));
		this.saveDefaultConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (label.equalsIgnoreCase("quitjob")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("This command is only executable by a player.");
				return true;
			}
			Player p = (Player) sender;
			if (args.length == 0) {
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&lMissing argument: &f/QuitJob <job>"));
				return true;
			} else if (args.length > 0) {
				String arg = args[0].toUpperCase();
				if (args[0].equalsIgnoreCase("reload")) { // /quitjob reload
					if (!p.hasPermission("quitjob.reload")) {
						p.sendMessage(ChatColor.RED + "Insufficient permissions");
						return true;
					}
					this.reloadConfig();
					p.sendMessage(ChatColor.GREEN + "Config reloaded");
					return true;
					
				} else if (this.getConfig().getStringList("Jobs-To-Check").contains(arg)) { // Job is checkable
					String groups = "%vault_ranks%";
					groups = PlaceholderAPI.setPlaceholders(p, " " + groups).toUpperCase();
					
					if (!groups.contains(" " + arg)) { // Player does not have that group
						p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&8&lInvalid argument: &fYou do not have that job: &c" + args[0].toLowerCase()));
						return true;
					} else { // Does have that group
						if (cooldowns.containsKey(p.getName())) {
							if (cooldowns.get(p.getName()) > System.currentTimeMillis()) {
								long sec = (TimeUnit.MILLISECONDS.toSeconds(cooldowns.get(p.getName()) - System.currentTimeMillis()) % 60);
								long min = (TimeUnit.MILLISECONDS.toMinutes(cooldowns.get(p.getName()) - System.currentTimeMillis()) % 60);
								long hour = (TimeUnit.MILLISECONDS.toHours(cooldowns.get(p.getName()) - System.currentTimeMillis()) % 24);
								
								p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&7[&bQuitJob&7] &f" +
								"Please wait &e" + hour + "h&f, &e" + min + "m&f, &e" + sec + "s &fbefore executing that command again."));
								
								return true;
							}
						}
						cooldowns.put(p.getName(), System.currentTimeMillis() + ((240*60) * 1000));
						Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + p.getName() + " parent remove " + args[0]);
						p.sendMessage(ChatColor.GREEN + "You have quit your job as " + ChatColor.YELLOW + args[0].toLowerCase());
						return true;
					}
				} else { // Job is uncheckable
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', "&c&lError: &fJob cannot be found: &c" + args[0].toLowerCase()));
					return true;
				}
			}
		}
		return false;
	}
	
}
