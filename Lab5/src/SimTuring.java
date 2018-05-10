import java.util.*;
import java.io.*;

public class SimTuring {
	
	private static Character praznaCelija;
	private static String stanje;
	private static int polozajGlave;
	private static Character[] traka;
	
	private static Map<String,String> prijelazi = new HashMap<String,String>();
	private static List<String> Q = new ArrayList<String>();
	private static List<String> E = new ArrayList<String>();
	private static List<String> F = new ArrayList<String>();
	private static List<String> znakoviTrake = new ArrayList<String>();
	
	
	public static void main(String[] args) throws IOException{
		BufferedReader unos = new BufferedReader(new InputStreamReader(System.in));
		
		String entry = unos.readLine();
		odvajanjeStanja(entry);
		
		entry = unos.readLine();
		odvajanjeAbeceda(entry);
		
		entry = unos.readLine();
		odvajanjeZnakovaTrake(entry);
		
		praznaCelija=unos.readLine().charAt(0);
		
		entry = unos.readLine();
		traka=new Character[entry.length()];
		zapisTrake(entry);
		
		entry = unos.readLine();
		prihvatljivaStanja(entry);
		
		stanje=unos.readLine();
		
		polozajGlave=Integer.parseInt(unos.readLine());
		
		while((entry = unos.readLine() ) != null && entry.equals("")== false){
			String[] podjela = entry.split("->");
			prijelazi.put(podjela[0],podjela[1]);
		}
		
		Turing();
		
		return;
	}
	
	public static void odvajanjeStanja(String entry){
		String[] parts = entry.split(",");
		for(String x : parts)
			Q.add(x);
		
	}
	
	public static void odvajanjeAbeceda(String entry){
		String[] parts = entry.split(",");
		for(String x : parts)
			E.add(x);
		
	}
	
	public static void odvajanjeZnakovaTrake(String entry){
		String[] parts = entry.split(",");
		for(String x : parts)
			znakoviTrake.add(x);
		
	}
	
	public static void zapisTrake(String entry){
		int i;
		for(i=0;i<entry.length();i++)
			traka[i]=entry.charAt(i);
		
	}
	
	public static void prihvatljivaStanja(String entry){
		String[] parts = entry.split(",");
		for(String x : parts)
			F.add(x);
		
	}
	
	public static void Turing(){
		int i=polozajGlave;
		int prihvatljivo=0;
		String prijelaz;
		String[] next;
		
		if(traka[polozajGlave].equals(praznaCelija)){
			System.out.println(stanje+"|"+polozajGlave+"|"+traka+"|"+prihvatljivo);
			return;
		}
		try{
			while(true){
				prijelaz=prijelazi.get(stanje+","+traka[i]);
				
				next=prijelaz.split(",");
				if(i==0 && next[2]=="L" || i==traka.length && next[2]=="R")
					break;
				stanje=next[0];
				traka[i]=next[1].charAt(0);
				if(next[2].equals("R"))i++;
				else i--;
			}
		}catch(IndexOutOfBoundsException|NullPointerException s){
			if(i>=70) i--;
			else if(i<=0) i++;
			polozajGlave=i;
			if(F.contains(stanje)) prihvatljivo = 1;
			System.out.print(stanje+"|"+polozajGlave+"|");
			for(i=0;i<traka.length;i++)
				System.out.print(traka[i]);
			System.out.print("|"+prihvatljivo);
			return;
		}
		
	}

}
