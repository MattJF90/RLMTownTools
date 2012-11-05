package com.rlminecraft.RLMTownTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownBlock;

public class RLMTownTools extends JavaPlugin {
	
	Logger log;
	public Map<String, ArrayList<PlotCoord>> selectionList;
	public Map<Integer, ArrayList<PlotCoord>> groupList;
	//public Map<PlotCoord, Integer> groupOfPlot;
	public PlotHashMap groupOfPlot;
	Towny towny;
	FileManager fileman;
	
	public void onEnable(){
		log = this.getLogger();
		towny = (Towny) Bukkit.getServer().getPluginManager().getPlugin("Towny");
		fileman = new FileManager(this);
		// Load plot groups
		groupList = new HashMap<Integer, ArrayList<PlotCoord>>();
		try {
			fileman.loadGroupList(groupList);
		} catch(Exception e) {
			log.warning("Group data file not found! Recreating file...");
		}
		// Load plot group data
		groupOfPlot = new PlotHashMap();
		try {
			fileman.loadPlotList(groupOfPlot);
		} catch(Exception e) {
			log.warning("Plot data file not found! Recreating file...");
			
		}
		// Create blank selection data list
		selectionList = new HashMap<String, ArrayList<PlotCoord>>();
		
		// Register listeners
		this.getServer().getPluginManager().registerEvents(new PlotChangeListener(this), this);
		
		log.info("Towny Tools addon successfully enabled!");
	}
	
	public void onDisable(){
		// Save plot groups
		try {
			fileman.saveGroupList(groupList);
		} catch(Exception e) {
			log.warning("Group data file not found! Recreating file...");
		}
		// Save plot group data
		try {
			fileman.savePlotList(groupOfPlot);
		} catch(Exception e) {
			log.warning("Plot data file not found! Recreating file...");
		}
		log.info("Towny Tools disabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if (sender instanceof Player) {
			if (args.length == 1) {
				if (cmd.getName().equalsIgnoreCase("lot")) {
					if (args.length == 1) {
						
						if (args[0].equalsIgnoreCase("add")) {
							int result = PlotSelectionAdd(sender);
							switch (result) {
								case -1:
									sender.sendMessage(ChatColor.RED + "ERROR: Towny could not be found!");
									break;
								case 0:
									sender.sendMessage(ChatColor.YELLOW + "Plot added to selection!");
									break;
								case 1:
									sender.sendMessage(ChatColor.RED + "Plot is already selected!");
									break;
								case 2:
									sender.sendMessage(ChatColor.RED + "Plot is not connected to the existing selection!");
									break;
								case 3:
									sender.sendMessage(ChatColor.RED + "Plot is already part of another group!");
									break;
								default:
									sender.sendMessage(ChatColor.RED + "Unexpected result from \"PlotSelectionAdd()\"!");
							}
							return true; 
						}
						
						else if (args[0].equalsIgnoreCase("remove")) {
							int result = PlotSelectionRemove(sender);
							switch (result) {
								case -1:
									sender.sendMessage(ChatColor.RED + "ERROR: Towny could not be found!");
									break;
								case 0:
									sender.sendMessage(ChatColor.YELLOW + "Plot removed from selection!");
									break;
								case 1:
									sender.sendMessage(ChatColor.RED + "Plot is not selected!");
									break;
								default:
									sender.sendMessage(ChatColor.RED + "Unexpected result from \"PlotSelectionRemove()\"!");
							}
							return true; 
						}
						
						else if (args[0].equalsIgnoreCase("clear")) {
							boolean result = PlotSelectionClear(sender);
							if (result) {
								sender.sendMessage(ChatColor.YELLOW + "Plot selection reset!");
							} else {
								sender.sendMessage(ChatColor.RED + "Unexpected result from \"PlotSelectionClear()\"!");
							}
							return result;
						}
						
						else if (args[0].equalsIgnoreCase("list")) {
							return PlotSelectionList(sender);
						}
						
						else if (args[0].equalsIgnoreCase("join")) {
							int result = PlotJoin(sender);
							switch (result) {
								case 0:
									sender.sendMessage(ChatColor.GREEN + "Plots successfully joined!");
									break;
								case 1:
									sender.sendMessage(ChatColor.RED + "No selection has been made!");
									break;
								default:
									sender.sendMessage(ChatColor.RED + "Unexpected result from \"PlotJoin()\"!");
							}
						}
						
					}
				}
			}
		} else {
			sender.sendMessage(ChatColor.RED + "This command cannot be used from the console!");
			return false;
		}
		return false;
	}
	
	/*public void onPlayerMoveChunk(Player player, WorldCoord from, WorldCoord to, org.bukkit.Location fromLoc, org.bukkit.Location toLoc) {
		log.info(player.getName() + " switched chunk!");
		//if (towny == null) return;
		//WorldCoord currentPlot = ((Towny)towny).getCache(player).getLastTownBlock();
		DisplayGroup(player, to);
	}*/
	
	
	/*---------------*
	 * COMMAND: lot  *
	 *---------------*/
	
	public int PlotSelectionAdd (CommandSender sender) {
		ArrayList<PlotCoord> playerSelectionList;
		if (selectionList.containsKey(sender.getName())) {
			playerSelectionList = selectionList.get(sender.getName());
		} else {
			playerSelectionList = new ArrayList<PlotCoord>();
		}
		// Get player's last known location (as WorldCoord)
		if (towny == null) return -1;
		PlotCoord currentPlot = new PlotCoord(((Towny)towny).getCache((Player)sender).getLastTownBlock());
		if (!playerSelectionList.contains(currentPlot)) {
			if (IsConnected(currentPlot,playerSelectionList)) {
				playerSelectionList.add(currentPlot);
				selectionList.put(sender.getName(), playerSelectionList);
				log.info("Plot " + currentPlot.toString() + " has been selected by " + sender.getName() + ".");
				return 0;
			} else {
				return 2;
			}
		} else {
			return 1;
		}
	}
	
	public int PlotSelectionRemove (CommandSender sender) {
		ArrayList<PlotCoord> playerSelectionList;
		if (selectionList.containsKey(sender.getName())) {
			playerSelectionList = selectionList.get(sender.getName());
		} else {
			playerSelectionList = new ArrayList<PlotCoord>();
		}
		// Get player's last known location (as WorldCoord)
		Plugin towny = Bukkit.getServer().getPluginManager().getPlugin("Towny");
		if (towny == null) return -1;
		PlotCoord currentPlot = new PlotCoord(((Towny)towny).getCache((Player)sender).getLastTownBlock());
		// Remove plot from selection
		if (playerSelectionList.contains(currentPlot)) {
			playerSelectionList.remove(currentPlot);
			selectionList.put(sender.getName(), playerSelectionList);
			log.info("Plot " + currentPlot.toString() + " has been deselected by " + sender.getName() + ".");
			return 0;
		} else {
			return 1;
		}
	}
	
	public boolean PlotSelectionClear (CommandSender sender) {
		if (selectionList.containsKey(sender.getName())) selectionList.remove(sender.getName());
		log.info(sender.getName() + " has cleared their selection.");
		return true;
	}
	
	public boolean PlotSelectionList (CommandSender sender) {
		if (selectionList.containsKey(sender.getName())) {
			ArrayList<PlotCoord> playerSelectionList = selectionList.get(sender.getName());
			sender.sendMessage(ChatColor.GREEN + "");
			for (int i = 0; i < playerSelectionList.size(); i++) {
				sender.sendMessage(ChatColor.YELLOW + "  " + playerSelectionList.get(i).toString());
			}
			return true;
		} else {
			sender.sendMessage(ChatColor.RED + "You do not have any plots selected!");
			return false;
		}
	}
	
	public int PlotJoin (CommandSender sender) {
		log.info("DEBUG: Beginning plot join.");
		ArrayList<PlotCoord> playerSelectionList;
		if (selectionList.containsKey(sender.getName())) {
			playerSelectionList = selectionList.get(sender.getName());
		} else{
			return 1;
		}
		log.info("DEBUG: Player selection loaded.");
		int groupID;
		if (groupList == null) {
			groupID = 0;
		} else {
			groupID = groupList.size();
		}
		log.info("DEBUG: Group ID set (" + groupID + ").");
		groupList.put(groupID, playerSelectionList);
		log.info("DEBUG: Group saved! Beginning plot data saving...");
		// Perform for every plot in the group
		for (int i = 0; i < (selectionList.get(sender.getName())).size(); i++) {
			log.info("DEBUG:   Saving plot " + i + "...");
			groupOfPlot.put(selectionList.get(sender.getName()).get(i), groupID);
			log.info("DEBUG:                           saved!");
		}
		log.info("DEBUG: Plot data saved successfully! Clearing current selection....");
		PlotSelectionClear(sender);
		log.info("DEBUG: Complete!");
		return 0;
	}
	
	
	private boolean IsConnected (PlotCoord currentPlot, ArrayList<PlotCoord> playerSelectionList) {
		if (playerSelectionList.isEmpty()) return true;
		for (int i = 0; i < playerSelectionList.size();i++) {
			PlotCoord thisPlot = playerSelectionList.get(i);
			int dX = thisPlot.getX() - currentPlot.getX();
			if (dX < 0) dX *= -1;
			int dZ = thisPlot.getZ() - currentPlot.getZ();
			if (dZ < 0) dZ *= -1;
			if ((dX == 0 && dZ == 1) || (dX == 1 && dZ == 0)) {
				return true;
			}
		}
		return false;
	}
	
	
	public void PlotSync(TownBlock townblock) {
		//TownBlock townblock = TownyUniverse.getTownBlock(loc);
		PlotProperties plot = new PlotProperties(townblock);
		PlotCoord coord = new PlotCoord(townblock.getWorldCoord());
		log.info("Checking if first plot is in a group...");
		int groupIndex = -1;
		if (groupOfPlot.containsKey(coord)) {
			groupIndex = groupOfPlot.getCacheValue();
			log.info("In group " + groupIndex);
		} else {
			log.info("Not in a group");
			return;
		}
		
		// Change properties of all other plots in group.
		ArrayList<PlotCoord> group = groupList.get(groupIndex);
		ListIterator<PlotCoord> iterator = group.listIterator();
		while (iterator.hasNext()) {
			PlotCoord coordInGroup = iterator.next();
			TownBlock townblockInGroup = null;
			try {
				townblockInGroup = townblock.getWorld().getTownBlock(coordInGroup.getX(), coordInGroup.getZ());
			} catch (NotRegisteredException e) {
				e.printStackTrace();
			}
			//if (TownyUniverse.getTownBlock(coordInGroup.toLocation()) == null) log.info("Null townblock!");
			if (townblockInGroup == null) {
				log.info("Null townblock!");
			} else {
				//plot.commitToTownBlock(TownyUniverse.getTownBlock(coordInGroup.toLocation()));
				plot.commitToTownBlock(townblockInGroup);
			}
		}
	}
	
	
	/*--------------------------*
	 * ACTION: Player Movement  *
	 *--------------------------*/
	
	/*public void DisplayGroup (Player player, PlotCoord plot) {
		if (!groupOfPlot.containsKey(plot)) return;
		int groupID = groupOfPlot.get(plot);
		ArrayList<PlotCoord> group = groupList.get(groupID);
		player.sendMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "Grouped Plot (Size: " + group.size() + ")" + ChatColor.WHITE + "]");
	}*/
	
}
