import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;


public class SearchFiles {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String index = "E:\\lucene_index";
		String field = "contents";
		String queryString1 = "string";
		String queryString2 = "int";
		int hitsPerPage = 10;
				
		try {
			Directory dir = FSDirectory.open(new File(index));
			IndexReader ir = IndexReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(ir);
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
			QueryParser parser = new QueryParser(Version.LUCENE_31, field, analyzer);
			
			Query query1 = parser.parse(queryString1);
			Query query2 = parser.parse(queryString2);
			
			BooleanQuery bq = new BooleanQuery();
			bq.add(query1, Occur.MUST);
			bq.add(query2, Occur.MUST);
						
		    System.out.println("Query String : " + queryString1);
		    System.out.println("Query String : " + queryString2);
		    System.out.println("Query : " + bq.toString());
		    System.out.println("Searching for: " + bq.toString(field));
		    
		    TopDocs results = searcher.search(bq, 5 * hitsPerPage);
		    ScoreDoc[] hits = results.scoreDocs;

		    int numTotalHits = results.totalHits;
		    System.out.println(numTotalHits + " total matching documents");
		    
		    for (int i = 0; i < hits.length; i++) {
		    	System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
		    	
		    	Document doc = searcher.doc(hits[i].doc);
		        String path = doc.get("full_path");
		        if (path != null) {
		          System.out.println((i+1) + ". " + path);
		        } else {
		          System.out.println((i+1) + ". " + "No path for this document");
		        }
		    }

		    searcher.close();
		} catch (CorruptIndexException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
