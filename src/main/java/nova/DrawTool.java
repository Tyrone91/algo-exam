package nova;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;

public class DrawTool implements ImageTool {
    
    private AlgoImage m_Target;
    private Controller m_Controller;
    private boolean m_isDragging = false;
    private int m_DragStartX, m_DragStartY;
    private Color m_Color1 = Color.BLACK;
    private Color m_Color2 = Color.BLACK;
    
    private JButton m_BttnColor1;
    private JButton m_BttnColor2;
    
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
            Bresenham.line(m_Target, m_DragStartX, m_DragStartY, x, y, m_Color1.getRGB(), m_Color2.getRGB());
            m_Target.getImageSource().newPixels();
        }
    }

    @Override
    public void onPressed(int x, int y) {
        m_Target.createBuffer();
        m_isDragging = true;
        m_DragStartX = x;
        m_DragStartY = y;
    }

    @Override
    public void onReleased(int x, int y) {
        m_isDragging = false;
        m_Target.clearBuffer();
        
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
            .addNavEntry("Draw", "---", () -> {})
            .addNavEntry("Draw", "●", () -> {})
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
