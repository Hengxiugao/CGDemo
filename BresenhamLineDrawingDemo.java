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
import java.text.DecimalFormat;

public class BresenhamLineDrawingDemo extends Canvas  //tried JPanel then Panel

{
	int X1=100000, Y1, X2=100000, Y2;
	int curLn = 0;
	boolean P1Selected = false, P2Selected = false, badPoints = false;
	int curAlg = 0;
	int D = 0, H = 0, c = 0, M = 0, x = 0, y = 0;
	int X1Coord = 0, Y1Coord = 0, X2Coord = 0, Y2Coord = 0;
	int X1Out = 0, Y1Out = 0, X2Out = 0, Y2Out = 0;
	int [][] pixels = new int[10][10];
	String errMsg = " ";
	float smalld = 0.0F;
	boolean dAdjusted = false;


	public void startNewDemo()

	{	// When a new demo is started, variables need to be
		//   initialized and the points checked for validity

		for (int i=0; i<10; i++)
			for (int j=0; j<10; j++)
				pixels[i][j] = 0;

		if (P1Selected && P2Selected)
		{
			// Initialize some variables common to all algorithm forms

			curLn = 0;
			D = 0;
			smalld = 0.0F;
			x = X1Out;
			y = Y1Out;
			badPoints = false;
			curAlg = 0;

			// Determine which algorithm form to use

			if (X1Out<=X2Out)
			{
				if (Y1Out<=Y2Out)
				{
					if ((Y2Out-Y1Out)<=(X2Out-X1Out))
					{
						curAlg = 1;	// 0 to 45 degrees
						H = X2Out - X1Out;
						c = 2 * H;
						M = 2 * (Y2Out - Y1Out);
					}
					else
					{
						curAlg = 2;	// >45 to 90 degrees
						H = Y2Out - Y1Out;
						c = 2 * H;
						M = 2 * (X2Out - X1Out);
					}
				}
				else
				{
					if ((Y1Out-Y2Out)<=(X2Out-X1Out))
					{
						curAlg = 8;	// 315 to <360 degrees
						H = X2Out - X1Out;
						c = 2 * H;
						M = 2 * (Y1Out - Y2Out);
					}
					else
					{
						curAlg = 7;	// 270 to <315 degrees
						H = Y1Out - Y2Out;
						c = 2 * H;
						M = 2 * (X2Out - X1Out);
					}
				}
			}
			else
			{
				if (Y1Out<=Y2Out)
				{
					if ((Y2Out-Y1Out)<=(X1Out-X2Out))
					{
						curAlg = 4;	// 135 to 180 degrees
						H = X1Out - X2Out;
						c = 2 * H;
						M = 2 * (Y2Out - Y1Out);
					}
					else
					{
						curAlg = 3;	// >90 to <135 degrees
						H = Y2Out - Y1Out;
						c = 2 * H;
						M = 2 * (X1Out - X2Out);
					}
				}
				else
				{
					if ((Y1Out-Y2Out)<=(X1Out-X2Out))
					{
						curAlg = 5;	// >180 to 225 degrees
						H = X1Out - X2Out;
						c = 2 * H;
						M = 2 * (Y1Out - Y2Out);
					}
					else
					{
						curAlg = 6;	// >225 to <270 degrees
						H = Y1Out - Y2Out;
						c = 2 * H;
						M = 2 * (X1Out - X2Out);
					}
				}
			}

		}
		else	// First time through - no points selected yet
		{
			curAlg = 0;
			curLn = 0;
			D = 0;
			smalld = 0.0F;
			H = 0;
			c = 0;
			M = 0;
			x = 0;
			y = 0;
		}
	}

	public void stepPressed()

	{	// Events to process when the STEP button has been pressed
		//   Depending on the line of code being executed, the corresponding
		//   information on the screen is changed

		if (P1Selected && P2Selected && !badPoints)
		switch(curLn)
		{	case 0:	curLn = 1;
			break;

			case 1:
				if ((curAlg==1)|(curAlg==8))
				{	if (x <= X2Out)
						{	curLn = 2;
							pixels[x][y] = 1;
							if (y>0)	pixels[x][y-1] = 0;
							if (y<9)	pixels[x][y+1] = 0;
						}
					else curLn = 6;
				}

				if ((curAlg==2)|(curAlg==3))
				{	if (y <= Y2Out)
						{	curLn = 2;
							pixels[x][y] = 1;
							if (x>0)	pixels[x-1][y] = 0;
							if (x<9)	pixels[x+1][y] = 0;
						}
					else curLn = 6;
				}

				if ((curAlg==4)|(curAlg==5))
				{	if (x >= X2Out)
						{	curLn = 2;
							pixels[x][y] = 1;
							if (y>0)	pixels[x][y-1] = 0;
							if (y<9)	pixels[x][y+1] = 0;
						}
					else curLn = 6;
				}

				if ((curAlg==6)|(curAlg==7))
				{	if (y >= Y2Out)
						{	curLn = 2;
							pixels[x][y] = 1;
							if (x>0)	pixels[x-1][y] = 0;
							if (x<9)	pixels[x+1][y] = 0;
						}
					else curLn = 6;
				}
				dAdjusted = false;

			break;

			case 2:
				D = D + M;
				smalld = (float) D / c;
				if (curAlg==1)
				{	if (x < X2Out)
					{	pixels[x+1][y] = 2;
						pixels[x+1][y+1] = 3;
					}
				}
				if (curAlg==2)
				{	if (y < Y2Out)
					{	pixels[x][y+1] = 2;
						pixels[x+1][y+1] = 3;
					}
				}
				if (curAlg==3)
				{	if (y < Y2Out)
					{	pixels[x][y+1] = 2;
						pixels[x-1][y+1] = 3;
					}
				}
				if (curAlg==4)
				{	if (x > X2Out)
					{	pixels[x-1][y] = 2;
						pixels[x-1][y+1] = 3;
					}
				}
				if (curAlg==5)
				{	if (x > X2Out)
					{	pixels[x-1][y] = 2;
						pixels[x-1][y-1] = 3;
					}
				}
				if (curAlg==6)
				{	if (y > Y2Out)
					{	pixels[x][y-1] = 2;
						pixels[x-1][y-1] = 3;
					}
				}
				if (curAlg==7)
				{	if (y > Y2Out)
					{	pixels[x][y-1] = 2;
						pixels[x+1][y-1] = 3;
					}
				}
				if (curAlg==8)
				{	if (x < X2Out)
					{	pixels[x+1][y] = 2;
						pixels[x+1][y-1] = 3;
					}
				}
				curLn = 3;
			break;

			case 3:	curLn = 4;
			break;

			case 4:	if (D > H)
				{	if ((curAlg==1)|(curAlg==4))	y = y + 1;
					if ((curAlg==2)|(curAlg==7))	x = x + 1;
					if ((curAlg==3)|(curAlg==6))	x = x - 1;
					if ((curAlg==5)|(curAlg==8))	y = y - 1;
					D = D - c;
					smalld = (float) D / c;
					dAdjusted = true;
					curLn = 5;
				}
				else
				{	if ((curAlg==1)|(curAlg==8))	x = x + 1;
					if ((curAlg==2)|(curAlg==3))	y = y + 1;
					if ((curAlg==4)|(curAlg==5))	x = x - 1;
					if ((curAlg==6)|(curAlg==7))	y = y - 1;
					curLn = 1;
				}
			break;

			case 5:
				if ((curAlg==1)|(curAlg==8))	x = x + 1;
				if ((curAlg==2)|(curAlg==3))	y = y + 1;
				if ((curAlg==4)|(curAlg==5))	x = x - 1;
				if ((curAlg==6)|(curAlg==7))	y = y - 1;
				curLn = 1;
			break;

			case 6:	break;
		}
		else
			errMsg = "Please select two valid points before pressing STEP.";
	}

	public void pointSelected()

	{	// When a point has been selected, convert the screen location to
		//   the corresponding x and y coordinates on our oversize grid.
		//   The X and Y get translated from device coordinates to the
		//   conventional standard of (0,0) at the lower left of the grid

		if (P1Selected)
		{	X1Coord = (int) Math.floor((X1-1)/50);
			Y1Coord = (int) Math.floor((Y1-1)/50);
			X1Out = X1Coord;
			Y1Out = 9 - Y1Coord;
		}
		if (P2Selected)
		{	X2Coord = (int) Math.floor((X2-1)/50);
			Y2Coord = (int) Math.floor((Y2-1)/50);
			X2Out = X2Coord;
			Y2Out = 9 - Y2Coord;
		}
		startNewDemo();
	}

	public void drawUpArrow(Graphics g, int ptX, int ptY)

	{	// Draws the up arrow on the line for display of "d"

		int[] arrowX, arrowY;
		arrowX = new int[3];
		arrowY = new int[3];
		arrowX[0] = ptX;
		arrowX[1] = ptX-3;
		arrowX[2] = ptX+3;
		arrowY[0] = ptY;
		arrowY[1] = ptY+3;
		arrowY[2] = ptY+3;
		g.fillPolygon(arrowX, arrowY, 3);
	}

	public void drawDownArrow(Graphics g, int ptX, int ptY)

	{	// Draws the down arrow on the line for display of "d"

		int[] arrowX, arrowY;
		arrowX = new int[3];
		arrowY = new int[3];
		arrowX[0] = ptX;
		arrowX[1] = ptX-3;
		arrowX[2] = ptX+3;
		arrowY[0] = ptY;
		arrowY[1] = ptY-3;
		arrowY[2] = ptY-3;
		g.fillPolygon(arrowX, arrowY, 3);
	}

	public void drawLeftArrow(Graphics g, int ptX, int ptY)

	{	// Draws the left arrow on the line for display of "d"

		int[] arrowX, arrowY;
		arrowX = new int[3];
		arrowY = new int[3];
		arrowX[0] = ptX;
		arrowX[1] = ptX+3;
		arrowX[2] = ptX+3;
		arrowY[0] = ptY;
		arrowY[1] = ptY+3;
		arrowY[2] = ptY-3;
		g.fillPolygon(arrowX, arrowY, 3);
	}

	public void drawRightArrow(Graphics g, int ptX, int ptY)

	{	// Draws the right arrow on the line for display of "d"

		int[] arrowX, arrowY;
		arrowX = new int[3];
		arrowY = new int[3];
		arrowX[0] = ptX;
		arrowX[1] = ptX-3;
		arrowX[2] = ptX-3;
		arrowY[0] = ptY;
		arrowY[1] = ptY+3;
		arrowY[2] = ptY-3;
		g.fillPolygon(arrowX, arrowY, 3);
	}

	BresenhamLineDrawingDemo()
	{
		setBackground(Color.WHITE);
		startNewDemo();
		addMouseListener(new MouseAdapter()
		{	public void mousePressed(MouseEvent evt)

			{	// When the mouse is clicked, determine where
				//   it is on the screen and do the appropriate
				//   action, if any.

				int Xclick = 0, Yclick = 0;

				// Get the coordinates
				Xclick = evt.getX();
				Yclick = evt.getY();

				// Check to see if STEP button was pressed
				if (Xclick>619 && Xclick<686 &&
					Yclick>359 && Yclick<386)
				{	stepPressed();
				}
				else

				// Only process clicks inside grid border
				{	if (Xclick>3 && Xclick<501 &&
						Yclick>3 && Yclick<501)
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
							else	//start over
							{	X1 = Xclick;
								Y1 = Yclick;
								P2Selected = false;
							}
						}
						pointSelected();
					}
				}

				repaint();
			}
		});
	}
/*
	public void update(Graphics g)	//TMP
	{
		//	Skip the background re-painting; won't clear a thing

		paint(g);
	}
*/
	public void paint(Graphics g)

	{
		// Produce the graphics display in the window

		int XOffset = 1, YOffset = 1, i = 0,
			X1Grid = 1, Y1Grid = 1, X2Grid = 1, Y2Grid = 1;
		String[] title = {
			"Bresenham",
			"Line-Drawing Algorithm",
			"Demonstration"};
		String[][] alg =
		{	// Algorithm 0 (default)
			{"int x,y,D=0,H=xQ-xP,c=2*H,M=2*(yQ-yP);",
			"for (x=xP; x<=xQ; x++) {",
			"    putPixel(g, x, y);",
			"    D = D + M;",
			"    if (D > H) {",
			"        y++; D = D - c;",
			"}}"},
			// Algorithm 1
			{"int x,y,D=0,H=xQ-xP,c=2*H,M=2*(yQ-yP);",
			"for (x=xP; x<=xQ; x++) {",
			"    putPixel(g, x, y);",
			"    D = D + M;",
			"    if (D > H) {",
			"        y++; D = D - c;",
			"}}"},
			// Algorithm 2
			{"int x,y,D=0,H=yQ-yP,c=2*H,M=2*(xQ-xP);",
			"for (y=yP; y<=yQ; y++) {",
			"    putPixel(g, x, y);",
			"    D = D + M;",
			"    if (D > H) {",
			"        x++; D = D - c;",
			"}}"},
			// Algorithm 3
			{"int x,y,D=0,H=yQ-yP,c=2*H,M=2*(xP-xQ);",
			"for (y=yP; y<=yQ; y++) {",
			"    putPixel(g, x, y);",
			"    D = D + M;",
			"    if (D > H) {",
			"        x--; D = D - c;",
			"}}"},
			// Algorithm 4
			{"int x,y,D=0,H=xP-xQ,c=2*H,M=2*(yQ-yP);",
			"for (x=xP; x>=xQ; x--) {",
			"    putPixel(g, x, y);",
			"    D = D + M;",
			"    if (D > H) {",
			"        y++; D = D - c;",
			"}}"},
			// Algorithm 5
			{"int x,y,D=0,H=xP-xQ,c=2*H,M=2*(yP-yQ);",
			"for (x=xP; x>=xQ; x--) {",
			"    putPixel(g, x, y);",
			"    D = D + M;",
			"    if (D > H) {",
			"        y--; D = D - c;",
			"}}"},
			// Algorithm 6
			{"int x,y,D=0,H=yP-yQ,c=2*H,M=2*(xP-xQ);",
			"for (y=yP; y>=yQ; y--) {",
			"    putPixel(g, x, y);",
			"    D = D + M;",
			"    if (D > H) {",
			"        x--; D = D - c;",
			"}}"},
			// Algorithm 7
			{"int x,y,D=0,H=yP-yQ,c=2*H,M=2*(xQ-xP);",
			"for (y=yP; y>=yQ; y--) {",
			"    putPixel(g, x, y);",
			"    D = D + M;",
			"    if (D > H) {",
			"        x++; D = D - c;",
			"}}"},
			// Algorithm 8
			{"int x,y,D=0,H=xQ-xP,c=2*H,M=2*(yP-yQ);",
			"for (x=xP; x<=xQ; x++) {",
			"    putPixel(g, x, y);",
			"    D = D + M;",
			"    if (D > H) {",
			"        y--; D = D - c;",
			"}}"}
		};

		// Draw Title
		Font titleFont = new Font("Arial", Font.BOLD, 18);
		g.setFont(titleFont);
		g.setColor(Color.black);
		g.drawString(title[0], 595, 30);
		g.drawString(title[1], 545, 50);
		g.drawString(title[2], 580, 70);
		g.drawLine(535, 90, 765, 90);

		// Draw algorithm text
		Font algorithmFont = new Font("Arial", Font.PLAIN, 14);
		Font algorithmFontBold = new Font("Arial", Font.BOLD, 14);
		g.setFont(algorithmFont);
		int textOffset = 120;
		for (i=0; i<=6; i++)
		{
			if (i == curLn)
			{	g.setColor(Color.red);
				g.setFont(algorithmFontBold);
			}
			g.drawString(alg[curAlg][i], 525, textOffset);
			g.setColor(Color.black);
			g.setFont(algorithmFont);
			textOffset = textOffset + 20;
		}

		// Draw grid
		g.setColor(Color.black);
		g.fillRect(1, 1, 503, 3);
		g.fillRect(1, 1, 3, 503);
		g.fillRect(1, 501, 503, 3);
		g.fillRect(501, 1, 3, 503);
		g.setColor(Color.gray);
		YOffset = 27;
		for (i=0; i<=9; i++)
		{	g.drawLine(4, YOffset, 501, YOffset);
			YOffset = YOffset + 50;
		}
		XOffset = 27;
		for (i=0; i<=9; i++)
		{	g.drawLine(XOffset, 4, XOffset, 501);
			XOffset = XOffset + 50;
		}

		// Draw step button
		g.setColor(Color.red);
		g.drawRect(610, 360, 65, 25);
		g.setColor(Color.black);
		g.drawRect(609, 359, 67, 27);
		g.drawRect(611, 361, 63, 23);
		g.drawString("STEP", 625, 377);

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

		// Draw any points already selected
		if (P1Selected)
		{	X1Grid = 27 + X1Coord * 50;
			Y1Grid = 27 + Y1Coord * 50;
			g.setColor(Color.blue);
			g.fillRect(X1Grid-3, Y1Grid-3, 7, 7);
			g.setColor(Color.black);
			g.drawString("P(" + X1Out + ", " + Y1Out + ")", (X1Grid+4), (Y1Grid+40));
		}

		if (P2Selected)
		{	X2Grid = 27 + X2Coord * 50;
			Y2Grid = 27 + Y2Coord * 50;
			g.setColor(Color.blue);
			g.fillRect(X2Grid-3, Y2Grid-3, 7, 7);
			g.drawLine(X1Grid, Y1Grid, X2Grid, Y2Grid);
			g.setColor(Color.black);
			g.drawString("Q(" + X2Out + ", " + Y2Out + ")", (X2Grid+4), (Y2Grid+40));

			// If both points are already selected, also display variable values
			g.drawString("Current Variable Values", 560, 290);
			g.drawRect(540, 300, 100, 20);
			g.drawRect(640, 300, 100, 20);
			g.drawRect(540, 320, 100, 20);
			g.drawRect(640, 320, 100, 20);
			g.drawRect(539, 299, 202, 42);
			g.drawString("D = " + D, 550, 315);
			g.drawString("H = " + H, 650, 315);
			g.drawString("c = " + c, 550, 335);
			g.drawString("M = " + M, 650, 335);


		}

		// Display any "pixels" that have been lit at this point in the algorithm
		//   A value of 0 indicates a pixel that is not lit
		//   A value of 1 indicates a pixel that is definitely lit (red)
		//   A value of 2 or 3 indicates pixels that are choices for the next
		//     pixel to light up (pink)
		//   A value of 2 indicates that the lines for "d" need to be drawn

		DecimalFormat dFormat = new DecimalFormat("0.00");
		for (i=0; i<10; i++)
			for (int j=0; j<10; j++)
			{	if (pixels[i][j] == 1)
				{	g.setColor(Color.red);
					X1Grid = 27 + i * 50;
					Y1Grid = 27 + (9 - j) * 50;
					g.fillOval(X1Grid-15, Y1Grid-15, 30, 30);
				}
				if (pixels[i][j] == 2)
				{	X1Grid = 27 + i * 50;
					Y1Grid = 27 + (9 - j) * 50;
					int smalldX = 0, smalldY = 0, smalldPixels = 0;
					int X1Alt = 0, Y1Alt = 0;
					smalldPixels = (int) Math.round(smalld * 50);
					if (smalldPixels != 0)
					{
						if (curAlg==1)
						{
							smalldX = X1Grid + 25;
							g.setColor(Color.blue);
							if ((smalldPixels > 0) && (!dAdjusted))
							{
								Y1Alt = Y1Grid;
								smalldY = Y1Alt - smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawUpArrow(g, smalldX, smalldY);
								drawDownArrow(g, smalldX, Y1Alt);
							}
							if ((smalldPixels > 0) && (dAdjusted))
							{
								Y1Alt = Y1Grid - 50;
								smalldY = Y1Alt - smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawUpArrow(g, smalldX, smalldY);
								drawDownArrow(g, smalldX, Y1Alt);
							}

							if ((smalldPixels < 0) && (dAdjusted))
							{
								Y1Alt = Y1Grid - 50;
								smalldY = Y1Alt - smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawDownArrow(g, smalldX, smalldY);
								drawUpArrow(g, smalldX, Y1Alt);
							}
							if ((smalldPixels < 0) && (!dAdjusted))
							{
								Y1Alt = Y1Grid;
								smalldY = Y1Alt - smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawDownArrow(g, smalldX, smalldY);
								drawUpArrow(g, smalldX, Y1Alt);
							}
							g.setFont(smallFont);
							g.drawLine(X1Grid, smalldY, X1Grid + 40, smalldY);
							g.drawString("d = " + dFormat.format(smalld), smalldX+30, smalldY+10);
						}
						if (curAlg==2)
						{
							smalldY = Y1Grid - 25;
							g.setColor(Color.blue);
							if ((smalldPixels > 0) && (!dAdjusted))
							{
								X1Alt = X1Grid;
								smalldX = X1Alt + smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawRightArrow(g, smalldX, smalldY);
								drawLeftArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels > 0) && (dAdjusted))
							{
								X1Alt = X1Grid + 50;
								smalldX = X1Alt + smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawRightArrow(g, smalldX, smalldY);
								drawLeftArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels < 0) && (dAdjusted))
							{
								X1Alt = X1Grid + 50;
								smalldX = X1Alt + smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawLeftArrow(g, smalldX, smalldY);
								drawRightArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels < 0) && (!dAdjusted))
							{
								X1Alt = X1Grid;
								smalldX = X1Alt + smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawLeftArrow(g, smalldX, smalldY);
								drawRightArrow(g, X1Alt, smalldY);
							}
							g.setFont(smallFont);
							g.drawLine(smalldX, Y1Grid, smalldX, Y1Grid - 40);
							g.drawString("d = " + dFormat.format(smalld), smalldX+3, smalldY-3);
						}
						if (curAlg==3)
						{
							smalldY = Y1Grid - 25;
							g.setColor(Color.blue);
							if ((smalldPixels > 0) && (!dAdjusted))
							{
								X1Alt = X1Grid;
								smalldX = X1Alt - smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawLeftArrow(g, smalldX, smalldY);
								drawRightArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels > 0) && (dAdjusted))
							{
								X1Alt = X1Grid - 50;
								smalldX = X1Alt - smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawLeftArrow(g, smalldX, smalldY);
								drawRightArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels < 0) && (dAdjusted))
							{
								X1Alt = X1Grid - 50;
								smalldX = X1Alt - smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawRightArrow(g, smalldX, smalldY);
								drawLeftArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels < 0) && (!dAdjusted))
							{
								X1Alt = X1Grid;
								smalldX = X1Alt - smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawRightArrow(g, smalldX, smalldY);
								drawLeftArrow(g, X1Alt, smalldY);
							}
							g.setFont(smallFont);
							g.drawLine(smalldX, Y1Grid, smalldX, Y1Grid - 40);
							g.drawString("d = " + dFormat.format(smalld), smalldX+3, smalldY-3);
						}
						if (curAlg==4)
						{
							smalldX = X1Grid - 25;
							g.setColor(Color.blue);
							if ((smalldPixels > 0) && (!dAdjusted))
							{
								Y1Alt = Y1Grid;
								smalldY = Y1Alt - smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawUpArrow(g, smalldX, smalldY);
								drawDownArrow(g, smalldX, Y1Alt);
							}
							if ((smalldPixels > 0) && (dAdjusted))
							{
								Y1Alt = Y1Grid - 50;
								smalldY = Y1Alt - smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawUpArrow(g, smalldX, smalldY);
								drawDownArrow(g, smalldX, Y1Alt);
							}

							if ((smalldPixels < 0) && (dAdjusted))
							{
								Y1Alt = Y1Grid - 50;
								smalldY = Y1Alt - smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawDownArrow(g, smalldX, smalldY);
								drawUpArrow(g, smalldX, Y1Alt);
							}
							if ((smalldPixels < 0) && (!dAdjusted))
							{
								Y1Alt = Y1Grid;
								smalldY = Y1Alt - smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawDownArrow(g, smalldX, smalldY);
								drawUpArrow(g, smalldX, Y1Alt);
							}
							g.setFont(smallFont);
							g.drawLine(X1Grid, smalldY, X1Grid - 40, smalldY);
							g.drawString("d = " + dFormat.format(smalld), smalldX-70, smalldY+10);
						}
						if (curAlg==5)
						{
							smalldX = X1Grid - 25;
							g.setColor(Color.blue);
							if ((smalldPixels > 0) && (!dAdjusted))
							{
								Y1Alt = Y1Grid;
								smalldY = Y1Alt + smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawDownArrow(g, smalldX, smalldY);
								drawUpArrow(g, smalldX, Y1Alt);
							}
							if ((smalldPixels > 0) && (dAdjusted))
							{
								Y1Alt = Y1Grid + 50;
								smalldY = Y1Alt + smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawDownArrow(g, smalldX, smalldY);
								drawUpArrow(g, smalldX, Y1Alt);
							}

							if ((smalldPixels < 0) && (dAdjusted))
							{
								Y1Alt = Y1Grid + 50;
								smalldY = Y1Alt + smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawUpArrow(g, smalldX, smalldY);
								drawDownArrow(g, smalldX, Y1Alt);
							}
							if ((smalldPixels < 0) && (!dAdjusted))
							{
								Y1Alt = Y1Grid;
								smalldY = Y1Alt + smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawUpArrow(g, smalldX, smalldY);
								drawDownArrow(g, smalldX, Y1Alt);
							}
							g.setFont(smallFont);
							g.drawLine(X1Grid, smalldY, X1Grid - 40, smalldY);
							g.drawString("d = " + dFormat.format(smalld), smalldX-70, smalldY-6);
						}
						if (curAlg==6)
						{
							smalldY = Y1Grid + 25;
							g.setColor(Color.blue);
							if ((smalldPixels > 0) && (!dAdjusted))
							{
								X1Alt = X1Grid;
								smalldX = X1Alt - smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawLeftArrow(g, smalldX, smalldY);
								drawRightArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels > 0) && (dAdjusted))
							{
								X1Alt = X1Grid - 50;
								smalldX = X1Alt - smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawLeftArrow(g, smalldX, smalldY);
								drawRightArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels < 0) && (dAdjusted))
							{
								X1Alt = X1Grid - 50;
								smalldX = X1Alt - smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawRightArrow(g, smalldX, smalldY);
								drawLeftArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels < 0) && (!dAdjusted))
							{
								X1Alt = X1Grid;
								smalldX = X1Alt - smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawRightArrow(g, smalldX, smalldY);
								drawLeftArrow(g, X1Alt, smalldY);
							}
							g.setFont(smallFont);
							g.drawLine(smalldX, Y1Grid, smalldX, Y1Grid + 40);
							g.drawString("d = " + dFormat.format(smalld), smalldX+3, smalldY-3);
						}
						if (curAlg==7)
						{
							smalldY = Y1Grid + 25;
							g.setColor(Color.blue);
							if ((smalldPixels > 0) && (!dAdjusted))
							{
								X1Alt = X1Grid;
								smalldX = X1Alt + smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawRightArrow(g, smalldX, smalldY);
								drawLeftArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels > 0) && (dAdjusted))
							{
								X1Alt = X1Grid + 50;
								smalldX = X1Alt + smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawRightArrow(g, smalldX, smalldY);
								drawLeftArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels < 0) && (dAdjusted))
							{
								X1Alt = X1Grid + 50;
								smalldX = X1Alt + smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawLeftArrow(g, smalldX, smalldY);
								drawRightArrow(g, X1Alt, smalldY);
							}
							if ((smalldPixels < 0) && (!dAdjusted))
							{
								X1Alt = X1Grid;
								smalldX = X1Alt + smalldPixels;
								g.drawLine(X1Alt, smalldY, smalldX, smalldY);
								drawLeftArrow(g, smalldX, smalldY);
								drawRightArrow(g, X1Alt, smalldY);
							}
							g.setFont(smallFont);
							g.drawLine(smalldX, Y1Grid, smalldX, Y1Grid + 40);
							g.drawString("d = " + dFormat.format(smalld), smalldX+3, smalldY-3);
						}
						if (curAlg==8)
						{
							smalldX = X1Grid + 25;
							g.setColor(Color.blue);
							if ((smalldPixels > 0) && (!dAdjusted))
							{
								Y1Alt = Y1Grid;
								smalldY = Y1Alt + smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawDownArrow(g, smalldX, smalldY);
								drawUpArrow(g, smalldX, Y1Alt);
							}
							if ((smalldPixels > 0) && (dAdjusted))
							{
								Y1Alt = Y1Grid + 50;
								smalldY = Y1Alt + smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawDownArrow(g, smalldX, smalldY);
								drawUpArrow(g, smalldX, Y1Alt);
							}

							if ((smalldPixels < 0) && (dAdjusted))
							{
								Y1Alt = Y1Grid + 50;
								smalldY = Y1Alt + smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawUpArrow(g, smalldX, smalldY);
								drawDownArrow(g, smalldX, Y1Alt);
							}
							if ((smalldPixels < 0) && (!dAdjusted))
							{
								Y1Alt = Y1Grid;
								smalldY = Y1Alt + smalldPixels;
								g.drawLine(smalldX, Y1Alt, smalldX, smalldY);
								drawUpArrow(g, smalldX, smalldY);
								drawDownArrow(g, smalldX, Y1Alt);
							}
							g.setFont(smallFont);
							g.drawLine(X1Grid, smalldY, X1Grid + 40, smalldY);
							g.drawString("d = " + dFormat.format(smalld), smalldX+30, smalldY-6);
						}
					}
					g.setColor(Color.pink);
					g.fillOval(X1Grid-15, Y1Grid-15, 30, 30);
				}
				if (pixels[i][j] == 3)
				{	g.setColor(Color.pink);
					X1Grid = 27 + i * 50;
					Y1Grid = 27 + (9 - j) * 50;
					g.fillOval(X1Grid-15, Y1Grid-15, 30, 30);
				}
			}

	}
}


