import java.util.ArrayList;

/**
 * this class is the main frame of the game, 
 * It support all the operations and the steps for the game.
 * Here we provide a clone method for state save, also, a BoardSaver class can be used to help record steps.
 * @author uiniuhc
 */
public class GameBoard {
	public final static int CELL_VALUE=0;
	public final static int OCCUPY_STATE=1;
	public final static int PLAYER_X=0;
	public final static int PLAYER_Y=1;
	public final static int EMPTY_CELL=2;
	public final static int CELL_LENGTH=2;//one for cell value, the other for who is occupying the cell.
	public final static int USE_STAKE=1;
	public final static int USE_RAID=2;
	private int[][][] board;
	private int player_x_score;
	private int player_y_score;
	private int size;
	private int vacant;
	/**
	 * init a new board
	 * @param n
	 */
	public GameBoard(int n){
		size=n;
		vacant=size*size;
		board=new int[n][n][CELL_LENGTH];
		player_x_score=0;
		player_x_score=0;
	}
	/**
	 * this is a clone method
	 * @param gb
	 */
	public GameBoard(GameBoard gb){
		this.size=gb.size;
		this.vacant=gb.vacant;
		this.board=new int[size][size][CELL_LENGTH];
		this.player_x_score=gb.player_x_score;
		this.player_x_score=gb.player_y_score;
		for(int i=0;i<this.size;i++){
			for(int j=0;j<this.size;j++){
				this.board[i][j][CELL_VALUE]=gb.board[i][j][CELL_VALUE];
				this.board[i][j][OCCUPY_STATE]=gb.board[i][j][OCCUPY_STATE];
			}
		}
	}
	public int getSize(){
		return size;
	}
	public int  getPlayerXScore(){
		return player_x_score;
	}
	public int getPlayerYScore(){
		return player_y_score;
	}
	public void printBoardState(char player_x,char player_y, char empty_cell){
		
		for(int i=0;i<this.size;i++){
			StringBuilder line=new StringBuilder();
			for(int j=0;j<this.size;j++){
				if(board[i][j][OCCUPY_STATE]==PLAYER_X)line.append(player_x);
				else if(board[i][j][OCCUPY_STATE]==PLAYER_Y)line.append(player_y);
				else line.append(empty_cell);
			}
			System.out.println(line.toString());
		}
		System.out.println("now vacant cells: "+vacant);
	}
	public void printBoardValue(){
		for(int i=0;i<this.size;i++){
			StringBuilder line=new StringBuilder();
			for(int j=0;j<this.size;j++){
				line.append(board[i][j][CELL_VALUE]+" ");
			}
			System.out.println(line.toString());
		}
	}
	/**
	 * this method will give the board in string
	 * @return rows of string
	 */
	public ArrayList<String> getBoardData(char player_x, char player_y, char empty_cell){
		ArrayList<String> res=new ArrayList<String>();
		for(int i=0;i<this.size;i++){
			String line="";
			for(int j=0;j<this.size;j++){
				if(board[i][j][OCCUPY_STATE]==PLAYER_X)line+=player_x;
				else if(board[i][j][OCCUPY_STATE]==PLAYER_Y)line+=player_y;
				else line+=empty_cell;
			}
			res.add(line);
		}
		return res;
	}
	/**
	 * get a cell's all neighbours
	 * @param x
	 * @param y
	 * @return a surroundedcell object
	 */
	public SurroundedCell getSurroundings(int x,int y){
		assert x>=0 && x<size && y>=0 && y<size;
		return new SurroundedCell(x,y, 
				board[x][y][OCCUPY_STATE],x-1>=0?board[x-1][y][OCCUPY_STATE]:-1, 
				x+1<size?board[x+1][y][OCCUPY_STATE]:-1, y-1>=0?board[x][y-1][OCCUPY_STATE]:-1, 
				y+1<size?board[x][y+1][OCCUPY_STATE]:-1);
	}
	/**
	 * set a cell and it's surroundings to specific value,
	 * use x_score_check and y_score_check to check the score's consistency after setting 
	 * always return true
	 * can be used as a way for restoring data
	 * @param sur
	 * @param player_x_score_check
	 * @param player_y_score_check
	 * @return true for a success setting, false for not set because the error of x and y score
	 */
	public boolean setWithSurroundings(SurroundedCell sur,int player_x_score_checker,int player_y_score_checker){
		int x=sur.coord_x;int y=sur.coord_y;
		changeOneNode(x,y,sur.center);
		changeOneNode(x-1,y,sur.left);
		changeOneNode(x+1,y,sur.right);
		changeOneNode(x,y-1,sur.top);
		changeOneNode(x,y+1,sur.bottom);
		if(player_x_score!= player_x_score_checker || player_y_score!=player_y_score_checker)return false;
		return true;
	}
	/**
	 * return the state of occupation, who is occupying this cell?
	 * @param x
	 * @param y
	 * @return number which can denote the occupier of the cell
	 */
	public int getOccupy(int x, int y){
		return board[x][y][OCCUPY_STATE];
	}
	/**
	 * only set one node, and maintain the scores
	 * use two check for finally score check
	 * normally, always return true
	 * if false, there must be some mistakes in the program
	 * can be used for restore data for stake
	 * @param x
	 * @param y
	 * @param toWhom to whom this cell will be occupied
	 * @param player_x_score_check
	 * @param player_y_score_check
	 * @return
	 */
	public boolean setOnlyOneCell(int x, int y, int toWhom, int player_x_score_checker,int player_y_score_checker){
	//get current occupier 	
		changeOneNode(x,y,toWhom);
		if(player_x_score!= player_x_score_checker || player_y_score!=player_y_score_checker)return false;
		return true;
	}
	private void changeOneNode(int x, int y, int toWhom){
		if(x<0 || y<0 || x>=size || y>=size) return;
		int loss=board[x][y][CELL_VALUE];
		if(toWhom==board[x][y][OCCUPY_STATE])return;
		if(toWhom==EMPTY_CELL)vacant++;
		else if(board[x][y][OCCUPY_STATE]==EMPTY_CELL)vacant--;
		if(board[x][y][OCCUPY_STATE]==PLAYER_X){
			if(toWhom==PLAYER_Y){
				//from x to y
				player_x_score-=loss;
				player_y_score+=loss;
			}else{
				player_x_score-=loss;
			}
		}else if(board[x][y][OCCUPY_STATE]==PLAYER_Y){
			if(toWhom==PLAYER_X){
				//from y to x
				player_x_score+=loss;
				player_y_score-=loss;
			}else{
				player_y_score-=loss;
			}
		}else{
		//from empty to some one
			if(toWhom==PLAYER_X){
				player_x_score+=loss;
			}
			else{
				player_y_score+=loss;
			}
		}
		board[x][y][OCCUPY_STATE]=toWhom;
	}
	void printScore(){
		System.out.println("now score: "+this.getCurrentScore()+" x: "+this.getPlayerXScore()+" y: "+this.getPlayerYScore());
	}
	/**
	 * set a cell's value
	 * @param x
	 * @param y
	 * @param val
	 */
	public void setCellValue(int x,int y,int val){
		assert x>=0 && x<size && y>=0 && y<size;
		board[x][y][CELL_VALUE]=val;
	}
	/**
	 * set a board occupy
	 * It will not change the score!!
	 * @param x
	 * @param y
	 * @param player
	 */
	private void setOccupy(int x,int y, int player){
		assert player==PLAYER_X || player==PLAYER_Y;
		board[x][y][OCCUPY_STATE]=player;
	}
	/**
	 *init the board state with an char array 
	 * @param board a two dimensional char array for board occupy state 
	 * @param px the char for player x
	 * @param py char for player y
	 */
	public void loadBoardState(char[][] charBoard,char px, char py){
		assert charBoard.length==size && charBoard[0].length==size;
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				char ch=charBoard[i][j];
				if(ch==px)setOccupy(i,j,PLAYER_X);
				else if(ch==py) setOccupy(i,j,PLAYER_Y);
				else setOccupy(i,j,EMPTY_CELL);
			}
		}
		reCalculateScore();
	}
	/**
	 * recalculate the score, it is used in load board state.
	 * normally, when taking action, score will be maintained , so this method is no need
	 * @return
	 */
	private int reCalculateScore(){
		this.player_x_score=0;
		this.player_y_score=0;
		for(int[][] l:board){
			for(int[] cel:l){
				if(cel[OCCUPY_STATE]==PLAYER_X)this.player_x_score+=cel[CELL_VALUE];
				else if(cel[OCCUPY_STATE]==PLAYER_Y)this.player_y_score+=cel[CELL_VALUE];
				if(cel[OCCUPY_STATE]!=EMPTY_CELL)vacant--;
			}
		}
		return getCurrentScore();
	}
	/**
	 * return the score difference between player x and player y
	 */ 
	public int getCurrentScore(){
		return player_x_score-player_y_score;
	}
	/**
	 * @param x x-coord
	 * @param y y-coord
	 * @param player which player?
	 * @param action raid or stake
	 * @return true for a valid step, false for an invalid step
	 */
	public boolean takeAction(int x,int y,int player,int action){
		if(!checkValidAction(x,y,player,action))return false;
		vacant--;
		//System.out.println("before doing "+x+" "+y+" "+action);
		//System.out.println("score x " + this.getPlayerXScore());
		//System.out.println("score y " + this.getPlayerYScore());
		int oponent=PLAYER_X;
		if(player==PLAYER_X)oponent=PLAYER_Y;
		//this.printBoardState('x', 'y', '.');
		if(action==USE_STAKE){
			board[x][y][OCCUPY_STATE]=player;
			if(player==PLAYER_X)player_x_score+=board[x][y][CELL_VALUE];
			if(player==PLAYER_Y)player_y_score+=board[x][y][CELL_VALUE];
		}
		else if(action==USE_RAID){
			int center_add=0;
			int adj_add=0;
			board[x][y][OCCUPY_STATE]=player;
			center_add+=board[x][y][CELL_VALUE];
			if(x-1>=0 && board[x-1][y][OCCUPY_STATE]==oponent){
				adj_add+=board[x-1][y][CELL_VALUE];
				board[x-1][y][OCCUPY_STATE]=player;
			}
			if(x+1<size && board[x+1][y][OCCUPY_STATE]==oponent){
				adj_add+=board[x+1][y][CELL_VALUE];
				board[x+1][y][OCCUPY_STATE]=player;
			}
			if(y-1>=0 && board[x][y-1][OCCUPY_STATE]==oponent){
				adj_add+=board[x][y-1][CELL_VALUE];
				board[x][y-1][OCCUPY_STATE]=player;
			}
			if(y+1<size && board[x][y+1][OCCUPY_STATE]==oponent){
				adj_add+=board[x][y+1][CELL_VALUE];
				board[x][y+1][OCCUPY_STATE]=player;
			}
			if(player==PLAYER_X){player_x_score+=center_add+adj_add; player_y_score-=adj_add; assert player_y_score>=0;}
			if(player==PLAYER_Y){player_y_score+=center_add+adj_add; player_x_score-=adj_add; assert player_x_score>=0;}
		}
		//System.out.println("after doing "+x+" "+y+" "+action);
		//System.out.println("score x " + this.getPlayerXScore());
		//System.out.println("score y " + this.getPlayerYScore());
		//this.printBoardState('x', 'y', '.');
		return true;
	}
	/**
	 * check whether an action is valid
	 * @param x
	 * @param y
	 * @param player
	 * @param action
	 * @return true for valid, false for not valid
	 */
	private boolean checkValidAction(int x,int y,int player,int action){
		assert x<size && y<size && x>=0 && y>=0 && (player==PLAYER_X || player==PLAYER_Y) && (action==USE_STAKE ||action==USE_RAID);
		if(board[x][y][OCCUPY_STATE]!=EMPTY_CELL)return false;
		if(action==USE_STAKE){
			return true;
		}
		if(action==USE_RAID){
			//check for 4 adj
			if(x-1>=0 && board[x-1][y][OCCUPY_STATE]==player)return true;
			if(x+1<size && board[x+1][y][OCCUPY_STATE]==player)return true;
			if(y-1>=0 && board[x][y-1][OCCUPY_STATE]==player)return true;
			if(y+1<size && board[x][y+1][OCCUPY_STATE]==player)return true;
		}
		return false;
	}
	/**
	 * transform x y to a string	
	 * @param x
	 * @param y
	 * @return string for the result, use alphabetical rows and numerical columns
	 */
	public static String translatePosition(int x, int y){
		int px=y+1;
		int py=x+1;
		String res="";
		while(true){
				char t=(char) ('A'+(px-1)%26);
				res=t+res;
				px=(px-1)/26;
				if(px==0)break;
		}
		return res+py;
	}
	public boolean isOver(){
		return vacant<=0;	
	}
}
