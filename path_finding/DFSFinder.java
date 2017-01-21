
import java.util.HashMap;
import java.util.HashSet;
import java.util.Stack;

public class DFSFinder implements GraphSearcher {
	HashMap<Integer,Integer> prelist;//node list with predecessor
	HashSet<Integer> hasExplored;
	HashMap<Integer,Integer> cost;
	Stack<Integer> searchQue;
	DFSFinder(){
		prelist=new HashMap<Integer,Integer>();
		searchQue=new Stack<Integer>();
		cost=new HashMap<Integer,Integer>();
		hasExplored=new HashSet<Integer>();
	}

	@Override
	public void enQue(int element) {
		// TODO Auto-generated method stub
		searchQue.push(element);
		hasExplored.add(element);
	}


	@Override
	public int deQue() {
		// TODO Auto-generated method stub
		/*
		while(hasExplored.contains(searchQue.peek())){
			searchQue.pop();
		}
		hasExplored.add(searchQue.peek());
		*/
		return searchQue.pop();
	}


	@Override
	public int getPre(int curr) {
		// TODO Auto-generated method stub
		return prelist.get(curr);
	}


	@Override
	public void setPre(int curr, int pre) {
		// TODO Auto-generated method stub
		if(curr==pre){cost.put(curr, 0);}
		else {
			assert cost.containsKey(pre);
			cost.put(curr, cost.get(pre)+1);
		}
		prelist.put(curr,pre);
	}


	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return searchQue.isEmpty();
	}


	@Override
	public boolean explored(int curr) {
		// TODO Auto-generated method stub
		return hasExplored.contains(curr);
	}




	@Override
	public boolean upDateNode(int node, int pre, int cost, int h) {
		// TODO Auto-generated method stub
		/*
		if(!prelist.containsKey(node)){
			return false;
		}
		int old_pre=getPre(node);
		if(getCost(old_pre)>getCost(pre)){
			//setPre(node,pre);
			hasExplored.remove(node);
			return true;
		}*/
		return false;
	}

	@Override
	public int getCost(int node) {
		// TODO Auto-generated method stub
		assert cost.containsKey(node);
		return cost.get(node);
	}
}
