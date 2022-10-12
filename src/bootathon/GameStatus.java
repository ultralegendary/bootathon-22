package bootathon;
import java.util.Date;
public class GameStatus {
	private String game_id,game_name;
    private boolean has_won;
    private Date date;
    long score;
    long seconds_lasted;
    GameStatus(){
        date=new Date(2000,1,1);
        has_won=false;
        game_id="";
        game_name="";
        score=0;
        seconds_lasted=0;
    }
    GameStatus(String game_id,String game_name,boolean has_won,int dd,int yyyy,int mm,int score,int seconds_lasted){
        this.game_id=game_id;
        this.game_name=game_name;
        this.has_won=has_won;
        this.date=new Date(yyyy,mm,dd);
        this.score=score;
        this.seconds_lasted=seconds_lasted;
    }
}
