package nova;

import java.util.function.Consumer;

import javax.swing.JMenuItem;

public final class UtilsUI {
    private UtilsUI() {}
    
    public static JMenuItem createItem(String text, Runnable callback){
        final JMenuItem item = new JMenuItem(text);
        item.addActionListener( event -> callback.run());
        return item;
    }
    
    public static JMenuItem createItem(String text, Consumer<JMenuItem> callback){
        final JMenuItem item = new JMenuItem(text);
        item.addActionListener( event -> callback.accept(item));
        return item;
    }
}
