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

import java.awt.Graphics;

// Contains static method for drawing dashed lines.

public class HiddenLineDemo2Lines  {

	static int iX(float x) {
		return Math.round(x);
	}

	static int iY(float y) {
		return Math.round(y);
	}

	static float fx(int X) {
		return (float)X;
	}

	static float fy(int Y) {
		return (float)Y;
	}

	static void dashedLine(Graphics g, int xA, int yA, int xB, int yB, int dashLength) {
		float xa = fx(xA);
		float ya = fy(yA);
		float xb = fx(xB);
		float yb = fy(yB);

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
}
