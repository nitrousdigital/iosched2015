package com.nitrous.iosched.client.component.scroll;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.view.config.Layers;

/**
 * A simple scroll implementation where the header is scrolled as part of the scrolling content.
 * This is used in place of the ScollHeaderPanel to improve performance.
 * 
 * @author nitrousdigital
 */
public class SimpleScrollHeaderPanel implements ScrollingHeaderLayout {
	private ScrollPanel scroll;
	private Widget header;
	private VerticalPanel scrollContent;
	
	private Style headerStyle;
	private static int NEXT_ID = 0;
	private int myId;
	
	private DockLayoutPanel layout;
	private int headerHeight;
	
	public SimpleScrollHeaderPanel(IsWidget header, int headerHeight) {
		this.myId = ++NEXT_ID;
		this.headerHeight = headerHeight;
		
		this.header = header.asWidget();
		this.headerStyle = this.header.getElement().getStyle();
		this.headerStyle.setHeight(headerHeight, Unit.PX);
		this.headerStyle.setWidth(100, Unit.PCT);
		
		this.headerStyle.setZIndex(Layers.HEADER_Z_INDEX);
		
		this.scrollContent = new VerticalPanel();
		this.scrollContent.setStyleName(IOSchedResources.INSTANCE.css().scrollContent());
		this.scrollContent.getElement().setId("scroll-content-"+myId);
		this.scrollContent.setWidth("100%");
		
		this.scroll = new ScrollPanel(scrollContent);
		//this.scroll.setStyleName(IOSchedResources.INSTANCE.css().scrollPanel()); // style for iOS performance touch scrolling
		this.scroll.getElement().setId("scroll-"+myId);
		this.scroll.getElement().getStyle().setZIndex(0);
		this.scroll.setSize("100%", "100%");
		
//		int windowWidth = Window.getClientWidth();
//		this.scroll.setSize(windowWidth+"px", "100%");		
//		this.scrollContent.setWidth(windowWidth+"px");	

		this.layout = new DockLayoutPanel(Unit.PX);
		this.layout.addNorth(header, headerHeight);
		this.layout.add(scroll);
		
//		new DeferredWindowResizeHandler(new ResizeHandler() {
//			@Override
//			public void onResize(ResizeEvent event) {
//				int windowWidth = Window.getClientWidth();
////				scroll.setSize(windowWidth+"px", "100%");
//				scrollContent.setWidth(windowWidth+"px");	
//			}
//		});
	}
	
	@Override
	public ScrollPanel getScrollPanel() {
		return scroll;
	}
	
	@Override
	public void scrollToWidget(IsWidget widget, final AnimationCompletionCallback callback) {
		int cardTop = widget.asWidget().getAbsoluteTop();
		int curScrollPos = getScrollPanel().getVerticalScrollPosition();
		int cardDefaultTop = cardTop + curScrollPos;
		int scrollTo = cardDefaultTop - headerHeight;
		
		scrollTo(scrollTo, true, new AnimationCompletionCallback() {
			@Override
			public void onAnimationComplete() {
				if (callback != null) {
					callback.onAnimationComplete();
				}
			}
		});				
	}		
	
	public void scrollTo(int scrollTo, boolean animate, final AnimationCompletionCallback callback) {
		if (animate) {
			new ScrollAnimation(this.scroll.getVerticalScrollPosition(), scrollTo, callback).run(500);
		} else {
			this.scroll.setVerticalScrollPosition(scrollTo);
			if (callback != null) {
				Scheduler.get().scheduleFinally(new ScheduledCommand() {
					@Override
					public void execute() {
						callback.onAnimationComplete();
					}
				});
			}
		}
	}
	
	private class ScrollAnimation extends Animation {
		private int start;
		private int dist;
		private AnimationCompletionCallback callback;
		
		public ScrollAnimation(int start, int end, AnimationCompletionCallback callback) {
			this.start = start;
			this.dist = end - start;
			this.callback = callback;
		}
		
		/**
		 * Called when the animation should be updated.
		 * 
		 * The value of progress is between 0.0 and 1.0 (inclusive) (unless you
		 * override the {@link #interpolate(double)} method to provide a wider
		 * range of values). There is no guarantee that
		 * {@link #onUpdate(double)} is called with 0.0 or 1.0. If you need to
		 * perform setup or tear down procedures, you can override
		 * {@link #onStart()} and {@link #onComplete()}.
		 * 
		 * @param progress
		 *            a double, normally between 0.0 and 1.0 (inclusive)
		 */
		@Override
		protected void onUpdate(double progress) {
			int newPos = start + (int)(dist * progress);
			scroll.setVerticalScrollPosition(newPos);
			
			if (progress == 1.0 && callback != null) {
				Scheduler.get().scheduleFinally(new ScheduledCommand() {
					@Override
					public void execute() {
						callback.onAnimationComplete();				
					}
				});
			}
		}
	}

	/**
	 * Get the scrolling container
	 * @return The container to which scrollable content should be added
	 */
	@Override
	public VerticalPanel getScrollContainer() {
		return scrollContent;
	}
	
	@Override
	public int getVerticalScrollPosition() {
		return scroll.getVerticalScrollPosition();
	}

	@Override
	public void setVerticalScrollPosition(int position) {
		scroll.setVerticalScrollPosition(position);
	}

	@Override
	public Widget asWidget() {
		return layout;
	}

}
