package tiles;

public enum Layer2  implements Layer{
    P0("empty",0),
    P1("L2/P1.png",1),
    P2("L2/P2.png",2),
    P3("L2/P3.png",3),
    P4("L2/P4.png",4),
    P5("L2/P5.png",5),
    P6("L2/P6.png",6);

    final String path;
    final int index;

    Layer2(String path, int index){
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
