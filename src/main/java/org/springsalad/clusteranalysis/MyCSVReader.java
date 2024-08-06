package org.springsalad.clusteranalysis;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class MyCSVReader{
	private BufferedReader hiddenBR;

	private static final int NEWLINE = 0;
	private static final int UNDECIDED = 1;
	private static final int NORMAL = 2;
	private static final int QUOTED = 3;
	
	private static final char DOUBLEQUOTE = '"';
	private static final char COMMA = ',';
	private static final char SPACE = ' ';
	private static final char TAB   = '\t';
	
	
	public static void main(String[] args) throws IOException{
		//String filePathStr = "C:\\Users\\imt_w\\Documents\\SpringSalad\\Clustering_tutorial_01\\Clustering_tutorial_01_SIMULATIONS\\Simulation3_SIM_FOLDER\\data\\Cluster_stat\\Histograms\\Size_Freq_Fotm\\MEAN_Run\\MEAN_RUN_0.0200_Size_Freq_Fotm.csv";
		String filePathStr = "C:\\Users\\imt_w\\Downloads\\test.csv";
		MyCSVReader myCSVReader = new MyCSVReader(new FileReader(filePathStr));
		while (true) {
			List<String> line = myCSVReader.readLine();
			if (line == null) {
				System.out.print("<EOF>");
				break;
			}
			for (String str: line) {
				System.out.print("<FIELD>");
				if (str.isEmpty()) {
					System.out.print("BLANK");
				}
				else {
					System.out.print(str);
				}
			}
			System.out.print("<EOL>\n");
		}
	}
	
	
    public MyCSVReader(Reader in){
        hiddenBR = new BufferedReader(in);
    }
    
    public void close() throws IOException{
    	hiddenBR.close();
    }
    
    public List<String> readLine() throws IOException {
    	int state = NEWLINE;
    	String line = null;
    	int lineLen = -1;
    	int currentIndex = -1, start = -1, end;
    	char currentChar;
    	List<String> list = null;
    	StringBuilder quotedFieldSB = null;
    	while (true) {
    		switch (state) {
    			case NEWLINE:
    				line = hiddenBR.readLine();
    				if (line == null) {
    					return null;
    				}
    				line = line.trim();
    				if (line.isEmpty()) { //state is still NEWLINE
    					continue;
    				}
    				lineLen = line.length();
    				list = new ArrayList<>();
    				currentIndex = 0;
    				currentChar = line.charAt(currentIndex);
    				switch (currentChar) {
    					case DOUBLEQUOTE:
    						start = 1;
    						quotedFieldSB = new StringBuilder();
    						state = QUOTED;
    						break;
    					case COMMA:
    						list.add("");
    						state = UNDECIDED;
    						break;
    					default:
    						start = 0; 
    						state = NORMAL;
    				}
    				break;
    			case UNDECIDED:
    				if (currentIndex == lineLen -1) {
    					list.add("");
    					return list;
    				}
    				currentChar = line.charAt(++currentIndex);
    				switch (currentChar) {
	    				case SPACE:
	    				case TAB:
	    					break; // still UNDECIDED
	    				case DOUBLEQUOTE:
	    					start = currentIndex +1;
	    					quotedFieldSB = new StringBuilder();
	    					state = QUOTED;
	    					break;
	    				case COMMA:
	    					list.add("");
	    					break; // still UNDECIDED
	    				default:
	    					start = currentIndex;
	    					state = NORMAL;
    				}
    				break;
    			case NORMAL:
    				if (currentIndex == lineLen-1) {
    					list.add(line.substring(start));
    					return list;
    				}
    				currentChar = line.charAt(++currentIndex);
    				if (currentChar == COMMA) {
    					end = currentIndex;
    					list.add(line.substring(start,end));
    					state = UNDECIDED;
    				}
    				// otherwise, still NORMAL
    				break;
    			case QUOTED:
    				if (currentIndex == lineLen-1) { // even if len ==0
    					quotedFieldSB.append(line.substring(start)).append("\n");
    					line = hiddenBR.readLine();
        				if (line == null) {
        					list.add(quotedFieldSB.toString());
        					return list;
        				}
        				line = line.trim();
        				lineLen = line.length();
        				currentIndex = -1;
        				start = 0;
        				if (line.isEmpty()) { //state is still QUOTED
        					continue;
        				}
    				}
    				currentChar = line.charAt(++currentIndex);
    				if (currentChar == DOUBLEQUOTE) {
    					end = currentIndex;
    					quotedFieldSB.append(line.substring(start, end));
    					list.add(quotedFieldSB.toString());
    					while (true) {
    						if (currentIndex == lineLen -1) {
    							return list;
    						}
    						currentChar = line.charAt(++currentIndex);
    						if (currentChar == COMMA) {
    							state = UNDECIDED;
    							break;
    						} // else continue as QUOTED
    					}
    				}
    				break;
    		}
    	}
    }
}
