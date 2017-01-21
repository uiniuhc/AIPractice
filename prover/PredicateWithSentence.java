	/**
	 * this class is for binding sentence and predicate's place
	 * 
	 */
	class PredicateWithSentence{
		public Sentence sentence;
		public int indexOfPredicate;//where the predicate shows in the sentence
		PredicateWithSentence(Sentence sen, int i){
			sentence=sen;
			indexOfPredicate=i;
		}
		public void printPWS(){
			System.out.print("pred for: "+sentence.getOnePredicate(indexOfPredicate).getType()+" pred at: "+indexOfPredicate+" sentence: ");
			sentence.printSentence();
		}
	}