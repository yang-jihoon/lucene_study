import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.NumericField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tistory.devyongsik.analyzer.KoreanAnalyzer;


public class IndexFiles {
	
	final static Logger logger = LoggerFactory.getLogger(IndexFiles.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String docsPath = "d:\\workspace";
		String indexPath = "d:\\lucene_index";
		
	    Date start = new Date();
		
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			Analyzer analyzer = new KoreanAnalyzer(true);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);
			
			iwc.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(dir, iwc);
			
			final File docDir = new File(docsPath);
			indexDocs(writer, docDir);

		    writer.close();
		    
            Date end = new Date();
            logger.info(end.getTime() - start.getTime() + " total milliseconds");

		} catch (Exception e) {
        	logger.error(e.toString());  
		}
	}

	static void indexDocs(IndexWriter writer, File file)throws IOException {
    if (file.canRead()) {

      if (file.isDirectory()) {
        String[] files = file.list();
        if (files != null) {
          for (int i = 0; i < files.length; i++) {
            indexDocs(writer, new File(file, files[i]));
          }
        }
      } else {
    	if (file.getName().endsWith(".txt") || file.getName().endsWith(".java")) {
	        FileInputStream fis;
	        try {
	          fis = new FileInputStream(file);
	        } catch (FileNotFoundException fnfe) {
	        	logger.error(fnfe.toString());  
	          return;
	        }
	
	        try {
	          Document doc = new Document();
	
	          Field fullPathField = new Field("full_path", file.getPath(), Field.Store.YES, Field.Index.ANALYZED);
	          doc.add(fullPathField);
	          
	          Field fileNameField = new Field("file_name", file.getName().toLowerCase(), Field.Store.YES, Field.Index.ANALYZED);
	          doc.add(fileNameField);
	
	          NumericField modifiedField = new NumericField("modified",Field.Store.YES, true);
	          SimpleDateFormat formatter = new SimpleDateFormat ( "yyyyMMddHHmmss", Locale.KOREA );
	          modifiedField.setLongValue(Long.parseLong(formatter.format(new Date(file.lastModified()))));
	          doc.add(modifiedField);
	
	          doc.add(new Field("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));

	          logger.info("adding " + file);
	          
	          writer.addDocument(doc);
	        } finally {
	          fis.close();
	        }
    	}
      }
    }
  }
}
