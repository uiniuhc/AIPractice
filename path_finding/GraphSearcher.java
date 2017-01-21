
/**
 * mainly for searches such as dfs and bfs which don't care about cost
 * also can support a* and ucs by using updateNode
 * @author uiniuhc
 *
 */
public interface GraphSearcher {
	void enQue(int element);
	int deQue();
	int getPre(int curr);
	void setPre(int curr,int pre);
	boolean isEmpty();//for search que
	boolean explored(int curr);
	boolean upDateNode(int node,int pre,int cost,int h);//cost is the cost from node to pre
	int getCost(int node);//get the cost to this node
}
