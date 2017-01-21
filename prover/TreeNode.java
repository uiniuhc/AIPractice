import java.util.ArrayList;

/**
 * This class is for generating a tree for sentences.
 * With such a tree, we can easily do the parsing and transforms.
 * It's actually performed more like a structure, so I made all members public.
 * @author uiniuhc
 *
 */
public class TreeNode {
	public int nodeType;
	public static final int OPT_AND=0;//&
	public static final int OPT_OR=1;//|
	public static final int OPT_NOT=2;//~
	public static final int OPT_INF=3;//=>
	public static final int PREDICATE=4;//leaves!
	public static final int LPAREN=5;
	public static final int RPAREN=6;
	public static final int CONSTANT=7;
	public static final int VARIABLE=8;
	public static final int AND_SENTENCE=9;
	public static final int OR_SENTENCE=10;
	public static final int NOT_SENTENCE=11;
	public static final int INFER_SENTENCE=12;
	public static final int ERR_NODE=13;
	public String nodeLabel;
	public TreeNode left;
	public TreeNode right;
	public ArrayList<TreeNode> parameters;
	public TreeNode(int type,String label){
		nodeType=type;
		nodeLabel=label;
		parameters=new ArrayList<TreeNode>();
		left=null;right=null;
	}
	private void copyFrom(TreeNode node){
		if(node!=null){
			nodeType=node.nodeType;
			left=node.left;
			right=node.right;
			parameters=node.parameters;
			nodeLabel=node.nodeLabel;
		}
	}
	/**
	 * used for making a sentence, apply some rules for the sentence
	 * details: apply the not inside rule, the infer to or rule, and the and is upper than or rule
	 * @param type should be one of the 4 sentences
	 * @param left left of an operand, null if it is constructing a not sentence
	 * @param right right of an operand
	 * pred: left and right are always sentences, type only be and/or/not/infer sentence
	 */
	public TreeNode(int type,TreeNode tleft,TreeNode tright){
		nodeType=ERR_NODE;//the error type should be flushed by any one of the sentences
		if(type==NOT_SENTENCE){
			if(tright.nodeType==NOT_SENTENCE){
				//copy tright.right here
				//we are same
				TreeNode me_myself=tright.right;
				copyFrom(me_myself);
			}else if(tright.nodeType==AND_SENTENCE){
				TreeNode left_baby=new TreeNode(NOT_SENTENCE,null,tright.left);
				TreeNode right_baby=new TreeNode(NOT_SENTENCE,null,tright.right);
				TreeNode me_myself=new TreeNode(OR_SENTENCE,left_baby,right_baby);
				copyFrom(me_myself);
			}else if(tright.nodeType==OR_SENTENCE){
				TreeNode left_baby=new TreeNode(NOT_SENTENCE,null,tright.left);
				TreeNode right_baby=new TreeNode(NOT_SENTENCE,null,tright.right);
				TreeNode me_myself=new TreeNode(AND_SENTENCE,left_baby,right_baby);
				copyFrom(me_myself);
			}else if(tright.nodeType==PREDICATE){
				//normal form
				nodeType=NOT_SENTENCE;
				right=tright;
			}
		}else if(type==AND_SENTENCE){
			nodeType=AND_SENTENCE;
			left=tleft;
			right=tright;
		}else if(type==OR_SENTENCE){
			//when never there is an and sentence, we shift it up!
			if(tleft.nodeType==AND_SENTENCE){
				TreeNode tright2=copyATree(tright);
				TreeNode l=new TreeNode(OR_SENTENCE,tleft.left,tright);
				TreeNode r=new TreeNode(OR_SENTENCE,tleft.right,tright2);
				TreeNode me_myself=new TreeNode(AND_SENTENCE,l,r);
				copyFrom(me_myself);
			}else if(tright.nodeType==AND_SENTENCE){
				TreeNode tleft2=copyATree(tleft);
				TreeNode l=new TreeNode(OR_SENTENCE,tleft,tright.left);
				TreeNode r=new TreeNode(OR_SENTENCE,tleft2,tright.right);
				TreeNode me_myself=new TreeNode(AND_SENTENCE,l,r);
				copyFrom(me_myself);
			}else{
				//normal form
				nodeType=OR_SENTENCE;
				left=tleft;
				right=tright;
			}
		}else if(type==INFER_SENTENCE){
			//not left or right
			//direct to cnf
			TreeNode l=new TreeNode(NOT_SENTENCE,null,tleft);
			TreeNode me_myself=new TreeNode(OR_SENTENCE,l,tright);
			copyFrom(me_myself);
		}
		else{
			nodeType=ERR_NODE;//what the xxx?! error here
		}
	}
	/**
	 * clone all nodes except predicates, no matter how it changes, predicates remain the same.
	 * @param node
	 * @return
	 */
	private TreeNode copyATree(TreeNode node){
		if(node==null)return null;
		if(node.nodeType==PREDICATE){
			return node;
		}
		else if(node.isSentence()){
			return new TreeNode(node.nodeType,copyATree(node.left),copyATree(node.right));
		}
		return node;
	}
	public boolean isOperator(){
		return nodeType==OPT_AND || nodeType==OPT_OR || nodeType==OPT_INF ||nodeType==OPT_NOT ;
	}
	public boolean isSentence(){
		return nodeType==PREDICATE||nodeType==AND_SENTENCE ||nodeType==OR_SENTENCE ||nodeType==NOT_SENTENCE ||nodeType==INFER_SENTENCE;
	}
	/**
	 * print the tree, surround with parenthesis
	 */
	public void printTree(){
		 printTreeHelper();
		 System.out.println("");
	}
	public void printTreeHelper(){
		if(nodeType==PREDICATE){
			System.out.print(nodeLabel);
			System.out.print("(");
			int i=0;
			for(TreeNode n:parameters){
				i++;
				if(n.nodeType==VARIABLE){
					System.out.print("?");
				}
				System.out.print(n.nodeLabel);
				if(i<parameters.size())System.out.print(",");
			}
			System.out.print(")");
		}else{
			System.out.print("(");//surround with parenthesis
			if(left!=null)left.printTreeHelper();
			switch(nodeType){
			case AND_SENTENCE:
				System.out.print("&");
				break;
			case OR_SENTENCE:
				System.out.print("|");
				break;
			case NOT_SENTENCE:
				System.out.print("~");
				break;
			case INFER_SENTENCE:
				System.out.print("=>");
				break;
			}
			if(right!=null)right.printTreeHelper();
			System.out.print(")");
		}
	}
}
