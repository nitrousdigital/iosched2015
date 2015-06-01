package com.nitrous.iosched.client.view;

/**
 * Implemented by any component that configures its size based up on the size of its parent.
 * It is the responsibility of any implemented of this interface to delegate RequiresContainerWidth calls to its children.
 * 
 * @author nitrousdigital
 *
 */
public interface RequiresContainerWidth {
	/**
	 * Called when ever the parent width is changed.
	 * 
	 * @param parentWidth The width of the parent container
	 */
	void onResize(int parentWidth);
}
