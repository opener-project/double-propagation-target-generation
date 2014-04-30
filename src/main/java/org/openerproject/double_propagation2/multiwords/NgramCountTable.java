package org.openerproject.double_propagation2.multiwords;

import java.util.Set;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class NgramCountTable {

	public static final String TOKENS_BEFORE="tokens_before";
	public static final String TOKENS_AFTER="tokens_after";
	public static final String FREQ="freq";
	public static final String LLR="llr";
	
	private Table<String, String, Object> table;
	private int overallCount;
	
	private NgramCountTable(){
		super();
		table=HashBasedTable.create();
		setOverallCount(0);
	}
	
	public static NgramCountTable createTable(){
		return new NgramCountTable();
	}
	
	public Set<String>getRowsSet(){
		return table.rowKeySet();
	}
	
	public void addBeforeToken(String gram,String beforeToken){
		@SuppressWarnings("unchecked")
		Set<String>beforeTokens=(Set<String>) table.get(gram, TOKENS_BEFORE);
		if(beforeTokens==null){
			beforeTokens=Sets.newHashSet();
		}
		beforeTokens.add(beforeToken);
		table.put(gram, TOKENS_BEFORE, beforeTokens);
	}
	
	public void addAfterToken(String gram, String afterToken){
		@SuppressWarnings("unchecked")
		Set<String>afterTokens=(Set<String>) table.get(gram, TOKENS_AFTER);
		if(afterTokens==null){
			afterTokens=Sets.newHashSet();
		}
		afterTokens.add(afterToken);
		table.put(gram, TOKENS_AFTER, afterTokens);
	}
	
	public void incrementCount(String gram){
		Integer count=(Integer) table.get(gram, FREQ);
		if(count==null){
			count=0;
		}
		table.put(gram, FREQ, count+1);
		setOverallCount(getOverallCount() + 1);
	}
	
	public void setScore(String gram,Double score){
		table.put(gram, LLR	, score);
	}
	
	public Double getScore(String gram){
		return (Double) table.get(gram, LLR);
	}
	
	public int getCount(String gram){
		Integer count=(Integer) table.get(gram, FREQ);
		return count!=null?count:0;
	}
	
	public int getNumberOfDifferentBeforeTokens(String gram){
		@SuppressWarnings("unchecked")
		Set<String>beforeTokens=(Set<String>) table.get(gram, TOKENS_BEFORE);
		if(beforeTokens==null){
			beforeTokens=Sets.newHashSet();
		}
		return beforeTokens.size();
	}
	
	public Set<String>getBeforeTokens(String gram){
		@SuppressWarnings("unchecked")
		Set<String>beforeTokens=(Set<String>) table.get(gram, TOKENS_BEFORE);
		if(beforeTokens==null){
			beforeTokens=Sets.newHashSet();
		}
		return beforeTokens;
	}
	
	public int getNumberOfDifferentAfterTokens(String gram){
		@SuppressWarnings("unchecked")
		Set<String>afterTokens=(Set<String>) table.get(gram, TOKENS_AFTER);
		if(afterTokens==null){
			afterTokens=Sets.newHashSet();
		}
		return afterTokens.size();
	}
	
	public Set<String>getAfterTokens(String gram){
		@SuppressWarnings("unchecked")
		Set<String>afterTokens=(Set<String>) table.get(gram, TOKENS_AFTER);
		if(afterTokens==null){
			afterTokens=Sets.newHashSet();
		}
		return afterTokens;
	}
	
	public int getK11(String gram){
		//K11 if the count of A and B together, so the gram count itself (A is the first token, B is the the rest)
		return getCount(gram);
	}

	public int getOverallCount() {
		return overallCount;
	}

	private void setOverallCount(int overallCount) {
		this.overallCount = overallCount;
	}
	
//	public int getK21(String gram){
//		//K21 is A without B
//		String[] parts=gram.split(" ", 2);
//		String a=parts[0];
//		String b=parts[1];
//		
//	}
	
}
