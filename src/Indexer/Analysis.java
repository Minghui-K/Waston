package Indexer;

import java.util.ArrayList;

public class Analysis {

	public static String[] stopwords = {"a", "an", "and", "are", "as", "at", "be", "but", "by",
		      "for", "if", "in", "into", "is", "it",
		      "no", "not", "of", "on", "or", "such",
		      "that", "the", "their", "then", "there", "these",
		      "they", "this", "to", "was", "will", "with"};
	public static ArrayList<String> wordsList;
	
	public static void main(String[] args) {
		ArrayList<Float> t = new ArrayList<>();
		System.out.println(t.get(0) == null);
		//System.out.println(firstSent(null));
	}
	
	public static String removeStopWords(String input) {
		input = input.trim().replaceAll("\\p{Punct}|\\s+", " ").toLowerCase();
        String[] words = input.split(" ");
        wordsList = new ArrayList<String>();
        for (String word : words) {
            wordsList.add(word);
        }
        for (int j = 0; j < stopwords.length; j++) {
            while (wordsList.contains(stopwords[j])) {
                wordsList.remove(stopwords[j]);
            }
        }
        String rev = "";
        for (String str : wordsList) {
            rev += (str + " ");
        }
		return rev;
	}
	
	public static String findInteger(String input) {
		String in = "0 ";
		for (String j : input.split(" ")) {
			if (j.matches("-?\\d+")) {
				in += j + " ";
			}
		}
		return in;
	}
	
	public static String findQuote(String input) {
		boolean found = false;
		String quote = "a ";
		for (String j : input.split(" ")) {
			if (j.length() > 1 && j.charAt(0) == '"') {
				quote += j + " ";
				found = true;
			}
			if (found) {
				quote += j + " ";
				if (j.length() > 1 && j.charAt(j.length()-1) == '"') {
					quote = quote.replaceAll("\\p{Punct}", "").toLowerCase();
					break;
				}
			}
		}
		return quote;
	}
	
	public static String firstSent(String input) {
		int i = 0;
		while (i < input.length()) {
			if (input.substring(i, i+1).equals(".")) return input.substring(0, i+1);
			i++;
		}
		return "";
	}
}