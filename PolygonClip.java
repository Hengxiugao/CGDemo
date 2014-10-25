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

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;


public class PolygonClip extends Canvas
{
	private char Boundary = 'L';
	private Poly poly = null;
	private float rWidth = 10.0F, rHeight = 7.5F, pixelSize;
	private int X0, Y0, centerX, centerY;
	private int drawXmin = 5;
	private int drawXmax = 500;
	private int drawYmin = 5;
	private int drawYmax = 500;
	//private boolean ready = true;
	private Graphics G;
	//private boolean DrawLineFlag = true;
	private boolean ButtonPressedFlag = false;
	//private boolean ProcessTerminated = false;
	private int processState = 0;
	private String ClippedPolyVertices = "Testing";
	Font textFontBold = new Font("Arial", Font.BOLD, 14);
	Font textFont = new Font("Arial", Font.PLAIN, 14);

	PolygonClip()
	{
		setBackground(Color.white);
		setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		repaint();

		addMouseListener(new MouseAdapter()
		{
			public void mousePressed(MouseEvent evt)
			{
				int X = evt.getX(), Y = evt.getY();

				// Check to see if STEP button was pressed
				if (X>639 && X<706 &&
					Y>359 && Y<386 &&
					processState > 2)
				{
					ButtonPressedFlag = true;
					repaint();
				}
				else
				{
					// Only process clicks inside drawing area
					if (X>=drawXmin && X<=drawXmax &&
						Y>=drawYmin && Y<=drawYmax)
					{
						if ((processState == 0) | (processState > 2))	// Start new polygon
						{
							poly = new Poly();
							X0 = X; Y0 = Y;		//remember starting point
							//ready = false;
							//ProcessTerminated = false;
							//DrawLineFlag = true;
							ButtonPressedFlag = false;
							processState = 1;
							Boundary = 'L';
							setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
						}
						float x = fx(X), y = fy(Y);
						if (poly.size() > 2 &&
							Math.abs(X - X0) < 3 &&
							Math.abs(Y - Y0) < 3)
						{
							//ready = true;
							processState = 2;
							setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
						}
						else
						{
							poly.addVertex(new PClipPoint2D(x, y, poly.LabelsForVertices++));
						}
						repaint();
					}
				}
 			}
		});

	}

	void initgr()
	{
		int maxX = drawXmax, maxY = drawYmax;
		int minX = drawXmin, minY = drawYmin;
		int drawWidth = (maxX-minX+1);
		int drawHeight = (maxY-minY+1);

		pixelSize = Math.max(rWidth/drawWidth, rHeight/drawHeight);
		centerX = minX+(drawWidth/2)-1; centerY = minY+(drawHeight/2)-1;
	}

	int iX(float x){return Math.round(centerX + x/pixelSize);}

	int iY(float y){return Math.round(centerY - y/pixelSize);}

	float fx(int X){return (X - centerX) * pixelSize;}

	float fy(int Y){return (centerY - Y) * pixelSize;}

	void drawLine(Graphics g, float xP, float yP, float xQ, float yQ)
	{
		g.drawLine(iX(xP), iY(yP), iX(xQ), iY(yQ));
	}

	void drawLineWithLabels(Graphics g, float xP, float yP, char Label1, float xQ, float yQ, char Label2, char lineColor)
	{
		if (lineColor == 'C')	g.setColor(Color.CYAN);
		else					g.setColor(Color.BLACK);
		g.drawLine(iX(xP), iY(yP), iX(xQ), iY(yQ));

		g.setColor(Color.BLACK);
		g.setFont(textFontBold);
		g.drawString(Character.toString(Label1),iX(xP)+3,iY(yP)-3);
		g.drawString(Character.toString(Label2),iX(xQ)+3,iY(yQ)-3);
		g.fillOval(iX(xQ)-3, iY(yQ)-3, 6, 6);
		g.fillOval(iX(xP)-3, iY(yP)-3, 6, 6);
	}

	void drawBoundary(Graphics g, float xP, float yP, float xQ, float yQ, char b)
	{
		Graphics2D g2 = (Graphics2D)g;

		//g2.setStroke(new BasicStroke(1.0F));
		//g2.setColor(Color.WHITE);
		//g2.drawLine(iX(xP), iY(yP), iX(xQ), iY(yQ));

		//g2.setColor(Color.GREEN);
		//g2.drawLine(iX(xP), iY(yP), iX(xQ), iY(yQ));

		g.setColor(Color.GREEN);
		g.drawLine(iX(xP), iY(yP), iX(xQ), iY(yQ));

		g.setColor(Color.RED);
		g.setFont(textFontBold);

		if (b == 'L')
			g.drawString("Clipping along LEFT edge...", 545, 180);
		else if (b == 'T')
			g.drawString("Clipping along TOP edge...", 545, 180);
		else if (b == 'R')
			g.drawString("Clipping along RIGHT edge...", 545, 180);
		else if (b == 'B')
			g.drawString("Clipping along BOTTOM edge...", 545, 180);

		g2.setColor(Color.BLACK);
	}

	void drawDottedLine(Graphics g, float xP, float yP, char Label1, float xQ, float yQ, char Label2)
	{
		Graphics2D g2 = (Graphics2D)g;
		Stroke s = new BasicStroke(1.0f,					// Width
						   BasicStroke.CAP_SQUARE,			// End cap
						   BasicStroke.JOIN_MITER,			// Join style
						   10.0f,							// Miter limit
						   new float[] {3.0f,3.0f},			// Dash pattern
						   0.0f);							// Dash phase

		g2.setStroke(new BasicStroke(2.0F));
		g2.setColor(Color.WHITE);
		g2.drawLine(iX(xP), iY(yP), iX(xQ), iY(yQ));

		g2.setStroke(s);
		g2.setColor(Color.RED);
		g2.drawLine(iX(xP), iY(yP), iX(xQ), iY(yQ));
		g.setColor(Color.BLACK);
		g.setFont(textFontBold);
		g.drawString(Character.toString(Label1),iX(xP)+3,iY(yP)-3);
		g.drawString(Character.toString(Label2),iX(xQ)+3,iY(yQ)-3);
		g.fillOval(iX(xQ)-3, iY(yQ)-3, 6, 6);
		g.fillOval(iX(xP)-3, iY(yP)-3, 6, 6);
	}

	void drawPoly(Poly poly)
	{
		int n = poly.size();

		if (n == 0) return;
		PClipPoint2D A = poly.vertexAt(n - 1);
		//System.out.println("Old Polygon draw");
		for (int i=0; i<n; i++)
		{
			PClipPoint2D B = poly.vertexAt(i);
			//System.out.println("Drawing polygon line " + A.Label + " to " + B.Label);
			drawLineWithLabels(G, A.x, A.y, A.Label, B.x, B.y, B.Label, 'B');
		 	A = B;
		}
	}

	void drawPartialPoly(Poly poly)
	{
		int n = poly.sizeP1();

		if (n < 2) return;
		PClipPoint2D A = poly.vertexAtP1(0);
		//System.out.println("Partial Polygon draw");
		for (int i=1; i<n; i++)
		{
			PClipPoint2D B = poly.vertexAtP1(i);
			//System.out.println("Drawing polygon line " + A.Label + " to " + B.Label);
			drawLineWithLabels(G, A.x, A.y, A.Label, B.x, B.y, B.Label, 'B');
		 	A = B;
		}
	}

	void drawDotted()
	{
		for(int i=0;i<poly.DottedPoly.size();i++)
		{
			//System.out.println("Dotted line " + i);
			DottedLines AB = (DottedLines) poly.DottedPoly.elementAt(i);
			drawDottedLine(G, AB.xp, AB.yp, AB.Label1, AB.xq, AB.yq, AB.Label2);
		}
	}

	void listClippedVertices()
	{
		G.setFont(textFont);
		G.setColor(Color.BLACK);
		G.drawString(poly.getClippedPolyVertices(), 565, 140);
	}

	void drawClippingLine(PClipPoint2D A, PClipPoint2D B)
	{
		//G.setColor(Color.WHITE);
		//drawLine(G,A.x,A.y,B.x,B.y);
		//G.setColor(Color.CYAN);
		drawLineWithLabels(G, A.x, A.y, A.Label, B.x, B.y, B.Label, 'C');
	}

	public void paint(Graphics g)
	{
		this.G = g;

		Dimension d = getSize();
		initgr();
		float xmin = -rWidth/3, xmax = rWidth/3,
			ymin = -rHeight/3, ymax = rHeight/3;
		String[] title = {
			"Sutherland-Hodgman",
			"Polygon Clipping Algorithm",
			"Demonstration"};

		// Draw Title
		Font titleFont = new Font("Arial", Font.BOLD, 18);
		g.setFont(titleFont);
		g.setColor(Color.black);
		g.drawString(title[0], 570, 30);
		g.drawString(title[1], 545, 50);
		g.drawString(title[2], 595, 70);
		g.drawLine(535, 90, 795, 90);

		// Draw outline for canvas area
		g.setColor(Color.black);
		g.fillRect(1, 1, 503, 3);
		g.fillRect(1, 1, 3, 503);
		g.fillRect(1, 501, 503, 3);
		g.fillRect(501, 1, 3, 503);

		// Draw step button
		g.setFont(textFont);
		g.setColor(Color.red);
		g.drawRect(630, 360, 65, 25);
		g.setColor(Color.black);
		g.drawRect(629, 359, 67, 27);
		g.drawRect(631, 361, 63, 23);
		g.drawString("STEP", 645, 377);

		// Draw clipping rectangle:
		g.setColor(Color.blue);
		drawLine(g, xmin, ymin, xmax, ymin);
		drawLine(g, xmax, ymin, xmax, ymax);
		drawLine(g, xmax, ymax, xmin, ymax);
		drawLine(g, xmin, ymax, xmin, ymin);

		// Draw text and polygon

		// Polygon not started
		if (processState == 0)
		{
			g.setFont(textFontBold);
			g.setColor(Color.RED);
			g.drawString("Click inside drawing area", 545, 180);
			g.drawString("to select polygon vertices.", 565, 200);
			return;
		}

		// Polygon not finished
		if (processState == 1)
		{
			g.setFont(textFontBold);
			g.setColor(Color.RED);
			g.drawString("Click inside drawing area", 545, 180);
			g.drawString("to select polygon vertices.", 565, 200);

			g.setColor(Color.black);
			if (poly != null)
			{
				int n = poly.size();
				if (n > 0)
				{
					PClipPoint2D A = poly.vertexAt(0);
					// Show tiny rectangle around first vertex:
					g.drawRect(iX(A.x)-3, iY(A.y)-3, 6, 6);
					// Draw incomplete polygon:
					for (int i=1; i<n; i++)
					{
						PClipPoint2D B = poly.vertexAt(i);
						drawLineWithLabels(g, A.x, A.y, A.Label, B.x, B.y, B.Label, 'B');
						A = B;
					}

				}
			}
			return;
		}

		// Polygon finished - no clipping done
		if (processState == 2)
		{
  			g.setFont(textFontBold);
 			g.setColor(Color.RED);
            g.drawString("Polygon selection complete.", 545, 180);
            g.drawString("Click STEP to clip polygon.", 565, 200);
			g.setColor(Color.black);
			g.drawString("Clipped polygon vertices: ", 545, 120);
			drawPoly(poly);

			PClipPoint2D P;
			P = poly.vertexAt(poly.size() - 1);
			poly.MaxOriginalVertex = P.Label;
			processState = 3;
			return;
		}

		// Polygon finished - start clipping
		// Show boundary and highlight new line being clipped
		if ((processState == 3) && (ButtonPressedFlag == true))
		{
  			g.setFont(textFontBold);
			g.setColor(Color.black);
			g.drawString("Clipped polygon vertices: ", 545, 120);

 			if(Boundary == 'L')	drawBoundary(g, xmin, rHeight/2, xmin, -rHeight/2, Boundary);
 			if(Boundary == 'B')	drawBoundary(g, rWidth/2, ymin, -rWidth/2, ymin, Boundary);
			if(Boundary == 'R')	drawBoundary(g, xmax, rHeight/2, xmax, -rHeight/2, Boundary);
            if(Boundary == 'T')	drawBoundary(g, rWidth/2, ymax, -rWidth/2, ymax, Boundary);

            PClipPoint2D P, Q;
            P = poly.vertexAt(poly.Counter);
            if (poly.Counter == poly.size() - 1)
            	Q = poly.vertexAt(0);
            else
            	Q = poly.vertexAt(poly.Counter + 1);
            //System.out.println("poly Counter is " + poly.Counter);

            drawPoly(poly);
            drawClippingLine(P, Q);
            drawDotted();
            listClippedVertices();
            processState = 4;
            return;
		}

		// Clip current line
		if ((processState == 4) && (ButtonPressedFlag == true))
		{
  			g.setFont(textFontBold);
			g.setColor(Color.black);
			g.drawString("Clipped polygon vertices: ", 545, 120);

 			if(Boundary == 'L')	drawBoundary(g, xmin, rHeight/2, xmin, -rHeight/2, Boundary);
 			if(Boundary == 'B')	drawBoundary(g, rWidth/2, ymin, -rWidth/2, ymin, Boundary);
			if(Boundary == 'R')	drawBoundary(g, xmax, rHeight/2, xmax, -rHeight/2, Boundary);
            if(Boundary == 'T')	drawBoundary(g, rWidth/2, ymax, -rWidth/2, ymax, Boundary);

            poly.clip(xmin, ymin, xmax, ymax, Boundary);
            drawPartialPoly(poly);
            drawPoly(poly);
            drawDotted();
            listClippedVertices();
            processState = 3;

            if (poly.Counter == poly.size())	// Finished with the polygon
            {
				if (Boundary != 'B')			// Advance to next boundary
				{
					if (Boundary == 'L')		Boundary = 'T';
					else if (Boundary == 'T')	Boundary = 'R';
					else if (Boundary == 'R')	Boundary = 'B';

					poly.Counter = 0;
					poly.ClippedPolyVertices = " ";
					poly.v = poly.poly1;
					poly.poly1 = new Vector(1);
				}
				else						// Finished with all boundaries
				{
					poly.Counter = 0;
					poly.v = poly.poly1;
					processState = 5;
				}

			}
            return;
		}

		// Clipping is completed
		if ((processState == 5) && (ButtonPressedFlag == true))
		{
  			g.setFont(textFontBold);
			g.setColor(Color.black);
			g.drawString("Clipped polygon vertices: ", 545, 120);

			drawPoly(poly);
			drawDotted();
			listClippedVertices();
			//DrawLineFlag = true;
			//ProcessTerminated = true;

			g.setFont(textFontBold);
			g.setColor(Color.RED);
			g.drawString("Polygon clipping complete.", 545, 180);
			g.drawString("Click inside drawing area", 545, 200);
			g.drawString("to select new polygon vertices.", 565, 210);
			return;
		}
	}
}

class Poly
{
    Vector v = new Vector(1);

   	void addVertex(PClipPoint2D P){v.addElement(P);}

   	int size(){return v.size();}

   	int sizeP1(){return poly1.size();}

   	Vector DottedPoly = new Vector(1);
   	Vector poly1 = new Vector(1);
   	int Counter=0;
   	char LabelsForVertices = 'A';
   	char MaxOriginalVertex = ' ';
   	String ClippedPolyVertices = " ";

   	PClipPoint2D vertexAt(int i)
   	{
		return (PClipPoint2D)v.elementAt(i);
   	}

   	PClipPoint2D vertexAtP1(int i)
   	{
		return (PClipPoint2D)poly1.elementAt(i);
   	}

	String getClippedPolyVertices()
	{
		return(ClippedPolyVertices);
	}

   	void clip(float xmin, float ymin, float xmax, float ymax, char Boundary)
   	{
		// Sutherland-Hodgman polygon clipping:
      	int n;
      	boolean sIns, pIns;
      	float  x,y;
      	PClipPoint2D A, B;

      	if ((n = size()) == 0) return;

      	if(Counter == size()-1)
          	B = vertexAt(0);
      	else
          	B = vertexAt(Counter+1);
      	A = vertexAt(Counter);

      	// Clip against x == xmax:
      	if(Boundary == 'R')
      	{
          	sIns = A.x <= xmax; pIns = B.x <= xmax;

      		//If A is inside and B outside
          	if (sIns != pIns && sIns == true)
          	{
              	y =  A.y + (B.y - A.y) * (xmax - A.x)/(B.x - A.x) ;
              	poly1.addElement(new PClipPoint2D(xmax,y,LabelsForVertices));
              	DottedPoly.addElement(new DottedLines(xmax,y,LabelsForVertices++, B.x, B.y, B.Label));

              	//Update The Text Box ClippedPolyVertices
             	ClippedPolyVertices = "";
             	for(int i=0;i<poly1.size();i++)
             	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
             	}
          	}

       		//If B is inside and A outside
          	else if (sIns != pIns && pIns == true)
          	{
              	y =  A.y + (B.y - A.y) * (xmax - A.x)/(B.x - A.x) ;
              	poly1.addElement(new PClipPoint2D(xmax,y,LabelsForVertices));
              	DottedPoly.addElement(new DottedLines(xmax,y,LabelsForVertices++,A.x, A.y, A.Label));

              	//Update ClippedPolyVertices
             	ClippedPolyVertices = "";
             	for(int i=0;i<poly1.size();i++)
             	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
             	}
           	}
          	else if (sIns == pIns && sIns == false)
          	{
           		DottedPoly.addElement(new DottedLines(B.x,B.y,B.Label,A.x, A.y, A.Label));
          	}
          	if (pIns)
          	{
              	poly1.addElement(B);

              	//Update ClippedPolyVertices
             	ClippedPolyVertices = "";
             	for(int i=0;i<poly1.size();i++)
             	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
             	}
          	}

          	Counter++;
          	return;
      	}	//Boundary = R


      	// Clip against x == xmin:
      	if(Boundary == 'L')
      	{
         	sIns = A.x >= xmin; pIns = B.x >= xmin;

         	//If A is inside and B outside
          	if (sIns != pIns && sIns == true)
          	{
              	y = A.y + (B.y - A.y) * (xmin - A.x)/(B.x - A.x);
              	poly1.addElement(new PClipPoint2D(xmin,y,LabelsForVertices));
           		DottedPoly.addElement(new DottedLines(xmin,y,LabelsForVertices++,B.x, B.y, B.Label));

              	//Update The Text Box ClippedPolyVertices
              	ClippedPolyVertices = ""  ;
              	for(int i=0;i<poly1.size();i++)
              	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
              	}
          	}

       		//If B is inside and A outside
          	else if (sIns != pIns && pIns == true)
          	{
              	y = A.y + (B.y - A.y) * (xmin - A.x)/(B.x - A.x);
              	poly1.addElement(new PClipPoint2D(xmin,y,LabelsForVertices));
           		DottedPoly.addElement(new DottedLines(xmin,y,LabelsForVertices++,A.x, A.y, A.Label));

              	//Update The Text Box ClippedPolyVertices
              	ClippedPolyVertices = "";
              	for(int i=0;i<poly1.size();i++)
              	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                	 ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
              	}
          	}
          	else if (sIns == pIns && sIns == false)
          	{
           		DottedPoly.addElement(new DottedLines(B.x,B.y,B.Label,A.x, A.y, A.Label));
          	}
          	if (pIns)
          	{
             	poly1.addElement(B);

             	//Update The Text Box ClippedPolyVertices
             	ClippedPolyVertices = "";
             	for(int i=0;i<poly1.size();i++)
             	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
             	}
          	}
          	Counter++;
          	return;
      	}	//Boundary = L


      	// Clip against y == ymax:
      	if(Boundary == 'T')
      	{
          	sIns = A.y <= ymax; pIns = B.y <= ymax;

         	//If A is inside and B outside
          	if (sIns != pIns && sIns == true)
          	{
             	x =  A.x + (B.x - A.x) * (ymax - A.y)/(B.y - A.y);
              	poly1.addElement(new PClipPoint2D(x,ymax,LabelsForVertices));
           		DottedPoly.addElement(new DottedLines(x,ymax,LabelsForVertices++, B.x, B.y, B.Label));

              	//Update The Text Box ClippedPolyVertices
             	ClippedPolyVertices = "";
             	for(int i=0;i<poly1.size();i++)
             	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
             	}
          	}

       		//If B is inside and A outside
          	else if (sIns != pIns && pIns == true)
          	{
              	x =  A.x + (B.x - A.x) * (ymax - A.y)/(B.y - A.y);
              	poly1.addElement(new PClipPoint2D(x,ymax,LabelsForVertices));
           		DottedPoly.addElement(new DottedLines(x,ymax,LabelsForVertices++, A.x, A.y, A.Label));

              	//Update The Text Box ClippedPolyVertices
             	ClippedPolyVertices = "";
             	for(int i=0;i<poly1.size();i++)
             	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
             	}
          	}
          	else if (sIns == pIns && sIns == false)
          	{
           		DottedPoly.addElement(new DottedLines(B.x,B.y,B.Label,A.x, A.y,A.Label));
          	}
          	if (pIns)
          	{
              	poly1.addElement(B);

              	//Update The Text Box ClippedPolyVertices
            	ClippedPolyVertices = "";
             	for(int i=0;i<poly1.size();i++)
             	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
             	}
          	}
          	Counter++;
          	return;
      	}	// Boundary = T

      	// Clip against y == ymin:
      	if(Boundary == 'B')
      	{
         	sIns = A.y >= ymin; pIns = B.y >= ymin;

         	//If A is inside and B outside
          	if (sIns != pIns && sIns == true)
          	{
              	x = A.x + (B.x - A.x) * (ymin - A.y)/(B.y - A.y);
              	poly1.addElement(new PClipPoint2D(x,ymin,LabelsForVertices));
           		DottedPoly.addElement(new DottedLines(x, ymin, LabelsForVertices++, B.x, B.y, B.Label));

              	//Update The Text Box ClippedPolyVertices
             	ClippedPolyVertices = "";
             	for(int i=0;i<poly1.size();i++)
             	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
             	}
          	}

       		//If B is inside and A outside
          	else if (sIns != pIns && pIns == true)
          	{
              	x = A.x + (B.x - A.x) * (ymin - A.y)/(B.y - A.y);
              	poly1.addElement(new PClipPoint2D(x,ymin,LabelsForVertices));
           		DottedPoly.addElement(new DottedLines(x,ymin,LabelsForVertices++,A.x, A.y, A.Label));

              	//Update The Text Box ClippedPolyVertices
             	ClippedPolyVertices = "";
             	for(int i=0;i<poly1.size();i++)
             	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
             	}
          	}
          	else if (sIns == pIns && sIns == false)
          	{
           		DottedPoly.addElement(new DottedLines(B.x,B.y,B.Label,A.x,A.y,A.Label));
          	}
         	if (pIns)
         	{
             	poly1.addElement(B);

             	//Update The Text Box ClippedPolyVertices
             	ClippedPolyVertices = "";
             	for(int i=0;i<poly1.size();i++)
             	{
                 	PClipPoint2D temp = (PClipPoint2D)poly1.elementAt(i);
                 	ClippedPolyVertices += Character.toString(temp.Label);
                 	if(i != poly1.size()-1)
                    	ClippedPolyVertices += ", ";
             	}
         	}
         	Counter++;
         	return;
      	}
   	}
}

class PClipPoint2D
{
	float x, y;
	char Label;

	PClipPoint2D(float x, float y, char Label){this.x = x; this.y = y; this.Label = Label;}
}

class DottedLines
{
	float xp, yp, xq, yq;
    char Label1, Label2;

    DottedLines(float xp, float yp, char Label1, float xq, float yq, char Label2)
    {
        this.xp = xp;
        this.xq = xq;
        this.yp = yp;
        this.yq = yq;
        this.Label1 = Label1;
        this.Label2 = Label2;
    }
}
