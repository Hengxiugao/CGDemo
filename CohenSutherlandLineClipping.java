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

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class CohenSutherlandLineClipping extends Canvas

{
	int X1=100000, Y1, X2=100000, Y2, X2temp=-1, Y2temp;
	int XP, YP, XQ, YQ, X1prime = -1, Y1prime = -1, X2prime = -1, Y2prime = -1;
	int XPprime=-1, YPprime=-1, XQprime=-1, YQprime=-1;
	int cP=-1, cQ=-1;

	// Border extents
	int bXmin=1, bYmin=1, bWidth=506, bHeight=506;

	// Grid area extents
	int gXmin=4, gXmax=503, gYmin=4, gYmax=503;

	// Convert device coordinates to grid coordinates
	int vX(int X) {return X - gXmin;}
	int vY(int Y) {return gYmax - Y;}

	// Convert grid coordinates to device coordinates
	int dvX(int X) {return X + gXmin;}
	int dvY(int Y) {return gYmax - Y;}

	// Clipping rectangle extents
	int rXmin=156, rXmax=350, rYmin=164, rYmax=338, rWidth=195, rHeight=175;
	int vXmin=vX(rXmin), vXmax=vX(rXmax), vYmin=vY(rYmax), vYmax=vY(rYmin);

	int curLn = 0, curStep = 0;
	boolean P1Selected = false, P2Selected = false;
	boolean PActive = false, QActive = false;
	boolean HilitePQ = false;
	boolean Top = false, Bottom = false, Left = false, Right = false;
	String errMsg = " ";

	int clipCode(int X, int Y)
	{	return
		(X < vXmin ? 8 : 0) | (X > vXmax ? 4 : 0) |
		(Y < vYmin ? 2 : 0) | (Y > vYmax ? 1 : 0);
	}

	public void newDemo()

	{	// When a new demo is started, variables need to be
		//   initialized and the points checked for validity

		if (P1Selected && P2Selected)

		{	cP = clipCode(XP, YP);
			cQ = clipCode(XQ, YQ);
		}
		else	// First time through
		{	cP = -1;
			cQ = -1;
		}
		PActive = false;
		QActive = false;
		X1prime = -1;
		X2prime = -1;
		Y1prime = -1;
		Y2prime = -1;
		HilitePQ = false;
		Top = false;
		Bottom = false;
		Left = false;
		Right = false;
		curLn = 0;
		curStep = 0;
	}

	public void stepPressed()

	{	// Events to process when the STEP button has been pressed
		//   Depending on the line of code being executed, the corresponding
		//   information on the screen is changed

		int dX = 0, dY = 0;

		if (P1Selected && P2Selected)
		switch(curStep)
		{	case 0:	// while (outcode_P OR outcode_Q != 0)
					if ((cP | cQ) != 0)
					{	curLn = 0;
						curStep = 10;
					}
					else
					{	curLn = 0;
						curStep = 99;
					}
					HilitePQ = true;
					break;

			case 10:	// if (outcode_P AND outcode_Q != 0) return;
					if ((cP & cQ) != 0)
					{	curLn = 1;
						curStep = 70;
					}
					else
					{	curLn = 1;
						curStep = 20;
					}
					break;

			case 20:	// else {start from outside endpoint
					if (cP != 0)
					{	PActive = true;
						QActive = false;
					}
					else
					if (cQ != 0)
					{	PActive = false;
						QActive = true;
					}
					curLn = 2;
					curStep = 30;
					break;

			case 30:	// in top-botom-right-left order
					// find a point I intersecting with a grid line
				//	Top = true;
					curLn = 3;
					curStep = 31;
					break;

			case 31:	dX = XQ - XP;
					dY = YQ - YP;
					if (cP != 0)
					{	if ((cP & 1) == 1)
						{	XPprime = XP + (vYmax-YP) * dX / dY;
							YPprime = vYmax;
							X1prime = dvX(XPprime);
							Y1prime = dvY(YPprime);
							curLn = 4;
							curStep = 50;
							Top = true;
						}
						else
						{	
							//bottom
							if ((cP & 2) == 2)
						{	XPprime = XP + (vYmin-YP) * dX / dY;
						YPprime = vYmin;
						X1prime = dvX(XPprime);
						Y1prime = dvY(YPprime);
						curLn = 4;
						curStep = 50;
						Bottom = true;
					}
							//right
					else
					{	if ((cP & 4) == 4)
					{	YPprime = YP + (vXmax-XP) * dY / dX;
					XPprime = vXmax;
					X1prime = dvX(XPprime);
					Y1prime = dvY(YPprime);
					curLn = 4;
					curStep = 50;
					Right = true;
				}
				else
				{	if ((cP & 8) == 8)
				{
					YPprime = YP + (vXmin-XP) * dY / dX;
					XPprime = vXmin;
					X1prime = dvX(XPprime);
					Y1prime = dvY(YPprime);
					curLn = 4;
					curStep = 50;
				Left = true;
				}
			
				}
						
					}
							
						}
					}
					else
					if (cQ != 0)
					{	if ((cQ & 1) == 1)
						{	XQprime = XQ + (vYmax-YQ) * dX / dY;
							YQprime = vYmax;
							X2prime = dvX(XQprime);
							Y2prime = dvY(YQprime);
							curLn = 4;
							curStep = 50;
							Top = true;
						}
						else
						{	
							if ((cQ & 2) == 2)
						{	XQprime = XQ + (vYmin-YQ) * dX / dY;
						YQprime = vYmin;
						X2prime = dvX(XQprime);
						Y2prime = dvY(YQprime);
						curLn = 4;
						curStep = 50;
						Bottom = true;
					}
							else{
								if ((cQ & 4) == 4)
								{	YQprime = YQ + (vXmax-XQ) * dY / dX;
								XQprime = vXmax;
								X2prime = dvX(XQprime);
								Y2prime = dvY(YQprime);
								curLn = 4;
								curStep = 50;
									Right = true;
								}
								else{
									if ((cQ & 8) == 8)
									{	YQprime = YQ + (vXmin-XQ) * dY / dX;
									XQprime = vXmin;
									X2prime = dvX(XQprime);
									Y2prime = dvY(YQprime);
									curLn = 4;
									curStep = 50;
										Left = true;
									}
									
								}
							}
						}
					}
					break;

		/*	case 32:	dX = XQ - XP;
					dY = YQ - YP;
					if (cP != 0)
					{	if ((cP & 2) == 2)
						{	XPprime = XP + (vYmin-YP) * dX / dY;
							YPprime = vYmin;
							X1prime = dvX(XPprime);
							Y1prime = dvY(YPprime);
							curLn = 4;
							curStep = 50;
						}
						else
						{	Right = true;
							Bottom = false;
							curStep = 33;
						}
					}
					else
					if (cQ != 0)
					{	if ((cQ & 2) == 2)
						{	XQprime = XQ + (vYmin-YQ) * dX / dY;
							YQprime = vYmin;
							X2prime = dvX(XQprime);
							Y2prime = dvY(YQprime);
							curLn = 4;
							curStep = 50;
						}
						else
						{	Right = true;
							Bottom = false;
							curStep = 33;
						}
					}
					break;

			case 33:	dX = XQ - XP;
					dY = YQ - YP;
					if (cP != 0)
					{	if ((cP & 4) == 4)
						{	YPprime = YP + (vXmax-XP) * dY / dX;
							XPprime = vXmax;
							X1prime = dvX(XPprime);
							Y1prime = dvY(YPprime);
							curLn = 4;
							curStep = 50;
						}
						else
						{	Left = true;
							Right = false;
							curStep = 34;
						}
					}
					else
					if (cQ != 0)
					{	if ((cQ & 4) == 4)
						{	YQprime = YQ + (vXmax-XQ) * dY / dX;
							XQprime = vXmax;
							X2prime = dvX(XQprime);
							Y2prime = dvY(YQprime);
							curLn = 4;
							curStep = 50;
						}
						else
						{	Left = true;
							Right = false;
							curStep = 34;
						}
					}
					break;

			case 34:	dX = XQ - XP;
					dY = YQ - YP;
					if (cP != 0)
					{	if ((cP & 8) == 8)
						{	YPprime = YP + (vXmin-XP) * dY / dX;
							XPprime = vXmin;
							X1prime = dvX(XPprime);
							Y1prime = dvY(YPprime);
							curLn = 4;
							curStep = 50;
						}
					}
					else
					if (cQ != 0)
					{	if ((cQ & 8) == 8)
						{	YQprime = YQ + (vXmin-XQ) * dY / dX;
							XQprime = vXmin;
							X2prime = dvX(XQprime);
							Y2prime = dvY(YQprime);
							curLn = 4;
							curStep = 50;
						}
					}
					break;
*/
			case 50:	// clip away outside line segment
					curLn = 5;
					curStep = 60;
					Top = false;
					Bottom = false;
					Right = false;
					Left = false;
					break;

			case 60:	// replace outside endpoint with I
					curLn = 6;
					if (YPprime >= 0)
					{	YP = YPprime;
						XP = XPprime;
						cP = clipCode(XP, YP);
						YPprime = -1;
						XPprime = -1;
						Y1prime = -1;
						X1prime = -1;
					}
					else
					if (YQprime >= 0)
					{	YQ = YQprime;
						XQ = XQprime;
						cQ = clipCode(XQ, YQ);
						YQprime = -1;
						XQprime = -1;
						Y2prime = -1;
						X2prime = -1;
					}
					curStep = 0;
					PActive = false;
					QActive = false;
					break;

			case 70:	// entire segment is outside clip rectangle
					XP = -1;
					YP = -1;
					XQ = -1;
					YQ = -1;
					curLn = 7;
					curStep = 99;
					break;

			case 99:	// clipping is complete
					curLn = 7;
					curStep = 100;
					break;
			case 100:
				P1Selected = false;
				P2Selected = false;
				PActive = false;
				QActive = false;
				XP = -1;
				YP = -1;
				XQ = -1;
				YQ = -1;
				curLn = 8;
				break;
		}
		else
			errMsg = "Please select two valid points before pressing STEP.";
	}

	public	String bitCode(int Code)
	{	String outCode = "";
		switch(Code)
		{	case 0:	outCode = "0000";
					break;
			case 1:	outCode = "1000";
					break;
			case 2:	outCode = "0100";
					break;
			case 4:	outCode = "0010";
					break;
			case 5:	outCode = "1010";
					break;
			case 6:	outCode = "0110";
					break;
			case 8:	outCode = "0001";
					break;
			case 9:	outCode = "1001";
					break;
			case 10:	outCode = "0101";
					break;
		}
		return outCode;
	}

	public void pointSelected()

	{	// When a point has been selected, convert the screen location to the
		//   conventional standard of (0,0) at the lower left of the grid

		if (P1Selected)
		{	XP = vX(X1);
			YP = vY(Y1);
		}
		if (P2Selected)
		{	XQ = vX(X2);
			YQ = vY(Y2);
		}
		newDemo();
	}

	CohenSutherlandLineClipping()
	{
		setBackground(Color.WHITE);
		newDemo();
		addMouseListener(new MouseAdapter()
		{	public void mousePressed(MouseEvent evt)

			{	// When the mouse is clicked, determine where
				//   it is on the screen and do the appropriate
				//   action, if any.
                int count = 0;
				int Xclick = 0, Yclick = 0;

				// Get the coordinates
				Xclick = evt.getX();
				Yclick = evt.getY();

				// Check to see if STEP button was pressed
				if (Xclick>631 && Xclick<694 &&
					Yclick>361 && Yclick<384)
				{	stepPressed();
				
				}
				else

				// Only process clicks inside grid border
				{	if (Xclick>=gXmin && Xclick<=gXmax &&
						Yclick>=gYmin && Yclick<=gYmax)
					{	if (!P1Selected)
						{	X1 = Xclick;
							Y1 = Yclick;
							P1Selected = true;
						}
						else
						{	if (!P2Selected)
							{	X2 = Xclick;
								Y2 = Yclick;
								P2Selected = true;
							}
							else
							{	X1 = Xclick;
								Y1 = Yclick;
								P2Selected = false;
							}
							X2temp = -1;
							Y2temp = -1;
						}
						pointSelected();
					}
				}
				repaint();
			}
		});
/*
		addMouseMotionListener(new MouseMotionAdapter()
		{	public void mouseMoved(MouseEvent evt)

			{	// When the mouse is moved, if one point has been
				//   already selected but not the other, display
				//   the line. Otherwise ignore the motion.

				int Xloc = 0, Yloc = 0;

				if (P1Selected && !P2Selected)
				{	// Get the coordinates
					Xloc = evt.getX();
					Yloc = evt.getY();

					// Only process locations inside grid border
					{	if (Xloc>=gXmin && Xloc<=gXmax &&
							Yloc>=gYmin && Yloc<=gYmax)
						{	X2temp = Xloc;
							Y2temp = Yloc;
							repaint();
						}
					}
				}
			}
		});*/
	}


	public void paint(Graphics g)
	{
		// Produce the graphics display in the window

		int dashLen=10;
		int XOffset = 1, YOffset = 1, i = 0;
		String[] title = {
			"Cohen-Sutherland",
			"Line-Clipping Algorithm",
			"Demonstration"};
		String[] algorithm = {
			"while (outcode_P OR outcode_Q != 0)",
			"   if (outcode_P AND outcode_Q != 0) return;",
			"   else {start from outside endpoint;",
			"      in top-bottom-right-left order;",
			"      find a point I intersecting with a grid line;",
			"      clip away outside line segment;",
			"      replace outside endpoint with I;",
			"   }"};
		Font titleFont = new Font("Arial", Font.BOLD, 18);
		Font smallBoldFont = new Font("Arial", Font.BOLD, 10);
		Font mediumBoldFont = new Font("Arial", Font.BOLD, 12);
		Font algorithmFont = new Font("Arial", Font.PLAIN, 14);
		Font algorithmHiFont = new Font("Arial", Font.BOLD, 14);
		Font outcodeFont = new Font("Arial", Font.BOLD, 34);



		// Draw Title
		g.setFont(titleFont);
		g.setColor(Color.black);
		g.drawString(title[0], 580, 30);
		g.drawString(title[1], 560, 50);
		g.drawString(title[2], 600, 70);
		g.drawLine(545, 90, 790, 90);

		// Draw algorithm text
		g.setFont(algorithmFont);
		int textOffset = 120;
		for (i=0; i<=7; i++)
		{	if (i == curLn)
			{	g.setColor(Color.red);
				g.setFont(algorithmHiFont);
				g.drawString(algorithm[i], 518, textOffset);
				g.setColor(Color.black);
				g.setFont(algorithmFont);
			}
			else
				g.drawString(algorithm[i], 518, textOffset);
			textOffset = textOffset + 20;
		}

		// Draw the border
		g.setColor(Color.black);
		g.drawRect(bXmin, bYmin, bWidth-1, bHeight-1);
		g.drawRect(bXmin+1, bYmin+1, bWidth-3, bHeight-3);
		g.drawRect(bXmin+2, bYmin+2, bWidth-5, bHeight-5);

		// Write message
		if (curLn == 7)
		{	g.setColor(Color.red);
			g.setFont(algorithmHiFont);
			g.drawString("Clipping is complete!", 580, 330);
			g.drawString("Click again to start a new line", 560, 345);
		}

		// Draw step button
		g.setColor(Color.red);
		g.drawRect(630, 360, 65, 25);
		g.setColor(Color.black);
		g.drawRect(629, 359, 67, 27);
		g.drawRect(631, 361, 63, 23);
		g.setFont(algorithmFont);
		g.drawString("STEP", 645, 377);

		// Draw information about algorithm and instructions
		Font smallFont = new Font("Arial", Font.PLAIN, 10);
		g.setFont(smallFont);
		g.drawString("Press 'STEP' button to process the algorithm.", 540, 420);
		g.setFont(algorithmFont);

		// Write error messages, if any
		if (errMsg != " ")
		{	g.setColor(Color.red);
			g.drawString("***ERROR***: " + errMsg, 20, 520);
			errMsg = " ";
		}

		// Shade in the active area
		g.setColor(Color.pink);
		if (Top)
			g.fillRect(gXmin, gYmin, gXmax-gXmin+1, rYmin-gYmin);
		if (Bottom)
			g.fillRect(gXmin, rYmax+1, gXmax-gXmin+1, gYmax-rYmax);
		if (Right)
			g.fillRect(rXmax+1, gYmin, gXmax-rXmax, gYmax-gYmin+1);
		if (Left)
			g.fillRect(gXmin, gYmin, rXmin-gXmin, gYmax-gYmin+1);

		// Draw the clipping rectangle
		g.setColor(Color.green);
		g.drawRect(rXmin, rYmin, rWidth-1, rHeight-1);
		g.drawRect(rXmin+1, rYmin+1, rWidth-3, rHeight-3);
		g.drawRect(rXmin+2, rYmin+2, rWidth-5, rHeight-5);
		g.setColor(Color.white);
		g.fillRect(rXmin+3, rYmin+3, rWidth-7, rHeight-7);

		// Draw the quadrant lines
		g.setColor(Color.green);
		for (i=rXmax+1; i<=gXmax-10; i=i+20)
		{	if (i+19 > gXmax)
				dashLen = gXmax-(i+10)+1;
			else
				dashLen = 10;
			g.fillRect(i+10, rYmin, dashLen, 3);
			g.fillRect(i+10, rYmax-2, dashLen, 3);
		}
		for (i=rXmin-1; i>=gXmin+10; i=i-20)
		{	if (i-19 < gXmin)
			{	dashLen = (i-10) - gXmin + 1;
				g.fillRect(gXmin, rYmin, dashLen, 3);
				g.fillRect(gXmin, rYmax-2, dashLen, 3);}
			else
			{	dashLen = 10;
				g.fillRect(i-19, rYmin, dashLen, 3);
				g.fillRect(i-19, rYmax-2, dashLen, 3);}}
		for (i=rYmax+1; i<=gYmax-10; i=i+20)
		{	if (i+19 > gYmax)
				dashLen = gYmax-(i+10)+1;
			else
				dashLen = 10;
			g.fillRect(rXmin, i+10, 3, dashLen);
			g.fillRect(rXmax-2, i+10, 3, dashLen);}
		for (i=rYmin-1; i>=gYmin+10; i=i-20)
		{	if (i-19 < gYmin)
			{	dashLen = (i-10) - gYmin + 1;
				g.fillRect(rXmin, gYmin, 3, dashLen);
				g.fillRect(rXmax-2, gYmin, 3, dashLen);}
			else
			{	dashLen = 10;
				g.fillRect(rXmin, i-19, 3, dashLen);
				g.fillRect(rXmax-2, i-19, 3, dashLen);}}

		// Label the quadrant lines
		g.setFont(smallBoldFont);
		g.setColor(Color.black);
		g.drawString("y", gXmin+2, rYmin-5);
		g.drawString("max", gXmin+8, rYmin-3);
		g.drawString("y", gXmin+2, rYmax-7);
		g.drawString("min", gXmin+8, rYmax-5);
		g.drawString("x", rXmin+4, gYmin+8);
		g.drawString("min", rXmin+10, gYmin+10);
		g.drawString("x", rXmax+2, gYmin+8);
		g.drawString("max", rXmax+8, gYmin+10);

		// Show the outcodes for the quadrants
		g.setFont(outcodeFont);
		g.setColor(Color.gray);
		g.drawString("1001", 40, 100);
		g.drawString("0001", 40, 260);
		g.drawString("0101", 40, 430);
		g.drawString("1000", 220, 100);
		g.drawString("0000", 220, 260);
		g.drawString("0100", 220, 430);
		g.drawString("1010", 390, 100);
		g.drawString("0010", 390, 260);
		g.drawString("0110", 390, 430);

		// Draw any points and line already selected
		if (P1Selected)
		{	g.setColor(Color.blue);
			g.fillRect(X1-3, Y1-3, 7, 7);
			g.setColor(Color.black);
		}
		if (P2Selected)
		{	g.setColor(Color.blue);
			g.fillRect(X2-3, Y2-3, 7, 7);
			g.drawLine(X1, Y1, X2, Y2);
			g.setColor(Color.black);
		}
		if (X2temp >= 0)
		{	g.setColor(Color.gray);
			g.drawLine(X1, Y1, X2temp, Y2temp);
		}

		// Draw the current line highlighted if demo has begun
		g.setFont(mediumBoldFont);
		if (curLn == 5)
		{	if (XPprime >= 0)
			{	g.setColor(Color.red);
				g.fillRect(dvX(XPprime)-3, dvY(YPprime)-3, 7, 7);
				g.fillRect(dvX(XQ)-3, dvY(YQ)-3, 7, 7);
				g.drawLine(dvX(XPprime), dvY(YPprime), dvX(XQ), dvY(YQ));
				g.drawLine(dvX(XPprime), dvY(YPprime)+1, dvX(XQ), dvY(YQ)+1);
				g.drawLine(dvX(XPprime), dvY(YPprime)-1, dvX(XQ), dvY(YQ)-1);
				g.setColor(Color.black);
				g.drawString("outcode="+bitCode(cP), dvX(XP)+20, dvY(YP)+20);
				g.drawString("outcode="+bitCode(cQ), dvX(XQ)+20, dvY(YQ)+20);
			}
			else
			if (XQprime >= 0)
			{	g.setColor(Color.red);
				g.fillRect(dvX(XP)-3, dvY(YP)-3, 7, 7);
				g.fillRect(dvX(XQprime)-3, dvY(YQprime)-3, 7, 7);
				g.drawLine(dvX(XP), dvY(YP), dvX(XQprime), dvY(YQprime));
				g.drawLine(dvX(XP), dvY(YP)+1, dvX(XQprime), dvY(YQprime)+1);
				g.drawLine(dvX(XP), dvY(YP)-1, dvX(XQprime), dvY(YQprime)-1);
				g.setColor(Color.black);
				g.drawString("outcode="+bitCode(cP), dvX(XP)+20, dvY(YP)+20);
				g.drawString("outcode="+bitCode(cQ), dvX(XQ)+20, dvY(YQ)+20);
			}
		}
		else
		if (HilitePQ && XP >= 0 && XQ >= 0)
		{	g.setColor(Color.red);
			g.fillRect(dvX(XP)-3, dvY(YP)-3, 7, 7);
			g.fillRect(dvX(XQ)-3, dvY(YQ)-3, 7, 7);
			g.drawLine(dvX(XP), dvY(YP), dvX(XQ), dvY(YQ));
			g.drawLine(dvX(XP), dvY(YP)+1, dvX(XQ), dvY(YQ)+1);
			g.drawLine(dvX(XP), dvY(YP)-1, dvX(XQ), dvY(YQ)-1);
			g.setColor(Color.black);
			g.drawString("outcode="+bitCode(cP), dvX(XP)+20, dvY(YP)+20);
			g.drawString("outcode="+bitCode(cQ), dvX(XQ)+20, dvY(YQ)+20);
		}

		// Highlight currently selected outer point
		if (PActive)
		{
			g.setColor(Color.black);
			g.fillOval(dvX(XP)-10, dvY(YP)-10, 20, 20);
			g.setColor(Color.red);
			g.fillOval(dvX(XP)-8, dvY(YP)-8, 16, 16);
		}
		else
		if (QActive)
		{
			g.setColor(Color.black);
			g.fillOval(dvX(XQ)-10, dvY(YQ)-10, 20, 20);
			g.setColor(Color.red);
			g.fillOval(dvX(XQ)-8, dvY(YQ)-8, 16, 16);
		}

		// Mark P or Q prime point
		if (X1prime >= 0)
		{
			g.setColor(Color.black);
			g.fillOval(X1prime-10, Y1prime-10, 20, 20);
			g.setColor(Color.orange);
			g.fillOval(X1prime-8, Y1prime-8, 16, 16);
		}
		else
		if (X2prime >= 0)
		{
			g.setColor(Color.black);
			g.fillOval(X2prime-10, Y2prime-10, 20, 20);
			g.setColor(Color.orange);
			g.fillOval(X2prime-8, Y2prime-8, 16, 16);
		}
	}
}

