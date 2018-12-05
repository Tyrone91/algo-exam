

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.swing.SwingUtilities;

public class Controller {
    
    private Set<Consumer<AlgoImage>> m_NewMainImageListener = new HashSet<>();
    private Set<StartUpListener> m_StartUpListener  = new HashSet<>();
    
    private MainFrame m_MainFrame;
    private ImageHandler m_ImageHandler;
    
    private ShuffleManager m_ShuffleManager;
    private ImageTool m_CurrentTool;
    private AlgoImage m_CurrentImage;
    private List<ImageTool> m_Tools = Arrays.asList(
        new DrawTool(),
        new RotateTool(),
        new SelectionTool(),
        new TranslateTool(),
        new ScaleTool(),
        new XShereTool(),
        new YShereTool());

    private Rectangle m_ImageSelection;
    private Matrix m_ImageOperations = Matrix.unit();

    public Controller(){
       
        m_MainFrame = new MainFrame(this);
        m_ImageHandler = new ImageHandler();
        m_ShuffleManager = new ShuffleManager(this);
        m_CurrentTool = m_Tools.get(0);
        m_CurrentImage = new AlgoImage(400,400);
        for(int i = 0; i < m_CurrentImage.raw().length; ++i){
            m_CurrentImage.set(i, 0xFFFFFFFF);
        }
    }
    
    private void loadImagesFromFiles(Collection<File> files){
        System.out.println("loadImagesFromFiles: " + files.size());
        files.parallelStream()
        .map(m_MainFrame::loadImage)
        .forEach(m_ImageHandler::addImage);;
    }

    private void loadImageFromDirectory(Collection<File> dirs){
        Collection<File> images = dirs.parallelStream()
        .filter(f -> f.listFiles(Utils.IMAGE_FILTER) != null)
        .flatMap( f -> Arrays.stream( f.listFiles(Utils.IMAGE_FILTER)))
        .collect(Collectors.toCollection(ArrayList::new));
        loadImagesFromFiles(images);
        
    }
    public void startUp(){
        Collection<StartUpListener> listeners = new ArrayList<>(m_StartUpListener);
        m_StartUpListener.clear();
        m_StartUpListener = null;
        listeners.forEach( l -> l.onStartUp(this));
        m_CurrentTool.onInit(this, m_CurrentImage);
        listeners.clear();

        SwingUtilities.invokeLater( () -> {
            m_MainFrame.setVisible(true);
            showImage(m_CurrentImage);
        });
    }

    public boolean isShuffleActive(){
        return false;
    }

    public ImageHandler getImageHandler(){
        return m_ImageHandler;
    }

    public void addStartUpListener(StartUpListener listener){
        if(m_StartUpListener == null){
            System.err.println("No more listeners allowed");
            throw new RuntimeException("this time as error");
            //return;
        }
        m_StartUpListener.add(listener);
    }
    
    public void addNewMainImageListener(Consumer<AlgoImage> listener){
        m_NewMainImageListener.add(listener);
    }
    
    public void loadImagesCommand(){
        m_MainFrame.openImageFileChooser();
    }

    public void loadImages(Collection<File> files){
        System.out.println("loading files: " + files.size() );
        loadImageFromDirectory( 
            files.stream()
            .filter(File::isDirectory)
            .collect(Collectors.toList())
        );
        loadImagesFromFiles(
            files.stream()
            .filter(File::isFile)
            .collect(Collectors.toList())
        );
    }

    public static interface StartUpListener{
        public void onStartUp(Controller controller);
    }
    
    public void error(Exception e){
        e.printStackTrace(System.err);
    }
    
    public void error(String msg){
        System.err.println(msg);
    }
    
    public void showImage(AlgoImage image){
        m_MainFrame.setCenterImage(image);
        m_CurrentImage = image;
        m_CurrentTool.onImageChange(image);
        m_NewMainImageListener.forEach(l -> l.accept(image));
    }

    public ShuffleManager getShuffleManager(){
        return m_ShuffleManager;
    }

    public void toggleShuffle(){
        if(m_ShuffleManager.isRunning() ){
            m_ShuffleManager.endShuffle();
        }else{
            m_ShuffleManager.setConsumer( img -> {
                SwingUtilities.invokeLater( () -> m_MainFrame.setCenterImage(img) );
            });
            m_ShuffleManager.setCallback( () -> {
                SwingUtilities.invokeLater( () -> {
                    m_MainFrame.repaint();
                });
            });
            m_ShuffleManager.startShuffle();
        }
    }

    public void closeApp(){
        m_ShuffleManager.endShuffle();
        m_MainFrame.dispose();
    }

    public AlgoImage scaleImage(AlgoImage src,int width,int height){
        return new AlgoImage(m_MainFrame.createImage(src.getImageSource())
            .getScaledInstance(width, height, Image.SCALE_SMOOTH), width, height);
    }
    
    public void activateTool(ImageTool tool){
        if(m_CurrentTool != null){
            m_CurrentTool.onClose(this);
        }
        m_CurrentTool = tool;
        tool.onInit(this, m_CurrentImage);
    }
    
    public ImageTool getCurrentTool(){
        return m_CurrentTool;
    }
    
    public List<ImageTool> getTools(){
        return m_Tools;
    }
    
    public Color openColorChooser(){
        return m_MainFrame.openColorChooser();
    }
    
    public void addAllImagesToShuffle(){
        m_ImageHandler.getImages().stream()
            .filter(Utils.not(m_ShuffleManager::has))
            .forEach(m_ShuffleManager::toggleImage);
    }
    
    public void removeAllImagesFromShuffle() {
        if(m_ShuffleManager.isRunning()){
            return;
        }
        m_ShuffleManager.clear();
    }

    public void setSelectedImageArea(Rectangle rect) {
        m_ImageSelection = rect;
    }

    public void setSelectedImageArea(int x, int y, int w, int h) {
        setSelectedImageArea( new Rectangle(x, y, w, h));
    }

    public Rectangle getSelectedImageArea(){
        return m_ImageSelection;
    }

    public AlgoImage createNewBlankImage(int w, int h){
        final AlgoImage res = new AlgoImage(w, h);
        for(int i = 0; i < res.raw().length; ++i){
            res.set(i, 0xFFFFFFFF);
        }
        m_ImageHandler.addImage(res);
        return res;
    }

    public void clearImageOperations() {
        m_ImageOperations = Matrix.unit();
    }

    public Matrix getImageOperations() {
        return m_ImageOperations;
    }

    public Matrix applyToImageOperations(Matrix m){
        final Matrix res = Matrix.mult(m_ImageOperations, m);
        m_ImageOperations = res;
        return res;
    }

    public void setImageOperations(Matrix op) {
        m_ImageOperations = op;
    }

    public void applyOperationsTo(AlgoImage image){
        final AlgoImage target = image;
        Rectangle rect = m_ImageSelection;
        if(rect == null){
            rect = new Rectangle(target.getWidth(), target.getHeight());
        }
        if(target.hasBuffer()){
            target.resetToBuffer();
            target.apply(m_ImageOperations, rect);
            target.update();
            target.clearBuffer();
        }
        clearImageOperations();
    }
    
    public void analyseCurrentImage() {
        try {
            File dir = new File(System.getProperty("user.dir"));
            new ColorAnalyser(m_CurrentImage).toFile( new File(dir, "color-data.txt") );
            Desktop.getDesktop().open( dir);
        } catch(IOException e) {
            
        }
    }
    
    public void reduceCurrentImage(int cut) {
        ColorAnalyser analsyer = new ColorAnalyser(m_CurrentImage);
        Map<Integer, Integer> values = analsyer.substitution2(cut);
        
        if(m_CurrentImage.hasBuffer()){
            m_CurrentImage.resetToBuffer();
        }
        m_CurrentImage.createBuffer();
        
        for(int i = 0; i < m_CurrentImage.raw().length; ++i){
            final int color = m_CurrentImage.get(i);
            if(!values.containsKey(color)) {
                System.err.print("missing color entry");
                continue;
            }
            m_CurrentImage.set(i, values.get(color));
        }
        m_CurrentImage.update();
    }
}