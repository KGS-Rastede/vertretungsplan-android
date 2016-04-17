package android.kgs_rastede.danieloltmanns.com;

/**
 * Created by Daniel Oltmanns on 15.02.2016.
 */
public class View2ListItem {

    private String info;

    public View2ListItem() {}

    public View2ListItem(String infos) {
        // ' - ' wird mit einer neuen Zeile ersetzt
        infos = infos.replace(" - ", "\n");
        //Infos werden im Objeckt abgespeichert
        this.info = infos;
    }
    //Funktion um die Infos zu bekommen
    public String getInfo() { return this.info; }
}
