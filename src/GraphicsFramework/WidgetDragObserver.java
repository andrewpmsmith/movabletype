package GraphicsFramework;

public interface WidgetDragObserver {
	public void onDragStart(Widget w);
	public void onDragEnd(Widget w);
	public void onDrag(Widget w, int x, int y);
}
