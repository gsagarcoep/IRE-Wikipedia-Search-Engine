import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class FileOperation {
	static int countWords=0,count=0,countFiles=0;
	static String outputFile=".";
	static TreeMap<String,String> postingList=null;
	static int pageCount=0;
	StringBuilder stringBuffer=new StringBuilder("");

	String[] lines;
	boolean[] readLineBool;
	boolean[] isFileAvailable;
	boolean anyFileRemaining=true;
	BufferedReader[] readers;
	File[] tempFiles;
	PriorityQueue<WordInQueueNode> priorityQ=new PriorityQueue<WordInQueueNode>();
	
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
	//Getting the word from list
	/***************************************************************************************************************/
	
	public static String getDocIdFromLine(String currentLine){
		if(currentLine!=null && !currentLine.isEmpty())
    	{
    		String currentKey="";
    		int i=0;
    		for(int strIndex=0;strIndex< currentLine.length();strIndex++){
    			if(currentLine.charAt(strIndex)==':'){
    				i=strIndex;
    				break;
    			}
    		}
    		currentKey=currentLine.substring(i+1,currentLine.length());
			return currentKey;
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
	//Printing posting to file
	/***************************************************************************************************************/
	public void printTitleToFile(TreeMap<String,String> docIdToNameTree){
		try {
			BufferedWriter outputWriter = new BufferedWriter(new FileWriter(new File("docIdToName")));
			// Get a set of the entries
		    Set set = docIdToNameTree.entrySet();
		    // Get an iterator
		    Iterator i = set.iterator();
		    
		    while(i.hasNext()) {
		    	Map.Entry me = (Map.Entry)i.next();
		    	
				String tempStr=me.getKey()+"#"+me.getValue();
				outputWriter.write(tempStr+"\n");
		
		      }  
		    
		    outputWriter.close();
		
		} catch (Exception e) {}

	}
	/***************************************************************************************************************/
	//Printing posting to file
	/***************************************************************************************************************/
		
	public static void printToFile(TreeMap<String, String> postingList){
		File tempFile=new File(outputFile+"/"+"tempFile");
		File finalFile=new File(outputFile+"/"+"tempPostingList"+(countFiles));
		
		count++;
		//System.out.println(count);
		if(count==10){
			//System.out.println(count);
			count=0;
			countFiles++;
		}
		try{
		
		//If file does not exist
			/********************** Start of If *******************/
			if(!finalFile.exists()){
			
				//System.out.println("File was not present");
				try {
					finalFile.createNewFile();
				
					// Get a set of the entries
				    Set set = postingList.entrySet();
				    // Get an iterator
				    Iterator i = set.iterator();
				    
				    BufferedWriter output = new BufferedWriter(new FileWriter(finalFile));
				    
				    while(i.hasNext()) {
				    	Map.Entry me = (Map.Entry)i.next();
				    	
						String tempStr=me.getKey()+":"+me.getValue();
						output.write(tempStr+"\n");
				//		System.out.println(tempStr);
				      }  
				    
				    output.close();
				} 
				catch (IOException e) {System.out.println("E ka ho gawa aaah");}  
			} // End of if block
			/********************** End of If *******************/
			/********************** Start of else ***************/
			else{
				
				//System.out.println("File is present "+count);
							
				//DriverClass.printTreemap(postingList);	
				try{
					if(tempFile.exists())
						tempFile.delete();
					tempFile.createNewFile();
						
					// Get a set of the entries
				    Set set = postingList.entrySet();
				    // Get an iterator
				    Iterator i = set.iterator();
				    FileWriter wFile=new FileWriter(tempFile);
				    FileReader rFIle=new FileReader(finalFile);
				    BufferedWriter output = new BufferedWriter(wFile);
				    BufferedReader currentFile = new BufferedReader(rFIle);
				    
				    //Merge sort the tree map and postinglist file to temp file, then rename temp file to posting list
				    String currentLine=currentFile.readLine();
				    Map.Entry me = (Map.Entry)i.next();
				    
				    /*++++++++++++++++++++++++++++ Start of while loop +++++++++++++++++++++++++++++*/
				    while(me!=null && currentLine!=null){
				        	
				    	String tempStr=""+me.getKey();
				    	String currentKeyFromFile=getWordKeyFromLine(currentLine);
				    //	System.out.println("imhere");
				    	/*--------------------- Start of If ----------------------*/
				       	if(stringCompare(currentKeyFromFile, tempStr)==-1) {
				    		output.write(currentLine+"\n");
				    	  	currentLine=currentFile.readLine();
				    	}
				       	/*--------------------- end of If ----------------------*/
				       	/*--------------------- Start of else-if ----------------------*/
				    	else if(stringCompare(currentKeyFromFile, tempStr)==1) {
				    		String tempS=me.getKey()+":"+me.getValue();
				    		output.write(tempS+"\n");
							
							if(i.hasNext())	me=(Map.Entry)i.next();
				    		else me=null;
				    	}
				       	/*--------------------- end of else-if ----------------------*/
				       	/*--------------------- Start of else ----------------------*/
				    	else{
				    		//Both keys are same
				    		String tempS=currentLine+me.getValue();
				    		output.write(tempS+"\n");
				    		
				    		if(i.hasNext()) me=(Map.Entry)i.next();
				    		else me=null;
				    		currentLine=currentFile.readLine();
				    	}
				       	/*--------------------- end of else ----------------------*/	
				    }  
				    /*++++++++++++++++++++++++++++ end of while loop +++++++++++++++++++++++++++++*/
				    /*++++++++++++++++++++++++++++ Start of while loop +++++++++++++++++++++++++++++*/
				    while(me!=null){
				    	String tempS=me.getKey()+":"+me.getValue();
				    	output.write(tempS+"\n");
						
				    	if(i.hasNext())	me=(Map.Entry)i.next();
				    	else me=null;
				    }
				    /*++++++++++++++++++++++++++++ end of while loop +++++++++++++++++++++++++++++*/
				    /*++++++++++++++++++++++++++++ Start of while loop +++++++++++++++++++++++++++++*/
				    while(currentLine!=null){
				    	output.write(currentLine+"\n");
				    		    	
			    		currentLine=currentFile.readLine();
				    }
				    /*++++++++++++++++++++++++++++ end of while loop +++++++++++++++++++++++++++++*/
				    output.close();
				    currentFile.close();
				    postingList.clear();
				    
				    finalFile.delete();
				    tempFile.renameTo(finalFile);
				    
				}catch (IOException e) {System.out.println("E ka ho gawa ");} 
			}
			/********************** End of Else *******************/
		}catch(Exception e){}
	}
	
	/***************************************************************************************************************/
	//Remove Similar Lines from other files
	/***************************************************************************************************************/

	public StringBuilder removeSimilarLinesFromOtherFiles(){
		   if(priorityQ.isEmpty())
			   return null;
		  try{ 
		   WordInQueueNode head = priorityQ.remove();
		   int fileIndex = head.fileIndex;
		   long heapSize = Runtime.getRuntime().totalMemory();
         
      		   readLineBool[fileIndex] = true;
		   stringBuffer.setLength(0);
		   if(priorityQ.isEmpty())
			   return new StringBuilder( new String(lines[fileIndex]));
		   
		   String currentTerm=getWordKeyFromLine(lines[fileIndex]);
		   stringBuffer.append(lines[fileIndex]);
		    
		   while(!priorityQ.isEmpty() && stringCompare(priorityQ.peek().word, currentTerm)==0)
		   {
			   head = priorityQ.remove();
			   int newIndex = head.fileIndex;
			   readLineBool[newIndex] = true;
			   
			   if(heapSize> stringBuffer.length()+ (getDocIdFromLine(lines[newIndex])).length())
				   stringBuffer.append(getDocIdFromLine(lines[newIndex]));
			   else{
				   System.out.println("Heap Size = " + heapSize/1000);
				   System.out.println(currentTerm+"  "+ stringBuffer.length()+ (getDocIdFromLine(lines[newIndex])).length());
			   }
		   }
		   return stringBuffer;
		  }catch(Exception e){}
		   return null;
	   }
	/***************************************************************************************************************/
	//Adding new lines to the top of each queue
	/***************************************************************************************************************/
   
	public void AddLines(){
		try{
		   for(int index = 0; index <= countFiles; index++) {
			   if(readLineBool[index] == false || isFileAvailable[index] == false)
				   continue;
				   
				String line = readers[index].readLine();
				   
				if(line == null){
					isFileAvailable[index]=false;
				}
				else{
					lines[index] = line;
					readLineBool[index] = false;
					   
					WordInQueueNode node = new WordInQueueNode(getWordKeyFromLine(lines[index]), index);
					   
					priorityQ.add(node);
				}
		   	}
		   }
		catch(Exception e) {
		//   System.out.print(e.getMessage());
	   }
	}
	/***************************************************************************************************************/
	//Compare Function for treeSet
	/***************************************************************************************************************/
   
	class MyScoreComp implements Comparator<DocIdObject>{
		 
	 
	    public int compare(DocIdObject e1, DocIdObject e2) {
	        if(e1.getScore() > e2.getScore())
	        	return -1;
	        else if(e1.getScore() < e2.getScore())
	        	return 1;
	        else 
	        	return 1;
	    }
	}
	/***************************************************************************************************************/
	//Sort the docid
	/***************************************************************************************************************/
   
	public String sortDocIds(String line) {
		TreeSet<DocIdObject> ts=new TreeSet<DocIdObject>(new MyScoreComp());
		String[] tempArr=getDocIdFromLine(line).split(",");
		int count=0;
		
	
		for(int i=0;i<tempArr.length && i<80000 ;i++){
			ts.add(new DocIdObject(tempArr[i]));
		}
		
		count=0;
		StringBuilder op=new StringBuilder("");
		op.append(getWordKeyFromLine(line)+":");
		
		for(DocIdObject d:ts){
			
			op.append(d.getId()+",");
			if((count++)==30000){
				//System.out.println(getWordKeyFromLine(line));
				break;
			}
		}
		return op.toString();
	}
	/***************************************************************************************************************/
	//Create Secondary file
	/***************************************************************************************************************/
  	public void createSecondaryIndex(){
  		File secondaryIndex=new File(outputFile+"/"+"postingListSparse");
  		File denseIndex=new File(outputFile+"/"+"postingListDense");
  		 System.out.println("I am creating secondayry");

  		try {
			BufferedWriter outputSec = new BufferedWriter(new FileWriter(secondaryIndex));
			RandomAccessFile denIndex = new RandomAccessFile(denseIndex,"r");
  		
			long offset=0;
			
			denIndex.seek(0);
			
			StringBuilder line=new StringBuilder();
			line.append(denIndex.readLine());
			outputSec.write(getWordKeyFromLine(line.toString())+":"+offset+"\n");
			
			while(line!=null && line.length()!=0){
		
				outputSec.write(getWordKeyFromLine(line.toString())+":"+offset+"\n");
				offset=denIndex.getFilePointer();
				line.setLength(0);
				String str=denIndex.readLine();
				if(str==null || str.isEmpty())
					break;
				line.append(str);
			}
			denIndex.close();
			outputSec.close();
  		} catch (Exception e) {}	
  		
  		createTertiaryIndex();
  	}
  	/***************************************************************************************************************/
	//Create Secondary file
	/***************************************************************************************************************/
  	public void createQuarternaryIndex(){
		File TertiaryIndex=new File(outputFile+"/"+"postingListQuarternary");
		File secondaryIndex=new File(outputFile+"/"+"postingListTertiary");
		int wordCount=0;
		 System.out.println("I am creating quarternary");

		try {
			BufferedWriter outputTer = new BufferedWriter(new FileWriter(TertiaryIndex));
			RandomAccessFile secondIndex = new RandomAccessFile(secondaryIndex,"r");
			
			secondIndex.seek(0);
			long offset=0;
			long offsetPrev=0;
			
			String line=secondIndex.readLine();
			outputTer.write(getWordKeyFromLine(line)+":"+offset+"\n");
			String currentWord="";
			
			while(line!=null && !line.isEmpty()){
				currentWord=getWordKeyFromLine(line);
				if(wordCount++==100){
					outputTer.write(currentWord+":"+offset+"\n");
					wordCount=0;
				}
				offsetPrev=offset;
				offset=secondIndex.getFilePointer();
				line =secondIndex.readLine();
				
			}
			
			if(wordCount++==100){
				outputTer.write(currentWord+":"+offsetPrev+"\n");
				wordCount=0;
			}
			
			outputTer.close();
			secondIndex.close();
		} catch (Exception e) {}
		
  	}
  	/***************************************************************************************************************/
	//Create Secondary file
	/***************************************************************************************************************/
  	public void createTertiaryIndex(){
		File TertiaryIndex=new File(outputFile+"/"+"postingListTertiary");
		File secondaryIndex=new File(outputFile+"/"+"postingListSparse");
		int wordCount=0;
		 System.out.println("I am creating tertiary");

		try {
			BufferedWriter outputTer = new BufferedWriter(new FileWriter(TertiaryIndex));
			RandomAccessFile secondIndex = new RandomAccessFile(secondaryIndex,"r");
			
			secondIndex.seek(0);
			long offset=0;
			long offsetPrev=0;
			
			StringBuilder line=new StringBuilder();
			line.append(secondIndex.readLine());
			outputTer.write(getWordKeyFromLine(line.toString())+":"+offset+"\n");
			String currentWord="";
			
			while(line!=null && line.length()!=0){
				currentWord=getWordKeyFromLine(line.toString());
				if(wordCount++==100){
					outputTer.write(currentWord+":"+offset+"\n");
					wordCount=0;
				}
				offsetPrev=offset;
				offset=secondIndex.getFilePointer();
				line.setLength(0);
				String str=secondIndex.readLine();
				if(str==null || str.isEmpty())
					break;
				line.append(str);
			}
			
			if(wordCount++==100){
				outputTer.write(currentWord+":"+offsetPrev+"\n");
				wordCount=0;
			}
			
			outputTer.close();
			secondIndex.close();
		} catch (Exception e) {}
		createQuarternaryIndex();
  	}
  	
	/***************************************************************************************************************/
	//CreatePrimary sorted file
	/***************************************************************************************************************/
  	public void createIndex(){
  		File indexFile=new File(outputFile+"/"+"postingListPrimary");
		File denseIndex=new File(outputFile+"/"+"postingListDense");
  		System.out.println("I am creating index");
		//Create Index files
  		long heapSize = Runtime.getRuntime().totalMemory();
        System.out.println("Heap size "+heapSize);
		try{
			
				BufferedReader input = new BufferedReader(new FileReader(indexFile));
				BufferedWriter outputDense = new BufferedWriter(new FileWriter(denseIndex));
				
				StringBuilder currentLine=new StringBuilder();
				currentLine.append(input.readLine());
				
				while(getWordKeyFromLine(currentLine.toString()).length()>100){
					currentLine.setLength(0);
					currentLine.append(input.readLine());
					continue;
				}
				
				String current=sortDocIds(currentLine.toString());
				outputDense.write(current+"\n");
				
				currentLine.setLength(0);
				currentLine.append(input.readLine());
				
				//System.out.println(sortDocIds(line));
				
				while(currentLine!=null ){
					if(getWordKeyFromLine(currentLine.toString()).length()>100){
						currentLine.setLength(0);
						currentLine.append(input.readLine());
							continue;
					}
					if(getWordKeyFromLine(currentLine.toString()).equalsIgnoreCase("zone"))
							System.out.println("zone");
					String current1=sortDocIds(currentLine.toString());
					outputDense.write(current1+"\n");
					
					currentLine.setLength(0);
					String str=input.readLine();
					if(str==null || str.isEmpty())
						break;
					currentLine.append(str);
				}
				
				outputDense.close();
			//	indexFile.delete();
				input.close();
			
		}catch(Exception e){ 
			 e.printStackTrace();
			System.out.println("what went wrong?");}	
		createSecondaryIndex();
  	}
	/***************************************************************************************************************/
	//Merging Primary files
	/***************************************************************************************************************/
   
	public void mergeFiles(){
		try
		   {
			   File primFile = new File(outputFile+"/"+"postingListPrimary");
			   
			   //Open output primary index file
			   BufferedWriter outputWriter = new BufferedWriter(new FileWriter(primFile));
			   
			   //Open temporary files for reading 
			   readers = new BufferedReader[countFiles+1];
			   
			   lines = new String[countFiles+1];
			   tempFiles=new File[countFiles+1];
			   readLineBool = new boolean[countFiles+1];
			   
			   //Open file readers for each file
			 
			   for(int i =0; i <= countFiles; i++)
			   {
				   tempFiles[i]=new File(outputFile+"/"+"tempPostingList"+i);
				   readers[i] = new BufferedReader(new FileReader(tempFiles[i]));
				   //read first line
				   String temp = readers[i].readLine();
				   lines[i]=temp;
				   String term=getWordKeyFromLine(temp);
				   
				   WordInQueueNode node = new WordInQueueNode(term, i);
				   priorityQ.add(node);
			   }
			   
			   isFileAvailable = new boolean[countFiles+1];
			   for(int k = 0; k <= countFiles; k++){
				   isFileAvailable[k]=true;
			   }
			   			   
			   while(true){
				   StringBuilder line = removeSimilarLinesFromOtherFiles();
				   if(line == null )
					   break;
				  
				   outputWriter.write(line+"\n");
				   line.setLength(0);
				   // Add lines...
				   AddLines();
			   }
			   
			   while(!priorityQ.isEmpty()) {
				   WordInQueueNode head = priorityQ.remove();
				   outputWriter.write(lines[head.fileIndex]+"\n");
			   }
			outputWriter.close();
			   
		   }
			
		   catch(Exception e) {
			   System.out.println("Yeh kaha aa gaye hum");
		   }
			for(int i =0; i <= countFiles; i++)		   {
			   tempFiles[i].delete();
			}	
			createIndex();
		}
/********************* End of Class**********************************/	
}
