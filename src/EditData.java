import java.util.*;


public class EditData {
	
	//Class members: Data
	//TreeMap<String,TreeSet<Integer>>  postingListForDoc = new TreeMap<String,TreeSet<Integer>>();
	
	static TreeMap<String,String> postingListForDoc = new TreeMap<String,String>();
	static TreeMap<String,WordObject> wordArray = new TreeMap<String,WordObject>();
	static HashMap<String,Boolean> stopWordsHashed = new HashMap<String,Boolean>();
	static boolean stopWord=false;
	static int pageCount=0;
	
	/***************************************************************************************************************/
	// Default Constructor
	/***************************************************************************************************************/
	EditData(){
	/*	multifieldHashing.put("title", 0);
		multifieldHashing.put("infobox", 1);
		multifieldHashing.put("category", 2);
		multifieldHashing.put("link", 3);
		multifieldHashing.put("reference", 4);
		multifieldHashing.put("body", 5);
	*/	
		String[] stopWords={"a","able","about","across","after","all","almost","also","am","among","an","and","any","are","as","at","be","because","been",
				"but","by","can","cannot","could","dear","did","do","does","either","else","ever","every","for","from","get","got","gt","had","has","have","he",
				"her","hers","him","his","how","however","i","if","in","into","is","it","its","just","least","let","like","likely","lt","may","me","might","most",
				"must","my","nbsp","neither","no","nor","not","of","off","often","on","only","or","other","our","own","rather","ref","said","say","says","she","should",
				"since","so","some","than","that","the","their","them","then","there","these","they","this","tis","to","too","twas","url","us","wants","was","we",
				"were","what","when","where","which","while","who","whom","why","will","with","would","yet","you","your"};
		
		int length=stopWords.length;
		
		for(int i=0;i<length;i++){
			stopWordsHashed.put(stopWords[i], true);
		
		}
	}
	/***************************************************************************************************************/
	// Method for stop word removal
	
	/***************************************************************************************************************/
	static String stopWordRemoval(String currentWord){
		
		if(!stopWordsHashed.isEmpty() && stopWordsHashed.containsKey(currentWord)){	
			stopWord=true;
			return "";
		}
		currentWord=Stemmer.mainStemer(currentWord);
		//System.out.println(currentWord);
		stopWord=false;
		
		return currentWord;
		
	}
	
	/***************************************************************************************************************/
	//Creating the posting list
	/***************************************************************************************************************/
	void createList(String textInputData,int pageId){
		
		//System.out.println(pageId);
		int length=textInputData.length();
		pageCount++;
		int currentIndex=0;
		//boolean bracketTag=false;
		textInputData=textInputData.toLowerCase();
		String currentWord="";
		boolean titleFlag=true;
		boolean categoryFlag=false;
		boolean infoboxFlag=false;
		boolean referenceFlag=false;
		boolean bodyFlag=false;
		boolean linkFlag=false;
		int curlyBraces=0;
	
		
		try{
			// Parsing text data character by character
			for(;currentIndex <length;currentIndex++){
				
				char currentChar=textInputData.charAt(currentIndex);
				
				//If an alphabet
				if(currentChar>='a' && currentChar<='z')
					currentWord=currentWord+currentChar;
				
				//If not alphabet
				if(currentChar<'a' || currentChar>'z' || currentIndex==length-1){
				
					if( currentWord.equalsIgnoreCase(new String("infobox"))){
						//System.out.println(currentWord);
						currentWord="";
						infoboxFlag=true;
						bodyFlag=false;
						titleFlag=false;
						continue;
					}
					if( currentWord.equalsIgnoreCase(new String("references"))&& textInputData.charAt(currentIndex)=='='){
					//	System.out.println(currentWord);
						currentWord="";
						referenceFlag=true;
						bodyFlag=false;
						if(infoboxFlag) infoboxFlag=false;
						if(linkFlag) linkFlag=false;
						if(categoryFlag) categoryFlag=false;
						continue;
					}
					if( currentWord.equalsIgnoreCase(new String("External")) && textInputData.substring(currentIndex+1,currentIndex+6).equalsIgnoreCase(new String("links"))){
					//	System.out.println(currentWord);
						currentWord="";
						linkFlag=true;
						bodyFlag=false;
						infoboxFlag=false;
						referenceFlag=false;
						categoryFlag=false;
						continue;
					}
					
					if( currentWord.equalsIgnoreCase(new String("category")) && textInputData.charAt(currentIndex)==':'){
						//System.out.println(currentWord+textInputData.charAt(currentIndex));
						currentWord="";
						categoryFlag=true;
						infoboxFlag=false;
						referenceFlag=false;
						linkFlag=false;
						bodyFlag=false;
						continue;
					}
					
					// Removing &lt, gt , quot;
					if(textInputData.charAt(currentIndex)=='&'){
						if( (textInputData.charAt(currentIndex+1)=='l' || textInputData.charAt(currentIndex+1)=='g'  )
								&& textInputData.charAt(currentIndex+2)=='t' && textInputData.charAt(currentIndex+3)==';'){
							
							currentIndex+=3; }
						else if((textInputData.charAt(currentIndex+1)=='q' && textInputData.charAt(currentIndex+2)=='u' 
								&& textInputData.charAt(currentIndex+3)=='o')&& textInputData.charAt(currentIndex+4)=='t' 
								&& textInputData.charAt(currentIndex+5)==';'){
							
							currentIndex+=5;}
						
					}

					if(textInputData.charAt(currentIndex)==']' || textInputData.charAt(currentIndex+1)==']'){
						categoryFlag=false;
						infoboxFlag=false;
						referenceFlag=false;
						linkFlag=false;
						bodyFlag=true;;
					}
					
					if(textInputData.charAt(currentIndex)=='=' && textInputData.charAt(currentIndex+1)=='='){
						infoboxFlag=false;
						referenceFlag=false;
						linkFlag=false;
						categoryFlag=false;
						bodyFlag=true;;
					}
						
					if(textInputData.charAt(currentIndex)=='{'){
						curlyBraces++;
					}
					else if(textInputData.charAt(currentIndex)=='}'){
						curlyBraces--;
						if(curlyBraces==0 && infoboxFlag)
						{	
							infoboxFlag=false;
							bodyFlag=true;
						}
					}
					
					if(currentWord.equalsIgnoreCase(new String("ref"))){
						currentWord="";
						continue;
					}
					
					currentWord=stopWordRemoval(currentWord);
					
					if(currentWord==null || currentWord.isEmpty()){
						currentWord="";
						continue;
					}
			
					
					if(!wordArray.containsKey(currentWord))
					{
						WordObject currentWordObject=new WordObject(currentWord);
						
						ArrayList<Integer> docIdToFreq=new ArrayList<Integer>();
						
						for(int i=0;i<6;i++)
							docIdToFreq.add(i, 0);
						
						if(titleFlag)
							docIdToFreq.set(0, 1);
						if(categoryFlag)
							docIdToFreq.set(2, 1);
						if(infoboxFlag)
							docIdToFreq.set(1, 1);
						if(referenceFlag)
							docIdToFreq.set(4, 1);;
						if(bodyFlag)
							docIdToFreq.set(5, 1);;
						if(linkFlag)
							docIdToFreq.set(3, 1);
				
						currentWordObject.docIdToFrequency.put(pageId, docIdToFreq);
						wordArray.put(currentWord, currentWordObject);
					
						currentWord="";
					}
					else{
						
						int index=5;
						if(titleFlag)
							index=0;
						if(categoryFlag)
							index=2;
						if(infoboxFlag)
							index=1;
						if(referenceFlag)
							index=4;
						if(bodyFlag)
							index=5;
						if(linkFlag)
							index=3;
						
						WordObject currentWordObject=wordArray.get(currentWord);
						int freq=0;
						
						if(currentWordObject.docIdToFrequency.containsKey(pageId)){
							freq=currentWordObject.docIdToFrequency.get(pageId).get(index);
							currentWordObject.docIdToFrequency.get(pageId).set(index, freq+1);
						}
						else{
							ArrayList<Integer> docIdToFreq=new ArrayList<Integer>();
							
							for(int i=0;i<6;i++)
								docIdToFreq.add(i, 0);
							
							if(titleFlag)
								docIdToFreq.set(0, 1);
							if(categoryFlag)
								docIdToFreq.set(2, 1);
							if(infoboxFlag)
								docIdToFreq.set(1, 1);
							if(referenceFlag)
								docIdToFreq.set(4, 1);
							if(bodyFlag)
								docIdToFreq.set(5, 1);
							if(linkFlag)
								docIdToFreq.set(3, 1);
					
							currentWordObject.docIdToFrequency.put(pageId, docIdToFreq);
							//currentWordObject.docIdToFrequency.get(pageId).set(index, freq+1);
						}
						currentWord="";
					}
				}
			}
	}
	catch(Exception e){}
	//	printAllData();
	}
	
	/********************************************End of Method********************************************************/

	TreeMap<String,String> getHashMapForPostingListOfDoc(){
			return postingListForDoc;
	}
	/*****************************Print all words*************************************************/
	void printAllData(){
		for (Map.Entry entry : wordArray.entrySet()) {
		    System.out.print("key : ");
		    System.out.println(entry.getKey());
		    WordObject temp=(WordObject) entry.getValue();
		    for (Map.Entry ent : temp.docIdToFrequency.entrySet()) {
		    	  System.out.println("DocId: "+ent.getKey());
		    
		    	  for(int i=0;i<6;i++)
		    	    System.out.println(i+" : "+ ((ArrayList<Integer>)ent.getValue()).get(i));
		    	  }   
		}
	}
	
	/*****************************Print all words*************************************************/
	void convertToString(){
		
		for (Map.Entry entry : wordArray.entrySet()) {
		    
			WordObject temp=(WordObject) entry.getValue();
		    String currentDocId="";
		    
		    for (Map.Entry ent : temp.docIdToFrequency.entrySet()) {
		    	//System.out.println("DocId: "+ent.getKey());
		    	currentDocId+=ent.getKey()+"-";
		    	for(int i=0;i<6;i++){
		    		
		    		int fr=((ArrayList<Integer>)ent.getValue()).get(i);
		    
		    		if(i==0 && fr!=0) currentDocId+="t"+fr;
		    		if(i==1 && fr!=0) currentDocId+="i"+fr;
		    		if(i==2 && fr!=0) currentDocId+="c"+fr;
		    		if(i==3 && fr!=0) currentDocId+="l"+fr;
		    		if(i==4 && fr!=0) currentDocId+="r"+fr;
		    		if(i==5 && fr!=0) currentDocId+="b"+fr;
		    	}
		    	currentDocId+=",";
		    //	System.out.println+currentDocId);
		    }

			if(!postingListForDoc.containsKey(temp.wordString))
			{
			/*	if(temp.wordString.equalsIgnoreCase(new String("aaahh")))
			    	System.out.println(currentDocId);
			*/	
				postingListForDoc.put(temp.wordString,currentDocId);
			//	System.out.println(temp.wordString+" + "+currentDocId);
			}
			else{
				String n=postingListForDoc.get(temp.wordString);
				n+=currentDocId;
				
				/*if(temp.wordString.equalsIgnoreCase(new String("aaahh")))
			    	System.out.println(n);
				*/
				postingListForDoc.put(temp.wordString, n);
			//	System.out.println(temp.wordString+"+"+n);
			}
		}
	}
}
/************************************End of Class****************************************************************/
