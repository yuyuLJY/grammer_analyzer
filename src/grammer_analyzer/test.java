package grammer_analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*
		//�����ArrayList��߷�����
		ArrayList<String[]>  setName = new ArrayList<>();
		String a[] = {"1","2"};
		setName.add(a);
		System.out.println(setName.get(0)[0]);
		*/
		/*
		//����ڱ���ArrayList�м����Ԫ�أ�������java.util.ConcurrentModificationException
		ArrayList<String>  a = new ArrayList<>();
		a.add("1");
		a.add("2");
		for(String s : a) {
			System.out.println(s);
			if(s.equals("1")) {
				a.add("�����");
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
				iterator.add("�ҷ���1��!!!");
			}
		}
		System.out.println("----------------");
		for(int i = 0;i<a.size(); i++) {
			System.out.println(a.get(i));
		}
		//��� 1 �ҷ���1��!!! 2  //����˳�򲻺�
		 */
		
		/*
		ArrayList<String>  a = new ArrayList<>();
		ArrayList<String>  newElement = new ArrayList<>();//�����ӵ�Ԫ��
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
		String s = "$|a|b";
		String[] a = s.split("\\|");
		System.out.println(Arrays.toString(a));
		
		
		
		
		
		
		
	}

}
