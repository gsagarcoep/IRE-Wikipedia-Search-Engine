
public class WordInQueueNode  implements Comparable<WordInQueueNode> {
		public String word;
		public int fileIndex;
		
		public WordInQueueNode(String str, int ind)
		{
			word = str;
			fileIndex = ind;
		}

		@Override
		public int compareTo(WordInQueueNode arg) {
		
			return this.word.compareTo(arg.word);
		}
		
		
	   
}
