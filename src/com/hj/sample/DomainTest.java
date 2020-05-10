package com.hj.sample;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import com.tscale.ttouch.thread.v2.PriorityThreadFactory;
import com.tscale.ttouch.thread.v2.runnable.PriorityThread;

public class DomainTest {
	public static final String queryUrl = "http://panda.www.net.cn/cgi-bin/check.cgi?area_domain=";
	
	static String[] end_prefix;

	private static Logger logger = Logger.getLogger(DomainTest.class);

	static ArrayList<String> domains = new ArrayList<>();

	public static void main(String[] args) {
		test();
	}

	/**
	 * 鍒ゆ柇瀛楃涓蹭腑鏄惁鍖呭惈涓枃
	 * 
	 * @param str 寰呮牎楠屽瓧绗︿覆
	 * @return 鏄惁涓轰腑鏂�
	 * @warn 涓嶈兘鏍￠獙鏄惁涓轰腑鏂囨爣鐐圭鍙�
	 */
	public static boolean isContainChinese(String str) {
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(str);
		if (m.find()) {
			return true;
		}
		return false;
	}

	static int current_count = 0;
	static int all_count = 0;

	public static void loadDomains() {
		Properties pro = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("a.properties");
			pro.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		end_prefix = pro.getProperty("domains").split(",");
	}

	public static void test() {

		loadDomains();

		buildDomains(1);

		logger.debug("start process");

		System.out.println("domains length is " + domains.size());
		System.out.println("domains prefix length is " + end_prefix.length);
		all_count = domains.size() * end_prefix.length;

		for (int di = 0; di < domains.size(); di++) {
			for (int d = 0; d < end_prefix.length; d++) {
				String prefix = end_prefix[d];

				String domain = domains.get(di) + prefix;
				PriorityThreadFactory.getPool().execute(new PriorityThread() {

					@Override
					public void run() {
						try {
							get(queryUrl + domain);
						} catch (Exception e) {
							e.printStackTrace();
							get(queryUrl + domain);
						}
					}

					@Override
					public String getThreadName() {
						return "thread : " + domain;
					}
				});
			}
		}
		System.out.println("Task down");
	}

	public static void buildDomains(int length) {
		int start_a = 'a';

		int current = start_a;
		for (int i = 1; i <= length; i++) {
			if (i == 1) {
				for (int a = 0; a < 26; a++) {
					char c = (char) current;
					current += 1;
					domains.add(new String() + c);
				}
			}
			if (i == 2) {
				start_a = 'a';

				String s2;
				int i1 = start_a, i2 = start_a;
				for (int a = 0; a < 26; a++) {
					s2 = new String();
					s2 = "" + (char) i1;
					i1 += 1;
					for (int b = 0; b < 26; b++) {
						if (s2.length() > 1) {
							s2 = s2.substring(0, 1);
						}
						s2 = s2 + (char) i2;
						domains.add(s2);
						// System.out.println(s2);

						i2 += 1;
					}
					s2 = "";
					i2 = start_a;
				}
			}
			if (i == 3) {
				start_a = 'a';

				String s2;
				int i1 = start_a, i2 = start_a, i3 = start_a;
				for (int a = 0; a < 26; a++) {
					s2 = new String();
					s2 = "" + (char) i1;
					i1 += 1;
					for (int b = 0; b < 26; b++) {
						if (s2.length() > 1) {
							s2 = s2.substring(0, 1);
						}
						s2 = s2 + (char) i2;

						for (int c = 0; c < 26; c++) {
							if (s2.length() > 2) {
								s2 = s2.substring(0, 2);
							}
							s2 = s2 + (char) i3;
							domains.add(s2);
							// System.out.println(s2);
							i3 += 1;
						}

						i2 += 1;
						i3 = start_a;
					}
					s2 = "";
					i2 = start_a;
				}
			}
		}
	}

	public static void get(String url) {
		// 1.鎵撳紑娴忚鍣�
		CloseableHttpClient httpClient = HttpClients.createDefault();
		// 2.澹版槑get璇锋眰
		HttpGet httpGet = new HttpGet(url);
		// 3.鍙戦�佽姹�
		CloseableHttpResponse response;
		try {
			response = httpClient.execute(httpGet);
			// 4.鍒ゆ柇鐘舵�佺爜
			current_count++;
			if (response.getStatusLine().getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				// 浣跨敤宸ュ叿绫籈ntityUtils锛屼粠鍝嶅簲涓彇鍑哄疄浣撹〃绀虹殑鍐呭骞惰浆鎹㈡垚瀛楃涓�
				String string = EntityUtils.toString(entity, "utf-8");
//				System.out.println(url);
				System.out.println(string);
				if (string.contains("<original>210")) {
//					logger.debug(
//							url.substring(url.lastIndexOf("=") + 1) + " Domain name is available =================>");
					System.out.println(
							url.substring(url.lastIndexOf("=") + 1) + " Domain name is available =================>");
				} else {

					System.out.println(url.substring(url.lastIndexOf("=") + 1) + " Domain name not available");
				}

			}

			// if (current_count % 100 == 0 || (all_count - current_count) < 50) {
			System.out.println("current is " + current_count + "/" + all_count);
			// }
			// 5.鍏抽棴璧勬簮
			response.close();
			httpClient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
