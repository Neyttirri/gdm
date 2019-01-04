
class YUV {
	private double y, u, v, originalBrightness, brightness;
	
	YUV(int r, int g, int b) {
		y = 0.299 * r + 0.587 * g + 0.114 * b;
		//System.out.println("y is " + y);
		u = (b - y) * 0.493;
		v = (r - y) * 0.877;
		originalBrightness = y;
	}
	
	YUV changeBrightness(double brightness) {
		y += brightness;
		this.brightness = brightness;
		return this;
	}
	
	YUV changeContrast(double contrast) {
		y = contrast * (originalBrightness - 127.5) + 127.5 + brightness;
		return this;
	}
	
	YUV changeSaturation(double saturation) {
		u = (u-1)*saturation + 1;
		v = (v-1)*saturation + 1;
		return this;
	}
	
	YUV changeHue(double degree) {
		double rad = Math.toRadians(degree);
		double sin = Math.sin(rad);
		double cos = Math.cos(rad);
		u = u*(cos - sin);
		v = v*(sin + cos);
		return this;
	}
	
	int[] toRGB() {
		int[] rgb = new int[3];
		rgb[0] = (int) Math.max(0, Math.min(y + v/0.877, 255)); //R
		rgb[2] = (int) Math.max(0, Math.min(y + u/0.493, 255)); //B
		rgb[1] = (int) Math.max(0, Math.min(1/0.587 * y - 0.299/0.587*rgb[0] - 0.114/0.587 * rgb[2], 255)); //G
		return rgb;
	}
}
