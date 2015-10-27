package BoomGame;

import java.io.IOException;

import javax.microedition.lcdui.Image;

import Original.GameWorld;
import Original.ImageUtility;
import java.io.InputStream;

public class BoomWorld extends GameWorld{
	public final static int CELL_WIDTH = 20 ;
	public final static int CELL_HEIGHT = 20 ; 
	
	public final static byte CELL_SOFTWALL = 1  ;
	public final static byte CELL_STRONGWALL = 2 ;
	public final static byte CELL_GRASS = 4 ;
	
	public static int xMinScrollable  ;
	public static int yMinScrollable  ;
	public static int xMaxScrollable ;
	public static int yMaxScrollable ;
	
	private final static byte explosionCell[]={26,27,28,29} ;
	private byte   currFrames[] ;
	private short  positions[] ; 
	private String string;
	private int numSoftWall ;
	public BoomWorld(int level ) throws IOException {
                super(null,
                	  0, 0,
                	  CELL_WIDTH,CELL_HEIGHT);
            //load map----------------------------------------------------------
                string = file("/map.txt");
                System.out.println(string);
                String a[]=split(string, '-');
                String dcmatran[] =split(a[0], ' ');//lay so dong ,so cot

                String b[]=split(a[level*2-1], ' ');//lay ten map
                this.setNumRows(Integer.parseInt(dcmatran[0]));
                this.setNumCols(Integer.parseInt(dcmatran[1]));
                this.setCells();
                this.setimgs(ImageUtility.extractFrames(Image.createImage("/"+b[1]+".png"), 0, 0, 5, 6, CELL_WIDTH, CELL_HEIGHT, true));
             
		byte mapwood[][] = convert_matrix( a[level*2],Integer.parseInt(dcmatran[0]) ,Integer.parseInt(dcmatran[1]));
           //-------------------------------------------------------------------
		
		byte cell ;
		for(int row = 0 ; row < Integer.parseInt(dcmatran[0]) ;row++ ){
			for(int col = 0 ; col < Integer.parseInt(dcmatran[1]); col++){
				cell =  mapwood[row][col] ;
				if( cell == CELL_SOFTWALL ){
					numSoftWall++ ;
				}
				setCell(col, row, cell ) ;
			}
		}
		
		for( int row = 0 ; row < Integer.parseInt(dcmatran[0]) ; row++ ){
			for(int col = 0 ; col < Integer.parseInt(dcmatran[1]) ; col++){
				System.out.print(" "+getCell(col, row)) ;									
		} // for
		System.out.println() ;
	} // for
		System.out.println("-----------------") ;
	}
	
	public void tick(int tickCount){
		if( tickCount % 3 == 0 )
		{
			if( positions == null ){
				return ;
			}
			
			byte currFrame ;
			for(int i = positions.length-1 ; i >= 0 ; i-- ){				
				currFrame = currFrames[i] ;
				if( currFrame != -1 ){
					setCell(positions[i],explosionCell[currFrame]) ;
					currFrame++ ;
					if( currFrame >= explosionCell.length){
						currFrame = -1 ;
						
						setCell(positions[i], CELL_GRASS) ;
					}
					
					currFrames[i] = currFrame ;
				}	
				
			} // for 
			
		}// tickcount % 3
	}
	
	public void setExplosionCell(int x,int y){
		short pos = (short)(y*getNumCols() + x) ;
		
		if( this.positions == null ){
			positions = new short[1] ;
			currFrames = new byte[1] ; 
			
			positions[0] = pos ;
		}
		else{
			int oldLength = positions.length ; 
			int newLength = oldLength + 1;			
			short newPositions[] = new short[newLength] ;
			byte newCurrFrames[] = new byte[newLength] ;
						 
			// copy the old element
			for( int i = 0 ; i < oldLength ; i++ ){
				newPositions[i] = positions[i] ;
				newCurrFrames[i] = currFrames[i] ; 
			}
		
			// save the new element. Now, oldLength is last index of the new array.
			newPositions[oldLength] = pos ;
			newCurrFrames[oldLength] = 0 ;
			positions = newPositions ;
			currFrames = newCurrFrames ;
		}

	}
	
	public boolean isExplosionCell(int x,int y){
		byte cell = getCell(x, y) ;
		if( cell >= 26 && cell <= 29){
			return true ;
		}
		
		return false ;
	}
	
	public boolean isWall(int col,int row) {
		if( col < 1 || row < 1 || col >= getNumCols()-1 || row >= getNumRows()-1 ){
			return true ;
		}else{
			int currCell = getCell(col, row) ;
			
			return currCell == CELL_SOFTWALL || 
				   currCell == CELL_STRONGWALL ;	
		}		
	}	

	public void scroll(int dx, int dy) {
		int xOldView = getXViewWindow() ;
		int yOldView = getYViewWindow() ;
		int xView = xOldView+dx ;
		int yView = yOldView+dy ;
		if( xView < xMinScrollable ){
			dx = xMinScrollable  - xOldView ;
		}else if( xView > xMaxScrollable){
			dx = xMaxScrollable  - xOldView;
		}
		
		if( yView < yMinScrollable ){
			dy = yMinScrollable - yOldView ;
		}
		else if( yView > yMaxScrollable){
			dy = yMaxScrollable - yOldView ;
		}

		super.scroll(dx, dy) ;
	
	}
	
	/* xObject, yObject is the position of Object that is view.*/
	public void setViewWindowPos(int xObject, int yObject) {		
		int halfWidthView =  getWidthViewWindow() >> 1;
		int halfHeightView = getHeightViewWindow() >> 1;
		int xView = xObject - halfWidthView , 
			yView = yObject - halfHeightView ;
		if( xView > xMaxScrollable ){
			xView = xMaxScrollable ;
		}
		else if( xView < xMinScrollable  ) 	{		
			xView = xMinScrollable ;
		}
		
		
		if( yView > yMaxScrollable ){
			yView = yMaxScrollable ;
		}
		else if( yView < yMinScrollable) {
			yView = yMinScrollable ;
		}

		super.setViewWindowPos(xView, yView);
	}
	
	public void setDrawingSize(int widthDraw, int heightDraw) {		
		super.setDrawingSize(widthDraw, heightDraw);
		
		xMinScrollable = 0;
		yMinScrollable = 0 ;
		
		xMaxScrollable = getNumCols()*CELL_WIDTH  - getWidthViewWindow() ;
		yMaxScrollable = getNumRows()*CELL_HEIGHT - getHeightViewWindow() ;
	}
        //cat chuoi theo ky tu cho truoc
        public String[] split( String in, char ch ){
            String[] result = new String[100];
            int      pos = in.indexOf( ch );
            int i=0;
            while(pos!=-1)
            {

                result[i] = in.substring( 0, pos ).trim();
                result[i+1] = in.substring( pos+1 ).trim();
                in=result[i+1];
                i++;
                pos = in.indexOf( ch );
            }
            return result;
        }
        //chuyen chuoi thanh matran byte
        public byte[][] convert_matrix( String in,int dong ,int cot){
            byte[][] result = new byte[dong][cot];
            String []mangdong=split(in, '\n');
            for(int i=0;i<dong;i++)
            {
                System.out.println(mangdong[i]+"||");
                String []mangcot=split(mangdong[i],' ');
               for(int j=0;j<cot;j++)
                    result[i][j]=(byte) Integer.parseInt(mangcot[j]);
            }

            return result;
        }

	private String file(String fn){

		InputStream is = getClass().getResourceAsStream(fn);

		StringBuffer sb = new StringBuffer();
		try{
			int chars, i = 0;

			while ((chars = is.read()) != -1){
                            sb.append((char) chars);
			}

			return sb.toString();
		}catch (Exception e){}
		return null;
	}
	
	public int getNumSoftWall() {
		return numSoftWall;
	}
}
