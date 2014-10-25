// HiddenLineElimination.java: Perspective drawing with hidden-line elimination.
// Uses: Point2D, Point3D, Tools2D, Obj3D, Input, Polygon3D, Tria.

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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class HiddenLineElimination extends JPanel
{	private JFrame frame;
	private JButton openButton, eyeUpButton, eyeDownButton,
		eyeLeftButton, eyeRightButton, incrDistButton, decrDistButton;
	private JButton testButton, triangButton;
	private JRadioButton testOnButton, triangOnButton, testTriangOffButton;
	private HiddenLineDrawPanel drawPanel;
	private JPanel buttonPanel;
	private JPanel menuPanel;
	private String sDir = "C:/Java/GraphicsDemo/";

	HiddenLineElimination(JFrame fr)
	{	frame = fr;

		setLayout(new BorderLayout());

		MenuCommands mListener = new MenuCommands();

		menuPanel = new JPanel();
		menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
		openButton = new JButton("   Open   ");
		eyeUpButton = new JButton("Viewpoint Up");
		eyeDownButton = new JButton("Viewpoint Down");
		eyeLeftButton = new JButton("Viewpoint Left");
		eyeRightButton = new JButton("Viewpoint Right");
		incrDistButton = new JButton("Increase Distance ");
		decrDistButton = new JButton("Decrease Distance");
		openButton.addActionListener(mListener);
		eyeUpButton.addActionListener(mListener);
		eyeDownButton.addActionListener(mListener);
		eyeLeftButton.addActionListener(mListener);
		eyeRightButton.addActionListener(mListener);
		incrDistButton.addActionListener(mListener);
		decrDistButton.addActionListener(mListener);
		menuPanel.add(openButton);
		menuPanel.add(eyeUpButton);
		menuPanel.add(eyeDownButton);
		menuPanel.add(eyeLeftButton);
		menuPanel.add(eyeRightButton);
		menuPanel.add(incrDistButton);
		menuPanel.add(decrDistButton);
		add(menuPanel, "East");

		drawPanel = new HiddenLineDrawPanel();
		drawPanel.setBackground(Color.white);
		drawPanel.setSize(800,500);
		add(drawPanel, "Center");

		/*buttonPanel = new JPanel();
		testButton = new JButton("Test On ");
		triangButton = new JButton("Triangulate On ");
		testButton.addActionListener(mListener);
		triangButton.addActionListener(mListener);
		buttonPanel.add(testButton);
		buttonPanel.add(triangButton);
		add(buttonPanel, "South");*/


		testOnButton = new JRadioButton("Test On");
		triangOnButton = new JRadioButton("Triangulate On");
		testTriangOffButton = new JRadioButton("Test/Triangulate Off");

		testOnButton.addActionListener(mListener);
		triangOnButton.addActionListener(mListener);
		testTriangOffButton.addActionListener(mListener);
		testTriangOffButton.setSelected(true);

		ButtonGroup group = new ButtonGroup();
		group.add(testOnButton);
		group.add(triangOnButton);
		group.add(testTriangOffButton);

		buttonPanel = new JPanel();
		buttonPanel.add(testOnButton);
		buttonPanel.add(triangOnButton);
		buttonPanel.add(testTriangOffButton);
		add(buttonPanel, "South");

		String fName = "C:/Java/GraphicsDemo/steps.dat";
		HiddenLineDemo1Obj3D obj = new HiddenLineDemo1Obj3D();
		if (obj.read(fName))
		{	drawPanel.setObj(obj);
			repaint();
		}

	}

	class MenuCommands implements ActionListener
	{	public void actionPerformed(ActionEvent ae)
		{	if (ae.getSource() instanceof JRadioButton)
			{	JRadioButton jrb = (JRadioButton)ae.getSource();
				//boolean testOn = drawPanel.getTestOn();
				//boolean triangOn = drawPanel.getTriangOn();
				if (jrb == testOnButton)
				{	drawPanel.setTestOn(true);
					drawPanel.setTriangOn(false);
					repaint();
				} else
				if (jrb == triangOnButton)
				{	drawPanel.setTriangOn(true);
					drawPanel.setTestOn(false);
					repaint();
				} else
				if (jrb == testTriangOffButton)
				{	drawPanel.setTriangOn(false);
					drawPanel.setTestOn(false);
					repaint();
				}
			} else

			if (ae.getSource() instanceof JButton)
			{	JButton jb = (JButton)ae.getSource();
				if (jb == openButton)
				{	FileDialog fDia = new FileDialog(frame, "Open", FileDialog.LOAD);
					fDia.setDirectory(sDir);
					fDia.setFile("*.dat");
					fDia.show();
					String sDir1 = fDia.getDirectory();
					String sFile = fDia.getFile();
					String fName = sDir1 + sFile;
					HiddenLineDemo1Obj3D obj = new HiddenLineDemo1Obj3D();
					if (obj.read(fName))
					{	sDir = sDir1;
						drawPanel.setObj(obj);
						repaint();
					}
				} else
				if (jb == eyeUpButton)		drawPanel.vp(0, -.1F, 1);	else
				if (jb == eyeDownButton)	drawPanel.vp(0, .1F, 1);	else
				if (jb == eyeLeftButton)	drawPanel.vp(-.1F, 0, 1);	else
				if (jb == eyeRightButton)	drawPanel.vp(.1F, 0, 1);	else
				if (jb == incrDistButton)	drawPanel.vp(0, 0, 2);		else
				if (jb == decrDistButton)	drawPanel.vp(0, 0, .5F);
			}
		}
	}
}

class HiddenLineDrawPanel extends JPanel
{	private HiddenLineDemo1Obj3D obj;
	private int maxX, maxY, centerX, centerY, nTria, nVertices;
	private Point2D imgCenter;
	private Tria[] tr;
	private int[] refPol;
	private int[][] connect;
	private int[] nConnect;
	private int chunkSize = 4;
	private double hLimit;
	private Vector polyList;
	private float maxScreenRange;
	private int curTriang;
	private int curTr, curLni, curLnj, curTest;
	private boolean clicked = false;
	private boolean testOn = false, triangOn = false;
	private boolean drawTr = false;
	private boolean drawLines = false;
	private	Font textFont = new Font("Arial", Font.PLAIN, 11);
	private	Font textBoldFont = new Font("Arial", Font.BOLD, 11);
	private int PX, PY, QX, QY, IX, IY, JX, JY;

	HiddenLineDemo1Obj3D getObj(){return obj;}
	void setObj(HiddenLineDemo1Obj3D inObj){obj = inObj;}

	boolean getTestOn(){return testOn;}
	void setTestOn(boolean inTestOn){testOn = inTestOn;}

	boolean getTriangOn(){return triangOn;}
	void setTriangOn(boolean inTriangOn){triangOn = inTriangOn;}

	void vp(float dTheta, float dPhi, float fRho) // Viewpoint
	{	HiddenLineDemo1Obj3D obj = getObj();
		if (obj == null || !obj.vp(this, dTheta, dPhi, fRho))
			Toolkit.getDefaultToolkit().beep();
	}

	HiddenLineDrawPanel()
	{	curTriang=-1;
		curTr=-1;
		curLni=-1;
		curLnj=-1;
		curTest=0;
		addMouseListener(new MouseAdapter()
		{	public void mousePressed(MouseEvent evt)

			{	if (triangOn)
				{	curTriang = curTriang + 1;
					repaint();
				}
				if (testOn)
				{	curTr = curTr + 1;
					repaint();
				}
			}
		});
	}

	public void paintComponent(Graphics g)
	{	super.paintComponent(g);

		String[] title = {
			"Hidden Line Elimination",
			"Demonstration"};
		Font titleFont = new Font("Arial", Font.BOLD, 18);

		// Draw Title
		g.setFont(titleFont);
		g.setColor(Color.black);
		g.drawString(title[0], 570, 30);
		g.drawString(title[1], 615, 50);
		g.drawLine(545, 70, 800, 70);

		if (obj == null) return;
		Vector polyList = obj.getPolyList();
		if (polyList == null) return;
		int nFaces = polyList.size();
		if (nFaces == 0) return;
		float xe, ye, ze;
		Dimension dim = getSize();
		maxX = dim.width - 1; maxY = dim.height - 1;
		centerX = maxX/2; centerY = maxY/2;
		maxScreenRange = obj.eyeAndScreen(dim);
		imgCenter = obj.getImgCenter();
		obj.planeCoeff();      // Compute a, b, c and h.

		hLimit = -1e-6 * obj.getRho();
		buildLineSet();

		// Construct an array of triangles in
		// each polygon and count the total number
		// of triangles.
		nTria = 0;
		for (int j=0; j<nFaces; j++)
		{	HiddenLineDemo1Polygon3D pol = (HiddenLineDemo1Polygon3D)(polyList.elementAt(j));
			if (pol.getNrs().length > 2 && pol.getH() <= hLimit)
			{	pol.triangulate(obj);
				nTria += pol.getT().length;
			}
		}
		tr = new Tria[nTria];    // Triangles of all polygons
      		refPol = new int[nTria]; // tr[i] belongs to polygon refPol[i]
      		int iTria = 0;

      		Point3D[] e = obj.getE();
      		Point2D[] vScr = obj.getVScr();

		int colorCt = 0;
		if (triangOn && curTriang==nTria) curTriang=0;
      		for (int j=0; j<nFaces; j++)
      		{	HiddenLineDemo1Polygon3D pol = (HiddenLineDemo1Polygon3D)(polyList.elementAt(j));
         		Tria[] t = pol.getT(); // Triangles of one polygon
         		if (pol.getNrs().length > 2 && pol.getH() <= hLimit)
         		{	for (int i=0; i<t.length; i++)
            		{	Tria tri = t[i];
						int iA = tri.iA, iB = tri.iB, iC = tri.iC;
						Point2D AScr = vScr[iA], BScr = vScr[iB], CScr = vScr[iC];
						if (triangOn)
						{	if (curTriang>=iTria)
							{	g.setPaintMode();
								if (colorCt==0) g.setColor(Color.green);
								if (colorCt==1) g.setColor(Color.red);
								if (colorCt==2) g.setColor(Color.blue);
								if (colorCt==3) g.setColor(Color.orange);
								if (colorCt==4) g.setColor(Color.cyan);
								if (colorCt==5) g.setColor(Color.pink);
								if (colorCt==6) g.setColor(Color.magenta);
								int[] xPts={iX(AScr.x), iX(BScr.x), iX(CScr.x)};
								int[] yPts={iY(AScr.y), iY(BScr.y), iY(CScr.y)};
								int nPts=3;
								g.fillPolygon(xPts, yPts, nPts);
								colorCt = colorCt + 1;
								if (colorCt>6) colorCt = 0;
							}
						}
               			tr[iTria] = tri;
               			refPol[iTria++] = j;
            		}
         		}
      		}

		if (testOn)
		{	if (curTr>-1)
			{	if (curLnj==-1 && curLni==-1) // First click with test on
				{	curLnj = 0;
					curLni = 0;
				}
				if (curTr==nTria)
				{	curTr = 0;
					curLnj = curLnj + 1;
				}
				if (curLnj==nConnect[curLni])
				{	curLnj = 0;
					curLni = curLni + 1;
				}
				if (curLni==nVertices) curLni = 0;
				while (nConnect[curLni]==0)
				{	curLni = curLni + 1;
					if (curLni==nVertices) curLni=0;
				}
				drawLines = true;

				Tria t = tr[curTr];
				int iA = t.iA, iB = t.iB, iC = t.iC;
				Point2D AScr = vScr[iA], BScr = vScr[iB], CScr = vScr[iC];
				int[] xPts={iX(AScr.x), iX(BScr.x), iX(CScr.x)};
				int[] yPts={iY(AScr.y), iY(BScr.y), iY(CScr.y)};
				int nPts=3;
				g.setColor(Color.yellow);
				g.fillPolygon(xPts, yPts, nPts);
				g.setColor(Color.black);
				drawLine(g, AScr.x, AScr.y, BScr.x, BScr.y);
				drawLine(g, BScr.x, BScr.y, CScr.x, CScr.y);
				drawLine(g, CScr.x, CScr.y, AScr.x, AScr.y);
			}
			else drawLines = false;
		}
		//drawLines = true;
		for (int i=0; i<nVertices; i++)
		{	for (int j=0; j<nConnect[i]; j++)
			{	int jj = connect[i][j];
				if (testOn)
				{	g.setColor(Color.black);
					if (curLni==i && curLnj==j)
						drawTr=true;
					else
					{	drawTr=false;
						if (drawLines)
						{
							g.setColor(Color.blue);
							dashedLine(g, vScr[i].x, vScr[i].y, vScr[jj].x, vScr[jj].y, 4);
							dashedLine(g, vScr[i].x-1, vScr[i].y, vScr[jj].x-1, vScr[jj].y, 4);
							dashedLine(g, vScr[i].x-1, vScr[i].y-1, vScr[jj].x-1, vScr[jj].y-1, 4);
						}
						else
						{
							g.setColor(Color.GRAY);
							dashedLine(g, vScr[i].x, vScr[i].y, vScr[jj].x, vScr[jj].y, 4);
						}

					}
				}
				if (testOn && curLni==i && curLnj==j)
				{	PX = iX(vScr[i].x);
					PY = iY(vScr[i].y);
					QX = iX(vScr[jj].x);
					QY = iY(vScr[jj].y);
				}
            	lineSegment(g, e[i], e[jj], vScr[i], vScr[jj], i, jj, 0);
				if (testOn)
				{	if (curLni==i && curLnj==j)
					{	g.setFont(textBoldFont);
						g.setColor(Color.red);

						if (curTest==1)
						{
							g.drawString("Test 1: 2D", 515, 90);
							g.drawString("Both P and Q are to the left of A, B and C OR", 535, 105);
							g.drawString("both P and Q are to the right of A, B and C OR", 535, 120);
							g.drawString("both P and Q are above A, B and C OR", 535, 135);
							g.drawString("both P and Q are below A, B and C.", 535, 150);
							g.drawString("Line is visible.", 535, 165);
						}
						if (curTest==2)
						{
							g.drawString("Test 2: 3D", 515, 90);
							g.drawString("PQ is identical with one of the edges", 535, 105);
							g.drawString("of triangle ABC.", 535, 120);
							g.drawString("Line is visible.", 535, 135);
						}
						if (curTest==3)
						{
							g.drawString("Test 3: 3D", 515, 90);
							g.drawString("z-coordinates of P and Q are less", 535, 105);
							g.drawString("than those of A, B and C.", 535, 120);
							g.drawString("Line is visible.", 535, 135);
						}
						if (curTest==4)
						{
							g.drawString("Test 4: 2D", 515, 90);
							g.drawString("P and Q lie on a different side of an edge", 535, 105);
							g.drawString("of the triangle from the third vertex", 535, 120);
							g.drawString("Line is visible.", 535, 135);
						}
						if (curTest==5)
						{
							g.drawString("Test 5: 2D", 515, 90);
							g.drawString("P and Q lie on a different side of a vertex", 535, 105);
							g.drawString("of the triangle from the third edge.", 535, 120);
							g.drawString("Line is visible.", 535, 135);
						}
						if (curTest==6)
						{
							g.drawString("Test 6: 3D", 515, 90);
							g.drawString("Test 3 failed, but PQ still lies in front", 535, 105);
							g.drawString("of the plane of triangle ABC.", 535, 120);
							g.drawString("Line is visible.", 535, 135);
						}
						if (curTest==7)
						{
							g.drawString("Test 7: 2D", 515, 90);
							g.drawString("PQ is completely obscured by triangle ABC.", 535, 105);
							g.drawString("Line is NOT visible.", 535, 120);

							// Reset the current triangle pointer
							curTr = nTria - 1;
						}
						if (curTest==8)
						{
							g.drawString("Test 8: 3D", 515, 90);
							g.drawString("Either P or Q is nearer than ABC plane", 535, 105);
							g.drawString("and also appears inside ABC.", 535, 120);
							g.drawString("Line is visible.", 535, 135);
						}
						if (curTest==9)
						{
							g.drawString("Test 9: 3D", 515, 90);
							g.drawString("PQ intersects ABC.", 535, 105);
							g.drawString("If intersection is in front of ABC, PQ is visible.", 535, 120);
							g.drawString("Else ABC partially obscures PQ.", 535, 135);

							// Reset the current triangle pointer
							curTr = nTria - 1;
						}
					}
				}
         		}
      		}
      		//hpgl = null;
   	}

	void dashedLine(Graphics g, float xa, float ya, float xb, float yb, int dashLength) {

		int xA = iX(xa);
		int yA = iY(ya);
		int xB = iX(xb);
		int yB = iY(yb);

		//determine the total length of the line to be drawn
		float total_length = (float)Math.sqrt(((yb-ya) * (yb-ya)) + ((xb-xa)*(xb-xa)));

		//determine the length of the line, minus the two dashes at the endpoints
		float trunc_length = total_length - (dashLength * 2);

		if (trunc_length < 0) {
			g.drawLine(xA, yA, xB, yB);
			return;
		}

		//determine the horizontal component of the dash length
		float y_dash_offset = (yb-ya)*(dashLength/total_length);

		//determine the vertical component of the dash length
		float x_dash_offset = (xb-xa)*(dashLength/total_length);

		//draw the ndpoints

		g.drawLine(iX(xa), iY(ya), iX(xa + x_dash_offset), iY(ya + y_dash_offset));
		g.drawLine(iX(xb - x_dash_offset), iY(yb - y_dash_offset), iX(xb), iY(yb));

		//divvy up the remaining length of the line into pairs of dashes and spaces,
		//but leave one extra space to fall after one of the endpoint dashes

		int total_pairs = (int)Math.floor(trunc_length/(dashLength * 2)) - 1;

		//determine how much empty space will be left over after drawing
		//each of the inner dashes
		float leftover_space = trunc_length - (total_pairs * dashLength);

		//evenly divide the leftover space between each empty block
		float empty_space_length = leftover_space/(total_pairs + 1);

		//determine the horizontal and vertical components of each
		//individual empty block
		float x_space_offset = (xb-xa)*(empty_space_length/total_length);
		float y_space_offset = (yb-ya)*(empty_space_length/total_length);

		//start drawing the inner dashes one dash length and one space length
		//away from the beginning of the line

		float current_x, current_y;
		current_x = xa + x_dash_offset;
		current_y = ya + y_dash_offset;
		current_x += x_space_offset;
		current_y += y_space_offset;

		//draw a dash, and then move the pen across one empty space interval
		for (int i = 0; i < total_pairs; i++) {
			g.drawLine(iX(current_x), iY(current_y), iX(current_x + x_dash_offset), iY(current_y + y_dash_offset));
			current_x += (x_space_offset + x_dash_offset);
			current_y += (y_space_offset + y_dash_offset);
		}
	}

   	private void buildLineSet()
   	{	// Build the array
      		// 'connect' of int arrays, where
      		// connect[i] is the array of all
      		// vertex numbers j, such that connect[i][j] is
      		// an edge of the 3D object.
      		polyList = obj.getPolyList();
      		nVertices = obj.getVScr().length;
      		connect = new int[nVertices][];
      		nConnect = new int[nVertices];
      		for (int i=0; i<nVertices; i++)
      			nConnect[i] = 0;
      		int nFaces = polyList.size();

      		for (int j=0; j<nFaces; j++)
      		{	HiddenLineDemo1Polygon3D pol = (HiddenLineDemo1Polygon3D)(polyList.elementAt(j));
         		int[] nrs = pol.getNrs();
         		int n = nrs.length;
         	//	if (n > 2 && pol.getH() > 0) continue;
         		int ii = Math.abs(nrs[n-1]);
         		for (int k=0; k<n; k++)
         		{	int jj = nrs[k];
            			if (jj < 0)
               			jj = -jj; // abs
            			else
            			{	int i1 = Math.min(ii, jj), j1 = Math.max(ii, jj),
                   			nCon = nConnect[i1];
               			// Look if j1 is already present:
               			int l;
               			for (l=0; l<nCon; l++) if (connect[i1][l] == j1) break;
               			if (l == nCon)  // Not found:
               			{	if (nCon % chunkSize == 0)
                  				{	int[] temp = new int[nCon + chunkSize];
                     				for (l=0; l<nCon; l++) temp[l] = connect[i1][l];
                     				connect[i1] = temp;
                  				}
                  				connect[i1][nConnect[i1]++] = j1;
               			}
            			}
            			ii = jj;
         		}
      		}
   	}

   	int iX(float x){return Math.round(centerX + x - imgCenter.x);}
   	int iY(float y){return Math.round(centerY - y + imgCenter.y);}

   	private String toString(float t)
   	// From screen device units (pixels) to HP-GL units (0-10000):
   	{	int i = Math.round(5000 + t * 9000/maxScreenRange);
      		String s = "";
      		int n = 1000;
      		for (int j=3; j>=0; j--)
      		{	s += i/n;
         		i %= n;
         		n /= 10;
      		}
      		return s;
   	}

   	private String hpx(float x){return toString(x - imgCenter.x);}
   	private String hpy(float y){return toString(y - imgCenter.y);}

   	private void drawLine(Graphics g, float x1, float y1, float x2, float y2)
   	{
		if (x1 != x2 || y1 != y2)	g.drawLine(iX(x1), iY(y1), iX(x2), iY(y2));
   	}

   	private void lineSegment(Graphics g, Point3D Pe, Point3D Qe,
      					Point2D PScr, Point2D QScr,
					int iP, int iQ, int iStart)
   	{	double u1 = QScr.x - PScr.x, u2 = QScr.y - PScr.y;
      		double minPQx = Math.min(PScr.x, QScr.x);
      		double maxPQx = Math.max(PScr.x, QScr.x);
      		double minPQy = Math.min(PScr.y, QScr.y);
      		double maxPQy = Math.max(PScr.y, QScr.y);
      		double zP = Pe.z, zQ = Qe.z;
      		double minPQz = Math.min(zP, zQ);
      		Point3D[] e = obj.getE();
      		Point2D[] vScr = obj.getVScr();
		boolean rememberTest = false;

		int i=iStart;
      		for (i=iStart; i<nTria; i++)
      		{
				Tria t = tr[i];
         		int iA = t.iA, iB = t.iB, iC = t.iC;
         		Point2D AScr = vScr[iA], BScr = vScr[iB], CScr = vScr[iC];


         	// If "Test" is turned on AND
         	// 		this is the current line being tested AND
         	//		this is the current triangle being tested
         	// Then
         	//		draw the line being tested
         	//		draw the triangle
         	//		set "rememberTest = true" to get the test number
         	//		set "drawLines = false" to know rest of lines have NOT been tested
         	// Else
         	//		set "rememberTest = false" since we don't want to display this test
			if (testOn && drawTr && curTr==i)
			{
				/*int[] xPts={iX(AScr.x), iX(BScr.x), iX(CScr.x)};
				int[] yPts={iY(AScr.y), iY(BScr.y), iY(CScr.y)};
				int nPts=3;
				g.setColor(Color.yellow);
				g.fillPolygon(xPts, yPts, nPts);
				g.setColor(Color.black);
				drawLine(g, AScr.x, AScr.y, BScr.x, BScr.y);
				drawLine(g, BScr.x, BScr.y, CScr.x, CScr.y);
				drawLine(g, CScr.x, CScr.y, AScr.x, AScr.y);*/

				g.setColor(Color.red);
				g.drawLine(PX, PY, QX, QY);
				g.drawLine(PX-1, PY, QX-1, QY);
				g.drawLine(PX-1, PY-1, QX-1, QY-1);

				g.setColor(Color.black);
				g.setFont(textBoldFont);
				g.fillRect(PX-2, PY-2, 5, 5);
				if (PX < QX)	g.drawString("P", PX-15, PY+10);
				else			g.drawString("P", PX+10, PY+10);
				g.fillRect(QX-2, QY-2, 5, 5);
				if (QX > PX)	g.drawString("Q", QX+10, QY+10);
				else			g.drawString("Q", QX-15, QY+10);

				drawLines = false;
				rememberTest = true;
			}
			else
				rememberTest = false;

         		// 1. Minimax test for x and y screen coordinates:
         		if (maxPQx <= AScr.x && maxPQx <= BScr.x && maxPQx <= CScr.x
          			|| minPQx >= AScr.x && minPQx >= BScr.x && minPQx >= CScr.x
          			|| maxPQy <= AScr.y && maxPQy <= BScr.y && maxPQy <= CScr.y
          			|| minPQy >= AScr.y && minPQy >= BScr.y && minPQy >= CScr.y)
				{	if (rememberTest) curTest = 1;
					continue;
				}

         		// 2. Test if PQ is an edge of ABC:
         		if ((iP == iA || iP == iB || iP == iC) &&
             			(iQ == iA || iQ == iB || iQ == iC))
				{	if (rememberTest) curTest = 2;
					continue;
				}

         		// 3. Test if PQ is clearly nearer than ABC:
         		Point3D Ae = e[iA], Be = e[iB], Ce = e[iC];
         		double zA = Ae.z, zB = Be.z, zC = Ce.z;
         		if (minPQz >= zA && minPQz >= zB && minPQz >= zC)
				{	if (rememberTest) curTest = 3;
					continue;
				}

         		// 4. Do P and Q (in 2D) lie in a half plane defined
         		//    by line AB, on the side other than that of C?
         		//    Similar for the edges BC and CA.
         		double eps = 0.1; // Relative to numbers of pixels
         		if (Tools2D.area2(AScr, BScr, PScr) < eps &&
             			Tools2D.area2(AScr, BScr, QScr) < eps ||
             			Tools2D.area2(BScr, CScr, PScr) < eps &&
             			Tools2D.area2(BScr, CScr, QScr) < eps ||
             			Tools2D.area2(CScr, AScr, PScr) < eps &&
             			Tools2D.area2(CScr, AScr, QScr) < eps)
				{	if (rememberTest) curTest = 4;
					continue;
				}

         		// 5. Test (2D) if A, B and C lie on the same side
         		//    of the infinite line through P and Q:
         		double PQA = Tools2D.area2(PScr, QScr, AScr);
         		double PQB = Tools2D.area2(PScr, QScr, BScr);
         		double PQC = Tools2D.area2(PScr, QScr, CScr);

         		if (PQA < +eps && PQB < +eps && PQC < +eps ||
             			PQA > -eps && PQB > -eps && PQC > -eps)
				{	if (rememberTest) curTest = 5;
            				continue;
				}

         		// 6. Test if neither P nor Q lies behind the
         		//    infinite plane through A, B and C:
         		int iPol = refPol[i];
         		HiddenLineDemo1Polygon3D pol = (HiddenLineDemo1Polygon3D)polyList.elementAt(iPol);
         		double a = pol.getA(), b = pol.getB(), c = pol.getC(),
            			h = pol.getH(), eps1 = 1e-5 * Math.abs(h),
            			hP = a * Pe.x + b * Pe.y + c * Pe.z,
            			hQ = a * Qe.x + b * Qe.y + c * Qe.z;
         		if (hP > h - eps1 && hQ > h - eps1)
				{	if (rememberTest) curTest = 6;
            				continue;
				}

         		// 7. Test if both P and Q behind triangle ABC:
         		boolean PInside =
            			Tools2D.insideTriangle(AScr, BScr, CScr, PScr);
         		boolean QInside =
            			Tools2D.insideTriangle(AScr, BScr, CScr, QScr);
         		if (PInside && QInside)
				{	if (rememberTest) {curTest = 7; /*curTr = nTria-1;*/}
					return;
				}

         		// 8. If P nearer than ABC and inside, PQ visible;
         		//    the same for Q:
         		double h1 = h + eps1;
         		boolean PNear = hP > h1, QNear = hQ > h1;
         		if (PNear && PInside || QNear && QInside)
				{	if (rememberTest) curTest = 8;
					continue;
				}

         		// 9. Compute the intersections I and J of PQ
         		// with ABC in 2D.
         		// If, in 3D, such an intersection lies in front of
         		// ABC, this triangle does not obscure PQ.
         		// Otherwise, the intersections lie behind ABC and
         		// this triangle obscures part of PQ:
         		double lambdaMin = 1.0, lambdaMax = 0.0;

			if (rememberTest) {curTest = 9;	/*curTr = nTria-1;*/}

         		for (int ii=0; ii<3; ii++)
         		{	double v1 = BScr.x - AScr.x, v2 = BScr.y - AScr.y,
                   			w1 = AScr.x - PScr.x, w2 = AScr.y - PScr.y,
                   			denom = u2 * v1 - u1 * v2;
            			if (denom != 0)
            			{	double mu = (u1 * w2 - u2 * w1)/denom;
               			// mu = 0 gives A and mu = 1 gives B.
               			if (mu > -0.0001 && mu < 1.0001)
               			{	double lambda = (v1 * w2 - v2 * w1)/denom;
                  				// lambda = PI/PQ
                  				// (I is point of intersection)
                  				if (lambda > -0.0001 && lambda < 1.0001)
                  				{	if (PInside != QInside &&
                     					lambda > 0.0001 && lambda < 0.9999)
                     				{	lambdaMin = lambdaMax = lambda;
                        					break;
                        					// Only one point of intersection
                     				}
                     				if (lambda < lambdaMin) lambdaMin = lambda;
                     				if (lambda > lambdaMax) lambdaMax = lambda;
                  				}
               			}
            			}
            			Point2D temp = AScr; AScr = BScr;
            			BScr = CScr; CScr = temp;
         		}
         		float d = obj.getD();
         		if (!PInside && lambdaMin > 0.001)
         		{	double IScrx = PScr.x + lambdaMin * u1,
                   			IScry = PScr.y + lambdaMin * u2;
            			// Back from screen to eye coordinates:
            			double zI = 1/(lambdaMin/zQ + (1 - lambdaMin)/zP),
                   			xI = -zI * IScrx / d, yI = -zI * IScry / d;
            			if (a * xI + b * yI + c * zI > h1) continue;
            			Point2D IScr = new Point2D((float)IScrx, (float)IScry);
            			if (Tools2D.distance2(IScr, PScr) >= 1.0)
						{	IX = iX(IScr.x);
							IY = iY(IScr.y);
							if (testOn && drawTr && curTr==i)
							{
								g.setColor(Color.black);
								g.setFont(textBoldFont);
								g.fillRect(IX-2, IY-2, 5, 5);
								g.drawString("I", IX-25, IY+20);
							}
               				lineSegment(g, Pe, new Point3D(xI, yI, zI), PScr,
                  				IScr, iP, -1, i + 1);
						}
         		}
         		if (!QInside && lambdaMax < 0.999)
         		{	double JScrx = PScr.x + lambdaMax * u1,
                   			JScry = PScr.y + lambdaMax * u2;
            			double zJ =
               			1/(lambdaMax/zQ + (1 - lambdaMax)/zP),
                  			xJ = -zJ * JScrx / d, yJ = -zJ * JScry / d;
            			if (a * xJ + b * yJ + c * zJ > h1)
					continue;
            			Point2D JScr = new Point2D((float)JScrx, (float)JScry);
            			if (Tools2D.distance2(JScr, QScr) >= 1.0)
               			lineSegment(g, Qe, new Point3D(xJ, yJ, zJ),
                  				QScr, JScr, iQ, -1, i + 1);
         		}

        		return;
            		// if no continue-statement has been executed
      		}


		g.setPaintMode();
		if (!testOn)
		{	g.setColor(Color.black);
			drawLine(g, PScr.x, PScr.y, QScr.x, QScr.y);
		}
		else
		if (testOn && drawLines)
		{	g.setColor(Color.blue);
      		drawLine(g, PScr.x, PScr.y, QScr.x, QScr.y);
      		drawLine(g, PScr.x-1, PScr.y-1, QScr.x-1, QScr.y-1);
      		drawLine(g, PScr.x-1, PScr.y, QScr.x-1, QScr.y);
		}
		//else
		//if (testOn && !drawLines && !drawTr)
		//{	g.setColor(Color.GRAY);
		//	dashedLine(g, PScr.x, PScr.y, QScr.x, QScr.y, 4);
		//}
   	}


}

// Obj3D.java: A 3D object and its 2D representation.
// Uses: Point2D (Section 1.5), Point3D (Section 3.9),
//       Polygon3D, Input (Section 6.3).

// Copied from Appendix C (discussed in Section 6.3) of
//    Ammeraal, L. (1998) Computer Graphics for Java Programmers,
//       Chichester: John Wiley.


class HiddenLineDemo1Obj3D
{  private float rho, d, theta=0.30F, phi=1.3F, rhoMin, rhoMax,
      xMin, xMax, yMin, yMax, zMin, zMax, v11, v12, v13, v21,
      v22, v23, v32, v33, v43, xe, ye, ze, objSize;
   private Point2D imgCenter;
   private double sunZ = 1/Math.sqrt(3), sunY = sunZ, sunX = -sunZ,
      inprodMin = 1e30, inprodMax = -1e30, inprodRange;
   private Vector w = new Vector();        // World coordinates
   private Point3D[] e;                    // Eye coordinates
   private Point2D[] vScr;                 // Screen coordinates
   private Vector polyList = new Vector(); // Polygon3D objects
   private String fName = "";              // File name

   boolean read(String fName)
   {  Input inp = new Input(fName);
      if (inp.fails())return failing();
      this.fName = fName;
      xMin = yMin = zMin = +1e30F;
      xMax = yMax = zMax = -1e30F;
      return readObject(inp); // Read from inp into obj
   }

   Vector getPolyList(){return polyList;}
   String getFName(){return fName;}
   Point3D[] getE(){return e;}
   Point2D[] getVScr(){return vScr;}
   Point2D getImgCenter(){return imgCenter;}
   float getRho(){return rho;}
   float getD(){return d;}

   private boolean failing()
   {  Toolkit.getDefaultToolkit().beep();
      return false;
   }

   private boolean readObject(Input inp)
   {  for (;;)
      {  int i = inp.readInt();
         if (inp.fails()){inp.clear(); break;}
         if (i < 0)
         {  System.out.println(
               "Negative vertex number in first part of input file");
            return failing();
         }
         w.ensureCapacity(i + 1);
         float x = inp.readFloat(),
               y = inp.readFloat(),
               z = inp.readFloat();
         addVertex(i, x, y, z);
      }
      shiftToOrigin(); // Origin in center of object.
      char ch;
      int count = 0;
      do   // Skip the line "Faces:"
      {  ch = inp.readChar(); count++;
      }  while (!inp.eof() && ch != '\n');
      if (count < 6 || count > 8)
      {  System.out.println("Invalid input file"); return failing();
      }
      // Build polygon list:
      for (;;)
      {  Vector vnrs = new Vector();
         for (;;)
         {  int i = inp.readInt();
            if (inp.fails()){inp.clear(); break;}
            int absi = Math.abs(i);
            if (i == 0 || absi >= w.size() ||
               w.elementAt(absi) == null)
            {  System.out.println("Invalid vertex number: " + absi +
               " must be defined, nonzero and less than " + w.size());
               return failing();
            }
            vnrs.addElement(new Integer(i));
         }
         ch = inp.readChar();
         if (ch != '.' && ch != '#') break;
         // Ignore input lines with only one vertex number:
         if (vnrs.size() >= 2)
            polyList.addElement(new HiddenLineDemo1Polygon3D(vnrs));
      }
      inp.close();
      return true;
   }

   private void addVertex(int i, float x, float y, float z)
   {  if (x < xMin) xMin = x; if (x > xMax) xMax = x;
      if (y < yMin) yMin = y; if (y > yMax) yMax = y;
      if (z < zMin) zMin = z; if (z > zMax) zMax = z;
      if (i >= w.size()) w.setSize(i + 1);
      w.setElementAt(new Point3D(x, y, z), i);
   }

   private void shiftToOrigin()
   {  float xwC = 0.5F * (xMin + xMax),
            ywC = 0.5F * (yMin + yMax),
            zwC = 0.5F * (zMin + zMax);
      int n = w.size();
      for (int i=1; i<n; i++)
         if (w.elementAt(i) != null)
         {  ((Point3D)w.elementAt(i)).x -= xwC;
            ((Point3D)w.elementAt(i)).y -= ywC;
            ((Point3D)w.elementAt(i)).z -= zwC;
         }
      float dx = xMax - xMin, dy = yMax - yMin, dz = zMax - zMin;
      rhoMin = 0.6F * (float) Math.sqrt(dx * dx + dy * dy + dz * dz);
      rhoMax = 1000 * rhoMin;
      rho = 3 * rhoMin;
   }

   private void initPersp()
   {  float costh = (float)Math.cos(theta),
            sinth = (float)Math.sin(theta),
            cosph = (float)Math.cos(phi),
            sinph = (float)Math.sin(phi);
      v11 = -sinth;   v12 = -cosph * costh;   v13 = sinph * costh;
      v21 = costh;    v22 = -cosph * sinth;   v23 = sinph * sinth;
                      v32 = sinph;            v33 = cosph;
                                              v43 = -rho;
   }

   float eyeAndScreen(Dimension dim)
      // Called in paint method of Canvas class
   {  initPersp();
      int n = w.size();
      e = new Point3D[n];
      vScr = new Point2D[n];
      float xScrMin=1e30F, xScrMax=-1e30F,
            yScrMin=1e30F, yScrMax=-1e30F;
      for (int i=1; i<n; i++)
      {  Point3D P = (Point3D)(w.elementAt(i));
         if (P == null)
         {  e[i] = null; vScr[i] = null;
         }
         else
         {  float x = v11 * P.x + v21 * P.y;
            float y = v12 * P.x + v22 * P.y + v32 * P.z;
            float z = v13 * P.x + v23 * P.y + v33 * P.z + v43;
            Point3D Pe = e[i] = new Point3D(x, y, z);
            float xScr = -Pe.x/Pe.z, yScr = -Pe.y/Pe.z;
            vScr[i] = new Point2D(xScr, yScr);
            if (xScr < xScrMin) xScrMin = xScr;
            if (xScr > xScrMax) xScrMax = xScr;
            if (yScr < yScrMin) yScrMin = yScr;
            if (yScr > yScrMax) yScrMax = yScr;
         }
      }
      float rangeX = xScrMax - xScrMin, rangeY = yScrMax - yScrMin;
      d = 0.95F * Math.min(dim.width/rangeX, dim.height/rangeY);
      imgCenter = new Point2D(d * (xScrMin + xScrMax)/2,
                              d * (yScrMin + yScrMax)/2);
      for (int i=1; i<n; i++)
      {  if (vScr[i] != null){vScr[i].x *= d; vScr[i].y *= d;}
      }
      return d * Math.max(rangeX, rangeY);
      // Maximum screen-coordinate range used in CvHLines for HP-GL
   }

   void planeCoeff()
   {  int nFaces = polyList.size();

      for (int j=0; j<nFaces; j++)
      {  HiddenLineDemo1Polygon3D pol = (HiddenLineDemo1Polygon3D)(polyList.elementAt(j));
         int[] nrs = pol.getNrs();
         if (nrs.length < 3) continue;
         int iA = Math.abs(nrs[0]), // Possibly negative
             iB = Math.abs(nrs[1]), // for HLines.
             iC = Math.abs(nrs[2]);
         Point3D A = e[iA], B = e[iB], C = e[iC];
         double
            u1 = B.x - A.x, u2 = B.y - A.y, u3 = B.z - A.z,
            v1 = C.x - A.x, v2 = C.y - A.y, v3 = C.z - A.z,
            a = u2 * v3 - u3 * v2,
            b = u3 * v1 - u1 * v3,
            c = u1 * v2 - u2 * v1,
            len = Math.sqrt(a * a + b * b + c * c), h;
            a /= len; b /= len; c /= len;
            h = a * A.x + b * A.y + c * A.z;
         pol.setAbch(a, b, c, h);
         Point2D A1 = vScr[iA], B1 = vScr[iB], C1 = vScr[iC];
         u1 = B1.x - A1.x; u2 = B1.y - A1.y;
         v1 = C1.x - A1.x; v2 = C1.y - A1.y;
         if (u1 * v2 - u2 * v1 <= 0) continue; // backface
         double inprod = a * sunX + b * sunY + c * sunZ;
         if (inprod < inprodMin) inprodMin = inprod;
         if (inprod > inprodMax) inprodMax = inprod;
      }
      inprodRange = inprodMax - inprodMin;
   }

   boolean vp(JPanel cvpanel, float dTheta, float dPhi, float fRho)
   {  theta += dTheta;
      phi += dPhi;
      float rhoNew = fRho * rho;
      if (rhoNew >= rhoMin && rhoNew <= rhoMax)
         rho = rhoNew;
      else
         return false;
      cvpanel.repaint();
      return true;
   }

   int colorCode(double a, double b, double c)
   {  double inprod = a * sunX + b * sunY + c * sunZ;
      return (int)Math.round(
         ((inprod - inprodMin)/inprodRange) * 255);
   }
}

// Polygon3D.java: Polygon in 3D, represented by vertex numbers
//                 referring to coordinates stored in an Obj3D object.
// Uses: Point2D (Section 1.5), Tools2D (Section 2.13),
//       Tria and Obj3D (both discussed above).

// Copied from Section 6.3 of
//    Ammeraal, L. (1998) Computer Graphics for Java Programmers,
//       Chichester: John Wiley.


class HiddenLineDemo1Polygon3D
{  private int[] nrs;
   private double a, b, c, h;
   private Tria[] t;

   HiddenLineDemo1Polygon3D(Vector vnrs)
   {  int n = vnrs.size();
      nrs = new int[n];
      for (int i=0; i<n; i++)
         nrs[i] = ((Integer)vnrs.elementAt(i)).intValue();
   }

   int[] getNrs(){return nrs;}
   double getA(){return a;}
   double getB(){return b;}
   double getC(){return c;}
   double getH(){return h;}
   void setAbch(double a, double b, double c, double h)
   {  this.a = a; this.b = b; this.c = c; this.h = h;
   }
   Tria[] getT(){return t;}

   void triangulate(HiddenLineDemo1Obj3D obj)
   // Successive vertex numbers (CCW) in vector nrs.
   // Resulting triangles will be put in array t.
   {  int n = nrs.length;         // n > 2 is required
      int[] next = new int[n];
      t = new Tria[n - 2];
      Point2D[] vScr = obj.getVScr();
      int iA=0, iB, iC;
      int j = n - 1;
      for (int i=0; i<n; i++){next[j] = i; j = i;}
      for (int k=0; k<n-2; k++)
      {  // Find a suitable triangle, consisting of two edges
         // and an internal diagonal:
         Point2D A, B, C;
         boolean found = false;
         int count = 0, nA = -1, nB = 0, nC = 0, nj;
         while (!found && ++count < n)
         {  iB = next[iA]; iC = next[iB];
            nA = Math.abs(nrs[iA]); A = vScr[nA];
            nB = Math.abs(nrs[iB]); B = vScr[nB];
            nC = Math.abs(nrs[iC]); C = vScr[nC];
            if (Tools2D.area2(A, B, C) >= 0)
            {  // Edges AB and BC; diagonal AC.
               // Test to see if no vertex (other than A,
               // B, C) lies within triangle ABC:
               j = next[iC]; nj = Math.abs(nrs[j]);
               while (j != iA &&
                     (nj == nA || nj == nB || nj == nC ||
                     !Tools2D.insideTriangle(A, B, C, vScr[nj])))
                  {  j = next[j]; nj = Math.abs(nrs[j]);
                  }
               if (j == iA)
               {  // Triangle found:
                  t[k] = new Tria(nA, nB, nC);
                  next[iA] = iC;
                  found = true;
               }
            }
            iA = next[iA];
         }
         if (count == n)
         {  // Degenerated polygon, possibly with all
            // vertices on one line.
            if (nA >= 0) t[k] = new Tria(nA, nB, nC);
            else
            {  System.out.println("Nonsimple polygon");
               System.exit(1);
            }
         }
      }
   }
}

