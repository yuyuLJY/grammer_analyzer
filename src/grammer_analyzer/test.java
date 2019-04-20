package grammer_analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;

public class test {

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
		String s = "$|a|b";
		String[] a = s.split("\\|");
		System.out.println(Arrays.toString(a));
		
		
		
		
		
		
		
	}

}
