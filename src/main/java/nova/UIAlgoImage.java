package nova;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

public class UIAlgoImage extends JComponent{

    private AlgoImage m_SrcImg;
    private Image m_Image;
    private float m_Scale = 1.0f;
    private boolean m_AutoFit = true;

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
        if(m_AutoFit){
            int w = getWidth();
            int h = getHeight();
            g.drawImage(m_Image, 0, 0, w, h, this);
        } else {
            int imgW = getSizeByScale().width;
            int imgH = getSizeByScale().height;
            
            int x = getWidth()/2 - imgW/2;
            int y = getHeight()/2 - imgH/2;
            
            g.drawImage(m_Image, x, y, imgW, imgH, this);
        }    
    }
    
    public float getXScaleOfImage(){
        if(m_Image == null){
            return 0;
        }
        
        if(m_AutoFit){
            return (float)m_Image.getWidth(this) / getWidth();
        }
        
        float s = m_Image.getWidth(this) < getWidth() ? 1 : 1 / m_Scale;
        if(m_Image.getWidth(this) <= getWidth()){
            return (float)m_Image.getWidth(this) / getWidth() * s;
        } else {
            return (float)Math.floor((float)m_Image.getWidth(this) / getWidth()) * s;
        }
        
        //return (float)m_Image.getWidth(this) / getWidth() * s;
    }
    
    public float getYScaleOfImage(){
        if(m_Image == null){
            return 0;
        }
        
        if(m_AutoFit){
            return (float)m_Image.getHeight(this) / getHeight();
        }
        
        float s = m_Image.getHeight(this) < getHeight() ? 1 : 1 / m_Scale;
//        return (float)m_Image.getHeight(this) / getHeight() * s;
        
        if(m_Image.getHeight(this) <= getHeight()){
            return (float)m_Image.getHeight(this) / getHeight() * s;
        } else {
            return (float)Math.floor((float)m_Image.getHeight(this) / getHeight()) * s;
        }
    }
    
    public Dimension getSizeByScale(){
        return new Dimension((int)(m_Image.getWidth(this) * m_Scale), (int)(m_Image.getHeight(this) * m_Scale));
    }
    
    public void setScale(float val){
        m_AutoFit = false;
        m_Scale = val;
    }
    
    public void setAutoFit(boolean val){
        m_AutoFit = val;
    }
    
}