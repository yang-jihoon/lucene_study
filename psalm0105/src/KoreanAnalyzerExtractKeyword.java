import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;

public class KoreanAnalyzerExtractKeyword {
	final static Logger logger = LoggerFactory.getLogger(KoreanAnalyzerExtractKeyword.class);
	
	public static void main(String[] args) throws IOException {
		Analyzer a = new KoreanAnalyzer(true);
		TokenStream ts = a.tokenStream("not use", new StringReader("아버지가방에들어가신다"));
		ts.addAttribute(CharTermAttribute.class);
		
		 while(ts.incrementToken()) {
			 CharTermAttribute charterm = ts.getAttribute(CharTermAttribute.class);	    	
			 logger.info(charterm.toString());
		 }
	}
}
