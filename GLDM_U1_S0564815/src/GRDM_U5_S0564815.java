import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.ImageCanvas;
import ij.gui.ImageWindow;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
     Opens an image window and adds a panel below the image
 */
public class GRDM_U5_S0564815 implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;
	private int[] extPixels;

	String[] items = {"Original","Filter 1", "Wechgezeichnetes Bild", "Hochpassgefiltert", "verstaerkte Kanten"};


	public static void main(String args[]) {

		IJ.open("C:\\Users\\Anele\\Desktop\\ImageJ\\images\\sail.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GRDM_U5_S0564815 pw = new GRDM_U5_S0564815();
		pw.imp = IJ.getImage();
		pw.run("");
	}

	public void run(String arg) {
		if (imp==null) 
			imp = WindowManager.getCurrentImage();
		if (imp==null) {
			return;
		}
		CustomCanvas cc = new CustomCanvas(imp);

		storePixelValues(imp.getProcessor());

		new CustomWindow(imp, cc);
	}


	private void storePixelValues(ImageProcessor ip) {
		width = ip.getWidth();
		height = ip.getHeight();

		origPixels = ((int []) ip.getPixels()).clone();
	}


	class CustomCanvas extends ImageCanvas {

		CustomCanvas(ImagePlus imp) {
			super(imp);
		}

	} // CustomCanvas inner class


	class CustomWindow extends ImageWindow implements ItemListener {

		private String method;
		
		CustomWindow(ImagePlus imp, ImageCanvas ic) {
			super(imp, ic);
			addPanel();
		}

		void addPanel() {
			//JPanel panel = new JPanel();
			Panel panel = new Panel();

			JComboBox cb = new JComboBox(items);
			panel.add(cb);
			cb.addItemListener(this);

			add(panel);
			pack();
		}

		public void itemStateChanged(ItemEvent evt) {

			// Get the affected item
			Object item = evt.getItem();

			if (evt.getStateChange() == ItemEvent.SELECTED) {
				System.out.println("Selected: " + item.toString());
				method = item.toString();
				changePixelValues(imp.getProcessor());
				imp.updateAndDraw();
			} 

		}


		private void changePixelValues(ImageProcessor ip) {
			int width = ip.getWidth();
			//array with extended dimensions
			int newW = width + 2;
			int newH = ip.getHeight() + 2;
			int[] extendedPixels = new int[newW*newH];
			int posExtended;
			for (int i = 0; i < newH; i++) {
				for (int j = 0; j < newW; j++) {
					posExtended = i*newW + j;
					if(posExtended == 0 ) 
					{
						extendedPixels[posExtended] = origPixels[posExtended];
					}
					else if(i == 0 && j == newW-1) { //make it with posExtended
						extendedPixels[posExtended] = origPixels[posExtended-2];
					}
					//top body
					else if(i == 0) {
						extendedPixels[posExtended] = origPixels[posExtended-1];
					}
					
					//bottom diagonals left and right
					else if(i == newH - 1 && j == 0) {
						extendedPixels[posExtended] = origPixels[(i - 2)*width];
					}
					else if(i == newH - 1 && j == newW - 1) {
						extendedPixels[posExtended] = origPixels[(i - 2)*width + (j - 2)];
					}
					//bottom body
					else if(i == newH - 1) {
						extendedPixels[posExtended] = origPixels[(i - 2)*width + (j - 1)];
					}
					//left
					else if(posExtended % newW == 0) 
					{
						extendedPixels[posExtended] = origPixels[(i-1)*width];	
					}
					//right
					else if(posExtended % newW == newW-1)  
					{
						extendedPixels[posExtended] = origPixels[(i-1)*width + j - 2];
					}
					//body
					else {
						extendedPixels[posExtended] = origPixels[(i-1)*width + j -1];
					}
				}
			}
			extendedPixels = extendedPixels;
			// Array zum Zurückschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						
						pixels[pos] = origPixels[pos];
					}
				}
			}
			
			if (method.equals("Filter 1")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;

						int rn = r/2;
						int gn = g/2;
						int bn = b/2;

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
					}
				}
			}
			if (method.equals("Wechgezeichnetes Bild")) {
  
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int posOrig = y*width + x;
						int extPos =  (y + 1)*newW + (x + 1);
						int argb =origPixels[posOrig]; 
								
						
						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int rn = calculateNewValues(extPos,r, 1, 1, 9, ip);
						int gn = calculateNewValues(extPos,g, 1, 1, 9, ip);
						int bn = calculateNewValues(extPos,b, 1, 1, 9, ip);
						
						pixels[posOrig] = (0xFF<<24) | (rn<<16) | (gn << 8) | bn;
						
					}
				}
			}
			
			
			
		}
		
		private int calculateNewValues(int position, int valueKern, int faktorKern, int valuePixel, int scale,ImageProcessor ip) {
			int sum = extPixels[position - ip.getWidth() -3] + extPixels[position - ip.getWidth() -2] + extPixels[position - ip.getWidth() -1] + extPixels[position - 1]
					+ extPixels[position + 1] + extPixels[position + ip.getWidth() + 1] + extPixels[position + ip.getWidth() + 2] + extPixels[position + ip.getWidth() + 3];
			int n = (sum*valuePixel + faktorKern* valueKern) / scale;
			
			return n;
		}


	} // CustomWindow inner class
} 