package qqzeng.ip;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.sql.SQLException;

public class IpSearch {
	private final static String IP_FILE = getIpFilePath();
	public static final int IP_RECORD_LENGTH = 7;
	private static IpSearch instance = null;
	private RandomAccessFile ipFile = null;

	public RandomAccessFile getIpFile() {
		return ipFile;
	}

	public static String getIpFilePath() {
		try {
			return IpSearch.class.getClassLoader().getResource("qqzeng-ip.dat")
					.getPath();
		} catch (Exception e) {
			System.out.println("û���ҵ�qqzeng-ip.dat�ļ�");
			e.printStackTrace();
			return null;
		}
	}

	public IpSearch() {
		try {
			if (null == IP_FILE)
				System.exit(1);
			ipFile = new RandomAccessFile(IP_FILE, "r");
		} catch (IOException e) {
			System.err.println("�޷���" + IP_FILE + "�ļ�");
		}
	}

	public void closeIpFile() {
		try {
			if (ipFile != null) {
				ipFile.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != ipFile)
				ipFile = null;
		}
	}

	public synchronized static IpSearch getInstance() {
		if (null == instance)
			instance = new IpSearch();
		return instance;
	}

	public IpEntity find(String ip) {
		long ipValue = IpUtils.ipToLong(ip);
		IpHeader header = new IpHeader(ipFile);
		long first = header.getIpBegin();
		int left = 0;
		int right = (int) ((header.getIpEnd() - first) / IP_RECORD_LENGTH);
		int middle = 0;
		IpIndex middleIndex = null;
		// ���ֲ���
		while (left <= right) {
			// �޷������ƣ���ֹ���
			middle = (left + right) >>> 1;
			middleIndex = new IpIndex(ipFile, first + middle
					* IP_RECORD_LENGTH);
			if (ipValue > middleIndex.getStartIp())
				left = middle + 1;
			else if (ipValue < middleIndex.getStartIp())
				right = middle - 1;
			else
				return new IpEntity(ipFile, middleIndex.getStartIp(),
						middleIndex.getIpPos());
		}
		// �Ҳ�����ȷ�ģ�ȡ�ڷ�Χ�ڵ�
		middleIndex = new IpIndex(ipFile, first + right * IP_RECORD_LENGTH);
		IpEntity record = new IpEntity(ipFile, middleIndex.getStartIp(),
				middleIndex.getIpPos());
		if (ipValue >= record.getBeginIP() && ipValue <= record.getEndIP()) {
			return record;
		} else {
			// �Ҳ�����Ӧ�ļ�¼
			return new IpEntity(0L, ipValue);
		}
	}

	public static void main(String[] args) throws SQLException {
		String ip = "1.197.224.9";

		IpSearch finder = IpSearch.getInstance();
		IpEntity record = finder.find(ip);

		System.out.println(ip);
		System.out.println(record.getCountry());
		System.out.println(record.getArea());

/*		1.197.224.9
		�й�
		����|�ܿ�|��ˮ|����|411623|����|China|CN|114.60604|33.53912 */

		finder.closeIpFile();
	}
}