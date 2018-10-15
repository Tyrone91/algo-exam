package nova;

import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import javax.swing.text.DefaultEditorKit.CutAction;

public class MorphTool implements ImageTool{

    private Controller m_Controller;
    private AlgoImage m_Target;
    private Point m_FirstClick;
    private Matrix m_Calc = Matrix.unit();
    private boolean m_RotateInCenter = true;

    private void selectRotateMode(){

    }

    private void rotate(float alpha){
        long start  = System.currentTimeMillis();
        int xtrans = 0;
        int ytrans = 0;
        if(m_RotateInCenter){
            xtrans = m_Target.getWidth()/2;
            ytrans = m_Target.getHeight()/2;
        }
        Matrix m = Matrix.mult(
            m_Calc,
            Matrix.inverseTranslate(-xtrans, -ytrans),
            Matrix.inverseRotate(alpha),
            Matrix.inverseTranslate(xtrans, ytrans));
        m_Calc = m;
        
        m_Target.resetToBuffer();
        int startX = 0;
        int startY = 0;
        int endX = m_Target.getWidth();
        int endY = m_Target.getHeight();
        Vector3 vec = Vector3.of(0,0,1);
        for(int y = startY; y < endY; ++y){
            for(int x = startX; x < endX; ++x){
                vec.setX(x).setY(y);
                Vector3 res = Matrix.mult(m, vec);
                if(!m_Target.inRange( (int)res.getX(), (int)res.getY())){
                    m_Target.setPx(x, y, 0xFFFFFFFF);
                }else {
                    m_Target.setPx(x, y, m_Target.getBufferData((int)res.getX(), (int)res.getY()));
                }
            }
        }
        m_Target.update();
        System.out.println( System.currentTimeMillis() - start + "ms");
    }

    public MorphTool(){

    }

    @Override
    public String getName() {
        return "Morph";
    }

    @Override
    public String getDescription() {
        return "Morph Tool";
    }

    @Override
    public void onClose(Controller controller) {}

    @Override
    public void onInit(Controller controller, AlgoImage currentImage) {
        m_Controller = controller;
        m_Target = currentImage;
        m_Target.createBuffer();
    }

    @Override
    public void onImageChange(AlgoImage newImage) {
        m_Target = newImage;
        m_Target.createBuffer();
    }

    @Override
    public String getRepresentation() {
        return "Mop";
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
            int d = x - m_FirstClick.x;
            float alpha  = d/100f;
            rotate(alpha);
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
    }

    @Override
    public void initNavigationBarContext(QuickNavigationBar bar) {
        bar.addNavEntry("Rotate", "+" , (ctrl) -> {
            ctrl.activateTool(this);
        });
    }
}