package android.kgs_rastede.danieloltmanns.com;

/**
 * Created by Daniel Oltmanns on 15.02.2016.
 */
public class View2ListItem {

    private String info;
    private String color;

    public View2ListItem() {}

    public View2ListItem(String infos, String color) {
        // ' - ' wird mit einer neuen Zeile ersetzt
        infos = infos.replace(" - ", "\n");
        //Infos werden im Objeckt abgespeichert
        this.info = infos;
        this.color = color;
    }
    //Funktion um die Infos zu bekommen
    public String getInfo() { return this.info; }

    public String getColor() { return this.color; }
}
