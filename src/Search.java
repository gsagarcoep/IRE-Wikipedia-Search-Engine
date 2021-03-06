import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;


public class Search {
	
	String outputF="";
	/***************************************************************************/
	// Default Constructor
	/***************************************************************************/
	
	Search(){}
	Search(String outputFolder){
		outputF=outputFolder;
	}
	/***************************************************************************/
	// Separate offset from string
	/***************************************************************************/
	long getOffset(String currentLine){
		if(currentLine==null || currentLine.isEmpty())
			return 0;
		
		String str="";
		int len=currentLine.length();
		
		for(int i=0;i<len;i++){
			if(currentLine.charAt(i)==':')
				str=currentLine.substring(i+1,len);
		}
		long offset=Long.parseLong(str);
		return offset;
	}
	
	/***************************************************************************/
	// Search the title
	/***************************************************************************/
    	public void printTitle(Set<Integer> tempSet,boolean wikiflag,boolean titleflag) {
	    	File denseIndex=new File(outputF+"/"+"docIdToName");
		
		try{
			RandomAccessFile titleFile;
			titleFile=new RandomAccessFile(denseIndex,"r");
		    for( int docId:tempSet)
		    {
		       searchTitle(docId, titleFile, 0, titleFile.length(),wikiflag,titleflag);
		    }
		    titleFile.close();
		}
		catch(Exception e){
		    e.printStackTrace();
		}

    	}

    /***************************************************************************/
	// Binary search for file
	/***************************************************************************/
    public void searchTitle(int docId,RandomAccessFile raf,long start,long end,boolean wikiflag, boolean titleflag) {

    	//System.out.println(docId);
        
    	long mid;
        String res;
          try {
            while(start<=end) {
                mid=(start+end)/2;
                raf.seek(mid);
                raf.readLine();
                if((res=raf.readLine())!=null){
                    String[] tokens=res.split("#");
                    //System.out.println( tokens[0]);
                    
                    int comparison=Integer.parseInt(tokens[0]);
                    if(comparison==docId) {
                    	 if(!wikiflag && titleflag && tokens[1].contains("wiki"))
                    	 	break;
                    	 System.out.println( docId+" "+tokens[1]);
                         break;
                    }
                    else if(comparison<docId){
                        start=mid+1;
                    }
                    else {
                        end=mid-1;
                    }
                }
                else{
                    end=mid-1;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();  
        }
    }
	
	/***************************************************************************/
	// Take top 10
	/***************************************************************************/
	public Set<Integer> getTopTen(String line,String Field,boolean wikiFlag){
		
		line=FileOperation.getDocIdFromLine(line);
		Set<Integer> vec=new HashSet<Integer>();
		int count=0;
		boolean flag=true;
		
		if(line ==null || line.isEmpty())
			return vec;
		
		if(Field.equalsIgnoreCase("-"))
			flag=false;
			
		String curr="";
		for(int i=0;i<line.length();i++){
			
			if(count==30)
				return vec;
			
			while(line.charAt(i)!='-' )	{	
				curr+=line.charAt(i);
				i++;
			}
			
			if(!flag){
				count++;
				vec.add(Integer.parseInt(curr));
				curr="";
				while(line.charAt(i)!=',') i++;
			}
			else if(flag){
				while(line.charAt(i)!=Field.toLowerCase().charAt(0) && line.charAt(i)!=',' ) i++;
				
				if(line.charAt(i)==',') { curr="";continue;}
				
				vec.add(Integer.parseInt(curr));
				curr="";
				count++;
				while(line.charAt(i)!=',') i++;
			}
		}
		return vec;
	}
	
	/***************************************************************************/
	// Search the word
	/***************************************************************************/
	public void parseQueryAndSearch(){
		Scanner scan= new Scanner(System.in);			
		
		String num=scan.nextLine();
		int numberOfQueries=Integer.parseInt(num);
		
		
	OUTER:	for(int currQuery=0;currQuery<numberOfQueries;currQuery++){
				String searchQuery=scan.nextLine();
				searchQuery.toLowerCase();
				double tim=System.currentTimeMillis();
				boolean wikiFlag=false;
				
				if(searchQuery.contains("wiki"))
					wikiFlag=true;
				
				String[] currentTokens=searchQuery.split(" ");
				Map<String,String> searchWordField=new HashMap<String,String>();
				String resultForOneTerm="";
				
				Set<Integer> result=new HashSet<Integer>();
				Set<Integer> res=new HashSet<Integer>();
					
		INNER:	for(int j=0;j<currentTokens.length;j++){
					int index=currentTokens[j].indexOf(':');
						
					if(index>0 && index<currentTokens[j].length()-1){
						String searchWord=EditData.stopWordRemoval(currentTokens[j].substring(index+1, currentTokens[j].length()).toLowerCase());
						searchWordField.put(searchWord,currentTokens[j].substring(0, index));
					}
					else
						searchWordField.put(EditData.stopWordRemoval(currentTokens[j].toLowerCase()),"-");
				}
			/*	
			// Get a set of the entries
			Set set = searchWordField.entrySet();
			// Get an iterator
			Iterator i = set.iterator();
			*/
				boolean titleflag=false;
				String wholeTr="";
			
			/*while(i.hasNext()) {
				Map.Entry me = (Map.Entry)i.next();
			    	String tem1=(String)me.getKey();
			    	String tem2=(String)me.getValue();
			  */
			if(currentTokens.length>1){
				for(int i=0;i<currentTokens.length;i++){
					int index=currentTokens[i].indexOf(':');
					String tem1="";
					if(index>0 && index<currentTokens[i].length()-1){
						tem1=EditData.stopWordRemoval(currentTokens[i].substring(index+1, currentTokens[i].length()).toLowerCase());
					}
					else
						tem1=EditData.stopWordRemoval(currentTokens[i]);
					
					wholeTr+=tem1;
				    	
				    if(searchWordField.get(tem1).charAt(0)=='t')
				    		titleflag=true;	
				
				    	resultForOneTerm=searchMain(tem1);
				    	res.addAll(getTopTen(resultForOneTerm,searchWordField.get(tem1),wikiFlag));
				    	if(result.isEmpty())
				    		result.addAll(res);
				    	else{
				    		result.retainAll(res);
				    	res.removeAll(result);}
					}  
				
					resultForOneTerm=searchMain(wholeTr);
					res.addAll(getTopTen(resultForOneTerm,"-",wikiFlag));
					
					if(result.isEmpty())
						result.addAll(res);
					else{
						result.retainAll(res);
						res.removeAll(result);}
				  
					printTitle(result,wikiFlag,titleflag);
					printTitle(res,wikiFlag,titleflag);
					
			}
		
			else if(currentTokens.length==1){
				int index=currentTokens[0].indexOf(':');
				String tem1="";
				if(index>0 && index<currentTokens[0].length()-1){
					tem1=EditData.stopWordRemoval(currentTokens[0].substring(index+1, currentTokens[0].length()).toLowerCase());
				}
				else
					tem1=EditData.stopWordRemoval(currentTokens[0]);
				
				if(searchWordField.get(tem1).charAt(0)=='t')
		    		titleflag=true;	
		
		    	resultForOneTerm=searchMain(tem1);
		    	res.addAll(getTopTen(resultForOneTerm,searchWordField.get(tem1),wikiFlag));
		    	printTitle(res,wikiFlag,titleflag);
			}
			double time=(System.currentTimeMillis()-tim)/1000;	
			System.out.println(time+" seconds");
		}
	}
	/***************************************************************************/
	// Search the word
	/***************************************************************************/
	public String searchMain(String searchWord){
		File denseIndex=new File(outputF+"/"+"postingListDense");
  		File secondaryIndex=new File(outputF+"/"+"postingListSparse");
  		File TertiaryIndex=new File(outputF+"/"+"postingListTertiary");
		
  		if(searchWord==null || searchWord.isEmpty())
  			return "";
		
		try {
			RandomAccessFile primIndex = new RandomAccessFile(denseIndex,"r");
			RandomAccessFile secondIndex = new RandomAccessFile(secondaryIndex,"r");
			BufferedReader terIndex = new BufferedReader(new FileReader(TertiaryIndex));
			{
					String prevLine=terIndex.readLine();
					String nextLine=terIndex.readLine();
					/********************** Search in index files **************************/
			INNER:	while(nextLine!=null && prevLine!=null){
					String prev=ParseXML.getWordKeyFromLine(prevLine);
					String next=ParseXML.getWordKeyFromLine(nextLine);				
					int isGreaterThanPrev=ParseXML.stringCompare(searchWord,prev);
					int isLessThanNext=ParseXML.stringCompare(searchWord,next);
						
					if(isGreaterThanPrev<0) {
						//Not found 
						return "";
					}
					else if(isGreaterThanPrev==0){
						long offset = getOffset(prevLine);
						
						// print To Console
						secondIndex.seek(offset);
						primIndex.seek(getOffset(secondIndex.readLine()));
						String outputIds=primIndex.readLine();
						return(outputIds);
					}
					else if(isLessThanNext==0){
						long offset = getOffset(nextLine);
						
						//print To Console
						secondIndex.seek(offset);
						primIndex.seek(getOffset(secondIndex.readLine()));
						String outputIds=primIndex.readLine();
						return(outputIds);
					
					}
					else if(isGreaterThanPrev==1 && isLessThanNext==-1){
						long offsetPrev = getOffset(prevLine);
						long offsetNext = getOffset(nextLine);
							
						//Found the block, go for primary index file
						secondIndex.seek(offsetPrev);
						String outputStr=secondIndex.readLine();
							
						while(secondIndex.getFilePointer()<=offsetNext){
							
							if(outputStr.startsWith(searchWord)){
								primIndex.seek(getOffset(outputStr));
								String outputIds=primIndex.readLine();
								return outputIds;
							//	break INNER;
							}
							else outputStr=secondIndex.readLine();
						}
							
						break INNER;
						
						}
						else if(isLessThanNext==1){
							prevLine=nextLine;
							nextLine=terIndex.readLine();
					}
				}
			}
		terIndex.close();
		primIndex.close();
		secondIndex.close();
		}catch (Exception e) {}
		
		return "";		
		
	}
	
}
