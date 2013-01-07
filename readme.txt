Summary

	MovableType is a two player word game for Android.
	
	It is based on the iPhone game LetterPress which is available on the iTunes 
	store at https://itunes.apple.com/gb/app/letterpress-word-game/id526619424?mt=8.
	
	The main difference is that both players use the same device, therefore 
	it can be played without an internet connection.

Source Code

	The source is divided into the following packages:
	
	gameframework -	A generic frame work for drawing simple graphics, animations 
					and accepting touch events.
	model - 		Manages the game state
	ui - 			The user interface

Tests

	Unit tests are stored in the directory ./test. The tests should be loaded as a separate
	project into Eclipse. The unit tests can be run as an "Android JUnit Test" application.
	
	The user interface can be tested using the Monkey tool by running the script ./test/runmonkey.sh
	This will subject the application to a series of random screen gestures. Note that a
	random seed is specified to help reproduction of problems, however this is not guaranteed
	to work as the timing may be affected by the state of the device.

License

	Word list from: http://www.curlewcommunications.co.uk/wordlist.html Used under the
	following agreement: These files are provided "as is" without any warranty. They may be
	used and copied freely for non-commercial purposes provided that this notice is included.
	Downloading of the files signifies acceptance of these conditions. All rights are
	reserved.
	
	The code for elastic easing (four lines in Interpolators.java) was adapted from the Kivy
	source code which is released under the GNU LGPL Version 3 license. http://kivy.org/
	
	The icon was adapted from the  Android Icon Templates Pack available at
	http://developer.android.com/guide/practices/ui_guidelines/icon_design.html
	The icons are released under the Apache 2.0 license