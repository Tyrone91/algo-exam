package nova;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

public class MainFrame extends JFrame implements Controller.StartUpListener {

    private Controller m_Controller;
    private UIImageList m_ImageList;
    private UIAlgoImage m_CenterImage;
    private QuickNavigationBar m_NavBar;

    public MainFrame(Controller controller){
        super("ALGO: Assignment 1.0");

        m_NavBar = new QuickNavigationBar();

        m_NavBar
            .addNavEntry("Draw", "─", () -> {})
            .addNavEntry("Draw", "●", () -> {})

            .addNavEntry("Morph", "🗘", () -> {})
            .addNavEntry("Morph", "🡘 🡙", () -> {})
            .addNavEntry("Morph", "✂", () -> {})
            .addNavEntry("Morph", "⇱", () -> {})

            .update();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(720, 480);
        controller.addStartUpListener(this);
        addWindowListener( new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent arg0) {
                m_Controller.closeApp();
            }
        });
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
        add(m_NavBar,BorderLayout.NORTH);
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
            validate();
        }));
        menu.add( UtilsUI.createItem("Exit", () -> {
            m_Controller.closeApp();
        }));
        return menu;
    }

    private JMenu initActionMenu(Controller controller){
        final JMenu menu = new JMenu("Actions");
        
        final JMenuItem shuffleToggle = UtilsUI.createItem("Start shuffle", (item) -> {
            m_Controller.toggleShuffle();
            if(m_Controller.getShuffleManager().isRunning()){
                item.setText("End shuffle");
            }else{
                item.setText("Start shuffle");
            }
        });
        shuffleToggle.setEnabled(false);
        menu.add(shuffleToggle);
        controller.getShuffleManager().addToggleListener( (img, added) -> {
            shuffleToggle.setEnabled(controller.getShuffleManager().isReady());
        });
        
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

    public void openImageFileChooser(){
        JFileChooser jfc = new JFileChooser();
        jfc.setCurrentDirectory( new File (System.getProperty("user.dir")));
        jfc.setDialogType(JFileChooser.OPEN_DIALOG);
        jfc.setMultiSelectionEnabled(true);
        jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        jfc.setFileFilter( new FileFilter(){
        
            @Override
            public String getDescription() {
                return Utils.ALLOWED_FILES.stream().collect(Collectors.joining(","))+ ", directory";
            }
        
            @Override
            public boolean accept(File file) {
                return Utils.IMAGE_AND_DIRECTORY_FILTER.accept(file);
            }
        });
        jfc.showOpenDialog(this);
        System.out.println(jfc.getSelectedFiles().length);
        
        File[] files = jfc.getSelectedFiles();
        if(files == null){
            return;
        }
        m_Controller.loadImages(Arrays.asList(files));
    }
    
    public void setCenterImage(AlgoImage image){
       m_CenterImage.setSource(image);
       validate();
       m_CenterImage.repaint();
    }
        
        

}