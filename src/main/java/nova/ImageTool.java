package nova;

public interface ImageTool {

    public void onInit(Controller controller, AlgoImage currentImage);
    public void onClose(Controller controller);

    public void onImageChange(AlgoImage newImage);
    public void onMove(int x, int y);
    public void onPressed(int x, int y);
    public void onReleased(int x, int y);

    public String getName();
    public String getDescription();
    public String getRepresentation();

}