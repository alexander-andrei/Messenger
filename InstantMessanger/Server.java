package InstantMessanger;

import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Server extends JFrame{

	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection; //creates soket
	
/////////////////////////SERVER CONSTRUCTOR//////////////////////////
////////////////////////////////////////////////////////////////////
	
	public Server(){
		super("Nova Instant Messenger");
		userText = new JTextField();
		userText.setEditable(false); //if you press ENTER without typing anithing, it wont work
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent event){
						sendMessage(event.getActionCommand()); //takes the string
						userText.setText(""); //prints the string
					}
				}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow)); //create panel
		setSize(300, 150);
		setVisible(true);
		
////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////
	
	}
	
//////////////////////////SET UP AND RUN SERVER//////////////////////////
////////////////////////////////////////////////////////////////////////
	
	public void startRunning(){
		try{
			server = new ServerSocket(6789, 100);//parameters(port number, how manny people can wait in line)
			while(true){
				try{
					//connect and have conversation
					waitForConnection();
					setupStreams(); //set up the connection
					whileChatting();
				}catch(EOFException eofException){
					showMessage("\n Server ended the connection!");
				}finally{
					closeCrap(); //closing method
				}
			}
		}catch(IOException ioException){
			ioException.printStackTrace();//error
		}
		
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////	
	
	}
	
//////////WAIT FOR CONNECTION, THEN DISPLAY CONNECTION INFO//////////////
/////////////////////////////////////////////////////////////////////////
	
	private void waitForConnection() throws IOException{
		showMessage("Waiting for someone to connect... \n");
		connection = server.accept(); //waits for a connection
		showMessage("Now connected to "+connection.getInetAddress().getHostName());
		
	}
	
/////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////

	
	
/////////////////GET STREAM TO SEND AND RECEIVE DATA////////////////////
////////////////////////////////////////////////////////////////////////
	
	private void setupStreams() throws IOException{
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush(); 
		input = new ObjectInputStream(connection.getInputStream());
		showMessage("\n Streams are now set up \n");
	}
	
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////

	
	
/////////////////////DURING THE CHAT CONVERSATION///////////////////////
////////////////////////////////////////////////////////////////////////
	
	private void whileChatting() throws IOException{
		String message = " You are now connected! ";
		sendMessage(message);
		ableToType(true);
		do{
			//have a conversation
			try{
				message = (String) input.readObject(); //takes what other people write and transforms it into a string
				showMessage("\n" + message);
			}catch(ClassNotFoundException classNotFoundException){
				showMessage("\n Cannot understand user object \n");
			}
		}while(!message.equals("CLIENT - END"));
	}
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
	
	
	
/////////////CLOSE STREAMS AND SOKETS AFTHER DONE CHATTING//////////////
////////////////////////////////////////////////////////////////////////
	
	private void closeCrap(){
		showMessage("\n Closing connections... \n");
		ableToType(false); 
		try{
			output.close(); //close outputul
			input.close(); //close inputul
			connection.close(); //close serverul
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////


///////////////////////SEND A MESSAGE TO CLIENT/////////////////////////
////////////////////////////////////////////////////////////////////////
	
	private void sendMessage(String message){
		try{
			output.writeObject("SERVER - " + message); //display message in window
			output.flush(); //delete remaining bits
			showMessage("\nSERVER - " + message);
		}catch(IOException ioException){
			chatWindow.append("\n ERROR: CANNOT SEND THAT MESSAGE");
		}
	}
	
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
	
	
//////////////////////////UPDATE CHAT WINDOW////////////////////////////
////////////////////////////////////////////////////////////////////////
	
	private void showMessage(final String text){
		SwingUtilities.invokeLater( //updates GUI without closing and opening it
			new Runnable(){
				public void run(){
					chatWindow.append(text); //updateaza GUI text
				}
			}
		);
	}
	
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////


//////////////////LET USER TYPE MESSAGES IN THE BOX/////////////////////
////////////////////////////////////////////////////////////////////////
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						userText.setEditable(tof);
					}
				}
		);
	}
	
////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////
}


















