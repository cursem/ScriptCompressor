package gdut.tool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Vector;
import java.text.DecimalFormat;

/**
  JSA��ѹ���㷨 
 */
public class JSEncoder {
    public static final String ENCODE_BASE64 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_$";
    public boolean isDebug = false;

    /**
     * @param fileInput js fileInput
     * @param offset   offset>=0ָ��ƫ�Ʊ�������ͬ��offset����ʵ�ִ����λ�õı任����С��offset���Ի�ø����ѹ����
     * @return ѹ����Ĵ���
     */
    public String encode(String fileInput, int offset) throws Exception {
        String jscript =fileInput;
        jscript.length();
        jscript = jscript.replaceAll("\n", " ");
        //�滻\->\\
        jscript = jscript.replaceAll("\\\\", "\\\\\\\\");
        //�滻������'=>\'
        jscript = jscript.replaceAll("\\'", "\\\\\\'");

        Pattern p = Pattern.compile("([\\w\\$]+)");
        Matcher m = p.matcher(jscript);
        String element;
        Vector<String> dict = new Vector<String>();
        int index;
        StringBuffer encscript = new StringBuffer();
        StringBuffer dicttab = new StringBuffer();

        debugInfo("=====�����ֵ��Ӧ��=====");
        while (m.find()) {
            element = m.group(1).trim();
            if (!dict.contains(element)) {
                dict.add(element);
                index = dict.size() - 1;
            } else {
                index = dict.indexOf(element);
            }
            debugInfo(index + "==>" + element);
            m.appendReplacement(encscript, Base64Encode(offset + index + 1));
        }
        for (String o : dict) dicttab.append(o + "|");
        m.appendTail(encscript);
        debugInfo("=====  �����ֵ����  =====");
        debugInfo("Offset=" + offset + ",�ֵ��С=" + dict.size());
        debugInfo("ѹ����Ĵ��룺\n" + encscript.toString());
        String dictstr = dicttab.substring(0, dicttab.length() - 1).toString();
        debugInfo("�ֵ��ַ���:\n" + dictstr);
        String res = formatCode(encscript.toString(), dictstr, dict.size(), offset);
        res.length();
        new DecimalFormat("######.0");
         return res;
    }


    private void debugInfo(String txt) {
        if (isDebug) System.out.println(txt);
    }

    /**
     * ��Base64����
     *
     * @param c �����������
     * @return ����ֵ
     */
    private String Base64Encode(int c) throws Exception {
        String res;
        if (c < 0) throw new Exception("Error:Offset����>=0.");
        if (c > 63)
            res = Base64Encode(c >> 6) + Base64Encode(c & 63);
        else {
            //Ϊ�����appendReplacement������ʹ�ã���$�滻Ϊ\$
            res = c == 63 ? "\\$" : String.valueOf(ENCODE_BASE64.charAt(c));
        }
        return res;
    }

    private String formatCode(String enc, String dict, int size, int offset) {
        StringBuffer str = new StringBuffer();
        str.append("\neval(function(E,I,A,D,J,K,L,H){function C(A){return A<62?String.fromCharCode(A+=A<26?65:A<52?71:-4):A<63?'_':A<64?'$':C(A>>6)+C(A&63)}while(A>0)K[C(D--)]=I[--A];function N(A){return K[A]==L[A]?A:K[A]}if(''.replace(/^/,String)){var M=E.match(J),B=M[0],F=E.split(J),G=0;if(E.indexOf(F[0]))F=[''].concat(F);do{H[A++]=F[G++];H[A++]=N(B)}while(B=M[G]);H[A++]=F[G]||'';return H.join('')}return E.replace(J,N)}(");
        str.append("'" + enc + "',");
        str.append("'" + dict + "'.split('|'),");
        str.append(size + "," + (size + offset) + ",/[\\w\\$]+/g, {}, {}, []))");
        return str.toString();
    }

}
