package nova;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ImageHandler {

    private Collection<AlgoImage> m_ImageList;
    private Set<NewImageListener> m_NewImageListener;

    public ImageHandler(){
        m_ImageList = new ArrayList<>();
        m_NewImageListener = new HashSet<>();
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

    public static interface NewImageListener{
        public void update(AlgoImage newImage, ImageHandler caller);
    }
    
    public int size(){
        return m_ImageList.size();
    }
}