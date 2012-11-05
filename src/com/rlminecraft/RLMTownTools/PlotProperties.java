package com.rlminecraft.RLMTownTools;

import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
import com.palmergames.bukkit.towny.object.TownBlock;
import com.palmergames.bukkit.towny.object.TownBlockType;
import com.palmergames.bukkit.towny.object.TownyPermission;

public class PlotProperties {
	
	private boolean invalid;
	private TownyPermission permissions;
	private double price;
	private Resident resident;
	private Town town;
	private TownBlockType type;
	
	public PlotProperties (TownBlock townblock) {
		invalid = false;
		permissions = townblock.getPermissions();
		price = townblock.getPlotPrice();
		if (townblock.hasResident()) {
			try {
				resident = townblock.getResident();
			} catch (NotRegisteredException e) {
				invalid = true;
			}
		} else resident = null;
		if (townblock.hasTown()) {
			try {
				town = townblock.getTown();
			} catch (NotRegisteredException e) {
				invalid = true;
			}
		} else town = null;
		type = townblock.getType();
	}
	
	public boolean isInvalid () {
		return invalid;
	}
	
	public void commitToTownBlock (TownBlock townblock) {
		//townblock.setPermissions(this.permissions.toString());
		townblock.setPlotPrice(this.price);
		townblock.setResident(this.resident);
		townblock.setTown(this.town);
		townblock.setType(type);
	}
	
	public boolean equals (PlotProperties plot) {
		return
			(this.permissions == plot.permissions
			&& this.price == plot.price
			&& this.resident == plot.resident
			&& this.town == plot.town
			&& this.type == plot.type);
	}
	
}
