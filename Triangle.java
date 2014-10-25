// Triangle.java: Class to store a triangle;
//    vertices in logical coordinates.
// Uses: Point2D.

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

class Triangle
{  Point2D A, B, C;
   Triangle(Point2D A, Point2D B, Point2D C)
   {  this.A = A; this.B = B; this.C = C;
   }
}
