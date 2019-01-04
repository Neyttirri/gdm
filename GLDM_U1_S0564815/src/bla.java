
public class bla {

	public static void main(String[] args) {
		int width =7; // (int) (Math.random()* 100 + 1);
		int height = 7; //(int) (Math.random()* 100 + 1);
		int[] test = new int[width*height];
		//array with extended dimensions
		int newW = width + 2;
		int newH = height + 2;
		int[] extendedPixels = new int[newW*newH];
		int posExtended;
		for (int i = 0; i < test.length; i++) {
			test[i] = 2*i + 3 - i*5/13;
		}
		
		
		for (int i = 0; i < newH; i++) {
			for (int j = 0; j < newW; j++) {
				posExtended = i*newW + j;
				//the diagonals top left and right
				if(posExtended == 0 ) 
				{
					extendedPixels[posExtended] = test[posExtended];
				}
				else if(i == 0 && j == newW-1) { //make it with posExtended
					extendedPixels[posExtended] = test[posExtended-2];
				}
				//top body
				else if(i == 0) {
					extendedPixels[posExtended] = test[posExtended-1];
				}
				
				//bottom diagonals left and right
				else if(i == newH - 1 && j == 0) {
					extendedPixels[posExtended] = test[(i - 2)*width];
				}
				else if(i == newH - 1 && j == newW - 1) {
					extendedPixels[posExtended] = test[(i - 2)*width + (j - 2)];
				}
				//bottom body
				else if(i == newH - 1) {
					extendedPixels[posExtended] = test[(i - 2)*width + (j - 1)];
				}
				//left
				else if(posExtended % newW == 0) 
				{
					extendedPixels[posExtended] = test[(i-1)*width];	
				}
				//right
				else if(posExtended % newW == newW-1)  
				{
					extendedPixels[posExtended] = test[(i-1)*width + j - 2];
				}
				//body
				else {
					extendedPixels[posExtended] = test[(i-1)*width + j -1];
				}
				
				
				
				
				/*
				else if(posExtended == newW -1) extendedPixels[posExtended] = test[posExtended -2];
				else if(posExtended < newW -1)
				{
					extendedPixels[posExtended] = test[posExtended -1];
				}
				//diagonals bottom
				else if(i == newH -1 && posExtended % newW == 0)
				{
					extendedPixels[posExtended] = test[(i-2)*width];	
				}
				else if(i == newH -1 && j == newW - 1)
				{
					extendedPixels[posExtended] = test[(i-2)*width + j - 2];	
				}
				else if(posExtended % newW == 0) //left
				{
					extendedPixels[posExtended] = test[(i-1)*width + j];	
				}
				else if(posExtended % newW == newW-1) //right 
				{
					extendedPixels[posExtended] = test[(i-1)*width + j - 2];
				}
				else if(i == newH - 1) //bottom
				{ 
					extendedPixels[posExtended] = test[(i-2)*width + j - 1];
				}
				
				else  { //the common body inside
					extendedPixels[posExtended] = test[(i-1)*width + j -1];
				}*/
			}
		}
	for (int i = 0; i < height; i++) {
		for (int j = 0; j < width; j++) {
			int pos = i*width + j;
			System.out.print(test[pos] + "   ");
			if(j== width - 1) System.out.println();
			
		}
	}
	for (int i = 0; i < newH; i++) {
		for (int j = 0; j < newW; j++) {
			int pos = i*newW + j;
			System.out.print(extendedPixels[pos] + "   ");
			if(j== newW -1) System.out.println();
			
		}
	}
			
	
	}
}
