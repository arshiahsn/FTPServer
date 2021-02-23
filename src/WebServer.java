

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

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.*;


public class WebServer extends Thread {
	
	// global logger object, configures in the driver class
	private static final Logger logger = Logger.getLogger("WebServer");
	private ServerSocket ssocket;
	private boolean shutdownFlag;
	private Scanner userInput;
	private ExecutorService exServ;
    /**
     * Constructor to initialize the web server
     * 
     * @param port 	The server port at which the web server listens > 1024
     * 
     */
	public WebServer(int port){
		shutdownFlag = false;
		try{
			this.ssocket = new ServerSocket(port);
			this.ssocket.setSoTimeout(1*1000);
			this.userInput = new Scanner(System.in);
			this.exServ = Executors.newFixedThreadPool(10);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}



	public class WorkerThread extends Thread {
		private Socket csocket;
		private DataInputStream input;
		private DataOutputStream output;
		public WorkerThread(Socket csocket_){
			this.csocket = csocket_;
			try{
				this.input  = new DataInputStream(this.csocket.getInputStream());
				this.output = new DataOutputStream(this.csocket.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public String getHTTPReq(){

			byte[] http_request_header_bytes = new byte[2048];
			byte[] http_object_bytes = new byte[1024];
			int num_byte_read = 0;
			int off = 0;
			/* read whole http header */
			try{
				while(num_byte_read!= -1)
				{
					this.input.read(http_request_header_bytes, off, 1);
					off++;
					String http_request_header_string = new String(http_request_header_bytes, 0, off, "US-ASCII");
					if(http_request_header_string.contains("\r\n\r\n")){
						return http_request_header_string;
					}

				}
			}

			catch (IOException e){
				//Exception handling – file download error
			}
			return null;

		}

		public void sendHTTPResp(String request) throws IOException {
			String lines[] = request.split("\n");
			System.out.println(lines[0]);
			String filename = lines[0].split(" ")[1];
			String source = Paths.get("").toAbsolutePath().toString();
			File file = new File(source + filename);
			String response = null;
			String CRLF = " \r\n";

			try {

				FileInputStream fileInputStream = new FileInputStream(file);
				System.out.println("\n----Start of Response Frame----");
				response = "HTTP/1.1 200" + CRLF;
				response = response + "Server: Multi-Threaded Web Server/1.0" + CRLF;
				response = response + "Content-Type: application/x-binary" + CRLF;
				response = response + "Connection: close" + CRLF;
				response = response + "Content-Length: " + file.length() + CRLF;
				response = response + CRLF;
				System.out.print(response);
				System.out.println("----End of Response Frame----");

				byte [] httpResponse = response.getBytes("US-ASCII");
				this.output.writeBytes(response);
				this.output.flush();

				int bytes = 0;
				int bufferSize = 2048;
				try { //Print contents of file to console
					byte[] buffer = new byte[bufferSize];
					while (((bytes=fileInputStream.read(buffer))!=-1)){
						output.write(buffer,0, bytes);
						output.flush();

					}
					this.output.writeBytes(CRLF);
					this.output.flush();
				} catch (IOException e) {
					System.out.println(e);
				}
			} catch(FileNotFoundException | UnsupportedEncodingException e) {
				response = response.replace("200", "404"); //File not found
				//byte [] httpResponse = response.getBytes("US-ASCII");
				this.output.writeBytes(response);
				this.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}


		}


		public void run(){
			try {

				String request = getHTTPReq();
				sendHTTPResp(request);
				input.close();
				output.close();

			} catch (IOException e) {
				e.printStackTrace();
			}

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

		while(!shutdownFlag){
			try {
				Socket csocket = ssocket.accept();
				if(csocket != null){
					WorkerThread wthread = new WorkerThread(csocket);
					Thread workerThread = new Thread(wthread);
					exServ.execute(workerThread);

				}


				String s = userInput.nextLine();
				System.out.println(s);

				// exit if message from client is "bye"
				if (s.equalsIgnoreCase("shutdow") || s.equalsIgnoreCase("exit") ) {
					shutdownFlag = true;
					break;
				}


			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if(shutdownFlag)
			shutdown();

	}
	

    /**
     * Signals the web server to shutdown.
	 *
     */
	public void shutdown(){
		try{
			ssocket.close();
			exServ.shutdown();
			System.exit(1);
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}

	}
	
}
