package nova;

import java.util.function.Consumer;

import javax.swing.JButton;
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

    public static JButton createBttn(String text, Runnable action){
        return createBttn(text, bttn -> action.run());
    }

    public static JButton createBttn(String text, Consumer<JButton> callback){
        final JButton bttn = new JButton(text);
        bttn.addActionListener( action -> callback.accept(bttn));
        return bttn;
    }
}
