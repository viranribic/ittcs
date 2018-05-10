import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

/**
 * UTR_Lab01: Nondeterministic finite automaton with e-moves
 * 
 * @author Viran
 *
 */
public class SimEnka {

	public static void main(String[] args) {

		List<String[]> sequList = new LinkedList<>();
		ENKA enka = new ENKA();

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

			// row 4: accepted states
			ln = input.nextLine().trim();
			String[] acceptedStates = ln.split(",");

			// row 5: starting state
			ln = input.nextLine().trim();
			String startingState = ln.trim();

			// Initialize the automata
			enka.addStartingStateLabel(startingState);
			enka.addStatesLabels(states);
			enka.addAcceptedStatesLabels(acceptedStates);
			enka.addAlphabetLabels(alphabet);

			// start creating automata relations

			while (true) {
				ln = input.nextLine().trim();
				if (ln.trim().equals(""))
					break;

				String[] trans = ln.split("->");
				String[] arguments = trans[0].split(",");
				String[] res = trans[1].split(",");
				enka.addRelations(arguments[0].trim(), arguments[1].trim(), res);
			}

		} catch (Exception e) {
		}

		// run simulation for every test
		for (String[] s : sequList) {
			enka.runSim(s);
			enka.reset();
		}

	}

	/**
	 * Nondeterministic finite automaton with e-moves
	 * 
	 * @author Viran
	 *
	 */
	private static class ENKA {
		private Map<String, State> statesMap = new HashMap<>();
		private Set<String> alphabet = new HashSet<>(); // Unnecessary....
		private Set<String> acceptedStates = new HashSet<>(); // Unnecessary....
		private Set<String> stateList = new HashSet<>(); // Unnecessary....
		private String startingState;
		private Set<String> currentStates = new HashSet<>(); 
		
		
		public ENKA() {
			statesMap.put("#", new State("#"));
		}

		/**
		 * State representation for a finite automata
		 * 
		 * @author Viran
		 *
		 */
		private static class State {
			private String name;
			private Map<String, String[]> transitions = new HashMap<String, String[]>();

			/**
			 * State constructor.
			 * 
			 * @param name
			 *            State label
			 */
			public State(String name) {
				this.name = name;
			}

			/**
			 * Return all transitions of this state for the given letter.
			 * 
			 * @param a
			 *            Letter of alphabet which this state recognizes
			 * @return Array of strings containing transition state labels
			 */
			public String[] getTransitions(String a) {
				return transitions.get(a);
			}

			/**
			 * Assign the given
			 * 
			 * @param symbols
			 * @param trans
			 */
			public void addTransitions(String symbol, String[] trans) {
				if (!transitions.containsKey(symbol))
					transitions.put(symbol, trans);
				/*
				 * else{ //tell there is a mistake }
				 */
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

		}

		/**
		 * Add beginning state for this e-NKA.
		 * 
		 * @param startingState
		 *            First state from which e-NKA test the given input
		 *            sequence.
		 */
		public void addStartingStateLabel(String startingState) {
			this.startingState = startingState;
			currentStates.add(startingState.trim());

		}

		/**
		 * Add specific alphabet for this e-NKA.
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
		 * Add accepted states for this e-NKA.
		 * 
		 * @param acceptedStates
		 *            States for which the e-NKA returns the value of 1 (true).
		 */
		public void addAcceptedStatesLabels(String[] acceptedStates) {
			for (String s : acceptedStates) {
				this.acceptedStates.add(s.trim());
			}

		}

		/**
		 * Add states contained for this e-NKA.
		 * 
		 * @param states
		 *            States in which e-NKA can operate.
		 */
		public void addStatesLabels(String[] states) {
			for (String s : states) {
				this.stateList.add(s.trim());
			}
		}

		/**
		 * Add a new relation function to this e-NKA.
		 * 
		 * @param stateName
		 *            State label which extends its transition functions.
		 * @param symbol
		 *            Element of the e-NKA alphabet which triggers the change.
		 * @param transitions
		 *            List of states to which one state changes given a specific
		 *            element of the alphabet.
		 */
		public void addRelations(String stateName, String symbol,
				String[] transitions) {
			State state;
			if (statesMap.containsKey(stateName)) {
				state = statesMap.get(stateName);
			} else {
				state = new State(stateName);
			}
			state.addTransitions(symbol, transitions);
			statesMap.put(stateName, state);
		}

		/**
		 * Set the current state to the beginning state of the machine.
		 */
		public void reset() {
			currentStates = new HashSet<>();
			currentStates.add(startingState);
		}

		/**
		 * Run the simulation for a given sequence. For each transition print
		 * all reachable states using the e-closure.
		 * 
		 * @param sequence
		 *            Input sequence for automata behavior simulation.
		 */
		public void runSim(String[] sequence) {

			StringBuilder result = new StringBuilder();
			Set<String> startS_e = EClosure(startingState,
					new TreeSet<String>());

			// startingState result build:

			boolean fHelp = true; // fHelp determinants the right order for
									// output format as follows: a , b , c , d |
									// ...

			for (String s : startS_e) {
				if (fHelp) {
					fHelp = !fHelp;
					result.append(s);
					//to the searching states add all who have e-closure with the starting state
					currentStates.add(s); 
				} else{
					result.append("," + s);
					//to the searching states add all who have e-closure with the starting state
					currentStates.add(s);
				}
			}
			result.append("|");

			
			
			// build for the rest of states
			for (String inputSymbol : sequence) {
				// for every new change get the next states and the next
				// environments
				Set<String> nextStates = new HashSet<>();
				Set<String> eEnvironments = new TreeSet<>();
				Set<String> resultingStates=new TreeSet<>();
				
				// for a given symbol : extract all states which are reachable,
				// save them to nextStates list (later to be swapped with
				// currentStates)
				for (String curState : currentStates) {
					State currentState = statesMap.get(curState);
					
					// if the machine is stuck in unidentified state OR there is
					// a new element at the entrance and we don't have any
					// transition functions for this state...
					if (curState.equals("#") || currentState == null) {
						resultingStates.add("#");
					} else {
						String[] trans = currentState
								.getTransitions(inputSymbol);
						if (trans == null) {
							nextStates.add("#");
						} else {
							for (String nextSt : trans) {
								nextStates.add(nextSt);
							}
							// after all states have been found build a special
							// set
							// of
							// e-env. for the found params.
							for (String state : nextStates) {
								eEnvironments.addAll(EClosure(state,
										new TreeSet<String>()));
							}
						}
						// first check if there are any unreachable states by
						// removing the # state and adding only it if the rest
						// of
						// the set is empty

						eEnvironments.remove("#");
						if (eEnvironments.isEmpty()) {
							resultingStates.add("#");	
						} else {
							for (String s : eEnvironments) {
								resultingStates.add(s);
							}
						}
						// refresh current states for the next iteration
						if(nextStates.size()==1)
							currentStates = nextStates;
						else{
							currentStates= new HashSet<>(eEnvironments);
							
						}
					}
				}
				
				if(!(resultingStates.size()==1) && resultingStates.contains("#")){
					resultingStates.remove("#");
				}
				
				fHelp = true;
				for(String st:resultingStates){
					if (fHelp) {
						fHelp = !fHelp;
						result.append(st);
					} else
						result.append("," + st);
				}
				result.append("|");
			}
			String output = result.toString();
			
			//repair formatting
			if(output.endsWith("|")){
				output=output.substring(0, output.length()-1);
			}

			System.out.println(output);

		}

		/**
		 * Determinate the e-closure of a given state.
		 * 
		 * @param state
		 *            State we examine for e-transitions
		 * @param set
		 *            Set in which the resulting states are accumulated
		 * @return Set of all e-reachable states.
		 */
		private Set<String> EClosure(String state, Set<String> set) {
			if(!state.equals("#")){
				set.add(state);
				State givenState = statesMap.get(state);
				if (givenState != null) {
					String[] transmitions = givenState.getTransitions("$");
					if (transmitions != null) {
						for (String trans : transmitions) {
							//add only those who are not checked
							if(!set.contains(trans)){
								set.addAll(EClosure(trans, set));
							}
						}
					}
				}
			}
			return set;
		}
	}

}