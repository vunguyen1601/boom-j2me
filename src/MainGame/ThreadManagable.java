package MainGame;


public class ThreadManagable implements Runnable{
	
	private String threadName ;
	
	private long    waitTime ;
	private boolean shouldStop ;
	private boolean shouldPause ;
	
	public ThreadManagable(String name,long waitTime){
		threadName = name ;
		this.waitTime= waitTime ;
	}
	
	public void run() {
		while ( true ){
			long ft = System.currentTimeMillis() ;
			while( shouldPause ){
				synchronized (this) {
					try {
						wait() ;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			if( shouldStop ){
				break ;
			}
			
			action() ;
			
			long et = System.currentTimeMillis() - ft ;
			if( et < waitTime) {
				try {
					Thread.sleep(waitTime - et) ;
				} catch (InterruptedException e) {			
					e.printStackTrace();
				}
			}
			else{
				Thread.yield() ;
			}
		}
		
	}
	
	/**
	 * Override for the Thread do the some thing. 
	 * */
	public void action(){
				
	}
	
	public synchronized void start(){
		run() ;
	}
	
	public synchronized void pause(){
		shouldPause = true ;
	}
	
	public synchronized void resume(){
		shouldPause = false ;
		notify() ;
	}
	
	public synchronized void stop(){
		shouldStop = true ;
		//notify() ;
	}
	
	public synchronized String getThreadName() {
		return threadName;
	}	
}
