package action.icity.unionb2c.com.pay;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


import action.icity.unionb2c.com.sdk.AcpService;
import action.icity.unionb2c.com.sdk.LogUtil;
import action.icity.unionb2c.com.sdk.SDKConfig;
import action.icity.unionb2c.com.sdk.SDKConstants;

import com.inspur.base.BaseAction;
import com.inspur.util.SecurityConfig;

/**
 * 重要：联调测试时请仔细阅读注释！
 * 
 * 产品：跳转网关支付产品<br>
 * 功能：后台通知接收处理示例 <br>
 * 日期： 2015-09<br>
 * 版本： 1.0.0 
 * 版权： 中国银联<br>
 * 说明：以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己需要，按照技术文档编写。该代码仅供参考。<br>
 * 该接口参考文档位置：open.unionpay.com帮助中心 下载  产品接口规范  《网关支付产品接口规范》，<br>
 *              《平台接入接口规范-第5部分-附录》（内包含应答码接口规范，全渠道平台银行名称-简码对照表），
 * 测试过程中的如果遇到疑问或问题您可以：1）优先在open平台中查找答案：
 * 							        调试过程中的问题或其他问题请在 https://open.unionpay.com/ajweb/help/faq/list 帮助中心 FAQ 搜索解决方案
 *                             测试过程中产生的6位应答码问题疑问请在https://open.unionpay.com/ajweb/help/respCode/respCodeList 输入应答码搜索解决方案
 *                           2） 咨询在线人工支持： open.unionpay.com注册一个用户并登录在右上角点击“在线客服”，咨询人工QQ测试支持。
 * 交易说明：成功的交易才会发送后台通知，建议此交易与交易状态查询交易结合使用确定交易是否成功
 */

public class BackRcvResponse extends BaseAction{

	private static final long serialVersionUID = 1L;
	StringBuffer logStr =null;
	@Override
	public boolean handler(Map<String, Object> data) throws Exception {
		String configUrl = SecurityConfig.getString("configUrl");
		
		//从CLASSPATH加载acp_sdk.properties文件
		SDKConfig.getConfig().loadPropertiesFromPath(configUrl);
		receiveData();
		
		return false;
	}
	
	public void receiveData(){
		String encoding = this.getPostData().get(SDKConstants.param_encoding);
		Map<String, String> valideData = null;
		if (null != this.getPostData() && !this.getPostData().isEmpty()) {
			Iterator<Entry<String, String>> it = this.getPostData().entrySet().iterator();
			valideData = new HashMap<String, String>(this.getPostData().size());
			while (it.hasNext()) {
				Entry<String, String> e = it.next();
				String key = (String) e.getKey();
				String value = (String) e.getValue();
				try {
					value = new String(value.getBytes(encoding), encoding);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				valideData.put(key, value);
			}
		}
		String orderId = this.getPostData().get("orderId");
		// 缴费成功 更新外网 反馈博思
		BackRcvUtil br = new BackRcvUtil();
		logStr = new StringBuffer();
		//重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
		if (!AcpService.validate(valideData, encoding)) {
			LogUtil.writeLog("验证签名结果[失败].");
			//验签失败，需解决验签问题
			logStr.append("银联缴费失败");
		} else {
			LogUtil.writeLog("验证签名结果[成功].");
			//【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态
			logStr.append("银联缴费成功");
			String payCode = this.getPostData().get("reqReserved").substring(9, 23);;
			String totalAmount = this.getPostData().get("txnAmt");
			
			//反馈博思
			String str = br.bosiConfirmPay(payCode,totalAmount);
			logStr.append("#反馈博思："+str);
			//反馈外网
			br.updateBillState(orderId);
		}
		//返回给银联服务器http 200  状态码
		//resp.getWriter().print("ok");
		//记录日志
		br.updateBillLog(orderId, logStr.toString());
	}
}
