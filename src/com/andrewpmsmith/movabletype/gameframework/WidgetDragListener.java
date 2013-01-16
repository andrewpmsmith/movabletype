package com.andrewpmsmith.movabletype.gameframework;

public interface WidgetDragListener {
	public void onDragStart(Widget widget);
	public void onDragEnd(Widget widget);
	public void onDrag(Widget widget, int x, int y);
}
