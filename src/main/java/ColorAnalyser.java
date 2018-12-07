

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ColorAnalyser {
    
    private AlgoImage m_Image;
    
    public ColorAnalyser(AlgoImage target) {
        m_Image = target;
    }
    
    private Map<Integer, Integer> analyse(AlgoImage target) {
        Map<Integer, Integer> res = new HashMap<>();
        int[] data  = target.raw();
        for(int i = 0; i < data.length; ++i){
            
            Integer cnt = res.get(data[i]);
            if(cnt == null){
                cnt = 0;
            } 
            ++cnt;
            res.put(data[i], cnt);
        }
        return res;
    }
    
    private String rgb(int val) {
        return String.format("(%s,%s,%s)", ColorUtils.getRed(val), ColorUtils.getGreen(val), ColorUtils.getBlue(val));
    }
    
    public static double dist(int color1, int color2) {
        
        int r = ColorUtils.getRed(color1) - ColorUtils.getRed(color2);
        int g = ColorUtils.getGreen(color1) - ColorUtils.getGreen(color2);
        int b = ColorUtils.getBlue(color1) - ColorUtils.getBlue(color2);
        
        return Math.sqrt( (r*r) + (g*g) + (b*b));
    }
    
    private int findIndex(int[] array, int pivot, Function<Integer, Integer>... toColor) {
        int lower = 0;
        int upper = array.length-1;
        int index  = -1;
        
        while(lower <= upper) {
            index = lower + ((upper - lower) / 2);

            for(int i = 0; i < toColor.length; ++i) {
                final Function<Integer, Integer> func = toColor[i];
                final int currentColor = func.apply(array[index]);
                final int pivotColor = func.apply(pivot);
                
                if(pivotColor == currentColor) {
                    if(i == toColor.length -1) {
                        return index;
                    }
                } else {
                    
                    if(pivotColor > currentColor) {
                        lower = index + 1;
                    } else {
                        upper = index - 1;
                    }
                    break;
                }
            }
        }
        //return index;
        if(index == array.length-1) {
            return index;
        }
        double distL = dist(pivot, array[index] );
        double distR = dist(pivot, array[index+1] );
        //System.out.println(String.format("Not found: %s. index: %s. L: %s R: %s. Taking: %s", pivot,index, distL, distR, distL < distR ? "left" : "right"));
        return (distL < distR) ? index : index+1;
        
    }
    
    public void print(String prefix, int[] array) {
        System.out.print(prefix + " =\t[");
        System.out.print(Arrays.stream(array).mapToObj(this::rgb).collect(Collectors.joining(",")));
        System.out.println("]");
    }
    
    private List<Map.Entry<Integer, Integer>> sortByOccurrence(AlgoImage image) {
        Map<Integer, Integer> data = analyse(image);
        List<Map.Entry<Integer, Integer>> list = new ArrayList<>(data.entrySet());
        list.sort( (e0, e1) -> e1.getValue().compareTo(e0.getValue()) );
        return list;
    }
    
    private DistanceContainer[] toArray(DistanceContainer... array) {
        return array;
    }
    
    private DistanceContainer getDistance(ColorComperator c, int[] array, int red, int green, int blue, int color) {
        double dRedL = safeDist(c.getRed(), red-1, color);
        double dRedR = safeDist(c.getRed(), red, color);
        /*
        System.out.println("red:=" + red);
        System.out.println("green:=" + green);
        System.out.println("blue:=" + blue);
        System.out.println("dist(color,0):=" + dist(color, c.getRed()[red]));
        */
        
        double dGreenL = safeDist(c.getGreen(), green-1, color);
        double dGreenR = safeDist(c.getGreen(), green, color);
        
        double dBlueL = safeDist(c.getBlue(), blue-1, color);
        double dBlueR = safeDist(c.getBlue(), blue, color);
        
        /*
        System.out.printf("d(%s):=%s\n", "red-1", dRedL);
        System.out.printf("d(%s):=%s\n", "red+1", dRedR);
        
        System.out.printf("d(%s):=%s\n", "green-1", dGreenL);
        System.out.printf("d(%s):=%s\n", "green+1", dGreenR);
        */
        
        return Arrays.stream(
                toArray(
                    pair(red-1, dRedL, c.getRed()),
                    pair(red, dRedR, c.getRed()),
                    
                    pair(green-1, dGreenL, c.getGreen()),
                    pair(green, dGreenR, c.getGreen()),
                    
                    pair(blue-1, dBlueL, c.getBlue()),
                    pair(blue, dBlueR, c.getBlue()))
                )
                .min( (p1,p2) -> Double.compare(p1.distance, p2.distance) )
                .orElseThrow(RuntimeException::new);
    }
    
    private int rangeMin(int value, double distance, Function<Integer, Integer> func) {
        double tmp = func.apply(value) - distance;
        return (int)Math.ceil(tmp);
    }
    
    private int rangeMax(int value, double distance, Function<Integer, Integer> func) {
        double tmp = func.apply(value) + distance;
        return (int)Math.ceil(tmp);
    }
    
    private boolean inrange(int min,int max,int value) {
        return value >= min && value <= max;
    }
    
    private DistanceContainer pair(int index, double distance, int[] from) {
        DistanceContainer res = new DistanceContainer();
        res.index = index;
        res.distance = distance;
        res.from = from;
        return res;
    }
    
    private static class DistanceContainer {
        public int index;
        public double distance;
        int[] from;
    }
    
    private DistanceContainer checkCloserDistance(int color, int index, int[] array, double distance, DistanceContainer oldResult) {
        int min = (index - distance) < 0 ? 0 : (int)Math.ceil(index - distance);
        int max = Math.ceil(index + distance) >= array.length ? array.length -1 : (int)(Math.ceil(index + distance)); 
        for(int i = min; i <= max; ++i) {
            double tmpDist = dist(color, array[i]);
            if(tmpDist < distance) {
                oldResult.distance = tmpDist;
                oldResult.from = array;
                oldResult.index = i;
                distance = oldResult.distance;
                max = Math.ceil(index + distance) >= array.length ? array.length -1 : (int)(Math.ceil(index + distance));
            }
        }
        return oldResult;
    }
    
    @SuppressWarnings("unchecked")
    public Map<Integer, Integer> substitution2(int cut) {
        TimeMeasurement m0 = TimeMeasurement.now("m0");
        final List<Entry<Integer, Integer>> sortedList = sortByOccurrence(m_Image);
        m0.stop().print();
        
        TimeMeasurement m1 = TimeMeasurement.now("m1");
        int usedColorsSize = (int) (sortedList.size() * (cut/100f));
        if(usedColorsSize == 0) {
            usedColorsSize = 1;
        }
        System.out.println( usedColorsSize + " used from " + sortedList.size());
        final int[] colorsAsArray = sortedList.stream().mapToInt( e -> e.getKey()).toArray();
        final int[] usedColors = Arrays.copyOf(colorsAsArray, usedColorsSize);
        final int[] replacedColors = Arrays.copyOfRange(colorsAsArray, usedColorsSize,colorsAsArray.length);
        
        //print("used", usedColors);
        //print("notused", replacedColors);
        System.out.println(replacedColors.length);
        ColorComperator c = new ColorComperator(usedColors);
        m1.stop().print();
        //print("used:",usedColors);
        //print("red",c.getRed());
        //print("green",c.getGreen());
        //print("blue",c.getBlue());
       
        TimeMeasurement m2 = TimeMeasurement.now("m2 replacer:");
        Map<Integer, Integer> replaceMap = new HashMap<>();
        for(Map.Entry<Integer, Integer> entry : sortedList) {
            int color = entry.getKey();
            
            //TimeMeasurement m3 =  TimeMeasurement.now("m3 binary search");
            int redIndex = findIndex(c.getRed(), color, ColorUtils::getRed, ColorUtils::getGreen, ColorUtils::getBlue);
            int greenIndex = findIndex(c.getGreen(), color, ColorUtils::getGreen, ColorUtils::getBlue, ColorUtils::getRed);
            int blueIndex = findIndex(c.getBlue(), color, ColorUtils::getBlue, ColorUtils::getRed, ColorUtils::getGreen);
            
            //m3.stop().print();
            
            if(c.getRed()[redIndex] == color && c.getGreen()[greenIndex] == color && c.getBlue()[blueIndex] == color) {
                //System.out.println("color found nothing todo");
                replaceMap.put(color, color);
            } else {
                //System.out.println("color: " + color + " and: " + replacedColors[cnt++]);
                //System.out.println("didn't found color now the fun begins: "  + rgb(color) + " ri:=" + redIndex + ",gi:=" + greenIndex + ",bi:=" + blueIndex);
                DistanceContainer res = getDistance(c, usedColors, redIndex, greenIndex, blueIndex, color);
                
                res = checkCloserDistance(color, redIndex, c.getRed(), res.distance, res);
                res = checkCloserDistance(color, greenIndex, c.getGreen(), res.distance, res);
                res = checkCloserDistance(color, blueIndex, c.getBlue(), res.distance, res);
                
                replaceMap.put(color, res.from[res.index]);
               
            }
            
            //System.out.println("replace: " + replaceIndex);
            
            //System.out.println(String.format("looking for %s. red: %s green: %s. blue: %s", this.rgb(color), redIndex, greenIndex, blueIndex));
        }
        m2.stop().print();
        return replaceMap;
    }
    
    private double safeDist(int[] array, int index, int pivot) {
        if(index < 0 || index >= array.length) {
            return Double.MAX_VALUE;
        }
        return dist(pivot, array[index]);
    }
    
    public ColorAnalyser toFile(File target) {
        try(FileWriter writer = new FileWriter(target, false)){
            Map<Integer, Integer> data = analyse(m_Image);
            List<Map.Entry<Integer, Integer>> list = new ArrayList<>(data.entrySet());
            list.sort( (e0, e1) -> e1.getValue().compareTo(e0.getValue()) ); //sort reverse
            for(Map.Entry<Integer, Integer> entry : list){
                
                writer.write("#" + Integer.toHexString(entry.getKey()) + ": " + entry.getValue());
                writer.append(System.lineSeparator());
            }
            
            
        } catch(IOException e) {
            e.printStackTrace(System.err);
        }
        return this;
    }
    
    private static class ColorComperator {
        
        private int[] colorByRed;
        private int[] colorByGreen;
        private int[] colorByBlue;
        
        public ColorComperator( int[] colors) {
            this.colorByRed = sortByRed(colors);
            this.colorByGreen = sortByGreen(colors);
            this.colorByBlue = sortByBlue(colors);
        }
        
        
        private int[] sortByRed(int[] colors) {
            return Arrays.stream(colors)
                .boxed().sorted( (c1,c2) -> {
                    return compareRed(c1,c2);
                })
                .mapToInt(i->i)
                .toArray();
        }
        
        private int[] sortByGreen(int[] colors) {
            return Arrays.stream(colors)
                    .boxed().sorted( (c1,c2) -> {
                        return compareGreen(c1,c2);
                    })
                    .mapToInt(i->i)
                    .toArray();  
        }
        
        private int[] sortByBlue(int[] colors) {
            return Arrays.stream(colors)
                    .boxed().sorted( (c1,c2) -> {
                        return compareBlue(c1,c2);
                    })
                    .mapToInt(i->i)
                    .toArray();
        }
        
        private int compareRed(int color1, int color2) { // x
            final int red1 = ColorUtils.getRed(color1);
            final int red2 = ColorUtils.getRed(color2);
            
            if(red1 == red2) {
                return compareGreen(color1, color2);
            }
            
            if(red1 < red2) {
                return -1;
            }
            
            return 1;
        }
        
        private int compareGreen(int color1 ,int color2) { // y
            final int green1 = ColorUtils.getGreen(color1);
            final int green2 = ColorUtils.getGreen(color2);
            
            if(green1 == green2) {
                return compareBlue(color1, color2);
            }
            
            if(green1 < green2) {
                return -1;
            }
            
            return 1;
        }
        
        private int compareBlue(int color1, int color2) { // z
            final int blue1 = ColorUtils.getBlue(color1);
            final int blue2 = ColorUtils.getBlue(color2);
            
            if(blue1 == blue2) {
                return compareRed(color1, color2);
            }
            
            if( blue1 < blue2) {
                return -1;
            }
            
            return 1;
        }
        
        public int[] getBlue() {
            return this.colorByBlue;
        }
        
        public int[] getGreen() {
            return this.colorByGreen;
        }
        
        public int[] getRed() {
            return this.colorByRed;
        }
    }
    
    
}
