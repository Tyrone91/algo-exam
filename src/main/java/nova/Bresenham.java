package nova;

import java.awt.Color;
import java.awt.Graphics;

public class Bresenham {
    
    public static interface Renderer {
        public void render(int x, int y, int color);
    }
    
    private static void line(Renderer renderer, int x0, int y0, int x1, int y1, int color1, int color2){
        final int dx = Math.abs(x0-x1);
        final int dy = Math.abs(y0-y1);
        final int sgnDx = x0 < x1 ? 1 : -1;
        final int sgnDy = y0 < y1 ? 1 : -1;
        int shortD,longD,incXshort,incXlong,incYshort,incYlong;
        if (dx > dy) {
            shortD = dy; longD = dx; incXlong = sgnDx; incXshort = 0; incYlong = 0; incYshort = sgnDy;
        } else {
            shortD = dx; longD = dy; incXlong = 0; incXshort = sgnDx; incYlong = sgnDy; incYshort = 0;
        }
        int d = longD / 2, x = x0, y=y0;
        if(longD <= 0)
            return;
        for(int i = 0;i <= longD;++i){
//          calculateColor(g, (i/longD)*100 );
            int p = (int)(((float)i/(float)longD)*100);
            //int color = pix(m_Color1.getRGB(), m_Color2.getRGB(), p);
           // pix[(y*width + x)] = color;
            renderer.render(x, y, ColorUtils.gradient(color1, color2, p));        
//          pix[(y*width + x)] = 0xFF000000;
            x += incXlong;
            y += incYlong;
            d += shortD;
            if (d >= longD) {
                d -= longD;
                x += incXshort;
                y += incYshort;
            }
            
        }
    }
    
    public static void line(AlgoImage target, int x0, int y0, int x1, int y1){
        line(target, x0, y0, x1, y1, 0xFF000000, 0xFF000000);
    }
    
    public static void line(AlgoImage target, int x0, int y0, int x1, int y1, int color1, int color2 ){
        line( (x,y,color) -> { target.set(y * target.getWidth() + x, color); }, x0, y0, x1, y1, color1, color2);
    }
    
    public static void line(int[] target, int width, int x0, int y0, int x1, int y1 ){
        line( (x,y,color) -> { target[y * width + x] = color; }, x0, y0, x1, y1, 0xFF0000, 0xFF0000);
    }
    
    public static void line(Graphics target, int x0, int y0, int x1, int y1){
        line( (x,y, color) -> { 
                target.setColor( new Color(color));
                target.drawLine(x, y, x, y);
            }, x0, y0, x1, y1, 0xFF0000, 0xFF0000 );
    }
}
