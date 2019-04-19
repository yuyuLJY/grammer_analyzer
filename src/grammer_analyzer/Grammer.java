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
	static ArrayList<String> gotoToken = new ArrayList<>();//goto��ķ���
	static ArrayList<String> actionToken = new ArrayList<>();//action��ķ���
	static ArrayList<ArrayList<String[]>> projectSet = new ArrayList<>();//�������state
	static Map<String, String[]> stardardConbineGrammer = new HashMap<>();
	static Map<String, Set<Character>> firstList = new TreeMap<String, Set<Character>>();//first����
	public static void main(String[] args) {
		//TODO �ҳ��ս������
		Grammer tt = new Grammer ();
		Map<String,Integer> actionNumber = new HashMap<>();
		actionNumber.put("a", 0);
		
		Map<String,Integer> gotoNumber = new HashMap<>();
		
		//TODO ��ȡ�ķ�
		tt.readGrammer("src/G.txt");//����ı����������ķ�
		//TODO �����goto/action�ķ�������Щ
		gotoToken.add("S");gotoToken.add("A");gotoToken.add("B");
		actionToken.add("a");actionToken.add("b");actionToken.add("$");
		/*
		//��֤�Ƿ��ȡ���ķ�,ok
		for(String s:grammerList) {
			System.out.println(s);
		}
		*/
		
		//TODO ���ķ��ı��ʽ���ϵ�.
		for(String s:grammerList){
			String []list = s.split("->");
			String newStartGrammer = list[0]+"->"+"."+list[1];
			grammerListFirst.add(newStartGrammer);
		}
		
		//TODO ����first����
		countFirst();
		//countActionGotoToken();//�ѱ��к�
		/*
		String content = "";
        for(Map.Entry<String, Set<Character>> entry : firstList.entrySet()){
            content += entry.getKey() + "  :  " + entry.getValue() + "\n";
            System.out.println(entry.getKey() + "  :  " + entry.getValue());
        }*/
		/*
		//��֤�Ƿ��ڱ��ʽǰ�ӵ�ɹ�,ok
		for(String s:grammerListFirst) {
			System.out.println(s);
		}
		*/
		
		
		ArrayList<String[]>  startSet = new ArrayList<>();//״̬1
		ArrayList<String[]>  oneOfSet = new ArrayList<>();//״̬1
		//String a[] = {grammerListFirst.get(0),"$"};
		String a[] = {"B->a.B","a|b|$"};
		startSet.add(a);
		oneOfSet.addAll(closure(startSet));//ȡ����һ��
		projectSet.add(oneOfSet);//��״̬����״̬�󼯺�
		//����հ�����Ƿ���ȷ
		for(String[] s:oneOfSet) {
			System.out.println(s[0]+","+s[1]);
		}
		
	}
	
	//�����ķ�
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
		
	//����հ�
	//���룺����I
	//��� ���ϵıհ�
	static ArrayList<String[]> closure(ArrayList<String[]> set) {
		int length = set.size();
		ArrayList<String[]> newElement = new ArrayList<>();
		for(int i = 0;i<length;i++) {
			String grammer = set.get(i)[0];
			String lastToken = set.get(i)[1];//"$"
			//TODO �鿴.��ߵ���һ��������˭
			String[] grammerList = grammer.split("->");
			int index = grammerList[1].indexOf(".");//�ҵ�.��λ��
			//s->.a  s->A.B  s->AB. s->a.  a->.��
			//(index!=grammerList[1].length()-1)  //.�����ַ���ĩβ
			if((index!=grammerList[1].length()-1) ) {//�������֣�S->A.
				//.��߲��ǿմ�
				String docFollow = String.valueOf(grammerList[1].charAt(index+1));//.��ߵ��ַ�
				if(!docFollow.equals("��") && !actionToken.contains(docFollow)) {//��߲����ս��
					//System.out.println(docFollow);
					//TODO ����lastToken
					if(index+2<=grammerList[1].length()-1) {//���S��߻����ַ���e.g.:s->A.BC
						//System.out.println("��Ҫ������̵ģ�"+grammerList[1]);
						String docFollowFollow =  String.valueOf(grammerList[1].charAt(index+2));
						lastToken = lastToken +getLRFirst(docFollowFollow);
					}
					//�ҳ����еĺ�̼ӽ�another�б�
					String str = docFollow+"->";//S->
					for(String s :grammerListFirst) {//�����ķ��а���S��ͷ��ʽ��
						if(s.indexOf(str)!=-1) {
							String a[] = {s,lastToken};
							newElement.add(a);
						}
					}
				}
			}
			if(i==length-1) {//�����������,�鿴�Ƿ��ٱ���
				set.addAll(newElement);
				length = length+newElement.size();
				newElement.clear();//����´�ʹ��
			}
		}
		return set;
	}
	
	//�����ķ���first����
	static void  countFirst(){
		//TODO ������Ŀ������룺S->a|aB
		Map<String, ArrayList<String>> stardartRight = new TreeMap<String, ArrayList<String>>();
		for (String s :grammerList) {
			//System.out.println(s);
			String list[] = s.split("->");
			if(stardartRight.containsKey(list[0])) {//�Ѿ�������ߵ�ʽ��
				//System.out.println("�Ѿ����ظ����ˣ���Ҫ��ӣ�"+list[1]);
				stardartRight.get(list[0]).add(list[1]);
			}else {//��û�д���
				ArrayList<String> a = new ArrayList<String>();
				a.add(list[1]);
				stardartRight.put(list[0], a);
				//a.clear();
			}
		}
		
		//TODO ת���ɱ�׼�ĺϲ��﷨
		for(String m:stardartRight.keySet()) {
			String grammerLeft = m;
			ArrayList<String> grammerRight = stardartRight.get(m);
			String[] rightList = (String[])grammerRight.toArray(new String[grammerRight.size()]); 
			//System.out.println(m+" "+Arrays.toString(rightList));//�������
			stardardConbineGrammer.put(grammerLeft, rightList);
		}
		for(String k :stardardConbineGrammer.keySet()) {
			findEveryFirst(k, stardardConbineGrammer.get(k));//��ÿһ�����ս�����ò���first�ĺ���
		}
	}
	
	static Set<Character> findEveryFirst(String curNode, String[] rightNodes){
        if(firstList.containsKey(curNode)) return firstList.get(curNode); 
        Set<Character> st = new TreeSet<Character>();
        for(int i=0; i<rightNodes.length; ++i){
               for(int j=0; j<rightNodes[i].length(); ++j){
                   String nextNode = ""+rightNodes[i].charAt(j);
                   if(!stardardConbineGrammer.containsKey(nextNode)){//�ս��
                       st.add(nextNode.charAt(0));
                       break;
                   }
                   else{//���ս��
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
	
	//��first����
	static String getLRFirst(String s) {
		/**
		 * @return : |a|b
		 */
		String firstString = "";
		for(Character ss : firstList.get(s)){
			if(ss!='��') {//��������
				//System.out.println("jin "+ss);
				firstString = firstString+"|"+ss;
			}
		}
		//System.out.println("�ң�"+firstString);
		return firstString;
	}
	
	//������ĿI����һ������X�ıհ�
	static void Goto() {
		
	}
}
