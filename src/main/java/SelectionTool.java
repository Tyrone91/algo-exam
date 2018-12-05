

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.List;

public class SelectionTool extends AbstractMorphTool {

    private Point m_FirstClick;
    private Matrix m_PasteOperations;
    private AlgoImage m_PasteData;
    private Rectangle m_PasteRectangle;
    private boolean m_PasteMode = false;

    private void clear(){
        m_PasteMode = false;
        controller().applyOperationsTo(target());
        render(Matrix.unit(), false);
        controller().setSelectedImageArea(null);
        
    }

    private void activateTool(Controller controller){
        controller.activateTool(this);
    }

    private void copy() {
        m_PasteMode = false;
        Rectangle range = controller().getSelectedImageArea();
        m_PasteOperations = Matrix.of(controller().getImageOperations());
        m_PasteRectangle = new Rectangle(range);
        
        m_PasteData = new AlgoImage(range.width, range.height);
        if(!target().hasBuffer() ) {
            target().createBuffer();
        } else {
            target().resetToBuffer();
        }
        target().apply(m_PasteOperations, range);
        for(int y = 0; y < range.height; ++y) {
            for(int x = 0; x < range.width; ++x) {
                m_PasteData.setPx(x, y, target().getBufferData(range.x + x, range.y +y));
            }
        }
        
    }
    
    private void paste() {
        Rectangle range = m_PasteRectangle;
        paste(-range.x, -range.y);
    }
    
    private void paste(int positionX, int positionY) {
        Rectangle range = m_PasteRectangle;
        AlgoImage source = m_PasteData;
        source.createBuffer();
        AlgoImage target = target();
        if(target.hasBuffer()){
            target.resetToBuffer();
        } else {            
            target.createBuffer();
        }
        
        
        target.apply(m_PasteOperations, range, source, -range.x, -range.y);
        target.createBuffer();
        target.update();
        controller().setSelectedImageArea(null);
        
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
        controller().applyOperationsTo(target());
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
            .addNavEntry("Select", "Sel.", this::activateTool)
            .addNavEntry("Select", "X", ctrl -> this.clear())
            .addNavEntry("Select", "Copy", ctrl -> this.copy())
            .addNavEntry("Select", "Paste", ctrl -> {this.activateTool(ctrl); this.paste();})
            .update();
    }
}   