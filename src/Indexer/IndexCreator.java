package Indexer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import edu.stanford.nlp.simple.*;

public class IndexCreator {

	/*
	 * Creates different index by user input.
	 * 
	 * args[0]:  The index you want to create
	 * args[1]:  The wiki page file address
	 * args[2]:	 The index address where you want to put
	 */
	public static void main(String[] args) throws IOException {
		if (args[0].equals("lemma")) lemma(args[1], args[2]);
		else if (args[0].equals("stopword")) stopword(args[1], args[2]);
		else if (args[0].equals("stemming")) stemming_stop(args[1], args[2]);
		else if (args[0].equals("lemma_stop")) lemma_stop(args[1], args[2]);
		else if (args[0].equals("improve")) improve(args[1], args[2]);
		else if (args[0].equals("first_sentence")) first_Sentence(args[1], args[2]);
		else all(args[1], args[2]);
	}
	
	/*
	 * Add document into the index
	 */
	private static void addDoc(IndexWriter w, Wiki wiki) throws IOException {
		Document doc = new Document();
		doc.add(new StringField("title", wiki.title, Field.Store.YES));
		doc.add(new TextField("category", wiki.category, Field.Store.YES));
		doc.add(new TextField("content", wiki.content, Field.Store.YES));
		doc.add(new TextField("integer", wiki.Integer, Field.Store.YES));
		w.addDocument(doc);
	}
	
	/*
	 * simple lemmatization indexing.
	 */
	private static void lemma(String arg, String path) throws IOException {
		File directory = new File(arg);
		File[] contents = directory.listFiles();
		WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
		FSDirectory dir = FSDirectory.open(Paths.get(path));
	    IndexWriterConfig fig = new IndexWriterConfig(analyzer);
	    IndexWriter writer = new IndexWriter(dir, fig);
	    int nums = 1;
		for (File file : contents) {
			if (file.getName().substring(0,1).equals("e")) {
				System.out.println("begin " + nums + " files");
				nums++;
				Scanner sc = new Scanner(file, "UTF-8");
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.length() > 1 && line.substring(0, 2).equals("[[")) {
						String title = line.substring(2, line.length()-2);
						Wiki document = new Wiki();
						document.title = title;
						boolean blankLine = false;
						while (sc.hasNextLine()) {
							line = sc.nextLine();
							if (!line.isBlank() && line.length() > 0) {
								blankLine = false;
								if (line.length() > 10 && line.substring(0, 10).equals("CATEGORIES")) {
									String catagory = line.substring(11);
									document.category = catagory.toLowerCase();
								} else if (line.length() > 1 && line.substring(0, 2).equals("==")) {
									continue;
								} else {
									Sentence sent;
									try {
										sent = new Sentence(line);
									} catch (Exception e) {
										continue;
									}
									String temp = "";
									for (String str : sent.lemmas()) {
										temp += str + " ";
									}
									document.content += temp;
									document.Integer += Analysis.findInteger(line);
								}
							} else {
								if (blankLine) break;
								blankLine = true;
							}
						}
						addDoc(writer,document);
					}
				}
				sc.close();
			}
		}
		writer.close();
	}
	
	/*
	 * simple removing stopwords indexing.
	 */
	private static void stopword(String arg, String path) throws IOException {
		File directory = new File(arg);
		File[] contents = directory.listFiles();
		StandardAnalyzer analyzer = new StandardAnalyzer();
		FSDirectory dir = FSDirectory.open(Paths.get(path));
	    IndexWriterConfig fig = new IndexWriterConfig(analyzer);
	    IndexWriter writer = new IndexWriter(dir, fig);
	    int nums = 1;
		for (File file : contents) {
			if (file.getName().substring(0,1).equals("e")) {
				System.out.println("begin " + nums + " files");
				nums++;
				Scanner sc = new Scanner(file, "UTF-8");
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.length() > 1 && line.substring(0, 2).equals("[[")) {
						String title = line.substring(2, line.length()-2);
						Wiki document = new Wiki();
						document.title = title;
						boolean blankLine = false;
						while (sc.hasNextLine()) {
							line = sc.nextLine();
							if (!line.isBlank() && line.length() > 0) {
								blankLine = false;
								if (line.length() > 10 && line.substring(0, 10).equals("CATEGORIES")) {
									String catagory = line.substring(11);
									document.category = catagory.toLowerCase();
								} else if (line.length() > 1 && line.substring(0, 2).equals("==")) {
									continue;
								} else {
									document.content += line;
									document.Integer += Analysis.findInteger(line);
								}
							} else {
								if (blankLine) break;
								blankLine = true;
							}
						}
						addDoc(writer,document);
					}
				}
				sc.close();
			}
		}
		writer.close();
	}
	
	/*
	 * !!!BEST!!!
	 * stemming and removing stopwords indexing. 
	 */
	private static void stemming_stop(String arg, String path) throws IOException {
		File directory = new File(arg);
		File[] contents = directory.listFiles();
		EnglishAnalyzer analyzer = new EnglishAnalyzer();
		FSDirectory dir = FSDirectory.open(Paths.get(path));
	    IndexWriterConfig fig = new IndexWriterConfig(analyzer);
	    IndexWriter writer = new IndexWriter(dir, fig);
	    int nums = 1;
		for (File file : contents) {
			if (file.getName().substring(0,1).equals("e")) {
				System.out.println("begin " + nums + " files");
				nums++;
				Scanner sc = new Scanner(file, "UTF-8");
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.length() > 1 && line.substring(0, 2).equals("[[")) {
						String title = line.substring(2, line.length()-2);
						Wiki document = new Wiki();
						document.title = title;
						boolean blankLine = false;
						while (sc.hasNextLine()) {
							line = sc.nextLine();
							if (!line.isBlank() && line.length() > 0) {
								blankLine = false;
								if (line.length() > 10 && line.substring(0, 10).equals("CATEGORIES")) {
									String catagory = line.substring(11);
									document.category = catagory.toLowerCase();
								} else if (line.length() > 1 && line.substring(0, 2).equals("==")) {
									continue;
								} else {
									document.content += line;
									document.Integer += Analysis.findInteger(line);
								}
							} else {
								if (blankLine) break;
								blankLine = true;
							}
						}
						addDoc(writer,document);
					}
				}
				sc.close();
			}
		}
		writer.close();
	}
	
	/*
	 * lemmatization and remmoving stopwords indexing.
	 */
	private static void lemma_stop(String arg, String path) throws IOException {
		File directory = new File(arg);
		File[] contents = directory.listFiles();
		StandardAnalyzer analyzer = new StandardAnalyzer();
		FSDirectory dir = FSDirectory.open(Paths.get(path));
	    IndexWriterConfig fig = new IndexWriterConfig(analyzer);
	    IndexWriter writer = new IndexWriter(dir, fig);
	    int nums = 1;
		for (File file : contents) {
			if (file.getName().substring(0,1).equals("e")) {
				System.out.println("begin " + nums + " files");
				nums++;
				Scanner sc = new Scanner(file, "UTF-8");
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.length() > 1 && line.substring(0, 2).equals("[[")) {
						String title = line.substring(2, line.length()-2);
						Wiki document = new Wiki();
						document.title = title;
						boolean blankLine = false;
						while (sc.hasNextLine()) {
							line = sc.nextLine();
							if (!line.isBlank() && line.length() > 0) {
								blankLine = false;
								if (line.length() > 10 && line.substring(0, 10).equals("CATEGORIES")) {
									String catagory = line.substring(11);
									document.category = catagory.toLowerCase();
								} else if (line.length() > 1 && line.substring(0, 2).equals("==")) {
									continue;
								} else {
									Sentence sent;
									try {
										sent = new Sentence(line);
									} catch (Exception e) {
										continue;
									}
									String temp = "";
									for (String str : sent.lemmas()) {
										temp += str + " ";
									}
									document.content += temp;
									document.Integer += Analysis.findInteger(line);
								}
							} else {
								if (blankLine) break;
								blankLine = true;
							}
						}
						addDoc(writer,document);
					}
				}
				sc.close();
			}
		}
		writer.close();
	}
	
	/*
	 * Using stopwords, stemming and lemmatization to index.
	 */
	private static void all(String arg, String path) throws IOException {
		File directory = new File(arg);
		File[] contents = directory.listFiles();
		EnglishAnalyzer analyzer = new EnglishAnalyzer();
		FSDirectory dir = FSDirectory.open(Paths.get(path));
	    IndexWriterConfig fig = new IndexWriterConfig(analyzer);
	    IndexWriter writer = new IndexWriter(dir, fig);
	    int nums = 1;
		for (File file : contents) {
			if (file.getName().substring(0,1).equals("e")) {
				System.out.println("begin " + nums + " files");
				nums++;
				Scanner sc = new Scanner(file, "UTF-8");
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.length() > 1 && line.substring(0, 2).equals("[[")) {
						String title = line.substring(2, line.length()-2);
						Wiki document = new Wiki();
						document.title = title;
						boolean blankLine = false;
						while (sc.hasNextLine()) {
							line = sc.nextLine();
							if (!line.isBlank() && line.length() > 0) {
								blankLine = false;
								if (line.length() > 10 && line.substring(0, 10).equals("CATEGORIES")) {
									String catagory = line.substring(11);
									document.category = catagory.toLowerCase();
								} else if (line.length() > 1 && line.substring(0, 2).equals("==")) {
									continue;
								} else {
									Sentence sent;
									try {
										sent = new Sentence(line);
									} catch (Exception e) {
										continue;
									}
									String temp = "";
									for (String str : sent.lemmas()) {
										temp += str + " ";
									}
									document.content += temp;
									document.Integer += Analysis.findInteger(line);
								}
							} else {
								if (blankLine) break;
								blankLine = true;
							}
						}
						addDoc(writer,document);
					}
				}
				sc.close();
			}
		}
		writer.close();
	}
	
	/*
	 * Q5 improving with part of speech with stemming and removing stopwords.
	 */
	private static void improve(String arg, String path) throws IOException {
		File directory = new File(arg);
		File[] contents = directory.listFiles();
		EnglishAnalyzer analyzer = new EnglishAnalyzer();
		FSDirectory dir = FSDirectory.open(Paths.get(path));
	    IndexWriterConfig fig = new IndexWriterConfig(analyzer);
	    IndexWriter writer = new IndexWriter(dir, fig);
	    int nums = 1;
		for (File file : contents) {
			if (file.getName().substring(0,1).equals("e")) {
				System.out.println("begin " + nums + " files");
				nums++;
				Scanner sc = new Scanner(file, "UTF-8");
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.length() > 1 && line.substring(0, 2).equals("[[")) {
						String title = line.substring(2, line.length()-2);
						Wiki document = new Wiki();
						document.title = title;
						boolean blankLine = false;
						while (sc.hasNextLine()) {
							line = sc.nextLine();
							if (!line.isBlank() && line.length() > 0) {
								blankLine = false;
								if (line.length() > 10 && line.substring(0, 10).equals("CATEGORIES")) {
									String catagory = line.substring(11);
									document.category = catagory.toLowerCase();
								} else if (line.length() > 1 && line.substring(0, 2).equals("==")) {
									continue;
								} else {
									line = Analysis.removeStopWords(line);
									Sentence sent;
									try {
										sent = new Sentence(line);
									} catch (Exception e) {
										continue;
									}
									List<String> lemma = sent.lemmas();
									List<String> pos = sent.posTags();
									//List<String> ner = sent.nerTags();
									String temp = "";
									for (int i = 0; i<lemma.size(); i++) {
										temp += lemma.get(i) + "-" + pos.get(i) + " ";
									}
									document.content += temp;
								}
							} else {
								if (blankLine) break;
								blankLine = true;
							}
						}
						addDoc(writer,document);
					}
				}
				sc.close();
			}
		}
		writer.close();
	}
	
	/*
	 * Only index the first sentence in Wikipedia page with lemmatization and removing stop words.
	 */
	private static void first_Sentence(String arg, String path) throws IOException{
		File directory = new File(arg);
		File[] contents = directory.listFiles();
		WhitespaceAnalyzer analyzer = new WhitespaceAnalyzer();
		FSDirectory dir = FSDirectory.open(Paths.get(path));
	    IndexWriterConfig fig = new IndexWriterConfig(analyzer);
	    IndexWriter writer = new IndexWriter(dir, fig);
	    int nums = 1;
		for (File file : contents) {
			if (file.getName().substring(0,1).equals("e")) {
				System.out.println("begin " + nums + " files");
				nums++;
				Scanner sc = new Scanner(file, "UTF-8");
				while (sc.hasNextLine()) {
					String line = sc.nextLine();
					if (line.length() > 1 && line.substring(0, 2).equals("[[")) {
						boolean done = false;
						String title = line.substring(2, line.length()-2);
						Wiki document = new Wiki();
						document.title = title;
						boolean blankLine = false;
						while (sc.hasNextLine()) {
							line = sc.nextLine();
							if (!line.isBlank() && line.length() > 0) {
								blankLine = false;
								if (line.length() > 10 && line.substring(0, 10).equals("CATEGORIES")) {
									String catagory = line.substring(11);
									document.category = catagory.toLowerCase();
								} else if (line.length() > 1 && line.substring(0, 2).equals("==")) {
									continue;
								} else {
									if (!done) {
										Sentence sent;
										try {
											sent = new Sentence(Analysis.firstSent(Analysis.removeStopWords(line)).replaceAll("\\p{Punct}", ""));
										} catch (Exception e) {
											continue;
										}
										String temp = "";
										for (String str : sent.lemmas()) {
											temp += str + " ";
										}
										document.content += temp;
										document.Integer += Analysis.findInteger(line);
										done = true;
									}
								}
							} else {
								if (blankLine) break;
								blankLine = true;
							}
						}
						addDoc(writer,document);
					}
				}
				sc.close();
			}
		}
		writer.close();
	}
	
}