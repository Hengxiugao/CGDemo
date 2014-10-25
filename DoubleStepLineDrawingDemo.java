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

/**
 * Stephen Niles:
 * This demo is modified from the original CGDemo downloaded from www.utdallas.edu/~kzhang/BookCG/CGDemo.zip
 * Changes made:
 * 		-Removed demoes for unrelated projects.
 * 		-Refactored some of the existing code to made the code more manageable (mostly paint())
 * 		-Introduced my own Util2D.java class for handling some of the drawing.
 * 		-Moved items that don't change to be global static variables, so they are created with every call to paint()
 * 
 * My logic for incrementing through the sample code is similar to how it was done for Bresenham's algorithm.
 * The appropiate changes were made to convert this to a double-step demo, and the double-step code is the
 * same as the source code that was downloaded for the first problem.
 */

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class DoubleStepLineDrawingDemo extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final Point2D[] pattern1Points = {new Point2D(0, 0), new Point2D(1,0), new Point2D(2,0)};
	private static final Point2D[] pattern2Points = {new Point2D(0, 0), new Point2D(1,0), new Point2D(2,1)};
	private static final Point2D[] pattern3Points = {new Point2D(0, 0), new Point2D(1,1), new Point2D(2,1)};
	private static final Point2D[] pattern4Points = {new Point2D(0, 0), new Point2D(1,1), new Point2D(2,2)};
	
	private static final int[] selectedPatternCoords = {25, 225, 425, 625};
	
	private static int selectedPattern = -1;
	
	private static final String[][] alg =
	{	// Algorithm 0 (default)
		{"",
		"",
		"",
		"",
		"",
		"",
		""},
		// Algorithm 1
		{"while(currentX != X2Out){",
			"     if(currentX == X2Out - xInc){",
			"          drawPixel(currentX, currentY);",
			"          break;}",
			"     if (D < 0){",
			"          drawPattern1();",
			"          D += 4 * dY;}",
			"     else if (D < 2*dY){",
			"          drawPattern2();",
			"          D += 4 * dY - 2 * dX;}",
			"     else if (D >= 2*dY){",
			"          drawPattern3();",
			"          D += 4 * dY - 2 * dX;}",
			"}} drawRedPixel(X2Out, Y2Out);"},
		// Algorithm 2
		{"while(currentX != X2Out){",
		"     if(currentX == (X2Out - xInc)){",
		"          drawPixel(currentX, currentY);",
		"          break;}",
		"     if ( D >= (2 * dX) ){",
		"          drawPattern4();",
		"          D += 4 * dY - 4 * dX;}",
		"     else if ( D < (2 * dY) ){",
		"          drawPattern2();",
		"          D += 4 * dY - 2 * dX;}",
		"     else if ( D >= (2 * dY) && D < (2 * dX)){",
		"          drawPattern3();",
		"          D += 4 * dY - 2 * dX;",
		"}} drawPixel(X2Out, Y2Out);"},
		// Algorithm 3
		{"while(currentX != X2Out){",
			"     if(currentX == X2Out - xInc){",
			"          drawPixel(currentX, currentY);",
			"          break;}",
			"     if (D < 0){",
			"          drawPattern1();",
			"          D += 4 * dY;}",
			"     else if (D < 2*dY){",
			"          drawPattern2();",
			"          D += 4 * dY - 2 * dX;}",
			"     else if (D >= 2*dY){",
			"          drawPattern3();",
			"          D += 4 * dY - 2 * dX;}",
			"}} drawRedPixel(X2Out, Y2Out);"},
		// Algorithm 4
		{"while(currentX != X2Out){",
		"     if(currentX == (X2Out - xInc)){",
		"          drawPixel(currentX, currentY);",
		"          break;}",
		"     if ( D >= (2 * dX) ){",
		"          drawPattern4();",
		"          D += 4 * dY - 4 * dX;}",
		"     else if ( D < (2 * dY) ){",
		"          drawPattern2();",
		"          D += 4 * dY - 2 * dX;}",
		"     else if ( D >= (2 * dY) && D < (2 * dX)){",
		"          drawPattern3();",
		"          D += 4 * dY - 2 * dX;",
		"}} drawPixel(X2Out, Y2Out);"}
	};
	
	private static enum Pattern 
	{
		NONE(-1), ONE(0), TWO(1), THREE(2), FOUR(3);
		
		private int code;
		private Pattern(int c) {code = c;}
		public int toInt() { return code; }
	}
	
	DoubleStepLineDrawingDemo()
	{
		add(new DoubleStepCanvas(), BorderLayout.CENTER);
		
		setVisible(true);
	}

	public class DoubleStepCanvas extends Canvas
	{
		private static final long serialVersionUID = 1L;
		
		int X1=100000, Y1, X2=100000, Y2;
		int currentLine = 0, currentAlgorithm = 0;
		boolean P1Selected = false, P2Selected = false;
		int D = 0, dX = 0, dY = 0;
		int X1Out = 0, Y1Out = 0, X2Out = 0, Y2Out = 0, currentX = 0, currentY = 0, xInc = 0, yInc = 0;
		int [][] pixels = new int[10][10];
		String errMsg = " ";
		int X1Coord = 0, Y1Coord = 0, X2Coord = 0, Y2Coord = 0;
	
		/**
		 * When a new demo is started, variables need to be initialized and the points checked for validity
		 */
		public void startNewDemo()
		{	
			for (int i=0; i<10; i++)
			{
				for (int j=0; j<10; j++)
				{
					pixels[i][j] = 0;
				}
			}
			
			// Initialize some variables common to all algorithm forms
			currentAlgorithm = 0;
			currentLine = 0;
			D = 0;
			xInc = 1;
			yInc = 1;
			dX = 0;
			dY = 0;
			selectedPattern = Pattern.NONE.toInt();
			
			if (P1Selected && P2Selected)
			{
				dX = X2Out - X1Out;
				dY = Y2Out - Y1Out;
				
				currentX = X1Out;
				currentY = Y1Out; 

				if (dX < 0) 
			    { 
					xInc = -1;
					dX = -dX;
			    }
			      
				if (dY < 0) 
				{ 
					yInc = -1;
					dY = -dY;
				}
				if (dY <= dX)
				{
					if( dX > (2 * dY))
					{
						System.out.println("Run xslopeLessThanHalf");
						D = 4 * dY - dX;
						currentAlgorithm = 1;	//xslopeLessThanHalf(x, y, HX, HY, xQ, yQ);
					}
			        else
			        {
			        	System.out.println("Run xslopeMoreThanHalf");
			        	D = 4 * dY - dX;  //original: D = 4 * dY - 2 * dX
			        	
			        	currentAlgorithm = 2;	//xslopeMoreThanHalf(x, y, HX, HY, xQ, yQ);
			        }
				}
				else
				{
					if( dY > (2 * dX))
					{
						setVariables(currentY, currentX, dY, dX, Y2Out, X2Out, yInc, xInc);
						System.out.println("Run yslopeLessThanHalf");
						D = 4 * dY - dX;
						currentAlgorithm = 3;	//yslopeLessThanHalf(y, x, HY, HX, yQ, xQ);
					}
					else
					{
						setVariables(currentY, currentX, dY, dX, Y2Out, X2Out, yInc, xInc);
						System.out.println("Run yslopeMoreThanHalf");
						D = 4 * dY - dX;   //original: D = 4 * dY - 2 * dX
						currentAlgorithm = 4;	//yslopeMoreThanHalf(y, x, HY, HX, yQ, xQ);
					}
				}
			}
		}
		
		public void setVariables(int currentX, int currentY, int dX, int dY, int X2Out, int Y2Out, int xInc, int yInc)
		{
			this.currentX = currentX;
			this.currentY = currentY;
			this.dX = dX;
			this.dY = dY;
			this.X2Out = X2Out;
			this.Y2Out = Y2Out;
			this.xInc = xInc;
			this.yInc = yInc;
		}
		
		private void drawRedPixel(int x, int y)
		{
			pixels[x][y] = 1;
		}
		
		private void drawPinkPixel(int x, int y)
		{
			if(x > -1 && x < 10 && y > -1 && y < 10)
			{
				pixels[x][y] = 2;
			}
		}
	
		/**
		 * Events to process when the STEP button has been pressed. Depending on the line of 
		 * code being executed, the corresponding information on the screen is changed.
		 */
		public void stepPressed()
		{	
			if (P1Selected && P2Selected)
			{
				switch(currentAlgorithm)
				{
					case 1:
						xslopeLessThanHalf();
						break;
						
					case 2:
						xslopeMoreThanHalf();
						break;
						
					case 3:
						yslopeLessThanHalf();
						break;
						
					case 4:
						yslopeMoreThanHalf();
						break;
				}
			}
			else
				errMsg = "Please select two valid points before pressing STEP.";
		}
		
		private void xslopeLessThanHalf()
		{
			System.out.println("xslopeLessThanHalf, currentLine="+currentLine+",D="+D+",Xinc="+xInc+",YInx="+yInc+",DX="+dX+",DY="+dY);
			switch(currentLine)
			{
				
				case 0:
					
					if(currentX == X2Out)
					{
						currentLine = 13;
					}
					else
					{
						currentLine = 1;
					}
					break;
					
				case 1:
					if(currentX == X2Out - xInc)
					{
						drawRedPixel(currentX, currentY);
						currentLine = 13;
					}
					else
					{
						currentLine = 4;
					}
					break;
					
				case 4:
					if(D < 0)
					{
						selectedPattern = Pattern.ONE.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 7;
					}
					break;
					
				case 5:
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					drawPinkPixel(currentX, currentY);
					
					currentLine++;
					break;
					
				case 6:
					D += 4 * dY;
					currentLine = 0;
					break;
				
				case 7:
					if (D < 2*dY)
					{
						selectedPattern = Pattern.TWO.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 10;
					}
					break;
					
				case 8:
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					currentY += yInc;  
					drawPinkPixel(currentX, currentY);
					
					currentLine++;
					break;
					
				case 9:
					D += 4 * dY - 2 * dX;
					currentLine = 0;
					break;
					
				case 10:
					if (D >= 2*dY)
					{
						selectedPattern = Pattern.THREE.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 0;
					}
					break;
					
				case 11:
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					currentY += yInc;
					drawRedPixel(currentX, currentY);
					currentX += xInc; 
					drawPinkPixel(currentX, currentY);
					
					currentLine++;
					break;
					
				case 12:
					D += 4 * dY - 2 * dX;
					currentLine = 0;
					break;
					
				case 13:
					clearPinkPixels();
					drawRedPixel(X2Out, Y2Out);
					break;
			}    
		}
		
		private void xslopeMoreThanHalf()
		{
			System.out.println("xslopeMoreThanHalf currentLine="+currentLine+",D="+D+",DX="+dX+",DY="+dY+",currentX"+currentX+",X2OUT="+X2Out+",currentY="+currentY+",Xinc="+xInc+",YInx="+yInc);
			switch(currentLine)
			{
				case 0:
					if(currentX == X2Out)
					{
						currentLine = 13;
					}
					else
					{
						currentLine = 1;
					}
					break;
					
				case 1:
					if(currentX == X2Out - xInc)
					{
						drawRedPixel(currentX, currentY);
						currentLine = 13;
					}
					else
					{
						currentLine = 4;
					}
					break;
					
				case 4:
					if(D >= (2 * dX))//if(D >= (3 * dY))
					{
						selectedPattern = Pattern.FOUR.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 7;
					}
					break;
					
				case 5:
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					currentY += yInc;
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					currentY += yInc;
					drawPinkPixel(currentX, currentY);
					
					currentLine++;
					break;
					
				case 6:
					D += 4 * dY - 4 * dX;
					currentLine = 0;
					break;
				
				case 7:
					if(D < (2 * dY))
					{
						selectedPattern = Pattern.TWO.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 10;
					}
					break;
					
				case 8:
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					currentY += yInc;   
					drawPinkPixel(currentX, currentY);
					
					currentLine++;
					break;
					
				case 9:
					D += 4 * dY - 2 * dX;
					currentLine = 0;
					break;
					
				case 10:
					if(D >= (2 * dY) && D <(2 * dX))//D >= (2 * dY) && D <(3 * dY)
					{
						selectedPattern = Pattern.THREE.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 0;
					}
					break;
					
				case 11:
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					currentY += yInc;
					drawRedPixel(currentX, currentY);
					currentX += xInc;
					drawPinkPixel(currentX, currentY);
					
					currentLine++;
					break;
					
				case 12:
					D += 4 * dY - 2 * dX;
					currentLine = 0;
					break;
					
				case 13:
					clearPinkPixels();
					drawRedPixel(X2Out, Y2Out);
					break;
			}    
		}
		
		private void yslopeLessThanHalf()
		{
			switch(currentLine)
			{
				case 0:
					if(currentX == X2Out)
					{
						currentLine = 13;
					}
					else
					{
						currentLine = 1;
					}
					break;
					
				case 1:
					if(currentX == X2Out - xInc)
					{
						drawRedPixel(currentY, currentX);
						currentLine = 13;
					}
					else
					{
						currentLine = 4;
					}
					break;
					
				case 4:
					if(D < 0)
					{
						selectedPattern = Pattern.ONE.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 7;
					}
					break;
					
				case 5:
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					drawPinkPixel(currentY, currentX);
					
					currentLine++;
					break;
					
				case 6:
					D += 4 * dY;
					currentLine = 0;
					break;
				
				case 7:
					if (D < 2*dY)
					{
						selectedPattern = Pattern.TWO.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 10;
					}
					break;
					
				case 8:
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					currentY += yInc;  
					drawPinkPixel(currentY, currentX);
					
					currentLine++;
					break;
					
				case 9:
					D += 4 * dY - 2 * dX;
					currentLine = 0;
					break;
					
				case 10:
					if (D >= 2*dY)
					{
						selectedPattern = Pattern.THREE.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 0;
					}
					break;
					
				case 11:
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					currentY += yInc;
					drawRedPixel(currentY, currentX);
					currentX += xInc; 
					drawPinkPixel(currentY, currentX);
					
					currentLine++;
					break;
					
				case 12:
					D += 4 * dY - 2 * dX;
					currentLine = 0;
					break;
					
				case 13:
					clearPinkPixels();
					drawRedPixel(Y2Out, X2Out);
					break;
			}    
		}
		
		private void yslopeMoreThanHalf()
		{
			System.out.println("yslopeMoreThanHalf currentLine="+currentLine+",D="+D+",DX="+dX+",DY="+dY+",currentX"+currentX+",X2OUT="+X2Out+",currentY="+currentY+",Xinc="+xInc+",YInx="+yInc);
			switch(currentLine)
			{
				case 0:
					if(currentX == X2Out)
					{
						currentLine = 13;
					}
					else
					{
						currentLine = 1;
					}
					break;
					
				case 1:
					if(currentX == X2Out - xInc)
					{
						drawRedPixel(currentY, currentX);
						currentLine = 13;
					}
					else
					{
						currentLine = 4;
					}
					break;
					
				case 4:
					if(D >= (2 * dX))// previous vision is if(D >= (3 * dY))
					{
						selectedPattern = Pattern.FOUR.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 7;
					}
					break;
					
				case 5:
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					currentY += yInc;
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					currentY += yInc;
					drawPinkPixel(currentY, currentX);
					
					currentLine++;
					break;
					
				case 6:
					D += 4 * dY - 4 * dX;
					currentLine = 0;
					break;
				
				case 7:
					if(D < (2 * dY))
					{
						selectedPattern = Pattern.TWO.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 10;
					}
					break;
					
				case 8:
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					currentY += yInc;   
					drawPinkPixel(currentY, currentX);
					
					currentLine++;
					break;
					
				case 9:
					D += 4 * dY - 2 * dX;
					currentLine = 0;
					break;
					
				case 10:
					if(D >= (2 * dY) && D <=(2 * dX))// previous vision is D >= (2 * dY) && D <(3 * dY)
					{
						selectedPattern = Pattern.THREE.toInt();
						currentLine++;
					}
					else
					{
						currentLine = 0;
					}
					break;
					
				case 11:
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					currentY += yInc;
					drawRedPixel(currentY, currentX);
					currentX += xInc;
					drawPinkPixel(currentY, currentX);
					
					currentLine++;
					break;
					
				case 12:
					D += 4 * dY - 2 * dX;
					currentLine = 0;
					break;
					
				case 13:
					clearPinkPixels();
					drawRedPixel(Y2Out, X2Out);
					break;
			}    
		}
	
		/**
		 * When a point has been selected, convert the screen location to the corresponding x and y coordinates on our oversize grid.
		 * The X and Y get translated from device coordinates to the conventional standard of (0,0) at the lower left of the grid
		 */
		public void pointSelected()
		{	
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
	
		DoubleStepCanvas()
		{
			setSize(1000, 800);
			
			setBackground(Color.WHITE);
			
			startNewDemo();
			
			addMouseListener(new MouseAdapter()
			{	
				// When the mouse is clicked, determine where it is on the screen and do the appropriate action, if any.
				public void mousePressed(MouseEvent evt)
				{	
					int Xclick = evt.getX();
					int Yclick = evt.getY();
	
					if (Xclick > 619 && Xclick < 686 && Yclick > 359 && Yclick < 386)
					{	
						stepPressed();
					}
					else // Only process clicks inside grid border
					{	
						if (Xclick > 3 && Xclick < 501 && Yclick > 3 && Yclick < 501)
						{	
							if (!P1Selected)
							{	
								X1 = Xclick;
								Y1 = Yclick;
								P1Selected = true;
							}
							else
							{	
								if (!P2Selected)
								{	
									X2 = Xclick;
									Y2 = Yclick;
									P2Selected = true;
								}
								else	//Start over
								{	
									X1 = Xclick;
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
	
		public void paint(Graphics g)
		{
			drawAlgorithmText(g);
			
			drawMainGrid(g);
			
			drawPatterns(g);
			
			highlightSelectedPattern(g);
			
			drawStepButton(g);
			
			drawInformation(g);
	
			drawErrorMessage(g);
	
			drawSelectedPoints(g);
			
			drawPixels(g);
		}
		
		private void drawAlgorithmText(Graphics g)
		{
			Font algorithmFont = new Font("Arial", Font.PLAIN, 14);
			Font algorithmFontBold = new Font("Arial", Font.BOLD, 14);
			
			g.setFont(algorithmFont);
			int textOffset = 12;
			for (int i=0; i < alg[currentAlgorithm].length; i++)
			{
				if (i == currentLine)
				{	g.setColor(Color.red);
					g.setFont(algorithmFontBold);
				}
				g.drawString(alg[currentAlgorithm][i], 525, textOffset);
				g.setColor(Color.black);
				g.setFont(algorithmFont);
				textOffset = textOffset + 20;
			}
		}
		
		private void drawMainGrid(Graphics g)
		{
			g.setColor(Color.gray);
			
			int YOffset = 27;
			int XOffset = 27;
			
			for (int i=0; i<=9; i++)
			{	
				g.drawLine(4, YOffset, 501, YOffset);
				YOffset = YOffset + 50;
			}
			
			for (int i=0; i<=9; i++)
			{	
				g.drawLine(XOffset, 4, XOffset, 501);
				XOffset = XOffset + 50;
			}
			
			drawRect(g, 1, 1, 500, 500, 3, Color.black);		//Thick border
		}
		
		private void drawPatterns(Graphics g)
		{
			g.setColor(Color.black);
			
			g.drawString("Pattern 1", 70, 560);
			drawPattern(g, 50, 580, pattern1Points, Color.black, Color.red);
			
			g.drawString("Pattern 2", 270, 560);
			drawPattern(g, 250, 580, pattern2Points, Color.black, Color.red);
			
			g.drawString("Pattern 3", 470, 560);
			drawPattern(g, 450, 580, pattern3Points, Color.black, Color.red);
			
			g.drawString("Pattern 4", 670, 560);
			drawPattern(g, 650, 580, pattern4Points, Color.black, Color.red);
		}
		
		private void highlightSelectedPattern(Graphics g)
		{
			if(selectedPattern != Pattern.NONE.toInt())
			{
				drawRect(g, selectedPatternCoords[selectedPattern], 530, 150, 180, 3, Color.green);
			}
		}
		
		private void drawStepButton(Graphics g)
		{
			g.setColor(Color.red);
			g.drawRect(610, 360, 65, 25);
			g.setColor(Color.black);
			g.drawRect(609, 359, 67, 27);
			g.drawRect(611, 361, 63, 23);
			g.drawString("STEP", 625, 377);
		}
		
		private void drawInformation(Graphics g)
		{
			Font smallFont = new Font("Arial", Font.PLAIN, 10);
			g.setFont(smallFont);
			g.drawString("Press 'STEP' button to process the algorithm.", 540, 420);
			g.setFont(new Font("Arial", Font.PLAIN, 14));
		}
		
		private void drawErrorMessage(Graphics g)
		{
			if (errMsg != " ")
			{	g.setColor(Color.red);
				g.drawString("***ERROR***: " + errMsg, 20, 520);
				errMsg = " ";
			}
		}
		
		private void drawSelectedPoints(Graphics g)
		{
			int X1Grid = 0;
			int Y1Grid = 0;
			int X2Grid = 0;
			int Y2Grid = 0;
			
			if (P1Selected)
			{	
				X1Grid = 27 + X1Coord * 50;
				Y1Grid = 27 + Y1Coord * 50;
				g.setColor(Color.blue);
				g.fillRect(X1Grid-3, Y1Grid-3, 7, 7);
				g.setColor(Color.black);
				g.drawString("P(" + X1Out + ", " + Y1Out + ")", (X1Grid+4), (Y1Grid+40));
			}
	
			if (P2Selected)
			{	
				X2Grid = 27 + X2Coord * 50;
				Y2Grid = 27 + Y2Coord * 50;
				g.setColor(Color.blue);
				g.fillRect(X2Grid-3, Y2Grid-3, 7, 7);
				g.drawLine(X1Grid, Y1Grid, X2Grid, Y2Grid);
				g.setColor(Color.black);
				g.drawString("Q(" + X2Out + ", " + Y2Out + ")", (X2Grid+4), (Y2Grid+40));
	
				//Since we have both points, start showing variables.
				g.drawString("Current Variable Values", 560, 300);
				g.drawRect(540, 310, 100, 20);
				g.drawRect(640, 310, 100, 20);
				g.drawRect(540, 330, 100, 20);
				g.drawRect(640, 330, 100, 20);
				g.drawRect(539, 309, 202, 42);
				g.drawString("D = " + D, 550, 325);
				g.drawString("dX = " + dX, 550, 345);
				g.drawString("dY = " + dY, 650, 345);
			}
		}
		
		private void drawPixels(Graphics g)
		{
			int X1Grid, Y1Grid;
			
			for (int i=0; i<10; i++)
			{
				for (int j=0; j<10; j++)
				{	
					if (pixels[i][j] > 0)
					{	
						if(pixels[i][j] == 1)
						{
							g.setColor(Color.red);
						}
						else if(pixels[i][j] == 2)
						{
							g.setColor(Color.pink);
						}
							
						X1Grid = 27 + i * 50;
						Y1Grid = 27 + (9 - j) * 50;
						g.fillOval(X1Grid-15, Y1Grid-15, 30, 30);
					}
				}
			}
		}
		
		private void clearPinkPixels()
		{
			for (int i=0; i<10; i++)
			{
				for (int j=0; j<10; j++)
				{	
					if (pixels[i][j] == 2)
					{	
						pixels[i][j] = 0;
					}
				}
			}
		}
	}
	
	/**
	 * Draws a 2x2 grid used for displaying the various patterns. The grid is drawn starting
	 * from the offsets. Red "pixels" are then placed according to the array of points that
	 * are passed in. The grid color and dot colors can be specified.
	 */
	public void drawPattern(Graphics g, int xOffset, int yOffset, Point2D[] points, Color gridColor, Color dotColor)
	{
		Color savedColor = g.getColor();
		int xPoint, yPoint;
		int variableXOffset = xOffset;
		int variableYOffset = yOffset;
		
		g.setColor(gridColor);
		
		for (int i=0; i <= 2; i++)
		{	
			g.drawLine(variableXOffset, yOffset, variableXOffset, yOffset + 100);
			variableXOffset = variableXOffset + 50;
		}
		
		for (int i=0; i <= 2; i++)
		{	
			g.drawLine(xOffset, variableYOffset, xOffset + 100, variableYOffset);
			variableYOffset = variableYOffset + 50;
		}
		
		g.setColor(dotColor);
		
		for(int i=0; i < points.length; i++)
		{
			xPoint = (int)(xOffset + points[i].x * 50);
			yPoint = (int)(yOffset + (2-points[i].y) * 50);
			
			g.fillOval(xPoint-15, yPoint-15, 30, 30);
		}
		
		g.setColor(savedColor);
	}
	
	/**
	 * Helper method for drawing a rectangle that has some line thickess.
	 */
	public void drawRect(Graphics g, int x, int y, int width, int height, int thickness, Color color)
	{
		Color savedColor = g.getColor();
		
		g.setColor(color);
		g.fillRect(x, y, width + thickness, thickness);			//Top
		g.fillRect(x, y, thickness, height + thickness);		//Left
		g.fillRect(x, y + height, width + thickness, thickness);//Bottom
		g.fillRect(x + width, y, thickness, height + thickness);//Right
		
		g.setColor(savedColor);
	}
}
