package grammer_analyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TOGrammer {
	static ArrayList<String> grammerList = new ArrayList<>();
	static ArrayList<String> grammerListFirst = new ArrayList<>();// S'->.S
	static ArrayList<String> grammerListLast = new ArrayList<>();// S'->S.
	static ArrayList<String> gotoToken = new ArrayList<>();// goto表的符号
	static ArrayList<String> actionToken = new ArrayList<>();// action表的符号
	static ArrayList<ArrayList<String[]>> projectSet = new ArrayList<>();// 存放所有state
	static Map<String, String[]> stardardConbineGrammer = new HashMap<>();// 文法用S->A|aA表示
	static Map<String, Set<String>> firstList = new TreeMap<String, Set<String>>();// first集合
	static ArrayList<String[]> gotoList = new ArrayList<>();// 跳转表 {"本状态 活前缀 跳转状态"}、{}
	static Map<String, Integer> token2Number = new LinkedHashMap<>();// a在表的第一个，b在表的第二个
	static String startToken ;
	static ArrayList<String[]> grammerBlock = new ArrayList<>();
	static ArrayList<String> termimalToken = new ArrayList<>();// 终结符号有哪些
	static Stack<String> stateStack = new Stack<String>();
	static Stack<String> tokenStack = new Stack<String>();
	static String[][] analyList = new String[projectSet.size()][token2Number.size()];
	public static void main(String[] args) {
		System.out.println("----------------Start------------");
		
		// TODO 读取文法
		readGrammer("src/G.txt");// 输入的必须是增广文法
		
		//TODO 构建block
		termimalToken.add("num");
		termimalToken.add("int");
		termimalToken.add("id");

		// TODO 给文法的表达式加上点=> S->.A
		for (String s : grammerList) {
			String[] list = s.split("->");
			String newStartGrammer = list[0] + "->" + "." + list[1];
			grammerListFirst.add(newStartGrammer);
		}
		// TODO 给文法的表达式末尾加上点=> S->.A
		for (String s : grammerList) {
			if(!s.equals("A->ε")){ 
				String newStartGrammer = s + ".";
				grammerListLast.add(newStartGrammer);
			}else {
				grammerListLast.add("A->.ε");
			}
		}
		
		
		// TODO 计算first集合
		countFirst();
		System.out.println("验证first集合");
		String content = ""; for(Entry<String, Set<String>> entry :firstList.entrySet()){ 
			content += entry.getKey() + "  :  " + entry.getValue()+ "\n"; 
			System.out.println(entry.getKey() + "  :  " + entry.getValue()); 
		}
		
		// TODO 如何求goto/action的符号有哪些
		countGotoActionList();
		
		 //验证goto/action的符号 
		 //for(String s :actionToken) {
		 //System.out.println("action:"+s); } 
		 //for(String s :gotoToken) {
		  //System.out.println("goto:"+s); }
		 
		ArrayList<String[]> startSet = new ArrayList<>();// 状态1
		ArrayList<String[]> oneOfSet = new ArrayList<>();// 状态1
		String a[] = { grammerListFirst.get(0), "$" };
		startSet.add(a);
		oneOfSet.addAll(closure(startSet));// 取出第一条
		projectSet.add(oneOfSet);// 把状态加入状态大集合

		// 检验闭包求的是否正确 
		for (String[] s : oneOfSet) { 
			System.out.println(s[0] + "," +s[1]); }
		
		// TODO 使用goto来遍历后面的状态
		int projectStateNumber = projectSet.size();
		System.out.println("当前状态个数：" + projectStateNumber);
		for (int i = 0; i < projectStateNumber; i++) {
			System.out.println("遍历状态：" + i);
			System.out.println("--------------输入状态satrt--------");
			for (String[] s : projectSet.get(i)) {
				System.out.println(s[0] + "," + s[1]);
			}
			System.out.println("--------------输入状态end-----------");
			GOTO(projectSet.get(i), i);// 状态集合，当前状态的标号
			if (i == projectStateNumber - 1) {// 检查state数目是否变化
				System.out.println("当前状态个数：" + projectSet.size());
				projectStateNumber = projectSet.size() - 1;
			}
		}
		

		// ------------------打印效果start--------------------------
		System.out.println("-----------总的状态---------：" + projectSet.size());
		for (int k = 0; k < projectSet.size(); k++) {
			System.out.println("............" + k);
			for (String[] s : projectSet.get(k)) {
				System.out.println(s[0] + "," + s[1]);
			}
		}

		System.out.println("-----------goto状态---------：" + gotoList.size());
		for (String[] s : gotoList) {
			System.out.println(s[0] + " " + s[1] + " " + s[2]);
		}
		// ------------------打印效果end--------------------------
		
		// 创建LR1分析表
		analyList = reateLRAnalyTable();
		
		// --------------------------查看分析表的效果start -------------------
		//success
		System.out.println("查看分析表的效果");
		System.out.printf("%-10s", "");
		for (String s : token2Number.keySet()) {
			System.out.printf("%-10s", s);
		}
		System.out.print("\n");
		for (int i = 0; i < projectSet.size(); i++) {
			System.out.printf("%-10d", i);
			for (int j = 0; j < token2Number.size(); j++) {
				System.out.printf("%-10s", analyList[i][j]);
			}
			System.out.print("\n");
		}
		// --------------------------查看分析表的效果end -------------------
		
		//TODO 开始建立栈来识别
		ArrayList<String> wordList = new ArrayList<>();
		wordList = readWordList("src/word1.txt",wordList);
		for(String s :wordList) {
			System.out.print(s+" ");
		}
		System.out.print("\n");
		
		//TODO 得出规约式子
		judge(wordList);
		
		
	}

	// 读入文法
	static void readGrammer(String name) {
		try {
			FileReader fr = new FileReader(name);
			BufferedReader bf = new BufferedReader(fr);
			String str;
			while ((str = bf.readLine()) != null) {
				grammerList.add(str);
			}
			bf.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static ArrayList<String> readWordList(String name,ArrayList<String> list){
		try {
			FileReader fr = new FileReader(name);
			BufferedReader bf = new BufferedReader(fr);
			String str;
			while ((str = bf.readLine()) != null) {
				list.add(str);
			}
			bf.close();
			fr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	// 计算goto/action表的元素
	static void countGotoActionList() {
		String content = "";
		// 先把头符号s'找出来
		startToken = grammerList.get(0).split("->")[0];
		for (Entry<String, Set<String>> entry : firstList.entrySet()) {
			if (!gotoToken.contains(entry.getKey()) && !startToken.equals(entry.getKey())) {
				gotoToken.add(entry.getKey());
			}
			for (String s : entry.getValue()) {
				if (!actionToken.contains(s) && (!s.equals("ε"))) {
					actionToken.add(String.valueOf(s));
				}
			}
			// content += entry.getKey() + " : " + entry.getValue()+ "\n";
			// System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}

	// 将I中的项向右移动一位
	static void GOTO(ArrayList<String[]> set, int currentStateNumber) {
		ArrayList<String> allStateSet = new ArrayList<>();
		// 得到这个set的全部状态
		for (String[] ss : set) {
			allStateSet.add(ss[0]);
		}
		for (String[] s : set) {// ["s->a.B","$"]
			String newGrammer = "";
			String docFollow = "";
			// TODO 需要向后移动一位
			String grammer = s[0];
			String grammerBack = s[1];
			System.out.println("GOTO:" + grammer);
			String list[] = grammer.split("->");
			String stringContainDot = list[1];
			//System.out.println("包含doc:"+stringContainDot);
			int index = stringContainDot.indexOf(".");// 找到.的位置 a.B
			// 分成三种情况讨论 (1)A.B (2).ABC .A .ε (3)A.
			if ((index == (stringContainDot.length() - 1)) || (index == 0 && stringContainDot.charAt(1) == 'ε')) {// (3)A. // .ε																													
				System.out.println("忽略");
			} else if (index == 0 && stringContainDot.charAt(1) != 'ε') {// (2).ABC  .A  .AB
				String removeDoc = stringContainDot.replace(".", "");
				String ifSepecial [] = countSpecialToken(removeDoc);//后边的东西是否是关键字
				//System.out.println("判断是否是关键字的串："+removeDoc);
				//System.out.println("结果："+Arrays.toString(ifSepecial));
				if(!ifSepecial[0].equals("")) {//终结符号    .int+A
					docFollow = ifSepecial[0];
					newGrammer  = ifSepecial[0] + "."+ifSepecial[1];
				}else {
					docFollow = String.valueOf(stringContainDot.charAt(index + 1));
					if (stringContainDot.length() == 2) {// (2).A
						newGrammer = stringContainDot.charAt(1) + ".";
					} else {// (2).ABC .AB
							// System.out.println("(2).ABC类型 "+stringContainDot.substring(2,2));
						if ((stringContainDot.length() - 1) == 2) {
							newGrammer = stringContainDot.charAt(1) + "." + stringContainDot.charAt(2);
						} else {
							newGrammer = stringContainDot.charAt(1) + "."
									+ stringContainDot.substring(2, stringContainDot.length());
						}
					}
				}
			} else {// (1)A.B A.BCC     A.intB
				//.号前面的一节
				System.out.println("右边"+stringContainDot);
				String docSplit[] = stringContainDot.split("\\.");
				System.out.println("A.B分开"+Arrays.toString(docSplit));
				String ifSepecial [] = countSpecialToken(docSplit[1]);//后边的东西是否是关键字
				if(!ifSepecial[0].equals("")) {//终结符号    A.intB
					docFollow = ifSepecial[0];
					newGrammer  = docSplit[0]+ifSepecial[0] + "."+ifSepecial[1];
				}else {
					docFollow = String.valueOf(stringContainDot.charAt(index + 1));
					if (stringContainDot.length() == 3) {// (1)A.B
						System.out.println("(1)A.B类型");
						String last = String.valueOf(stringContainDot.charAt(2));
						newGrammer = String.valueOf(stringContainDot.charAt(0)) + last + ".";
					} else {
						System.out.println("(1)A.BB类型");
						String last = stringContainDot.substring(index + 1, stringContainDot.length());
						newGrammer = stringContainDot.substring(0, index - 1) + stringContainDot.substring(index + 1) + "."
								+ last.substring(1, last.length());
					}
				}
			}
			// TODO 创建新的state
			// 不再set里边的才重新建立新的stateSet;不是终结符，不是空串才建立
			int flag = 1;
			if ((!list[1].equals(".ε") && (index != (stringContainDot.length() - 1)))) {
				if (!allStateSet.contains(newGrammer)) {// 不仅仅跟自身比较，还得跟projectSet所有的比较
					int nextStateNumber = projectSet.size();// 新建状态
					//TODO 
					//char nextToken = stringContainDot.charAt(index + 1);// .后边的符号是什么
					// currentStateNumber = projectSet.size()-1;
					newGrammer = list[0] + "->" + newGrammer;
					System.out.println("新组好的："+newGrammer);
					for (int kk = 0; kk < projectSet.size(); kk++) {
						// System.out.println("............");
						String[] lll = projectSet.get(kk).get(0);
						// System.out.println("重要："+lll[0]+" "+lll[1]);
						if (lll[0].equals(newGrammer) && lll[1].equals(grammerBack)) {
							System.out.println("已经存在了state");
							flag = 0;// 不再需要重新创建
							nextStateNumber = kk;// 调回以前的状态state
						}
					}
					// 声明一个三元数组{"本状态 活前缀 跳转状态"},记录下goto状态
					String[] gotoStateList = new String[3];
					gotoStateList[0] = String.valueOf(currentStateNumber);
					gotoStateList[1] = docFollow ;
					gotoStateList[2] = String.valueOf(nextStateNumber);
					gotoList.add(gotoStateList);
					if (flag == 1) {// 创建新的state
						// System.out.println("创建newGrammer:"+newGrammer+","+grammerBack);
						ArrayList<String[]> startSet = new ArrayList<>();// 状态1
						ArrayList<String[]> oneOfSet = new ArrayList<>();// 状态1
						String a[] = { newGrammer, grammerBack };
						startSet.add(a);// 构建出初始状态
						oneOfSet.addAll(closure(startSet));// 把闭包的所有状态都加入oneset集合
						projectSet.add(oneOfSet);// 把状态加入状态大集合
					}
				}
			}

		}
	}

	// 计算闭包
	// 传入：集合I
	// 输出 集合的闭包
	static ArrayList<String[]> closure(ArrayList<String[]> set) {
		int length = set.size();
		ArrayList<String[]> newElement = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			String grammer = set.get(i)[0];
			String lastToken = set.get(i)[1];// "$"
			String docFollow  = "";
			// TODO 查看.后边的下一个符号是谁
			String[] grammerList = grammer.split("->");
			int index = grammerList[1].indexOf(".");// 找到.的位置
			// s->.a s->A.B s->AB. s->a. a->.ε
			// (index!=grammerList[1].length()-1) //.不在字符串末尾
			if ((index != grammerList[1].length() - 1)) {// 不是这种：S->A.
				// .后边不是空串
				//TODO !!!!!!!修改 docFollow要判断是不是关键字 (TODO )
				String docFollowString = grammerList[1].replace(".", "");
				String ifSepecial [] = countSpecialToken(docFollowString);//后边的东西是否是关键字
				if(ifSepecial[0].equals("")) {
					docFollow = String.valueOf(grammerList[1].charAt(index + 1));// .后边的字符
				}else {
					docFollow  = ifSepecial[0];
				}
				if (!docFollow.equals("ε") && !actionToken.contains(docFollow)) {// 后边不是终结符
					// System.out.println(docFollow);
					// TODO 计算lastToken
					if (index + 2 <= grammerList[1].length() - 1) {// 如果S后边还有字符，e.g.:s->A.BC
						// System.out.println("需要遍历后继的："+grammerList[1]);
						String docFollowFollow = String.valueOf(grammerList[1].charAt(index + 2));
						lastToken = lastToken + getLRFirst(docFollowFollow,lastToken);
					}
					// 找出所有的后继加进another列表
					String str = docFollow + "->";// S->
					for (String s : grammerListFirst) {// 查找文法中包含S开头的式子
						if (s.indexOf(str) != -1) {
							String a[] = { s, lastToken };
							newElement.add(a);
						}
					}
				}
			}
			if (i == length - 1) {// 遍历到最后了,查看是否再遍历
				set.addAll(newElement);
				length = length + newElement.size();
				newElement.clear();// 清空下次使用
			}
		}
		return set;
	}
	
	//输入：doc后边的字符串，检查是否有特殊字符，如何移动
	//输出:(1)如果word[0]==""，说明没有匹配，老老实实移动一个字节(2)!="，说明有关键字，要变成 word[0]+"."+word[1]
	static String[] countSpecialToken(String sentence){
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
		return word;
	}
	
	// 计算文法的first集合
	static void countFirst() {
		// TODO 先声称目标的输入：S->a|aB
		Map<String, ArrayList<String>> stardartRight = new TreeMap<String, ArrayList<String>>();
		for (String s : grammerList) {
			// System.out.println(s);
			String list[] = s.split("->");
			if (stardartRight.containsKey(list[0])) {// 已经存在左边的式子
				// System.out.println("已经有重复的了，需要添加："+list[1]);
				stardartRight.get(list[0]).add(list[1]);
			} else {// 还没有存在
				ArrayList<String> a = new ArrayList<String>();
				a.add(list[1]);
				stardartRight.put(list[0], a);
				// a.clear();
			}
		}

		// TODO 转换成标准的合并语法
		for (String m : stardartRight.keySet()) {
			String grammerLeft = m;
			ArrayList<String> grammerRight = stardartRight.get(m);
			String[] rightList = (String[]) grammerRight.toArray(new String[grammerRight.size()]);
			// System.out.println(m+" "+Arrays.toString(rightList));//构建完成
			stardardConbineGrammer.put(grammerLeft, rightList);
		}
		for (String k : stardardConbineGrammer.keySet()) {
			System.out.println("查找first集合："+k+" "+Arrays.toString(stardardConbineGrammer.get(k)));
			findEveryFirst(k, stardardConbineGrammer.get(k));// 对每一个非终结符调用查找first的函数
		}
	}

	static Set<String> findEveryFirst(String curNode, String[] rightNodes) {
		String nextNode = "";
		if (firstList.containsKey(curNode))
			return firstList.get(curNode);
		Set<String> st = new TreeSet<String>();
		for (int i = 0; i < rightNodes.length; ++i) {//有几个 aB|a, 2个
			System.out.println("当前i:"+rightNodes[i]);
			for (int j = 0; j < rightNodes[i].length(); ++j) {//
				String[] ifSprcial = countSpecialToken(rightNodes[i]);
				if(ifSprcial[0].equals("")) {//没有特殊符号
					nextNode = "" + rightNodes[i].charAt(j);
				}else {
					nextNode = ifSprcial[0];
				}
				System.out.println("nextNode"+j+" "+nextNode);
				
				if (!stardardConbineGrammer.containsKey(nextNode)) {// 终结点
					st.add(nextNode);//直接加进去
					break;
				} else {// 非终结点
					/*if (j + 1 < rightNodes[i].length() && rightNodes[i].charAt(j + 1) == '\'') {
						nextNode += rightNodes[i].charAt(j + 1);
						++j;
					}*/
					if (stardardConbineGrammer.containsKey(nextNode)) {//S->BA 找B的first集合
						Set<String> tmpSt = findEveryFirst(nextNode, stardardConbineGrammer.get(nextNode));//遍历
						st.addAll(tmpSt);
						if (!tmpSt.contains("$"))
							break;
					}
				}
			}
		}
		firstList.put(curNode, st);
		return st;
	}

	// 求first集合
	static String getLRFirst(String s,String lastToken) {
		/**
		 * 如果lastToken里边有了就不要了
		 * @return : |a|b
		 */
		
		String firstString = "";
		//System.out.println("查找"+s+"的first集合");
		if(firstList.get(s) != null) {
			for (String ss : firstList.get(s)) {
				if (!(ss.equals("ε")) &&  (!lastToken.contains(String.valueOf(ss)))) {// 不包含空
					// System.out.println("jin "+ss);
					firstString = firstString + "|" + ss;
				}
			}
		}
		// System.out.println("找："+firstString);
		return firstString;
	}

	static String[][] reateLRAnalyTable() {
		// TODO 把goto和action表结合起来
		System.out.println("构建DFA状态机");
		int count = 0;
		for (String s : actionToken) {
			token2Number.put(s, count);
			count++;
		}
		
		if(!token2Number.keySet().contains("$")) {
			token2Number.put("$", count);
			count++;
		}
		for (String s : gotoToken) {
			token2Number.put(s, count);
			count++;
		}
		
		for (String[] s : gotoList) {//不出从没添加的
			//System.out.println(s[0] + " " + s[1] + " " + s[2]);
			if(!token2Number.keySet().contains(s[1])) {//还没包括的，添加进去
				token2Number.put(s[1], count);
				count++;
			}
		}
		
		
		for (String s : token2Number.keySet()) {
			System.out.println(s + " " + token2Number.get(s));
		}

		// 创建String[state个数][action+goto的个数]数组:
		String[][] analyList = new String[projectSet.size()][count];
		for (int i = 0; i < projectSet.size(); i++) {
			for (int j = 0; j < token2Number.size(); j++) {
				analyList[i][j] = "";
			}
		}
		for (int i = 0; i < projectSet.size(); i++) {// 从每一行开始填表
			// 三元数组{"本状态 活前缀 跳转状态"}
			for (String s[] : gotoList) {// 遍历跳转列表
				if (s[0].equals(String.valueOf(i))) {// 是改栏的状态
					if (actionToken.contains(s[1])) {// 是动作表，需要判断是规约还是移进
						// 先全填上移入
						analyList[i][token2Number.get(s[1])] = "S" + s[2];
					} else {// 是goto表的项，只需要填数字
						//System.out.println("error:"+s[2]);
						//System.out.println("index:"+s[1]);
						analyList[i][token2Number.get(s[1])] = s[2];
					}
				}
			}
		}

		System.out.println("-----------规约总结---------：" + projectSet.size());
		for (int k = 0; k < projectSet.size(); k++) {
			System.out.println("............" + k);
			for (String[] s : projectSet.get(k)) {
				if (grammerListLast.contains(s[0])) {// 含有终结状态
					if(!(s[0].split("->")[0].equals(startToken))) {//不是终结状态
						System.out.println(k + "含有终结状态:" + s[0]+","+s[1]);
						String[] rToken = s[1].split("\\|");
						System.out.println("分解效果："+Arrays.toString(rToken));
						for (String token : rToken) {
							System.out.println(k+" "+token+" "+token2Number.get(token)+" 规约式子："+ grammerListLast.indexOf(s[0]));
							analyList[k][token2Number.get(token)] = "R" + grammerListLast.indexOf(s[0]);
						}
					}else {//含有终结符的式子
						analyList[k][token2Number.get("$")] = "acc";
					}

				}
			}
		}
		return analyList;
	}
	
	static void judge(ArrayList<String> wordList){
		stateStack.push(new String("0"));//状态栈的开始是0
		String action = "";//动作
		String token = "";//当前字符
		String currentState = "";//当前状态
		String currentToken = "";//当前符号栈栈顶状态
		String stackPeek = "";
		int count=0;
		stackPeek = stateStack.peek();
		int hang = Integer.valueOf(stackPeek);//行坐标
		token = wordList.get(count);
		int lie = token2Number.get(token);//列坐标
		while(!action.equals("acc")) {
			action = analyList[hang][lie];
			System.out.println(action);
			if(action.contains("S")) {//S1
				//压入状态站和符号
				System.out.println("移入");
				tokenStack.push(new String(token));
				currentState = action.replace("S","");
				stateStack.push(new String(currentState));
				count++;
				token = wordList.get(count);
				hang = Integer.valueOf(stackPeek);
				lie = token2Number.get(token);
			}else {//R1
				System.out.println("规约");
				String index = action.replace("R","");
				String rString = grammerListLast.get(Integer.valueOf(index));//用哪个式子来规约
				System.out.println("答案"+rString);
				String right = rString.split("->")[1];//int +BT
				String left = rString.split("->")[0];
				
				//TODO 把右边的东西放进一个list
				ArrayList<String> rWord = new ArrayList<>();
				int specialIndex = 0;
				String specilaRight =right;
				while(specialIndex<right.length()) {
					String ifSepecial [] = countSpecialToken(specilaRight );
					if(!ifSepecial[0].equals("")) {//是关键字
						rWord.add(ifSepecial[0]);
						specilaRight  = ifSepecial[1];//剩下的串
						specialIndex = specialIndex+ifSepecial[0].length();
					}else {
						rWord.add(String.valueOf(right.charAt(specialIndex)));
						specialIndex++;
					}
				}
				
				//把符号栈里边相同的弹出去
				for(String s: rWord) {
					if(tokenStack.peek().equals(s)) {//符号栈跟规约右边
						tokenStack.pop();
						stateStack.pop();
					}
				}
				//弹出完毕，把右边放进来
				tokenStack.push(new String(left));
				currentState = stateStack.peek();
				currentToken = tokenStack.peek();
				hang = Integer.valueOf(currentState);
				lie = Integer.valueOf(currentToken);
			}
		}
	}
	
	
	
}
