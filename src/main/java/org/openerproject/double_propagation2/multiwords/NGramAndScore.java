package org.openerproject.double_propagation2.multiwords;

public class NGramAndScore implements Comparable<NGramAndScore> {

	private String gram;
	private double score;
	private int numberOfBeforeTokensForSecondPart;
	private int numberOfAfterTokensForFirstPart;

	public NGramAndScore(String gram, double score) {
		super();
		this.gram = gram;
		this.score = score;
	}

	public NGramAndScore(String gram, double score, 
			int numberOfAfterTokensForFirstPart,int numberOfBeforeTokensForSecondPart) {
		super();
		this.gram = gram;
		this.score = score;
		this.numberOfBeforeTokensForSecondPart = numberOfBeforeTokensForSecondPart;
		this.numberOfAfterTokensForFirstPart = numberOfAfterTokensForFirstPart;
	}

	public String getGram() {
		return gram;
	}

	public void setGram(String gram) {
		this.gram = gram;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int getNumberOfBeforeTokensForSecondPart() {
		return numberOfBeforeTokensForSecondPart;
	}

	public void setNumberOfBeforeTokensForSecondPart(int numberOfBeforeTokensForSecondPart) {
		this.numberOfBeforeTokensForSecondPart = numberOfBeforeTokensForSecondPart;
	}

	public int getNumberOfAfterTokensForFirstPart() {
		return numberOfAfterTokensForFirstPart;
	}

	public void setNumberOfAfterTokensForFirstPart(int numberOfAfterTokensForFirstPart) {
		this.numberOfAfterTokensForFirstPart = numberOfAfterTokensForFirstPart;
	}

	@Override
	public int compareTo(NGramAndScore o) {
		if (o.getScore() == score) {
			return gram.compareTo(o.getGram());
		} else if (o.getScore() > score) {
			return 1;
		} else {
			return -1;
		}
	}

	@Override
	public String toString() {
		return "NGramAndScore [gram=" + gram + ", score=" + score + ", firstTokenCombinations="+numberOfAfterTokensForFirstPart+", secondPartCombinations="+numberOfBeforeTokensForSecondPart+"]  -> " + gram.replaceAll("_[A-Z,\\.]+", "");
	}

}
