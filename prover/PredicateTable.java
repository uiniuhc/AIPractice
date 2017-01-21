import java.util.ArrayList;
import java.util.HashMap;

/**
 * this class is a class which store and index all predicates
 * the predicates will link to some sentences which are in the table
 * each predicates inside a sentence have its variables
 * 
 * The table will never delete a sentence, so the growing is always upfront
 * All the sentences are in sequence (increasing sequence)
 * @author uiniuhc
 *
 */
public class PredicateTable {
	public static final int SAME_RESULT=-1;
	public static final int EMPTY_RESULT=-2;
	private ArrayList<Sentence> sentences;//all sentences
	private HashMap<Integer,ArrayList<Sentence>> sentencesIndex;//arrange the sentences into it's hashgroup, help me find a sentence
	private HashMap<Integer,ArrayList<PredicateWithSentence>> positivePredSentences;//int is predicate type
	private HashMap<Integer, ArrayList<PredicateWithSentence>> negativePredSentences;//int is prediate type
	public PredicateTable(){
		sentences=new ArrayList<Sentence>();
		sentencesIndex = new HashMap<Integer,ArrayList<Sentence>>();
		positivePredSentences = new HashMap<Integer,ArrayList<PredicateWithSentence>>();
		negativePredSentences = new HashMap<Integer, ArrayList<PredicateWithSentence>>();
	}
	/**
	 * add a sentence into table
	 * if have this sentence already, return false
	 * @param sen
	 * @return false for sentence inside the table
	 */
	public boolean addOneSentence(Sentence sen){
		if(hasOneSentence(sen)){
			return false;
		}
		//1. add into sentences and sentencesIndex
		sentences.add(sen);
		ArrayList<Sentence> lsen=sentencesIndex.get(sen.getHash());
		if(lsen==null){
			lsen=new ArrayList<Sentence>();
			lsen.add(sen);
			sentencesIndex.put(sen.getHash(), lsen);
		}else{
			lsen.add(sen);
		}
		//2. according to the predicate, add sentence into positive or negative predsentences table 
		for(int i=0;i<sen.getPredicateNumber();i++){
			addOneSentenceWithPredicate(sen,i);
		}
		return true;
	}
	public void cleanTable(){
		sentences.clear();
		sentencesIndex.clear();
		positivePredSentences.clear();
		negativePredSentences.clear();
	}
	/**
	 * given a sentence, check whether the sentence is inside the table 
	 * @param sen
	 * @return
	 */
	public boolean hasOneSentence(Sentence sen){
		
		if(sentencesIndex.containsKey(sen.getHash())){
			ArrayList<Sentence> sens=sentencesIndex.get(sen.getHash());
			for(Sentence s:sens){
				if(s.equals(sen)){
					//System.out.println("find a sentence is same as the other: ");s.printSentence();sen.printSentence();
					return true;
				}
			}
		}
		return false;
	}
	/**
	 * give the size of sentences
	 * @return
	 */
	public int size(){
		return sentences.size();
	}
	/**
	 * update data in positivepredsentences and negativepredsentences
	 * use predicate.gettype as a hashkey
	 * @param sen
	 * @param predIndex
	 * @return
	 */
	private boolean addOneSentenceWithPredicate(Sentence sen, int predIndex){
		Predicate pred=sen.getOnePredicate(predIndex);
		PredicateWithSentence pws=new PredicateWithSentence(sen,predIndex);
		if(pred.isNegativePredicate()){
			//add this sentence into negativePredSentences
			addOnePredSenWithDirection(pws, negativePredSentences,pred );
		}else{
			//add to positivePredSentences
			addOnePredSenWithDirection(pws, positivePredSentences,pred );
		}
		return false;
	}
	/**
	 * helper for addOnesentenceWithPredicate
	 * @param pws
	 * @param positivePredSentences
	 * @param pred
	 */
	private void addOnePredSenWithDirection(PredicateWithSentence pws, HashMap<Integer,ArrayList<PredicateWithSentence>> sens,Predicate pred ){
		ArrayList<PredicateWithSentence> list;
		if(!sens.containsKey(pred.getType())){
			list=new ArrayList<PredicateWithSentence>();
			sens.put(pred.getType(), list);
		}else{
			list=sens.get(pred.getType());
		};
		list.add(pws);
		//System.out.print ("add pws: ");
		//pws.printPWS();
	}
	/**
	 * try to find all sentences in the table which can resolve with sen,
	 * mainly use predicates in sen to find sentences with opposite predicate.
	 * @param sen
	 * @return 
	 */
	public ArrayList<PredicateWithSentence[]> findAllPairsOfResolvePossibleSentences(Sentence sen){
		return findAllPairsExclusive(sen,-1);
	}
	private ArrayList<PredicateWithSentence[]> findAllPairsExclusive(Sentence sen,int exclusive){
		ArrayList<PredicateWithSentence[]> res=new ArrayList<PredicateWithSentence[]>();
		for(int i=0;i<sen.getPredicateNumber();i++){
			PredicateWithSentence pws=new PredicateWithSentence(sen,i);
			Predicate pred=sen.getOnePredicate(i);
			if(pred.isNegativePredicate()){
				//find all in positive
				if(positivePredSentences.containsKey(pred.getType())){
					for(PredicateWithSentence otherPws:positivePredSentences.get(pred.getType())){
						PredicateWithSentence[] pairOfPws=new PredicateWithSentence[2];
						pairOfPws[0]=pws;
						pairOfPws[1]=otherPws;
						if(pws.sentence.equals(otherPws.sentence))continue;
						res.add(pairOfPws);
					}
				}
			}else{
				if(negativePredSentences.containsKey(pred.getType())){
					for(PredicateWithSentence otherPws:negativePredSentences.get(pred.getType())){
						PredicateWithSentence[] pairOfPws=new PredicateWithSentence[2];
						pairOfPws[0]=pws;
						pairOfPws[1]=otherPws;
						if(pws.sentence.equals(otherPws.sentence))continue;
						res.add(pairOfPws);
						//System.out.println("finds two possible pairs:");
						//pws.printPWS();
						//otherPws.printPWS();
					}
				}
			}
		}
		return res;
	}
	/**
	 * find all self sentences pairs (inside table)
	 * @return
	 */
	public ArrayList<PredicateWithSentence[]> findAllResolvePairsInItself(){
		ArrayList<PredicateWithSentence[]> res=new ArrayList<PredicateWithSentence[]>();
		for(int i=0;i<sentences.size();i++){
			res.addAll(findAllPairsExclusive(sentences.get(i),i));
		}
		return res;
	}
	/**
	 * return a sentence, null for no such sentence
	 * @param i
	 * @return
	 */
	public Sentence getOneSentence(int i){
		if(i<0 || i>=sentences.size()){
			return null;
		}
		else{
			return sentences.get(i);
		}
	}
	
}
