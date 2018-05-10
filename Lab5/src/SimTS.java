import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.print.attribute.standard.MediaSize.Other;




public class SimTS {

	public static void main(String[] args) {

		List<String[]> sequList = new LinkedList<>();
		TuringMachine turingMachine = new TuringMachine();

		try (Scanner input = new Scanner(System.in)) {

			// row 1: existing states
			String ln = input.nextLine().trim();
			String[] states = ln.split(",");

			// row 2: supported alphabet
			ln = input.nextLine().trim();
			String[] alphabet = ln.split(",");

			// row 3: machine labels
			ln = input.nextLine().trim();
			String[] machinLabels = ln.split(",");

			// row 4: empty position marker : machine sequence 
			ln = input.nextLine().trim();
			String emptyMarker = ln.trim();

			//row 5: sequence of starting machine states
			ln = input.nextLine().trim();
			String startingSeqrunce = ln.trim();
			
			//row 6: accepted state
			ln = input.nextLine().trim();
			String[] acceptedStates = ln.split(",");
						
			//row 7: starting state
			ln = input.nextLine().trim();
			String startingState = ln.trim();
			
			//row 8: starting position
			ln = input.nextLine().trim();
			String startingPosition = ln.trim();
			
			
			// Initialize the automata
			turingMachine.addStates(states);
			turingMachine.addAlphabet(alphabet);
			turingMachine.addListLabels(machinLabels);
			turingMachine.addEmptyListMarker(emptyMarker);
			turingMachine.addStartingList(startingSeqrunce);
			turingMachine.addAcceptedStates(acceptedStates);
			turingMachine.addStartingState(startingState);
			turingMachine.addStartingPosition(startingPosition);
			// start creating automata relations

			while (true) {
				ln = input.nextLine().trim();
				if (ln.trim().equals(""))
					break;

				String[] trans = ln.split("->");
				String[] arguments = trans[0].split(",");
				String[] res = trans[1].split(",");
				//arg0=current state  / arg1=symbol / res0=nextState / res1=newSymbol  res2=move {R=right, L=left}
				turingMachine.addRelations(arguments[0].trim(), arguments[1].trim(), res[0].trim(),res[1].trim(),res[2].trim());
			}

		} catch (Exception e) {
		}
		
		//start the calculation
		turingMachine.start();
	}
	
	private static class TuringMachine{
		private Set<String> states=new HashSet<>();
		private Set<String> alphabet = new HashSet<>();
		private Set<String> listLabels=new HashSet<>();
		private String emptyMarker;
		private LinkedList<String> machineList=new LinkedList<>();
		private Set<String> acceptedStates=new HashSet<>();
		private String startingState;
		private Integer startingPosition=0;
		private Integer cursorPosition=0;
		private String currentState;
		private  Map<Arguments,Transition> transitionsMap=new HashMap<>();
	
		private static class Arguments{
			private String currentState;
			private String inputSymbol;
			
			public Arguments(String currentState, String inputSymbol) {
				this.currentState=currentState;
				this.inputSymbol=inputSymbol;
			}
			
			public String getcurrentState() {
				return currentState;
			}
			public String getInputSymbol() {
				return inputSymbol;
			}
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime
						* result
						+ ((currentState == null) ? 0 : currentState.hashCode());
				result = prime * result
						+ ((inputSymbol == null) ? 0 : inputSymbol.hashCode());
				return result;
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Arguments other = (Arguments) obj;
				if (currentState == null) {
					if (other.currentState != null)
						return false;
				} else if (!currentState.equals(other.currentState))
					return false;
				if (inputSymbol == null) {
					if (other.inputSymbol != null)
						return false;
				} else if (!inputSymbol.equals(other.inputSymbol))
					return false;
				return true;
			}
			
			
		}
		
		private static class Transition{
			private String nextState;
			private String nextLabel;
			private String nextMove;
			
			public Transition(String nextState, String nextLabel, String nextMove) {
				this.nextLabel=nextLabel;
				this.nextMove=nextMove;
				this.nextState=nextState;
			}
			
			public String getNextState() {
				return nextState;
			}
			public String getnextLabel() {
				return nextLabel;
			}
			public String getNextMove() {
				return nextMove;
			}
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((nextMove == null) ? 0 : nextMove.hashCode());
				result = prime * result
						+ ((nextState == null) ? 0 : nextState.hashCode());
				result = prime * result
						+ ((nextLabel == null) ? 0 : nextLabel.hashCode());
				return result;
			}
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				Transition other = (Transition) obj;
				if (nextMove == null) {
					if (other.nextMove != null)
						return false;
				} else if (!nextMove.equals(other.nextMove))
					return false;
				if (nextState == null) {
					if (other.nextState != null)
						return false;
				} else if (!nextState.equals(other.nextState))
					return false;
				if (nextLabel == null) {
					if (other.nextLabel != null)
						return false;
				} else if (!nextLabel.equals(other.nextLabel))
					return false;
				return true;
			}
			
			
		}
		
		public void addStates(String[] states) {
			for(String s:states)
				this.states.add(s);
		}

		public void addAcceptedStates(String[] acceptedStates) {
			for (String s : acceptedStates) {
				this.acceptedStates.add(s.trim());
			}
			
		}

		public void addStartingPosition(String startingPosition) {
			cursorPosition=Integer.parseInt(startingPosition);
			this.startingPosition=Integer.parseInt(startingPosition);
		}

		public void addStartingState(String startingState) {
			this.startingState=startingState;
			this.currentState=startingState;
			
		}

		public void addStartingList(String startingSeqrunce) {
			int i;
			for(i=0;i<startingSeqrunce.length();i++)
				machineList.add(Character.toString( startingSeqrunce.charAt(i) ));
			for(;i<70;i++)
				machineList.add(emptyMarker);
		}

		public void addEmptyListMarker(String emptyMarker) {
			this.emptyMarker=emptyMarker;
			
		}

		public void addListLabels(String[] machinLabels) {
			for(String s:machinLabels)
				listLabels.add(s);
			
		}

		public void addAlphabet(String[] alphabet) {
			for(String s:alphabet)
				this.alphabet.add(s);
		}

		public void addRelations(String state, String symbol, String nextState, String nextLabel, String nextMove) {
			transitionsMap.put(new Arguments(state, symbol), new Transition(nextState, nextLabel, nextMove));
			
		}

		
		public void start() {
			try{
				Transition transition;
				while(true){
					transition=transitionsMap.get(new Arguments(currentState, machineList.get(cursorPosition)));			
					
					if (transition==null)break;
					
					currentState=transition.nextState;
					
					machineList.set(cursorPosition, transition.nextLabel);
					
					int offset=transition.nextMove.equals("R")?1:-1;
					cursorPosition+=offset;
				}
			}catch(IndexOutOfBoundsException|NullPointerException  e){
				if(e instanceof IndexOutOfBoundsException)
					cursorPosition= (cursorPosition==70)?(cursorPosition-1):(cursorPosition+1);
				printResult();
				return;
			}
			printResult();
			return;
		}

		private void printResult() {
			String finalMachineList="";
			for(String c:machineList)
				finalMachineList+=c;
			String outcome="";
			outcome+=currentState + "|" + cursorPosition + "|" + finalMachineList + "|";
			if (acceptedStates.contains(currentState))
				outcome+="1";
			else
				outcome+="0";
			System.out.println(outcome);
		}

		@Override
		public String toString() {
			return "TuringMachine [machineList=" + machineList
					+ ", cursorPosition=" + cursorPosition + ", currentState="
					+ currentState + "]";
		}
		
		

	}
}
