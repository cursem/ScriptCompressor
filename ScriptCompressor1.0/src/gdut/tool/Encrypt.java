package gdut.tool;

public class Encrypt {

	public static String encrypt(String str) {
		int strLen = str.length();	//str����Ҫ���ص��ַ���
		int charPerLine = 1;
		// start at line 2, go no further than length - 2
		for(int k=0; k < (strLen % charPerLine); k++)
		{
			str += " ";
		}
		strLen = str.length();
		//output = malcode[0] + "\n" + "d=" + (strLen / charPerLine) + ";\n";
		String[] output = new String[str.length()];
		for(int x = 0; x < str.length(); x++)
		{
			output[x] = toBin((int)str.charAt(x));
		}
		String encryptStr = "";
		for(int i = 0;i < output.length;i++) {
			encryptStr += output[i];
		}
		return encryptStr;		
	}

	public static String toBin(int inNum) {
		String retVal = "";
		int rem = 0;
		for (int i = 0; i < 8; i++)
		{
			rem = inNum % 2;
			if(rem == 0)
			{
				retVal = "\t" + retVal;	//��ǰ��\t
			}
			else
			{
				retVal = " " + retVal;	//��ǰ�ǿո�
			}
			inNum = inNum >> 1;
		}
		return(retVal);			
	}
}
