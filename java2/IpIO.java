package qqzeng.ip;

import java.io.IOException;
import java.io.RandomAccessFile;

public class IpIO {
	
	public static long readLong4(RandomAccessFile ipFile, long pos) {
		try {
			byte[] b = new byte[4];
			ipFile.seek(pos);
			ipFile.read(b, 0, 4);
			//IP���ļ�������Сβ��
			return (b[0] & 0xFFL) | ((b[1] << 8) & 0xFF00L) | ((b[2] << 16) & 0xFF0000L) | ((b[3] << 24) & 0xFF000000L);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static long readLong3(RandomAccessFile ipFile, long pos) {
		try {
			byte[] b = new byte[3];
			ipFile.seek(pos);
			ipFile.read(b, 0, 3);
			return (b[0] & 0xFFL) | ((b[1] << 8) & 0xFF00L) | ((b[2] << 16) & 0xFF0000L);
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}
	
	public static String readString(RandomAccessFile ipFile, long pos) {
		try {
			//��ȡ�ַ����ĳ���
			ipFile.seek(pos);
			int i = 1;
			for(; 0 != ipFile.readByte(); i++);
			
			byte[] b = new byte[i];
			ipFile.seek(pos);
			//����ת����utf8���룬�迼��\0��β��
			ipFile.read(b, 0, i);
			return IpUtils.encode(b, "GBK").trim();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}