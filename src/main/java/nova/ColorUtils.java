package nova;

public final class ColorUtils {

    private static final int BLUE_MASK = 0x000000FF;
    private static final int GREEN_MASK = 0x0000FF00;
    private static final int RED_MASK = 0x00FF0000;

    private static int gradientSingleColor(int color1, int color2, int percent){
        return color1 + (color2 - color1) * percent/100; //TODO: check java conversion error.
    }

    public static int getBlue(int color){
        return color & BLUE_MASK;
    }

    public static int getGreen(int color){
        return (color & GREEN_MASK) >> 8;
    }

    public static int getRed(int color){
        return (color & RED_MASK) >> 16;
    }

    public static int setRed(int color) {
        return color << 16;
    }

    public static int setBlue(int color){
        return color << 0;
    }

    public static int setGreen(int color){
        return color << 8;
    }

    public static int gradient(int color1, int color2, int percent){
        int red   = gradientSingleColor( getRed(color1),  getRed(color2),   percent);
        int green = gradientSingleColor( getGreen(color1),getGreen(color2), percent);
        int blue  = gradientSingleColor( getBlue(color1), getBlue(color2),  percent);
        return 255 << 24 | setRed(red) | setGreen(green) | setBlue(blue);
    }
}