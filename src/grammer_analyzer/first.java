package grammer_analyzer;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class first {
    private Map<String, Set<Character>> first = new TreeMap<String, Set<Character>>();
    private Map<String, String[]> mp = null;
    public first(Map<String, String[]> mp) {
        super();
        this.mp = mp;
    }
    public Map<String, Set<Character>> getFirstSet(){
        return first;
    }
    private Set<Character> findFirst(String curNode, String[] rightNodes){
         if(first.containsKey(curNode)) return first.get(curNode); 
         Set<Character> st = new TreeSet<Character>();
         for(int i=0; i<rightNodes.length; ++i){
                for(int j=0; j<rightNodes[i].length(); ++j){
                    String nextNode = ""+rightNodes[i].charAt(j);
                    if(!mp.containsKey(nextNode)){//�ս��
                        st.add(nextNode.charAt(0));
                        break;
                    }
                    else{//���ս��
                        if(j+1<rightNodes[i].length() && rightNodes[i].charAt(j+1)=='\''){
                            nextNode += rightNodes[i].charAt(j+1);
                            ++j;
                        }
                        if(mp.containsKey(nextNode)){
                            Set<Character> tmpSt = findFirst(nextNode, mp.get(nextNode));
                            st.addAll(tmpSt);
                            if(!tmpSt.contains('$'))
                                break;
                        }
                    }
                }
         }
         first.put(curNode, st);
         return st;
    }
    
    public String firstKernealCode(){
         String content = "";
         for(String leftNode : mp.keySet()){
             String[] rightNodes = mp.get(leftNode);
             findFirst(leftNode, rightNodes);
         }
         //��ӡfirst����
         System.out.println("First��������:");
         for(Map.Entry<String, Set<Character>> entry : first.entrySet()){
             content += entry.getKey() + "  :  " + entry.getValue() + "\n";
             System.out.println(entry.getKey() + "  :  " + entry.getValue());
         }
         return content;
    }
    
    public static void main(String[] args){
       /* String[] rightLinearGrammar = {
                "S->ABc",
                "A->a|��",
                "B->b"
        };*/
    	//"S'->S","S->A","A->BA","A->��","B->aB","B->b"
    	String[] rightLinearGrammar = {
    	"S'->S","S->A","A->BA|��","B->aB|b"	
    	};
        Map<String, String[]> mp = new LinkedHashMap<String, String[]>();
        try{
            for(int i=0; i<rightLinearGrammar.length; ++i){
                String split1[] = rightLinearGrammar[i].split("->");
                String split2[] = split1[1].split("\\|");
                mp.put(split1[0], split2);//["A","{BA,��}"]
            }
            
        } catch(Exception e){
            e.printStackTrace();
            System.out.println("�������ķ�����!");
        }
        new first(mp).firstKernealCode();
    }
    
}
