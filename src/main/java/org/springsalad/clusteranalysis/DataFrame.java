package org.springsalad.clusteranalysis;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class DataFrame implements Iterable<Object[]>{
    String[] headers;
    int[] columnLength;
    List<Object>[] frame;

    public static void main(String[] args) throws IOException{ //test
        DataFrame df = new DataFrame(new String[]{"Name","Age","Height (m)"});
        df.addRow(new Object[]{"Anna",15, 1.55});
        df.addRow(new Object[]{"Ben",16, 1.65});
        df.addRow(new Object[]{"Catherine",17, 1.75});
        System.out.println(df);
        df.addRow(new Object[]{"Deeee",18, 1.85});
        df.addRow(new Object[]{"Eeeeeeeeeeeee",19, 1.95});
        System.out.println("Added new rows. Now iterating by row");
        for (Object[] row: df){
            System.out.println(Arrays.toString(row));
        }
        System.out.println(df);

        String[] headers = new String[]{"Name","Age","Height (m)"};
        List<Object>[] frame = new List[3];
        frame[0] = Arrays.asList(new String[]{"Anna", "Ben", "Catherine"});
        frame[1] = Arrays.asList(new Integer[]{15,16,17});
        frame[2] = Arrays.asList(new Double[]{1.55,1.65,1.75});
        DataFrame dataFrame = new DataFrame(headers,frame);
        System.out.println(dataFrame);

    }

    //operating by row
    @SuppressWarnings("unchecked")
    public DataFrame(String[] headers){
        this.headers = headers;
        columnLength = new int[headers.length];
        frame = new List[headers.length];
        for (int i = 0; i<headers.length; i++){
            frame[i] = new ArrayList<>();
            columnLength[i] = headers[i].length();
        }
    }

    public void addRow(Object[] row) {
        if (row.length != frame.length){
            throw new IllegalShapeException("DataFrame rows are not of the same length.");
        }
        for (int i = 0; i<row.length; i++){
            if (frame[i].size() > 0 && frame[i].get(0).getClass() != row[i].getClass()){
                throw new TypeMismatchException("Element type mismatch while trying to add a row to dataframe.");
            }
            frame[i].add(row[i]);
            columnLength[i] = Math.max(columnLength[i], row[i].toString().length());
        }
    }

    public Iterator<Object[]> iterator(){
        return new DFIterator();
    }

    public class DFIterator implements Iterator<Object[]>{
        int rowLastRead;
        public DFIterator(){
            rowLastRead = -1;
        }
        public boolean hasNext(){
            return rowLastRead < frame[0].size() -1;
        }
        public Object[] next(){
            if (rowLastRead == frame[0].size()-1){
                throw new NoSuchElementException("End of dataframe");
            }
            rowLastRead++;
            Object[] ret = new Object[frame.length];
            for (int col = 0; col < frame.length; col++){
                ret[col] = frame[col].get(rowLastRead);
            }
            return ret;
        }
    }

    //operating by column
    public DataFrame(String[] headers, List<Object>... frame){
        this.headers = headers;
        if (frame.length != headers.length){
            throw new IllegalShapeException("DataFrame headers and columns have incompatible dimensions.");
        }
        for (int col = 1; col<frame.length; col++){
            if (frame[col].size() != frame[col-1].size()){
                throw new IllegalShapeException("DataFrame columns not of the same length.");
            }
        }
        this.frame = frame;
        columnLength = new int[frame.length];
        for (int col = 0; col<headers.length; col++){
            columnLength[col] = Math.max(headers[col].length(),
                    frame[col].stream().map(a -> a.toString().length()).reduce(0,Math::max));
        }
    }

    public List<Object> getSeries(int col){
        return frame[col];

    }

    public List<Object> getSeries(String header){
        for (int col = 0; col < headers.length; col++){
            if (headers[col].equals(header)){
                return frame[col];
            }
        }
        throw new IllegalArgumentException("Invalid header");
    }


    public Object getElement(int col, int row){
        return frame[col].get(row);
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder("DataFrame{\n");
        ret.append("\t\t");
        for (int i = 0; i < headers.length; i++){
            ret.append(String.format("%" + columnLength[i] + "s", headers[i]));
            if (i < headers.length -1){
                ret.append("\t");
            }
        }
        ret.append("\n");

        StringBuilder rowSB;
        for (int row = 0; row < frame[0].size(); row++){
            rowSB = new StringBuilder("\t");
            rowSB.append(row).append("\t");

            List<Object> series;
            for(int col = 0; col < frame.length; col++){
                series = frame[col];
                rowSB.append(String.format("%"+columnLength[col]+"s",series.get(row)));
                if (col < frame.length -1){
                    rowSB.append("\t");
                }
            }
            rowSB.append("\n");
            ret.append(rowSB);
        }
        ret.append("}");
        return ret.toString();
    }

    public static class IllegalShapeException extends RuntimeException{
		private static final long serialVersionUID = -2207485930383146933L;
		public IllegalShapeException() {
    		super();
    	}
		public IllegalShapeException(String message) {
		    super(message);
		}
		public IllegalShapeException(String message, Throwable cause) {
			super(message, cause);
		}
		public IllegalShapeException(Throwable cause) {
			super(cause);
		}
    }
    
    public static class TypeMismatchException extends RuntimeException{
		private static final long serialVersionUID = -2130421850009537977L;
		public TypeMismatchException() {
    		super();
    	}
		public TypeMismatchException(String message) {
		    super(message);
		}
		public TypeMismatchException(String message, Throwable cause) {
			super(message, cause);
		}
		public TypeMismatchException(Throwable cause) {
			super(cause);
		}
    }

}

