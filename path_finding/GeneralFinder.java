

import java.util.ArrayList;
import java.util.Stack;

public class GeneralFinder  {
	int choice;
	GeneralFinder(int mode){
		choice=mode;
	}
	GeneralFinder(){
		choice=0;
	}
	
	public ArrayList<ArrayList<Integer>> givePath(String from, String to, MapGraph traffic_info) {
		if(traffic_info.getLandMarkId(from)==-1 || traffic_info.getLandMarkId(to)==-1){
			System.out.println("what the hell! there is no solution");
			return null;
		}
		// TODO Auto-generated method stub
		if(choice==homework.ALGO_BFS ){
			//sort the same level with ascending order in id
			System.out.println("now is doing bfs");
			//traffic_info.sortEdgesWithId(true);
			return GraphSearch(from, to,  traffic_info,new BFSFinder());
		}
		if(choice==homework.ALGO_DFS){
			//sort the same level with descending order in id
			System.out.println("now is doing dfs");
			//traffic_info.sortEdgesWithId(false);
			traffic_info.reverseEdges();
			return normalSearch(from, to, traffic_info,new DFSFinder());
		}
		if(choice==homework.ALGO_UCS){
			//sort the same level with descending order in id
			System.out.println("now is doing ucs");
			return normalSearch(from, to, traffic_info,new UCSFinder());
		}
		if(choice==homework.ALGO_AS){
			//sort the same level with descending order in id
			System.out.println("now is doing as");
			return normalSearch(from, to, traffic_info,new ASFinder());
		}
		return null;
	}

	/**
	 * search for graph, esspecially for gfs and bfs
	 * goal check for node's children
	 * @param from
	 * @param to
	 * @param traffic_info
	 * @param gs
	 * @return
	 */
	private ArrayList<ArrayList<Integer>> GraphSearch(String from, String to, MapGraph traffic_info,GraphSearcher gs) {
		int start=traffic_info.getLandMarkId(from);
		int goal=traffic_info.getLandMarkId(to);
		//no need of searching...
		//other situations
		gs.enQue(start);
		gs.setPre(start,start);
		if(start==goal){
			return getPathFromGoal(goal,gs);
		}
		while(!gs.isEmpty()){
			//get the top node
			int curr=gs.deQue();
			//get all children
			ArrayList<int[]> neigh=traffic_info.getNeighbors(curr);
			for(int[] node:neigh){
				if(!gs.explored(node[0])){
					//add this children into queue
					gs.setPre(node[0],curr);//pred is curr
					gs.enQue(node[0]);
					if(node[0]==goal){
						//find result!
						return getPathFromGoal(node[0],gs);
					}
				}
			}
		}
		System.out.println("never find a path!! goal is " + goal);
		return null;
	}
	/**
	 * basically the same as GraphSearch
	 * However, we check the node when it is exploring nodes
	 * @param from
	 * @param to
	 * @param traffic_info
	 * @param gs
	 * @return
	 */
	private ArrayList<ArrayList<Integer>> normalSearch(String from, String to, MapGraph traffic_info,GraphSearcher gs) {
		int start=traffic_info.getLandMarkId(from);
		int goal=traffic_info.getLandMarkId(to);
		//no need of searching...
		//other situations
		gs.upDateNode(start, start, 0, traffic_info.getNormalTime(start));
		gs.enQue(start);
		gs.setPre(start,start);
		if(start==goal){
			return getPathFromGoal(goal,gs);
		}
		while(!gs.isEmpty()){
			//get the top node
			int curr=gs.deQue();
			if(curr==goal){
				return getPathFromGoal(curr,gs);
			}
			//get all children
			ArrayList<int[]> neigh=traffic_info.getNeighbors(curr);
			for(int[] node:neigh){
				if(!gs.explored(node[0]) || gs.upDateNode(node[0], curr, node[1], traffic_info.getNormalTime(node[0]))){
					//add this children into queue
					gs.setPre(node[0],curr);//pred is curr
					gs.enQue(node[0]);
				}
			}
		}
		System.out.println("never find a path!! goal is " + goal);
		return null;
	}
	/**
	 * for graph search, get goal with pre, use no cost informations
	 * @param goal
	 * @param gs
	 * @return
	 */
	private ArrayList<ArrayList<Integer>> getPathFromGoal(int goal, GraphSearcher gs){
		int curr=goal;
		boolean flag=true;
		ArrayList<ArrayList<Integer>> res=new ArrayList<ArrayList<Integer>>();
		Stack<Integer> stack=new Stack<Integer>();
		Stack<Integer> cost=new Stack<Integer>();
		while(flag){
			stack.push(curr);
			cost.push(gs.getCost(curr));
			if(curr==gs.getPre(curr)){
				flag=false;
			}else{
				curr=gs.getPre(curr);
			}
		}
		while(!stack.isEmpty()){
			ArrayList<Integer> temp=new ArrayList<Integer>();
			temp.add(stack.pop());
			temp.add(cost.pop());
			res.add(temp);
			//System.out.println(temp.get(0)+" "+temp.get(1));
		
		}
		return res;
	}
}
