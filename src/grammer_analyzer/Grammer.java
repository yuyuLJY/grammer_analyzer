package grammer_analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class Grammer {
	static ArrayList<String> grammerList = new ArrayList<>();
	static ArrayList<String> grammerListFirst = new ArrayList<>();//S'->.S
	static ArrayList<String> grammerListLast = new ArrayList<>();//S'->S.
	static ArrayList<String> gotoToken = new ArrayList<>();//goto表的符号
	static ArrayList<String> actionToken = new ArrayList<>();//action表的符号
	static ArrayList<ArrayList<String[]>> projectSet = new ArrayList<>();//存放所有state
	static Map<String, String[]> stardardConbineGrammer = new HashMap<>();
	static Map<String, Set<Character>> firstList = new TreeMap<String, Set<Character>>();//first集合
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
		//countActionGotoToken();//把表列好
		/*
		String content = "";
        for(Map.Entry<String, Set<Character>> entry : firstList.entrySet()){
            content += entry.getKey() + "  :  " + entry.getValue() + "\n";
            System.out.println(entry.getKey() + "  :  " + entry.getValue());
        }*/
		/*
		//验证是否在表达式前加点成功,ok
		for(String s:grammerListFirst) {
			System.out.println(s);
		}
		*/
		
		
		ArrayList<String[]>  startSet = new ArrayList<>();//状态1
		ArrayList<String[]>  oneOfSet = new ArrayList<>();//状态1
		//String a[] = {grammerListFirst.get(0),"$"};
		String a[] = {"B->a.B","a|b|$"};
		startSet.add(a);
		oneOfSet.addAll(closure(startSet));//取出第一条
		projectSet.add(oneOfSet);//把状态加入状态大集合
		//检验闭包求的是否正确
		for(String[] s:oneOfSet) {
			System.out.println(s[0]+","+s[1]);
		}
		
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
					//System.out.println(docFollow);
					//TODO 计算lastToken
					if(index+2<=grammerList[1].length()-1) {//如果S后边还有字符，e.g.:s->A.BC
						//System.out.println("需要遍历后继的："+grammerList[1]);
						String docFollowFollow =  String.valueOf(grammerList[1].charAt(index+2));
						lastToken = lastToken +getLRFirst(docFollowFollow);
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
	static void  countFirst(){
		//TODO 先声称目标的输入：S->a|aB
		Map<String, ArrayList<String>> stardartRight = new TreeMap<String, ArrayList<String>>();
		for (String s :grammerList) {
			//System.out.println(s);
			String list[] = s.split("->");
			if(stardartRight.containsKey(list[0])) {//已经存在左边的式子
				//System.out.println("已经有重复的了，需要添加："+list[1]);
				stardartRight.get(list[0]).add(list[1]);
			}else {//还没有存在
				ArrayList<String> a = new ArrayList<String>();
				a.add(list[1]);
				stardartRight.put(list[0], a);
				//a.clear();
			}
		}
		
		//TODO 转换成标准的合并语法
		for(String m:stardartRight.keySet()) {
			String grammerLeft = m;
			ArrayList<String> grammerRight = stardartRight.get(m);
			String[] rightList = (String[])grammerRight.toArray(new String[grammerRight.size()]); 
			//System.out.println(m+" "+Arrays.toString(rightList));//构建完成
			stardardConbineGrammer.put(grammerLeft, rightList);
		}
		for(String k :stardardConbineGrammer.keySet()) {
			findEveryFirst(k, stardardConbineGrammer.get(k));//对每一个非终结符调用查找first的函数
		}
	}
	
	static Set<Character> findEveryFirst(String curNode, String[] rightNodes){
        if(firstList.containsKey(curNode)) return firstList.get(curNode); 
        Set<Character> st = new TreeSet<Character>();
        for(int i=0; i<rightNodes.length; ++i){
               for(int j=0; j<rightNodes[i].length(); ++j){
                   String nextNode = ""+rightNodes[i].charAt(j);
                   if(!stardardConbineGrammer.containsKey(nextNode)){//终结点
                       st.add(nextNode.charAt(0));
                       break;
                   }
                   else{//非终结点
                       if(j+1<rightNodes[i].length() && rightNodes[i].charAt(j+1)=='\''){
                           nextNode += rightNodes[i].charAt(j+1);
                           ++j;
                       }
                       if(stardardConbineGrammer.containsKey(nextNode)){
                           Set<Character> tmpSt = findEveryFirst(nextNode, stardardConbineGrammer.get(nextNode));
                           st.addAll(tmpSt);
                           if(!tmpSt.contains('$'))
                               break;
                       }
                   }
               }
        }
        firstList.put(curNode, st);
        return st;
	}
	
	//求first集合
	static String getLRFirst(String s) {
		/**
		 * @return : |a|b
		 */
		String firstString = "";
		for(Character ss : firstList.get(s)){
			if(ss!='ε') {//不包含空
				//System.out.println("jin "+ss);
				firstString = firstString+"|"+ss;
			}
		}
		//System.out.println("找："+firstString);
		return firstString;
	}
	
	//计算项目I的下一个符号X的闭包
	static void Goto() {
		
	}
}
