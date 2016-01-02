/**
 * Assignment 2: Seam Carving
 * Name: Tri Minh Cao
 * Email: trimcao@gmail.com
 * Date: December 2015
 *
 * Important Note: I do not explicitly transpose the image to find horizontal seams.
 * This method is fairly complicated to understand.
 */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

public class SeamCarver {
    private int width;
    private int height;
    private double[][] energies;
    private int[][] colors; // store the colors information of the picture
    private boolean transposed = false;

    public SeamCarver(Picture picture)                // create a seam carver object based on the given picture
    {
        height = picture.height();
        width = picture.width();
        colors = new int[height][width];
        Color color;
        // copy the picture's pixels to colors matrix
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                color = picture.get(j, i);
                colors[i][j] = color.getRGB();
            }
        }
        // build the energy matrix
        energies = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                energies[i][j] = energy(j, i);
            }
        }
    }

    public Picture picture()                          // current picture
    {
        // possibly I need to create a new array of colors (we might have removed several seams)
        // create a picture based on the colors array
        Picture pic = new Picture(width, height);
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = new Color(colors[i][j]);
                pic.set(j, i, color);
            }
        }
        return pic;
    }
    public int width()                            // width of current picture
    {
        if (transposed)
            return height;
        else
            return width;
    }
    public int height()                           // height of current picture
    {
        if (transposed)
            return width;
        else
            return height;
    }

    private void setHeight(int h) {
        if (transposed)
            width = h;
        else
            height = h;
    }

    private void setWidth(int w) {
        if (transposed)
            height = w;
        else
            width = w;
    }

    private void flip() {
        if (transposed)
            transposed = false;
        else
            transposed = true;
    }
    // transpose method
    private void transpose() {
        flip();
        int temp = height;
        height = width;
        width = temp;
        int[][] newColors = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newColors[i][j] = colors[j][i];
            }
        }
        // build the energy matrix
        double[][] newEnergies = new double[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                newEnergies[i][j] = energies[j][i];
            }
        }
        colors = newColors;
        energies = newEnergies;
    }

    private boolean isBorder(int x, int y)
    {
        return (x == 0 || y == 0 || x == (width - 1) || y == (height - 1));
    }
    // find the delta^2 component in the dual-gradient formula
    private double delta2(Color one, Color another) {
        double red = (one.getRed() - another.getRed())*(one.getRed() - another.getRed());
        double green = (one.getGreen() - another.getGreen())*(one.getGreen() - another.getGreen());
        double blue = (one.getBlue() - another.getBlue())*(one.getBlue() - another.getBlue());
        return (red + green + blue);
    }

    public double energy(int x, int y)               // energy of pixel at column x and row y
    {
        if (!validIndex(x, y)) {
            throw new IndexOutOfBoundsException("index out of bounds.");
        }
        int currentX = x;
        int currentY = y;
        if (transposed) {
            currentX = y;
            currentY = x;
        }
        if (isBorder(currentX, currentY)) {
            return 1000;
        }
        else {
            Color left = new Color(colors[currentY][currentX - 1]);
            Color right = new Color(colors[currentY][currentX + 1]);
            Color up = new Color(colors[currentY - 1][currentX]);
            Color bottom = new Color(colors[currentY + 1][currentX]);
            double deltaX = delta2(left, right);
            double deltaY = delta2(up, bottom);
            return Math.sqrt(deltaX + deltaY);
        }
    }

    private boolean validIndex(int x, int y) {
        int currentX = x;
        int currentY = y;
        if (transposed) {
            currentX = y;
            currentY = x;
        }
        if (currentX < 0 || currentX >= width || currentY < 0 || currentY >= height) {
            return false;
        }
        else
            return true;
    }

    // helper method to relax the edges from a vertex
    private void relaxEdge(int x1, int y1, int x2, int y2, double[][] distTo, int[][] parent) {
        if (validIndex(x2, y2)) {
            double currentDist = distTo[y1][x1] + getEnergy(y2, x2);
            if (currentDist < distTo[y2][x2]) {
                distTo[y2][x2] = currentDist;
                parent[y2][x2] = x1;
            }
        }
    }

    private double getEnergy(int row, int col) {
        if (transposed)
            return energies[col][row];
        else
            return energies[row][col];
    }

    public int[] findHorizontalSeam()               // sequence of indices for horizontal seam
    {
        if (!transposed)
            transposed = true;
        int[] seam = findVerticalSeam();
        transposed = false;
        return seam;
    }

    public int[] findVerticalSeam()                 // sequence of indices for vertical seam
    {
        //System.out.println("transpose: " + transpose);
        int height = height();
        int width = width();

        // parent is the column index of the predecessor in the shortest path
        int[][] parent = new int[height][width];
        double[][] distTo = new double[height][width];
        // initialization
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                distTo[i][j] = Integer.MAX_VALUE;
                parent[i][j] = -1;
            }
        }
        // initilize the first row
        for (int j = 0; j < width; j++) {
            distTo[0][j] = getEnergy(0, j);
        }
        // process other rows, except the last row
        for (int i = 0; i < (height - 1); i++) {
            for (int j = 0; j < width; j++) {
                // note: j is x-axis, i is y-axis
                relaxEdge(j, i, j - 1, i + 1, distTo, parent);
                relaxEdge(j, i, j, i + 1, distTo, parent);
                relaxEdge(j, i, j + 1, i + 1, distTo, parent);
            }
        }
        // find the min vertex in the last row
        int[] seam = new int[height];
        double minDist = Integer.MAX_VALUE;
        for (int j = 0; j < width; j++) {
            if (distTo[height - 1][j] < minDist) {
                minDist = distTo[height - 1][j];
                seam[height - 1] = j;
            }
        }
        // find the seam
        for (int i = height - 1; i > 0; i--) {
            seam[i - 1] = parent[i][seam[i]];
        }

        return seam;
    }

    private void setEnergy(int newRow, int newCol, double[][] newEnergies, int oldRow, int oldCol, boolean compute) {
        if (transposed) {
            if (compute) {
                newEnergies[newCol][newRow] = energy(newCol, newRow);
            }
            else
                newEnergies[newCol][newRow] = energies[oldCol][oldRow];
        }
        else {
            if (compute) {
                newEnergies[newRow][newCol] = energy(newCol, newRow);
            }
            else
                newEnergies[newRow][newCol] = energies[oldRow][oldCol];
        }
    }

    private void setColor(int newRow, int newCol, int[][] newColors, int oldRow, int oldCol) {
        if (transposed) {
            newColors[newCol][newRow] = colors[oldCol][oldRow];
        }
        else {
            newColors[newRow][newCol] = colors[oldRow][oldCol];
        }

    }

    public void removeHorizontalSeam(int[] seam)   // remove horizontal seam from current picture
    {
        // transpose
        // call remove Vertical Seam, that's it
        // no mess up with findHorizontalSeam
        if (!transposed)
            transposed = true;
        removeVerticalSeam(seam);
        transposed = false;
    }

    public void removeVerticalSeam(int[] seam)     // remove vertical seam from current picture
    {
        int height = height();
        int width = width();

        if (seam == null)
            throw new NullPointerException("seam is null.");
        if (width <= 1)
            throw new IllegalArgumentException("canvas dimension is too small.");
        // check if the seam is invalid
        if (!(seam.length == height))
            throw new IllegalArgumentException("seam's length is not appropriate.");
        for (int i = 0; i < seam.length; i++) {
            if ((seam[i] < 0) || (seam[i] >= width)) {
                throw new IllegalArgumentException("seam's position is illegal.");
            }
            // check if distance between pixels > 1
            if (i < seam.length - 1) {
                if (Math.abs(seam[i] - seam[i + 1]) > 1)
                    throw new IllegalArgumentException("distance between two pixels > 1.");
            }
        }

        // update width (height remains the same)
        // create new Color matrix
        width--;
        setWidth(width);
        int originalWidth = this.width;
        int originalHeight = this.height;

        int[][] newColors = new int[originalHeight][originalWidth];
        for (int i = 0; i < height; i++) {
            int removeCol = seam[i];
            for (int j = 0; j < width; j++) {
                if (j >= removeCol)
                    setColor(i, j, newColors, i, j + 1);
                else
                    setColor(i, j, newColors, i, j);
            }
        }
        colors = newColors;
        // create new Energies matrix
        // update adjacent blocks to the remove column in each row
        double[][] newEnergies = new double[originalHeight][originalWidth];
        for (int i = 0; i < height; i++) {
            int removeCol = seam[i];
            for (int j = 0; j < width; j++) {
                if ( (j == removeCol) || (j == removeCol - 1) || (j == removeCol + 1) ) {
                    //newEnergies[i][j] = energy(j, i);
                    setEnergy(i, j, newEnergies, i, j, true);
                }
                else if (j > removeCol)
                    //newEnergies[i][j] = energies[i][j + 1];
                    setEnergy(i, j, newEnergies, i, j + 1, false);
                else
                    //newEnergies[i][j] = energies[i][j];
                    setEnergy(i, j, newEnergies, i, j, false);
            }
        }
        energies = newEnergies;
    }

    public static void main(String[] args) {
        Picture pic = new Picture("3x7.png");
        SeamCarver test = new SeamCarver(pic);
        //double energyTest = test.energy(5, 5);
        //another.show();
        int[] seam = new int[3];
        seam[0] = 6;
        seam[1] = 6;
        seam[2] = 4;
        test.removeHorizontalSeam(seam);
    }
}
