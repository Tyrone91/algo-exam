package nova;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

public class Controller {
    
    private MainFrame m_MainFrame;
    private ImageHandler m_ImageHandler;
    private Set<StartUpListener> m_StartUpListener;

    public Controller(){
        m_StartUpListener = new HashSet<>();
        m_MainFrame = new MainFrame(this);
        m_ImageHandler = new ImageHandler();
    }

    public void startUp(){
        m_StartUpListener.forEach( l -> l.onStartUp(this));
        m_StartUpListener.clear();
        m_StartUpListener = null;
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
            return;
        }
        m_StartUpListener.add(listener);
    }
    
    public void loadImagesCommand(){
        final File dir = new File("testimages/");
        
        
        Arrays.stream(dir.listFiles())
        .parallel()
        .filter(File::isFile)
        .map(m_MainFrame::loadImage)
        .forEach(m_ImageHandler::addImage);
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
    }
}