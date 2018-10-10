package nova;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class UIAlgoImage extends JComponent{

    private AlgoImage m_SrcImg;
    private Image m_Image;

    public UIAlgoImage(AlgoImage src){
        m_SrcImg = src;
        m_Image = createImage(src.getImageSource());
        setPreferredSize(new Dimension(src.getWidth(), src.getHeight()));
    }
    
    public UIAlgoImage(){
        m_Image = createImage(200, 200);
    }
    
    public void setSource(AlgoImage src){
        m_SrcImg = src;
        m_Image = createImage(src.getImageSource());
    }
    
    public AlgoImage getSource(){
        return m_SrcImg;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int w = getWidth();
        int h = getHeight();

        g.drawImage(m_Image, 0, 0, w, h, this);
    }
    
    public float getXScaleOfImage(){
        if(m_Image == null){
            return 0;
        }
        return (float)m_Image.getWidth(this) / (float)getWidth();
    }
    
    public float getYScaleOfImage(){
        if(m_Image == null){
            return 0;
        }
        return (float)m_Image.getHeight(this) / (float)getHeight();
    }

    
}