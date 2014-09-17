/*
 * Copyright 2009-2013 Aarhus University
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dk.brics.tajs.flowgraph;

import static dk.brics.tajs.util.Collections.newList;
import static dk.brics.tajs.util.Collections.newSet;

import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import dk.brics.tajs.util.AnalysisException;
import dk.brics.tajs.util.Strings;

/**
 * Sequence of nodes.
 * Must be non-empty.
 * Has a unique entry node and proceeds through the sequence unless exceptions are thrown. 
 */
public class BasicBlock {

	/**
	 * Unique index of this block in the flow graph, or -1 if not belonging to a flow graph.
	 */
	private int index = -1;
	
	/**
	 * Block order, used for worklist prioritization.
	 */
	private int order = -1;

	/**
	 * The nodes in this block.
	 */
	private List<AbstractNode> nodes;

	/**
	 * Successors of this block. Not including successors of call nodes and exception edges.
	 */
	private Collection<BasicBlock> successors;
	
	/**
	 * The exception handler block for nodes in this block.
	 */
	private BasicBlock exception_handler;
	
	/**
	 * The function containing this block.
	 */
	private Function function;

    /**
	 * Constructs a new initially empty block of nodes.
     *
     * @param function The function this block belongs to.
	 */
	public BasicBlock(Function function) {
		if (function == null)
			throw new NullPointerException("Creating a new basic block with null function");
		this.function = function;
		successors = newSet();
		nodes = newList();
	}
	
	/**
	 * Adds a successor.
	 */
	public void addSuccessor(BasicBlock succ) {
		if (succ == null)
			throw new NullPointerException("Setting successor of basic block to null");
		successors.add(succ);
	}
	
	/**
	 * Remove a successor.
	 */
	public void removeSuccessor(BasicBlock succ) {
		if (succ == null)
			throw new NullPointerException("Trying to remove a null successor");
		if (!successors.contains(succ))
			throw new AnalysisException("The basic block is not a successor");
		successors.remove(succ);
	}

	/**
	 * Returns the successors of this block.
	 */
	public Collection<BasicBlock> getSuccessors() {
		return successors;
	}
	
	/**
	 * Returns the single successor block.
	 * @throws AnalysisException if not exactly one successor
	 */
	public BasicBlock getSingleSuccessor() {
		if (successors.size() != 1)
			throw new AnalysisException("Expected exactly one successor of basic block " + index);
		return successors.iterator().next();
	}

	/**
	 * Sets the block order.
	 */
	void setOrder(int order) {
		this.order = order;
	}
	
	/**
	 * Returns the block order.
	 */
	public int getOrder() {
		return order;
	}
	
	/**
	 * Sets the block index.
	 * Called when the flow graph block structure is updated.
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Returns the block index.
	 * The block index uniquely identifies the block within the flow graph.
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Checks whether this block has been added to a flow graph.
	 */
	public boolean isAdded() {
		return index != -1;
	}

	/**
	 * Adds a node to this block.
	 */
	public void addNode(AbstractNode n) {
		if (n == null)
			throw new NullPointerException("Adding null node to basic block");
		nodes.add(n);
		n.setBlock(this);
	}
	
    /**
     * Removes a set of nodes from the basic block.
     */
    public void removeNodes(Collection<AbstractNode> ns) {
        nodes.removeAll(ns);
    }

 	/**
     * Replaces the set of nodes in the basic block.
     */
    public void replaceNodes(List<AbstractNode> ns) {
        nodes = ns;
    }

    /**
	 * Returns the sequence of nodes.
	 */
	public List<AbstractNode> getNodes() {
		return nodes;
	}

    /**
     * Returns true if there are no nodes in the basic block.
     */
    public boolean isEmpty() {
        return nodes.isEmpty();
    }

    /**
     * Returns the single node in the basic block.
     */
    public AbstractNode getSingleNode() {
        if (nodes.size() != 1)
            throw new AnalysisException("Basic block does not contain exactly one node: " + toString());
        return nodes.get(0);
    }

	/**
	 * Returns the first node.
	 */
	public AbstractNode getFirstNode() {
		return nodes.get(0);
	}
	
	/**
	 * Returns the last node.
	 */
	public AbstractNode getLastNode() {
		return nodes.get(nodes.size() - 1);
	}
	
	/**
	 * Checks whether this is a function entry block.
	 */
	public boolean isEntry() {
		return function.getEntry() == this;
	}

	/**
	 * Returns the function containing this block.
	 */
	public Function getFunction() {
		return function;
	}
	
	/**
	 * Returns the source location for the first node in the basic block.
	 */
	public SourceLocation getSourceLocation() {
		return getFirstNode().getSourceLocation();
	}
	
	/**
	 * Returns the exception handler block, or null if not set.
	 */
	public BasicBlock getExceptionHandler() {
		return exception_handler;
	}
	
	/**
	 * Sets the exception handler block.
	 */
	public void setExceptionHandler(BasicBlock exception_handler) {
		this.exception_handler = exception_handler;
	}

	/**
	 * Returns true if this block contains a node that may throw exceptions.
	 */
	public boolean canThrowExceptions() {
		for (AbstractNode n : nodes)
			if (n.canThrowExceptions())
				return true;
		return false;
	}
	
	/**
	 * Returns a string description of this block.
	 */
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("block ").append(index).append(':');
		for (AbstractNode n : getNodes()) {
			s.append("\n  node ").append(n.getIndex());
			AbstractNode d = n.getDuplicateOf();
			if (d != null)
				s.append("(~").append(d.getIndex()).append(")");
			s.append(": ").append(n);
			if (n.isRegistersDone())
				s.append('*');
			if (n.isDead())
				s.append('$');
		}
		if (exception_handler != null)
			s.append("\n  exceptions: ").append(exception_handler.getIndex());
		return s.toString();
	}
	
	/**
	 * Produces a Graphviz dot representation of this block.
     *
     * @param pw Receiver of the dot representation of this basic block.
     * @param standalone Indicates that this is a complete graph that should be closed.
	 */
	public void toDot(PrintWriter pw, boolean standalone) {
		if (standalone) {
			pw.println("digraph {");
			pw.println("rankdir=\"TD\"");
		}
		pw.print("BB" + index + " [shape=record label=\"{");
		boolean first = true;
		for (AbstractNode n : getNodes()) {
			if (first)
				first = false;
			else
				pw.print('|');
			pw.print(n.getIndex() + ": " + Strings.escape(n.toString()));
			if (n.isRegistersDone())
				pw.print('*');
			if (n.isDead()) 
				pw.print('$');
		}
		pw.print("}\" ] " + "\n");
		if (standalone) {
			pw.println("}");
			pw.close();
		}
	}

    /**
     * Perform a consistency check of the basic block.
     */
    public void check(BasicBlock ordinary_exit, BasicBlock exceptional_exit, Set<Integer> seen_blocks, Set<Integer> seen_nodes) {
        if (this != ordinary_exit && this != exceptional_exit && successors.isEmpty())
            throw new AnalysisException("No successor for block: " + toString());
        if (isEmpty())
            throw new AnalysisException("Basic block is empty: " + toString());
        // Make sure the line number checks are before block order check; bad line numbers give crazy block orders.
        if (getSourceLocation().getLineNumber() < 0)
            throw new AnalysisException("Negative line number in source information for block: " + toString());
        if (order == -1)
            throw new AnalysisException("Block order has not been set: " + toString());
        if (index == -1)
            throw new AnalysisException("Block has not been added to flow graph: " + toString());
        if (!seen_blocks.add(index))
            throw new AnalysisException("Duplicate block index: " + toString());
        if (exceptional_exit == null && canThrowExceptions())
            throw new AnalysisException("No exception handler for block " + toString());
        if (this == ordinary_exit && !(getFirstNode() instanceof IReturnNode))
            throw new AnalysisException("Last node in function is not a return node: " + toString());
        for (AbstractNode node : nodes) {
            if (node.getIndex() == -1)
                throw new AnalysisException("Node has not been added to flow graph: " + node.toString());
            if (!seen_nodes.add(node.getIndex()))
                throw new AnalysisException("Duplicate node index: " + node.toString());
            if (node.getSourceLocation().getLineNumber() < 0) 
                throw new AnalysisException("Negative line number in source information for node: " + node.toString());
            node.check(this);
        }
    }
}
