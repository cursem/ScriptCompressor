package gdut.tool;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

public class AutoRecompose {

	public static int getLen() {// 获取随机数，范围在6-12之间，源代码分段的长度
		int len;
		Calendar c = Calendar.getInstance();
		int minute = c.get(Calendar.MINUTE);
		int date = c.get(Calendar.DATE);
		int second = c.get(Calendar.SECOND);
		len = second % 12;// 秒取余，范围是0-12
		if (len < 6) {
			if (date >= 6 && date <= 12) {
				len = date;
			} else if (minute % 12 >= 6) {
				len = minute % 12;
			} else
				len = 6;
		}		
		return (len);
	}

	public static ArrayList<String> setGroupByTime(String source) {//根据上面的分段节数，将源代码分段
		int grouplength = getLen();
		int len = source.length();
		String sub, os1 = "\"", ns1 = "\\\"";
		ArrayList<String> group = new ArrayList<String>();// 存储js字符串
		for (int i = 0; i < len; i = len / grouplength + i) {
			if (i < len && len / grouplength + i < len) {
				sub = source.substring(i, len / grouplength + i).replace(
						os1.substring(0), ns1.substring(0));
				group.add(sub);
			} else if (i <= len - 1) {
				sub = source.substring(i, len).replace(os1.substring(0),
						ns1.substring(0));
				group.add(sub);
			} else
				break;
		}

		return group;
	}

	public static int[] getArray(int len) {//随机数组，将js的字符串组合序列打乱
		Random random = new Random();
		int k = len;
		int a[] = new int[k];
		a[0] = random.nextInt(k);
		for (int i = 1; i < a.length; i++) {
			a[i] = random.nextInt(k);
			for (int j = 0; j < i; j++) {
				while (a[i] == a[j]) {
					i--;
				}

			}
		}
		return a;
	}

	public static String autoRecompose(String source) {
		ArrayList<String> strGroup = setGroupByTime(source);
		System.out.println(strGroup.size());

		int[] al = getArray(strGroup.size());
		for (int i = 0; i < al.length; i++) {
			System.out.println("al[" + i + "]=" + al[i]);
		}
		String laterStr = "";
		String[] str = new String[] { "", "", "", "", "", "", "", "", "", "",
				"", "" };
		laterStr = "var len=0;var oo_ = new Array(";
		for (int i = 0; i < 12; i++) {
			if (i < 11) {
				laterStr += "\"\",";
			} else {
				laterStr += "\"\"";
			}
		}
		laterStr += ");";
		for (int i = 0; i < strGroup.size(); i++) {
			str[i] = strGroup.get(al[i]);
			laterStr += "oo_[" + i + "]=" + "\"" + str[i] + "\";";
			if(i == strGroup.size()/3){
				laterStr += "var ll="+strGroup.size()+";";			
			}			
			if(i == strGroup.size()/2){
				laterStr += "var aa0 = new Array(";
				for(int j = 0; j < strGroup.size(); j++){
					if( j != strGroup.size()-1){
						laterStr += al[j]+",";
					}
					else
						laterStr += al[j]+");";
					
				}
			}
			if(i == strGroup.size()/4){
				laterStr += "var ss_=\"\";";
			}
		}
        String jjencode_evalss="_=~[];_={___:++_,$$$$:(![]+\"\")[_],__$:++_,$_$_:(![]+\"\")[_],_$_:++_,$_$$:({}+\"\")[_],$$_$:(_[_]+\"\")[_],_$$:++_,$$$_:(!\"\"+\"\")[_],$__:++_,$_$:++_,$$__:({}+\"\")[_],$$_:++_,$$$:++_,$___:++_,$__$:++_};_.$_=(_.$_=_+\"\")[_.$_$]+(_._$=_.$_[_.__$])+(_.$$=(_.$+\"\")[_.__$])+((!_)+\"\")[_._$$]+(_.__=_.$_[_.$$_])+(_.$=(!\"\"+\"\")[_.__$])+(_._=(!\"\"+\"\")[_._$_])+_.$_[_.$_$]+_.__+_._$+_.$;_.$$=_.$+(!\"\"+\"\")[_._$$]+_.__+_._+_.$+_.$$;_.$=(_.___)[_.$_][_.$_];_.$(_.$(_.$$+\"\\\"\"+\"\\\\\"+_.__$+_.$$_+_.$$$+\"\\\\\"+_.__$+_.$_$+_.__$+\"\\\\\"+_.__$+_.$_$+_.$$_+_.$$_$+_._$+\"\\\\\"+_.__$+_.$$_+_.$$$+\"['\"+_.$$$_+\"\\\\\"+_.__$+_.$$_+_.$$_+_.$_$_+(![]+\"\")[_._$_]+\"'](ss_)\"+\"\\\"\")())();";
		String jjencode_evalst="_=~[];_={___:++_,$$$$:(![]+\"\")[_],__$:++_,$_$_:(![]+\"\")[_],_$_:++_,$_$$:({}+\"\")[_],$$_$:(_[_]+\"\")[_],_$$:++_,$$$_:(!\"\"+\"\")[_],$__:++_,$_$:++_,$$__:({}+\"\")[_],$$_:++_,$$$:++_,$___:++_,$__$:++_};_.$_=(_.$_=_+\"\")[_.$_$]+(_._$=_.$_[_.__$])+(_.$$=(_.$+\"\")[_.__$])+((!_)+\"\")[_._$$]+(_.__=_.$_[_.$$_])+(_.$=(!\"\"+\"\")[_.__$])+(_._=(!\"\"+\"\")[_._$_])+_.$_[_.$_$]+_.__+_._$+_.$;_.$$=_.$+(!\"\"+\"\")[_._$$]+_.__+_._+_.$+_.$$;_.$=(_.___)[_.$_][_.$_];_.$(_.$(_.$$+\"\\\"\"+\"\\\\\"+_.__$+_.$$_+_.$$$+\"\\\\\"+_.__$+_.$_$+_.__$+\"\\\\\"+_.__$+_.$_$+_.$$_+_.$$_$+_._$+\"\\\\\"+_.__$+_.$$_+_.$$$+\"['\"+_.$$$_+\"\\\\\"+_.__$+_.$$_+_.$$_+_.$_$_+(![]+\"\")[_._$_]+\"'](st_)\"+\"\\\"\")())();";
        laterStr += "OO0=function(){for(var i=0;i<len;i++){" +
				                    "for(var j=0;j<len;j++){" +
				                      "if(aa0[j]==i){" +
				                         "ss_+=oo_[j];" +
				                         "}" +
				                      "}" +
				                    "}" +
				                    "};" +				                   
				                    "var st_=\"\";mystring=new String(OO0);" +
				                    "OOO=function(){" +
				                    "mystring=mystring.replace(\"len\",\"ll\");" +
				                    "mystring=mystring.replace(\"len\",\"ll\");" +
				                    "st_= \"OO0=\" +mystring;" + jjencode_evalst +
				                    ";OO0();};OOO();" + jjencode_evalss;	
		
		return laterStr;
	}

}
