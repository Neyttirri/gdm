import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.NewImage;
import ij.plugin.PlugIn;
import ij.process.ImageProcessor;
import java.lang.Math;

//erste Uebung (elementare Bilderzeugung)

public class GLDM_U1_S0566223 implements PlugIn {
	
	final static String[] choices = {
		"Schwarzes Bild",
		"Gelbes Bild",
		"Schwarz/Weiss Verlauf",
		"Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf",
		"Französische Fahne",
		"Bangelische Fahne",
		"Tschechische Fahne",
	};
	
	private String choice;
	
	public static void main(String args[]) {
		ImageJ ij = new ImageJ(); // neue ImageJ Instanz starten und anzeigen 
		ij.exitWhenQuitting(true);
		
		GLDM_U1_S0566223 imageGeneration = new GLDM_U1_S0566223();
		imageGeneration.run("");
	}
	
	public void run(String arg) {
		
		int width  = 566;  // Breite
		int height = 400;  // Hoehe
		
		// RGB-Bild erzeugen
		ImagePlus imagePlus = NewImage.createRGBImage("GLDM_U1_S0566223", width, height, 1, NewImage.FILL_BLACK);
		ImageProcessor ip = imagePlus.getProcessor();
		
		// Arrays fuer den Zugriff auf die Pixelwerte
		int[] pixels = (int[])ip.getPixels();
		
		dialog();
		
		////////////////////////////////////////////////////////////////
		// Hier bitte Ihre Aenderungen / Erweiterungen
		
		if ( choice.equals("Schwarzes Bild") ) {
			generateBlackImage(width, height, pixels);
		} else if ( choice.equals("Gelbes Bild") ) {
			generateYellowImage(width, height, pixels);
		} else if ( choice.equals("Schwarz/Weiss Verlauf") ) {
			generateSchwarzWeissVerlauf(width, height, pixels);
		} else if ( choice.equals("Horiz. Schwarz/Rot vert. Schwarz/Blau Verlauf")) {
			generateSchwarzRotSchwarzBlauVerlauf(width, height, pixels);
		} else if( choice.equals("Französische Fahne")) {
			generateFrenchFlag(width, height, pixels);
		} else if( choice.equals("Tschechische Fahne")) {
			generateCzechFlag(width, height, pixels);
		} else if( choice.equals("Bangelische Fahne")) {
			generateBangeliFlag(width, height, pixels);
		}

		
		////////////////////////////////////////////////////////////////////
		
		// neues Bild anzeigen
		imagePlus.show();
		imagePlus.updateAndDraw();
	}

	private void generateBlackImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen
				
				int r = 0;
				int g = 0;
				int b = 0;
				
				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}


	private void generateYellowImage(int width, int height, int[] pixels) {
		// Schleife ueber die y-Werte
		for (int y=0; y<height; y++) {
			// Schleife ueber die x-Werte
			for (int x=0; x<width; x++) {
				int pos = y*width + x; // Arrayposition bestimmen

				int r = 255;
				int g = 255;
				int b = 0;

				// Werte zurueckschreiben
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	private void generateSchwarzWeissVerlauf(int width, int height, int[] pixels) {
		int middle = height/2;
		int increasement = 255/middle;
		int r = 0, g = 0, b = 0;
		int pos;
		for (int y=0; y<height/2; y++) {
			r += increasement;
			b += increasement;
			g += increasement;
			for (int x = 0; x < width; x++) {
				pos = y * width + x;
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;

				//Das gleiche gegenüber machen
				pos = (height - y-1) * width + x;
				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) | b;
			}
		}
	}
	
	private void generateSchwarzRotSchwarzBlauVerlauf(int width, int height, int[] pixels) {
		int pos;
		double increasement;

		//Schwarz-rot
		increasement = (double) 255/height;
		double r = 0;
		for (int y = 0; y < height; y++) {
			r += increasement;
			for(int x=0; x<width; x++) {
				pos = y*width + x;
				pixels[pos] = 0xFF000000 | ((int)r << 16);
			}
		}

		//Schwarz-blau
		increasement = (double) 255/width;
		double b = 0;
		for (int x = 0; x < width; x++) {
			b += increasement;
			for(int y=0; y<height; y++) {
				pos = y*width + x;
				pixels[pos] |= (int) b; // |= Operator, um den Schwarz-Rot-Verlauf beizubehalten
			}
		}
	}
	
	private void generateFrenchFlag(int width, int height, int[] pixels) {
		int r, g, b, pos;

		int third = width/3, twoThirds = 2*third;

		for (int x=0; x<width; x++) {
			for (int y=0; y<height; y++) {
				pos = y*width + x;

				if(x <= third) { //~blau
					r = 0;
					g = 85;
					b = 164;
				} else if(x <= twoThirds) { //~weiß
					r = 255;
					g = 255;
					b = 255;
				} else { //~rot
					r = 239;
					g = 65;
					b = 53;
				}

				pixels[pos] = 0xFF000000 | (r << 16) | (g << 8) |  b;
			}
		}
	}
	
	private void generateCzechFlag(int width, int height, int[] pixels) {
		int pos;
		double halfHeight = height/2, halfWidth = width/2;
		//Weißer und ~roter Balken
		for(int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pos = y*width + x;
				if(y < halfHeight) {
					pixels[pos] = 0xFFFFFFFF;
				} else {
					pixels[pos] = 0xFFD7141A;
				}
			}
		}
		//~Blaues Dreieck links
		double m = halfHeight/halfWidth; //Steigung m
		double newHeight = height;
		for(int x = 0; x < halfWidth; x++) {
			newHeight -= m;
			for(int y = (int) (height - newHeight); y < newHeight; y++) {
				pos = y*width + x;
				pixels[pos] = 0xFF11457E;
			}
		}
	}

	//Berechne die Entfernung von zwei geg. Punkten
	private double distance(int x1, int y1, double x2, double y2) {
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}
	
	private void generateBangeliFlag(int width, int height, int[] pixels) {
		double centerX = (double) width*9/20, centerY = (double) height/2; //Die Verhältnisse wurden Wikipedia entnommen
		double radius = (double) width/5;
		int pos;
		for (int y=0; y<height; y++) {
			for (int x = 0; x < width; x++) {
				pos = y * width + x;
				//Liegt Punkt in Kreis?
				if (distance(x, y, centerX, centerY) < radius) {
					//~rot
					pixels[pos] = 0xFFDA291C;
				} else {
					//~grün
					pixels[pos] = 0xFF006747;
				}

			}
		}
	}
	
	private void dialog() {
		// Dialog fuer Auswahl der Bilderzeugung
		GenericDialog gd = new GenericDialog("Bildart");
		
		gd.addChoice("Bildtyp", choices, choices[0]);
		
		
		gd.showDialog();	// generiere Eingabefenster
		
		choice = gd.getNextChoice(); // Auswahl uebernehmen
		
		if (gd.wasCanceled())
			System.exit(0);
	}
}

