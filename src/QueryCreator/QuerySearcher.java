package QueryCreator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.store.FSDirectory;

import Indexer.Analysis;
import edu.stanford.nlp.simple.Sentence;


public class QuerySearcher {
	
	private static HashMap<String, ArrayList<Float>> GloVe = new HashMap<>();
    
	public static void main(String[] args) throws IOException {	
		System.out.println("Begin dafault search with only stopword & stemming...");
		stemming_stop(false);
		
		System.out.println("Begin dafault search with only stopword...");
		stopword(false);
		System.out.println("Begin dafault search with only lemmatization...");
		lemma(false);
		System.out.println("Begin dafault search with only stopword & lemmatization...");
		lemma_stop(false);
		System.out.println("Begin dafault search with stopword & stemming & lemmatization...");
		all(false);
		System.out.println("Begin TF-IDF similarity search with only stopword...");
		stopword(true);
		System.out.println("Begin TF-IDF similarity search with only lemmanization...");
		lemma(true);
		System.out.println("Begin TF-IDF similarity search with only stopword & lemmatization...");
		lemma_stop(true);
		System.out.println("Begin TF-IDF similarity searcg with only stopword & stemming...");
		stemming_stop(true);
		System.out.println("Begin TF-IDF similarity search with stopword & stemming & lemmatization...");
		all(true);
		System.out.println("Begin default search with lemmatization && part of speech");
		improve();
		/*
		makeGloVeModel();
		System.out.println("Begin embedding GloVe seach...");
		embedding();
		*/
	}
	
	/*
	 * Q5 improving with part of speech
	 */
	private static void improve() throws IOException {
		Scanner sc = new Scanner(new File("src/QueryCreator/questions.txt"));
		int count = 0, total = 0, top = 0;
		double score = 0;
		while (sc.hasNext()) {
			total++;
			String category = sc.nextLine().replaceAll("\\p{Punct}", "").toLowerCase();
			String question = Analysis.removeStopWords(sc.nextLine().replaceAll("\\p{Punct}", ""));
			Sentence sent = new Sentence(question);
			List<String> lemma = sent.lemmas();
			List<String> pos = sent.posTags();
			//List<String> ner = sent.nerTags();
			String temp = " ";
			for (int i = 0; i<lemma.size(); i++) {
				temp += lemma.get(i) + "-" + pos.get(i) + " ";
			}
			question = temp.toLowerCase();
			String[] query = new String[] {question, category, "0 "};
			String answer= sc.nextLine();
			sc.nextLine(); // empty;
			int num = 1;
			for (String i : search(new WhitespaceAnalyzer(), query, "Index/improve", false)) {
				if (i.equals(answer)) {
					if (num == 1) top++;
					score += 1 / (double) num;
					count++;
					break;
				}
				num++;
			}
		}
		sc.close();
		System.out.println("top1  Result: " + top + "/" + total);
		System.out.println("top10 Result: " + count + "/" + total);
		System.out.println("   MRR Score: " + score);
		System.out.println("-----------------------------------\n");
		
	}
	
	/*
	 * lemmatization
	 */
	private static void lemma(boolean cos) throws IOException {
		Scanner sc = new Scanner(new File("src/QueryCreator/questions.txt"));
		int count = 0, total = 0, top = 0;
		double score = 0;
		while (sc.hasNext()) {
			total++;
			String category = sc.nextLine().replaceAll("\\p{Punct}", "").toLowerCase();
			String question = sc.nextLine().replaceAll("\\p{Punct}", "");
			String in = Analysis.findInteger(question);
			String temp = " ";
			Sentence sent = new Sentence(question);
			for (String str : sent.lemmas()) {
				temp += str + " ";
			}
			question = temp;
			String[] query = new String[] {question, category, in};
			String answer= sc.nextLine();
			sc.nextLine(); // empty;
			int num = 1;
			for (String i : search(new WhitespaceAnalyzer(), query, "Index/lemma", cos)) {
				if (i.equals(answer)) {
					if (num == 1) top++;
					score += 1 / (double) num;
					count++;
					break;
				}
				num++;
			}
		}
		sc.close();
		System.out.println("top1  Result: " + top + "/" + total);
		System.out.println("top10 Result: " + count + "/" + total);
		System.out.println("   MRR Score: " + score);
		System.out.println("-----------------------------------\n");
	}
	
	/*
	 * lemmatization and removing stopwords
	 */
	private static void lemma_stop(boolean cos) throws IOException {
		Scanner sc = new Scanner(new File("src/QueryCreator/questions.txt"));
		int count = 0, total = 0, top = 0;
		double score = 0;
		while (sc.hasNext()) {
			total++;
			String category = sc.nextLine().replaceAll("\\p{Punct}", "").toLowerCase();
			String question = sc.nextLine().replaceAll("\\p{Punct}", "");
			String in = Analysis.findInteger(question);
			String temp = " ";
			Sentence sent = new Sentence(question);
			for (String str : sent.lemmas()) {
				temp += str + " ";
			}
			question = temp;
			String[] query = new String[] {question, category, in};
			String answer= sc.nextLine();
			sc.nextLine(); // empty;
			int num = 1;
			for (String i : search(new StandardAnalyzer(), query, "Index/lemma_stop", cos)) {
				if (i.equals(answer)) {
					if (num == 1) top++;
					score += 1 / (double) num;
					count++;
					break;
				}
				num++;
			}
		}
		sc.close();
		System.out.println("top1  Result: " + top + "/" + total);
		System.out.println("top10 Result: " + count + "/" + total);
		System.out.println("   MRR Score: " + score);
		System.out.println("-----------------------------------\n");
	}
	
	/*
	 * removing stopwords
	 */
	private static void stopword(boolean cos) throws IOException {
		Scanner sc = new Scanner(new File("src/QueryCreator/questions.txt"));
		int count = 0, total = 0, top = 0;
		double score = 0;
		while (sc.hasNext()) {
			total++;
			String category = sc.nextLine().replaceAll("\\p{Punct}", "").toLowerCase();
			String question = sc.nextLine().replaceAll("\\p{Punct}", "");
			String in = Analysis.findInteger(question);
			String[] query = new String[] {question, category, in};
			String answer= sc.nextLine();
			sc.nextLine(); // empty;
			int num = 1;
			for (String i : search(new StandardAnalyzer(), query, "Index/stopword", cos)) {
				if (i.equals(answer)) {
					if (num == 1) top++;
					score += 1 / (double) num;
					count++;
					break;
				}
				num++;
			}
		}
		sc.close();
		System.out.println("top1  Result: " + top + "/" + total);
		System.out.println("top10 Result: " + count + "/" + total);
		System.out.println("   MRR Score: " + score);
		System.out.println("-----------------------------------\n");
	}
	
	/*
	 * stemming and removing stopwords
	 */
	private static void stemming_stop(boolean cos) throws IOException {
		Scanner sc = new Scanner(new File("src/QueryCreator/questions.txt"));
		int count = 0, total = 0, top = 0;
		double score = 0;
		while (sc.hasNext()) {
			total++;
			String category = sc.nextLine().replaceAll("\\p{Punct}", "").toLowerCase();
			String question = sc.nextLine().replaceAll("\\p{Punct}", "");
			String in = Analysis.findInteger(question);
			String[] query = new String[] {question, category, in};
			String answer= sc.nextLine();
			sc.nextLine(); // empty;
			int num = 1;
			for (String i : search(new EnglishAnalyzer(), query, "Index/stemming", cos)) {
				if (i.equals(answer)) {
					if (num == 1) top++;
					score += 1 / (double) num;
					count++;
					break;
				}
				num++;
			}
		}
		sc.close();
		System.out.println("top1  Result: " + top + "/" + total);
		System.out.println("top10 Result: " + count + "/" + total);
		System.out.println("   MRR Score: " + score);
		System.out.println("-----------------------------------\n");
	}
	
	/*
	 * lemmatization, stemming, removing stopwords
	 */
	private static void all(boolean cos) throws IOException {
		Scanner sc = new Scanner(new File("src/QueryCreator/questions.txt"));
		int count = 0, total = 0, top = 0;
		double score = 0;
		while (sc.hasNext()) {
			total++;
			String category = sc.nextLine().replaceAll("\\p{Punct}", "").toLowerCase();
			String question = sc.nextLine().replaceAll("\\p{Punct}", "");
			String in = Analysis.findInteger(question);
			String temp = " ";
			Sentence sent = new Sentence(question);
			for (String str : sent.lemmas()) {
				temp += str + " ";
			}
			question = temp;
			String[] query = new String[] {question, category, in};
			String answer= sc.nextLine();
			sc.nextLine(); // empty;
			int num = 1;
			for (String i : search(new EnglishAnalyzer(), query, "Index/stem_lemm_stop", cos)) {
				if (i.equals(answer)) {
					if (num == 1) top++;
					score += 1 / (double) num;
					count++;
					break;
				}
				num++;
			}
		}
		sc.close();
		System.out.println("top1  Result: " + top + "/" + total);
		System.out.println("top10 Result: " + count + "/" + total);
		System.out.println("   MRR Score: " + score);
		System.out.println("-----------------------------------\n");
	}
	
	/*
	 * Q6 embedding GloVe word to rerank.
	 */
	private static void embedding() throws IOException {
		Scanner sc = new Scanner(new File("src/QueryCreator/questions.txt"));
		int count = 0, total = 0, top = 0;
		double score = 0;
		while (sc.hasNext()) {
			total++;
			System.out.println("Quention "+total);
			String category = sc.nextLine().replaceAll("\\p{Punct}", "").toLowerCase();
			String question = sc.nextLine().replaceAll("\\p{Punct}", "");
			String in = Analysis.findInteger(question);
			String[] query = new String[] {question, category, in};
			String answer= sc.nextLine();
			sc.nextLine(); // empty;
			int num = 1;
			for (String i : searchWithEmbed(new WhitespaceAnalyzer(), query, "Index/firstSentence")) {
				if (i.equals(answer)) {
					if (num == 1) top++;
					score += 1 / (double) num;
					count++;
					break;
				}
				num++;
			}
		}
		sc.close();
		System.out.println("top1  Result: " + top + "/" + total);
		System.out.println("top10 Result: " + count + "/" + total);
		System.out.println("   MRR Score: " + score);
		System.out.println("-----------------------------------\n");
	}
	
	/*
	 * search with embedding words
	 */
	private static List<String> searchWithEmbed(Analyzer analyzer,String[] query, String path) throws IOException {
		Query q1 = null, q2 = null, q3 = null;
		try {
			q1 = new QueryParser("content", analyzer).parse(query[0]);
			q2 = new QueryParser("category", analyzer).parse(query[1]);
			q3 = new QueryParser("integer", analyzer).parse(query[2]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		booleanQuery.add(q1, BooleanClause.Occur.MUST);
		booleanQuery.add(q2, BooleanClause.Occur.SHOULD);
		booleanQuery.add(q3, BooleanClause.Occur.SHOULD);
		
		
        int hitsPerPage = 100;
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(path)));
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(booleanQuery.build(), hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        
        List<Document> list = new ArrayList<>();
        for(ScoreDoc doc : hits) {
            int docId = doc.doc;
            Document document = searcher.doc(docId);
            list.add(document);
        }
        reader.close();
        return similarity(list, query[0]);
	}

	/*
	 * create vectors into map by the GloVe file.
	 */
	private static void makeGloVeModel() throws FileNotFoundException {
		Scanner sc = new Scanner(new File("glove.840B.300d.10f.txt"), "UTF-8");
		int num = 0;
		while (sc.hasNext()) {
			System.out.println(num++);
			String line = sc.nextLine();
			String[] arr = line.split(" ");
			ArrayList<Float> list = new ArrayList<>(); 
			GloVe.put(arr[0], list);
			for (int i = 1; i < arr.length; i++) {
				list.add(Float.parseFloat(arr[i]));
			}
		}
		System.out.println("done");
	}
	
	/*
	 * average the vectors
	 */
	private static ArrayList<Float> average(String input){
		ArrayList<Float> list = new ArrayList<>();
		for (int i = 0; i < 300; i++) {
			list.add((float) 0);
		}
		for (String str : input.split(" ")) {
			ArrayList<Float> vectors = GloVe.get(str);
			if (vectors == null) {
				vectors = GloVe.get("");
			}
			for (int i = 0; i < vectors.size(); i++) {
				list.set(i, list.get(i)+vectors.get(i));
			}
		}
		// average
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i)/input.split(" ").length);
		}
		return list;
	}
	
	/*
	 * get the cos similarity of two vectors.
	 */
	private static float cos(List<Float> a, List<Float> b) {
		float ab = 0;
		float a_ = 0;
		float b_ = 0;
		for (int i = 0; i < a.size(); i++) {
			ab += a.get(i)*b.get(i);
			a_ += Math.pow(a.get(i), 2);
			b_ += Math.pow(b.get(i), 2);
		}
		return (float) (ab / (Math.sqrt(a_) * Math.sqrt(b_)));
	}
	
	/*
	 * check the similarity and return the rank.
	 */
	private static ArrayList<String> similarity(List<Document> list, String query) {
		Map<String, Float> map = new TreeMap<>();
		ArrayList<Float> queryVectors = average(query.toLowerCase());
		for (Document doc : list) {
			ArrayList<Float> docVectors = average(doc.get("content").toLowerCase());
			map.put(doc.get("title"), cos(queryVectors, docVectors));
		}
		
        List<Map.Entry<String, Float>> temp = new ArrayList<Map.Entry<String, Float>>(map.entrySet());
        Collections.sort(temp,new Comparator<Map.Entry<String, Float>>() {
			@Override
			public int compare(Entry<String, Float> o1, Entry<String, Float> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
        });
        ArrayList<String> rev = new ArrayList<String>();
        for(Map.Entry<String, Float> mapping : temp){
        	rev.add(mapping.getKey());
        }
        return rev;
	}

	/*
	 * Normal search
	 */
	private static List<String> search(Analyzer analyzer,String[] query, String path, boolean cos) throws IOException{
		Query q1 = null, q2 = null, q3 = null;
		try {
			q1 = new QueryParser("content", analyzer).parse(query[0]);
			q2 = new QueryParser("category", analyzer).parse(query[1]);
			q3 = new QueryParser("integer", analyzer).parse(query[2]);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
		booleanQuery.add(q1, BooleanClause.Occur.MUST);
		booleanQuery.add(q2, BooleanClause.Occur.SHOULD);
		booleanQuery.add(q3, BooleanClause.Occur.SHOULD);
		
		
        int hitsPerPage = 10;
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(path)));
        IndexSearcher searcher = new IndexSearcher(reader);
        if (cos) searcher.setSimilarity(new MySimilarity());
        TopDocs docs = searcher.search(booleanQuery.build(), hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;
        
        List<String> ans = new ArrayList<String>();
        for(ScoreDoc doc : hits) {
            int docId = doc.doc;
            Document document = searcher.doc(docId);
            ans.add(document.get("title"));
        }
        reader.close();
        return ans;
    }
	
	// create a similarity class that override the score method with tf-idf.
    private static class MySimilarity extends SimilarityBase {

        @Override
        protected float score(BasicStats stats, float termFreq, float docLength) {
        	
            double tf = 1 + (Math.log(termFreq));
            double idf = Math.log((stats.getNumberOfDocuments() + 1) / stats.getDocFreq());
            float score = (float) ((tf * idf)/docLength); // because query will constant length, just consider docLength.
            return score;
        }
        
		@Override
		public String toString() {
			return null;
		}
    }

}
