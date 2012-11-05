package com.rlminecraft.RLMTownTools;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class DataTester {
	
	RLMTownTools plugin;
	
	public DataTester (RLMTownTools instance) {
		this.plugin = instance;
	}
	
	public void PrintCoord (PlotCoord data) {
		plugin.log.info("    [" + data.getX() + ", " + data.getZ() + ", " + data.getWorld().getName() + "]");
	}
	
	public void PrintGroup (int id, ArrayList<PlotCoord> data) {
		plugin.log.info("  Group ID: " + id);
		plugin.log.info("  Group Size: " + data.size());
		plugin.log.info("  Group Contents:");
		Iterator<PlotCoord> iterator = data.iterator();
		while (iterator.hasNext()) PrintCoord(iterator.next());
	}
	
	public void PrintFile (Map<Integer, ArrayList<PlotCoord>> data) {
		plugin.log.info("File Size: " + data.size());
		Iterator<Integer> iterator = data.keySet().iterator();
		while (iterator.hasNext()) {
			int id = iterator.next();
			PrintGroup(id,data.get(id));
		}
	}
	
}
