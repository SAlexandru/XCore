package xcorexview.utils;

public class Pair <T, K> {
	public T first;
	public K second;
	
	public Pair() {
		
	}
	
	public Pair(T first, K second) {
		this.first = first;
		this.second = second;
	}
	
	public T getFirst()  {return first;}
	public K getSecond() {return second;}
	
	public void setFirst(T first)   {this.first = first;}
	public void setSecond(K second) {this.second = second;}
	
	@Override
	public boolean equals(Object obj) {
		if (null == obj || !(obj instanceof Pair)) {
			return false;
		}
		Pair<?, ?> pObj = (Pair<?, ?>)obj;
		
		boolean okFirst = null == this.first ? null == pObj.getFirst() : this.first.equals(pObj.getFirst());
		boolean okSecond = null == this.second ? null == pObj.getSecond() : this.second.equals(pObj.getSecond());
		
		return  okFirst && okSecond;
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		
		s.append("Pair<T, K>(");
		
		if (null == first) s.append("null,");
		else  s.append(first);
		
		if (null == second) s.append("null");
		else s.append(second);
		
		s.append(")");
		return s.toString();
	}
	
	@Override
	public int hashCode() {
		if (null == this.first && null == this.second) return 0;
		else if (null == this.first) return this.second.hashCode();
		else if (null == this.second) return this.first.hashCode();
		else return this.first.hashCode() ^ this.second.hashCode();
	}
}
