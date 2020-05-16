package hu.bigo;


import java.io.File;
import java.io.IOException;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;


public class main extends JavaPlugin implements Listener, CommandExecutor{
	int i;
	int time = this.getConfig().getInt("timeInSecs");
	File cFile;
	FileConfiguration fC;
	
	public void onEnable() {
		this.getServer().getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		getConfig().options().copyDefaults(true);
		Bukkit.getLogger().info(""+this.getConfig().getInt("timeInSecs"));
	}
	
	public void onDisable(){
		saveTime();
	}
	
	public void startTime() {
	i = Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
	    @Override
	    public void run() {
	    	time++;
	    	int hours = (int) time / 3600;
	    	int remainder = (int) time - hours * 3600;
	        int mins = remainder / 60;
	        remainder = remainder - mins * 60;
	        int secs = remainder;
	        
	        String h = hours > 9 ? ""+hours : "0"+hours;
	        String m = mins > 9 ? ""+mins : "0"+mins;
	        String s = secs > 9 ? ""+secs : "0"+secs;
	        
	        for(Player p : Bukkit.getOnlinePlayers()) {
	            String message = "§6"+h+":"+m+":"+s;
	            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
	        }
	    }
	}, 0L, 20L);
	}
	
	private void saveTime(){
		this.getConfig().set("timeInSecs", time);
		this.saveConfig();
	}
	
	public void pauseTime() {
	Bukkit.getScheduler().cancelTask(i);
	}
	
	@Override
	 public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("hely")) {
			Player p = (Player) sender;
			String hely = (" ("+args[0]+" - "+p.getLocation().getBlockX()+"/"
			+p.getLocation().getBlockY()+"/"+p.getLocation().getBlockZ()+") ");
			List<String> newHelyek = this.getConfig().getStringList("Helyek");
			newHelyek.add(hely);
			this.getConfig().set("Helyek", newHelyek);
			this.saveConfig();
			this.reloadConfig();
			p.sendMessage("§6"+args[0]+" §alerakva!");
		}
		else if(command.getName().equalsIgnoreCase("helyek")) {
			String allHely = "";
			for(String s : this.getConfig().getStringList("Helyek")) {
				allHely += "§3|§f"+s+"§3|";
			}
			sender.sendMessage(allHely);
		}
		else if(command.getName().equalsIgnoreCase("done")) {
			int id = Integer.parseInt(args[0]);
			String newText = "#"+this.getConfig().getStringList("Quests").get(id);
			List<String> old = this.getConfig().getStringList("Quests");
			old.set(id, newText);
			this.getConfig().set("Quests", old);
			this.saveConfig();
			this.reloadConfig();
			sender.sendMessage("\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n");
			((Player) sender).chat("/feladatok");
		}
		else if(command.getName().equalsIgnoreCase("undone")) {
			int id = Integer.parseInt(args[0]);
			String newText = this.getConfig().getStringList("Quests").get(id).replaceAll("#", "");
			List<String> old = this.getConfig().getStringList("Quests");
			old.set(id, newText);
			this.getConfig().set("Quests", old);
			this.saveConfig();
			this.reloadConfig();
			sender.sendMessage("\n \n \n \n \n \n \n \n \n \n \n \n \n \n \n");
			((Player) sender).chat("/feladatok");
		}
		else if(command.getName().equalsIgnoreCase("start")) {
			Bukkit.getLogger().info("Starting...");
			startTime();
			sender.sendMessage("§cIndul az idő!");
		}
		else if(command.getName().equalsIgnoreCase("pause")) {
			pauseTime();
			saveTime();
			sender.sendMessage("§cSzünetel az idő!");
		}
		else if(command.getName().equalsIgnoreCase("feladatok")) {
			int iter = 0;
			for(String s : this.getConfig().getStringList("Quests")) {
				if(s.contains("#")) {
					TextComponent message = new TextComponent("§a"+s.replaceAll("#", ""));
					message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/undone "+iter ) );
					message.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("§l§2Kész!").create()));
					sender.spigot().sendMessage(message);
					iter++;
					continue;
				}
				TextComponent message = new TextComponent("§c"+s);
				message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/done "+iter ) );
				message.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new ComponentBuilder("§l§8Nem Kész!").create()));
				sender.spigot().sendMessage(message);
				iter++;
			}
		}
		return false;
	}
	
}
