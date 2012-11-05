package com.rlminecraft.RLMTownTools;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.bukkit.Server;

public class FileManager {
	
	private Server server;
	DataTester tester;
	
	public FileManager (RLMTownTools instance) {
		this.server = instance.getServer();
		this.tester = new DataTester(instance);
	}
	
	
	public void saveGroupList (Map<Integer, ArrayList<PlotCoord>> data) throws FileNotFoundException, IOException {
		/* File Contents:
		 *   - Number of groups
		 *   - Group contents for 1st group
		 *   - Group contents for 2nd group
		 *     ...
		 *   - Group contents for last group
		 */
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("plugins/RLMTownTools/groups.dat"));
		Iterator<Integer> groupIterator = data.keySet().iterator();
		
		while (groupIterator.hasNext()) {
			/* Group Contents:
			 *   - Group ID (integer)
			 *   - Size of group (integer)
			 *   - Plot contents for 1st plot
			 *   - Plot contents for 2nd plot
			 *     ...
			 *   - Plot contents for last plot
			 */
			int id = groupIterator.next();
			oos.writeObject(id);									// Write group ID
			ArrayList<PlotCoord> plotList = data.get(id);
			int size = plotList.size();
			oos.writeObject(size);									// Write group size
			Iterator<PlotCoord> plotIterator = plotList.iterator();
			
			while (plotIterator.hasNext()) {
				/* Plot Contents:
				 *   - X coordinate
				 *   - Z coordinate
				 *   - World name
				 */
				PlotCoord plot = plotIterator.next();
				oos.writeObject(plot.getX());
				oos.writeObject(plot.getZ());
				oos.writeObject(plot.getWorld().getName());
			}
		}
		oos.close();
	}
	
	
	public void loadGroupList (Map<Integer, ArrayList<PlotCoord>> data) throws FileNotFoundException, IOException, ClassNotFoundException {
		/* File Contents:
		 *   - Number of groups
		 *   - Group contents for 1st group
		 *   - Group contents for 2nd group
		 *     ...
		 *   - Group contents for last group
		 *   - NullType
		 */
		ObjectInputStream ios = new ObjectInputStream(new FileInputStream("plugins/RLMTownTools/groups.dat"));
		int numGroups = (Integer) ios.readObject();
		for (int i = 0; i < numGroups; i++) {
			/* Group Contents:
			 *   - Group ID (integer)
			 *   - Size of group (integer)
			 *   - Plot contents for 1st plot
			 *   - Plot contents for 2nd plot
			 *     ...
			 *   - Plot contents for last plot
			 */
			int id = (Integer) ios.readObject();					// Read group ID
			int numPlots = (Integer) ios.readObject();				// Read group size
			ArrayList<PlotCoord> group = new ArrayList<PlotCoord>();
			for (int j = 0; j < numPlots; j++) {
				/* Plot Contents:
				 *   - X coordinate
				 *   - Z coordinate
				 *   - World name
				 */
				int x = (Integer) ios.readObject();
				int z = (Integer) ios.readObject();
				String world = (String) ios.readObject();
				PlotCoord plot = new PlotCoord(x,z,world,this.server);
				group.add(plot);
			}
			data.put(id, group);
		}
		ios.close();
		tester.PrintFile(data);
	}
	
	
	public void savePlotList (PlotHashMap data) throws FileNotFoundException, IOException {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("plugins/RLMTownTools/plots.dat"));
		oos.writeObject(data.size());
		Iterator<PlotCoord> iterator = data.keySet().iterator();
		while (iterator.hasNext()) {
			PlotCoord plot = iterator.next();
			oos.writeObject(plot.getX());
			oos.writeObject(plot.getZ());
			oos.writeObject(plot.getWorld().getName());
			try {
				oos.writeObject(data.get(plot));
			} catch (Exception e) {
				e.printStackTrace();
				break;
			}
		}
		oos.close();
	}
	
	
	public void loadPlotList (PlotHashMap data) throws FileNotFoundException, IOException, ClassNotFoundException {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream("plugins/RLMTownTools/plots.dat"));
		int size = (Integer) ois.readObject();
		for (int i = 0; i < size; i++) {
			int x = (Integer) ois.readObject();
			int z = (Integer) ois.readObject();
			String world = (String) ois.readObject();
			int groupID = (Integer) ois.readObject();
			data.put(new PlotCoord(x,z,world,server), groupID);
		}
		ois.close();
	}
	
}
