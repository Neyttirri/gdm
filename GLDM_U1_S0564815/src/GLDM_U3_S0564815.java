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
public class GLDM_U3_S0564815  implements PlugIn {

	ImagePlus imp; // ImagePlus object
	private int[] origPixels;
	private int width;
	private int height;

	String[] items = {"Original", "Rot-Kanal", "Negativ des Bildes", "Graustufen","Bin‰rbild", "Bin‰rbild mit 5 Stufen","Bin‰rbild mit 10 Stufen", "Bin‰rbild mit Diffusion", "Sepia", "6 Farben"}; //"Error Diffusion"


	public static void main(String args[]) {

		IJ.open("C:\\\\Users\\\\Anele\\\\fiji-win64\\\\Bear.jpg");
		//IJ.open("Z:/Pictures/Beispielbilder/orchid.jpg");

		GLDM_U3_S0564815 pw = new GLDM_U3_S0564815();
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

			// Array zum Zur√ºckschreiben der Pixelwerte
			int[] pixels = (int[])ip.getPixels();

			if (method.equals("Original")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						
						pixels[pos] = origPixels[pos];
					}
				}
			}
			
			if (method.equals("Rot-Kanal")) {

				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  // Lesen der Originalwerte 

						int r = (argb >> 16) & 0xff;
						//int g = (argb >>  8) & 0xff;
						//int b =  argb        & 0xff;

						int rn = r;
						int gn = 0;
						int bn = 0;

						// Hier muessen die neuen RGB-Werte wieder auf den Bereich von 0 bis 255 begrenzt werden

						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			if (method.equals("Graustufen")) {
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						double Y = 0.299 * r + 0.587 * g + 0.114 * b;
						int rn = (int) Y;
						int gn = (int) Y;
						int bn = (int) Y;
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				
					} 
			
				}
			
			}

			
			if (method.equals("Negativ des Bildes")) {
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int rn = 255 -r;
						int gn = 255 - g;
						int bn = 255 - b;
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					}
				}
			}
			if (method.equals("Bin‰rbild")) {
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int rn, gn, bn;
						rn = gn = bn = ((r+g+b)/3) > 127.5 ? 255 : 0;
							
						
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					} 
				}
			
			}
			if (method.equals("Bin‰rbild mit 5 Stufen")) {
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int rn, gn, bn;
						double Y = 0.299 * r + 0.587 * g + 0.114 * b;
						double delta = 255/5;
						int res = (int)(Y / delta);
						rn = gn = bn = res * (255/4);
							
						
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					} 
				}
			
			}
			if (method.equals("Bin‰rbild mit 10 Stufen")) {
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int rn, gn, bn;
						double Y = 0.299 * r + 0.587 * g + 0.114 * b;
						double delta = 255/10;
						rn = gn = bn =(int)(Y / delta) * (255/9);
							
						
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
					} 
				}
			
			}
			
			if (method.equals("Bin‰rbild mit Diffusion")) {
				double error = 0.0;
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int rn, gn, bn;
						double Y = Math.max(0, Math.min(0.299 * r + 0.587 * g + 0.114 * b + error, 255));
						rn = gn = bn = Y > 127.5 ? 255 : 0;
						/* if(Y < 128) { //schwarz
	                            error = Y;
	                            rn = gn = bn = 0;
	                        } else { //weiﬂ
	                            error = -Y;
	                            rn = gn = bn = 255;
	                        }*/
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;

						error = Y -rn;
						
					} 
			
				}
				
				
			}
			if (method.equals("Sepia")) {
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						double Y = 0.299 * r + 0.587 * g + 0.114 * b;
						int rn = (int) Y;
						int gn = (int) Y;
						int bn = (int) Y;
						
						
						//Werte von Internet als Empfehlung genommen
						rn += 40;
						gn += 20;
						if(rn > 255) rn = 255;
						if(gn > 255) gn = 255;
						
						/*
						int rn = (int) (r * .393 + g * .769 + b * .189);
                        int gn = (int) (r * .349 + g * .686 + b * .168);
                        int bn = (int) (r * .272 + g * .534 + b * .131);
                        */
						
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
						
					} 
			
				}
			
			}
			if (method.equals("6 Farben")) {
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						int rn = 0;
						int gn = 0;
						int bn =0;
						//calculate distance 
						//to the first color (11,16,16)
						double d1 = Math.sqrt((Math.pow((11-r), 2) + Math.pow((16-g), 2) + Math.pow((16-b), 2)));
						//to the second color (25,32,33)
						double d2 = Math.sqrt((Math.pow((25-r), 2) + Math.pow((32-g), 2) + Math.pow((33-b), 2)));
						//to the third color (62,56,51)
						double d3 = Math.sqrt((Math.pow((62-r), 2) + Math.pow((56-g), 2) + Math.pow((51-b), 2)));
						//to the fourth color (53,104,140)
						double d4 = Math.sqrt((Math.pow((53-r), 2) + Math.pow((104-g), 2) + Math.pow((140-b), 2)));
						//to the fifth color (82,87,88)
						double d5 = Math.sqrt((Math.pow((82-r), 2) + Math.pow((87-g), 2) + Math.pow((88-b), 2)));
						//to the sixth color (184,177,175)
						double d6 = Math.sqrt((Math.pow((184-r), 2) + Math.pow((177-g), 2) + Math.pow((175-b), 2)));
						
						double smallestDistance = Math.min(d1, Math.min(d2, Math.min(d3, Math.min(d4, Math.min(d5, d6)))));
						
						
						if(smallestDistance ==  d1) {
							rn = 11;
							gn = 16;
							bn = 16;
							}
						if(smallestDistance== d2) {
							rn = 25;
							gn = 32;
							bn = 33;
						}
						if(smallestDistance ==  d3) {
							rn = 62;
							gn = 56;
							bn = 51;
						}
						if(smallestDistance ==  d4) {
							rn = 53;
							gn = 104;
							bn = 140;
						}
						if(smallestDistance ==  d5) {
							rn = 82;
							gn = 87;
							bn = 88;
						}
						if(smallestDistance ==  d6) {
							rn = 184;
							gn = 177;
							bn = 175;
						}
						
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
				
					} 
			
				}
			
			}
			
			/*
			if (method.equals("Error Diffusion")) {
				double error = 0.0;
				boolean bottom = true, left = true, right = true;
				for (int y=0; y<height; y++) {
					for (int x=0; x<width; x++) {
						int pos = y*width + x;
						int argb = origPixels[pos];  

						int r = (argb >> 16) & 0xff;
						int g = (argb >>  8) & 0xff;
						int b =  argb        & 0xff;
						
						int rn, gn, bn;
						double Y = 0.299 * r + 0.587 * g + 0.114 * b;
						rn = gn = bn = Y > 127.5 ? 255 : 0;
						
						pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;

						error = Y - rn;
						
						if (pos>= (height -1)*width) {
							bottom = false;
						}
						if (pos == 0 || pos % width == 0 || bottom) {
							left = false;
						}
						if (pos % width == 1 ) {
							right = false;
						}
						if(left) { pos = (y+1)*width + x - 1;
						
							 argb = origPixels[pos];  
	
							 r = (argb >> 16) & 0xff;
							 g = (argb >>  8) & 0xff;
							 b =  argb        & 0xff;
							
							 /*rn = (int) (r + error*3/16);
							 gn = (int) (g + error*3/16);
							 bn = (int) (b + error*3/16);*/
						
			/*
							 rn = bn = gn = (int) (Y = 0.299 * r + 0.587 * g + 0.114 * b + error*3.0/16.0);
							 pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
						}
						if(bottom) {
							pos = (y+1)*width + x;
							
							 argb = origPixels[pos];  
	
							 r = (argb >> 16) & 0xff;
							 g = (argb >>  8) & 0xff;
							 b =  argb        & 0xff;
							/* rn = (int) (r + error*5/16);
							 gn = (int) (g + error*5/16);
							 bn = (int) (b + error*5/16);*/
			/*
							 rn = bn = gn = (int) (Y = 0.299 * r + 0.587 * g + 0.114 * b + error*5.0/16.0);
							 pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
							 if(right) {
									pos = (y+1)*width + x +1;
									
									 argb = origPixels[pos];  
			
									 r = (argb >> 16) & 0xff;
									 g = (argb >>  8) & 0xff;
									 b =  argb        & 0xff;
									/* rn = (int) (r + error*1/16);
									 gn = (int) (g + error*1/16);
									 bn = (int) (b + error*1/16);*/
					/*				 rn = bn = gn = (int) (Y = 0.299 * r + 0.587 * g + 0.114 * b + error*.0/16.0);
									 pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
								}
						}
						
						if(right) {
							
								 pos = y*width + x + 1;
								 argb = origPixels[pos];  
		
								 r = (argb >> 16) & 0xff;
								 g = (argb >>  8) & 0xff;
								 b =  argb        & 0xff;
								/* rn = (int) (r + error*7/16);
								 gn = (int) (g + error*7/16);
								 bn = (int) (b + error*7/16);*/
					/*			 rn = bn = gn = (int) (Y = 0.299 * r + 0.587 * g + 0.114 * b + error*7.0/16.0);
								 pixels[pos] = (0xFF<<24) | (rn<<16) | (gn<<8) | bn;
							
						}
						
					} 
			
				}
			}*/
			
		}

	} // CustomWindow inner class
	
	
	
} 
