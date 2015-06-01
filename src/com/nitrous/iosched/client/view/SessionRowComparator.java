package com.nitrous.iosched.client.view;

import java.util.Comparator;

/**
 * Sort SessionRow components by the session start time and title.
 * 
 * @author nitrousdigital
 */
public class SessionRowComparator implements Comparator<SessionRow> {

	@Override
	public int compare(SessionRow o1, SessionRow o2) {
		Long s1 = o1.getSession().getStartTime();
		Long s2 = o2.getSession().getStartTime();
		int result = s1.compareTo(s2);
		if (result == 0){
			result = o1.getSession().getTitle().compareToIgnoreCase(o2.getSession().getTitle());
		}
		return result;
	}

}
