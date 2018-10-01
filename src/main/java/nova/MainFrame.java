package nova;

import java.awt.BorderLayout;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;

public class MainFrame extends JFrame implements Controller.StartUpListener {

    private Controller m_Controller;
    private UIImageList m_ImageList;
    private UIAlgoImage m_CenterImage;

    public MainFrame(Controller controller){
        super("ALGO: Assignment 1.0");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        controller.addStartUpListener(this);
    }

    @Override
    public void onStartUp(Controller controller){
        m_Controller = controller;
        m_CenterImage = new UIAlgoImage();
        m_ImageList = new UIImageList( controller, controller.getImageHandler());
        setJMenuBar(initMenuBar(controller));
        
        setLayout( new BorderLayout());
        final JScrollPane listPane = new JScrollPane(m_ImageList);
        add(listPane, BorderLayout.EAST);
        add(m_CenterImage, BorderLayout.CENTER);
    }

    private JMenuBar initMenuBar(Controller controller){
        final JMenuBar bar = new JMenuBar();
        bar.add( initFileMenu(controller));
        bar.add( initActionMenu(controller));
        return bar;
    }

    private JMenu initFileMenu(Controller controller){
        final JMenu menu = new JMenu("File");
        menu.add( UtilsUI.createItem("Load", () -> {
            controller.loadImagesCommand();
        }));
        menu.add( UtilsUI.createItem("Exit", () -> {
            
        }));
        return menu;
    }

    private JMenu initActionMenu(Controller controller){
        final JMenu menu = new JMenu("Actions");

        final List<String> todoControllerStuff = new ArrayList<>();
        menu.add( UtilsUI.createItem("Start shuffle", (item) -> {
            if(todoControllerStuff.size() == 1){
                item.setText("Start shuffle");
                todoControllerStuff.clear();
            }else {
                item.setText("End shuffle");
                todoControllerStuff.add("fuck java");
            }
        }));

        menu.add( UtilsUI.createItem("Add all", () -> {
            
        }));
        return menu;   
    }

    public AlgoImage loadImage(File imageFile) {
        try{
            Image img = ImageIO.read( imageFile);
            return new AlgoImage(img, img.getWidth(this), img.getHeight(this));
        }catch(IOException e){
            m_Controller.error(e);
        }
        return null;
    }
    
    public void setCenterImage(AlgoImage image){
       m_CenterImage.setSource(image);
       validate();
       m_CenterImage.repaint();
    }
        
        

}