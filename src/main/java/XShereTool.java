

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

public class XShereTool extends AbstractMorphTool {
    
    private Point m_FirstClick;
    private Matrix m_LastCalc = Matrix.unit();

    private void shere(float scale){
        Matrix m = Matrix.inverseXShear(scale);
        m_LastCalc = m;
        render(m,true);
    }

    @Override
    public String getName() {
        return "X-Shere";
    }

    @Override
    public String getDescription() {
        return "Morph Tool";
    }

    @Override
    public String getRepresentation() {
        return "X-Shere";
    }

    @Override
    public List<ToolOption> getToolOptions() {
        return Arrays.asList(
            ToolOption.of(getRepresentation(), (ctrl) -> ctrl.activateTool(this))
        );
    }

    @Override
    public void onMove(int x, int y) {
        if(m_FirstClick != null){
            m_LastCalc = Matrix.unit();
            float d =  (float)((x - m_FirstClick.x) * 0.015);
            if(d == 0){
                shere(0);
            }
            shere(-1/d);
        }
    }


    @Override
    public void onPressed(int x, int y) {
        if(m_FirstClick == null){
            m_FirstClick = new Point(x,y);
        }
    }

    @Override
    public void onReleased(int x, int y) {
        m_FirstClick = null;
        controller().applyToImageOperations(m_LastCalc);
    }

    @Override
    public void initNavigationBarContext(QuickNavigationBar bar) {
        bar.addNavEntry("Morph", getRepresentation() , (ctrl) -> {
            ctrl.activateTool(this);
        });
    }
}
