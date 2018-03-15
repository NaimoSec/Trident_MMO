
/**
 * This client is so mem-expensive it needs this..
 * @author Gabbe
 */
public class GarbageCollector implements Runnable {
	
	private Thread t;
	public GarbageCollector() {
		t = new Thread(this, "Memory monitor");
		t.start();
	}

	@Override
	public void run() {
		while(true) {
			System.out.println("Ran garbage collector");
			System.gc();
			try {
				Thread.sleep(20000);
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
