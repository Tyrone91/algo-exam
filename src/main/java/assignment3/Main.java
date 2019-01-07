package assignment3;

public class Main {

    public static final String MY_LINUX_PATH = "/home/tyrone/dev/hs/algo-exam/uDrawGraph-3.1/bin/";
    public static final String MY_WINDOWS_PATH = "c:/dev/tools/uDraw(Graph)/bin/";

    public static void main(String[] args) {
        PatriciaTree.test();
        RedBlackTree.test();
        /*
        UDrawConnector u = new UDrawConnector(MY_LINUX_PATH + "uDrawGraph");
        final String graph = u.newGraph(
                u.newNode("1"),
                u.newNode("2"),
                u.newNode("3")
                );

        u.send(graph); 
       */
    }
}

