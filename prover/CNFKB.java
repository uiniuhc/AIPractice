import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;

/**
 * this class is for constructing a cnf kb for resolution
 * @author uiniuhc
 */
public class CNFKB {
	private PredicateTable truthTable;
	private PredicateTable queryTable;
	private PredicateTable waitTable;
	public CNFKB(){
		truthTable=new PredicateTable();
		queryTable=new PredicateTable();
		waitTable=new PredicateTable();
		
	}
	
	/**
	 * convert one sentence into cnf and then insert into the kb
	 * @param str
	 * @return true when the sentence is valid, and the work is done. false when the sentence is not valid, and the KB remain the same
	 */
	public boolean addOneSentence(String str){
		
		return addOneSentenceToTable(str, truthTable);
		//add results into myKB
	}
	private boolean addOneSentenceToTable(String str,PredicateTable table){
		//first convert the string to a tree
		TreeNode myTree=scanString(str);
		if(myTree==null){
			System.out.print("empty sentence or invalid sentence!");
			return false;//invalid string!
		}else{
			myTree.printTree();
		}
		//after we get the tree in cnf form:
		//1. split ands
		ArrayList<ArrayList<TreeNode>> list_dis=new ArrayList<ArrayList<TreeNode>>();
		split_and(myTree,list_dis);
		//2. standardize variables
		//printListOfDis(list_dis);
		//3. make it in to sentence format, and add into the table
		for( ArrayList<TreeNode> sentence:list_dis){
			//format changing
			Sentence sen=new Sentence();
			for(TreeNode pred:sentence){
				Predicate p=new Predicate(pred);
				if(p.getType()!=Predicate.NOT_EXIST){
					sen.addAPredicate(p);
				}else{
					System.out.println("a predicate is not exist!!!!: error");
				}
			}
			sen.regulationOfSentence();
			//System.out.println("this is generated in another form");
			//sen.printSentence();
			table.addOneSentence(sen);
		}
		//that's it
		return true;
	}
	/**
	 * for clear and start a new query
	 */
	private void clearQueryTable(){
		queryTable.cleanTable();
		queryTable=new PredicateTable();
	}
	public static final int  QUERY_HAS_ERROR=-1;//get error from functions.
	public static final int  QUERY_IS_TRUE=1;//empty sentence
	public static final int QUERY_IS_FALSE=2;//no more sentences to resolve
	public static final int QUERY_TIME_OUT=3;//too many sentences, give up 
	public static final int MAX_SEARCH_ROUND=2000;//resolve times shouldn't exceed this value, otherwise, return timeout
	/**
	 * do one query
	 * return the result
	 * @param str
	 * @return int as the const result, -1 for error, 1 is true, 2 is false
	 */
	public int startOneQuery(String str){
		clearQueryTable();
		//query with contradiction
		if(!addOneSentenceToTable("~("+str+")", queryTable))return QUERY_HAS_ERROR;
		if(queryTable.size()!=1) {System.out.println("queryTable has not only one sentences"); return QUERY_HAS_ERROR;}
		if(queryTable.getOneSentence(0).size()==0) {System.out.println("query should only use one predicate!!!!!!!"); return QUERY_HAS_ERROR;}
		//try original direction
		int resNormal=queryWithQueryTable();
		return resNormal;
		/*
		if(resNormal!=QUERY_TIME_OUT) return resNormal;

		System.out.println("*****time out!!!!! start another try");
		//try opposite direction
		clearQueryTable();
		//create opposite query
		if(!addOneSentenceToTable(str, queryTable))return QUERY_HAS_ERROR;
		if(queryTable.size()!=1) {System.out.println("queryTable has not only one sentences"); return QUERY_HAS_ERROR;}
		if(queryTable.getOneSentence(0).size()==0) {System.out.println("query should only use one predicate!!!!!!!"); return QUERY_HAS_ERROR;}
		int resOppo=queryWithQueryTable();
		if(resOppo==QUERY_HAS_ERROR)return resOppo;
		//return opposite result
		if(resOppo!=QUERY_TIME_OUT){
			if(resOppo==QUERY_IS_FALSE){
				return QUERY_IS_TRUE;//flaw, how can it be true?
			}else{
				return QUERY_IS_FALSE;
			}
		}else{
			return QUERY_IS_FALSE;//twice time out, random or false?
		}*/
		//main query logic
		//////////////////////////////////////////////////////////////////////////////////////////
		//It should be a state machine
		/*
		truthTable.findAllResolvePairsInItself();
		System.out.println("\n\n\n\n***********all possible for the quering sentence");
		ArrayList<PredicateWithSentence[]> pairs=truthTable.findAllPairsOfResolvePossibleSentences(queryTable.getOneSentence(0));
		for(PredicateWithSentence[] pair:pairs){
			System.out.println("unifying two sentences: ");
			pair[0].sentence.printSentence();
			pair[1].sentence.printSentence();
			Sentence result=unifyWithPWSPairs(pair[0],pair[1]);
			if(result==null){
				System.out.println("these two cann't resolve");
			}else{
				result.printSentence();
				System.out.println("result sentence size: "+result.getPredicateNumber());
			}
		}
		return QUERY_IS_FALSE;
		*/
		//////////////////////////////////////////////////////////////////////////////////////////
		
	}
	/**
	 * a helper function to do query. queryWith contradiction, so finding an empty sentence means true. 
	 * unification result with one sentence from queryTable will be put in queryTable. Results with two sentences both from truthTable will be in truthTable.
	 * Always keep trying to unify first from queryTable related sentences!
	 * If no possible sentences can unify, start using truthTable.
	 * @return
	 */
	PriorityQueue<PredicateWithSentence[]> pairsTruth=new PriorityQueue<PredicateWithSentence[]>(MAX_SEARCH_ROUND, 
			new Comparator<PredicateWithSentence[]>(){
				public int compare(PredicateWithSentence[] a,PredicateWithSentence[] b){			
					return a[0].sentence.size()+a[1].sentence.size()-b[0].sentence.size()-b[1].sentence.size();
				}
				});
	Queue<Sentence> waitForTruthTable=new LinkedList<Sentence>();
	private int queryWithQueryTable(){
		int roundCount=0;//how many unify runs?
		PriorityQueue<PredicateWithSentence[]> pairsQuery=new PriorityQueue<PredicateWithSentence[]>(MAX_SEARCH_ROUND, 
				new Comparator<PredicateWithSentence[]>(){
					public int compare(PredicateWithSentence[] a,PredicateWithSentence[] b){			
						return a[0].sentence.size()+a[1].sentence.size()-b[0].sentence.size()-b[1].sentence.size();
					}
					});

		//1. init get all queryTable related sentences
		pairsQuery.addAll(queryTable.findAllResolvePairsInItself());
		for(int i=0;i<queryTable.size();i++){
			pairsQuery.addAll(truthTable.findAllPairsOfResolvePossibleSentences(queryTable.getOneSentence(i)));
		}

		//2.start searching
		boolean searchingInQuerySentences=true;
		boolean firstTruth=true;//for lazy start
		while(roundCount<MAX_SEARCH_ROUND){
			//see whether there are no more sentences
			if((!firstTruth) && pairsTruth.size()==0 && pairsQuery.size()==0 && waitForTruthTable.size()==0){
				return QUERY_IS_FALSE;//no more sentences
			}
			//do a unify with a pair, only add round for successful unifications.
			if(searchingInQuerySentences){
				if(pairsQuery.size()==0){
					//jump out
					searchingInQuerySentences=false;
					continue;
				}
				PredicateWithSentence[] pairPws=pairsQuery.poll();
				/*
				int newSize=pairPws[0].sentence.size()+pairPws[1].sentence.size()-2;
				if(newSize>pairPws[0].sentence.size() && newSize>=pairPws[1].sentence.size()){
					continue;
				}*/
				Sentence resultSen=unifyWithPWSPairs(pairPws[0], pairPws[1]);
				//deal with sentence
				if(resultSen==null) {
					//System.out.println("sentences can't unify, keep moving");
					continue;//can't unify
				}
				if(resultSen.size()==0){
					return QUERY_IS_TRUE;//infer with contradiction
				}
				if(queryTable.hasOneSentence(resultSen)){
					//no need to use it
					continue;
				}else{
					pairsQuery.addAll(queryTable.findAllPairsOfResolvePossibleSentences(resultSen));
					pairsQuery.addAll(truthTable.findAllPairsOfResolvePossibleSentences(resultSen));
					queryTable.addOneSentence(resultSen);
				}
			}else{
				if(firstTruth){
					firstTruth=false;
					//init
					pairsTruth.addAll(truthTable.findAllResolvePairsInItself());
				}
				if(pairsTruth.size()==0){
					while(waitForTruthTable.size()>0 && pairsTruth.size()==0){
						Sentence inTruth=waitForTruthTable.poll();
						if(truthTable.hasOneSentence(inTruth)){continue;}
						pairsTruth.addAll(truthTable.findAllPairsOfResolvePossibleSentences(inTruth));
						truthTable.addOneSentence(inTruth);
					}
					continue;
				}
				
				PredicateWithSentence[] pairPws=pairsTruth.poll();
				/*
				int newSize=pairPws[0].sentence.size()+pairPws[1].sentence.size()-2;
				if(newSize>pairPws[0].sentence.size() && newSize>=pairPws[1].sentence.size()){
					continue;
				}*/
				Sentence resultSen=unifyWithPWSPairs(pairPws[0], pairPws[1]);
				//deal with sentence, use this sentence, and try to play with queryTable
				if(resultSen==null) {
					//System.out.println("sentences can't unify, keep moving");
					continue;//can't unify
				}
				if(resultSen.size()==0){
					return QUERY_IS_TRUE;//infer with contradiction, although that's not possible!
				}
				if(truthTable.hasOneSentence(resultSen)){
					//no need to use it
					continue;
				}else{
					pairsQuery.addAll(queryTable.findAllPairsOfResolvePossibleSentences(resultSen));
					searchingInQuerySentences=true;
					
					if(waitTable.addOneSentence(resultSen)){
						waitForTruthTable.add(resultSen);
					}
				}
				
			}
			roundCount++;//only count valid unifications!
		}
		return QUERY_TIME_OUT;
	}
	/**
	 * 
	 * @param pws1
	 * @param pws2
	 * @return null for fail
	 */
	private Sentence unifyWithPWSPairs(PredicateWithSentence pws1,PredicateWithSentence pws2){
		//System.out.println("unifying two sentences: ");
		//pws1.printPWS();
		//pws2.printPWS();
		Sentence res=Sentence.unifyTwoSentences(pws1.sentence, pws2.sentence, pws1.indexOfPredicate, pws2.indexOfPredicate);
		//if(res!=null)res.printSentence();
		return res;
		
	}

	/**
	 * translate a string to treenode form
	 * @param str
	 * @return
	 */
	public TreeNode readOneSentence(String str){
		//first convert the string to a tree
			TreeNode myTree=scanString(str);
		if(myTree==null){
			System.out.print("empty sentence or invalid sentence!");
			return null;//invalid string!
		}else{
			myTree.printTree();
		}
		return myTree;
	}
	/**
	 * print a list of treenode form sentences
	 * @param list
	 */
	/*
	private void printListOfDis(ArrayList<ArrayList<TreeNode>>list){
		for(ArrayList<TreeNode> l:list){
			int i=0;
			for(TreeNode n:l){
				i++;
				n.printTreeHelper();
				if(i<l.size())System.out.print(" | ");
			}
			System.out.println("");
		}
	}*/
	/**
	 * recursively find disjunction clauses, and group them together
	 * @param node 
	 * @return 2d array with each row a cnf sentence
	 */
	private  void split_and(TreeNode node,ArrayList<ArrayList<TreeNode>> res){
		if(!node.isSentence()){
			System.out.println("error for split and: not a sentence");
		}
		if(node.nodeType!=TreeNode.AND_SENTENCE){
			ArrayList<TreeNode> one_or=new ArrayList<TreeNode>();
			spread_or(node,one_or);
			res.add(one_or);
		}else{
			//and sentence
			split_and(node.left,res);
			split_and(node.right,res);
		}
		return ;
	}
	/**
	 * from a or node, add all leaves into the list
	 * @param node
	 * @return
	 */
	private void spread_or(TreeNode node,ArrayList<TreeNode> res){
		if(node==null){
			System.out.println("out of expectation! + emptynode");
			return ;
		}
		
		if(node.nodeType==TreeNode.NOT_SENTENCE || node.nodeType==TreeNode.PREDICATE){
			res.add(node);
		}
		else if(node.nodeType==TreeNode.OR_SENTENCE){
			//dfs find all elements,and then
			spread_or(node.left,res);
			spread_or(node.right,res);
		}else{
			System.out.println("out of expectation! + nodeType-"+node.nodeType);
		}
		
	}
	
	/**
	 * from left to right, do the converge
	 * @param node_pool
	 * @return the result of node, null for invalid or no nodes to converge
	 */
	private TreeNode convergeToOne(Stack<TreeNode> node_pool){
		if(node_pool.size()==0)return null;
		if(node_pool.size()==1)return node_pool.pop();
		TreeNode res_sentence=null;
		while(node_pool.size()>1){
			//every time get one out
			if(node_pool.peek().isSentence()){
				if(node_pool.size()==2)return null;
				//left op right
				TreeNode left=node_pool.pop();
				TreeNode op=node_pool.pop();
				TreeNode right=node_pool.pop();
				if(!op.isOperator() || !right.isSentence()){
					return null;//error!!!!!!!!!
				}
				switch(op.nodeType){
					case TreeNode.OPT_AND:
						res_sentence=new TreeNode(TreeNode.AND_SENTENCE,left,right);
						break;
					case TreeNode.OPT_OR:
						res_sentence=new TreeNode(TreeNode.OR_SENTENCE,left,right);
						break;
					case TreeNode.OPT_INF:
						res_sentence=new TreeNode(TreeNode.INFER_SENTENCE,left,right);
						break;
					
				}
				node_pool.push(res_sentence);
			}
			else if(node_pool.peek().isOperator()){
				TreeNode op=node_pool.pop();
				TreeNode right=node_pool.pop();
				if(op.nodeType!=TreeNode.OPT_NOT || !right.isSentence()){
					return null;
				}
				res_sentence=new TreeNode(TreeNode.NOT_SENTENCE,null,right);
				node_pool.push(res_sentence);
			}else{
				return null;
			}
		}
		return node_pool.pop();
	}
	/**
	 * get the first 2 elements of stack, and make them as not back into the stack,
	 * apply recursively, till the bottom (predicate)
	 * @param nodes
	 * @return
	 */
	private TreeNode applyNot(Stack<TreeNode> nodes){
		TreeNode right=nodes.pop();
		TreeNode op=nodes.pop();
		if(op.nodeType!=TreeNode.OPT_NOT || !right.isSentence()){
			return null;//error
		}
		
		TreeNode not_sentence=new TreeNode(TreeNode.NOT_SENTENCE,null,right);
		nodes.push(not_sentence);
		return not_sentence;
	}
/**
 * scan a string and construct a tree with cnf form
 * on top of the tree, there are all and operators
 * @param str
 * @return
 */
	private TreeNode scanString(String str){
		//finite state machine!!!!!!!!!!!!
		final int BASE_STATE=0;
		final int L_PAREN=1;//l paren
		final int IN_PRED=2;//pred after lpare
		//final int AFTER_OPT=3;
		final int TRACK_CONST=4;
		final int TRACK_VAR=5;
		final int TRACK_PRED=6;
		final int TRACK_INF=7;
		final int TRACK_NOT=8;
		final int ERROR=-1;
		int curr_state=BASE_STATE;
		Stack<Integer> states=new Stack<Integer>();
		Stack<TreeNode> nodes=new Stack<TreeNode>();//node with out connections
		Stack<TreeNode> temp_pool=new Stack<TreeNode>();
		char [] sentence=str.toCharArray();
		StringBuilder curr_Tracking=new StringBuilder();
		for(char ch:sentence){
			//loop and analysis
			if(ch==' '||ch=='\t'||ch=='\r'||ch=='\n')continue;//useless information
			switch(curr_state){
				case BASE_STATE:
					if(ch=='('){
						states.push(curr_state);
						curr_state=L_PAREN;
						nodes.push(new TreeNode(TreeNode.LPAREN,""));
					}else if(ch>='A' && ch<='Z'){
						states.push(curr_state);
						curr_state=TRACK_PRED;
						curr_Tracking.append(ch);
					}else if(ch=='~'){
						states.push(curr_state);
						curr_state=TRACK_NOT;
						nodes.push(new TreeNode(TreeNode.OPT_NOT,""));
					}else if(ch=='&'){
						nodes.push(new TreeNode(TreeNode.OPT_AND,""));
					}else if(ch=='|'){
						nodes.push(new TreeNode(TreeNode.OPT_OR,""));
					}else if(ch=='='){
						states.push(curr_state);
						curr_state=TRACK_INF;
					}else{
						return null;//error!
					}
					break;
				case L_PAREN:
					if(ch=='('){
						states.push(curr_state);
						curr_state=L_PAREN;
						nodes.push(new TreeNode(TreeNode.LPAREN,""));
					}else if(ch==')'){
						//exit state
						if(states.isEmpty())return null;//error
						curr_state=states.pop();
						//get all nodes caculated
						while(true){
							TreeNode k=nodes.pop();
							if(k.nodeType==TreeNode.LPAREN)break;
							else{
								temp_pool.push(k);
							}
						}
						int old_size=temp_pool.size();
						TreeNode inParen=convergeToOne(temp_pool);
						if(inParen!=null)nodes.push(inParen);
						else if(old_size>0)return null;//error
						temp_pool.clear();
						if(curr_state==TRACK_NOT){
							curr_state=states.pop();
							if(nodes.size()>=2){
								if(null==applyNot(nodes))return null;
							}
						}
					}else if(ch>='A' && ch<='Z'){
						states.push(curr_state);
						curr_state=TRACK_PRED;
						curr_Tracking.append(ch);
					}else if(ch=='~'){
						states.push(curr_state);
						curr_state=TRACK_NOT;
						nodes.push(new TreeNode(TreeNode.OPT_NOT,""));
					}else if(ch=='&'){
						nodes.push(new TreeNode(TreeNode.OPT_AND,""));
					}else if(ch=='|'){
						nodes.push(new TreeNode(TreeNode.OPT_OR,""));
					}else if(ch=='='){
						states.push(curr_state);
						curr_state=TRACK_INF;
					}else{
						return null;//error!
					}
					break;
				case TRACK_NOT:
					///////////////////////top==sentence means we can do the converge now!!!
					if(ch=='('){
						states.push(curr_state);
						curr_state=L_PAREN;
						nodes.push(new TreeNode(TreeNode.LPAREN,""));
					}else if(Character.isUpperCase(ch)){
						states.push(curr_state);
						curr_state=TRACK_PRED;
						curr_Tracking.append(ch);
					}else if(ch=='~'){
						curr_state=states.pop();//turn back to last state
						nodes.pop();//pop the last node
					}else
					{
						return null;
					}
					break;
				case TRACK_PRED:
					if(Character.isLetter(ch)){
						curr_Tracking.append(ch);
					}else if(ch=='('){
						//states.push(curr_state);
						curr_state=IN_PRED;
						TreeNode pred=new TreeNode(TreeNode.PREDICATE,curr_Tracking.toString());
						curr_Tracking=new StringBuilder();//reset string for future use
						nodes.add(pred);
					}else{
						return null;//error
					}
					break;
				case TRACK_INF:
					if(ch=='>'){
						nodes.push(new TreeNode(TreeNode.OPT_INF,""));
						curr_state=states.pop();
					}else{
						return null;
					}
					break;
				case IN_PRED:
					//inside predicate, can shift to track const or track var depend on the first char
					if(Character.isUpperCase(ch)){
						curr_state=TRACK_CONST;
						curr_Tracking.append(ch);
					}else if(Character.isLowerCase(ch)){
						curr_state=TRACK_VAR;
						curr_Tracking.append(ch);
					}else if(ch==')'){
						curr_state=states.pop();
						if(curr_state==TRACK_NOT){
							curr_state=states.pop();
							if(nodes.size()>=2){
								if(null==applyNot(nodes))return null;
							}
						}
					}else{
						return null;
					}
					break;
					
				case TRACK_CONST:
					if(Character.isLetter(ch)){
						curr_Tracking.append(ch);
					}else if(ch==','){
						nodes.peek().parameters.add(new TreeNode(TreeNode.CONSTANT,curr_Tracking.toString()));
						curr_Tracking=new StringBuilder();
						curr_state=IN_PRED;
					}else if(ch==')'){
						nodes.peek().parameters.add(new TreeNode(TreeNode.CONSTANT,curr_Tracking.toString()));
						curr_Tracking=new StringBuilder();
						curr_state=states.pop();
						if(curr_state==TRACK_NOT){
							curr_state=states.pop();
							if(nodes.size()>=2){
								if(null==applyNot(nodes))return null;
							}
						}
					}else{
						return null;
					}
					break;
				case TRACK_VAR:
					if(Character.isLetter(ch)){
						curr_Tracking.append(ch);
					}else if(ch==','){
						nodes.peek().parameters.add(new TreeNode(TreeNode.VARIABLE,curr_Tracking.toString()));
						curr_Tracking=new StringBuilder();
						curr_state=IN_PRED;
					}else if(ch==')'){
						nodes.peek().parameters.add(new TreeNode(TreeNode.VARIABLE,curr_Tracking.toString()));
						curr_Tracking=new StringBuilder();
						curr_state=states.pop();
						if(curr_state==TRACK_NOT){
							curr_state=states.pop();
							if(nodes.size()>=2){
								if(null==applyNot(nodes))return null;
							}
						}
					}else{
						return null;
					}
					break;
				case ERROR: return null;
			}
			
		}
		//converge the last time, all nodes collapse to one, that's the answer
		while(!nodes.isEmpty()){
			temp_pool.push(nodes.pop());
		}
		return convergeToOne(temp_pool);
		
	}
}
