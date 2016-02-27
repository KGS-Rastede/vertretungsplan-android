package android.kgs_rastede.danieloltmanns.com;

/**
 * Created by Daniel Oltmanns on 15.02.2016.
 */
public class View2ListItem {

    private String lesson;
    private String room;

    public View2ListItem() {}

    public View2ListItem(String infos) {
        String[] info = infos.split(" - ");
        this.lesson = info[0];
        this.room = info[1];
    }

    public String getLesson() { return this.lesson; }
    public String getRoom() { return this.room; }
}
