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
	static ArrayList<String> gotoToken = new ArrayList<>();// goto��ķ���
	static ArrayList<String> actionToken = new ArrayList<>();// action��ķ���
	static ArrayList<ArrayList<String[]>> projectSet = new ArrayList<>();// �������state
	static Map<String, String[]> stardardConbineGrammer = new HashMap<>();// �ķ���S->A|aA��ʾ
	static Map<String, Set<String>> firstList = new TreeMap<String, Set<String>>();// first����
	static ArrayList<String[]> gotoList = new ArrayList<>();// ��ת�� {"��״̬ ��ǰ׺ ��ת״̬"}��{}
	static Map<String, Integer> token2Number = new LinkedHashMap<>();// a�ڱ�ĵ�һ����b�ڱ�ĵڶ���
	static String startToken ;
	static ArrayList<String[]> grammerBlock = new ArrayList<>();
	static ArrayList<String> termimalToken = new ArrayList<>();// �ս��������Щ
	static Stack<String> stateStack = new Stack<String>();
	static Stack<String> tokenStack = new Stack<String>();
	static String[][] analyList = new String[projectSet.size()][token2Number.size()];
	static ArrayList<String> errorInfoListMore = new ArrayList<>();// ��¼������Ϣ
	static ArrayList<String> errorInfoListLess = new ArrayList<>();// ��¼������Ϣ
	public static void main(String[] args) {
		System.out.println("----------------Start------------");
		String GName[] = {"G_if_else.txt","G_assignment.txt","G_declear.txt","G_declare_assignment.txt","G_while.txt","G.txt"};
		String wordName[] = {"word_if_else.txt","word_assignment.txt","word_declare.txt","word_declare_assignment.txt","word_while.txt","word.txt"};
		String oneWordName = "src/"+wordName[3];
		
		// TODO ��ȡ�ķ�
		readGrammer("src/"+GName[3]);// ����ı����������ķ�
		
		//TODO ����block
		termimalToken.add("num");
		termimalToken.add("int");
		termimalToken.add("id");
		termimalToken.add("E'");
		termimalToken.add("E''");
		termimalToken.add("else");
		termimalToken.add("if");
		termimalToken.add("&&");
		termimalToken.add(";");
		termimalToken.add("digit");
		termimalToken.add("do");
		termimalToken.add("then");
		termimalToken.add("while");
		//��ӡ���ķ�
		for(int i = 0;i<grammerList.size();i++) {
			System.out.println(i+" "+grammerList.get(i));
		}
		
		// TODO ���ķ��ı��ʽ���ϵ�=> S->.A
		for (String s : grammerList) {
			String[] list = s.split("->");
			String newStartGrammer = list[0] + "->" + "." + list[1];
			grammerListFirst.add(newStartGrammer);
		}
		// TODO ���ķ��ı��ʽĩβ���ϵ�=> S->.A
		for (String s : grammerList) {
			if(!s.equals("A->��")){ 
				String newStartGrammer = s + ".";
				grammerListLast.add(newStartGrammer);
			}else {
				grammerListLast.add("A->.��");
			}
		}
		
		
		// TODO ����first����
		countFirst();
		System.out.println("��֤first����");
		String content = ""; for(Entry<String, Set<String>> entry :firstList.entrySet()){ 
			content += entry.getKey() + "  :  " + entry.getValue()+ "\n"; 
			System.out.println(entry.getKey() + "  :  " + entry.getValue()); 
		}
		
		
		// TODO �����goto/action�ķ�������Щ
		countGotoActionList();
		
		 //��֤goto/action�ķ��� 
		 //for(String s :actionToken) {
		 //System.out.println("action:"+s); } 
		 //for(String s :gotoToken) {
		  //System.out.println("goto:"+s); }
		 
		ArrayList<String[]> startSet = new ArrayList<>();// ״̬1
		ArrayList<String[]> oneOfSet = new ArrayList<>();// ״̬1
		String a[] = { grammerListFirst.get(0), "$" };
		startSet.add(a);
		oneOfSet.addAll(closure(startSet));// ȡ����һ��
		projectSet.add(oneOfSet);// ��״̬����״̬�󼯺�

		// ����հ�����Ƿ���ȷ 
		for (String[] s : oneOfSet) { 
			System.out.println(s[0] + "," +s[1]); }
		
		
		// TODO ʹ��goto�����������״̬
		int projectStateNumber = projectSet.size();
		System.out.println("��ǰ״̬������" + projectStateNumber);
		for (int i = 0; (i < projectStateNumber); i++) {
			System.out.println("����״̬��" + i);
			System.out.println("--------------����״̬satrt--------");
			for (String[] s : projectSet.get(i)) {
				System.out.println(s[0] + "," + s[1]);
			}
			System.out.println("--------------����״̬end-----------");
			GOTO1(projectSet.get(i), i);// ״̬���ϣ���ǰ״̬�ı��
			System.out.println("��ǰ״̬������" + projectSet.size()+" i"+(projectStateNumber - 1));
			if (i == projectStateNumber - 1) {// ���state��Ŀ�Ƿ�仯,�����һ����
				projectStateNumber = projectSet.size();
			}
		}
		

		// ------------------��ӡЧ��start--------------------------
		System.out.println("-----------�ܵ�״̬---------��" + projectSet.size());
		for (int k = 0; k < projectSet.size(); k++) {
			System.out.println("............" + k);
			for (String[] s : projectSet.get(k)) {
				System.out.println(s[0] + "," + s[1]);
			}
		}

		System.out.println("-----------goto״̬---------��" + gotoList.size());
		for (String[] s : gotoList) {
			System.out.println(s[0] + " " + s[1] + " " + s[2]);
		}
		// ------------------��ӡЧ��end--------------------------
		
		
		// ����LR1������
		analyList = reateLRAnalyTable();
		
		// --------------------------�鿴�������Ч��start -------------------
		//success
		System.out.println("�鿴�������Ч��");
		System.out.printf("%-7s", "");
		for (String s : token2Number.keySet()) {
			System.out.printf("%-7s", s);
		}
		System.out.print("\n");
		for (int i = 0; i < projectSet.size(); i++) {
			System.out.printf("%-7d", i);
			for (int j = 0; j < token2Number.size(); j++) {
				System.out.printf("%-7s", analyList[i][j]);
			}
			System.out.print("\n");
		}
		// --------------------------�鿴�������Ч��end -------------------
		
		//TODO ��ʼ����ջ��ʶ��
		ArrayList<String> wordList = new ArrayList<>();
		wordList = readWordList(oneWordName,wordList);
		for(String s :wordList) {
			System.out.print(s+" ");
		}
		System.out.print("\n");
		
		//TODO �ó���Լʽ��
		judge(wordList);
		
		
	}

	// �����ķ�
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
	
	// ����goto/action���Ԫ��
	static void countGotoActionList() {
		String content = "";
		// �Ȱ�ͷ����s'�ҳ���
		startToken = grammerList.get(0).split("->")[0];
		for (Entry<String, Set<String>> entry : firstList.entrySet()) {
			//GOTO�����п�ͷ������ && !startToken.equals(entry.getKey()
			if (!gotoToken.contains(entry.getKey())) {
				gotoToken.add(entry.getKey());
			}
			for (String s : entry.getValue()) {
				if (!actionToken.contains(s) && (!s.equals("��"))) {
					actionToken.add(String.valueOf(s));
				}
			}
			// content += entry.getKey() + " : " + entry.getValue()+ "\n";
			// System.out.println(entry.getKey() + " : " + entry.getValue());
		}
	}

	//��������nλ�ַ�������
	static String moveToLeft(String str,int position) {
		String str1=str.substring(position);
		String str2=str.substring(0, position);
		return str1+str2;
	}
	
	//��дGOTO����
	//���룺��1��һ��set��һ��״̬���ϣ���2����ǰ��state�����
	//Ŀ�꣺��¼����ת��״̬���͵�����հ��ĺ������ó�������״̬����
	static void GOTO1(ArrayList<String[]> set, int currentStateNumber) {
		//TODO �����޸�TODO����
		Map<String, Set<String>> countNewState = new TreeMap<String, Set<String>>();//�����м�����ת		
		for (int i = 0;i<set.size();i++) {// ["s->a.B","$"]
			String grammer = set.get(i)[0];
			String grammerBack = set.get(i)[1];
			System.out.println("GOTO:" + grammer);
			//leftRightlist[0]:s leftRightlist[1]:a.B
			String leftRightlist[] = grammer.split("->");
			String leftGrammer = leftRightlist[0];//leftRightlist[0]:s
			String rightGrammer = leftRightlist[1];//leftRightlist[1]:a.B
			int index = rightGrammer.indexOf(".");// �ҵ�.��λ�� a.B
			//TODO �����doc����һ���ַ���ʲô
			//�����S->A.�Ͳ��Ž�ȥ�ˣ���Ϊû����һ��״̬��
			if(index!=(rightGrammer.length()-1)) {
			String ifSepecial [] = countSpecialToken(rightGrammer.substring(index+1));//��ߵĶ����Ƿ��ǹؼ���
			String docFollow="";
			if(!ifSepecial[0].equals("")) {//�ս����    .int+A
				docFollow = ifSepecial[0];
			}else {
				docFollow = String.valueOf(rightGrammer.charAt(index + 1));
			}
			if(countNewState.containsKey(docFollow)) {//����Ѿ����������key
				//��¼���ұߵ��﷨��λ�� L  :  [1, 5]
				String recordIndex = String.valueOf(i);
				countNewState.get(docFollow).add(recordIndex);
			}else {
				Set<String> oneRightGrammer = new TreeSet<>();
				String recordIndex = String.valueOf(i);
				oneRightGrammer.add(recordIndex);
				countNewState.put(docFollow, oneRightGrammer );
			}
			}
		}
		System.out.println("�����е���״̬��");
		for(Entry<String, Set<String>> entry :countNewState.entrySet()){ 
			//content += entry.getKey() + "  :  " + entry.getValue()+ "\n"; 
			System.out.println(entry.getKey() + "  :  " + entry.getValue()); 
		}
		//TODO ��ʹ��һ��map<String ,set>���̻�Ҫ�м����µ�state
		for(String oneStateKey : countNewState.keySet()) {//ÿ�ε�ѭ������һ��״̬
			//System.out.println("oneStateKey:"+oneStateKey);
			Set<String> set1 = countNewState.get(oneStateKey);
			ArrayList<String[]> oneOfSet = new ArrayList<>();// ״̬1
			int nextStateNumber = projectSet.size();// �½�״̬�����
			for(String value : set1) {//����set��ߵ�Ԫ��
				String grammer1 = set.get(Integer.valueOf(value))[0];//������Ǿ����index
				String grammer1Back =  set.get(Integer.valueOf(value))[1];
				System.out.println("GOTOoneset:" + grammer1);
				//leftRightlist[0]:s leftRightlist[1]:a.B
				String leftRightlist[] = grammer1.split("->");
				String leftGrammer = leftRightlist[0];//leftRightlist[0]:s
				String rightGrammer = leftRightlist[1];//leftRightlist[1]:a.B
				int index = rightGrammer.indexOf(".");// �ҵ�.��λ�� a.B
				//û��S->A.��S->.�����������
				//ֻ�÷ֳ��������(1) A.B .B  (2)A.BC .BC  
				String docFollow = rightGrammer.substring(index+1);
				String ifSepecial [] = countSpecialToken(docFollow);//��ߵĶ����Ƿ��ǹؼ���
				//System.out.println("�ж��Ƿ��ǹؼ��ֵĴ���"+removeDoc);
				//System.out.println("�����"+Arrays.toString(ifSepecial));
				//TODO ���doc������ַ�
				String docFollowToken = "";
				int docFollowTokenLength = 0;
				String secondFollowString="";
				if(!ifSepecial[0].equals("")) {//�ս����    .int+A
					docFollowToken = ifSepecial[0];
					docFollowTokenLength =  docFollowToken.length();
					secondFollowString = ifSepecial[1];
				}else {
					docFollowToken =  String.valueOf(rightGrammer.charAt(index+1));
					docFollowTokenLength =  1;
					if(index+2<=rightGrammer.length()-1) {//.BC  .BCD
						secondFollowString = rightGrammer.substring(index+2);;
					}
					//����Ļ����ǿմ�
				}
				
				String newGrammer="";
				if(index==0) {//.A .AB����ʽ
					newGrammer = leftGrammer+"->"+docFollowToken+"."+secondFollowString;
				}else {//A.B  A.BC
					newGrammer = leftGrammer+"->"+rightGrammer.substring(0, index)+docFollowToken+"."+secondFollowString;
				}
				System.out.println("����ŵĴ�"+newGrammer);
				
				//TODO �ж�����﷨�Ƿ��Ѿ����ˣ����˵Ļ��Ͳ���Ҫ����ȥ������
				int flagIfadd=1;
				for (int kk = 0; kk < projectSet.size(); kk++) {
					// System.out.println("............");
					String[] lll = projectSet.get(kk).get(0);
					// System.out.println("��Ҫ��"+lll[0]+" "+lll[1]);
					if (lll[0].equals(newGrammer) && lll[1].equals(grammer1Back)) {
						System.out.println("�Ѿ�������state");
						flagIfadd = 0;// ������Ҫ���´���
						nextStateNumber = kk;// ������ǰ��״̬state
					}
				}
					
				//TODO �ж��Ƿ�����﷨���ѣ����ǷŽ�����﷨�ıհ�
				if(flagIfadd!=0) {
				ArrayList<String[]> startSet = new ArrayList<>();// ״̬1
				String a[] = { newGrammer, grammer1Back };
				startSet.add(a);// ��������ʼ״̬
				oneOfSet.addAll(closure(startSet));// �ѱհ�������״̬������oneset����
				}
			}
			//��鹹���ļ����Ƿ���ȷ
			System.out.println("-------------start��������state"+projectSet.size());
			for(String[] oneset: oneOfSet) {
				System.out.println("��������state:"+oneset[0]+" "+oneset[1]);
			}
			System.out.println("-------------end��������state"+projectSet.size());
			//����û����״̬�Ĳ�����Ҳ����״̬goto�����
			String[] gotoStateList = new String[3];
			gotoStateList[0] = String.valueOf(currentStateNumber);
			gotoStateList[1] = oneStateKey ;
			gotoStateList[2] = String.valueOf(nextStateNumber);
			if(!actionToken.contains(oneStateKey) && !(gotoToken.contains(oneStateKey))) {//������ӽ�action����
				actionToken.add(oneStateKey);
			}
			gotoList.add(gotoStateList);
			if(oneOfSet.size()!=0) {
			projectSet.add(oneOfSet);// ��״̬����״̬�󼯺�
			}
			}
			
		//TODO ��ÿ�������д���
	}
	
	
	// ��I�е��������ƶ�һλ
	static void GOTO(ArrayList<String[]> set, int currentStateNumber) {
		ArrayList<String> allStateSet = new ArrayList<>();
		// �õ����set��ȫ��״̬
		for (String[] ss : set) {
			allStateSet.add(ss[0]);
		}
		for (String[] s : set) {// ["s->a.B","$"]
			String newGrammer = "";
			String docFollow = "";
			// TODO ��Ҫ����ƶ�һλ
			String grammer = s[0];
			String grammerBack = s[1];
			System.out.println("GOTO:" + grammer);
			String list[] = grammer.split("->");
			String stringContainDot = list[1];
			//System.out.println("����doc:"+stringContainDot);
			int index = stringContainDot.indexOf(".");// �ҵ�.��λ�� a.B
			// �ֳ������������ (1)A.B (2).ABC .A .�� (3)A.
			if ((index == (stringContainDot.length() - 1)) || (index == 0 && stringContainDot.charAt(1) == '��')) {// (3)A. // .��																													
				System.out.println("����");
			} else if (index == 0 && stringContainDot.charAt(1) != '��') {// (2).ABC  .A  .AB
				String removeDoc = stringContainDot.replace(".", "");
				String ifSepecial [] = countSpecialToken(removeDoc);//��ߵĶ����Ƿ��ǹؼ���
				//System.out.println("�ж��Ƿ��ǹؼ��ֵĴ���"+removeDoc);
				//System.out.println("�����"+Arrays.toString(ifSepecial));
				if(!ifSepecial[0].equals("")) {//�ս����    .int+A
					docFollow = ifSepecial[0];
					newGrammer  = ifSepecial[0] + "."+ifSepecial[1];
				}else {
					docFollow = String.valueOf(stringContainDot.charAt(index + 1));
					if (stringContainDot.length() == 2) {// (2).A
						newGrammer = stringContainDot.charAt(1) + ".";
					} else {// (2).ABC .AB
							// System.out.println("(2).ABC���� "+stringContainDot.substring(2,2));
						if ((stringContainDot.length() - 1) == 2) {
							newGrammer = stringContainDot.charAt(1) + "." + stringContainDot.charAt(2);
						} else {
							newGrammer = stringContainDot.charAt(1) + "."
									+ stringContainDot.substring(2, stringContainDot.length());
						}
					}
				}
			} else {// (1)A.B A.BCC     A.intB
				//.��ǰ���һ��
				System.out.println("�ұ�"+stringContainDot);
				String docSplit[] = stringContainDot.split("\\.");
				System.out.println("A.B�ֿ�"+Arrays.toString(docSplit));
				String ifSepecial [] = countSpecialToken(docSplit[1]);//��ߵĶ����Ƿ��ǹؼ���
				if(!ifSepecial[0].equals("")) {//�ս����    A.intB
					docFollow = ifSepecial[0];
					newGrammer  = docSplit[0]+ifSepecial[0] + "."+ifSepecial[1];
				}else {
					docFollow = String.valueOf(stringContainDot.charAt(index + 1));
					if (stringContainDot.length() == 3) {// (1)A.B
						System.out.println("(1)A.B����");
						String last = String.valueOf(stringContainDot.charAt(2));
						newGrammer = String.valueOf(stringContainDot.charAt(0)) + last + ".";
					} else {
						System.out.println("(1)A.BB����");
						String docSplit1[] = stringContainDot.split("\\.");
						String secondChar = String.valueOf(docSplit1[1].charAt(0));
						System.out.println("nextCode��"+secondChar);
						System.out.println("�󲿷֣�"+docSplit1[1]);
						//String last = docSplit1[1].replaceFirst(secondChar, "");
						//String last  = moveToLeft(docSplit1[1],1);
						String last = docSplit1[1].substring(1);
						System.out.println("�ƶ���Ĵ���"+last);
						newGrammer =docSplit1[0] + secondChar + "."
								+ last;
					}
				}
			}
			// TODO �����µ�state
			// ����set��ߵĲ����½����µ�stateSet;�����ս�������ǿմ��Ž���
			int flag = 1;
			if ((!list[1].equals(".��") && (index != (stringContainDot.length() - 1)))) {
				if (!allStateSet.contains(newGrammer)) {// ������������Ƚϣ����ø�projectSet���еıȽ�
					int nextStateNumber = projectSet.size();// �½�״̬
					//TODO 
					//char nextToken = stringContainDot.charAt(index + 1);// .��ߵķ�����ʲô
					// currentStateNumber = projectSet.size()-1;
					newGrammer = list[0] + "->" + newGrammer;
					System.out.println("����õģ�"+newGrammer);
					for (int kk = 0; kk < projectSet.size(); kk++) {
						// System.out.println("............");
						String[] lll = projectSet.get(kk).get(0);
						// System.out.println("��Ҫ��"+lll[0]+" "+lll[1]);
						if (lll[0].equals(newGrammer) && lll[1].equals(grammerBack)) {
							System.out.println("�Ѿ�������state");
							flag = 0;// ������Ҫ���´���
							nextStateNumber = kk;// ������ǰ��״̬state
						}
					}
					// ����һ����Ԫ����{"��״̬ ��ǰ׺ ��ת״̬"},��¼��goto״̬
					String[] gotoStateList = new String[3];
					gotoStateList[0] = String.valueOf(currentStateNumber);
					gotoStateList[1] = docFollow ;
					gotoStateList[2] = String.valueOf(nextStateNumber);
					if(!actionToken.contains(docFollow) && !(gotoToken.contains(docFollow))) {//������ӽ�action����
						actionToken.add(docFollow);
					}
					gotoList.add(gotoStateList);
					if (flag == 1) {// �����µ�state
						// System.out.println("����newGrammer:"+newGrammer+","+grammerBack);
						ArrayList<String[]> startSet = new ArrayList<>();// ״̬1
						ArrayList<String[]> oneOfSet = new ArrayList<>();// ״̬1
						String a[] = { newGrammer, grammerBack };
						startSet.add(a);// ��������ʼ״̬
						System.out.println("����հ���"+newGrammer);//���µıհ�
						oneOfSet.addAll(closure(startSet));// �ѱհ�������״̬������oneset����
						projectSet.add(oneOfSet);// ��״̬����״̬�󼯺�
					}
				}
			}

		}
	}

	// ����հ�
	// ���룺����I
	// ��� ���ϵıհ�
	static ArrayList<String[]> closure(ArrayList<String[]> set) {
		int length = set.size();
		ArrayList<String[]> newElement = new ArrayList<>();
		for (int i = 0; i < length; i++) {
			System.out.println("��հ��Ĵ�"+set.get(i)[0]+" "+set.get(i)[1]);
			String grammer = set.get(i)[0];
			String lastToken = set.get(i)[1];// "$"
			String docFollow  = "";
			// TODO �鿴.��ߵ���һ��������˭
			String[] grammerList = grammer.split("->");
			int index = grammerList[1].indexOf(".");// �ҵ�.��λ��
			// s->.a s->A.B s->AB. s->a. a->.��
			// (index!=grammerList[1].length()-1) //.�����ַ���ĩβ
			if ((index != grammerList[1].length() - 1)) {// �������֣�S->A.
				// .��߲��ǿմ�
				//TODO !!!!!!!�޸� docFollowҪ�ж��ǲ��ǹؼ��� (TODO )
				String docFollowString = grammerList[1].split("\\.")[1];
				String ifSepecial [] = countSpecialToken(docFollowString);//��ߵĶ����Ƿ��ǹؼ���
				if(ifSepecial[0].equals("")) {
					docFollow = String.valueOf(grammerList[1].charAt(index + 1));// .��ߵ��ַ�
				}else {
					docFollow  = ifSepecial[0];
				}
				System.out.println(".........................��ߵ���"+docFollow);
				if (!docFollow.equals("��") && !actionToken.contains(docFollow)) {//��߲����ս��,���ǿմ�
					// System.out.println(docFollow);
					// TODO ����lastToken
					if (index + 2 <= grammerList[1].length() - 1) {// ���S��߻����ַ���e.g.:s->A.BC
						// System.out.println("��Ҫ������̵ģ�"+grammerList[1]);//S->L=R
						//�ж�followfollow�ǲ��ǹؼ��֣���docFollow�滻����ʣ�µĴ��ж϶�ͷ���ǲ��ǹؼ���
						String docFollowFollowString = docFollowString.substring(docFollow.length());//=R
						String ifSepecial1 [] = countSpecialToken(docFollowFollowString);//��ߵĶ����Ƿ��ǹؼ���
						//TODO !!!!!!ǰ������ս���ţ�һ��Ҫ�����ȫ����Ȼ�������
						//��ΪĬ������жϻ��������ս�����ͻ���Ϊ��B��д��ĸ
						if(ifSepecial1[0].equals("")) {//��߲��������ս��
							//�������ֿ���(1)�Ǵ�д��ĸB 
							System.out.println("�ڶ����ַ�followfollow:"+docFollowFollowString);
							String docFollowFollow = String.valueOf(docFollowFollowString.charAt(0));//+������B
							if(gotoToken.contains(docFollowFollow)) {//�Ǵ�д��ĸ
								//TODO ԭ��д����s->A.BC��C�����մ���������ӣ��������մ�����ֱ�Ӹ���
								if(firstList.get(docFollowFollow).contains("��")) {
									lastToken = lastToken + getLRFirst(docFollowFollow,lastToken);
								}else {
									//�����������
									for(String k : firstList.get(docFollowFollow)) {
										lastToken = lastToken+"|"+k;
									}
								}
								
							}else {
								if(!lastToken.contains(docFollowFollow)) {//+ �����ֽڵ��ս����
									//TODO ԭ��д����
									//lastToken = lastToken + "|"+docFollowFollow;
									lastToken = docFollowFollow;
								}
							}
						}else {
							//TODO �ж����ԭ����û�У����ڲżӽ�ȥ
							if(!lastToken.contains(ifSepecial1[0])) {
								lastToken = lastToken + "|"+ifSepecial1[0];
							}
						}

					}
					
					// �ҳ����еĺ�̼ӽ�another�б�
					String str = docFollow + "->";// S->
					System.out.println("��հ�����ͷ����"+str);
					for (String s : grammerListFirst) {// �����ķ��а���S��ͷ��ʽ��
						if ((s.indexOf(str) != -1)) {//ǰ׺���ķ����ʽ��ͬ
							//����sû�г�����set��߹�
							int ifSetSelf = 1;
							//������������E->.E+T����
							
							//--------------------�¼��ϵ�-------------------
							String selfType[] = s.split("->");
							String selfDocFollow = String.valueOf(selfType[1].charAt(1));
							if(selfType[0].equals(selfDocFollow)) {//E.equal(E)
								//lastTokenҪ�����һЩ����
								String ifSepecial2 [] = countSpecialToken(selfType[1].substring(2));//+T
								if((!ifSepecial2[0].equals("")) && (!lastToken.contains(ifSepecial2[0]))) {//�Ƕ���ַ���ɵ��ս����
										lastToken = lastToken + "|"+ifSepecial2[0];
								}else {
									String doubleFollow = String.valueOf(selfType[1].charAt(2));
									if((!gotoToken.contains(doubleFollow)) &&(!lastToken.contains(doubleFollow))) {//�Ǵ�д��ĸ
										lastToken = lastToken + "|"+ doubleFollow;
									}
								}
							}
							//--------------------�¼��ϵ�-------------------
							
							for(String []oneG:set) {
								if((oneG[0].contains(s))) {//���ҵ��ı��ʽ��set����Ѿ�����
									//�ҵ���E->E+T,lastToken����һ��+��
									if(!oneG[1].contains(lastToken)) {
										oneG[1] = oneG[1]+"|"+lastToken;
									}
									ifSetSelf = 0 ;
								}
							}
							if(ifSetSelf==1) {
								String a[] = { s, lastToken };
								newElement.add(a);
							}
						}
					}
				}
			}
			if (i == length - 1) {// �����������,�鿴�Ƿ��ٱ���
				set.addAll(newElement);
				length = length + newElement.size();
				newElement.clear();// ����´�ʹ��
			}
		}
		return set;
	}
	
	//���룺doc��ߵ��ַ���������Ƿ��������ַ�������ƶ�
	//���:(1)���word[0]==""��˵��û��ƥ�䣬����ʵʵ�ƶ�һ���ֽ�(2)!="��˵���йؼ��֣�Ҫ��� word[0]+"."+word[1]
	static String[] countSpecialToken(String sentence){
		//int+id  TF
		String[] word = {"",""};
		for(String s :termimalToken) {
			String REGEX = s;
			Pattern pattern = Pattern.compile(REGEX); //����һ�����ַ���Ϊ��׼��ģʽ
			Matcher matcher = pattern.matcher(sentence);//�ж��Ƿ�match
			if(matcher.lookingAt()) {//��ƥ��Ķ���
				word[0] = s;
				word[1] = sentence.replaceFirst(s, "");
				break;
			}
			//System.out.println("lookingAt(): "+matcher.lookingAt());
		}
		//System.out.println(Arrays.toString(word));
		return word;
	}
	
	// �����ķ���first����
	static void countFirst() {
		// TODO ������Ŀ������룺S->a|aB
		Map<String, ArrayList<String>> stardartRight = new TreeMap<String, ArrayList<String>>();
		for (String s : grammerList) {
			// System.out.println(s);
			String list[] = s.split("->");
			if (stardartRight.containsKey(list[0])) {// �Ѿ�������ߵ�ʽ��
				// System.out.println("�Ѿ����ظ����ˣ���Ҫ��ӣ�"+list[1]);
				stardartRight.get(list[0]).add(list[1]);
			} else {// ��û�д���
				ArrayList<String> a = new ArrayList<String>();
				a.add(list[1]);
				stardartRight.put(list[0], a);
				// a.clear();
			}
		}

		// TODO ת���ɱ�׼�ĺϲ��﷨
		for (String m : stardartRight.keySet()) {
			String grammerLeft = m;
			ArrayList<String> grammerRight = stardartRight.get(m);
			String[] rightList = (String[]) grammerRight.toArray(new String[grammerRight.size()]);
			// System.out.println(m+" "+Arrays.toString(rightList));//�������
			stardardConbineGrammer.put(grammerLeft, rightList);
		}
		for (String k : stardardConbineGrammer.keySet()) {
			System.out.println("����first���ϣ�"+k+" "+Arrays.toString(stardardConbineGrammer.get(k)));
			findEveryFirst(k, stardardConbineGrammer.get(k));// ��ÿһ�����ս�����ò���first�ĺ���
		}
	}

	static Set<String> findEveryFirst(String curNode, String[] rightNodes) {
		String nextNode = "";
		if (firstList.containsKey(curNode))
			return firstList.get(curNode);
		Set<String> st = new TreeSet<String>();
		for (int i = 0; i < rightNodes.length; ++i) {//�м��� aB|a, 2��
			System.out.println("��ǰi:"+rightNodes[i]);
			for (int j = 0; j < rightNodes[i].length(); ++j) {//
				String[] ifSprcial = countSpecialToken(rightNodes[i]);
				if(ifSprcial[0].equals("")) {//û���������
					nextNode = "" + rightNodes[i].charAt(j);
				}else {
					nextNode = ifSprcial[0];
				}
				System.out.println("nextNode"+j+" "+nextNode);
				
				if (!stardardConbineGrammer.containsKey(nextNode)) {// �ս��
					st.add(nextNode);//ֱ�Ӽӽ�ȥ
					break;
				} else {// ���ս��
					/*if (j + 1 < rightNodes[i].length() && rightNodes[i].charAt(j + 1) == '\'') {
						nextNode += rightNodes[i].charAt(j + 1);
						++j;
					}*/
					if ((!curNode.equals(nextNode)) ) {//S->BA ��B��first����
						Set<String> tmpSt = findEveryFirst(nextNode, stardardConbineGrammer.get(nextNode));//����
						st.addAll(tmpSt);
						if (!tmpSt.contains("$"))
							break;
					}else {//E->E+F;
						break;
					}
				}
			}
		}
		firstList.put(curNode, st);
		return st;
	}

	// ��first����
	static String getLRFirst(String s,String lastToken) {
		/**
		 * ���lastToken������˾Ͳ�Ҫ��
		 * @return : |a|b
		 */
		
		String firstString = "";
		//System.out.println("����"+s+"��first����");
		if(firstList.get(s) != null) {
			for (String ss : firstList.get(s)) {
				if (!(ss.equals("��")) &&  (!lastToken.contains(String.valueOf(ss)))) {// ��������
					// System.out.println("jin "+ss);
					firstString = firstString + "|" + ss;
				}
			}
		}
		// System.out.println("�ң�"+firstString);
		return firstString;
	}

	static String[][] reateLRAnalyTable() {
		// TODO ��goto��action��������
		System.out.println("����DFA״̬��");
		int count = 0;
		for (String s : actionToken) {
			System.out.println("action���"+s+" "+count);
			token2Number.put(s, count);
			count++;
		}
		
		/*
		for (String[] s : gotoList) {//������û��ӵ�
			//System.out.println(s[0] + " " + s[1] + " " + s[2]);
			if(!token2Number.keySet().contains(s[1]) && !(gotoToken.contains(s[1])) && !(startToken.equals(s[1]))) {//��û�����ģ���ӽ�ȥ
				token2Number.put(s[1], count);
				count++;
			}
		}
		*/
		if(!token2Number.keySet().contains("$")) {
			System.out.println("$���"+"$"+" "+count);
			token2Number.put("$", count);
			count++;
		}
		for (String s : gotoToken) {
			System.out.println("goto���"+s+" "+count);
			token2Number.put(s, count);
			count++;
		}
		
		
		for (String s : token2Number.keySet()) {
			System.out.println(s + " " + token2Number.get(s));
		}

		// ����String[state����][action+goto�ĸ���]����:
		String[][] analyList = new String[projectSet.size()][count];
		for (int i = 0; i < projectSet.size(); i++) {
			for (int j = 0; j < token2Number.size(); j++) {
				analyList[i][j] = "";
			}
		}
		for (int i = 0; i < projectSet.size(); i++) {// ��ÿһ�п�ʼ���
			// ��Ԫ����{"��״̬ ��ǰ׺ ��ת״̬"}
			for (String s[] : gotoList) {// ������ת�б�
				if (s[0].equals(String.valueOf(i))) {// �Ǹ�����״̬
					if (actionToken.contains(s[1])) {// �Ƕ�������Ҫ�ж��ǹ�Լ�����ƽ�
						// ��ȫ��������
						analyList[i][token2Number.get(s[1])] = "S" + s[2];
					} else {// ��goto����ֻ��Ҫ������
						//System.out.println("error:"+s[2]);
						//System.out.println("index:"+s[1]);
						analyList[i][token2Number.get(s[1])] = s[2];
					}
				}
			}
		}

		System.out.println("-----------��Լ�ܽ�---------��" + projectSet.size());
		for (int k = 0; k < projectSet.size(); k++) {
			System.out.println("............" + k);
			for (String[] s : projectSet.get(k)) {
				if (grammerListLast.contains(s[0])) {// �����ս�״̬
					if(!(s[0].split("->")[0].equals(startToken))) {//�����ս�״̬
						System.out.println(k + "�����ս�״̬:" + s[0]+","+s[1]);
						String[] rToken = s[1].split("\\|");
						System.out.println("�ֽ�Ч����"+Arrays.toString(rToken));
						for (String token : rToken) {
							System.out.println(k+" "+token+" "+token2Number.get(token)+" ��Լʽ�ӣ�"+ grammerListLast.indexOf(s[0]));
							analyList[k][token2Number.get(token)] = "R" + grammerListLast.indexOf(s[0]);
						}
					}else {//�����ս����ʽ��
						analyList[k][token2Number.get("$")] = "acc";
					}

				}
			}
		}
		return analyList;
	}
	
	static void judge(ArrayList<String> wordList){
		ArrayList<String> errorTokenLess = new ArrayList<>();//ȱ�ٵķ���
		stateStack.push(new String("0"));//״̬ջ�Ŀ�ʼ��0
		tokenStack.push(new String("$"));
		String action = "";//����
		String token = "";//��ǰ�ַ�
		String currentState = "";//��ǰ״̬
		String currentToken = "";//��ǰ����ջջ��״̬
		String stackPeek = "";
		int count=0;
		stackPeek = stateStack.peek();
		int hang = Integer.valueOf(stackPeek);//������
		token = wordList.get(count);
		int lie = token2Number.get(token);//������
		int iter = 1;
		System.out.printf("%-9s","");
		System.out.printf("%-65s","״̬ջ");
		System.out.printf("%-60s","����ջ");
		System.out.printf("%-10s","��ǰ����");
		System.out.printf("\n");
		boolean ifSuccess = false;
		boolean firstError = false;
		while(!action.equals("acc")) {
		
			System.out.printf("%-5s",iter);
			System.out.printf("%-40s",stateStack);
			System.out.printf("%-40s",tokenStack);
			System.out.printf("%-10s",token);
			System.out.printf("\n");
			
			action = analyList[hang][lie];
			//System.out.println("��ǰ����"+action);
			
			if(action.equals("acc")) {
				 ifSuccess = true;
				break;
			}
			if(action.contains("S")) {//S1
				//ѹ��״̬վ�ͷ���
				//System.out.println("����");
				tokenStack.push(new String(token));
				currentState = action.replace("S","");
				stateStack.push(new String(currentState));
				if(count<wordList.size()) {
					count++;
				}
			}else if(action.contains("R")) {//R1
				//System.out.println("��Լ");
				String index = action.replace("R","");
				String rString = grammerList.get(Integer.valueOf(index));//���ĸ�ʽ������Լ
				//TODO !!!!!�𰸣�Ҫ��Լ��ʽ��
				System.out.println("��Լ"+rString);
				String right = rString.split("->")[1];//int +BT
				String left = rString.split("->")[0];
				
				//TODO ���ұߵĶ����Ž�һ��list
				ArrayList<String> rWord = new ArrayList<>();
				int specialIndex = 0;
				String specilaRight =right;
				
				while(specialIndex<right.length()) {
					//System.out.println("�ܳ��ȣ�"+right.length()+" �ֳ��ȣ�"+specialIndex);
					String ifSepecial [] = countSpecialToken(specilaRight);
					if(!ifSepecial[0].equals("")) {//�ǹؼ���
						rWord.add(ifSepecial[0]);
						specilaRight  = ifSepecial[1];//ʣ�µĴ�
						//System.out.println("�ҵ��Ĵ�"+ifSepecial[0]+" ʣ�µĴ���"+specilaRight);
						specialIndex = specialIndex+ifSepecial[0].length();
					}else {
						rWord.add(String.valueOf(right.charAt(specialIndex)));
						specilaRight = specilaRight.substring(1);
						specialIndex++;
					}
				}
				for(String s : rWord) {
					//System.out.print("��Լ����"+s+" ");
				}
				//System.out.print("\n");
				//�ѷ���ջ�����ͬ�ĵ���ȥ
				for(int y =rWord.size()-1;y>=0;y-- ) {
					if(tokenStack.peek().equals(rWord.get(y))) {//����ջ����Լ�ұ�
						tokenStack.pop();
						stateStack.pop();
					}
				}
				//System.out.println("��Լ�������״̬��"+stateStack+" "+tokenStack);
				//������ϣ����ұ߷Ž���
				tokenStack.push(new String(left));
				//System.out.println("��Լ��������״̬��"+stateStack+" "+tokenStack);
				currentState = stateStack.peek();
				currentToken = tokenStack.peek();
				//System.out.println("��ǰ״̬��"+currentState+"��ǰ����ջ�ַ���"+currentToken);
				hang = Integer.valueOf(currentState);
				lie = Integer.valueOf(token2Number.get(currentToken));
				action = analyList[hang][lie];
				stateStack.push(new String(action));
				//System.out.println("��Լ��Ϻ��״̬��"+stateStack+" "+tokenStack);
			}else {
				//TODO ���������ַ�
				String ingfo = "["+count+"]"+"[�����ַ�"+token+"]";
				//System.out.println(ingfo);
				errorInfoListMore.add(ingfo);
				if(firstError==false){
					//TODO Ҳ���ȱ��ʲô
					errorTokenLess = judgeErrorLess(hang);
					String[] array = (String[])errorTokenLess.toArray(new String[errorTokenLess.size()]);
					String lessInfo = "["+count+"]"+"[ȱ���ַ�"+Arrays.toString(array)+"]";
					errorInfoListLess.add(lessInfo);
					//System.out.println("��С��"+errorTokenLess.size());
				}
				count++;//ֱ���Թ�����ַ�
				firstError=true;
				if(count>=wordList.size()) {
					break;//���ڵ�ǰ���ַ����Ķ�
				}
			}
			token = wordList.get(count);
			//System.out.println("��ǰ�ַ���"+token);
			stackPeek = stateStack.peek();
			hang = Integer.valueOf(stackPeek);
			//System.out.println("��һ�����ţ�"+token);
			lie = token2Number.get(token);
			iter++;
		}
		//�ж��Ƿ�ɹ�
		System.out.println("----------------��Ϣ��ʾ----------");
		if( ifSuccess == true) {//��λ����ȷ��
			System.out.println("�ɹ�");
			for(String s :errorInfoListMore) {
				System.out.println(s);
			}
		}else {
			System.out.println("ʧ��");
			for(String s :errorInfoListLess) {
				System.out.println(s);
			}
		}
	}
	
	static ArrayList<String> judgeErrorLess(int hang) {
		//System.out.println("�кţ�"+hang);
		ArrayList<String> errorTokenLess = new ArrayList<>();//ȱ�ٵķ���
		for(int i = 0;i<analyList[hang].length;i++) {
			if((analyList[hang][i]!="")) {
				//�ҳ������Ӧ���������
				System.out.println(analyList[hang][i]);
				for(String s : token2Number.keySet()) {
					if((token2Number.get(s)==i)&&(actionToken.contains(s))) {//��������ַ�
						//System.out.println("�ַ���"+s);
						errorTokenLess.add(s);
					}
				}
			}
		}
		return errorTokenLess;
	}
	
}
