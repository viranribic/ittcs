import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

/*
 * Working diary:
 * 	16 ne radi :S	
 *  
 * */


/**
 * Pushdown automata simulation.
 * @author Viran
 *
 */

public class SimPa {

	public static void main(String[] args) {

		List<String[]> sequList = new LinkedList<>();
		PushdownAutomata pa = new PushdownAutomata();

		try (Scanner input = new Scanner(System.in)) {
			// row 1: Automata sequences
			String ln = input.nextLine().trim();
			String[] sequences = ln.split("\\|");
			for (String s : sequences) {
				sequList.add(s.split(","));
			}

			// row 2: existing states
			ln = input.nextLine().trim();
			String[] states = ln.split(",");

			// row 3: supported alphabet
			ln = input.nextLine().trim();
			String[] alphabet = ln.split(",");

			//row 4: supported stack labels
			ln=input.nextLine();
			String[] stackLabels=ln.split(","); 
					
			// row 5: accepted states
			ln = input.nextLine().trim();
			String[] acceptedStates = ln.split(",");

			// row 6: starting state
			ln = input.nextLine().trim();
			String startingState = ln.trim();

			// row 7: starting state
			ln = input.nextLine().trim();
			String startingStackLabel = ln.trim();
			
			
			// Initialize the automata
			pa.addStartingStateLabel(startingState);
			pa.addStatesLabels(states);
			pa.addAcceptedStatesLabels(acceptedStates);
			pa.addAlphabetLabels(alphabet);
			pa.addStackLabels(stackLabels);
			pa.addStartingStackLabel(startingStackLabel);

			// start creating automata relations

			while (true) {
				ln = input.nextLine().trim();
				if (ln.trim().equals(""))
					break;

				String[] trans = ln.split("->");
				String[] arguments = trans[0].split(",");
				String[] res = trans[1].split(",");
				pa.addRelations(arguments[0].trim(), arguments[1].trim(),arguments[2].trim(), res);
			}

		} catch (Exception e) {
		}

		
		// run simulation for every test
		for (String[] s : sequList) {
			pa.runSim(s);
			pa.reset();
		}

	}
	
	/**
	 * Pushdown automata.
	 * 
	 * @author Viran
	 *
	 */
	private static class PushdownAutomata {
		//automata details
		private Map<String, State> statesMap = new HashMap<>();
		private Set<String> alphabet = new HashSet<>(); 
		private Set<String> acceptedStates = new HashSet<>(); 
		private Set<String> stateList = new HashSet<>();
		private String startingState;
		private String currentState; 
		
		//stack details
		private String startingStackLabel;
		private Set<String> stackLabels=new HashSet<>();
		private Stack<String> automataStack=new Stack<>();
		
		//Output details
		StringBuilder resultingString;
		boolean exitWithFailStatus=false;
		
		public PushdownAutomata() {
			statesMap.put("#", new State("#"));
		}
		
		/**
		 * State representation for a pushdown automata.
		 * @author Viran
		 *
		 */
		private static class State {
			private String name;
			private Map<String, Symbol> symbolsMap = new HashMap<String, Symbol>();


			/**
			 * State constructor.
			 * 
			 * @param name
			 *            State label
			 */
			public State(String name) {
				this.name = name;
			}
			
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((name == null) ? 0 : name.hashCode());
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
				State other = (State) obj;
				if (name == null) {
					if (other.name != null)
						return false;
				} else if (!name.equals(other.name))
					return false;
				return true;
			}




			/**
			 * Set the transition for this state, in association with the corresponding input symbol and stack label element. 
			 * @param symbolName Symbol for a transition.
			 * @param stackElement Stack label for a transition.
			 * @param transitions Result of the transition of a particular input combination.
			 */
			public void addTransitions(String symbolName, String stackElement,
					String[] transitions) {
				Symbol symbol;
				if (symbolsMap.containsKey(symbolName)) {
					symbol = symbolsMap.get(symbolName);
				} else {
					symbol = new Symbol(symbolName);
				}
				symbol.addTransitions(stackElement,transitions);
				symbolsMap.put(symbolName, symbol);
				
			}
			
			
			
			@Override
			public String toString() {
				return  name + symbolsMap
						;
			}



			/**
			 * Symbol class containing a map for all stack labels of a transition.
			 * @author Viran
			 *
			 */
			public static class Symbol{
				private String name;
				private Map<String, Label> labelMap=new HashMap<>();
				
				public Symbol(String symbolName) {
					this.name=symbolName;
				}
				
				/**
				 * Set the transition for this symbol, in association with the corresponding stack label element. 
				 * @param stackElementName Stack label for transition.
				 * @param transitions Result of the transition of a particular input combination.
				 */
				public void addTransitions(String stackElementName,
						String[] transitions) {
					Label stackElement;
					if (labelMap.containsKey(stackElementName)) {
						stackElement = labelMap.get(stackElementName);
					} else {
						stackElement = new Label(stackElementName);
					}
					stackElement.addTransitions(transitions);
					labelMap.put(stackElementName, stackElement);
					
				}

				@Override
				public int hashCode() {
					final int prime = 31;
					int result = 1;
					result = prime * result
							+ ((name == null) ? 0 : name.hashCode());
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
					Symbol other = (Symbol) obj;
					if (name == null) {
						if (other.name != null)
							return false;
					} else if (!name.equals(other.name))
						return false;
					return true;
				}

				@Override
				public String toString() {
					return name + labelMap;
				}



				/**
				 * Label class containing a map for all transitions of a state with a given symbol and finally label.
				 * @author Viran
				 *
				 */
				public static class Label{
					private String name;
					private Transition transition;
					
					public Label(String stackElementName) {
						this.name=stackElementName;
					}

					/**
					 * Set the transition for this stack label element. 
					 * @param stackElement Stack label for transition.
					 * @param transitions Result of the transition of a particular input combination.
					 */
					public void addTransitions(String[] transitions) {
						transition = new Transition(transitions);
					}
					
					

					public Transition getTransition() {
						return transition;
					}

					@Override
					public int hashCode() {
						final int prime = 31;
						int result = 1;
						result = prime * result
								+ ((name == null) ? 0 : name.hashCode());
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
						Label other = (Label) obj;
						if (name == null) {
							if (other.name != null)
								return false;
						} else if (!name.equals(other.name))
							return false;
						return true;
					}

					@Override
					public String toString() {
						return  name +
								transition ;
					}
				}
			}	
		}

		/**
		 * Class which represents the resulting values of a transition.
		 * @author Viran
		 *
		 */
		static class Transition{
			private String nextState;
			private String[] nextStackElements;
			
			
			@SuppressWarnings("unused")
			public Transition(String nextState,String nextStack){
				this.nextState=nextState;
				nextStackElements=new String[nextStack.length()];
				for(int i=0;i<nextStack.length();i++)
					this.nextStackElements[i]=""+nextStack.charAt(i);
			}

			public Transition(String[] transitions) {
				//transitions has two elements 0- next state, 1 string of next stack elements
				//writing them in reversed order so that the stack push operation flows naturally
				nextState=transitions[0];
				int numOfElements=transitions[1].length();
				nextStackElements=new String[numOfElements];
				for(int i=numOfElements-1;i>=0;i--){
					nextStackElements[(numOfElements-1)-i]=""+transitions[1].charAt(i);
				}
			}

			public String getNextState() {
				return nextState;
			}

			
			public String[] getNextStackElements() {
				return nextStackElements;
			}

			

			
			
			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((nextState == null) ? 0 : nextState.hashCode());
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
				if (nextState == null) {
					if (other.nextState != null)
						return false;
				} else if (!nextState.equals(other.nextState))
					return false;
				return true;
			}

			@Override
			public String toString() {
				return nextState
						+ Arrays.toString(nextStackElements) ;
			}
			
			
			
		}
		
		/**
		 * Add beginning state for this pushdown automata.
		 * 
		 * @param startingState
		 *            First state from which pushdown automata test the given input
		 *            sequence.
		 */
		public void addStartingStateLabel(String startingState) {
			this.startingState = startingState;
			currentState=startingState.trim();

		}

		/**
		 * Add specific alphabet for this pushdown automata.
		 * 
		 * @param alphabet
		 *            Specific alphabet
		 */
		public void addAlphabetLabels(String[] alphabet) {
			for (String s : alphabet) {
				this.alphabet.add(s.trim());
			}

		}

		/**
		 * Add accepted states for this pushdown automata.
		 * 
		 * @param acceptedStates
		 *            States for which the pushdown automata returns the value of 1 (true).
		 */
		public void addAcceptedStatesLabels(String[] acceptedStates) {
			for (String s : acceptedStates) {
				this.acceptedStates.add(s.trim());
			}

		}

		/**
		 * Add states contained for this pushdown automata.
		 * 
		 * @param states
		 *            States in which pushdown automata can operate.
		 */
		public void addStatesLabels(String[] states) {
			for (String s : states) {
				this.stateList.add(s.trim());
			}
		}

		/**
		 * Add a new relation function to this pushdown automata.
		 * 
		 * @param stateName
		 *            State label which extends its transition functions.
		 * @param symbol
		 *            Element of the pushdown automata alphabet which triggers the change.
		 * @param stackElement 
		 * 			  Stack state for this transition.
		 * @param transitions
		 *            List of states to which one state changes given a specific
		 *            element of the alphabet.
		 */
		public void addRelations(String stateName, String symbol, String stackElement,
				String[] transitions) {
			State state;
			if (statesMap.containsKey(stateName)) {
				state = statesMap.get(stateName);
			} else {
				state = new State(stateName);
			}
			state.addTransitions(symbol, stackElement,transitions);
			statesMap.put(stateName, state);
		}

		/**
		 * Set the current state to the beginning state of the machine.
		 */
		public void reset() {
			currentState=startingState;
			automataStack=new Stack<>();
			automataStack.push(startingStackLabel);
		}
		
		/**
		 * Set the starting state of PA stack to a specific value.
		 * @param startingStackLabel New starting PA stack values.
		 */
		public void addStartingStackLabel(String startingStackLabel) {
			this.startingStackLabel=startingStackLabel;
			automataStack.push(startingStackLabel);
		}

		/**
		 * Set all possible stack labels of this PA.
		 * @param stackLabels List of stack labels.
		 */
		public void addStackLabels(String[] stackLabels) {
			for(String s:stackLabels)
				this.stackLabels.add(s);
		}

		/**
		 * Run the simulation for a given sequence.
		 * 
		 * @param sequence
		 *            Input sequence for automata behavior simulation.
		 */
		public void runSim(String[] sequence) {
			resultingString=new StringBuilder();
			//Part I: While there are sequence symbols
			
			String symbol="$";
			boolean repeatSymbol=false;
			int sequencePosition=0;
			///OOOMMGGG!!!!!->
			String stackLabel = null;
			//<-
			exitWithFailStatus=false;
			saveSystemStatus();
			
			do{
				//read s symbol from the sequence if there was no $ transition
				if(!repeatSymbol){
					if(sequencePosition==sequence.length){
						break;
					}
					else
						symbol=sequence[sequencePosition++];
				}
				
				try{
					//get the next state different from $
					stackLabel=automataStack.pop();
					while(stackLabel.equals("$")&& automataStack.size()!=0)
						stackLabel=automataStack.pop();
				}catch(EmptyStackException e){
					exitWithFailStatus=true;
					resultingString.append("fail|");
					break;
				}
				
				
				Transition transition;
				try{
					transition=determinateTransition(currentState,symbol,stackLabel);
					repeatSymbol=false;
				}catch(NullPointerException e){
						//try to see if there is an $ transition
						try{
							transition=determinateTransition(currentState,"$",stackLabel);
						}catch(RuntimeException err){
							exitWithFailStatus=true;
							resultingString.append("fail|");
							break;
						}
						repeatSymbol=true;
				}
				
				
				currentState=transition.getNextState();
				String[] nextStackLabels = transition.getNextStackElements();
				for(String l:nextStackLabels){
					automataStack.push(l);
				}
				
				saveSystemStatus();
			}while(true);
			
			//abort
			if(exitWithFailStatus){
				resultingString.append("0");
				System.out.println(resultingString.toString());
				return;
			}
			
			//all symbols have been red.
			//if the state is accepted call done
			if(acceptedStates.contains(currentState)){
				resultingString.append("1");
				System.out.println(resultingString.toString());
				return;
			}else{
				//part II:if we reached the end of sequence
				while(!acceptedStates.contains(currentState)){
					
					try{
						//get the next state different from $
						stackLabel=automataStack.pop();
						while(stackLabel.equals("$") && automataStack.size()!=0)
							stackLabel=automataStack.pop();
					}catch(EmptyStackException e){
						break;
					}
				
					
					Transition transition;
					try{
						transition=determinateTransition(currentState,"$",stackLabel);
					}catch(NullPointerException e){
						//try to see if there is a transition for remaining state and $ transition
						//if not, end the algorithm
						break;
					}
				
					currentState=transition.getNextState();
					String[] nextStackLabels = transition.getNextStackElements();
					
					
					for(String l:nextStackLabels){
						automataStack.push(l);
					}
					
					saveSystemStatus();
				}
			
			
				if(acceptedStates.contains(currentState)){
					resultingString.append("1");
				}else
					resultingString.append("0");
				System.out.println(resultingString.toString());
			}
		}

		/**
		 * Save current system status for later print.
		 */
		private void saveSystemStatus() {
			resultingString.append(currentState+"#");
			
			String stackState="";
			for(String label:automataStack){
				if(!label.equals("$"))
					stackState+=label;
			}
			
			if(stackState.equals(""))
				stackState+="$";
			
			for(int i=stackState.length()-1;i>=0;i--){
				resultingString.append(stackState.charAt(i));
			}
			
			resultingString.append("|");
		}

		/**
		 * Determinate next state and new stack values associated with the current state, input symbol and the stack label.
		 * @param curState Current state
		 * @param symbol Sequence symbol
		 * @param stackLabel Stack label
		 * @return Transition for the specific value combination.
		 */
		private Transition determinateTransition(String curState,String symbolName,String stackLabel){
			State state=statesMap.get(curState);
			
			if(state==null)
				throw new NullPointerException("state");
			
			State.Symbol symbol=state.symbolsMap.get(symbolName);
			
			if(symbol==null)
				throw new NullPointerException("symbol");
			
			State.Symbol.Label label=symbol.labelMap.get(stackLabel);
			
			if(label==null)
				throw new NullPointerException("label");
			
			return label.getTransition();
		}
		
	}
}
