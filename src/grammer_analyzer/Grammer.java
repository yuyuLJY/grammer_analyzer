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
	static ArrayList<String> gotoToken = new ArrayList<>();//goto��ķ���
	static ArrayList<String> actionToken = new ArrayList<>();//action��ķ���
	static ArrayList<ArrayList<String[]>> projectSet = new ArrayList<>();//�������state
	static ArrayList<ArrayList<String[]>> firstList = new ArrayList<>();//first����
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
		/*
		//��֤�Ƿ��ڱ��ʽǰ�ӵ�ɹ�,ok
		for(String s:grammerListFirst) {
			System.out.println(s);
		}
		*/
		
		/*
		ArrayList<String[]>  startSet = new ArrayList<>();//״̬1
		ArrayList<String[]>  oneOfSet = new ArrayList<>();//״̬1
		String a[] = {grammerListFirst.get(0),"$"};
		startSet.add(a);
		oneOfSet.addAll(closure(startSet));//ȡ����һ��
		projectSet.add(oneOfSet);//��״̬����״̬�󼯺�
		//����հ�����Ƿ���ȷ
		for(String[] s:oneOfSet) {
			System.out.println(s[0]+","+s[1]);
		}*/
		
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
	
	//������Ŀ�ļ���
	public ArrayList<String[]> createSet(int count) {	
		ArrayList<String[]>  oneOfSet = new ArrayList<>();
		return oneOfSet;
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
					System.out.println(docFollow);
					//TODO ����lastToken
					if(index+2<=grammerList[1].length()-1) {//���S��߻����ַ���e.g.:s->A.BC
						//System.out.println("��Ҫ������̵ģ�"+grammerList[1]);
						String docFollowFollow =  String.valueOf(grammerList[1].charAt(index+2));
						lastToken = lastToken +getFirst(docFollowFollow);
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
	static void  countFirst() {
		
	}
	//��first����
	static String getFirst(String s) {
		/**
		 * @return : |a|b
		 */
		String firstString = "|";
		return firstString;
	}
	
	//������ĿI����һ������X�ıհ�
	static void Goto() {
		
	}
}
