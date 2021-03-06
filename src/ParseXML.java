import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TreeMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
 
public class ParseXML {
	
	static int countWords=0,count=0;
	static String outputFile="";
	static TreeMap<String,String> postingList=null;
	
	
	/***************************************************************************************************************/
	//Getting the word from list
	/***************************************************************************************************************/
	
	public static String getWordKeyFromLine(String currentLine){
		if(currentLine!=null && !currentLine.isEmpty())
    	{
    		String currentKey="";
    		for(int strIndex=0;strIndex< currentLine.length();strIndex++){
    			if(currentLine.charAt(strIndex)==':')
    			{
    				currentKey=currentLine.substring(0, strIndex);
    				return currentKey;
    			}
    		}
    	}
		return null;
	}
	
	/***************************************************************************************************************/
	//Comparing strings
	/***************************************************************************************************************/
	public static int stringCompare(String str1,String str2){
		
		if(str1==null || str1.isEmpty())
			return -1;
		
		//Str1 is greater
		if(str1.compareToIgnoreCase(str2) > 0)
			return 1;
		//Str2 is greater
		else if(str1.compareToIgnoreCase(str2)<0)
			return -1;
		//both are equal	
		else
			return 0;
	}

	/***************************************************************************************************************/
	//SAX Parser
	/***************************************************************************************************************/
	
	public static void parse(String fileXMLName,String outputF){
		
	try {
		 
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = factory.newSAXParser();
		outputFile=outputF;
		FileOperation fileIO=new FileOperation();
		/*System.out.println("I am in");
		fileIO.outputFile=outputFile;
		fileIO.createIndex();
		System.out.println("I am done");
		*/
		// Overridden default handler for SAX parser
		DefaultHandler handler = new DefaultHandler() {
		
			long timeL=System.currentTimeMillis();
			EditData editData=new EditData();
			FileOperation fileIO=new FileOperation();
			StringBuilder stringInput=new StringBuilder("");
			StringBuilder titleString=new StringBuilder("");
			TreeMap<String,String> docIdToNameTree=new TreeMap<String,String>();
			
			int pageId=0;
			int count=0;
			boolean tem=true;
			boolean textTag = false,titleTag=false;
			boolean pageTag = false;
			boolean pageIdTag = false;
			
			/*************************************************************/
			// Start Element of SAX parser
			/*************************************************************/
			public void startElement(String uri, String localName,String qName, 
		                Attributes attributes) throws SAXException {
			
				if (qName.equalsIgnoreCase("page")) {
					pageTag = true;
				}
				else if (qName.equalsIgnoreCase("text")) {
					if(textTag){
					//	editData.createList(stringInput.toString(), pageId);
						stringInput.setLength(0);
						pageId=0;
					}
					textTag = true;
					
				}
				else if (qName.equalsIgnoreCase("title")) {
					titleString.setLength(0);
					titleTag = true;
					
				}
				else if (qName.equalsIgnoreCase("id") && pageTag) {
					pageIdTag=true;
					pageTag = false;
	
				}
		 				 
			}
			/*************************************************************/
			// End Element of SAX parser
			/*************************************************************/
			public void endElement(String uri, String localName,
				String qName) throws SAXException {
			
				if (qName.equalsIgnoreCase("text")) {
					textTag = false;
					editData.createList(stringInput.toString(), pageId);
					stringInput.setLength(0);
					pageId=0;
				}
				if (qName.equalsIgnoreCase("page")) {
					
						if(count++==2000){
							editData.convertToString();
							fileIO.pageCount+=count;
							fileIO.outputFile=ParseXML.outputFile;
							fileIO.printToFile(editData.getHashMapForPostingListOfDoc());
							editData.wordArray.clear();
							editData.postingListForDoc.clear();
							
							count=0;
						}
				}
			}
			/*************************************************************/
			// End Document of SAX parser
			/*************************************************************/
			public void endDocument(){
				
				if(count>0){
					//System.out.println("Time taken is "+tim+" seconds");
					editData.convertToString();
					fileIO.pageCount+=count;
					fileIO.outputFile=ParseXML.outputFile;
					fileIO.printToFile(editData.getHashMapForPostingListOfDoc());
					editData.wordArray.clear();
					editData.postingListForDoc.clear();
					fileIO.printTitleToFile(docIdToNameTree);
					count=0;
				}
				//System.out.println("Page Count= "+editData.pageCount);
				fileIO.mergeFiles();
			//	fileIO.createIndex();
			}
			/*************************************************************/
			// Character of SAX parser
			/*************************************************************/
			public void characters(char ch[], int start, int length) throws SAXException {
		 		
				if(titleTag){
					titleString.append(new String(ch, start, length));
					stringInput.append(" "+titleString+" ");
					titleTag=false;
				}
				if (textTag) {
						if(ch!=null)
							stringInput.append(new String(ch, start, length));
					}
				if (pageIdTag) {
					
					pageId=Integer.parseInt(new String(ch, start, length));
					try {
						
						docIdToNameTree.put(new String(ch, start, length), titleString.toString());
						titleString.setLength(0);
					} catch (Exception e) {}
					
					pageIdTag=false;
					}
				}
		     };
		 
		       saxParser.parse(fileXMLName, handler);
		 
		 } 
		catch (Exception e) {
		      
		     }
		 
	   }
}
