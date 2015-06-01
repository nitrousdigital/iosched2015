package com.nitrous.iosched.client.component.polymer;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HTML;

/**
 * A wrapper for a polymer core-image
 * 
 * @author nitrousdigital
 *
 */
public class CoreImage extends HTML {
	public enum Sizing {
		/** image takes natural size */
		DEFAULT(null),

		/** image is cropped in order to fully cover the bounds of the element */
		COVER("cover"),

		/**
		 * contain (full aspect ratio of the image is contained within the
		 * element and letterboxed
		 */
		CONTAIN("contain");
		private Sizing(String value) {
			this.value = value;
		}

		private String value;
	}

	/**
	 * 
	 * @param width
	 *            The width of the image or null
	 * @param height
	 *            The height of the image or null
	 * @param srcUrl
	 *            The source URL of the image to load
	 * @param placeholder
	 *            The optional placeholder or null. This image will be used as a
	 *            background/placeholder until the src image has loaded.
	 * @param size
	 *            The size option or null for the natural size
	 * @param fade
	 *            True to fade in
	 * @param preload
	 *            When true, any change to the src property will cause the
	 *            placeholder image (or background color, if no placeholder is
	 *            specified) to be shown until the image has loaded.
	 */
	public CoreImage(String width, String height, String srcUrl,
			ImageResource placeholder, Sizing size, boolean fade,
			boolean preload) {
		this(width, height, srcUrl, placeholder, size, fade, preload, true);
	}

	/**
	 * 
	 * @param width
	 *            The width of the image or null
	 * @param height
	 *            The height of the image or null
	 * @param srcUrl
	 *            The source URL of the image to load
	 * @param placeholder
	 *            The optional placeholder or null. This image will be used as a
	 *            background/placeholder until the src image has loaded.
	 * @param size
	 *            The size option or null for the natural size
	 * @param fade
	 *            True to fade in
	 * @param preload
	 *            When true, any change to the src property will cause the
	 *            placeholder image (or background color, if no placeholder is
	 *            specified) to be shown until the image has loaded.
	 * @param load
	 *            True to load the image from srcUrl, false to show the
	 *            placeholder without attempting to load.
	 */
	public CoreImage(String width, String height, String srcUrl,
			ImageResource placeholder, Sizing size, boolean fade,
			boolean preload, boolean load) {
		this.setHTML(toHTML(width, height, srcUrl, placeholder, size, fade,
				preload));
	}

	/**
	 * 
	 * @param style
	 *            The style or null
	 * @param srcUrl
	 *            The source URL of the image to load
	 * @param placeholder
	 *            The optional placeholder or null. This image will be used as a
	 *            background/placeholder until the src image has loaded.
	 * @param size
	 *            The size option or null for the natural size
	 * @param fade
	 *            True to fade in
	 * @param preload
	 *            When true, any change to the src property will cause the
	 *            placeholder image (or background color, if no placeholder is
	 *            specified) to be shown until the image has loaded.
	 * @param load
	 *            True to load the image from srcUrl, false to show the
	 *            placeholder without attempting to load.
	 */
	public CoreImage(String style, String srcUrl,
			ImageResource placeholder, Sizing size, boolean fade,
			boolean preload, boolean load) {
		this.setHTML(toHTML(style, srcUrl, placeholder, size, fade,
				preload, load));
	}
	
	/**
	 * Returns a <core-image> definition
	 * 
	 * @param width
	 *            The width of the image or null
	 * @param height
	 *            The height of the image or null
	 * @param srcUrl
	 *            The source URL of the image to load
	 * @param placeholder
	 *            The optional placeholder or null. This image will be used as a
	 *            background/placeholder until the src image has loaded.
	 * @param size
	 *            The size option or null for the natural size
	 * @param fade
	 *            True to fade in
	 * @param preload
	 *            When true, any change to the src property will cause the
	 *            placeholder image (or background color, if no placeholder is
	 *            specified) to be shown until the image has loaded.
	 * 
	 * @return a &lt;core-image&gt; definition configured with the specified
	 *         parameters
	 */
	public static String toHTML(String width, String height, String srcUrl,
			ImageResource placeholder, Sizing size, boolean fade,
			boolean preload) {
		return toHTML(width, height, srcUrl, placeholder, size, fade, preload,
				true);
	}

	/**
	 * Returns a <core-image> definition
	 * 
	 * @param width
	 *            The width of the image or null
	 * @param height
	 *            The height of the image or null
	 * @param srcUrl
	 *            The source URL of the image to load
	 * @param placeholder
	 *            The optional placeholder or null. This image will be used as a
	 *            background/placeholder until the src image has loaded.
	 * @param size
	 *            The size option or null for the natural size
	 * @param fade
	 *            True to fade in
	 * @param preload
	 *            When true, any change to the src property will cause the
	 *            placeholder image (or background color, if no placeholder is
	 *            specified) to be shown until the image has loaded.
	 * @param load
	 *            True to load the image from srcUrl, false to show the
	 *            placeholder without attempting to load.
	 * 
	 * @return a &lt;core-image&gt; definition configured with the specified
	 *         parameters
	 */
	public static String toHTML(String width, String height, String srcUrl,
			ImageResource placeholder, Sizing size, boolean fade,
			boolean preload, boolean load) {
		String style = null;
		if (width != null || height != null) {
			StringBuilder s = new StringBuilder("style='");
			if (width != null) {
				s.append("width:").append(width).append(";");
			}
			if (height != null) {
				s.append("height:").append(height).append(";");
			}
			s.append("'");
			style = s.toString();
		}
		return toHTML(style, srcUrl, placeholder, size, fade, preload, load);
	}
	
	public static String toHTML(String style, String srcUrl,
			ImageResource placeholder, Sizing size, boolean fade,
			boolean preload, boolean load) {

		StringBuilder img = new StringBuilder("<core-image");
		if (style != null) {
			boolean wrap = false;
			img.append(" ");
			if (!style.trim().toLowerCase().startsWith("style")) {
				wrap = true;
				img.append("style='");
			}
			img.append(style);
			if (wrap) {
				img.append("'");
			}
		}
		
		img.append(" src=\"").append(srcUrl).append("\"");

		if (!load) {
			img.append(" load=\"false\"");
		}

		if (placeholder != null) {
			img.append(" placeholder=\"")
					.append(placeholder.getSafeUri().asString()).append("\"");
		}

		if (size != null && size.value != null) {
			img.append(" sizing=\"").append(size.value).append("\"");
		}

		if (fade) {
			img.append(" fade");
		}

		if (preload) {
			img.append(" preload");
		}

		img.append("></core-image>");

		return img.toString();
	}
}
