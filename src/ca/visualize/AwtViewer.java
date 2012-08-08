package ca.visualize;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import javax.swing.JComponent;
import javax.swing.JFrame;

import ca.base.Rule;

/**
 * 
 * Dummy rule visualizer
 * 
 * @author houp
 *
 */
public class AwtViewer {
	
	private static void drawLine(final int[] buffer, boolean[] line, int h) {
		for(int i=0;i<line.length;i++) {
			buffer[h*line.length + i] = !line[i] ? (int)0xFFFFFFFF : 0;
		}
	}
	
	public static void main(String args[]) {

		// 
		// RULE TO DRAW
		// 
		ca.base.Rule rule = (new Rule(150, 1)).increaseRadius().increaseRadius();
		
		
		JFrame frame = new JFrame("Cellular Automata");
		final int width = 512;
		final int height = 512;

		final BufferedImage image = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_RGB);

		@SuppressWarnings("serial")
		JComponent viewer = new JComponent() {
			@Override
			public void paintComponent(Graphics g) {
				g.drawImage(image, 0, 0, width, height, this);
			}
		};

		viewer.setPreferredSize(new Dimension(width, height));
		frame.getContentPane().add(viewer);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		final int[] imageRgb = ((DataBufferInt) image.getRaster()
				.getDataBuffer()).getData();
		
		boolean[] state = new boolean[width];
		state[width/2] = true;
		
		drawLine(imageRgb, state, 0);
		
		for(int i=1;i<height;i++) {
			state = rule.eval(state);
			drawLine(imageRgb, state, i);
		}

		viewer.repaint();
	}
}
