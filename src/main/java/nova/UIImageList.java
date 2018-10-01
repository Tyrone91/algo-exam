package nova;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JComponent;

public class UIImageList extends JComponent implements Controller.StartUpListener{
    
    private static final int PREVIEW_WIDTH = 200;
    private static final int PREVIEW_HEIGHT = 200;
    private ImageHandler m_Handler;
    private Controller m_Controller;

    public UIImageList(Controller controller, ImageHandler source){
        m_Controller = controller;
        m_Handler = source;
        
        setPreferredSize( new Dimension( PREVIEW_WIDTH , PREVIEW_HEIGHT * 5));
        controller.addStartUpListener(this);
        setLayout( new GridLayout(0, 1));
        
        updateImageList();
        source.addNewImageListener( (img, handler ) -> {
            updateImageList();
        });
    }
    
    private void applyMouseListener(UIAlgoImage image){
        image.addMouseListener( new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                m_Controller.showImage(image.getSource());
            }
        });
    }
    
    @Override
    public void onStartUp(Controller controller) {
        setSize( new Dimension( PREVIEW_WIDTH , getParent().getHeight()));
        getParent().validate();
        
    }
    

    public void updateImageList(){
        final Dimension previewSize = new Dimension(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        removeAll();
        
        m_Handler.getImages().stream()
            .map(UIAlgoImage::new)
            .forEach( img -> {
                img.setSize(previewSize);
                applyMouseListener(img);
                add(img);
            });
        
        setSize( new Dimension( PREVIEW_WIDTH , m_Handler.size()));
        validate();
        repaint();
    }
}