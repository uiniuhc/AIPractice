

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;

/**
 * a data structure for bfs path finder
 * take step as the cost
 * return an optimal path
 * @author uiniuhc
 *
 */
public class BFSFinder implements GraphSearcher {
	HashMap<Integer,Integer> explored;//node list with predecessor
	Queue<Integer> searchQue;
	HashMap<Integer,Integer> cost;
	BFSFinder(){
		explored=new HashMap<Integer,Integer>();
		searchQue=new LinkedList<Integer>();
		cost=new HashMap<Integer,Integer>();
	}

	@Override
	public void enQue(int element) {
		// TODO Auto-generated method stub
		searchQue.add(element);
	}


	@Override
	public int deQue() {
		// TODO Auto-generated method stub
		return searchQue.poll();
	}


	@Override
	public int getPre(int curr) {
		// TODO Auto-generated method stub
		return explored.get(curr);
	}


	@Override
	public void setPre(int curr, int pre) {
		// TODO Auto-generated method stub
		if(curr==pre){cost.put(curr, 0);}
		else {
			assert cost.containsKey(pre);
			cost.put(curr, cost.get(pre)+1);
		}
		explored.put(curr,pre);
	}
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return searchQue.isEmpty();
	}
	@Override
	public boolean explored(int curr) {
		// TODO Auto-generated method stub
		return explored.containsKey(curr);
	}



	@Override
	public boolean upDateNode(int node, int pre, int cost, int h) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getCost(int node) {
		// TODO Auto-generated method stub
		return cost.get(node);
	}

}
