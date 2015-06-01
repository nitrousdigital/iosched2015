package com.nitrous.iosched.client.view.importexport;

import java.util.ArrayList;

import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.nitrous.iosched.client.resources.IOSchedResources;
import com.nitrous.iosched.client.view.config.Layers;
import com.nitrous.polygwt.client.component.Dimension;
import com.nitrous.polygwt.client.component.HtmlRippleButton;
import com.nitrous.polygwt.client.component.RippleButton;

public abstract class AbstractImportExportDialog extends PopupPanel {
	private static final int TEXT_AREA_MARGIN = 20;
	private static final int INSTRUCTIONS_PADDING = 20;
	private Dimension preferredSize = new Dimension(800,600);

	private DockLayoutPanel layout;
	
	private HTML title;
	private HorizontalPanel buttonPanel;
	private TextArea textArea;
	
	private static final int HEADER_HEIGHT = 31;
	private static final int BUTTON_BAR_HEIGHT = 10 + RippleButton.DEFAULT_HEIGHT_WITH_PADDING;
	private static final String BUTTON_WIDTH = "120px";
	
	private HTML instructions;
	
	protected ArrayList<HandlerRegistration> reg = new ArrayList<HandlerRegistration>();
	
	public AbstractImportExportDialog() {
		this.setStyleName(IOSchedResources.INSTANCE.css().popupDialog());
		setAnimationEnabled(true);
		setAnimationType(AnimationType.CENTER);
		setAutoHideEnabled(false);
		setGlassEnabled(true);
		
		this.layout = new DockLayoutPanel(Unit.PX);
		
		// top title
		HorizontalPanel titlePanel = new HorizontalPanel();
		titlePanel.setStyleName(IOSchedResources.INSTANCE.css().popupDialogHeaderBar());
		title = new HTML();
		title.setStyleName(IOSchedResources.INSTANCE.css().popupDialogHeaderText());
		title.setWidth("100%");
		titlePanel.add(title);
		titlePanel.setWidth("100%");
		this.layout.addNorth(titlePanel, HEADER_HEIGHT);
		
		// bottom buttons
		buttonPanel = new HorizontalPanel();
		buttonPanel.setHeight(BUTTON_BAR_HEIGHT + "px");
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_RIGHT);
		HorizontalPanel south = new HorizontalPanel();
		south.setStyleName(IOSchedResources.INSTANCE.css().popupDialogBottomButtonBar());
		south.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		south.setWidth("100%");
		south.setHeight(BUTTON_BAR_HEIGHT + "px");
		south.add(buttonPanel);
		this.layout.addSouth(south, BUTTON_BAR_HEIGHT);
		
		// center text
		this.textArea = new TextArea();
		this.textArea.getElement().getStyle().setMargin(TEXT_AREA_MARGIN, Unit.PX);
		
		VerticalPanel center = new VerticalPanel();
		instructions = new HTML();
		instructions.getElement().getStyle().setPaddingTop(INSTRUCTIONS_PADDING, Unit.PX);
		instructions.getElement().getStyle().setPaddingRight(INSTRUCTIONS_PADDING, Unit.PX);
		instructions.getElement().getStyle().setPaddingLeft(INSTRUCTIONS_PADDING, Unit.PX);
		center.add(instructions);
		center.add(textArea);
		this.layout.add(center);
		
		this.add(layout);
//		this.layout.setSize("100%", "100%");
	}
	
	protected void unregister() {
		for (HandlerRegistration r : reg) {
			r.removeHandler();
		}
		reg.clear();
	}
	
	protected void register() {
		unregister();
		reg.add(RootPanel.get().addDomHandler(new KeyDownHandler(){
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ESCAPE) {
					hide();
				}
			}}, KeyDownEvent.getType()));
		
		reg.add(Window.addResizeHandler(new ResizeHandler() {			
			@Override
			public void onResize(ResizeEvent event) {
				doLayout();
			}
		}));
	}
	
	public void showDialog() {
		refreshContent();
		doLayoutAndShow();
	}
	
	protected TextArea getTextArea() {
		return textArea;
	}
	
	private void doLayoutAndShow() {
		setAnimationEnabled(false);
		show();		
		resizeComponents(false);
		hide();
		setAnimationEnabled(true);		
		super.center();
	}
	
	private void resizeComponents(boolean center) {
		int windowWidth = Window.getClientWidth();
		int windowHeight = Window.getClientHeight();
		int dialogWidth = Math.min(windowWidth - 20, preferredSize.width);
		int dialogHeight = Math.min(windowHeight - 20, preferredSize.height);
		int instructionHeight = instructions.getElement().getOffsetHeight();
		int textAreaWidth = dialogWidth - ((TEXT_AREA_MARGIN * 2) + 10);
		int textAreaHeight = dialogHeight - 
				(HEADER_HEIGHT
				+ (INSTRUCTIONS_PADDING * 4) 
				+ instructionHeight 
				+ BUTTON_BAR_HEIGHT);
		this.textArea.setPixelSize(textAreaWidth, textAreaHeight);
		this.setPixelSize(dialogWidth, dialogHeight);
		if (center) {
			center();
		}
	}
	
	private void doLayout() {
		resizeComponents(true);
	}
	
	/**
	 * Refresh the dialog content
	 */
	protected void refreshContent() {
	}
	
	@Override
	public void show() {
		super.show();
		register();
	}
	
	@Override
	public void hide(boolean auto) {
		super.hide(auto);
		unregister();
	}
	
	protected HtmlRippleButton createActionButton(String label) {
		final HtmlRippleButton button = new HtmlRippleButton(label);
		button.asWidget().setWidth(BUTTON_WIDTH);
		button.getRippleCanvas().getElement().getStyle().setZIndex(Layers.POPUP_RIPPLE_Z_INDEX);
		button.asWidget().setStyleName(IOSchedResources.INSTANCE.css().popupDialogActionButton());
		Style style = button.asWidget().getElement().getStyle();
		style.setPosition(Position.RELATIVE);
		return button;
	}
		
	protected HtmlRippleButton addCloseButton() {
		return addCloseButton("Close");
	}
	protected HtmlRippleButton addCloseButton(String label) {
		HtmlRippleButton closeButton = createActionButton(label);
		closeButton.addClickHandler(new com.google.gwt.event.dom.client.ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				delayedHide();
			}
		});		
		addButton(closeButton);
		return closeButton;
	}
	
	protected void delayedHide() {
		new Timer(){
			@Override
			public void run() {
				hide();
			}
		}.schedule(500);
	}
	
	protected HTML getInstructions() {
		return this.instructions;
	}
	
	public void setHeaderText(String title) {
		this.title.setHTML(title);
	}
	
	protected void setInstructions(String html) {
		this.instructions.setHTML(html);
	}
	
	protected void setText(String text) {
		textArea.setText(text);
	}
	
	protected void addButton(IsWidget button) {
		buttonPanel.add(button);
	}
	
}
