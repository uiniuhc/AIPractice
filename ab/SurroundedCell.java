
public class SurroundedCell {
	public int coord_x;
	public int coord_y;
	public int center;
	public int top;
	public int bottom;
	public int left;
	public int right;
	/**
	 * 
	 * @param x
	 * @param y
	 * @param cen
	 * @param t
	 * @param b
	 * @param l
	 * @param r
	 */
	SurroundedCell(int x,int y, int cen,int l,int r,int t, int b){
		coord_x=x;coord_y=y;center=cen;top=t;bottom=b;left=l;right=r;
	}
}
