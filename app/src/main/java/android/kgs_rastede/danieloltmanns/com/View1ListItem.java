package android.kgs_rastede.danieloltmanns.com;

/**
 * Created by Daniel Oltmanns on 14.02.2016.
 */
public class View1ListItem {

    private String date;
    private String hour;
    private String subject;
    private String status;
    private String room;
    private String teacher;
    private String supply;
    private String postponement;
    private String notice;

    public View1ListItem() {}

    public View1ListItem(String date,String hour,String subject,String status,String room,String teacher,String supply,String postponement,String notice) {
        this.date = date;
        this.hour = hour;
        this.subject = subject;
        this.status = status;
        this.room = room;
        this.teacher = teacher;
        this.supply = supply;
        this.postponement = postponement;
        this.notice = notice;
    }

    public String getDate() { return this.date; }
    public String getHour() { return this.hour; }
    public String getSubject() { return this.subject; }
    public String getStatus() { return this.status; }
    public String getRoom() { return this.room; }
    public String getTeacher() { return this.teacher; }
    public String getSupply() { return this.supply; }
    public String getPostponement() { return this.postponement; }
    public String getNotice() { return this.notice; }

}
