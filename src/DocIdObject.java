
public class DocIdObject {
	String id;
	int score;
	
	public DocIdObject(String temp) {
		// TODO Auto-generated constructor stub
		id=temp;
		String[] tokens=temp.split("-");
		
		StringBuilder frequency=new StringBuilder();
		int multFactor=1;
		score=0;
		char lastChar='\0';
		if(tokens.length <2)
			return;
		for(int i=0;i<tokens[1].length();i++){
			if(tokens[1].charAt(i)>'a' && tokens[1].charAt(i)<'z'){
				lastChar=tokens[1].charAt(i);
				if(frequency!=null && frequency.length()!=0){
					int tempInt=Integer.parseInt(frequency.toString());
					
					switch(tokens[1].charAt(i)){
					case 't':
						multFactor=10;
						break;
					case 'i':
						multFactor=5;
						break;
					case 'b':
						multFactor=1;
						break;
					case 'c':
						multFactor=2;
						break;
					case 'l':
						multFactor=2;
						break;
					case 'r':
						multFactor=1;
						break;
					default:
						multFactor=1;
						break;
					}
					score+=(tempInt*multFactor);
					frequency.setLength(0);
				}
			}
			else if(tokens[1].charAt(i)!=',')	
				frequency.append(tokens[1].charAt(i));
		}
		
		if(frequency!=null){
			int tempInt=Integer.parseInt(frequency.toString());
			
			switch(lastChar){
			case 't':
				multFactor=10;
				break;
			case 'i':
				multFactor=5;
				break;
			case 'b':
				multFactor=1;
				break;
			case 'c':
				multFactor=2;
				break;
			case 'l':
				multFactor=2;
				break;
			case 'r':
				multFactor=1;
				break;
			default:
				multFactor=1;
				break;
			}
			score+=(tempInt*multFactor);
			frequency.setLength(0);
		}
	//System.out.println(temp+"="+score);	
	}
	
	public int getScore(){	return score;}
	public String getId(){ return id;}
	
}
