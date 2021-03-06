

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.filechooser.FileFilter;

public class MainFrame extends JFrame implements Controller.StartUpListener {
    
    

    private Controller m_Controller;
    private UIImageList m_ImageList;
    private UIAlgoImage m_CenterImage;
    private QuickNavigationBar m_NavBar;
    private JComponent m_SouthBar;
    private Set<JComponent> m_PreviousDisabled = new HashSet<>();

    public MainFrame(Controller controller){
        super("ALGO: Assignment 1.0");
        setLayout( new BorderLayout());
        m_SouthBar = new JPanel();
        m_NavBar = new QuickNavigationBar(controller);

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
    
    private void traverseComponents(JComponent root, Consumer<JComponent> consumer) {
        for(int i = 0; i < root.getComponentCount(); ++i) {
            Object obj = root.getComponent(i);
            if( obj instanceof JComponent) {
                JComponent c = (JComponent)(obj);
                consumer.accept(c);
                traverseComponents(c, consumer);
            }
        }
    }
    
    public void disableInput() {
        synchronized (getContentPane().getTreeLock()) {
            traverseComponents((JComponent)getContentPane(),  c -> {
                if(!c.isEnabled()) {
                    m_PreviousDisabled.add(c);
                }
                c.setEnabled(false);
            });
        }
    }
    
    public void enableInput() {
        synchronized (getContentPane().getTreeLock()) {
            traverseComponents((JComponent)getContentPane(),  c -> {
                if(!m_PreviousDisabled.remove(c)) {                    
                    c.setEnabled(true);
                }
            });
        }
    }

    @Override
    public void onStartUp(Controller controller){
        m_Controller = controller;
        m_CenterImage = new UIAlgoImage();
        m_ImageList = new UIImageList( controller, controller.getImageHandler());
        setJMenuBar(initMenuBar(controller));
        initSouthBar(controller, m_SouthBar);
        
        
        final JScrollPane imgPane = new JScrollPane(m_CenterImage);
        add(m_ImageList, BorderLayout.EAST);
        add(imgPane, BorderLayout.CENTER);
        add(m_NavBar,BorderLayout.NORTH);
        add(m_SouthBar, BorderLayout.SOUTH);
        
        applyListenerTo(m_CenterImage, controller);
        
        final JPanel westPanel = new JPanel(new BorderLayout());
        
        final JSlider slider = new JSlider(JSlider.VERTICAL, 0, 100, 100);
        slider.setMajorTickSpacing(25);
        slider.setMinorTickSpacing(5);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        
        
        westPanel.add(slider, BorderLayout.CENTER);
        westPanel.add( UtilsUI.createBttn("OK", () -> {
            controller.reduceCurrentImage(slider.getValue());
        }), BorderLayout.SOUTH);
        
        add(westPanel, BorderLayout.WEST);
        
        
    }
    
    private void initSouthBar(Controller controller, JComponent target){
        target.setLayout( new BorderLayout());
        final JLabel imgScaleLabel = new JLabel(getImageScaleAsText());
        m_CenterImage.addComponentListener( new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                imgScaleLabel.setText(getImageScaleAsText());
            }
        });
        controller.addNewMainImageListener( img -> imgScaleLabel.setText(getImageScaleAsText()));
        
        JLabel scaleLabel = new JLabel("100%");
        
        JScrollBar scaleBar = new JScrollBar(JScrollBar.HORIZONTAL, 100, 1, 5, 300);
        scaleBar.addAdjustmentListener( evt -> {
            
            m_CenterImage.setScale( scaleBar.getValue()/100f);
            m_CenterImage.setPreferredSize(m_CenterImage.getSizeByScale());
            m_CenterImage.setSize(m_CenterImage.getSizeByScale());
            
            validate();
        });
        
        JButton fitBttn = UtilsUI.createBttn("fit", bttn -> {
            m_CenterImage.setAutoFit(true);
            Dimension parent =  m_CenterImage.getParent().getSize();
            m_CenterImage.setPreferredSize(parent);
            m_CenterImage.setSize(parent);
            validate();
            repaint();
        });
        
        JPanel scalePane = new JPanel( new BorderLayout());
        scalePane.add(scaleLabel, BorderLayout.WEST);
        scalePane.add(scaleBar, BorderLayout.CENTER);
        scalePane.add(fitBttn, BorderLayout.EAST);
        
        target.add(imgScaleLabel, BorderLayout.EAST);
        target.add(scalePane, BorderLayout.CENTER);
    }
    
    private String getImageScaleAsText(){
        if(m_CenterImage != null){
            return "x:" + String.format("%.2f", m_CenterImage.getXScaleOfImage())  + " y:" + String.format("%.2f", m_CenterImage.getYScaleOfImage());
        }
        return "";
    }
    
    private void applyListenerTo(UIAlgoImage image, Controller controller){
        image.addMouseListener(new MouseAdapter() {
            
            @Override
            public void mouseReleased(MouseEvent e) {
                if(image.getSource() != null) {
                    int x = fixXOffset(e.getX());
                    int y = fixYOffset(e.getY());
                    
                    if(image.getSource().inRange(x, y)){
                        if(controller.getCurrentTool() == null) {
                            return;
                        }
                        controller.getCurrentTool().onReleased(x,y );
                    }
                }
            }
            
            @Override
            public void mousePressed(MouseEvent e) {
                if(image.getSource() != null) {
                    int x = fixXOffset(e.getX());
                    int y = fixYOffset(e.getY());
                    
                    if(image.getSource().inRange(x, y)){
                        if(controller.getCurrentTool() == null) {
                            return;
                        }
                        controller.getCurrentTool().onPressed(x,y );
                    }
                }
            }
            
            @Override
            public void mouseMoved(MouseEvent e) {
                //controller.getCurrentTool().onMove(e.getX(), e.getY());
                
            }
        });
        
        
        image.addMouseMotionListener( new MouseMotionListener() {
            
            @Override
            public void mouseMoved(MouseEvent e) {
                //controller.getCurrentTool().onMove(e.getX(), e.getY());
                
            }
            
            @Override
            public void mouseDragged(MouseEvent e) {
                if(image.getSource() != null) {
                    int x = fixXOffset(e.getX());
                    int y = fixYOffset(e.getY());
                    //System.out.println(String.format("x:%s y:%s", e.getX(), e.getY()));
                    
                    if(image.getSource().inRange(x, y)){
                        if(controller.getCurrentTool() == null) {
                            return;
                        }
                        controller.getCurrentTool().onMove(x,y );
                    }
                }
                
            }
        });
    }
    
    private int fixXOffset(int x){
        int offset = /*0;*/m_CenterImage.getWidth() /2 - m_CenterImage.getSizeByScale().width / 2;
        x -= offset;
        return (int)Math.round(m_CenterImage.getXScaleOfImage() * x);
    }
    
    private int fixYOffset(int y){
        int offset = /*0;*/m_CenterImage.getHeight() /2 - m_CenterImage.getSizeByScale().height / 2;
        y -= offset;
        return (int)Math.round(m_CenterImage.getYScaleOfImage() * y);
    }

    private JMenuBar initMenuBar(Controller controller){
        final JMenuBar bar = new JMenuBar();
        bar.add( initFileMenu(controller));
        bar.add( initActionMenu(controller));
        bar.add( initToolMenu(controller));
        return bar;
    }
    private JMenu initToolMenu(Controller controller){
        JMenu toolMenu = new JMenu("Tools");
        for(ImageTool tool : controller.getTools()){
            tool.initNavigationBarContext(m_NavBar);
            toolMenu.add( UtilsUI.createItem(tool.getRepresentation() + " | " + tool.getName(), () -> controller.activateTool(tool)));
            tool.getToolOptions().forEach( opt -> {
                toolMenu.add(UtilsUI.createItem(opt.getName(), () -> {
                    if(controller.getCurrentTool() != tool){
                        controller.activateTool(tool);
                    }
                    opt.getAction().accept(controller);
                }));
            });
            
            //item.setText(tool.getName());
            toolMenu.addSeparator();
        }
        m_NavBar.update();
        return toolMenu;
    }

    private JMenu initFileMenu(Controller controller){
        final JMenu menu = new JMenu("File");
        menu.add( UtilsUI.createItem("Load", () -> {
            controller.loadImagesCommand();
            validate();
        }));
        
        menu.add( UtilsUI.createItem("Analyse Image", () -> {
            m_Controller.analyseCurrentImage();
        }));
        
        menu.add( UtilsUI.createItem("Exit", () -> {
            m_Controller.closeApp();
        }));
        return menu;
    }

    private JMenu initActionMenu(Controller controller){
        final JMenu menu = new JMenu("Actions");

        menu.add( UtilsUI.createItem("New", () -> {
            controller.createNewBlankImage(400, 200);
        }));

        m_NavBar.addNavEntry("New", "+", (ctrl) -> controller.createNewBlankImage(400,200));
        
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
        JButton bttnShuffleToggle = UtilsUI.createBttn("on/off", bttn -> {
           m_Controller.toggleShuffle();
           if(m_Controller.getShuffleManager().isRunning() ){
               shuffleToggle.setText("End shuffle");
           } else {
               shuffleToggle.setText("Start shuffle");
           }
        });
        bttnShuffleToggle.setEnabled(false);
        controller.getShuffleManager().addToggleListener( (img, added) -> {
            shuffleToggle.setEnabled(controller.getShuffleManager().isReady());
            bttnShuffleToggle.setEnabled(controller.getShuffleManager().isReady());
            
        });
        
        m_NavBar
            .addNavEntry("Shuffle", bttnShuffleToggle)
            .addNavEntry("Shuffle", UtilsUI.createBttn("all", controller::addAllImagesToShuffle))
            .addNavEntry("Shuffle", UtilsUI.createBttn("none", controller::removeAllImagesFromShuffle));
        
        
        menu.add( UtilsUI.createItem("Add all", controller::addAllImagesToShuffle));
        menu.add( UtilsUI.createItem("remove all", controller::removeAllImagesFromShuffle));
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
    
    
    public Color openColorChooser(){
        return JColorChooser.showDialog(this, "Please select a color", Color.BLACK);
    }
   
        
}