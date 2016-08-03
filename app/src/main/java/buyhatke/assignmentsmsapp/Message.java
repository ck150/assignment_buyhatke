package buyhatke.assignmentsmsapp;

/**
 * Created by Chandrakant on 02-08-2016.
 */
public class Message {
    public String body;
    public String address;
    public String person;
    public String date;
    public String time;
    public String type;
    public int count;
    public boolean received;

    Message(String b,String a,String d,String t){
        this.body = b;
        this.address = a;
        this.time = t;
        this.date = d;
        this.received = true;
        this.count = 1;
    }

    public void incrementCount(){
        this.count++;
    }

    public String getNum(){
        return this.address;
    }
    public String getBody(){
        return this.body;
    }
    public String getTime(){
        return this.time;
    }
    public String getDate(){
        return this.date;
    }
    public int getCount(){
        return this.count;
    }
}
