package com.nitrous.iosched.client.component.scroll;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.nitrous.iosched.client.component.DeferredWindowResizeHandler;
import com.nitrous.iosched.client.event.Dispatcher;
import com.nitrous.iosched.client.event.RequestEnableHeaderAutoHideEvent;
import com.nitrous.iosched.client.event.RequestHideHeaderEvent;
import com.nitrous.iosched.client.event.SimpleEventHandler;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.view.config.Layers;

public class ScrollHeaderPanel implements ScrollingHeaderLayout {
	private AbsolutePanel layout;
	private ScrollPanel scroll;
	private Widget header;
	private int headerFullHeight;
	
	private Widget headerProxy;
	
	private VerticalPanel scrollContent;
	
	private int scrollY;
	private int headerPos;
	
	private Style headerStyle;
	private static int NEXT_ID = 0;
	private int myId;
	
	private boolean autoHideEnabled = true;
	
	public ScrollHeaderPanel(IsWidget header, int headerHeight) {
		this.myId = ++NEXT_ID;
		this.headerFullHeight = headerHeight;
		
		this.header = header.asWidget();
		this.headerStyle = this.header.getElement().getStyle();
		this.headerStyle.setHeight(headerHeight, Unit.PX);
		this.headerStyle.setWidth(100, Unit.PCT);
		
		this.headerStyle.setZIndex(Layers.HEADER_Z_INDEX);
		this.headerStyle.setPosition(Position.ABSOLUTE);
		
		this.headerProxy = new HTML();
		this.headerProxy.getElement().setId("header-proxy-"+myId);
		this.headerProxy.getElement().getStyle().setHeight(headerHeight, Unit.PX);
		this.headerProxy.getElement().getStyle().setWidth(1, Unit.PX);
		
		this.scrollContent = new VerticalPanel();
		this.scrollContent.setStyleName(IOSchedResources.INSTANCE.css().scrollContent());
		this.scrollContent.getElement().setId("scroll-content-"+myId);
		this.scrollContent.add(headerProxy);
		this.scrollContent.getElement().getStyle().setPosition(Position.ABSOLUTE);
		
		this.scroll = new ScrollPanel(scrollContent);
		this.scroll.setStyleName(IOSchedResources.INSTANCE.css().scrollPanel());
		this.scroll.getElement().setId("scroll-"+myId);
		this.scroll.getElement().getStyle().setZIndex(0);
		
		int windowWidth = Window.getClientWidth();
		this.scroll.setSize(windowWidth+"px", "100%");		
		this.scrollContent.setWidth(windowWidth+"px");	
		
		this.layout = new AbsolutePanel();
		this.layout.getElement().setId("scroll-header-panel-"+myId);
		this.layout.add(header, 0, 0);
		this.layout.add(scroll, 0, 0);
		
		this.scroll.addScrollHandler(new ScrollHandler() {
			@Override
			public void onScroll(ScrollEvent event) {
				onScrolled();
			}
		});
		
		new DeferredWindowResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				int windowWidth = Window.getClientWidth();
				scroll.setSize(windowWidth+"px", "100%");
				scrollContent.setWidth(windowWidth+"px");	
			}
		});
		
		Dispatcher.addHandler(RequestHideHeaderEvent.TYPE,  new SimpleEventHandler<RequestHideHeaderEvent>() {
			@Override
			public void onEvent(RequestHideHeaderEvent event) {
				showHeader(false, event.isAnimate());
			}
		});
		Dispatcher.addHandler(RequestEnableHeaderAutoHideEvent.TYPE,  new SimpleEventHandler<RequestEnableHeaderAutoHideEvent>() {
			@Override
			public void onEvent(RequestEnableHeaderAutoHideEvent event) {
				setAutoHideEnabled(event.isEnableAutoHide());
			}
		});
		
	}
	
	/**
	 * Enable or disable the auto-hiding of the header in response to scroll events.
	 * 
	 * @param enabled True to enable auto-hiding in response to scroll events, False to prevent auto-hiding of the header.
	 */
	public void setAutoHideEnabled(boolean enabled) {
		this.autoHideEnabled = enabled;
	}
	
	@Override
	public ScrollPanel getScrollPanel() {
		return scroll;
	}
 	
	@Override
	public void scrollToWidget(IsWidget widget, final AnimationCompletionCallback callback) {
		int cardTop = widget.asWidget().getAbsoluteTop();
		final int curHeaderPos = getHeaderTop();
		int curScrollPos = getScrollPanel().getVerticalScrollPosition();
		int cardDefaultTop = cardTop + curScrollPos;
		int scrollTo = cardDefaultTop - getHeaderBottom();
		
		setAutoHideEnabled(false);
		scrollTo(scrollTo, true, new AnimationCompletionCallback() {
			@Override
			public void onAnimationComplete() {
				Timer t = new Timer(){
					@Override
					public void run() {
						setHeaderTop(curHeaderPos);
						setAutoHideEnabled(true);
						if (callback != null) {
							callback.onAnimationComplete();
						}
					}
				};
				t.schedule(500);
			}
		});				
	}
	
	public void scrollTo(int scrollTo, boolean animate) {
		scrollTo(scrollTo, animate, null);
	}
	
	public void scrollTo(int scrollTo, boolean animate, final AnimationCompletionCallback callback) {
		if (animate) {
			new ScrollAnimation(scrollY, scrollTo, callback).run(500);
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
	
	private void onScrolled() {
		int newY = this.scroll.getVerticalScrollPosition();
		if (autoHideEnabled) {
			if (newY < scrollY) {
				// when scrolling up, scroll header into view
				if (headerPos < 0) {
					int dist = scrollY - newY;
					int newHeaderPos = headerPos + dist;
					newHeaderPos = Math.min(newHeaderPos, headerFullHeight);
					setHeaderTop(newHeaderPos);
				}			
			} else {
				// when scrolling down, hide header
				if (headerPos < headerFullHeight) {
					int dist = newY - scrollY;
					int newHeaderPos = headerPos - dist;
					newHeaderPos = Math.max(newHeaderPos, -headerFullHeight);
					setHeaderTop(newHeaderPos);
				}
			}
		}
		this.scrollY = newY;
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

	private class HideHeaderAnimation extends Animation {
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
			int newPos = 0 - ((int)(progress * headerFullHeight));
			if (newPos < headerPos) {
				setHeaderTop(newPos);
			}
		}
	}
	
	private class ShowHeaderAnimation extends Animation {
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
			int newPos = ((int)(progress * headerFullHeight));
			if (newPos > headerPos) {
				setHeaderTop(newPos);
			}
		}
	}
	
	public void showHeader(boolean show, boolean animate) {
		if (animate) {
			if (show) {
				// show if not already fully showing
				if (headerPos < 0) {
					new ShowHeaderAnimation().run(500);
				}
			} else {
				// hide if not already fully hidden
				if (headerPos > -headerFullHeight) {
					new HideHeaderAnimation().run(500);
				}
			}
		} else {
			setHeaderTop( show ? 0 : -headerFullHeight);
		}
	}
	
	/**
	 * @return The current position of the top of the header
	 */
	public int getHeaderTop() {
		return headerPos;
	}
	
	/**
	 * @return The current position of the bottom of the header
	 */
	public int getHeaderBottom() {
		return headerPos + headerFullHeight;
	}
	
	/**
	 * Set the current position of the top header
	 * @param pos The top position to be applied to the header
	 */
	public void setHeaderTop(int pos) {
		int newHeaderPos = Math.min(pos, 0);
		if (newHeaderPos != headerPos) {
			headerPos = newHeaderPos;
			headerStyle.setTop(headerPos, Unit.PX);
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
	public Widget asWidget() {
		return layout;
	}

	@Override
	public int getVerticalScrollPosition() {
		return scroll.getVerticalScrollPosition();
	}

	@Override
	public void setVerticalScrollPosition(int position) {
		scroll.setVerticalScrollPosition(position);
	}

}
