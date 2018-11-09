

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    
    
}
