package nova;

import java.awt.Rectangle;

public abstract class AbstractMorphTool implements ImageTool {

    private AlgoImage m_TargetImage;
    private Controller m_Controller;

    protected AlgoImage target() {
        return m_TargetImage;
    }

    protected Controller controller(){
        return m_Controller;
    }

    protected void render(Matrix op, boolean drawSelection){
        Rectangle rect = controller().getSelectedImageArea();
        if(rect != null ){
            render(op,rect, drawSelection);
        } else {
            render(op, new Rectangle(0, 0, target().getWidth(), target().getHeight()), drawSelection);
        }
    }

    protected void render(Matrix op, Rectangle range, boolean drawSelection) {
        final Matrix former = controller().getImageOperations();
        final Rectangle rect = controller().getSelectedImageArea();

        target().resetToBuffer();
        target().apply( Matrix.mult(former, op), range);

        if(drawSelection && rect != null) {
            Bresenham.selection(target(),range);
        }
        target().update();
    }

    protected void drawSelection(){
        final Rectangle rect = controller().getSelectedImageArea();
        if(rect != null) {
            Bresenham.selection(target(), rect);
            target().update();
        }
    }

    @Override
    public void onImageChange(AlgoImage newImage) {
        controller().applyOperations();
        m_TargetImage = newImage;
        if( target().hasBuffer() ){
            target().resetToBuffer();
        } else {
            target().createBuffer();
        }
        render(Matrix.unit(), true);
    }

    @Override
    public void onInit(Controller controller, AlgoImage currentImage) {
        m_Controller = controller;
        m_TargetImage = currentImage;

        if(m_TargetImage.hasBuffer()){
            target().resetToBuffer();
        } else {
            target().createBuffer();
        }
        render(Matrix.unit(), true);
    }

    @Override
    public void onClose(Controller controller) {
        if(m_TargetImage.hasBuffer()){
            m_TargetImage.resetToBuffer();
        }
        //controller.applyOperations();
    }
}