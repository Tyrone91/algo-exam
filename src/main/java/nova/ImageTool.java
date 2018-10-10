package nova;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public interface ImageTool {
    
    public static class ToolOption{
        
        private String m_Name;
        private Consumer<Controller> m_Action;
        
        public String getName() { return m_Name;}
        public Consumer<Controller> getAction() { return m_Action;}
        
        private ToolOption(String name, Consumer<Controller> action){
            m_Name = name;
            m_Action = action;
        }
        
        public static ToolOption of(String name, Consumer<Controller> action){
            return new ToolOption(name, action);
        }
    }

    public void onInit(Controller controller, AlgoImage currentImage);
    public void onClose(Controller controller);

    public void onImageChange(AlgoImage newImage);
    public default void onMove(int x, int y){};
    public default void onPressed(int x, int y){};
    public default void onReleased(int x, int y){}

    public String getName();
    public String getDescription();
    public String getRepresentation();
    
    public default void initNavigationBarContext(QuickNavigationBar bar) {}
    public default List<ToolOption> getToolOptions() { return Collections.emptyList(); };

}