package ClientServer;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Client extends JFrame{

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	
/////////////////////////////CONSTRUCTOR///////////////////////////////
///////////////////////////////////////////////////////////////////////
	
	public Client(String host){
		super("Client messanger");
		serverIP = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand());
						userText.setText("");
					}
				}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300, 150);
		setVisible(true);
	}
	
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////

	
	
//////////////////////////CONNECT TO SERVER////////////////////////////
///////////////////////////////////////////////////////////////////////

	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofException){
			showMessages("\n Client closed the connection");
		}catch(IOException ioException){
			ioException.printStackTrace(); //print error
		}finally{
			closeCrap(); //close method
		}
	}
	
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////

	
	
/////////////////////////////CONNECT TO SERVER/////////////////////////
///////////////////////////////////////////////////////////////////////	

	private void connectToServer() throws IOException{
		showMessages("Attempting connection... \n");
		connection = new Socket(InetAddress.getByName(serverIP), 6789); //ip and port number
		showMessages("Connected to: " + connection.getInetAddress().getHostName());
	}
	
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
	

/////////////SET UP STREAMS TO SEND AND RECIEVE MESSAGGES//////////////
///////////////////////////////////////////////////////////////////////
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessages("\n Streams are ready! \n");
	}

///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
	
	
///////////////////////WHILE CHATTING WITH SERVER//////////////////////
///////////////////////////////////////////////////////////////////////
	
	private void whileChatting() throws IOException{
		ableToType(true);
		do{
			try{
				message = (String) input.readObject(); //wait for user to type message
				showMessages("\n" + message); //shows the message
			}catch(ClassNotFoundException classNotFoundException){
				showMessages("\n Object type not recognized! \n");
			}
		}while(!message.equals("SERVER - END"));
	}
	
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////


//////////////////////////CLOSE THE CLIENT/////////////////////////////
///////////////////////////////////////////////////////////////////////
	
	private void closeCrap(){
		showMessages("\n Closing...");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////


/////////////////////SEND MESSAGES TO SERVER///////////////////////////
///////////////////////////////////////////////////////////////////////

	private void sendMessage(String message){
		try{
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessages("\nClient - " + message); //numele clientului
		}catch(IOException ioException){
			chatWindow.append("\n Something went wrong while sending the message!");
		}
	}
	
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
	
	
/////////////////////UPTADE THE GUI TO DISPLAY MESSAGE/////////////////
///////////////////////////////////////////////////////////////////////
	
	private void showMessages(final String mesaje){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(mesaje);
					}
				}
		);
	}

///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
	
	
////////////////GIVES USER PERMISION TO TYPE IN THE TEXT BOX///////////
///////////////////////////////////////////////////////////////////////
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
			new Runnable(){
				public void run(){
					userText.setEditable(tof);
				}
			}
		);
	}
	
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
}


















