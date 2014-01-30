package TorrentSeeker.Tables;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import TorrentSeeker.Exceptions.VoidListException;
import TorrentSeeker.Exceptions.NotFoundException;
import TorrentSeeker.Parser.Parser;

public class VisitTable {
	
	private Hashtable<String, Integer> intTabla;
	
	public VisitTable(){
		this.intTabla=new Hashtable<String, Integer>();
	}
	
	public void addOne(String s){
		if(!this.intTabla.containsKey(s) && Parser.checkDomainURL(s)) 
			this.intTabla.put(s, 0);
	}
	
	public void delete(String s){
		this.intTabla.remove(s);
	}
	
	public Hashtable<String, Integer> getTabla(){
		return this.intTabla;
	}
	
	private boolean evaluaCond(String vigente,String candidato){
		if (vigente.length()<candidato.length())return true;
		else return true;
	}
	
	public void visit(String s) throws NotFoundException{
		if(this.intTabla.contains(s))this.intTabla.put(s, 1);
		else throw new NotFoundException(s, "tabla a visitar");
	}
	public int tamaño(){
		Vector<String> lista = new Vector<String>(this.intTabla.keySet());
	    return lista.size();
		
	}
	public String visitNext() throws VoidListException{
		Vector<String> lista = new Vector<String>(this.intTabla.keySet());
	    Iterator<String> it = lista.iterator();
		if (lista.isEmpty())throw new VoidListException("visitarSiguiente");
		String next=lista.get(0);
		while(it.hasNext()){
			String iterado=it.next();
			if(this.intTabla.get(iterado)==0)
				if(evaluaCond(next, iterado))
					next=iterado;
		}
		if(this.intTabla.get(next)==0){
			this.intTabla.put(next, 1);
			return next;
		}
		else
			return "";
	}
		
}
