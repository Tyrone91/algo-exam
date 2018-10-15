package nova;

import java.awt.Color;
import java.awt.Point;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;

public class DrawTool implements ImageTool {
    
    private AlgoImage m_Target;
    private Controller m_Controller;
    private boolean m_isDragging = false;
    private Color m_Color1 = Color.BLACK;
    private Color m_Color2 = Color.BLACK;

    private Point m_FirstClick = null;
    private Point m_SecondClick = null;
    
    private JButton m_BttnColor1;
    private JButton m_BttnColor2;

    private void line(int x1, int y1, int x2, int y2){
        Bresenham.line(m_Target, x1, y1, x2, y2, m_Color1.getRGB(), m_Color2.getRGB());
        m_Target.getImageSource().newPixels();
    }
    
    private void openColorSelectorColor1(){
        m_Color1 = m_Controller.openColorChooser();
        m_BttnColor1.setBackground(m_Color1);
    }
    
    private void openColorSelectorColor2(){
        m_Color2 = m_Controller.openColorChooser();
        m_BttnColor2.setBackground(m_Color2);
    }

    @Override
    public void onInit(Controller controller, AlgoImage currentImage) {
        m_Target = currentImage;
        m_Controller = controller;
    }

    @Override
    public void onClose(Controller controller) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onImageChange(AlgoImage newImage) {
        m_Target = newImage;
    }

    @Override
    public void onMove(int x, int y) {
        if(m_isDragging){
            m_Target.resetToBuffer();
            line(m_FirstClick.x, m_FirstClick.y, x, y);
            m_SecondClick = new Point(x,y);
        }
    }

    @Override
    public void onPressed(int x, int y) {
        m_isDragging = true;
        if(m_FirstClick == null) {
            m_Target.createBuffer();
            m_FirstClick = new Point(x,y);
        } else {
            m_SecondClick = new Point(x,y);
        }
    }

    @Override
    public void onReleased(int x, int y) {
        m_isDragging = false;
        if(m_FirstClick != null && m_SecondClick != null){

            line(m_FirstClick.x, m_FirstClick.y, m_SecondClick.x, m_SecondClick.y);
            m_Target.clearBuffer();

            m_FirstClick = null;
            m_SecondClick = null;
        }
    }

    @Override
    public String getName() {
        return "Draw";
    }

    @Override
    public String getDescription() {
        return "Test";
    }

    @Override
    public String getRepresentation() {
        return "---";
    }
    
    @Override
    public void initNavigationBarContext(QuickNavigationBar bar) {
        m_BttnColor1 = UtilsUI.createBttn("", (bttn) -> {
            openColorSelectorColor1();
        });
        m_BttnColor1.setBackground(m_Color1);
        
        m_BttnColor2 = UtilsUI.createBttn("", (bttn) -> {
            openColorSelectorColor2();
        });
        m_BttnColor2.setBackground(m_Color2);
        
        bar
            .addNavEntry("Draw", "---", (ctrl) -> { ctrl.activateTool(this);})
            .addNavEntry("Draw", "●", (ctrl) -> {})
            .addNavEntry("Draw", m_BttnColor1)
            .addNavEntry("Draw", m_BttnColor2)
            
            .update();
    }
    
    
    @Override
    public List<ToolOption> getToolOptions() {
        return Arrays.asList(
                ToolOption.of("Change color 1", ctr -> openColorSelectorColor1()),
                ToolOption.of("Change color 2", ctr -> openColorSelectorColor2())
        );
    }

}
