import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;


public class SearchFiles {
	final static Logger logger = LoggerFactory.getLogger(SearchFiles.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String index = "d:\\lucene_index";
		int hitsPerPage = 10;
				
		try {
			Directory dir = FSDirectory.open(new File(index));
			IndexReader ir = IndexReader.open(dir);
			IndexSearcher searcher = new IndexSearcher(ir);
			Analyzer analyzer = new KoreanAnalyzer(true);

			BooleanQuery bq = new BooleanQuery();
			Query query1 = makeQueryHW1(analyzer);	
			Query query4 = makeQueryHW4(analyzer);			

			bq.add(query1, Occur.MUST);
			bq.add(query4, Occur.MUST);
			
		    logger.info("Query : " + bq.toString());
		    
		    TopDocs results = searcher.search(bq, 10 * hitsPerPage);
		    ScoreDoc[] hits = results.scoreDocs;

		    int numTotalHits = results.totalHits;
		    logger.info(numTotalHits + " total matching documents");
		    
		    for (int i = 0; i < hits.length; i++) {
		    	logger.info((i+1) + ". "+ "doc="+hits[i].doc+" score="+hits[i].score);
		    	
		    	Document doc = searcher.doc(hits[i].doc);
		        String path = doc.get("full_path");
		        if (path != null) {
		          logger.info((i+1) + ". "+ "full_path : "  + path);
		          logger.info((i+1) + ". "+ "modified : " + doc.get("modified"));
		        } else {
		          logger.info((i+1) + ". "+ ". " + "No path for this document");
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
	
	//제목에 java가 있고, 내용에 main이 있는 파일 검색
	private static Query makeQueryHW1(Analyzer analyzer) throws ParseException {
		String fileName = "file_name";
		String contents = "contents";
		String queryString1 = "java";
		String queryString2 = "main";
		
		QueryParser parser1 = new QueryParser(Version.LUCENE_31, fileName, analyzer);
		QueryParser parser2 = new QueryParser(Version.LUCENE_31, contents, analyzer);
		
		Query query1 = parser1.parse(queryString1);
		Query query2 = parser2.parse(queryString2);
		
		BooleanQuery bq = new BooleanQuery();
		bq.add(query1, Occur.MUST);
		bq.add(query2, Occur.MUST);
		return bq;
	}
	
	//제목에 java가 있고, 내용에 main이 없는 파일 검색
	private static Query makeQueryHW2(Analyzer analyzer) throws ParseException {
		String fileName = "file_name";
		String contents = "contents";
		String queryString1 = "java";
		String queryString2 = "main";
		
		QueryParser parser1 = new QueryParser(Version.LUCENE_31, fileName, analyzer);
		QueryParser parser2 = new QueryParser(Version.LUCENE_31, contents, analyzer);
		
		Query query1 = parser1.parse(queryString1);
		Query query2 = parser2.parse(queryString2);
		
		BooleanQuery bq = new BooleanQuery();
		bq.add(query1, Occur.MUST);
		bq.add(query2, Occur.MUST_NOT);
		return bq;
	}
	
	//제목에 java, file이 있고, 내용에 main이 있는 파일 검색
	private static Query makeQueryHW3(Analyzer analyzer) throws ParseException {
		String fileName = "file_name";
		String contents = "contents";
		String queryString1 = "java";
		String queryString1_1 = "file";
		String queryString2 = "main";
		
		QueryParser parser1 = new QueryParser(Version.LUCENE_31, fileName, analyzer);
		QueryParser parser2 = new QueryParser(Version.LUCENE_31, contents, analyzer);
		
		Query query1 = parser1.parse(queryString1);
		Query query1_1 = parser1.parse(queryString1_1);
		Query query2 = parser2.parse(queryString2);
		
		BooleanQuery bq = new BooleanQuery();
		bq.add(query1, Occur.MUST);
		bq.add(query1_1, Occur.MUST);
		bq.add(query2, Occur.MUST);
		return bq;
	}
	
	//파일 수정일이 20121001 ~ 20121231 사이인 파일 검색
	private static Query makeQueryHW4(Analyzer analyzer) throws ParseException {
		
		String modified = "modified";
		String queryString1 = "20121001000000";
		String queryString2 = "20121231235959";
				
		Query q = NumericRangeQuery.newLongRange(modified, Long.parseLong(queryString1), Long.parseLong(queryString2), true, true);	
		return q;
	}

}
