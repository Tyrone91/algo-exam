package nova;

import java.awt.Image;
import java.awt.Rectangle;
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
        if(!inRange(x, y)){
            throw new IllegalArgumentException(
                String.format("x=%s or y=%s not in range of %s/%s", x,y, m_Width, m_Height)
            );
        }
        return m_Width * y + x;
    }

    public boolean inRange(int x, int y){
        return !(x < 0 || y < 0 || x >= m_Width || y >= m_Height);
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

    public int getBufferData(int x, int y){
        return m_TmpImageBuffer[toIndex(x, y)];
    }

    public void createBuffer(){  
        m_TmpImageBuffer = Arrays.copyOf(m_ImagePix, m_ImagePix.length);
    }

    public void resetToBuffer(){
        for(int i = 0; i < m_ImagePix.length; ++i){
            m_ImagePix[i] = m_TmpImageBuffer[i];
        }
    }

    public boolean hasBuffer() {
        return m_TmpImageBuffer != null;
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

    public int[] raw(){
        return m_ImagePix;
    }

    public AlgoImage set(int i, int value){
        m_ImagePix[i] = value;
        return this;
    }

    public int get(int i){
        return m_ImagePix[i];
    }

    public int getPx(int x, int y){
        return m_ImagePix[toIndex(x, y)];
    }

    public void apply(Matrix op) {
        apply(op, new Rectangle(getWidth(), getHeight()));
    }

    public void apply(Matrix op, Rectangle range) {
        if(!hasBuffer()){
            //createBuffer();
        }
        final int w = getWidth();
        final int h = getHeight();
        final Vector3 p = Vector3.of(0,0);

        for(int y = 0; y < h; ++y){
            for(int x = 0; x < w; ++x){
                p.setX(x).setY(y);
                final Vector3 pCurrent = Matrix.mult(op, p);
                final int px = (int)pCurrent.getX();
                final int py = (int)pCurrent.getY();

                if(!range.contains(px, py)){
                    continue;
                }
                
                int val = 0xFFFFFFFF;
                if(inRange(px,py)){
                    val = getBufferData(px, py);
                    //setPx(px,py, 0xFFFFFFFF);
                }
                setPx(x, y, val);
            }
        }
    }
}