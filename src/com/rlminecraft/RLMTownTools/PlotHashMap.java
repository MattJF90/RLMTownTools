package com.rlminecraft.RLMTownTools;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class PlotHashMap extends HashMap<PlotCoord, Integer> {
	
	private static final long serialVersionUID = 1742366461192855810L;
	
	private PlotCoord cache;
	
	public boolean containsKey (PlotCoord key) {
		boolean found = false;
		Set<PlotCoord> keys = this.keySet();
		Iterator<PlotCoord> iterator = keys.iterator();
		while (iterator.hasNext() && !found) {
			PlotCoord coord = iterator.next();
			if (key.equals(coord)) {
				found = true;
				this.cache = coord;
			}
		}
		return found;
	}
	
	public PlotCoord getCache () {
		return this.cache;
	}
	
	public Integer get (PlotCoord key) throws Exception {
		if (!this.containsKey(key)) throw new Exception();
		return this.get((Object)this.cache);
	}
	
	public Integer getCacheValue () {
		return this.get((Object)this.cache);
	}
	
}
