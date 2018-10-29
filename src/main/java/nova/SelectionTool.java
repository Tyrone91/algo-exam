package nova;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

public class SelectionTool extends AbstractMorphTool {

    private Point m_FirstClick;
    private AlgoImage m_PasteSource;

    private void clear(){
        m_PasteSource = null;
        controller().setSelectedImageArea(null);
        render(Matrix.unit(), true);
    }

    private void activateTool(Controller controller){
        controller.activateTool(this);
    }

    private void copy() {
        m_PasteSource = target();
    }

    private void paste() {
        Rectangle range = controller().getSelectedImageArea();
        System.out.println("Pasting");
        if(range == null){
            return;
        }
        if( m_PasteSource == null ) {
            return;
        }
        System.out.println("transfer" + range);
        for(int y = 0; y < range.height; ++y) {
            for(int x = 0; x < range.width; ++x){
                int px = range.x + x;
                int py = range.y + y;

                if(target().inRange(px, py) && m_PasteSource.inRange(px, py)){
                    final int val = m_PasteSource.getPx(px, py);
                    target().setPx(px, py, val);
                }
            }
        }
        target().clearBuffer();
        target().update();
    }

    @Override
    public void onMove(int x, int y) {
        target().resetToBuffer();
        int w = x - m_FirstClick.x;
        int h = y - m_FirstClick.y;
        controller().setSelectedImageArea(m_FirstClick.x, m_FirstClick.y, w, h);
        drawSelection();
    }

    @Override
    public void onPressed(int x, int y) {
        controller().applyOperations();
        if(target().hasBuffer()){
            target().resetToBuffer();
        }
        target().createBuffer();
        m_FirstClick = new Point(x,y);
    }

    @Override
    public void onReleased(int x, int y) {
        this.onMove(x, y);
        m_FirstClick = null;
    }

    @Override
    public String getDescription() {
        return "Select Tool";
    }

    @Override
    public String getName() {
        return "Select";
    }

    @Override
    public String getRepresentation() {
        return "#";
    }

    @Override
    public List<ToolOption> getToolOptions() {
        return Arrays.asList(
            ToolOption.of("select", this::activateTool),
            ToolOption.of("clear", ctrl -> this.clear())
        );
    }

    @Override
    public void initNavigationBarContext(QuickNavigationBar bar) {
        bar
            .addNavEntry("Select", "+", this::activateTool)
            .addNavEntry("Select", "-", ctrl -> this.clear())
            .addNavEntry("Select", "Copy", ctrl -> this.copy())
            .addNavEntry("Select", "Paste", ctrl -> this.paste())
            .update();
    }
}   