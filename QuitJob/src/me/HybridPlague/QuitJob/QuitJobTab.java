package me.HybridPlague.QuitJob;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import me.clip.placeholderapi.PlaceholderAPI;

public class QuitJobTab implements TabCompleter {

	public Main plugin;
	public QuitJobTab(Main plugin) {
		this.plugin = plugin;
	}
	
	List<String> arguments = new ArrayList<String>();
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		Player p = (Player) sender;
		
		String groups = "%vault_ranks%";
		groups = PlaceholderAPI.setPlaceholders(p, groups).toLowerCase();
		
		for (int i = 0; i < plugin.getConfig().getStringList("Jobs-To-Check").size(); i++) {
			arguments.add(plugin.getConfig().getStringList("Jobs-To-Check").toArray()[i].toString().toLowerCase());
		}
		
		List<String> result = new ArrayList<String>();
		if (args.length == 1) {
			for (String a : arguments) {
				if (a.toLowerCase().startsWith(args[0].toLowerCase()))
					result.add(a);
			}
			return result;
		}
		
		return null;
	}
	
}
