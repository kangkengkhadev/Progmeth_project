package logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class Map {
    private int[][] mapInfo;
    private int[][] mapItemsInfo;
    private int row;
    private int col;

    public Map(int row, int col, String filePath) {
        setRow(row);
        setCol(col);
        this.mapInfo = new int[row][col];
        this.mapItemsInfo = new int[row][col];
        // Read the map from the filePath
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
                        if(Integer.parseInt(values[currentCol]) == -1){
                            if(currentRow == 17 && currentCol == 23){
                                this.mapItemsInfo[currentRow][currentCol] = 0;
                            }else{
                                this.mapItemsInfo[currentRow][currentCol] = 1;
                            }

                        }else{
                            this.mapItemsInfo[currentRow][currentCol] = 0;
                        }

                    }
                    currentRow++;
                }
                br.close();
                System.out.println(Arrays.deepToString(mapItemsInfo));
            } else {
                System.err.println("File not found: " + filePath);
            }
        } catch (IOException e) {
            System.err.println("IOException occurs");
        }
    }

    public boolean isWall(double x, double y) {
        int row = (int) y;
        int col = (int) x;
        return mapInfo[row][col] != -1;
    }
    public int[][] getMapItemsInfo(){
        return mapItemsInfo;
    }

    public void setMapItemsInfo(int x,int y,int val){
        this.mapItemsInfo[x][y] = val;
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
