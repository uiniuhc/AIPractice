

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;

public class ASFinder implements GraphSearcher {
	final int CURR_NODE=0;
	final int PRE_NODE=1;
	final int CURR_F=2;
	final int CURR_COST=3;
	final int CURR_H=4;
  	final int CURR_TIME=5;
	final int NODE_LEN=6;
	TreeSet<int[]> openlist;//contain all wait for
	HashSet<Integer> closelist;
	HashMap<Integer,int[]> nodes;//information about nodes, includes curr, parent, f, cost_so_far, h
  	int inner_time;
	ASFinder(){
		openlist=new TreeSet<int[]>(new Comparator<int[]>(){public int compare(int[] a,int b[]){
          if(a[CURR_F]==b[CURR_F]){if(a[CURR_TIME]==b[CURR_TIME])return a[CURR_NODE]-b[CURR_NODE];else return a[CURR_TIME]-b[CURR_TIME];}
			return a[CURR_F]-b[CURR_F];
			}});
		closelist=new HashSet<Integer>();
		nodes=new HashMap<Integer,int[]>();
      	inner_time=0;
	}
	@Override
	public void enQue(int element) {
		// TODO Auto-generated method stub
		assert nodes.containsKey(element) && !openlist.contains(nodes.get(element));
		openlist.add(nodes.get(element));
	}

	@Override
	public int deQue() {
		// TODO Auto-generated method stub
		assert !openlist.isEmpty();
      	//inner_time++;
		int [] curr=openlist.pollFirst();
		closelist.add(curr[CURR_NODE]);
		return curr[CURR_NODE];
	}

	@Override
	public int getPre(int node) {
		// TODO Auto-generated method stub
		int [] curr=nodes.get(node);
		assert nodes.containsKey(node);
		return curr[PRE_NODE];
	}

	@Override
	public void setPre(int node, int pre) {
		// TODO Auto-generated method stub
		assert nodes.containsKey(node);
		int[] curr=nodes.get(node);
		curr[PRE_NODE]=pre;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return openlist.isEmpty();
	}

	@Override
	public boolean explored(int curr) {
		// TODO Auto-generated method stub
		return true;
	}
/**
 * most useful for ucs and a*
 */
	@Override
	public boolean upDateNode(int node, int pre, int cost, int h) {
		inner_time++;
		// TODO Auto-generated method stub
		if(!nodes.containsKey(node)){
			//totally a new node!
			assert nodes.containsKey(pre) || node==pre;
			int [] nnode=new int[NODE_LEN];
			nnode[CURR_NODE]=node;
			nnode[PRE_NODE]=pre;
			nnode[CURR_COST]=0;
			nnode[CURR_H]=h;
			nnode[CURR_F]=h;
          	nnode[CURR_TIME]=inner_time;
			if(nodes.containsKey(pre)) {nnode[CURR_COST]=cost+getCost(pre);nnode[CURR_F]=h+nnode[CURR_COST];}
			nodes.put(node, nnode);
			return true;
		}
		int [] curr=nodes.get(node);
		boolean needUpdate=false;
		if(curr[CURR_COST]>cost+getCost(pre)){
			if(openlist.contains(nodes.get(node))){
				openlist.remove(nodes.get(node));
			}else if(closelist.contains(node)){
				closelist.remove(node);
			}
			needUpdate=true;
			curr[PRE_NODE]=pre;
			curr[CURR_COST]=cost+getCost(pre);
          	curr[CURR_TIME]=inner_time;
			curr[CURR_F]=curr[CURR_COST]+curr[CURR_H];
		}
		return needUpdate;
	}

	@Override
	public int getCost(int node) {
		// TODO Auto-generated method stub
		assert nodes.containsKey(node);
		int [] curr=nodes.get(node);
		return curr[CURR_COST];
	}

}
