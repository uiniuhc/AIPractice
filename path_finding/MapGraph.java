

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

//store all numbers when dealing with the map
//use a hash map to convert string to numbers
/**
 * A graph of the given infomation, store all edges as an list of list
 * @author uiniuhc
 *
 */
public class MapGraph {
	HashMap<String, Integer> landMarks;
	ArrayList<String> names;
	HashMap<Integer,Integer> normalToGoal;//sunday time, bad design, miss information of from and to.....
	ArrayList<ArrayList<int[]>> edges;//one for the nodes, the other for the time/distance
	int curr_node_id;//the sequence of nodes;
	MapGraph(){
		curr_node_id=0;
		landMarks=new HashMap<String,Integer>();
		edges=new ArrayList<ArrayList<int[]>>();
		normalToGoal=new HashMap<Integer,Integer>();
		names=new ArrayList<String>();
	}
	//add a name to the land marks
	/**
	 * translate a land mark to a number
	 * @param land
	 * @return id of this landmark
	 */
	int addLandMark(String land){
		//first check the string is a new one
		if(landMarks.containsKey(land))return landMarks.get(land);
		//add the id to map
		landMarks.put(land, curr_node_id);
		//make sure the edge is never over size
		assert edges.size()==curr_node_id && names.size()==curr_node_id;
		edges.add(new ArrayList<int[]>());
		names.add(land);
		curr_node_id++;
		return curr_node_id-1;
	}
	/**
	 * given a string, convert it into a number in the map
	 * @param land
	 * @return number of the item (rang 0-size), -1 if we find nothing.
	 */
	int getLandMarkId(String land){
		if(!landMarks.containsKey(land))return -1;
		return landMarks.get(land);
	}
	/**
	 * add an edge
	 * @param from
	 * @param to
	 * @param cost
	 */
	void addEdge(String from,String to,int cost){
		//System.out.println("adding edge info =  from: "+from+" to: "+to +" with cost: "+cost);
		int ifrom=addLandMark(from);
		int ito=addLandMark(to);
		ArrayList<int[]> using=edges.get(ifrom);
		int[] temp=new int[2];
		temp[0]=ito;
		temp[1]=cost;
		using.add(temp);
	}
	String getName(int land){
		return names.get(land);
	}
	/**
	 * dangerous design to give all neighbors
	 * @return
	 */
	ArrayList<int[]> getNeighbors(int from){
		return edges.get(from);
	}
	/**
	 * O(b) for linear search!!!!
	 * find the cost between two nodes
	 * @param from
	 * @param to
	 * @return
	 */
	int getCost(int from,int to){
		ArrayList<int[]> from_list=edges.get(from);
		for(int i=0;i<from_list.size();i++){
			int[] temp=from_list.get(i);
			if(temp[0]==to)return temp[1];
		}
		return 0;
	}
	/**
	 * return information of sunday traffic
	 * @param from
	 * @return -1 if no such information
	 */
	int getNormalTime(int from){
		if(!normalToGoal.containsKey(from))return -1;
		return normalToGoal.get(from);
	}
	/**
	 * set the sunday time from from to goal
	 * @param from
	 * @param time
	 * @return true if success, false if there is such information existed
	 */
	boolean addNormalTime(int from, int time){
		if(normalToGoal.containsKey(from))return false;
		
		normalToGoal.put(from, time);
		return true;
	}
	
	boolean addNormalTime(String from,int time){
		//System.out.println("adding sunday info =  from: "+from+" time: "+time);
		int id=getLandMarkId(from);
		if(id<0)return false;
		return addNormalTime(id,time);
	}
	/**
	 * used for sorting all edges so that they will in the sequence of id
	 * especially for bfs and dfs...
	 */
	void sortEdgesWithId(boolean assendent){
		for(ArrayList<int []> lists:edges){
			if(assendent)
				lists.sort(new Comparator<int[]>(){ public int compare(int[] a, int[] b){return a[0]-b[0];}});
			else
				lists.sort(new Comparator<int[]>(){ public int compare(int[] a, int[] b){return b[0]-a[0];}});
		}
	}
	void reverseEdges(){
		for(ArrayList<int []> lists:edges){
			int i=0;
			int j=lists.size()-1;
			while(i<j){
				int[] temp=lists.get(i);
				lists.set(i,lists.get(j));
				lists.set(j, temp);
				i++;j--;
			}
		}
	}

	void printAllNodes(){
		System.out.println("now is printing all nodes:");
		for(String name:names){
			System.out.println(this.getLandMarkId(name)+" "+name);
		}
	}
	void printAllEdges(){
		System.out.println("now is printing all Edges:");
		for(int i=0;i<edges.size();i++){
			System.out.println("node: "+names.get(i));
			for(int[] node:edges.get(i)){
				System.out.println("\t\t"+"to: "+names.get(node[0])+"("+node[0]+") cost: "+node[1]);
			}
		}
	}
	void printSunday(){
		System.out.println("now is printing sunday info:");
		for(int i=0;i<names.size();i++){
			System.out.println(i+" "+names.get(i)+" "+getNormalTime(i));
		}
	}

	
	
}
