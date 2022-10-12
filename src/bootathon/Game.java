package bootathon;
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import bootathon.Player;
enum game_states{
	not_connected,
	connected,
	requested_to_play,
	playing,
	game_over;
}
public class Game extends JFrame implements ActionListener{
	private ServerSocket serversock;
    private Socket sock;
    private int serverPort,clientPort;
    private boolean isServerHosted,isClientConnected;
    private PrintStream out;
    private String ip,id;
    private game_states state;
    boolean myturn=false;
    private Player p;
    private int xo_grid[];
    
    public Game() {
    	super();
    	p=new Player("dhi","#");
    	ip="127.0.0.1";
    	sock=null;
    	serversock=null;
    	isServerHosted=isClientConnected=false;
    	serverPort=8022;
    	clientPort=8021;
    	state=game_states.not_connected;
    	text_position=10;
    	render_ui();
    	xo_grid=new int[9];
    	
    }
    public void setIp(String ip){
        this.ip=ip;
    }
    public void display_games(){
        System.out.println("\n #1 xo \n #2 temp\n---- ");
    }
    public void findPorts() throws IOException{
        try{
        sock = new Socket(ip, clientPort);
        }catch(IOException io){
            System.out.println("Port "+clientPort+" is not available");
        }
        if (sock == null) {
            clientPort=8022;
            serverPort=8021;
        }
        else{
            clientPort=8021;
            serverPort=8022;
        }
    }
    public void connect(BufferedReader br_in) throws IOException{
        state=game_states.connected;
        startServer();
        startClient();
        
    }
    private Frame frame;
    private Label label;
    private int text_position;
    private Button submitbutton,exitbutton;
    private TextField text1;
    private TextArea received_text;
    private Label[] display_grid;
    public void render_ui() {
    	frame = new Frame();  
    	display_grid=new Label[9];
    	for(int i=0;i<3;i++) {
    		for(int j=0;j<3;j++) {
    			display_grid[i*3+j]=new Label(""+(i*3+j));
    			display_grid[i*3+j].setFont(new Font("Serif",Font.BOLD,30));
    			display_grid[i*3+j].setBounds(400+i*30,100+j*30,30,30);
    			frame.add(display_grid[i*3+j]);
    		}
    	}
        // creating a Label  
        label = new Label("Type your message here:");
        received_text=new TextArea("");
        received_text.setBounds(50,330,200,250);
        
        // creating a Button  
        submitbutton = new Button("Send Message");  
        submitbutton.addActionListener(this);
        exitbutton=new Button("Exit");
        exitbutton.addActionListener(this);
        
    
        // creating a TextField  
        text1 = new TextField();  
        TextField t1 = new TextField("Welcome to Javatpoint.");    
        t1.setBounds(50, 100, 200, 30);
//        frame.add(t1);
        // setting position of above components in the frame  
        label.setBounds(20, 50, 150, 20);  
        text1.setBounds(20, 80, 100, 100);  
        submitbutton.setBounds(20, 200, 100, 30);  
        exitbutton.setBounds(20,250,100,30);
    
        // adding components into frame    
        frame.add(submitbutton);  
        frame.add(label);  
        frame.add(text1);  
        frame.add(received_text);
        frame.add(exitbutton);
        // frame size 300 width and 300 height    
        frame.setSize(600,600);  
    
        // setting the title of frame  
        frame.setTitle("Employee info");   
            
        // no layout  
        frame.setLayout(null);   
    
        // setting visibility of frame  
        frame.setVisible(true);  
        
    }
    
    public void connect()throws IOException{
    	state=game_states.connected;
    	startServer();
    	startClient();
    }
    
    private void startServer() throws IOException {
    	final Thread st=new Thread(() -> {
            try {
                //start the server...
                serversock = new ServerSocket(serverPort);
                isServerHosted=true;
                System.out.println("Listening for connections..");
                //and (blockingly) listen for connections 
                final Socket sock = serversock.accept();
                //got one!
                final BufferedReader input = new BufferedReader(new InputStreamReader(
                    sock.getInputStream()
                ));
                while (true) {// as long as connected, bad exception otherwise
                    String message1 = input.readLine();// read remote message
                     System.out.println("state: "+state+" myturn:"+myturn);
                    if(state==game_states.connected){
                        boolean is_requested_to_play=false;
                        int i;
                        for(i=0;i<message1.length()-1;i++){
                            if(message1.charAt(i)=='>'&&message1.charAt(i+1)=='#'){
                                is_requested_to_play=true;break;
                            }
                        }
                        if(is_requested_to_play){
                            myturn=true;
                            state=game_states.requested_to_play;
                            received_text.setText(received_text.getText()+"\n\t"+message1.substring(0,i)+" has requested you to play a game\n\tpress # to accept");
                            System.out.println("\t-----"+message1.substring(i)+" has been requested you to play a game\n\t press # to accept");
                        }   
                        else{
                        	received_text.setText(received_text.getText()+"\n"+message1);
//                        	input_text.setBounds(400,text_position,600,450);
//                        	frame.add(input_text);
                            System.out.println(message1);// print to (local) console
                        }      
                    }
                    else if(state==game_states.requested_to_play){
                        if(message1.equals(new String("#accept#"))){
                            state=game_states.playing;
                            myturn=false;
                            received_text.setText(received_text.getText()+"\n player accepted your request\n opponent turn");
                            System.out.println("\n-- entering play state");
                        }

                    }
                    else if(state==game_states.playing){
                    	received_text.setText(received_text.getText()+"\n opponent turn : "+message1);
                    	xo_grid[Integer.parseInt(message1)]=2;
                    	display_grid[Integer.parseInt(message1)].setText("O");
                    	if(checkGameOver()!=0) {
                    		state=game_states.game_over;
                    	}
                        System.out.println("opponent turn: "+message1);
                        myturn=true;
                    }
                    else{

                        System.out.println(message1);// print to (local) console
                    }      
                    if(state==game_states.game_over) {
                    	state=game_states.connected;
                    	int g=checkGameOver();
            			if(g==1){
            				received_text.setText("You have won the game! ");
            			}
            			else if(g==2) {
            				received_text.setText("Opponent has won the game! ");
            			}
            			try {//..wait 10 secs
                            Thread.sleep(10000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace(System.err);
                        }
            			//write to database;
            			
            			for(int i=0;i<9;i++) {
            				xo_grid[i]=0;
            				display_grid[i].setText(""+i);
            			}
                    }
                }
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
                ioe.printStackTrace(System.err);
            }
        });
        st.start();
    }
    private void startClient() {
    	id=p.name+clientPort;
        final Thread ct = new Thread(() -> {
            while (sock == null) {
                try {//..to connect with a server
                    sock = new Socket(ip, clientPort);
                    
                } catch (IOException io) {
                    System.out.println("Socket Failed.. retry in 10 sec");
                    try {//..retry in 10 secs
                        Thread.sleep(10000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace(System.err);
                    }
                }
            }
            isClientConnected=true;
            System.out.println("connection successfull...");
            //sock != null -> connection established
            try {
            out = new PrintStream(sock.getOutputStream());
            }
            catch(IOException io){
            	System.out.println("Socket failed");
            }
        });
        ct.start();

    	    	
    }
    private int checkGameOver() {
    	boolean isvalid=false;
    	for(int i=0;i<3;i++) {
    		if(xo_grid[i*3]==xo_grid[i*3+1]&&xo_grid[i*3+1]==xo_grid[i*3+2]) {
    			return xo_grid[i*3];
    		}
    		if(xo_grid[0+i]==xo_grid[3+i]&&xo_grid[i+3]==xo_grid[i+6]) {
    			return xo_grid[0+i];
    		}
    	}
    	if(xo_grid[0]==xo_grid[4]&&xo_grid[4]==xo_grid[8])
    		return xo_grid[0];
    	if(xo_grid[2]==xo_grid[4]&&xo_grid[4]==xo_grid[6])
    		return xo_grid[2];
    	return 0;
    }
    public void actionPerformed(ActionEvent e) {
    	if(e.getSource()==submitbutton) {
    		System.out.println(text1.getText());
    		final String message1=text1.getText();
    		
    		text1.setText("");
    		if(state==game_states.connected){
    			out.println(id+">"+message1);
    			received_text.setText(received_text.getText()+"\nyou> "+message1);
        		
                if(message1.charAt(0)=='#'&&message1.length()>1){
                    state=game_states.requested_to_play;
                    myturn=false;
                }
                else {
                    // myturn=false;
                }
            }
            else if(state==game_states.requested_to_play){
            	
                if(!myturn){
                    
                    System.out.println(" \tWaiting for accept");
                    if(state==game_states.playing){
                        System.out.println("Enterinhg playing state");
                    }
                    else if(message1.length()==1&&message1.charAt(0)=='#'){
                        out.println("#accept#");
                        received_text.setText(received_text.getText()+"\nyou> "+message1);
                		
                        myturn=true;
                        state=game_states.playing;
                        received_text.setText(received_text.getText()+"\n your turn: ");
                        System.out.println("\n-- entering play state\n your turn:");
                    }
                }
            }
            else if(state==game_states.playing)
            {
                if(myturn==true){
                	try {
                		int t=Integer.parseInt(message1);
                		if(t<0||t>8||xo_grid[t]!=0) {
                			throw new Exception("Invalid input");
                		}
                		xo_grid[t]=1;
                		display_grid[t].setText("X");
                		if(checkGameOver()!=0) {
                    		state=game_states.game_over;
                    	}
                		out.println(message1);
                        received_text.setText(received_text.getText()+"\nyou> "+message1);
                		
                        myturn=false;
                        System.out.println("-- waiting for opponent --");
                	}
                	catch(Exception ee){
            			received_text.setText(received_text.getText()+" Invalid input ");
                	}
                    
                }
                
                
            }
    		
    		if(state==game_states.game_over) {
    			int g=checkGameOver();
    			if(g==1){
    				received_text.setText("You have won the game! ");
    			}
    			else if(g==2) {
    				received_text.setText("Opponent has won the game! ");
    			}
    			try {//..wait 10 secs
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace(System.err);
                }
    			//write to database;
    			
    			for(int i=0;i<9;i++) {
    				xo_grid[i]=0;
    				display_grid[i].setText(""+i);
    			}
            	state=game_states.connected;
            }

    		System.out.println("State: "+state+" myturn: "+myturn);
    	}
    	else if(e.getSource()==exitbutton) {
    		System.exit(0);
    	}
    	
    }
}
