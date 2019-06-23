/* FeatureIDE - A Framework for Feature-Oriented Software Development
 * Copyright (C) 2005-2017  FeatureIDE team, University of Magdeburg, Germany
 *
 * This file is part of FeatureIDE.
 *
 * FeatureIDE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FeatureIDE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FeatureIDE.  If not, see <http://www.gnu.org/licenses/>.
 *
 * See http://featureide.cs.ovgu.de/ for further information.
 */
package org.prop4j.solvers.impl.javasmt;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

import org.prop4j.Literal;
import org.prop4j.Node;
import org.prop4j.Or;
import org.prop4j.solver.ISolverProblem;

/**
 * Represents the memory for the solver. It is possible to push and pop Nodes with a given representation of the constraints that are used for the specific
 * Solver. Also manages the mapping from nodes to index and index to nodes.
 *
 * @author Joshua Sprey
 */
public class MutableSolverMemory<T, A> {

	/** Maps the assumptions to the nodes. */
	private final HashMap<A, Node> assumptionToNode = new HashMap<>();

	/** Maps the pushed nodes to integer. */
	private final HashMap<Node, Integer> clauseToInt = new HashMap<>();
	/** Maps the formula to the nodes. */
	private final HashMap<T, Node> formulaToNode = new HashMap<>();
	/** Stack holds the correct order how the Nodes were pushed to the stack. */
	private final LinkedList<Node> insertionStack = new LinkedList<Node>();
	/** Maps the integer to nodes for pushed nodes. */
	private final HashMap<Integer, Node> intToClause = new HashMap<>();

	/** Maps the integer to variables for new variables. */
	private final HashMap<Integer, Object> intToVar = new HashMap<>();
	/** Are assumptions assumed to be clauses when retrieving the indexes */
	private boolean isAssumptionAClause = false;

	/** Index for the next clause */
	private int nextClauseIndex = 0;
	/** Index for the next variable */
	private int nextVariableIndex = 1;

	/** Maps the nodes to the assumptions. */
	private final HashMap<Node, A> nodeToAssumption = new HashMap<>();
	/** Maps the nodes to the formula. */
	private final HashMap<Node, T> nodeToFormula = new HashMap<>();
	/** Represents a problem that is shared by many solvers to save memory. Contains the static clauses in {@link Node} representation. */
	private final ISolverProblem problem;
	/** Holds all formulas for the clauses which are part of the problem itself. The generic representation of all clauses from the main problem. */
	private final List<T> staticClauses;
	/** Maps the new variables to integer. */
	private final HashMap<Object, Integer> varToInt = new HashMap<>();

	/**
	 * Creates a new push solver stack without a problem.
	 *
	 */
	public MutableSolverMemory() {
		this.problem = null;
		this.staticClauses = null;
	}

	/**
	 * Creates a new push solver stack.
	 *
	 * @param problem Problem to use.
	 * @param staticClauses Holds all formulas for the clauses which are part of the problem itself. The generic representation of all clauses from the main
	 *        problem.
	 * @param isAssumptionAClause Are assumptions assumed to be clauses when retrieving the indexes
	 */
	public MutableSolverMemory(ISolverProblem problem, List<T> staticClauses, boolean isAssumptionAClause) {
		this.problem = problem;
		this.staticClauses = staticClauses;
		nextClauseIndex = problem.getClauses().length;
		nextVariableIndex = problem.getNumberOfVariables() + 1;
		this.isAssumptionAClause = isAssumptionAClause;
	}

	/**
	 * Add a new variable to the mapping.
	 *
	 * @param variable Variable to map. Does nothing if the variable was already added to the memory.
	 * @return 0 if the variable is already present, otherwise the assigned integer.
	 */
	public int addVariable(Object variable) {
		// Variable is new and not in problem
		if (!isVariablePresent(variable)) {
			// Also unknown in memory => add to variables
			varToInt.put(variable, nextVariableIndex);
			intToVar.put(nextVariableIndex, variable);
			return nextVariableIndex++;
		}
		return 0;
	}

	/**
	 * Returns all assumptions.
	 *
	 * @return All assumptions as list
	 */
	public List<A> getAssumptionsAsList() {
		final List<A> list = new ArrayList<>(nodeToAssumption.size());
		for (final A a : nodeToAssumption.values()) {
			list.add(a);
		}
		return list;
	}

	/**
	 * Returns the clause at the given index. If isAssumptionAClause is true then the ondex of assumption are also included.
	 *
	 * @param index Index
	 * @return Clause when index available, otherwise null
	 */
	public Node getClauseOfIndex(int index) {
		if ((index >= nextClauseIndex) || (index < 0)) {
			return null;
		}
		if (index < 0) {
			index = Math.abs(index);
		}
		if (problem != null) {
			final Node problemClause = problem.getClauseOfIndex(index);
			if (problemClause == null) {
				return intToClause.get(index);
			} else {
				return problemClause;
			}
		} else {
			return intToClause.get(index);
		}
	}

	/**
	 * Returns all clauses.
	 *
	 * @return All clauses as list
	 */
	public List<T> getClausesAsList() {
		final List<T> list = new ArrayList<>(staticClauses);
		for (final T a : nodeToFormula.values()) {
			list.add(a);
		}
		return list;
	}

	/**
	 * Returns all clauses.
	 *
	 * @return All clauses as list
	 */
	public List<Node> getClausesAsNodeList() {
		if (problem != null) {
			final List<Node> list = new ArrayList<>(Arrays.asList(problem.getClauses()));
			for (final Node a : nodeToFormula.keySet()) {
				list.add(a);
			}
			return list;
		} else {
			final List<Node> list = new ArrayList<>();
			for (final Node a : nodeToFormula.keySet()) {
				list.add(a);
			}
			return list;
		}
	}

	/**
	 * Returns index of clause.
	 *
	 * @param clause Clause
	 * @return Index of clause if available, otherwise -1
	 */
	public int getIndexOfClause(Node clause) {
		if (problem != null) {
			final int problemIndex = problem.getIndexOfClause(clause);
			if (problemIndex == -1) {
				if (clauseToInt.containsKey(clause)) {
					return clauseToInt.get(clause);
				} else {
					return -1;
				}
			} else {
				return problemIndex;
			}
		} else {
			if (clauseToInt.containsKey(clause)) {
				return clauseToInt.get(clause);
			} else {
				return -1;
			}
		}
	}

	/**
	 * Returns the index for the given variable.
	 *
	 * @param var
	 */
	public int getIndexOfVariable(Object var) {
		if (problem != null) {
			final int problemIndex = problem.getIndexOfVariable(var);
			if (problemIndex == 0) {
				if (isVariablePresent(var)) {
					return varToInt.get(var);
				} else {
					return 0;
				}
			} else {
				return problemIndex;
			}
		} else {
			if (isVariablePresent(var)) {
				return varToInt.get(var);
			} else {
				return 0;
			}
		}
	}

	public ISolverProblem getProblem() {
		return problem;
	}

	/**
	 * Returns the signed index of a given literal.
	 *
	 * @param var
	 * @return
	 */
	public int getSingedIndexOfVariable(Literal var) {
		return var.positive ? getIndexOfVariable(var.var) : -getIndexOfVariable(var.var);
	}

	/**
	 * Returns the variable that is registered on the given index.
	 *
	 * @param index Index you want the variable of
	 * @return Variable if the index is available, otherwise null
	 */
	public Object getVariableOfIndex(int index) {
		if (index == 0) {
			return null;
		}
		if (index < 0) {
			index = Math.abs(index);
		}
		if (problem != null) {
			if (((index > problem.getNumberOfVariables()) && !intToVar.containsKey(index))) {
				return null;
			}
			final Object problemVar = problem.getVariableOfIndex(index);
			if (problemVar == null) {
				return intToVar.get(index);
			} else {
				return problemVar;
			}
		} else {
			return intToVar.get(index);
		}
	}

	/**
	 * Checks if the next pop operation is going to be a assumption
	 *
	 * @return true, when the next popped node was an assumption
	 */
	public boolean isNextPopAssumption() {
		final Node node = insertionStack.peek();
		return nodeToAssumption.containsKey(node);
	}

	/**
	 * Returns true if the variable is registered in the problem or was already added to the stack.
	 *
	 * @param variable Variable to add
	 * @return true, if variable is present in the memory.
	 */
	public boolean isVariablePresent(Object variable) {
		if (problem != null) {
			return (problem.getIndexOfVariable(variable) == 0) ? varToInt.containsKey(variable) : true;
		} else {
			return varToInt.containsKey(variable);
		}
	}

	/**
	 * Peek at the next to be popped Node.
	 *
	 * @return
	 */
	public Node peekNextNode() {
		return insertionStack.peek();
	}

	/**
	 * Removes the latest pushed node from the memory and returns the Node
	 *
	 * @return Node
	 */
	public Node pop() {
		try {
			final Node node = insertionStack.pop();
			if (nodeToAssumption.containsKey(node)) {
				// Popped Assumption
				final A value = nodeToAssumption.get(node);
				nodeToAssumption.remove(node);
				assumptionToNode.remove(value);
				if (isAssumptionAClause) {
					removePushedAssumptionClause((Literal) node);
				}
			} else {
				// Popped clause
				final T value = nodeToFormula.get(node);
				nodeToAssumption.remove(node);
				nodeToFormula.remove(value);
				final int index = clauseToInt.get(node);
				clauseToInt.remove(node);
				intToClause.remove(index);
				nextClauseIndex--;
			}
			return node;
		} catch (final NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * Removes the latest pushed node from the memory and returns the Node
	 *
	 * @return Node
	 */
	public A popAssumption() {
		try {
			if (isNextPopAssumption()) {
				final Node node = insertionStack.pop();
				final A value = nodeToAssumption.get(node);
				nodeToAssumption.remove(node);
				assumptionToNode.remove(value);
				if (isAssumptionAClause) {
					removePushedAssumptionClause((Literal) node);
				}
				return value;
			} else {
				return null;
			}
		} catch (final NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * Removes the latest pushed node from the memory and returns the Node
	 *
	 * @return Node
	 */
	public T popClause() {
		try {
			if (isNextPopAssumption()) {
				return null;
			} else {
				// Popped clause
				final Node node = insertionStack.pop();
				final T value = nodeToFormula.get(node);
				nodeToAssumption.remove(node);
				nodeToFormula.remove(value);
				final int index = clauseToInt.get(node);
				clauseToInt.remove(node);
				intToClause.remove(index);
				nextClauseIndex--;
				return value;
			}
		} catch (final NoSuchElementException e) {
			return null;
		}
	}

	/**
	 * Pushes a clause node with the given representation to the memory.
	 *
	 * @param node Clause as Node to push
	 * @param formula Formula that represents the Node for the given Solver.
	 */
	public void push(Node node, T formula) {
		if (!(node instanceof Or)) {
			return;
		}
		insertionStack.push(node);
		nodeToFormula.put(node, formula);
		formulaToNode.put(formula, node);
		clauseToInt.put(node, nextClauseIndex);
		intToClause.put(nextClauseIndex, node);
		nextClauseIndex++;
	}

	/**
	 * Is used internally if an assumption was pushed and the solver should treat assumptions as clauses.
	 *
	 * @param node Clause as Node to push
	 * @param formula Formula that represents the Node for the given Solver.
	 */
	private void pushAssumptionAsClause(Node node, T formula) {
		nodeToFormula.put(node, formula);
		formulaToNode.put(formula, node);
		clauseToInt.put(node, nextClauseIndex);
		intToClause.put(nextClauseIndex, node);
		nextClauseIndex++;
	}

	/**
	 * Removes an assumption that was pushed as clause from the memory.
	 *
	 * @param node Clause to remove
	 */
	private void removePushedAssumptionClause(Literal node) {
		// Popped clause
		final T value = nodeToFormula.get(node);
		nodeToAssumption.remove(node);
		nodeToFormula.remove(value);
		final int index = clauseToInt.get(node);
		clauseToInt.remove(node);
		intToClause.remove(index);
		nextClauseIndex--;
	}

	/**
	 * Pushes an assumption node with the given representation to the memory. Also adds the assumption as clause internally if the respective flag
	 * isAssumptionAClause is set to true. Assumptions can only be pushed as clause if both generic types are equal.
	 *
	 * @param node Assumption node to push
	 * @param formula Formula that represents the Node for the given Solver.
	 */
	@SuppressWarnings("unchecked")
	public void pushAssumption(Literal node, A assumption) {
		if (!(node instanceof Literal)) {
			return;
		}
		insertionStack.push(node);
		nodeToAssumption.put(node, assumption);
		assumptionToNode.put(assumption, node);
		if (isAssumptionAClause) {
			pushAssumptionAsClause(node, (T) assumption);
		}
	}

}
