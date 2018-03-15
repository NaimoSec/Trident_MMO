


final class PlainTile
{

	public PlainTile(int colA, int colB, int colD, int colC, int texId, int rgbCol, boolean flat, int color, boolean tex)
	{
		isFlat = true;
		colourA = colA;
		colourB = colB;
		colourD = colD;
		colourC = colC;
		textureId = texId;
		rgbColour = rgbCol;
		isFlat = flat;
		this.color = color;
		textured = tex;
		color1 = colA; // lb = n
		color2 = colB;
		color3 = colD;
		color4 = colC;
		texture = texId;
		anInt722 = rgbCol;
		solid = flat;
		//textured = tex;
	}
	public final int color1; // lA
	public final int color2; // lB
	public final int color3; // lC
	public final int color4; // lD
	public final int texture; // lE
	public boolean solid; // bA
	public final int anInt722; // lF
	final int colourA;
	final int colourB;
	final int colourD;
	final int colourC;
	final int textureId;
	boolean isFlat;
	final int rgbColour;
	final int color;
	final boolean textured;
}
