package BoomGame;

public class Print {
	private final static StringBuffer str = new StringBuffer() ;
	public final static void printTheBoard(int numCols,int numRows,byte [] cells ){		
		for( int row = 0 ; row < numRows ; row++ ){
			for(int col = 0 ; col < numCols ; col++){
				str.append(" "); 
				
				System.out.print(str.append(cells[row*numCols+col] )) ;									
		} // for
		System.out.println() ;
	} // for
	}
}
