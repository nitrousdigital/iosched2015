package com.nitrous.iosched.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

public interface IOSchedResources extends ClientBundle {
	public static final IOSchedResources INSTANCE = GWT.create(IOSchedResources.class);
	
	interface IOSchedStyle extends CssResource {
		String header();
		String topToolbar();
		String headerTabs();
		
		int cardLeftRightMargin();
		String card();
		String cardExpired();
		
		String popupDialog();
		String popupDialogBottomButtonBar();
		String popupDialogHeaderBar();
		String popupDialogActionButton();
		String popupDialogHeaderText();
		
		String sessionDetailTextContentPanel();
		String sessionDetailTitle();
		String sessionDetailTime();
		String sessionDetailTags();
		String sessionDetailDescription();
		String sessionDetailSpeakersCard();
		String sessionDetailSpeakersHeader();
		String sessionDetailSpeakersList();
		String sessionDetailSpeakersFace();
		String sessionDetailSpeakersName();
		String sessionDetailSpeakersCompany();
		String sessionDetailSpeakersBio();
		
		String browseSessionsButton();
		
		String scrollPanel();
		String scrollContent();
		String scheduleExplorerGrid();
		
		int cardTitleButtonHorizontalPadding();
		String cardTitleButton();
		
		String cardTitleRow();
		int sessionTitleHorizontalMargin();
		String sessionTitle();
		String sessionTimeText();
		String sessionTimeRow();
		String sessionMenuHeaderText();
		
		int sessionCheckBoxLeftMargin();
		int sessionCheckBoxRightMargin();
		String sessionCheckBox();
		String sessionRow();
		String sessionRowExpired();
		
		String popupMenuSeparator();
		String popupMenuItem();
		String popupMenuCloseButton();
		String filterPopupMenuHeader();
		
		String applicationMenuItem();
		
		String noSessionsMatchFilterMessage();
		
		String sessionConflictStatusIcon();
		
		String topToolbarIOIcon();
		int topToolbarIOIconLeftRightPadding();
		
		int headerBottomBorderWidth();
	}
	
	@Source("profile_placeholder.png")
	ImageResource profilePlaceholder();
	
	@Source("back.png")
	ImageResource arrowBack();
	
	@Source("io15-color.png")
	ImageResource io15billBoard();
	
	@Source("check16blue.png")
	ImageResource check16blue();
	
	@Source("check16red.png")
	ImageResource check16red();
	
	@Source("check16gray.png")
	ImageResource check16gray();
	
	@Source("iosched.css")
	IOSchedStyle css();
	
	@Source("io15.png")
	ImageResource io15headerIcon();
	
}
