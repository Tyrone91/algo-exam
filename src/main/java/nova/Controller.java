package nova;

import java.util.Set;

import javax.swing.SwingUtilities;

public class Controller {
    
    private MainFrame m_MainFrame;
    private ImageHandler m_ImageHandler;
    private Set<StartUpListener> m_StartUpListener;

    public Controller(){
        m_MainFrame = new MainFrame();
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

    public static interface StartUpListener{
        public void onStartUp(Controller controller);
    }
}