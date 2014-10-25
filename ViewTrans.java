// ViewTrans.java: The view transformation exposed.
// Use left mouse to step through phases of view transformation.
// Use right mouse to start and stop spin (left of screen spin left)

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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class Point2DD
{	double x, y;
	Point2DD(double x, double y){this.x = x; this.y = y;}
}

class Point3DD
{	double x, y, z;
	Point3DD(double x, double y, double z)
	{	this.x = x;
		this.y = y;
		this.z = z;
	}
}

/*public class ViewTrans extends Frame
{	public static void main(String[] args){new ViewTrans();}
	private CvViewTrans cv = new CvViewTrans();
	ViewTrans()
	{	super("Click left and right mouse buttons");
		addWindowListener(new WindowAdapter()
			{public void windowClosing(WindowEvent e){System.exit(0);}});
		setLayout(new BorderLayout());
		add("Center", cv);
		Dimension dim = getToolkit().getScreenSize();
		setSize(dim.width/2, dim.height/2);
		setLocation(dim.width/4, dim.height/4);
		show();
	}
}
*/
abstract class DoubleBufferCanvas extends Canvas
{
	Image offscreen;

	/**
	 * null out the offscreen buffer as part of invalidation
	 */
	public void invalidate()
	{
		super.invalidate();
		offscreen = null;
	}

	/**
	 * override update to *not* erase the background before painting
	 */
	public void update(Graphics g)
	{
		paint(g);
	}

	/**
	 * paint children into an offscreen buffer, then blast entire image
	 * at once.
	 */
	public void paint(Graphics g)
	{
		if(offscreen == null)
		{
			offscreen = createImage(getSize().width, getSize().height);
		}
		Graphics og = offscreen.getGraphics();
		og.setClip(0,0,getSize().width, getSize().height);
		draw(og);
		g.drawImage(offscreen, 0, 0, null);
		og.dispose();
	}
	abstract void draw(Graphics g);
}

public class ViewTransformDemo extends DoubleBufferCanvas
	implements Runnable
{	int centerX, centerY;
	Obj[] objs;
	static final int kBegin = 0;
	static final int kTranslate = 1;
	static final int kRotatez = 2;
	static final int kRotatex = 3;
	int phase = kBegin;
	double alpha = 0.0F;
	double pihalf = Math.PI/2.0;
	double doublepi = Math.PI*2.0;
	Thread thr = new Thread(this);
	double rot = 0.3F;
	double beta = 0.0F; // change in rot per frame
	double rho, theta=0.3F, phi=1.3F, d, objSize=2.6F,
		v11, v12, v13, v21, v22, v23, v32, v33, v43;

	public void run()
	{	try
		{	for (;;)
			{	boolean dorepaint = false;
				if (alpha < 1.0 && phase != kBegin)
				{	alpha += 0.01;
					if (!(alpha< 1.0))
						alpha = 1.0F; // finish at 1.0 exactly
					dorepaint = true;
				}
				if (beta != 0.0F)
				{	rot = rot + beta;
					if (rot > doublepi)
						rot -= doublepi;
					dorepaint = true;
				}
				if (dorepaint)
					dorepaint();
				Thread.sleep(20);
			}
		}
		catch (InterruptedException e){}
	}

	synchronized void dorepaint()
	{
		repaint();
	}
	ViewTransformDemo()
	{	super();
		//setBackground(Color.WHITE);
		//show();
		objs = new Obj[2];
		objs[0] = new OrthoVec();
		objs[1] = new EyeTransform();
		thr.start();
		addMouseListener(new MouseAdapter()
		{	public void mousePressed(MouseEvent evt)
			{	if ((evt.getModifiers() & (evt.BUTTON3_MASK | evt.BUTTON2_MASK)) != 0)
				{	if (beta != 0.0F)
						beta = 0.0F;
					else if (evt.getX() < 250)
						beta = 0.02F;
					else
						beta = -0.02F;
				}
				else if (phase == kBegin || alpha >= 1.0F)
				{	alpha = 0.0F;
					phase++;
					if (phase > kRotatex)
					{	phase = kBegin;
						dorepaint();
					}
				}
			}
		});
	}

	int iX(double x){return (int)Math.round(centerX + x);}
	int iY(double y){return (int)Math.round(centerY - y);}

	void line(Graphics g, Obj obj, int i, int j)
	{	Point2DD P = obj.vScr[i], Q = obj.vScr[j];
		g.drawLine(iX(P.x), iY(P.y), iX(Q.x), iY(Q.y));
	}

	void drawText(Graphics g, Obj obj, int i, String s)
	{	g.drawString(s, iX(obj.vScr[i].x), iY(obj.vScr[i].y));
	}

	public void draw(Graphics g)
	{	Dimension dim = getSize();
		//int maxX = dim.width - 1, maxY = dim.height - 1,
		int maxX = 500, maxY = 500,
			minMaxXY = Math.min(maxX, maxY);
		//g.setColor(new Color(224,224,224));
		g.setColor(new Color(255,255,255));
		g.fillRect(0, 0, dim.width, dim.height);

		String[] title = {
			"Viewpoint",
			"Transformation",
			"Demonstration"};

		// Draw Title
		Font titleFont = new Font("Arial", Font.BOLD, 18);
		g.setFont(titleFont);
		g.setColor(Color.black);
		g.drawString(title[0], 625, 30);
		g.drawString(title[1], 605, 50);
		g.drawString(title[2], 608, 70);
		g.drawLine(535, 90, 795, 90);
		Font textFontBold = new Font("Arial", Font.BOLD, 14);
		g.setFont(textFontBold);
		g.setColor(Color.RED);
		g.drawString("Click anywhere to start", 545, 120);
		g.drawString("next phase of transformation.", 565, 140);
		Font pointFont = new Font("Arial", Font.PLAIN, 14);
		g.setFont(pointFont);

		centerX = maxX/2; centerY = maxY/2+100;
		Mat3DD mat = new Mat3DD();
		OrthoVec orth = (OrthoVec)objs[0];
		EyeTransform et = (EyeTransform)objs[1];
		switch (phase)
		{
			case kBegin:
				// move back to original position - identity
				et.ShowText(false);
				break;
			case kTranslate:
				et.ShowText(true);
				mat.xlate(orth.e().x * alpha, orth.e().y * alpha, orth.e().z * alpha);
				break;
			case kRotatez:
				Mat3DD rz = new Mat3DD();
				rz.rotz((-pihalf-orth.theta())*alpha);
				mat.xlate(orth.e().x, orth.e().y, orth.e().z);
				mat = rz.matmult(mat);
				break;
			case kRotatex:
				Mat3DD frz = new Mat3DD();
				frz.rotz(-pihalf-orth.theta());
				Mat3DD rx = new Mat3DD();
				rx.rotx(-orth.phi()*alpha);
				mat.xlate(orth.e().x, orth.e().y, orth.e().z);
				mat = frz.matmult(mat);
				mat = rx.matmult(mat);
				break;
			default:
				break;
		}
		et.SetXForm(mat);
		theta = rot;
		initPersp(minMaxXY);
		for (int i=0; i<objs.length; i++)
		{	eyeAndScreen(objs[i]);
			objs[i].drawLines(g, this);
		}
	}
	void eyeAndScreen(Obj o)
	{
		for (int i=0; i<o.w.length; i++)
		{	Point3DD P = o.w[i];
			if (o.xform != null)
				P = o.xform.matvec(P);
			double x = v11 * P.x + v21 * P.y,
				y = v12 * P.x + v22 * P.y + v32 * P.z,
				z = v13 * P.x + v23 * P.y + v33 * P.z + v43;
			o.vScr[i] = new Point2DD(-d * x/z, -d * y/z);
		}
	}
	void initPersp(int minMaxXY)
	{	double costh = Math.cos(theta),
			sinth = Math.sin(theta),
			cosph = Math.cos(phi),
			sinph = Math.sin(phi);
		rho = 5 * objSize; // For reasonable perspective effect
		d = rho* minMaxXY / objSize;
		v11 = -sinth; v12 = -cosph * costh; v13 = sinph * costh;
		v21 = costh;  v22 = -cosph * sinth; v23 = sinph * sinth;
		v32 = sinph;          v33 = cosph;
		v43 = -rho;
	}
}

class Mat3DD
{	double[][] pts;
	Mat3DD()
	{	pts = new double[4][4];
		for (int i=0; i<4; i++)
		{	for (int j=0; j<4; j++)
			{	if (i == j)
					pts[i][j] = 1.0;
				else
					pts[i][j] = 0.0;
			}
		}
	}

	// turn an identity matrix into a translation matrix
	void xlate(double x, double y, double z)
	{	pts[3][0] = x;
		pts[3][1] = y;
		pts[3][2] = z;
	}

	void rotz(double theta)
	{	pts[0][0] = Math.cos(theta);
		pts[1][0] = Math.sin(theta);
		pts[0][1] = -Math.sin(theta);
		pts[1][1] = Math.cos(theta);
	}

	void rotx(double phi)
	{	pts[1][1] = Math.cos(phi);
		pts[2][1] = Math.sin(phi);
		pts[1][2] = -Math.sin(phi);
		pts[2][2] = Math.cos(phi);
	}

	// multiply this by mat, return result (this x mat)
	Mat3DD matmult(Mat3DD mat)
	{	Mat3DD ret = new Mat3DD();
		for (int i=0; i<4; i++)
		{	for (int j=0; j<4; j++)
			{	ret.pts[i][j] = pts[i][0]*mat.pts[0][j]+pts[i][1]*mat.pts[1][j]+
					pts[i][2]*mat.pts[2][j]+pts[i][3]*mat.pts[3][j];
			}
		}
		return ret;
	}

	Point3DD matvec(Point3DD pt)
	{	Point3DD point = new Point3DD((pt.x * pts[0][0] + pt.y*pts[1][0] + pt.z*pts[2][0] + pts[3][0]),
			(pt.x * pts[0][1] + pt.y*pts[1][1] + pt.z*pts[2][1] + pts[3][1]),
			(pt.x * pts[0][2] + pt.y*pts[1][2] + pt.z*pts[2][2] + pts[3][2]));
		return point;
	}
}

abstract class Obj // Contains 3D object data
{	// Elements of viewing matrix V
	Point3DD[] w;     // World coordinates
	Point2DD[] vScr;  // Screen coordinates
	Mat3DD xform = null;
	boolean showText = true;

	void ShowText(boolean showText)
	{	this.showText = showText;
	}

	Obj()
	{
	}

	void SetXForm(Mat3DD x)
	{	xform = x;
	}

	abstract void drawLines(Graphics g, ViewTransformDemo cv);

	Obj dashLine3D(double x1, double y1, double z1, double x2, double y2, double z2, double dashlen)
	{	double dx = x2-x1;
		double dy = y2-y1;
		double dz = z2-z1;
		double len = Math.sqrt(dx*dx+dy*dy+dz*dz);
		int ndashes=1;
		Point3DD[] w=new Point3DD[0];
		Point3DD[] t;
		if (len > dashlen*3)
		{
			ndashes = ((int)((len - dashlen)/dashlen))/2;
			double dashx = (dx/(2*ndashes -1));
			double dashy = (dy/(2*ndashes -1));
			double dashz = (dz/(2*ndashes -1));
			for (int i=0; i<ndashes; i++)
			{
				double xs = x1+2*i*dashx;
				double ys = y1+2*i*dashy;
				double zs = z1+2*i*dashz;
				double xe = x1+(2*i+1)*dashx;
				double ye = y1+(2*i+1)*dashy;
				double ze = z1+(2*i+1)*dashz;
				if (i==ndashes-1)
				{
					xe=x2;
					ye=y2;
					ze=z2;
				}
				t = new Point3DD[w.length+2];
				int j;
				for (j=0; j<w.length; j++)
				{
					t[j] = w[j];
				}
				w = t;
				w[j] = new Point3DD(xs,ys,zs);
				w[j+1] = new Point3DD(xe,ye,ze);
			}
		}
		else
		{
			w=new Point3DD[2];
			w[0] = new Point3DD(x1,y1,z1);
			w[1] = new Point3DD(x2,y2,z2);
		}
		Obj dobj = new Dashes(w);
		return dobj;
	}

	Obj dashline(int i, int j)
	{	Point3DD P = w[i], Q = w[j];
		return dashLine3D(P.x, P.y, P.z, Q.x, Q.y, Q.z, 0.025);
	}
}

class Dashes extends Obj
{
	Dashes(Point3DD[] w)
	{
		this.w = w;
		vScr = new Point2DD[w.length];
	}
	void drawLines(Graphics g, ViewTransformDemo cv)
	{
		for (int i=0; i<w.length; i+=2)
		{
			cv.line(g, this, i, i+1);
		}
	}
}

class OrthoVec extends Obj
{
	Obj[] dobj;
	Point3DD e()
	{	return new Point3DD(.2F, .8F, .4F);
	}
	double rho()
	{	return Math.sqrt(e().x*e().x+e().y*e().y+e().z*e().z);
	}
	double theta()
	{	return Math.atan2(e().y, e().x);
	}
	double phi()
	{	return Math.acos(e().z/rho());
	}
	OrthoVec()
	{	w = new Point3DD[8];
		vScr = new Point2DD[w.length];
		// vectors:
		w[0] = new Point3DD( 0, 0, 0);
		w[1] = new Point3DD( 0, 0, 1);
		w[2] = new Point3DD( 0, 1, 0);
		w[3] = new Point3DD( 1, 0, 0);
		// Top surface:
		w[4] = e();
		w[5] = new Point3DD( w[4].x, w[4].y, 0);
		w[6] = new Point3DD( 0,  w[4].y,  0);
		w[7] = new Point3DD( 0,  0,  w[4].z);
		dobj = new Obj[5];
		dobj[0] = dashline(0, 4);
		dobj[1] = dashline(4, 7);
		dobj[2] = dashline(0, 5);
		dobj[3] = dashline(5, 6);
		dobj[4] = dashline(4, 5);
	}

	void drawLines(Graphics g, ViewTransformDemo cv)
	{	g.setColor(new Color(128, 128, 128));
		cv.line(g, this, 0, 1); cv.line(g, this, 0, 2); cv.line(g, this, 0, 3);
		if (showText)
		{	cv.drawText(g, this, 0, "O");
			cv.drawText(g, this, 1, "zw");
			cv.drawText(g, this, 2, "yw");
			cv.drawText(g, this, 3, "xw");
		}
		for (int i=0; i<dobj.length; i++)
		{
			cv.eyeAndScreen(dobj[i]);
			dobj[i].drawLines(g, cv);
		}
	}
}

class EyeTransform extends Obj
{	EyeTransform()
	{	w = new Point3DD[4];
		vScr = new Point2DD[w.length];
		// vectors:
		w[0] = new Point3DD( 0, 0, 0);
		w[1] = new Point3DD( 0, 0, 1);
		w[2] = new Point3DD( 0, 1, 0);
		w[3] = new Point3DD( 1, 0, 0);
	}

	void drawLines(Graphics g, ViewTransformDemo cv)
	{	g.setColor(new Color(0, 0, 0));
		cv.line(g, this, 0, 1); cv.line(g, this, 0, 2); cv.line(g, this, 0, 3);
		if (showText)
		{	cv.drawText(g, this, 0, "E");
			cv.drawText(g, this, 1, "z");
			cv.drawText(g, this, 2, "y");
			cv.drawText(g, this, 3, "x");
		}
	}
}
