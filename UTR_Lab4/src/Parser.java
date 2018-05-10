import java.util.Scanner;

/**
 * 
 * @author Viran
 *
 */
public class Parser {
	
	private static String word=null;
	private static int slider=0;
	private static boolean passTest=false;
	private static boolean reachedEnd=false;
	
	public static void main(String[] args) {
		Scanner input= new Scanner(System.in);
		word=input.nextLine().concat("$");
		
		passTest=S(word.charAt(slider));
		
		if( passTest && reachedEnd ){
				System.out.println("\nDA");
		}
		else
			System.out.println("\nNE");
		input.close();
	}
	
	/**
	 * Parser production for the unfinished symbol of S.
	 * @param letter Next letter of the testing sequence
	 * @return Intermediate result to deciding if the sequence is accepted
	 */
	private static boolean S(char letter){
		//S->aAB|bBA
		System.out.print("S");
		
		if(letter=='a'){
			
			slider++;
			if(!A(word.charAt(slider))) return false;
			return B(word.charAt(slider));
		}
		else if(letter=='b'){
			
			slider++;
			if(!B(word.charAt(slider))) return false;
			return A(word.charAt(slider));
		}
		else 
			return false; //exit
	}
	
	/**
	 * Parser production for the unfinished symbol of A.
	 * @param letter Next letter of the testing sequence
	 * @return Intermediate result to deciding if the sequence is accepted
	 */
	private static boolean A(char letter){
		//A->bC|a
		System.out.print("A");
		
		if(letter=='b'){

			slider++;
			return C(word.charAt(slider));
		}
		else if(letter=='a'){
			slider++;

			if(word.charAt(slider)=='$')
				reachedEnd=true;
			return true;
		}
		else 
			return false;

	}
	
	/**
	 * Parser production for the unfinished symbol of B.
	 * @param letter Next letter of the testing sequence
	 * @return Intermediate result to deciding if the sequence is accepted
	 */
	private static boolean B(char letter){
		//B->ccSbc|$
		System.out.print("B");
		

		if(letter=='c'){

			slider++;
			if(word.charAt(slider)!='c') 
				return false;
			
			slider++;
			if(!S(word.charAt(slider))) return false;
			if(word.charAt(slider)!='b') 
				return false;

			slider++;
			if(word.charAt(slider)!='c') 
				return false;

			slider++;
			if(word.charAt(slider)=='$')
				reachedEnd=true;
			return true;
			
		}
		else {
			if(letter=='$')
				reachedEnd=true;
			return true; //exit
		}
	}
	
	/**
	 * Parser production for the unfinished symbol of C.
	 * @param letter Next letter of the testing sequence
	 * @return Intermediate result to deciding if the sequence is accepted
	 */
	private static boolean C(char letter){
		//C->AA
		System.out.print("C");
		
		if(!A(word.charAt(slider))) return false;
		return A(word.charAt(slider));
	}
}
