package gdut.tool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileCode {
	public static String getCharsetByString(String fileStr)
			throws FileNotFoundException {	//��ΪfileStr��ֵ���ɵ��ó�����ģ������׳�FileNotFoundException���ϼ�����
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		boolean checked = false;
		File file = new File(fileStr);
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		bis.mark(0);
		try {
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1) {
				bis.close();
				return charset;
			}
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();
			if (!checked) {
				while ((read = bis.read()) != -1) {
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF) // ��������BF���µģ�Ҳ����GBK
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) // ˫�ֽ� (0xC0 - 0xDF)
															// (0x80
															// - 0xBF),Ҳ������GB������
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {// Ҳ�п��ܳ������Ǽ��ʽ�С
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
			}
			bis.close();
		} catch (IOException e) {
			// nothing to do
		}
		return charset;
	}

	public static String getCharsetByFile(File file)
			throws FileNotFoundException {	//��ΪfileStr��ֵ���ɵ��ó�����ģ������׳�FileNotFoundException���ϼ�����
		String charset = "GBK";
		byte[] first3Bytes = new byte[3];
		boolean checked = false;
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
				file));
		bis.mark(0);
		try {
			int read = bis.read(first3Bytes, 0, 3);
			if (read == -1) {
				bis.close();
				return charset;
			}
			if (first3Bytes[0] == (byte) 0xFF && first3Bytes[1] == (byte) 0xFE) {
				charset = "UTF-16LE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xFE
					&& first3Bytes[1] == (byte) 0xFF) {
				charset = "UTF-16BE";
				checked = true;
			} else if (first3Bytes[0] == (byte) 0xEF
					&& first3Bytes[1] == (byte) 0xBB
					&& first3Bytes[2] == (byte) 0xBF) {
				charset = "UTF-8";
				checked = true;
			}
			bis.reset();
			if (!checked) {
				while ((read = bis.read()) != -1) {
					if (read >= 0xF0)
						break;
					if (0x80 <= read && read <= 0xBF) // ��������BF���µģ�Ҳ����GBK
						break;
					if (0xC0 <= read && read <= 0xDF) {
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) // ˫�ֽ� (0xC0 - 0xDF)
															// (0x80
															// - 0xBF),Ҳ������GB������
							continue;
						else
							break;
					} else if (0xE0 <= read && read <= 0xEF) {// Ҳ�п��ܳ������Ǽ��ʽ�С
						read = bis.read();
						if (0x80 <= read && read <= 0xBF) {
							read = bis.read();
							if (0x80 <= read && read <= 0xBF) {
								charset = "UTF-8";
								break;
							} else
								break;
						} else
							break;
					}
				}
			}
			bis.close();
		} catch (IOException e) {
			// noting to do
		}
		return charset;
	}

}