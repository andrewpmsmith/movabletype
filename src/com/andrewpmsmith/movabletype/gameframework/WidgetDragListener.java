package com.andrewpmsmith.movabletype.gameframework;

/**
 * The interface used by the RenderSurfce to delegate drag events.
 * 
 * @author Andrew Smith
 */
public interface WidgetDragListener {

	public void onDragStart(Widget widget);

	public void onDragEnd(Widget widget);

	public void onDrag(Widget widget, int x, int y);

}
