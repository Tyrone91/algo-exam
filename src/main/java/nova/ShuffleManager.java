package nova;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ShuffleManager {

    private volatile boolean m_Running;
    private Set<AlgoImage> m_Images;
    private Consumer<AlgoImage> m_Consumer;
    private Supplier<AlgoImage> m_Supplier;
    private Runnable m_Callback;
    private Controller m_Controller;
    private Set<BiConsumer<AlgoImage, Boolean>> m_OnToggleListener;

    public ShuffleManager(Controller controller) {
        m_Running = false;
        m_Images = new HashSet<>();
        m_Consumer = img -> {
        };
        m_Callback = () -> {
        };
        m_Controller = controller;
        m_OnToggleListener = new HashSet<>();
    }

    private void shuffle() {

        final int w = m_Images.stream().mapToInt(AlgoImage::getWidth).min().orElse(500);
        final int h = m_Images.stream().mapToInt(AlgoImage::getHeight).min().orElse(300);

        if (m_Supplier == null) {
            m_Supplier = () -> new AlgoImage(w, h);
        }

        AlgoImage image = m_Supplier.get();
        m_Consumer.accept(image);

        AlgoImage[] array = m_Images.stream().map(img -> m_Controller.scaleImage(img, w, h))
                .collect(Collectors.toList()).toArray(new AlgoImage[0]);

        int cnt = 2 % array.length;
        AlgoImage img1 = array[0];
        AlgoImage img2 = array[1];
        while (isRunning()) {
            for (int p = 0; p <= 100 && isRunning(); ++p) {

                for (int i = 0; i < image.raw().length; ++i) {
                    int data = ColorUtils.gradient(img1.get(i), img2.get(i), p);
                    image.set(i, data);
                }
                image.getImageSource().newPixels();
                m_Callback.run();
                try {
                    Thread.sleep(20);
                } catch (Exception e) {
                    m_Controller.error(e);
                }
            }
            img1 = img2;
            img2 = array[cnt++];
            cnt = cnt % array.length;
        }
    }

    public boolean isReady() {
        return m_Images.size() >= 2;
    }

    public void setConsumer(Consumer<AlgoImage> consumer) {
        m_Consumer = consumer;
    }

    public void setSupplier(Supplier<AlgoImage> supplier) {
        m_Supplier = supplier;
    }

    public void setCallback(Runnable callback) {
        m_Callback = callback;
    }

    public boolean toggleImage(AlgoImage image) {
        if (m_Images.contains(image)) {
            m_Images.remove(image);
            m_OnToggleListener.forEach(l -> l.accept(image, false));
            return false;
        } else {
            m_Images.add(image);
            m_OnToggleListener.forEach(l -> l.accept(image, true));
            return true;
        }
    }
    
    public boolean has(AlgoImage image){
        return m_Images.contains(image);
    }

    public void startShuffle() {
        if (!isReady()) {
            return;
        }
        m_Running = true;
        new Thread(this::shuffle).start();
    }

    public void endShuffle() {
        m_Running = false;
    }

    public boolean isRunning() {
        return m_Running;
    }

    public void clear() {
        final List<AlgoImage> tmp = new ArrayList<>(m_Images);
        m_Images.clear();
        tmp.forEach( img -> m_OnToggleListener.forEach( l -> l.accept(img, false)));
    }

    public ShuffleManager addToggleListener(BiConsumer<AlgoImage, Boolean> listener) {
        m_OnToggleListener.add(listener);
        return this;
    }
}