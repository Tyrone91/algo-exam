package nova;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ItemEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;

public class MainFrame extends JFrame implements Controller.StartUpListener {

    private Controller m_Controller;
    private UIImageList m_ImageList;

    public MainFrame(){
        super("ALGO: Assignment 1.0");
    }

    public void onStartUp(Controller controller){
        m_Controller = controller;
        m_ImageList = new UIImageList( controller.getImageHandler());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        setJMenuBar(initMenuBar(controller));

        setLayout( new BorderLayout());

        final JScrollPane listPane = new JScrollPane(m_ImageList);
        add(listPane, BorderLayout.EAST);
    }

    private JMenuItem buildItem(String text, Runnable action){
        final JMenuItem item = new JMenuItem(text);
        item.addActionListener( event -> action.run());
        return item;
    }

    private JMenuItem buildItem(String text, Consumer<JMenuItem> action){
        final JMenuItem item = new JMenuItem(text);
        item.addActionListener( event -> action.accept(item));
        return item;
    }

    private JMenuBar initMenuBar(Controller controller){
        final JMenuBar bar = new JMenuBar();
        bar.add( initFileMenu(controller));
        bar.add( initActionMenu(controller));
        return bar;
    }

    private JMenu initFileMenu(Controller controller){
        final JMenu menu = new JMenu("File");
        menu.add( buildItem("Load", () -> {

        }));
        menu.add( buildItem("Exit", () -> {
            
        }));
        return menu;
    }

    private JMenu initActionMenu(Controller controller){
        final JMenu menu = new JMenu("Actions");

        final List<String> todoControllerStuff = new ArrayList<>();
        menu.add( buildItem("Start shuffle", (item) -> {
            if(todoControllerStuff.size() == 1){
                item.setText("Start shuffle");
                todoControllerStuff.clear();
            }else {
                item.setText("End shuffle");
                todoControllerStuff.add("fuck java");
            }
        }));

        menu.add( buildItem("Add all", () -> {
            
        }));
        return menu;   
    }

    public AlgoImage loadImage(File imageFile) throws IOException{
        Image img = ImageIO.read( imageFile);
        return new AlgoImage(img, img.getWidth(this), img.getHeight(this));
    }

}