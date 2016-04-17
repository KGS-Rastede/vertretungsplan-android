package android.kgs_rastede.danieloltmanns.com;

/**
 * Created by Daniel Oltmanns on 15.02.2016.
 */
public class View2ListItem {

    private String info;

    public View2ListItem() {}

    public View2ListItem(String infos) {
        infos = infos.replace(" - ", "\n");
        this.info = infos;
    }

    public String getInfo() { return this.info; }
}
