# Google I/O 2015 Scheduler #
A web based scheduler application for the Google I/O 2015 developer conference, built using Polymer and the Google Web Toolkit.

This application offers a subset of the functionality offered by the official Google I/O schedule site/app with the following key differences:

* Schedule information is saved into cookies in the browser.
* Sessions that are offered at more than one time during the conference are easily identified and links to the alternative times are provided in popup menus making it easier to define your schedule.
* You are provided with warning icons to inform you when you have included a session in your schedule more than once.

You can find the live application here: http://io2015schedule.appspot.com/ 


You can emulate a specific time by using the 'now' URL argument.
For example, to see what the app looked like at the beginning of day 2 of the conference: http://io2015schedule.appspot.com/?now=201505290900-0700

The timestamp format for the now argument is yyyyMMddHHmmZ
