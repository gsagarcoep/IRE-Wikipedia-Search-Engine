import java.util.ArrayList;
import java.util.TreeMap;


public class WordObject {
	String wordString;
	TreeMap<Integer,ArrayList<Integer>>  docIdToFrequency = new TreeMap<Integer,ArrayList<Integer>>();
	
	public WordObject(String wordStrin) {
		// TODO Auto-generated constructor stub
		wordString=wordStrin;
	}
}
