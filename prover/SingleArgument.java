import java.util.HashMap;
/**
 * defualtly, constant are all numbers > 300000
 * @author uiniuhc
 *
 */
public class SingleArgument {
	public static final int MAX_VARIABLE_RANGE=3000000;
	public boolean isVariable;
	public int argumentID;

	private static HashMap<String,Integer> constantMap=new HashMap<String, Integer>();
	private static HashMap<Integer,String> constantNameMap=new HashMap<Integer,String>();
	private static int currentConstantID = MAX_VARIABLE_RANGE+1;
	
	@Override
	public int hashCode() {
		if(isVariable){
			return argumentID;
		}
		return argumentID;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SingleArgument other = (SingleArgument) obj;
		if(other.argumentID!=argumentID) return false;
		return true;
	}
	private SingleArgument(boolean isWhat, int argID){
		isVariable=isWhat;
		argumentID=argID;

	}
	public SingleArgument(SingleArgument arg){
		isVariable=arg.isVariable;
		argumentID=arg.argumentID;

	}
	/**
	 * only use a id to create
	 * 
	 * @param id
	 */
	public SingleArgument(int id){
		argumentID=id;
		if(id>MAX_VARIABLE_RANGE){
			isVariable=false;
		}else{
			isVariable=true;
		}
	}
	/**
	 * generate a constant with label
	 * @param label
	 * @return a constant argument!
	 */
	public static SingleArgument generateAConstant(String label){
		if(constantMap.containsKey(label)){
			return new SingleArgument(false, constantMap.get(label));
		}
		else{
			constantMap.put(label, ++currentConstantID);
			constantNameMap.put(currentConstantID, label);
			return new SingleArgument(currentConstantID);
		}
		
	}
	/**
	 * generate a variable
	 * @param label
	 * @return a constant argument!
	 */
	public static SingleArgument generateAVariable(String label){
		return new SingleArgument(true,getVariableInt(label));
	}
	/**
	 * method for getting a variable's value
	 * because the problem is restricted into only one byte of 'a'-'z', so I simplified it
	 * @param str
	 * @return
	 */
	private static int getVariableInt(String str){
		if(str.length()==0)return -1;
		return str.charAt(0)-'a'+1;
	}
	public void setVariableID(int id){
		argumentID=id;
		if(id>MAX_VARIABLE_RANGE){
			isVariable=false;
		}

	}
	public void printArgument(){
		if(isVariable){
			System.out.print("v"+argumentID);
		}else{
			System.out.print(constantNameMap.get(argumentID));
		}
	}

}
