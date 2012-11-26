import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

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


public class IndexFiles {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String docsPath = "d:\\workspace";
		String indexPath = "d:\\lucene_index";
		
	    Date start = new Date();
		
		try {
			Directory dir = FSDirectory.open(new File(indexPath));
			Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_31);
			IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_31, analyzer);
			
			iwc.setOpenMode(OpenMode.CREATE);
			IndexWriter writer = new IndexWriter(dir, iwc);
			
			final File docDir = new File(docsPath);
			indexDocs(writer, docDir);

		    writer.close();
		    
            Date end = new Date();
		    System.out.println(end.getTime() - start.getTime() + " total milliseconds");

		} catch (Exception e) {
			System.out.println(e);  
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
			  System.out.println(fnfe);  
	          return;
	        }
	
	        try {
	          Document doc = new Document();
	
	          Field fullPathField = new Field("full_path", file.getPath(), Field.Store.YES, Field.Index.ANALYZED);
	          doc.add(fullPathField);
	          
	          Field fileNameField = new Field("file_name", file.getName(), Field.Store.YES, Field.Index.ANALYZED);
	          doc.add(fileNameField);
	
	          NumericField modifiedField = new NumericField("modified");
	          modifiedField.setLongValue(file.lastModified());
	          doc.add(modifiedField);
	
	          doc.add(new Field("contents", new BufferedReader(new InputStreamReader(fis, "UTF-8"))));

	          System.out.println("adding " + file);
	          
	          writer.addDocument(doc);
	        } finally {
	          fis.close();
	        }
    	}
      }
    }
  }
}
