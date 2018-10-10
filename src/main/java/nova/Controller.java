package nova;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

public class Controller {
    
    private MainFrame m_MainFrame;
    private ImageHandler m_ImageHandler;
    private Set<StartUpListener> m_StartUpListener;
    private ShuffleManager m_ShuffleManager;
    private ImageTool m_CurrentTool;
    private AlgoImage m_CurrentImage;
    private List<ImageTool> m_Tools = Arrays.asList(new DrawTool());

    public Controller(){
        m_StartUpListener = new HashSet<>();
        m_MainFrame = new MainFrame(this);
        m_ImageHandler = new ImageHandler();
        m_ShuffleManager = new ShuffleManager(this);
        m_CurrentTool = m_Tools.get(0);
    }
    
    private void loadImagesFromFiles(Collection<File> files){
        System.out.println("loadImagesFromFiles: " + files.size());
        files.parallelStream()
        .map(m_MainFrame::loadImage)
        .forEach(m_ImageHandler::addImage);;
    }

    private void loadImageFromDirectory(Collection<File> dirs){
        Collection<File> images = dirs.parallelStream()
        .filter(f -> f.listFiles(Utils.IMAGE_FILTER) != null)
        .flatMap( f -> Arrays.stream( f.listFiles(Utils.IMAGE_FILTER)))
        .collect(Collectors.toCollection(ArrayList::new));
        loadImagesFromFiles(images);
        
    }
    public void startUp(){
        Collection<StartUpListener> listeners = new ArrayList<>(m_StartUpListener);
        m_StartUpListener.clear();
        m_StartUpListener = null;
        listeners.forEach( l -> l.onStartUp(this));
        m_CurrentTool.onInit(this, null);
        listeners.clear();

        SwingUtilities.invokeLater( () -> {
            m_MainFrame.setVisible(true);
        });
    }

    public boolean isShuffleActive(){
        return false;
    }

    public ImageHandler getImageHandler(){
        return m_ImageHandler;
    }

    public void addStartUpListener(StartUpListener listener){
        if(m_StartUpListener == null){
            System.err.println("No more listeners allowed");
            throw new RuntimeException("this time as error");
            //return;
        }
        m_StartUpListener.add(listener);
    }
    
    public void loadImagesCommand(){
        m_MainFrame.openImageFileChooser();
    }

    public void loadImages(Collection<File> files){
        System.out.println("loading files: " + files.size() );
        loadImageFromDirectory( 
            files.stream()
            .filter(File::isDirectory)
            .collect(Collectors.toList())
        );
        loadImagesFromFiles(
            files.stream()
            .filter(File::isFile)
            .collect(Collectors.toList())
        );
    }

    public static interface StartUpListener{
        public void onStartUp(Controller controller);
    }
    
    public void error(Exception e){
        e.printStackTrace(System.err);
    }
    
    public void error(String msg){
        System.err.println(msg);
    }
    
    public void showImage(AlgoImage image){
        m_MainFrame.setCenterImage(image);
        m_CurrentImage = image;
        m_CurrentTool.onImageChange(image);
    }

    public ShuffleManager getShuffleManager(){
        return m_ShuffleManager;
    }

    public void toggleShuffle(){
        if(m_ShuffleManager.isRunning() ){
            m_ShuffleManager.endShuffle();
        }else{
            m_ShuffleManager.setConsumer( img -> {
                SwingUtilities.invokeLater( () -> m_MainFrame.setCenterImage(img) );
            });
            m_ShuffleManager.setCallback( () -> {
                SwingUtilities.invokeLater( () -> {
                    m_MainFrame.repaint();
                });
            });
            m_ShuffleManager.startShuffle();
        }
    }

    public void closeApp(){
        m_ShuffleManager.endShuffle();
        m_MainFrame.dispose();
    }

    public AlgoImage scaleImage(AlgoImage src,int width,int height){
        return new AlgoImage(m_MainFrame.createImage(src.getImageSource())
            .getScaledInstance(width, height, Image.SCALE_SMOOTH), width, height);
    }
    
    public void activateTool(ImageTool tool){
        
    }
    
    public ImageTool getCurrentTool(){
        return m_CurrentTool;
    }
    
    public List<ImageTool> getTools(){
        return m_Tools;
    }
    
    public Color openColorChooser(){
        return m_MainFrame.openColorChooser();
    }
}