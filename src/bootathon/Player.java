package bootathon;
import java.util.*;
import bootathon.GameStatus;
public class Player {
    protected String name;
    public String player_id;
    Vector<GameStatus> history;
    public Player(){
        name=player_id="";
        history=new Vector<GameStatus>();
    }
    
    public Player(String name,String player_id){
        this.name=name;
        this.player_id=player_id;
        history=null;//get_status(player_id)// retrive data from table status
    }
}

