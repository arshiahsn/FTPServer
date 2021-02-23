

/**
 * WebServer Class
 * 
 * Implements a multi-threaded web server
 * supporting non-persistent connections.
 * 
 * @author 	Majid Ghaderi
 * @version	2021
 *
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.*;


public class WebServer extends Thread {
	
	// global logger object, configures in the driver class
	private static final Logger logger = Logger.getLogger("WebServer");
	private ServerSocket ssocket;
    /**
     * Constructor to initialize the web server
     * 
     * @param port 	The server port at which the web server listens > 1024
     * 
     */
	public WebServer(int port){
		try{
			this.ssocket = new ServerSocket(port);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}

	public void recHTTPReq(){

	}

	public void sendHTTPResp(){

	}

	public void sendObject(){

	}

	public class WorkerThread extends Thread {
		private Socket csocket;
		public WorkerThread(Socket csocket_){
			this.csocket = csocket_;
		}
		public void run(){

		}

	}

	
    /**
	 * Main web server method.
	 * The web server remains in listening mode 
	 * and accepts connection requests from clients 
	 * until the shutdown method is called.
	 *
     */
	public void run(){

		while(true){
			try {
				Socket csocket = ssocket.accept();
				WorkerThread wthread = new WorkerThread(csocket);
				Thread workerThread = new Thread(wthread);
				workerThread.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


	}
	

    /**
     * Signals the web server to shutdown.
	 *
     */
	public void shutdown();
	
}
