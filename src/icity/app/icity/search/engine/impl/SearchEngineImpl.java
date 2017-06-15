package app.icity.search.engine.impl;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Fieldable;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.FieldCache;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;

import app.icity.search.engine.SearchEngine;
import app.icity.search.pojo.AnalyzerFactory;
import app.icity.search.pojo.SearchQuery;

import com.icore.util.StringUtil;
import com.inspur.bean.DataSet;
import com.inspur.util.PathUtil;
import com.inspur.util.Tools;

@SuppressWarnings("deprecation")
public class SearchEngineImpl implements SearchEngine {
	private static String rootLucene = PathUtil.getFileRoot() + File.separator + "lucene" + File.separator; // 索引根目录
	private Collection<Document> docList = new ArrayList<Document>();// 索引文档集合

	@Override
	public void clearIndex(String indexPath, Term term) {
		// 初始化时清除原索引信息
		IndexWriter indexWriter = null;// 搜索引擎输出流
		Directory directory = null;// 搜索引擎输出目录
		try {
			directory = getDirectory(indexPath);
			if (IndexWriter.isLocked(directory)) {
				directory.clearLock(IndexWriter.WRITE_LOCK_NAME);
			}

			if (IndexReader.indexExists(directory)) {
				Analyzer analyzer = AnalyzerFactory.getInstance().getAnalyzer();
				if (IndexWriter.isLocked(directory)) {
					IndexWriter.unlock(directory);
				}
				indexWriter = new IndexWriter(directory, analyzer, false, MaxFieldLength.LIMITED);
				indexWriter.deleteDocuments(term);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (LockObtainFailedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeWriter(indexWriter);
			closeDirectory(directory);
		}
	}

	@Override
	public void createIndex(JSONArray indexList) {
		// 建立索引
		if (indexList != null) {
			for (int k = 0; k < indexList.size(); k++) {
				JSONObject index = (JSONObject) indexList.get(k);
				if (index != null) {
					Document doc = new Document();
					Iterator<?> it = index.keys();
					while (it.hasNext()) {
						String key = (String) it.next();
						String value = index.getString(key);
						if ("NAME".equals(key) || "TITLE_NAME".equals(key)) {
							doc.add(new Field(key, value, Store.YES, Index.ANALYZED));
						} else {
							doc.add(new Field(key, value, Store.YES, Index.NOT_ANALYZED));
						}
					}
					docList.add(doc);
				}
			}
		}
	}

	@Override
	public boolean writeIndex(String indexPath, String indexType) {
		boolean result = true; // 写入索引结果
		IndexWriter indexWriter = null; // 搜索引擎输出流
		Directory directory = null;// 搜索引擎输出目录
		try {
			directory = getDirectory(indexPath);
			if (!IndexWriter.isLocked(directory)) {
				directory.makeLock(IndexWriter.WRITE_LOCK_NAME);

				boolean create = false;
				if (!IndexReader.indexExists(directory)) {
					create = true;
				}

				// 分词索引
				Analyzer analyzer = AnalyzerFactory.getInstance().getAnalyzer();
				if (IndexWriter.isLocked(directory)) {
					IndexWriter.unlock(directory);
				}
				indexWriter = new IndexWriter(directory, analyzer, create, MaxFieldLength.LIMITED);

				// 写入索引
				Term delTerm = new Term("TYPE", indexType);
				indexWriter.updateDocuments(delTerm, docList);
			}
		} catch (CorruptIndexException e) {
			e.printStackTrace();
			result = false;
		} catch (IOException e) {
			e.printStackTrace();
			result = false;
		} finally {
			docList.clear();
			closeWriter(indexWriter);
			closeDirectory(directory);
		}
		return result;
	}

	@Override
	public DataSet searchIndex(String indexPath, SearchQuery indexQuery, Sort sort) {
		DataSet ds = new DataSet();
		IndexSearcher searcher = null;// 搜索器
		try {
			// 返回内容
			JSONArray ret = new JSONArray();

			// 索引目录
			String path = rootLucene + indexPath;
			File file = new File(path);
			if (!file.exists()) {
				file.mkdirs();
			}

			// 进行索引
			Directory directory = FSDirectory.open(file);
			searcher = new IndexSearcher(directory);
			BooleanQuery query = indexQuery.getQuery();
			BooleanQuery mergeQuery = (BooleanQuery) query.clone();
			TermRangeQuery rangeQuery = indexQuery.getRangeQuery();
			if (rangeQuery != null) {
				mergeQuery.add(rangeQuery, Occur.MUST);
			}
			searcher.setDefaultFieldSortScoring(true, false);
			TopDocs topDocs = searcher.search(mergeQuery, 1000, sort);
			ScoreDoc[] hits = topDocs.scoreDocs;
			// 分页查询
			int hitsize = hits.length;
			int start = indexQuery.getStart();
			int rows = indexQuery.getRows();
			if (hitsize > 0) {
				int begin = (start + 1 < hitsize) ? start : hitsize - 1;
				int end = (begin + rows < hitsize) ? begin + rows : hitsize;

				// 将索引结果转换为json数组
				for (int i = begin; i < end; i++) {
					JSONObject jo = new JSONObject();
					Document doc = searcher.doc(hits[i].doc);
					List<Fieldable> fieldList = doc.getFields();
					for (int j = 0; j < fieldList.size(); j++) {
						Fieldable field = fieldList.get(j);
						String fkey = field.name();
						String fvalue = field.stringValue();
						jo.put(fkey, fvalue);
					}
					ret.add(jo);
				}
			}

			// 如果开启高亮显示，贵关键字进行高亮显示
			boolean isHighlight = indexQuery.isHighlight();
			if (isHighlight && ret.size() > 0) {
				String fields[] = indexQuery.getFields(); // 高亮显示关键字
				String highlightSimplePre = indexQuery.getHighlightSimplePre(); // 高亮显示前缀
				String highlightSimplePost = indexQuery.getHighlightSimplePost(); // 高亮显示后缀
				Analyzer analyzer = AnalyzerFactory.getInstance().getAnalyzer();
				SimpleHTMLFormatter simpleHtmlFormatter = new SimpleHTMLFormatter(highlightSimplePre,
						highlightSimplePost);// 设定高亮显示格式
				Highlighter highlighter = new Highlighter(simpleHtmlFormatter, new QueryScorer(query));
				for (int i = 0; i < ret.size(); i++) {
					JSONObject jo = (JSONObject) ret.get(i);
					for (String keyword : fields) {
						if (jo.containsKey(keyword)) {
							String value = jo.getString(keyword);
							if("NAME".equals(keyword)){
								jo.put("TNAME", value);
							}
							TokenStream tokenStream = analyzer.tokenStream(keyword, new StringReader(value));
							String hlvalue = highlighter.getBestFragment(tokenStream, value); // 高亮值
							if (StringUtil.isNotEmpty(hlvalue)) {
								jo.put(keyword, hlvalue);
							}
						}
					}
				}
			}

			// 返回数据
			ds.setData(Tools.stringToBytes(ret.toString()));
			if (hitsize > 0) {
				ds.setTotal(hitsize);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (searcher != null) {
				try {
					searcher.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ds;
	}

	public DataSet searchIndexByGroup(String indexPath, SearchQuery indexQuery, String groupBy) {
		DataSet ds = new DataSet();
		IndexSearcher searcher = null;
		String path = rootLucene + indexPath;
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		IndexReader reader;
		try {
			reader = IndexReader.open(FSDirectory.open(new File(path)), true);
			searcher = new IndexSearcher(reader);
			TopDocsCollector collector = TopScoreDocCollector.create(1, false);
			// 读取分组字段值字段值，放到fieldCache中
			final String[] fc = FieldCache.DEFAULT.getStrings(reader, groupBy);
			// GroupCollector是自定义文档收集器，用于实现分组统计
			GroupCollector groupCollector = new GroupCollector(collector, fc);
			BooleanQuery query = indexQuery.getQuery();
			BooleanQuery mergeQuery = (BooleanQuery) query.clone();
			TermRangeQuery rangeQuery = indexQuery.getRangeQuery();
			if (rangeQuery != null) {
				mergeQuery.add(rangeQuery, Occur.MUST);
			}
			searcher.search(mergeQuery, groupCollector);
			// GroupField用来保存分组统计的结果
			GroupField gf = groupCollector.getGroupField();
			ds.setRawData(gf.getCountMap());
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (searcher != null) {
				try {
					searcher.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ds;
	}

	/**
	 * 关闭搜索引擎输出流
	 * 
	 * @param writer
	 */
	private static void closeWriter(IndexWriter writer) {
		if (writer != null) {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 关闭目录并解锁目录
	 * 
	 * @param writer
	 */
	private static void closeDirectory(Directory directory) {
		if (directory != null) {
			try {
				directory.clearLock(IndexWriter.WRITE_LOCK_NAME);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取索引目录
	 * 
	 * @param indexPath
	 *            索引路径
	 */
	private static Directory getDirectory(String indexPath) {
		Directory directory = null;
		try {
			// 索引目录
			String dirPath = rootLucene + indexPath;
			File file = new File(dirPath);
			directory = FSDirectory.open(file);
			if (!IndexWriter.isLocked(directory)) {
				directory.makeLock(IndexWriter.WRITE_LOCK_NAME);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return directory;
	}
}