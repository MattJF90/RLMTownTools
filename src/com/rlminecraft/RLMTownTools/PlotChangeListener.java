package com.rlminecraft.RLMTownTools;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;

public class PlotChangeListener implements Listener {
	RLMTownTools main;
	
	public PlotChangeListener(RLMTownTools instance) {
		main = instance;
	}
	
	@EventHandler
	public void onPlayerCommand (PlayerCommandPreprocessEvent event) {
		if (!event.getMessage().startsWith("/plot ")) return;
		if (event.getMessage().equalsIgnoreCase("/plot selection add")) {
			main.PlotSelectionAdd(event.getPlayer());
			event.setCancelled(true);
		} else if (event.getMessage().equalsIgnoreCase("/plot selection remove")) {
			main.PlotSelectionRemove(event.getPlayer());
			event.setCancelled(true);
		} else if (event.getMessage().equalsIgnoreCase("/plot selection clear")) {
			main.PlotSelectionClear(event.getPlayer());
			event.setCancelled(true);
		} else if (event.getMessage().equalsIgnoreCase("/plot selection list")) {
			main.PlotSelectionList(event.getPlayer());
			event.setCancelled(true);
		} else if (event.getMessage().equalsIgnoreCase("/plot join")) {
			main.PlotJoin(event.getPlayer());
			event.setCancelled(true);
		} else {
			try {
				main.getServer().getScheduler().scheduleSyncDelayedTask(main, new PlotUpdateThread(main, main.towny.getCache(event.getPlayer()).getLastTownBlock().getTownBlock()), 100L);
			} catch (NotRegisteredException e) {
				main.log.info("Townblock does not exist!");
			}
		}
	}
}
