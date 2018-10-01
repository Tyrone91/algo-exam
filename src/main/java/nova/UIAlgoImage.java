package nova;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class UIAlgoImage extends JComponent{

    private AlgoImage m_SrcImg;
    private Image m_Image;

    public UIAlgoImage(AlgoImage src){
        m_SrcImg = src;
        m_Image = createImage(src.getImageSource());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int w = getWidth();
        int h = getHeight();

        g.drawImage(m_Image, 0, 0, w, h, this);
    }

    
}