package tiles;

public enum Layer3  implements Layer{
    P0("empty",0),
    P1("L3/P1.png",1),
    P2("L3/P2.png",2),
    P3("L3/P3.png",3),
    P4("L3/P4.png",4),
    P5("L3/P5.png",5),
    P6("L3/P6.png",6);

    final String path;
    final int index;

    Layer3(String path, int index){
        this.path = path;
        this.index = index;
    }
    public String getPath() {
        return path;
    }
    public int getIndex() {
        return index;
    }
}
