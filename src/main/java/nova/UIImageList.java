package nova;

import java.awt.ScrollPane;

import javax.swing.JComponent;
import javax.swing.JScrollPane;

public class UIImageList extends JComponent{

    private ImageHandler m_Handler;

    public UIImageList(ImageHandler source){
        m_Handler = source;
    }

    public void updateImageList(){
        removeAll();
        m_Handler.getImages().stream()
            .map(UIAlgoImage::new)
            .forEach(this::add);
        validate();
    }
}