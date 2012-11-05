package com.rlminecraft.RLMTownTools;

import com.palmergames.bukkit.towny.object.TownBlock;

public class PlotUpdateThread implements Runnable {
	
	TownBlock townblock;
	RLMTownTools plugin;
	
	public PlotUpdateThread (RLMTownTools instance, TownBlock townblock) {
		this.plugin = instance;
		this.townblock = townblock;
	}
	
	public void run() {
		//plugin.log.info("Plot synchronization in progress!");
		plugin.PlotSync(this.townblock);
	}
}
