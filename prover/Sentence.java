import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * this class is for storing sentences
 * every sentence has a time stamp
 * and a table of predicates
 * every predicate has its arguments, which has two features, type and no.
 * (type can show whether it is a constant or variable, and no. is the id of a variable)
 * 
 * @author uiniuhc
 *
 */
public class Sentence {

	//private static int sentenceCurrentUsingID = 0;//for giving a id to an sentence
	
	public ArrayList<Predicate> sentence;
	private int sentanceHashCode;
	private final static int VARIABLE_MAX=65536;//just a value, not a big deal, should be large enough for large sentences, this large is not possible to solve!
	private boolean isRegulated;
	Sentence(){
		//sentenceCurrentUsingID++;//always goes up
		isRegulated=false;
		sentence=new ArrayList<Predicate>();
		
	}
	


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for(int i=0;i<sentence.size();i++){
			result=prime*result+sentence.get(i).getType();
		}
		return result;
	}
	@Override
	public boolean equals(Object obj){
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Sentence other = (Sentence) obj;
		if (sentence == null) {
			if (other.sentence != null)
				return false;
		} else if(!other.sentence.equals(sentence))return false;
		return true;
	}


	/**
	 * This is for add a predicate for sentence, not changing the inner store of sentence
	 * @param pred
	 */
	public void addAPredicate(Predicate pred){
		sentence.add(pred);
	};
	/**
	 * give the size of sentences
	 * @return
	 */
	public int getPredicateNumber(){
		return sentence.size();
	}
	public int getHash(){
		return sentanceHashCode;
	}
	/**
	 * Very Important!!!!!!!! You have to do it, otherwise, there will be infinite loop
	 * However, if one sentence has two same predicates, still possible to not find the pitfall.
	 * 
	 * this will make the sentence into norm form, so compare will be possible
	 * sort all predicates
	 * and label all variables in sequence
	 * give the variableCount
	 * generate sentencehashcode for future compare
	 */
	public void regulationOfSentence(){
		//first, sort array with predicate type
		sentence.sort(new Comparator<Predicate>(){
			public int compare(Predicate a, Predicate b){
				//faster, only see some of them
				return a.compareTo(b);}
			});
		//construct a new sentence for reducing same predicate
		ArrayList<Predicate> sen=new ArrayList<Predicate>();
		boolean first=true;
		Predicate predLast=null;
		for(Predicate pred:sentence){
			if(first){
				first=false;predLast=pred;
				sen.add(pred);
			}else{
				if(!pred.equals(predLast)){
					sen.add(pred);
					predLast=pred;
				}
			}
		}
		sentence=sen;
		//second, count all variables, and make a map for variables in the sentence
		HashMap<Integer,Integer> varMap=new HashMap<Integer,Integer>();
		
		int currVarID=1;
		for( Predicate pred: sentence){
			for(SingleArgument arg:pred.arguments){
				if(arg.isVariable){
					if(!varMap.containsKey(arg.argumentID)){
						varMap.put(arg.argumentID,currVarID++);
					}
				}
			}
		}
		//apply the map, so the sentences' all variable will have same sequence
		for( Predicate pred: sentence){
			for(SingleArgument arg:pred.arguments){
				if(arg.isVariable){
					arg.setVariableID(varMap.get(arg.argumentID));
				}
			}
		}
		isRegulated=true;
		sentanceHashCode=hashCode();
	};
	
	
	/**
	 * unify two sentences
	 * preD: all sentences are in regular form
	 * @param senOne first sentence
 	 * @param senTWO second sentence
	 * @param predIndexOne the predicate in 
	 * @param predIndexTwo
	 * @return null for fail
	 */
	public static Sentence unifyTwoSentences(Sentence senOne,Sentence senTwo, int predIndexOne,int predIndexTwo){
		if( !senOne.isRegulated || !senTwo.isRegulated){
			System.out.println("a sentence is not regulated");
			return null;
		}
		
		//1. find the two predicate, and try to compare them
		Predicate predOne=senOne.sentence.get(predIndexOne);
		Predicate predTwo=senTwo.sentence.get(predIndexTwo);
		//these part should all be exceptions!!!! but I don;t have time to deal with them. so just if is ok for now
		if(predOne.getType()!=predTwo.getType()){
			System.out.println("cant unify: two predicates are not the same type");
			return null;
		}
		if(predOne.isNegativePredicate()==predTwo.isNegativePredicate()){
			System.out.println("cant unify: two predicates are not exclusive to each other");
			return null;
		}
		if(predOne.arguments.size()!=predTwo.arguments.size()){
			System.out.println("cant unify: not the same length!!!!");
			return null;
		}
		Predicate pOne=new Predicate(predOne);//clone pred for manipulating
		Predicate pTwo=new Predicate(predTwo);
		HashMap<Integer,Integer> substituteIDMap=new HashMap<Integer,Integer>();
		//init pTwo with diffrent variable ID
		for(int i=0;i<pTwo.arguments.size();i++){
			if(pTwo.arguments.get(i).isVariable){
				pTwo.arguments.get(i).setVariableID(pTwo.arguments.get(i).argumentID+VARIABLE_MAX);
			}
			//init substituteIDMap
			int idOne=pOne.arguments.get(i).argumentID;
			if(!substituteIDMap.containsKey(idOne)){
				substituteIDMap.put(idOne, idOne);
			}
			int idTwo=pTwo.arguments.get(i).argumentID;
			if(!substituteIDMap.containsKey(idTwo)){
				substituteIDMap.put(idTwo, idTwo);
			}
		}
		//loop through arguments, and get substitutions
		//naive algorithm: for every difference, try to do substitutions
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		boolean nothingChange=false;
		while(!nothingChange){
			nothingChange=true;
			//perdigit compare
			for(int i=0;i<pOne.arguments.size();i++){
				if(pOne.arguments.get(i).argumentID==pTwo.arguments.get(i).argumentID){
					continue;
				}
				nothingChange=false;//different ID => must can be some sub here
				SingleArgument arg1=pOne.arguments.get(i);
				SingleArgument arg2=pTwo.arguments.get(i);
				if((!arg1.isVariable) && (!arg2.isVariable)){
					//can't unify!
					return null;
				}
				if(arg1.isVariable && (!arg2.isVariable)){
					//can unify
					//from arg1 to arg2
					int id1=arg1.argumentID;
					int id2=arg2.argumentID;
					applySimpleSub(pOne, id1, id2);
					applySimpleSub(pTwo, id1, id2);
					//add sub
					substituteIDMap.put(id1, id2);
				}else{
					//from arg2 to arg1
					int id1=arg1.argumentID;
					int id2=arg2.argumentID;
					applySimpleSub(pOne, id2,id1);
					applySimpleSub(pTwo, id2,id1);
					substituteIDMap.put(id2,id1);
				}
			}
		}
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		//make new sentence with help of the substitutionMap
		Sentence res=new Sentence();
		for(int i=0;i<senOne.sentence.size();i++){
			if(i==predIndexOne)continue;
			Predicate tp=getNewPredicateWithSub(senOne.sentence.get(i),substituteIDMap,0);
			if(tp!=null){res.addAPredicate(tp);}
			else{System.out.println("error when substitution is running)"); return null;}
		}
		for(int i=0;i<senTwo.sentence.size();i++){
			if(i==predIndexTwo)continue;
			Predicate tp=getNewPredicateWithSub(senTwo.sentence.get(i),substituteIDMap,VARIABLE_MAX);
			if(tp!=null){res.addAPredicate(tp);}
			else{System.out.println("error when substitution is running)"); return null;}
		}
		res.regulationOfSentence();
		return res;
	}
	/**
	 * simple substitution inside a predicate
	 * @param pred
	 * @param fromID
	 * @param toID
	 */
	private static void applySimpleSub(Predicate pred, int fromID, int toID){
		for(int i=0;i<pred.arguments.size();i++){
			if(pred.arguments.get(i).argumentID==fromID){
				pred.arguments.set(i, new SingleArgument(toID));
			}
		}
	}
	/**
	 * If don't have it, return id directly
	 * If have it, find the final decision.
	 * @param id
	 * @param sub
	 * @return id for substitution
	 */
	private static int getFinalValueFromSubMap(int id,HashMap<Integer,Integer> sub){
		if(!sub.containsKey(id))return id;
		int p=id;
		while(sub.containsKey(p) && sub.get(p)!=p){
			p=sub.get(p);
		}
		if(p!=id && sub.get(id)!=p){
			sub.put(id, p);//short cut for future use
			return p;
		}
		return p;
	}
	/**
	 * give the size of predicates
	 * @return
	 */
	public int size(){
		return sentence.size();
	}
/**
 * generate a new predicate from substitution.
 * @param oldPred
 * @param sub 
 * @param offset If setting the id for different sentence, this can be used to add on the ID, normally, use 0 is ok
 * @return new predicate
 */
	private static Predicate getNewPredicateWithSub(Predicate oldPred,HashMap<Integer,Integer> sub, int offSet){

		Predicate res=new Predicate(oldPred);
		for(int i=0;i<res.arguments.size();i++){
			//res.arguments.get(i).argumentID+=offSet;
			
			if(!res.arguments.get(i).isVariable){continue;}//only variables need change
			int oldID=res.arguments.get(i).argumentID+offSet;
			res.arguments.get(i).setVariableID(oldID);
			
			int subID=getFinalValueFromSubMap(oldID,sub);
			if(subID!=oldID){
				res.arguments.set(i,new SingleArgument( sub.get(oldID)));
			}
		}
		return res;
	}
	/**
	 * print a sentence in new format! pretty cool !
	 */
	public void printSentence(){
		for(int i=0;i<sentence.size();i++){
			sentence.get(i).printPredicate();
			if(i!=sentence.size()-1){
				System.out.print("|");
			}
		}
		System.out.println("");
	}
	/**
	 * give the predicate in the place of i
	 * @param i the index of specific predicate
	 * @return null for error(out of bounds
	 */
	public Predicate getOnePredicate(int i){
		if(i>=sentence.size() || i<0){
			System.out.println("error in Sentence.getOnePredicate!!!!!!!!!!!!!!!!!!!!!");
			return null;
		}
		return sentence.get(i);
	}
	
}
