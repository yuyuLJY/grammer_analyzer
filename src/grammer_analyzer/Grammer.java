package grammer_analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Grammer {
	static ArrayList<String> grammerList = new ArrayList<>();
	static ArrayList<String> grammerListFirst = new ArrayList<>();//S'->.S
	static ArrayList<String> grammerListLast = new ArrayList<>();//S'->S.
	static ArrayList<String> gotoToken = new ArrayList<>();//goto表的符号
	static ArrayList<String> actionToken = new ArrayList<>();//action表的符号
	static ArrayList<ArrayList<String[]>> projectSet = new ArrayList<>();//存放所有state
	static ArrayList<ArrayList<String[]>> firstList = new ArrayList<>();//first集合
	public static void main(String[] args) {
		//TODO 找出终结符个数
		Grammer tt = new Grammer ();
		Map<String,Integer> actionNumber = new HashMap<>();
		actionNumber.put("a", 0);
		
		Map<String,Integer> gotoNumber = new HashMap<>();
		
		//TODO 读取文法
		tt.readGrammer("src/G.txt");//输入的必须是增广文法
		//TODO 如何求goto/action的符号有哪些
		gotoToken.add("S");gotoToken.add("A");gotoToken.add("B");
		actionToken.add("a");actionToken.add("b");actionToken.add("$");
		/*
		//验证是否读取了文法,ok
		for(String s:grammerList) {
			System.out.println(s);
		}
		*/
		
		//TODO 给文法的表达式加上点.
		for(String s:grammerList){
			String []list = s.split("->");
			String newStartGrammer = list[0]+"->"+"."+list[1];
			grammerListFirst.add(newStartGrammer);
		}
		
		//TODO 计算first集合
		
		countFirst();
		/*
		//验证是否在表达式前加点成功,ok
		for(String s:grammerListFirst) {
			System.out.println(s);
		}
		*/
		
		/*
		ArrayList<String[]>  startSet = new ArrayList<>();//状态1
		ArrayList<String[]>  oneOfSet = new ArrayList<>();//状态1
		String a[] = {grammerListFirst.get(0),"$"};
		startSet.add(a);
		oneOfSet.addAll(closure(startSet));//取出第一条
		projectSet.add(oneOfSet);//把状态加入状态大集合
		//检验闭包求的是否正确
		for(String[] s:oneOfSet) {
			System.out.println(s[0]+","+s[1]);
		}*/
		
	}
	
	//读入文法
	public void readGrammer(String name) {
		try{
			FileReader fr = new FileReader(name);
			BufferedReader bf = new BufferedReader(fr);
			String str;
			while((str = bf.readLine()) != null) {
				grammerList.add(str);
			}
			bf.close();
			fr.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	//创造项目的集合
	public ArrayList<String[]> createSet(int count) {	
		ArrayList<String[]>  oneOfSet = new ArrayList<>();
		return oneOfSet;
	}
	
	//计算闭包
	//传入：集合I
	//输出 集合的闭包
	static ArrayList<String[]> closure(ArrayList<String[]> set) {
		int length = set.size();
		ArrayList<String[]> newElement = new ArrayList<>();
		for(int i = 0;i<length;i++) {
			String grammer = set.get(i)[0];
			String lastToken = set.get(i)[1];//"$"
			//TODO 查看.后边的下一个符号是谁
			String[] grammerList = grammer.split("->");
			int index = grammerList[1].indexOf(".");//找到.的位置
			//s->.a  s->A.B  s->AB. s->a.  a->.ε
			//(index!=grammerList[1].length()-1)  //.不在字符串末尾
			if((index!=grammerList[1].length()-1) ) {//不是这种：S->A.
				//.后边不是空串
				String docFollow = String.valueOf(grammerList[1].charAt(index+1));//.后边的字符
				if(!docFollow.equals("ε") && !actionToken.contains(docFollow)) {//后边不是终结符
					System.out.println(docFollow);
					//TODO 计算lastToken
					if(index+2<=grammerList[1].length()-1) {//如果S后边还有字符，e.g.:s->A.BC
						//System.out.println("需要遍历后继的："+grammerList[1]);
						String docFollowFollow =  String.valueOf(grammerList[1].charAt(index+2));
						lastToken = lastToken +getFirst(docFollowFollow);
					}
					//找出所有的后继加进another列表
					String str = docFollow+"->";//S->
					for(String s :grammerListFirst) {//查找文法中包含S开头的式子
						if(s.indexOf(str)!=-1) {
							String a[] = {s,lastToken};
							newElement.add(a);
						}
					}
				}
			}
			if(i==length-1) {//遍历到最后了,查看是否再遍历
				set.addAll(newElement);
				length = length+newElement.size();
				newElement.clear();//清空下次使用
			}
		}
		return set;
	}
	
	//计算文法的first集合
	static void  countFirst() {
		
	}
	//求first集合
	static String getFirst(String s) {
		/**
		 * @return : |a|b
		 */
		String firstString = "|";
		return firstString;
	}
	
	//计算项目I的下一个符号X的闭包
	static void Goto() {
		
	}
}
