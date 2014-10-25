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
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

class SplashWindow extends JWindow
{
    public SplashWindow(String filename, Frame f, int waitTime)
    {
        super(f);
        JLabel l = new JLabel(new ImageIcon(filename));
        getContentPane().add(l, BorderLayout.CENTER);
        pack();
        Dimension screenSize =
          Toolkit.getDefaultToolkit().getScreenSize();
        Dimension labelSize = l.getPreferredSize();
        setLocation(screenSize.width/2 - (labelSize.width/2),
                    screenSize.height/2 - (labelSize.height/2));
        addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {
                    setVisible(false);
                    dispose();
                }
            });
        final int pause = waitTime;
        final Runnable closerRunner = new Runnable()
            {
                public void run()
                {
                    setVisible(false);
                    dispose();
                }
            };
        Runnable waitRunner = new Runnable()
            {
                public void run()
                {
                    try
                        {
                            Thread.sleep(pause);
                            SwingUtilities.invokeAndWait(closerRunner);
                        }
                    catch(Exception e)
                        {
                            e.printStackTrace();
                            // can catch InvocationTargetException
                            // can catch InterruptedException
                        }
                }
            };
        setVisible(true);
        Thread splashThread = new Thread(waitRunner, "SplashThread");
        splashThread.start();
    }
}

