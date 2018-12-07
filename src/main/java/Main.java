public class Main
{
    public static void main( String[] args ) {
        
        final Controller controller = new Controller();
        controller.addStartUpListener( ctrl -> {
            //ctrl.loadImages(Arrays.asList(new File("testimages/reduction-test.png")));
            //ctrl.loadImages(Arrays.asList(new File("testimages/reduction-test2.png")));
        });
        controller.startUp();
        
        
        //testColorSub();
        
        int color1  = rgb(0,118,255);
        int color2  = rgb(0,147,255);
        int color3  = rgb(1,255,2);
        
        d(color1, color2);
        d(color1, color3);
    }
    
    public static void d(int color1,int color2) {
        
        System.out.printf("%s vs %s dist:=%s\n", rgb(color1), rgb(color2), ColorAnalyser.dist(color1, color2));
    }
    
    public static String rgb(int val) {
        return String.format("(%s,%s,%s)", ColorUtils.getRed(val), ColorUtils.getGreen(val), ColorUtils.getBlue(val));
    }
    
    public static int rgb(int red, int green, int blue) {
        return ColorUtils.setRed(red) | ColorUtils.setGreen(green) | ColorUtils.setBlue(blue);
    }
    
    public static void testColorSub() {
        int i = 0;
        AlgoImage image = new AlgoImage(10, 1);
        
        image.set(i++, rgb(3,5,0));
        image.set(i++, rgb(4,1,0));
        image.set(i++, rgb(7,7,0));
        
        image.set(i++, rgb(3,5,0));
        image.set(i++, rgb(4,1,0));
        image.set(i++, rgb(7,7,0));
        
        image.set(i++, rgb(3,5,0));
        image.set(i++, rgb(4,1,0));
        image.set(i++, rgb(7,7,0));
    
        image.set(i++, rgb(5,4,0));
        
        
        /*
        image.set(i++, rgb(255,255,255));
        image.set(i++, rgb(255,255,120));
        
        
        image.set(i++, rgb(255,240,0));
        image.set(i++, rgb(255,240,0));
        image.set(i++, rgb(255,240,0));
        image.set(i++, rgb(120,0,0));
        image.set(i++, rgb(100,0,50));
        image.set(i++, rgb(100,1,50));
        */
        
        
        ColorAnalyser analyser = new ColorAnalyser(image);
        analyser.substitution2(10);
        //System.out.println(analyser.dist(rgb(255,255,240), rgb(255,255,255)));
    }
}   

