package net.kno3.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Jaxon A Brown
 */
public class Color {
	public static final Color RED;
	public static final Color GREEN;
	public static final Color BLUE;
	public static final Color MATT;

	private static final Set<Color> STANDARD_COLORS;

	static {
		STANDARD_COLORS = new HashSet<>();

		STANDARD_COLORS.add(RED = new Color("Red", 255, 0, 0));
		STANDARD_COLORS.add(GREEN = new Color("Green", 0, 255, 0));
		STANDARD_COLORS.add(BLUE = new Color("Blue", 0, 0, 255));
		STANDARD_COLORS.add(MATT = new Color("Matt", 0, 0, 0));// Default
	}

	private String name;
	private int r, g, b;

	Color(String name, int r, int g, int b) {
		this.name = name;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public Color(int r, int g, int b) {
		this(null, r, g, b);
	}

	public boolean isStandard() {
		return this.name != null;
	}

	public String getName() {
		return name;
	}

	public int getR() {
		return r;
	}

	public int getG() {
		return g;
	}

	public int getB() {
		return b;
	}

	public String toString() {
		return "Color[r: " + getR() + ", g: " + getG() + ", b: " + getB() + "]";
	}

	public static void setMatt(int r, int g, int b) {
		MATT.r = r;
		MATT.g = g;
		MATT.b = b;
	}

	public static Color getClosestColor(Color color) {
		double shortestDistance = 0;
		Color closestColor = null;
		for(Color standardColor : STANDARD_COLORS) {
			double thisDistance;
			if(closestColor == null) {
				closestColor = standardColor;
				shortestDistance = colorDistanceSquared(color, closestColor);
			} else if((thisDistance = colorDistanceSquared(color, standardColor)) < shortestDistance) {
				closestColor = standardColor;
				shortestDistance = thisDistance;
			}
		}
		return closestColor;
	}

	private static double colorDistanceSquared(Color c1, Color c2) {
		int red1 = c1.getR();
		int red2 = c2.getR();
		int rmean = (red1 + red2) >> 1;
		int r = red1 - red2;
		int g = c1.getG() - c2.getG();
		int b = c1.getB() - c2.getB();
		return (((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767 - rmean) * b * b) >> 8);
	}
}
