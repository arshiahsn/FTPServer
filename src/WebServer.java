

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



	public class WorkerThread extends Thread {
		private Socket csocket;
		DataInputStream input;
		DataOutputStream output;
		public WorkerThread(Socket csocket_){
			this.csocket = csocket_;
			try{
				input  = new DataInputStream(this.csocket.getInputStream());
				output = new DataOutputStream(this.csocket.getOutputStream());
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
					csocket.getInputStream().read(http_request_header_bytes, off, 1);
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

		public void sendHTTPResp(String request){
			String lines[] = request.split("\n");
			System.out.println(lines[0]);
			String filename = lines[0].split(" ")[1];
			String source = Paths.get("").toAbsolutePath().toString();
			File file = new File(source + filename);
			String response = null;
			String CRLF = " \r\n";

			try {

				FileInputStream file_input_stream = new FileInputStream(file);
				System.out.println("\n----Start of Response Frame----");
				response = "HTTP/1.1 200" + CRLF;
				response = response + "Server: Multi-Threaded Web Server/1.0" + CRLF;
				response = response + "Content-Type: text/html" + CRLF;
				response = response + "Connection: keep-alive" + CRLF;
				response = response + "Content-Length: " + file.length() + CRLF;
				response = response + CRLF;
				System.out.print(response);
				System.out.println("----End of Response Frame----");

				int stream_char;
				try { //Print contents of file to console
					while ((stream_char = file_input_stream.read()) != -1){
						response = response + (char)stream_char;
					}
					System.out.println("----Contents of the requested file----");
					System.out.print(response);
					System.out.println("\n----Contents of the requested file----");
				} catch (IOException e) {
					System.out.println(e);
				}
			} catch(FileNotFoundException e) {
				response = response.replace("200", "404"); //File not found
			}
			finally {
				try {
					byte [] httpResponse = response.getBytes("US-ASCII");
					this.output.writeBytes(String.valueOf(httpResponse));
					this.output.flush();
				}
				catch(Exception e) {
					System.out.println(e.getMessage());
				}



		}


		}

		public void sendObject(){

		}



		public void run(){
			try {

				String request = getHTTPReq();
				sendHTTPResp(request);
				sendObject();

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
