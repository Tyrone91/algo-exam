package nova;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bresenham {
    
    public static interface Renderer {
        public void render(int x, int y, int color);
    }
    
    private static interface Circlesetter {
        public void set(Renderer r, int x0, int y0, int x, int y, int color0, int color1);
    }
    
    private static void circlepointfilled(Renderer r, int x0, int y0, int x, int y, int color0, int color1) {
        
        Bresenham.line(r, x0 -x , y0 + y, x0 + x, y0 + y, color0, color1);
        Bresenham.line(r, x0 -x , y0 - y, x0 + x, y0 - y, color0, color1);
        
        Bresenham.line(r, x0 - y, y0 + x, x0 + y, y0 + x, color0, color1);
        Bresenham.line(r, x0 - y, y0 - x, x0 + y, y0 - x, color0, color1);
    }
    
    private static int hackR = 0; //TODO: think about this
    
    private static void circlepoint(Renderer r, int x0, int y0, int x, int y, int color0, int color1) {
        double rad = hackR;
        
        int color = ColorUtils.gradient(color0, color1, (int)Math.round((x/rad) * 100));
        System.out.println("grad: " + (int)Math.round((y/rad) * 100));
        r.render(x0 + x,y0 + y, color); // p_0
        r.render(x0 - x,y0 + y, color); // p_1
        r.render(x0 + x,y0 - y, color); // p_2
        r.render(x0 - x,y0 - y, color); // p_3
        r.render(x0 + y,y0 + x, color); // p_4
        r.render(x0 - y,y0 + x, color); // p_5
        r.render(x0 + y,y0 - x, color); // p_6
        r.render(x0 - y,y0 - x, color); // p_7
    }
    
    public static void circle(Circlesetter setter, Renderer renderer,int x0, int y0, int r, int color0, int color1) {
        hackR = r;
        int y = 0;
        int x = r;
        int F = -r;
        int dy = 1;
        int dyx = -2 * r + 3;
        while (y <= x) {
            setter.set(renderer,x0,y0,x,y,color0, color1);
            ++y;
            dy += 2;
            dyx += 2;
            if (F > 0) {
                F += dyx;
                --x;
                dyx += 2;
            } else {
                F += dy;
            }
        }
    }
    
    public static void circle(AlgoImage src, int x0, int y0, int x1, int y1,int color0, int color1, boolean filled) {
        int a = x1 - x0;
        int b = y1 - y0;
        int r = (int) Math.round(Math.sqrt( a*a + b*b));
        circle(src, x0, y0, r,color0, color1, filled);
    }
    
    public static void circle(AlgoImage src, int x0, int y0, int r, int color0, int color1, boolean filled) {
        Circlesetter setter = filled ? Bresenham::circlepointfilled : Bresenham::circlepoint;
        circle(setter, (x,y,color) -> {
            if(src.inRange(x, y)){
                src.setPx(x, y, color);
            }
        }, x0, y0, r, color0, color1);
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

    public static void selection(AlgoImage target, Rectangle rect){
        selection(target, rect.x, rect.y, rect.width, rect.height);
    }

    public static void selection(AlgoImage target, int x, int y, int w, int h){
        final int len = 3;
        final int gap = 2;
        for(int i = 0; i + len <= w; i += (len + gap)){
            int px = i + x;
            line(target, px, y, px + len, y);
            line(target, px, y + h, px + len, y + h);
        }

        for(int i = 0; i + len < h; i += len + gap){
            int py = i + y;
            line(target, x, py, x, py + len);
            line(target, x+w, py, x+w, py + len);
        }
    }
}
