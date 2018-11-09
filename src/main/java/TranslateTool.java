

import java.awt.Point;

public class TranslateTool extends AbstractMorphTool {

    private Point m_FirstClick;
    private Matrix m_Last = Matrix.unit();

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getRepresentation() {
        return "🡘 🡙";
    }

    @Override
    public void onPressed(int x, int y) {
        m_FirstClick = new Point(x,y);
    }

    @Override
    public void onReleased(int x, int y) {
        controller().applyToImageOperations(m_Last);
    }

    @Override
    public void onMove(int x, int y) {
        final int dx = m_FirstClick.x - x;
        final int dy = m_FirstClick.y - y;
        final Matrix m = Matrix.inverseTranslate(-dx, -dy);
        render( m, true);
        m_Last = m;
    }

    @Override
    public void initNavigationBarContext(QuickNavigationBar bar) {
        bar
            .addNavEntry("Morph", this.getRepresentation(), ctrl -> ctrl.activateTool(this))
            .update();
    }


}