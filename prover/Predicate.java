import java.util.ArrayList;
import java.util.HashMap;

public class Predicate implements Comparable<Predicate>{
	//hashmap for type and string transform
	static public HashMap<String, Integer> predicateTypeMap = new HashMap<String ,Integer>();
	static public HashMap<Integer,String> predicateTypeNameMap=new HashMap<Integer, String>();
	static int predicateNum = 0;
	public static final int NOT_EXIST=-1;
	public static final int TYPEIDERROR=-2;
	/**
	 * return type id for a specific predicate
	 * @param str
	 * @return -1 for not exist
	 */
	static public int getTypeID(String str){
		if(predicateTypeMap.containsKey(str))return predicateTypeMap.get(str);
		return NOT_EXIST;
	}
	static public int addNewType(String str){
		if(predicateTypeMap.containsKey(str)) return TYPEIDERROR;
		++predicateNum;
		predicateTypeMap.put(str, predicateNum);
		predicateTypeNameMap.put(predicateNum,str);
		return predicateNum;
	}
	
	private int typeID;//for checking the type of a predicate
	private boolean isNegative;//for checking whether it is negative or not
	public ArrayList<SingleArgument> arguments;//for all arguments
	Predicate(Predicate pred){
		typeID=pred.typeID;
		isNegative=pred.isNegative;
		arguments=new ArrayList<SingleArgument>();
		for(SingleArgument arg:pred.arguments){
			arguments.add(new SingleArgument(arg));
		}
		//System.out.println("clonning predicate: from "+pred.arguments.size()+" to "+arguments.size());
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((arguments == null) ? 0 : arguments.hashCode());
		result = prime * result + (isNegative ? 1231 : 1237);
		result = prime * result + typeID;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Predicate other = (Predicate) obj;
		if (arguments == null) {
			if (other.arguments != null)
				return false;
		} else if (!arguments.equals(other.arguments))
			return false;
		if (isNegative != other.isNegative)
			return false;
		if (typeID != other.typeID)
			return false;
		return true;
	}
	/**
	 * This is a method to generate a new predicate with opposite type. This is a immutable method.
	 * @param pred
	 * @return a pred with isnegative is opposite to pred
	 */
	public static Predicate generateOppositePredicate(Predicate pred){
		Predicate neg=new Predicate(pred);
		neg.isNegative=!neg.isNegative;
		return neg;
		
	}
	/**
	 * this will use a tree node to generate a predicate
	 * @param node
	 */
	Predicate(TreeNode node){
		typeID=NOT_EXIST;
		isNegative=false;
		arguments= new  ArrayList<SingleArgument>();
		if(node == null){
			return;
		}
		//set negative
		if(node.nodeType==TreeNode.NOT_SENTENCE){
			isNegative=true;
			node=node.right;
			if(node==null)return;
		}
		//set typeID
		if(node.nodeType==TreeNode.PREDICATE){
			int tempID=getTypeID(node.nodeLabel);
			if(tempID==NOT_EXIST){
				typeID=addNewType(node.nodeLabel);
				if(typeID<0){typeID=NOT_EXIST;return;}
			}else{
				typeID=tempID;
			}
		}
		//get all arguments
		if(node.parameters==null) {typeID=NOT_EXIST; return;}
		for(TreeNode arg:node.parameters){
			if(arg.nodeType==TreeNode.VARIABLE){
				arguments.add(SingleArgument.generateAVariable(arg.nodeLabel));
			}else if(arg.nodeType==TreeNode.CONSTANT){
				arguments.add(SingleArgument.generateAConstant(arg.nodeLabel));
			}
		}
		if(arguments.size()==0){
			typeID=NOT_EXIST;
		}
		//System.out.println("\ngenerating new predicate");
		//printPredicate();
	}
	public void printPredicate(){
		if(isNegative)System.out.print("~");
		System.out.print(predicateTypeNameMap.get(typeID)+"(");
		for(int i=0;i<arguments.size();i++){
			arguments.get(i).printArgument();;
			if(i!=arguments.size()-1){
				System.out.print(",");
			}
		}
		System.out.print(")");
		
	}
	
	public int getType(){
		return typeID;
	}
	public boolean isNegativePredicate(){
		return isNegative;
	}
	@Override
	public int compareTo(Predicate pred) {
		// TODO Auto-generated method stub
		if(typeID!=pred.typeID)return typeID-pred.typeID;
		if(isNegative && !pred.isNegative) return 1;
		if(pred.isNegative && !isNegative) return -1;
		for(int i=0;i<pred.arguments.size();i++){
			int aID=arguments.get(i).argumentID;
			int bID=pred.arguments.get(i).argumentID;
			if(aID!=bID)return aID-bID;
		}
		return 0;
	}
}

