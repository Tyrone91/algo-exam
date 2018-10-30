package nova;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

public class RotateTool extends AbstractMorphTool {

    private Point m_FirstClick;
    private Matrix m_LastCalc = Matrix.unit();


    private void selectRotateMode(){

    }

    private void rotate(float alpha){
        int xtrans = m_FirstClick.x;
        int ytrans = m_FirstClick.y;
        
        Matrix m = Matrix.mult(
            //controller().getImageOperations(),
            Matrix.inverseTranslate(-xtrans, -ytrans),
            Matrix.inverseRotate(alpha),
            Matrix.inverseTranslate(xtrans, ytrans));
        m_LastCalc = m;
        render(m,true);
    }

    @Override
    public String getName() {
        return "Rotate";
    }

    @Override
    public String getDescription() {
        return "Morph Tool";
    }

    @Override
    public String getRepresentation() {
        return "🗘";
    }

    @Override
    public List<ToolOption> getToolOptions() {
        return Arrays.asList(
            ToolOption.of("Rotate", (crtl) -> selectRotateMode())
        );
    }

    @Override
    public void onMove(int x, int y) {
        if(m_FirstClick != null){
            m_LastCalc = Matrix.unit();
            int d = (x - m_FirstClick.x);
            double alpha  = d *  Math.PI / 180.0;
            rotate((float)alpha);
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
        bar.addNavEntry("Morph", "🗘" , (ctrl) -> {
            ctrl.activateTool(this);
        });
    }
}