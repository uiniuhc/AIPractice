/**
 * this class is for solve the game
 * with minimax
 * the game is always player x first, and then, player y
 * player x is the max and player y is the min
 * @author uiniuhc
 *
 */
public class GameSolver {
	GameBoard gboard;
	int depth;
	GameSolver(GameBoard gb,int d){
		gboard=gb;depth=d;
	}
	public long count_func_calls=0;
	//now let's do it
	/**
	 *This method use the gameboard to run minimax,
	 *start with player x who is the max player
	 *then, player y who is the min player
	 *we want to return the action in the return value
	 *and the position of action is in an array which will be modified inside the funciton 
	 *
	 *The process will change and restore the board
	 * which means the board will still be the same after this function finished 
	 * @param position this is the position which will return
	 * @return it will return the action (raid or stake?)
	 */
	public int MiniMax(int[] position){
		count_func_calls++;
		int max=Integer.MIN_VALUE;
		int max_x=-1;int max_y=-1;
		int max_action=-1;
		int size=gboard.getSize();
		//test all stakes
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				int toWhom=gboard.getOccupy(i, j);
				int player_x_score_checker=gboard.getPlayerXScore();
				int player_y_score_checker=gboard.getPlayerYScore();
				if(!gboard.takeAction(i, j, GameBoard.PLAYER_X, GameBoard.USE_STAKE))continue;//didn't take the action
				int val=MiniMaxHelper(GameBoard.PLAYER_Y,false,depth-1);
				if(val>max){
					max=val;
					max_x=i;max_y=j;max_action=GameBoard.USE_STAKE;
				}
				//restore the board
				if(!gboard.setOnlyOneCell(i, j, toWhom, player_x_score_checker, player_y_score_checker)){
					System.out.println("first:there is a mistake when we are restoring an stake");
				}
			}
		}
		//test all raids
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				int player_x_score_checker=gboard.getPlayerXScore();
				int player_y_score_checker=gboard.getPlayerYScore();
				SurroundedCell surr=gboard.getSurroundings(i, j);
				if(!gboard.takeAction(i, j, GameBoard.PLAYER_X, GameBoard.USE_RAID))continue;
				int val=MiniMaxHelper(GameBoard.PLAYER_Y,false,depth-1);
				if(val>max){
					max=val;
					max_x=i;max_y=j;max_action=GameBoard.USE_RAID;
					//System.out.println("find a better raid at: "+max_x+" "+max_y);
				}
				if(!gboard.setWithSurroundings(surr, player_x_score_checker, player_y_score_checker)){
					System.out.println("first:there is a mistake at raid restoring");
				}

			}
		}
		position[0]=max_x;position[1]=max_y;
		System.out.println("final result action "+max_action);
		return max_action;
	}
	/**
	 *recursively get the value of final results 
	 * @param player the current player who will do the dicision
	 * @param isMax whether the current player is a max player or a min player
	 * @param depthLeft use evaluation function at 0
	 * @return
	 */
	public int MiniMaxHelper(int player,boolean isMax,int depthLeft){
		count_func_calls++;
		if(depthLeft==0 || gboard.isOver()){
			return gboard.getCurrentScore();
		}
		int size=gboard.getSize();
		int nextplayer=-1;
		if(player == GameBoard.PLAYER_X){
			nextplayer=GameBoard.PLAYER_Y;
		}
		else{
			nextplayer=GameBoard.PLAYER_X;
		}
		int max=Integer.MIN_VALUE;int min=Integer.MAX_VALUE;
		//test all stakes
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				int toWhom=gboard.getOccupy(i, j);
				int player_x_score_checker=gboard.getPlayerXScore();
				int player_y_score_checker=gboard.getPlayerYScore();
				if(!gboard.takeAction(i, j, player, GameBoard.USE_STAKE))continue;//didn't take the action
				int val=MiniMaxHelper(nextplayer,!isMax,depthLeft-1);
				//restore the board
				if(!gboard.setOnlyOneCell(i, j, toWhom, player_x_score_checker, player_y_score_checker)){
					System.out.println("there is a mistake when we are restoring an stake");
				}
				if(isMax){
					if(val>max)max=val;
				}else{
					if(val<min)min=val;
				}
			}
		}
		//test all raids
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				int player_x_score_checker=gboard.getPlayerXScore();
				int player_y_score_checker=gboard.getPlayerYScore();
				SurroundedCell surr=gboard.getSurroundings(i, j);
				if(!gboard.takeAction(i, j, player, GameBoard.USE_RAID))continue;
				int val=MiniMaxHelper(nextplayer,!isMax,depthLeft-1);
				if(!gboard.setWithSurroundings(surr, player_x_score_checker, player_y_score_checker)){
					System.out.println("there is a mistake at raid restoring");
				}
				if(isMax){
					if(val>max)max=val;
				}else{
					if(val<min)min=val;
				}
			}
		}
		if(isMax)return max;
		return min;
	}
	public int ABHelper(int player,boolean isMax,int depthLeft,int alpha,int beta){
		count_func_calls++;
		if(depthLeft==0 || gboard.isOver()){
			return gboard.getCurrentScore();
		}
		int size=gboard.getSize();
		int nextplayer=-1;
		if(player == GameBoard.PLAYER_X){
			nextplayer=GameBoard.PLAYER_Y;
		}
		else{
			nextplayer=GameBoard.PLAYER_X;
		}
		int max=Integer.MIN_VALUE;int min=Integer.MAX_VALUE;
		//test all stakes
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				int toWhom=gboard.getOccupy(i, j);
				int player_x_score_checker=gboard.getPlayerXScore();
				int player_y_score_checker=gboard.getPlayerYScore();
				if(!gboard.takeAction(i, j, player, GameBoard.USE_STAKE))continue;//didn't take the action
				int val=ABHelper(nextplayer,!isMax,depthLeft-1,alpha,beta);
				//restore the board
				if(!gboard.setOnlyOneCell(i, j, toWhom, player_x_score_checker, player_y_score_checker)){
					System.out.println("there is a mistake when we are restoring an stake");
				}
				if(isMax){
					if(val>max)max=val;
					if(val>alpha)alpha=val;
					if(alpha>=beta)return beta;
				}else{
					if(val<min)min=val;
					if(val<beta)beta=min;
					if(beta<=alpha)return alpha;
				}
			}
		}
		//test all raids
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				int player_x_score_checker=gboard.getPlayerXScore();
				int player_y_score_checker=gboard.getPlayerYScore();
				SurroundedCell surr=gboard.getSurroundings(i, j);
				if(!gboard.takeAction(i, j, player, GameBoard.USE_RAID))continue;
				int val=ABHelper(nextplayer,!isMax,depthLeft-1,alpha,beta);
				if(!gboard.setWithSurroundings(surr, player_x_score_checker, player_y_score_checker)){
					System.out.println("there is a mistake at raid restoring");
				}
				if(isMax){
					if(val>max)max=val;
					if(val>alpha)alpha=val;
					if(alpha>=beta)return beta;
				}else{
					if(val<min)min=val;
					if(val<beta)beta=min;
					if(beta<=alpha)return alpha;
				}
			}
		}
		if(isMax)return max;
		return min;
	}
	/**
	 * basically the same as minimax, but with the help of ab pruning
	 * @param position the position which action happens
	 * @return the action(raid or stake)
	 */
	public int ABPruning(int[] position){
		count_func_calls++;
		int max=Integer.MIN_VALUE;
		int max_x=-1;int max_y=-1;
		int max_action=-1;
		int alpha=Integer.MIN_VALUE;
		int beta=Integer.MAX_VALUE;
		int size=gboard.getSize();
		//test all stakes
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				int toWhom=gboard.getOccupy(i, j);
				int player_x_score_checker=gboard.getPlayerXScore();
				int player_y_score_checker=gboard.getPlayerYScore();
				if(!gboard.takeAction(i, j, GameBoard.PLAYER_X, GameBoard.USE_STAKE))continue;//didn't take the action
				int val=ABHelper(GameBoard.PLAYER_Y,false,depth-1,alpha,beta);
				if(val>max){
					max=val;
					max_x=i;max_y=j;max_action=GameBoard.USE_STAKE;
				}
				if(val>alpha){
					alpha=val;
				}
				//restore the board
				if(!gboard.setOnlyOneCell(i, j, toWhom, player_x_score_checker, player_y_score_checker)){
					System.out.println("first:there is a mistake when we are restoring an stake");
				}
			}
		}
		//test all raids
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				int player_x_score_checker=gboard.getPlayerXScore();
				int player_y_score_checker=gboard.getPlayerYScore();
				SurroundedCell surr=gboard.getSurroundings(i, j);
				if(!gboard.takeAction(i, j, GameBoard.PLAYER_X, GameBoard.USE_RAID))continue;
				int val=ABHelper(GameBoard.PLAYER_Y,false,depth-1,alpha,beta);
				if(val>max){
					max=val;
					max_x=i;max_y=j;max_action=GameBoard.USE_RAID;
					//System.out.println("find a better raid at: "+max_x+" "+max_y);
				}
				if(val>alpha){
					alpha=val;
				}
				if(!gboard.setWithSurroundings(surr, player_x_score_checker, player_y_score_checker)){
					System.out.println("first:there is a mistake at raid restoring");
				}

			}
		}
		position[0]=max_x;position[1]=max_y;
		System.out.println("final result action "+max_action);
		return max_action;
	}

}
