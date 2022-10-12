package bootathon;
import java.io.IOException;
import bootathon.Game;
public class Main {
	public static void main(String args[]) throws IOException{
		Game g=new Game();
		g.findPorts();
		g.connect();
	}
}
