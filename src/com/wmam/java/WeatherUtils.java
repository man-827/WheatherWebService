package com.wmam.java;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @ClassName WeatherUtils
 * @Description TODO ������Ϣ������Դ(http://www.webxml.com.cn/)
 * ���ݳ��л�������Ʋ�ѯ���δ��������������������ڵ�����ʵ��������������ָ��:
 * ���÷������£����������theCityName = ������������(������п���Ӣ��)����д���(������Ĭ��Ϊ�Ϻ���)���磺�Ϻ� �� 58367������
 * ���������ظ���ʹ�ó��д����ѯ(��ͨ�� getSupportCity �� getSupportDataSet ���)���������ݣ� һ��һά���� String(22)������
 * 23��Ԫ�ء�String(0) �� String(4)��ʡ�ݣ����У����д��룬����ͼƬ���ƣ�������ʱ�䡣String(5) �� String(11)������� ���£�
 * �ſ�������ͷ������������ƿ�ʼͼƬ����(���³ƣ�ͼ��һ)���������ƽ���ͼƬ����(���³ƣ�ͼ���)�����ڵ�����ʵ��������������
 * ָ����String(12) ��String(16)���ڶ���� ���£��ſ�������ͷ�����ͼ��һ��ͼ�����String(17) �� String(21)��������� ���£�
 * �ſ�������ͷ�����ͼ��һ��ͼ�����String(22) ����ѯ�ĳ��л�����Ľ���
 * @author zyj jayzh1993@gmail.com
 * @date 2013-10-14
 */
public class WeatherUtils {
	/**
	 * ��ȡSOAP������ͷ�����滻���еı�־����Ϊ�û�����ĳ���
	 * 
	 * @param city
	 *            �û�����ĳ�������
	 * @return �ͻ���Ҫ���͸���������SOAP����
	 */
	private static String getSoapRequest(String city) {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>"
					+ "<soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
					+ "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
					+ "xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\">"
					+ "<soap:Body>    <getWeather xmlns=\"http://WebXml.com.cn/\">"
					+ "<theCityCode>" + city
					+ "</theCityCode>    </getWeather>"
					+ "</soap:Body></soap:Envelope>");
		return sb.toString();
	}

	/**
	 * �û���SOAP�����͸��������ˣ������ط������㷵�ص�������
	 * 
	 * @param city
	 *            �û�����ĳ�������
	 * @return �������˷��ص������������ͻ��˶�ȡ
	 * @throws Exception
	 */
	private static InputStream getSoapInputStream(String city) throws Exception {
		try {
			String soap = getSoapRequest(city);
			if (soap == null) {
				return null;
			}
			URL url = new URL(
					"http://www.webxml.com.cn/WebServices/WeatherWS.asmx");
			URLConnection conn = url.openConnection();
			conn.setUseCaches(false);
			conn.setDoInput(true);
			conn.setDoOutput(true);

			conn.setRequestProperty("Content-Length", Integer.toString(soap
					.length()));
			conn.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
			conn.setRequestProperty("SOAPAction",
					"http://WebXml.com.cn/getWeather");

			OutputStream os = conn.getOutputStream();
			OutputStreamWriter osw = new OutputStreamWriter(os, "utf-8");
			osw.write(soap);
			osw.flush();
			osw.close();

			InputStream is = conn.getInputStream();
			return is;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * �Է������˷��ص�XML���н���
	 * 
	 * @param city
	 *            �û�����ĳ�������
	 * @return �ַ��� ��#�ָ�
	 */
	public static String getWeather(String city) {
		try {
			Document doc;
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setNamespaceAware(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputStream is = getSoapInputStream(city);
			doc = db.parse(is);
			NodeList nl = doc.getElementsByTagName("string");
			StringBuffer sb = new StringBuffer();
			for (int count = 0; count < nl.getLength(); count++) {
				Node n = nl.item(count);
				if(n.getFirstChild().getNodeValue().equals("��ѯ���Ϊ�գ�")) {
					sb = new StringBuffer("#") ;
					break ;
				}
				sb.append(n.getFirstChild().getNodeValue() + "#");
			}
			is.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * ����
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		String weatherInfo = getWeather("����");
		System.out.println(weatherInfo);
	}
}
