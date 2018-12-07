

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
    
    private double ratio(int imgSize,int componentSize){
        
        if(m_AutoFit || componentSize * m_Scale > imgSize) {
            return (float)(imgSize) / componentSize;
        }
        
        if((imgSize * m_Scale) < componentSize) {
            float diff = componentSize - (imgSize * m_Scale);
            System.out.println(diff);
            return  (componentSize) / (componentSize - diff);
        }
        
        if(imgSize <= componentSize){
            return 1.0;
        }
        
        double scaledSize = componentSize * m_Scale;
        if(m_AutoFit || scaledSize > imgSize){
            return (float)(imgSize) / componentSize;
        }
        
        return componentSize / (float)(imgSize);
        
    }
    
    public double getXScaleOfImage(){
        if(m_Image == null) {
            return 0;
        }
        //System.out.println(String.format("w: img: %s como: %s ratio: %s scale: %s", m_Image.getWidth(this), getWidth(), ratio(m_Image.getWidth(this), getWidth()), m_Scale));
        return ratio(m_Image.getWidth(this), getWidth());
    }
    
    public double getYScaleOfImage(){
        if(m_Image == null) {
            return 0;
        }
        //System.out.println(String.format("h: img: %s como: %s ratio: %s scale: %s", m_Image.getHeight(this), getHeight(), ratio(m_Image.getHeight(this), getHeight()), m_Scale));
        return ratio(m_Image.getHeight(this), getHeight());
    }
    
    public Dimension getSizeByScale(){
        if(m_AutoFit) {
            return getSize();    
        }
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