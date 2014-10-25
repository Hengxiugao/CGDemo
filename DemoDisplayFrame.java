// DemoDisplayFrame.java:

//	Sets up the display frame for the demo.


/* CGDemo is a companion of the textbook

L. Ammeraal and K. Zhang, Computer Graphics for Java Programmers, 
2nd Edition, Wiley, 2006.

Copyright (C) 2006  Janis Schubert, Kang Zhang, Leen Ammeraal 

This program is free software; you can redistribute it and/or 
modify it under the terms of the GNU General Public License as 
published by the Free Software Foundation; either version 2 of 
the License, or (at your option) any later version. 

This program is distributed in the hope that it will be useful, 
but WITHOUT ANY WARRANTY; without even the implied warranty of 
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
See the GNU General Public License for more details.  

You should have received a copy of the GNU General Public 
License along with this program; if not, write to 
the Free Software Foundation, Inc., 51 Franklin Street, 
Fifth Floor, Boston, MA  02110-1301, USA. 
*/

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

public class DemoDisplayFrame extends JFrame
{

	SplashWindow splash = new SplashWindow("CGDemoSplash.jpg", this, 7000);

	final static String BRESENHAM = "Bresenham Line-Drawing";
	final static String COHENSUTHERLAND = "Cohen-Sutherland Line Clipping";
	final static String POLYGONCLIP = "Sutherland-Hodgman Polygon Clipping";
	final static String HIDDENLINE = "Hidden Line Elimination";
    final static String VIEWTRANSFORM = "Viewpoint Transformation";
    final static String BEZIERCURVE = "Bezier Curve";
    final static String DOUBLESTEP = "Double Step";

    DemoDisplayFrame()
    {
    	setTitle("CGDemo - Computer Graphics Algorithm Demonstrations");

    	addWindowListener(new WindowAdapter()
    	{
    		public void windowClosing(WindowEvent e)
    		{
    			System.exit(0);
    		}
    	});

		BresenhamLineDrawingDemo bresenham = new BresenhamLineDrawingDemo();
		CohenSutherlandLineClipping cohensutherland = new CohenSutherlandLineClipping();
		PolygonClip polygonclip = new PolygonClip();
		HiddenLineElimination hiddenline = new HiddenLineElimination(this);
		ViewTransformDemo viewtransform = new ViewTransformDemo();
		DoubleStepLineDrawingDemo doubleStep = new DoubleStepLineDrawingDemo();
		

		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab(BRESENHAM, bresenham);
		tabbedPane.addTab(COHENSUTHERLAND, cohensutherland);
		tabbedPane.addTab(POLYGONCLIP, polygonclip);
		tabbedPane.addTab(HIDDENLINE, hiddenline);
		tabbedPane.addTab(VIEWTRANSFORM, viewtransform);
		tabbedPane.addTab(DOUBLESTEP, doubleStep);
		

		Container contentPane = getContentPane();
		contentPane.add(tabbedPane, BorderLayout.CENTER);
	}
}

