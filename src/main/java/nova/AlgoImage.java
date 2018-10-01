package nova;

import java.awt.Image;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.util.Arrays;

public class AlgoImage {

    private int[] m_TmpImageBuffer;
    private int[] m_ImagePix;
    private int m_Width, m_Height;
    private MemoryImageSource m_ImageSource;

    public AlgoImage(int width, int height){
        this( new int[width * height], width, height);
    }

    public AlgoImage(int[] src, int width, int height){
        m_Width = width;
        m_Height = height;
        m_ImagePix = src;
        m_ImageSource = initImageSource(m_ImagePix, width, height);
    }

    public AlgoImage(Image src, int width, int height){
        m_Width = width;
        m_Height = height;
        m_ImagePix = new int[width * height];
        PixelGrabber grabber = new PixelGrabber(
            src,
            0,
            0,
            width,
            height,
            m_ImagePix,
            0,
            width
        );
        try{
            grabber.grabPixels();
            m_ImageSource = initImageSource(m_ImagePix, width, height);
        } catch (Exception e){
            e.printStackTrace(System.err);
        }
    }

    private MemoryImageSource initImageSource(int[] pix, int w, int h){
        final MemoryImageSource mis = new MemoryImageSource(w, h, pix, 0, w);
        mis.setAnimated(true);
        return mis;
    }

    private int toIndex(int x, int y){
        return m_Width * y + x;
    }

    public int getWidth(){
        return m_Width;
    }

    public int getHeight(){
        return m_Height;
    }

    public void setPx(int x, int y, int value){
        final int i  = toIndex(x, y);
        m_ImagePix[i] = value;
    }

    public void setTmpPx(int x, int y, int value){
        if(m_TmpImageBuffer == null){
            m_TmpImageBuffer = Arrays.copyOf(m_ImagePix, m_ImagePix.length);
        }
        final int i  = toIndex(x, y);
        m_TmpImageBuffer[i] = value;
    }

    public void resetBuffer(){
        for(int i = 0; i < m_ImagePix.length; ++i){
            m_TmpImageBuffer[i] = m_ImagePix[i];
        }
    }

    public void writeBuffer(){
        m_ImagePix = m_TmpImageBuffer;
        m_TmpImageBuffer = null;
    }

    public void clearBuffer(){
        m_TmpImageBuffer = null;
    }

    public void update(){
        m_ImageSource.newPixels();
    }

    public MemoryImageSource getImageSource(){
        return m_ImageSource;
    }
}