package nova;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ImageHandler {

    private List<AlgoImage> m_ImageList;
    private Set<NewImageListener> m_NewImageListener;

    public ImageHandler(){
        m_ImageList = new ArrayList<>();
        m_NewImageListener = new HashSet<>();
    }

    private void swapEntries(int entry1, int entry2){
        AlgoImage tmp = m_ImageList.get(entry1);
        m_ImageList.set(entry1, m_ImageList.get(entry2));
        m_ImageList.set(entry2, tmp);
    }

    public synchronized ImageHandler addImage(AlgoImage image){
        m_ImageList.add(image);
        m_NewImageListener.forEach( l -> l.update(image, this));
        return this;
    }

    public void addNewImageListener(NewImageListener listener){
        m_NewImageListener.add(listener);
    }

    public Collection<AlgoImage> getImages(){
        return m_ImageList;
    }

    public void moveImageUp(AlgoImage image){
        final int index = m_ImageList.indexOf(image);
        if(index == 0){
            return;
        }
        swapEntries(index -1, index);
    }

    public void moveImageDown(AlgoImage image){
        final int index = m_ImageList.indexOf(image);
        if(index == m_ImageList.size()-1){
            return;
        }
        swapEntries(index + 1, index);
    }

    public static interface NewImageListener{
        public void update(AlgoImage newImage, ImageHandler caller);
    }
    
    public int size(){
        return m_ImageList.size();
    }
}