package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Map {
    private int[][] mapInfo;
    private int row;
    private int col;

    public Map(int row, int col, String filePath) {
        setRow(row);
        setCol(col);
        this.mapInfo = new int[row][col];

        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(filePath);
            if (inputStream != null) {
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                String buffer;
                int currentRow = 0;
                while ((buffer = br.readLine()) != null && currentRow < this.row) {
                    String[] values = buffer.split(",");
                    for (int currentCol = 0; currentCol < Math.min(this.col, values.length); currentCol++) {
                        this.mapInfo[currentRow][currentCol] = Integer.parseInt(values[currentCol]);
                    }
                    currentRow++;
                }
                br.close();
            } else {
                System.err.println("File not found: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("IOException occurs");
        }
    }

    public int[][] getMapInfo() {
        return mapInfo;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return this.row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getCol() {
        return this.col;
    }
}
