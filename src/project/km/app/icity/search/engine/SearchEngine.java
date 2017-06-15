package km.app.icity.search.engine;

import com.inspur.bean.DataSet;

import km.app.icity.search.pojo.SearchQuery;
import net.sf.json.JSONArray;

/**
 * 搜索引擎接口
 * 
 * @author XiongZhiwen
 * 
 */
public interface SearchEngine {

	/**
	 * 生成索引前先清除索引
	 * 
	 * @param indexPath
	 *            索引目录
	 * @param indexType
	 *            索引类型
	 */
	public void clearIndex(String indexPath, String indexType);

	/**
	 * 创建索引文档
	 * 
	 * @param indexList
	 *            待索引json数组
	 */
	public void createIndex(JSONArray indexList);

	/**
	 * 写入索引文档集合
	 * 
	 * @param indexPath
	 *            索引目录
	 * @param indexType
	 *            索引类型
	 * @return 返回true表示创建索引成功，返回false表示创建索引失败
	 */
	public boolean writeIndex(String indexPath, String indexType);

	/**
	 * 根据索引查询
	 * 
	 * @param indexPath
	 *            索引目录
	 * @param searchQuery
	 *            索引条件
	 * @return 数据集
	 */
	public DataSet searchIndex(String indexPath,SearchQuery searchQuery);
}