package grammer_analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test {
	static ArrayList<String> termimalToken = new ArrayList<>();// 终结符号有哪些
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		//如何在ArrayList里边放数组
		ArrayList<String[]>  setName = new ArrayList<>();
		String a[] = {"1","2"};
		setName.add(a);
		System.out.println(setName.get(0)[0]);
		*/
		/*
		//如果在遍历ArrayList中间加入元素，不允许java.util.ConcurrentModificationException
		ArrayList<String>  a = new ArrayList<>();
		a.add("1");
		a.add("2");
		for(String s : a) {
			System.out.println(s);
			if(s.equals("1")) {
				a.add("相等啦");
			}
		}
		*/
		/*
		ArrayList<String>  a = new ArrayList<>();
		a.add("1");
		a.add("2");
		ListIterator<String> iterator = a.listIterator();
		while (iterator.hasNext()) {
			String next = iterator.next();
			System.out.println(next);
			if(next.equals("1")) {
				iterator.add("我发现1啦!!!");
			}
		}
		System.out.println("----------------");
		for(int i = 0;i<a.size(); i++) {
			System.out.println(a.get(i));
		}
		//输出 1 我发现1啦!!! 2  //但是顺序不好
		 */
		
		/*
		ArrayList<String>  a = new ArrayList<>();
		ArrayList<String>  newElement = new ArrayList<>();//新增加的元素
		a.add("1");
		a.add("2");
		newElement.add("11");
		newElement.add("22");
		int length = a.size();
		for(int i= 0;i<length;i++) {
			System.out.println(a.get(i));
			if(i==length-1) {
				a.addAll(newElement);
				length = length+newElement.size();
				newElement.clear();
			}
		}*/
		/*
		termimalToken.add("num");
		termimalToken.add("int");
		termimalToken.add("id");
		countSpecialToken("int+id");
		countSpecialToken("id+id");
		countSpecialToken("TF");*/
		/*
		String s  = "D.;S";
		String a = s.replace(".", "");
		//System.out.println(s.substring(2, s.length()));
		String docSplit[] = s.split("\\.");
		System.out.println("A.B分开"+Arrays.toString(docSplit));
		*/
		
		String str="say87";
		String s1=".A";
		String s2="A.B";
		String s3=".AB";
		String s4="A.BC";
		int index1 = s1.indexOf(".");//0
		String newS1 = s1.substring(0,index1+2);//index = 0 (0,1)="." (0,2) = ".A"
		
		int index2 = s2.indexOf(".");
		String newS = s2.substring(0,index2);//index = 1 (0,1)=A (0,2) = "A."
		
		System.out.println(newS1);
		System.out.println(moveToLeft(str, 2));//左移两位
	}
	
	
	static String moveToLeft(String str,int position) {
		String str1=str.substring(position);
		System.out.println("str1:"+str1);
		String str2=str.substring(0, position);
		return str1+str2;
	}
	
	//输入：doc后边的字符串，检查是否有特殊字符，如何移动
	//输出:(1)如果word[0]==""，说明没有匹配，老老实实移动一个字节(2)!="，说明有关键字，要变成 word[0]+"."+word[1]
	static void countSpecialToken(String sentence){
		//int+id  TF
		String[] word = {"",""};
		for(String s :termimalToken) {
			String REGEX = s;
			Pattern pattern = Pattern.compile(REGEX); //创建一个以字符串为标准的模式
			Matcher matcher = pattern.matcher(sentence);//判断是否match
			if(matcher.lookingAt()) {//有匹配的东西
				word[0] = s;
				word[1] = sentence.replaceFirst(s, "");
				break;
			}
			//System.out.println("lookingAt(): "+matcher.lookingAt());
		}
		System.out.println(Arrays.toString(word));
	}
}
