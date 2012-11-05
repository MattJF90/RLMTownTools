package com.rlminecraft.RLMTownTools;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;

import com.palmergames.bukkit.towny.object.WorldCoord;

public class PlotCoord {
	
	private int x;
	private int z;
	private World world;
	
	public PlotCoord (WorldCoord coord) {
		this.x = coord.getX();
		this.z = coord.getZ();
		this.world = coord.getBukkitWorld();
	}
	
	public PlotCoord (int x, int z, String world, Server server) {
		this.x = x;
		this.z = z;
		this.world = server.getWorld(world);
	}
	
	public int getX () {
		return x;
	}
	
	public int getZ () {
		return z;
	}
	
	public World getWorld () {
		return world;
	}
	
	public boolean equals (PlotCoord plot) {
		return
			(  this.x == plot.getX()
			&& this.z == plot.getZ()
			&& this.world == plot.getWorld()
			);
	}
	
	public Location toLocation () {
		return new Location(world, x, 64, z);
	}
	
}
