public class DriverClass {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		double tim=System.currentTimeMillis();
		long heapSize = Runtime.getRuntime().totalMemory();
         
       		//Print the jvm heap size.
        	//System.out.println("Heap Size = " + heapSize/1000);
	
		if(args[0].equalsIgnoreCase("indexGanga")){
			ParseXML.parse(args[1],args[2]);
		}
		else{
			Search search=new Search(args[1]);
			
			if(!args[0].equalsIgnoreCase("indexGanga"))
				search.parseQueryAndSearch();
		}
		double time=(System.currentTimeMillis()-tim)/1000;	
		System.out.println(time+"seconds");
	}

}
